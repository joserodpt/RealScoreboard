<div align="center">

![Logo](https://i.imgur.com/nTk9ZGd.png)
## RealScoreboard - Best scoreboard plugin.
</div>

---

## 1. Repository

RealScoreboard API is hosted on jitpack.

* Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

* Gradle (Groovy)
```groovy
maven { url 'https://jitpack.io' }
```

## 2. Dependency

Only API module should be used. Available versions can be found on [Jitpack page](https://jitpack.io/#joserodpt/RealScoreboard).
If you need access to internals you need to add plugin dependency yourself or use reflections.

* Maven
```xml
<dependency>
    <groupId>com.github.joserodpt.RealScoreboard</groupId>
    <artifactId>realscoreboard-api</artifactId>
    <version>VERSION</version>
</dependency>
```

* Gradle (Groovy)
```groovy
implementation 'com.github.joserodpt.RealScoreboard:realscoreboard-api:VERSION'
```

## 3. Usage

You can obtain the API instance like this:
```java
final var api = RealScoreboardAPI.getInstance();
```

From there you can access different managers and classes.
These are visible as interfaces or abstract classes, so you can only use the exposed methods.

Here is example usage:

```java
final var databaseManager = this.scoreboardAPI.getDatabaseManager(); // Getting DatabaseManager
final var playerData = databaseManager.getPlayerData(player.getUniqueId()); // Getting player data from DatabaseManager
playerData.setScoreboardON(false); // Disabling scoreboard for player
databaseManager.savePlayerData(playerData, true); // Saving changes (asynchronously) to database
```

----

## Links
* [SpigotMC](https://www.spigotmc.org/resources/realscoreboard-1-13-to-1-19-2.22928/)
* [Discord Server](https://discord.gg/t7gfnYZKy8)
* [bStats](https://bstats.org/plugin/bukkit/RealScoreboard/10080)