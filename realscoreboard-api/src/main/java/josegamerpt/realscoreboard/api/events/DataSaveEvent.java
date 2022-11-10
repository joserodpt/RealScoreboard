package josegamerpt.realscoreboard.api.events;

import josegamerpt.realscoreboard.api.config.PlayerData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when playerdata is saving
 */
public class DataSaveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final PlayerData playerData;
    private final boolean async;
    private boolean cancelled;

    public DataSaveEvent(PlayerData playerData, boolean async) {
        this.playerData = playerData;
        this.async = async;
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
     * Gets boolean state of async data save
     *
     * @return true/false if save is asynchronous
     */
    public boolean isAsync() {
        return async;
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
