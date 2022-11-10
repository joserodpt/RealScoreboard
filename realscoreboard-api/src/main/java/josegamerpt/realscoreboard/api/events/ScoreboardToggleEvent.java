package josegamerpt.realscoreboard.api.events;

import josegamerpt.realscoreboard.api.config.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when scoreboard status change
 */
public class ScoreboardToggleEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final PlayerData playerData;
    private boolean cancelled;

    public ScoreboardToggleEvent(PlayerData playerData) {
        this.playerData = playerData;
    }

    /**
     * Gets playerdata related to this event
     *
     * @return the playerdata instance
     */
    public PlayerData getPlayerData() {
        return playerData;
    }

    /**
     * Checks if event is cancelled
     *
     * @return true/false if event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancels this event
     *
     * @param cancelled boolean state
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
