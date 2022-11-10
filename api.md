<div align="center">

![Logo](https://i.imgur.com/nTk9ZGd.png)
## RealScoreboard - API Usage
<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/built-with/maven_46h.png" height="35"></a>

</div>

----

## Adding repository and dependency

* Maven
```xml
<repository>
  <id>neziw-repo</id>
  <url>https://repo.neziw.xyz/releases</url>
</repository>
```
```xml
<dependency>
  <groupId>josegamerpt.realscoreboard</groupId>
  <artifactId>RealScoreboard-API</artifactId>
  <version>10-11-2022 Build 5</version>
</dependency>
```
* Gradle (Groovy)
```groovy
maven {
    url "https://repo.neziw.xyz/releases"
}
```
```groovy
implementation "josegamerpt.realscoreboard:RealScoreboard-API:10-11-2022 Build 5"
```

----

# API Usage

## Getting the API instance

You can access API instance by using following method:
```java
RealScoreboardAPI.getInstance();
```

Example Usage:
![Example](https://i.imgur.com/xoYXbFx.png)

## Accessing static configuration

Configuration file can be accessed via `Config` class:
```java
boolean isDebugEnabled = Config.file().getBoolean("Debug");
if (isDebugEnabled) {
  isDebugEnabled = false;
  Config.file().set("Debug", false);
}
```

## Accessing managers and other classes

API Classes like managers are abstract and plugin extends them directly itself.
You can't get direct access via API to managers, but you can use getters from API instance.

Example Usage:
```java
AbstractDatabaseManager databaseManager = this.scoreboardAPI.getDatabaseManager(); // Getting DatabaseManager
PlayerData playerData = databaseManager.getPlayerData(player.getUniqueId()); // Getting player data from DatabaseManager
playerData.setScoreboardON(false); // Disabling scoreboard for player
databaseManager.savePlayerData(playerData, true); // Saving changes (asynchronously) to database
```

----

## Links
* [SpigotMC](https://www.spigotmc.org/resources/realscoreboard-1-13-to-1-19-2.22928/)
* [Discord Server](https://discord.gg/t7gfnYZKy8)
* [bStats](https://bstats.org/plugin/bukkit/RealScoreboard/10080)
