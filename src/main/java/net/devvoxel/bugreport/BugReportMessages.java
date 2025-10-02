package net.devvoxel.bugreport;

import java.util.Locale;
import java.util.Map;

public final class BugReportMessages {
    private static final Map<String, Map<String, String>> TRANSLATIONS = Map.of(
        "de", Map.ofEntries(
            Map.entry("report-usage", "&cBenutze: /bugreport <grund>"),
            Map.entry("report-too-short", "&cBitte gib einen Grund für den Bugreport an."),
            Map.entry("report-sent", "&aDein Bugreport wurde gesendet."),
            Map.entry("report-failed", "&cBeim Versenden des Bugreports ist ein Fehler aufgetreten. Bitte informiere das Team."),
            Map.entry("report-notify", "&e%player% &7hat einen Bug gemeldet: &f%reason%"),
            Map.entry("reports-header", "&bAktuelle Bugreports:"),
            Map.entry("reports-empty", "&7Es wurden noch keine Bugreports erstellt."),
            Map.entry("reports-entry", "&8- &b%player% &7auf &f%server% &7(&f%location%&7): &f%reason%"),
            Map.entry("embed-username", "Username"),
            Map.entry("embed-server", "Aktueller Server"),
            Map.entry("embed-location", "Position"),
            Map.entry("embed-reason", "Bug"),
            Map.entry("location-unknown", "Unbekannt"),
            Map.entry("command-no-permission", "&cDu hast keine Berechtigung für diesen Befehl."),
            Map.entry("command-only-player", "&cNur Spieler können diesen Befehl verwenden."),
            Map.entry("reports-title", "Bugreports"),
            Map.entry("embed-title", "Neuer Bugreport"),
            Map.entry("reports-open-count", "&7Anzahl offener Reports: &b%count%")
        ),
        "en", Map.ofEntries(
            Map.entry("report-usage", "&cUsage: /bugreport <reason>"),
            Map.entry("report-too-short", "&cPlease provide a reason for your bug report."),
            Map.entry("report-sent", "&aYour bug report has been sent."),
            Map.entry("report-failed", "&cAn error occurred while sending the bug report. Please contact the staff."),
            Map.entry("report-notify", "&e%player% &7reported a bug: &f%reason%"),
            Map.entry("reports-header", "&bCurrent bug reports:"),
            Map.entry("reports-empty", "&7No bug reports have been submitted yet."),
            Map.entry("reports-entry", "&8- &b%player% &7on &f%server% &7(&f%location%&7): &f%reason%"),
            Map.entry("embed-username", "Username"),
            Map.entry("embed-server", "Current Server"),
            Map.entry("embed-location", "Location"),
            Map.entry("embed-reason", "Bug"),
            Map.entry("location-unknown", "Unknown"),
            Map.entry("command-no-permission", "&cYou do not have permission to use this command."),
            Map.entry("command-only-player", "&cOnly players can use this command."),
            Map.entry("reports-title", "Bug Reports"),
            Map.entry("embed-title", "New Bug Report"),
            Map.entry("reports-open-count", "&7Open reports: &b%count%")
        )
    );

    private final Map<String, String> messages;

    public BugReportMessages(String language) {
        this.messages = TRANSLATIONS.getOrDefault(language.toLowerCase(Locale.ROOT), TRANSLATIONS.get("en"));
    }

    public String get(String key) {
        return messages.getOrDefault(key, key);
    }
}
