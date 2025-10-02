package net.devvoxel.bugreport;

import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;

public final class BugReportsCommand implements SimpleCommand {
    private static final String PERMISSION = "net.devvoxel.bugreport.*";

    private final BugReportPlugin plugin;

    public BugReportsCommand(BugReportPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        if (!invocation.source().hasPermission(PERMISSION)) {
            plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("command-no-permission"));
            return;
        }

        List<BugReport> reports = plugin.getReports();
        if (reports.isEmpty()) {
            plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("reports-empty"));
            return;
        }

        plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("reports-header"));
        plugin.sendPrefixedMessage(invocation.source(), plugin.getMessages().get("reports-open-count").replace("%count%", Integer.toString(reports.size())));
        reports.forEach(report -> plugin.sendPrefixedMessage(invocation.source(),
            plugin.getMessages().get("reports-entry")
                .replace("%player%", report.getPlayerName())
                .replace("%server%", report.getServerName())
                .replace("%location%", report.getLocation())
                .replace("%reason%", report.getReason())
        ));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return List.of();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(PERMISSION);
    }
}
