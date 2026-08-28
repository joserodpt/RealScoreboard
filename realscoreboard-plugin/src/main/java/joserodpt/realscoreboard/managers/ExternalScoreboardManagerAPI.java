package joserodpt.realscoreboard.managers;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2026
 * @link https://github.com/joserodpt/RealScoreboard
 */

import com.google.common.base.Preconditions;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.external.ExternalBoard;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ExternalScoreboardManagerAPI implements joserodpt.realscoreboard.api.managers.ExternalScoreboardManagerAPI {

    /**
     * The board each player has been given, if any. Concurrent because plugins
     * push from the main thread while RSBPlayer's refresh task reads from its
     * own.
     */
    private final Map<UUID, Claim> boards = new ConcurrentHashMap<>();

    /**
     * The plugins that announced themselves, so /rsb hooks can name them even
     * while they have no board on anyone's screen.
     */
    private final Map<String, Plugin> hooks = new ConcurrentHashMap<>();

    private final RealScoreboardAPI rsa;

    public ExternalScoreboardManagerAPI(RealScoreboardAPI rsa) {
        this.rsa = rsa;
    }

    @Override
    public void registerHook(Plugin owner) {
        Preconditions.checkNotNull(owner, "owner");

        this.hooks.put(owner.getName(), owner);
        this.rsa.getLogger().info("Successfully registered hook: " + owner.getName()
                + " [" + owner.getDescription().getVersion() + "]");
    }

    @Override
    public Collection<Plugin> getHooks() {
        this.hooks.values().removeIf(hook -> !hook.isEnabled());
        return new ArrayList<>(this.hooks.values());
    }

    @Override
    public void setBoard(Plugin owner, Player player, String title, List<String> lines, boolean forced) {
        this.setBoard(owner, player, new ExternalBoard(title, lines, forced));
    }

    @Override
    public void setBoard(Plugin owner, Player player, ExternalBoard board) {
        this.checkRegistered(owner);
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(board, "board");

        this.boards.put(player.getUniqueId(), new Claim(owner, board));
    }

    @Override
    public void clearBoard(Plugin owner, Player player) {
        this.checkRegistered(owner);
        Preconditions.checkNotNull(player, "player");

        //only the plugin that set it may take it away
        this.boards.computeIfPresent(player.getUniqueId(),
                (uuid, claim) -> claim.owner.equals(owner) ? null : claim);
    }

    @Override
    public void clearAll(Plugin owner) {
        this.checkRegistered(owner);

        this.boards.values().removeIf(claim -> claim.owner.equals(owner));
    }

    @Override
    public ExternalBoard getBoard(Player player) {
        Claim claim = this.claim(player);
        return claim == null ? null : claim.board;
    }

    @Override
    public Plugin getOwner(Player player) {
        Claim claim = this.claim(player);
        return claim == null ? null : claim.owner;
    }

    /**
     * Forgets a player's board entirely, whoever set it. Used when they quit,
     * so a plugin that does not clean up cannot leak entries.
     */
    public void forget(Player player) {
        this.boards.remove(player.getUniqueId());
    }

    /**
     * A board has to be traceable back to a plugin that said it would be
     * pushing them, so an unregistered caller is turned away.
     */
    private void checkRegistered(Plugin owner) {
        Preconditions.checkNotNull(owner, "owner");
        Preconditions.checkState(owner.equals(this.hooks.get(owner.getName())),
                "%s must call registerHook(Plugin) before using the External RealScoreboard API", owner.getName());
    }

    private Claim claim(Player player) {
        if (player == null) {
            return null;
        }

        Claim claim = this.boards.get(player.getUniqueId());
        if (claim == null) {
            return null;
        }

        //a plugin that was disabled without cleaning up does not get to keep the screen
        if (!claim.owner.isEnabled()) {
            this.boards.remove(player.getUniqueId(), claim);
            return null;
        }

        return claim;
    }

    private static class Claim {
        private final Plugin owner;
        private final ExternalBoard board;

        private Claim(Plugin owner, ExternalBoard board) {
            this.owner = owner;
            this.board = board;
        }
    }
}
