# BugReportPlugin

Velocity plugin that lets players send bug reports to Discord via a webhook and review open reports in-game.

## Features
- Configurable language (`de` or `en`), chat prefix and Discord webhook URL via `config.conf`.
- `/bugreport <reason>` command for players (`net.devvoxel.bugreport.use`).
- `/bugreports` command to list pending reports (`net.devvoxel.bugreport.*`).
- Discord embed containing the reporting player, server, coordinates and the submitted reason.

## Building
Use Maven 3.9+ with JDK 17:

```
mvn package
```

The shaded plugin JAR will be available at `target/bugreportplugin-1.0.0-shaded.jar`.
