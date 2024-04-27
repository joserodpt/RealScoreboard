package joserodpt.realscoreboard.api.scoreboard;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import joserodpt.realscoreboard.api.config.RSBScoreboards;
import org.bukkit.World;

import java.util.Collections;
import java.util.List;

public abstract class RScoreboard {

    protected final String name;
    protected final String displayName;
    protected String permission;
    protected final String defaultWorld;
    protected final List<String> otherWorlds;
    protected final int titleRefresh, titleLoopDelay, globalScoreboardRefresh;
    protected boolean defaultScoreboard;

    public RScoreboard(final String name, final String displayName, final String permission, final String defaultWorld, final List<String> otherWorlds,
                       final int titleRefresh, final int titleLoopDelay, final int globalScoreboardRefresh, final boolean defaultScoreboard) {
        this.name = name;
        this.defaultScoreboard = defaultScoreboard;
        this.displayName = displayName;
        this.permission = permission;
        this.defaultWorld = defaultWorld;
        this.otherWorlds = otherWorlds;
        this.titleRefresh = titleRefresh;
        this.titleLoopDelay = titleLoopDelay;
        this.globalScoreboardRefresh = globalScoreboardRefresh;
    }

    public abstract void stopTasks();
    public abstract void init();
    public abstract String getTitle();
    public abstract List<String> getLines();
    public abstract void saveScoreboard();

    public String getConfigKey() {
        return "Scoreboards." + this.getName() + ".";
    }

    public void saveCommonData() {
        RSBScoreboards.file().set(getConfigKey() + "Default", this.isDefault());
        RSBScoreboards.file().set(getConfigKey() + "Default-World", this.getDefaultWord());
        RSBScoreboards.file().set(getConfigKey() + "Other-Worlds", Collections.emptyList());
        RSBScoreboards.file().set(getConfigKey() + "Display-Name", this.getDisplayName());
        RSBScoreboards.file().set(getConfigKey() + "Permission", this.getPermission());
        RSBScoreboards.file().set(getConfigKey() + "Refresh.Scoreboard", this.globalScoreboardRefresh);
        RSBScoreboards.file().set(getConfigKey() + "Refresh.Title", this.titleRefresh);
        RSBScoreboards.file().set(getConfigKey() + "Refresh.Title-Loop-Delay", this.titleLoopDelay);
    }

    public boolean isDefault() {
        return this.defaultScoreboard;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setPermission(String none) {
        this.permission = none;
    }

    public String getPermission() {
        return permission;
    }

    public String getDefaultWord() {
        return defaultWorld;
    }

    public void setDefault(boolean b) {
        this.defaultScoreboard = b;
    }

    public boolean isInWorld(World world) {
        if (this.otherWorlds.size() == 1 && this.otherWorlds.get(0).equalsIgnoreCase("*")) return true;

        return this.getDefaultWord().equalsIgnoreCase(world.getName()) || this.otherWorlds.contains(world.getName());
    }

    @Override
    public String toString() {
        return "RScoreboard{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", permission='" + permission + '\'' +
                ", defaultWorld='" + defaultWorld + '\'' +
                ", otherWorlds=" + otherWorlds +
                ", titleRefresh=" + titleRefresh +
                ", titleLoopDelay=" + titleLoopDelay +
                ", globalScoreboardRefresh=" + globalScoreboardRefresh +
                ", defaultScoreboard=" + defaultScoreboard +
                '}';
    }
}
