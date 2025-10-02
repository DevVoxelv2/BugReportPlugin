package net.devvoxel.bugreport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.util.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.ConfigurateException;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Plugin(id = "bugreportplugin", name = "BugReportPlugin", version = "1.0.0", authors = {"devvoxel"})
public final class BugReportPlugin {
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDirectory;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final List<BugReport> reports = new CopyOnWriteArrayList<>();

    private BugReportConfig config;
    private BugReportMessages messages;

    @Inject
    public BugReportPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.config = new BugReportConfig(dataDirectory);
        try {
            config.load();
        } catch (ConfigurateException | java.io.IOException e) {
            logger.error("Unable to load bug report configuration", e);
            config.useDefaults();
        }
        this.messages = new BugReportMessages(config.getLanguage());

        CommandManager commandManager = proxyServer.getCommandManager();
        commandManager.register(commandManager.metaBuilder("bugreport").plugin(this).build(), new BugReportCommand(this));
        commandManager.register(commandManager.metaBuilder("bugreports").plugin(this).build(), new BugReportsCommand(this));

        logger.info("BugReportPlugin has been enabled");
    }

    public BugReportMessages getMessages() {
        return messages;
    }

    public List<BugReport> getReports() {
        return reports;
    }

    public void handleBugReport(Player player, String reason) {
        String serverName = player.getCurrentServer()
            .map(connection -> connection.getServerInfo().getName())
            .orElse("Unknown");
        Position position = player.getPosition();
        String location = String.format(Locale.ROOT, "%.2f, %.2f, %.2f", position.getX(), position.getY(), position.getZ());

        BugReport report = new BugReport(player, serverName, location, reason);
        reports.add(report);

        sendPrefixedMessage(player, messages.get("report-sent"));
        notifyStaff(report, player);
        dispatchWebhook(report, player);
    }

    private void notifyStaff(BugReport report, Player sender) {
        String message = messages.get("report-notify")
            .replace("%player%", sender.getUsername())
            .replace("%reason%", report.getReason());
        Component component = LEGACY_SERIALIZER.deserialize(config.getPrefix() + message);
        proxyServer.getAllPlayers().stream()
            .filter(player -> player.hasPermission("net.devvoxel.bugreport.*"))
            .forEach(player -> player.sendMessage(component));
    }

    private void dispatchWebhook(BugReport report, Player sender) {
        String webhook = Objects.requireNonNullElse(config.getWebhook(), "").trim();
        if (webhook.isEmpty() || webhook.equalsIgnoreCase("dein webhook")) {
            logger.warn("Webhook URL is not configured. Skipping Discord notification for bug reports.");
            return;
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("username", "BugReportSystem");
        payload.addProperty("content", "");

        JsonObject embed = new JsonObject();
        embed.addProperty("title", messages.get("embed-title"));
        embed.addProperty("description", messages.get("embed-reason") + ": " + report.getReason());
        embed.addProperty("color", 4592639);
        embed.addProperty("timestamp", ISO_FORMATTER.format(report.getCreatedAt()));

        JsonArray fields = new JsonArray();

        JsonObject usernameField = new JsonObject();
        usernameField.addProperty("name", messages.get("embed-username"));
        usernameField.addProperty("value", report.getPlayerName());
        usernameField.addProperty("inline", false);
        fields.add(usernameField);

        JsonObject serverField = new JsonObject();
        serverField.addProperty("name", messages.get("embed-server"));
        serverField.addProperty("value", report.getServerName());
        serverField.addProperty("inline", false);
        fields.add(serverField);

        JsonObject locationField = new JsonObject();
        locationField.addProperty("name", messages.get("embed-location"));
        locationField.addProperty("value", report.getLocation());
        locationField.addProperty("inline", false);
        fields.add(locationField);

        embed.add("fields", fields);

        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        payload.add("embeds", embeds);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(webhook))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
            .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
            .whenComplete((response, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to deliver bug report to Discord webhook", throwable);
                    notifyDeliveryFailure(sender);
                    return;
                }

                if (response.statusCode() >= 300) {
                    logger.error("Discord webhook responded with status {} when delivering bug report", response.statusCode());
                    notifyDeliveryFailure(sender);
                }
            });
    }

    private void notifyDeliveryFailure(Player sender) {
        proxyServer.getScheduler().buildTask(this, () ->
            proxyServer.getPlayer(sender.getUniqueId())
                .ifPresent(player -> sendPrefixedMessage(player, messages.get("report-failed")))
        ).schedule();
    }

    public void sendPrefixedMessage(com.velocitypowered.api.command.CommandSource target, String message) {
        Component component = LEGACY_SERIALIZER.deserialize(config.getPrefix() + message);
        target.sendMessage(component);
    }
}
