package net.devvoxel.bugreport;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import java.util.List;

public final class BugReportCommand implements SimpleCommand {
    private static final String PERMISSION = "net.devvoxel.bugreport.use";

    private final BugReportPlugin plugin;

    public BugReportCommand(BugReportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("command-only-player"));
            return;
        }

        if (!invocation.source().hasPermission(PERMISSION) && !invocation.source().hasPermission("net.devvoxel.bugreport.*")) {
            plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("command-no-permission"));
            return;
        }

        String[] args = invocation.arguments();
        if (args.length == 0) {
            plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("report-usage"));
            return;
        }

        String reason = String.join(" ", args).trim();
        if (reason.isEmpty()) {
            plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("report-too-short"));
            return;
        }

        plugin.handleBugReport(player, reason);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return List.of();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(PERMISSION) || invocation.source().hasPermission("net.devvoxel.bugreport.*");
    }
}
