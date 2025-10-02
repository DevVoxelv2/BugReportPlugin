package net.devvoxel.bugreport;

import com.velocitypowered.api.proxy.Player;

import java.time.Instant;

public final class BugReport {
    private final String playerName;
    private final String serverName;
    private final String location;
    private final String reason;
    private final Instant createdAt;

    public BugReport(Player player, String serverName, String location, String reason) {
        this.playerName = player.getUsername();
        this.serverName = serverName;
        this.location = location;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
