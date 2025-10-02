package net.devvoxel.bugreport;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BugReportConfig {
    private static final String DEFAULT_LANGUAGE = "de";
    private static final String DEFAULT_PREFIX = "&bBugReportSystem &8» &r";
    private static final String DEFAULT_WEBHOOK = "dein webhook";

    private final Path filePath;
    private final HoconConfigurationLoader loader;

    private CommentedConfigurationNode root;

    public BugReportConfig(Path dataDirectory) {
        this.filePath = dataDirectory.resolve("config.conf");
        this.loader = HoconConfigurationLoader.builder().path(filePath).build();
    }

    public void load() throws IOException {
        Files.createDirectories(filePath.getParent());
        if (Files.notExists(filePath)) {
            root = createDefaultNode();
            saveNode(root);
            return;
        }

        try {
            root = loader.load();
        } catch (Exception e) {
            throw new IOException("Unable to load bug report configuration", e);
        }
        boolean changed = false;
        if (root.node("language").virtual()) {
            root.node("language").set(DEFAULT_LANGUAGE);
            changed = true;
        }
        if (root.node("prefix").virtual()) {
            root.node("prefix").set(DEFAULT_PREFIX);
            changed = true;
        }
        if (root.node("webhook").virtual()) {
            root.node("webhook").set(DEFAULT_WEBHOOK);
            changed = true;
        }
        if (changed) {
            saveNode(root);
        }
    }

    public void useDefaults() {
        try {
            root = createDefaultNode();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create default configuration", e);
        }
    }

    private CommentedConfigurationNode createDefaultNode() throws IOException {
        try {
            CommentedConfigurationNode node = loader.createNode();
            node.node("language").set(DEFAULT_LANGUAGE);
            node.node("prefix").set(DEFAULT_PREFIX);
            node.node("webhook").set(DEFAULT_WEBHOOK);
            return node;
        } catch (Exception e) {
            throw new IOException("Unable to create default configuration", e);
        }
    }

    private void saveNode(CommentedConfigurationNode node) throws IOException {
        try {
            loader.save(node);
        } catch (Exception e) {
            throw new IOException("Unable to save bug report configuration", e);
        }
    }

    public String getLanguage() {
        return root.node("language").getString(DEFAULT_LANGUAGE).trim().toLowerCase();
    }

    public String getPrefix() {
        return root.node("prefix").getString(DEFAULT_PREFIX);
    }

    public String getWebhook() {
        return root.node("webhook").getString(DEFAULT_WEBHOOK);
    }
}
