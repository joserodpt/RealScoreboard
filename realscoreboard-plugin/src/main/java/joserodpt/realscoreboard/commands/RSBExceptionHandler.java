package joserodpt.realscoreboard.commands;

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

import joserodpt.realscoreboard.api.utils.Text;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.bukkit.exception.EmptyEntitySelectorException;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.MalformedEntitySelectorException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.exception.UnknownCommandException;
import revxrsal.commands.node.ParameterNode;

/**
 * Puts the errors Lamp raises through RealScoreboard's own wording, so a mistyped command reads the
 * same as every other message the plugin sends. Anything not overridden here keeps Lamp's own
 * wording, which is already specific about what it couldn't parse.
 */
public class RSBExceptionHandler extends BukkitExceptionHandler {

    @Override
    public void onUnknownCommand(final UnknownCommandException e, final BukkitCommandActor actor) {
        Text.send(actor.sender(), "&cThe command you're trying to run doesn't exist.");
    }

    @Override
    public void onNoPermission(final NoPermissionException e, final BukkitCommandActor actor) {
        Text.send(actor.sender(), "&cYou don't have permission to execute this command!");
    }

    /**
     * Player-only commands take a {@link org.bukkit.entity.Player} instead of a
     * {@link org.bukkit.command.CommandSender}, and this is where console gets told so.
     */
    @Override
    public void onSenderNotPlayer(final SenderNotPlayerException e, final BukkitCommandActor actor) {
        Text.send(actor.sender(), "&cOnly players can use this command.");
    }

    /**
     * Lamp resolves a {@code Player} parameter itself, so the commands no longer check for null and
     * this is the only place an offline or misspelled name is reported.
     */
    @Override
    public void onInvalidPlayer(final InvalidPlayerException e, final BukkitCommandActor actor) {
        Text.send(actor.sender(), "&cPlayer not found.");
    }

    /**
     * A selector that matched nobody, such as {@code @a} on an empty server. Reported like a
     * missing player, because from the caller's side that is what happened.
     */
    @Override
    public void onEmptyEntitySelector(final EmptyEntitySelectorException e, final BukkitCommandActor actor) {
        Text.send(actor.sender(), "&cNo players matched &f" + e.input() + "&c.");
    }

    @Override
    public void onMalformedEntitySelector(final MalformedEntitySelectorException e, final BukkitCommandActor actor) {
        Text.send(actor.sender(), "&cInvalid selector &f" + e.input() + "&c: " + e.errorMessage());
    }

    @Override
    public void onMissingArgument(final MissingArgumentException e, final BukkitCommandActor actor,
                                  final ParameterNode<BukkitCommandActor, ?> parameter) {
        Text.send(actor.sender(), usageOf(e.command()));
    }

    /**
     * The handwritten {@link Usage} on the method, which spells the command out the way players are
     * used to seeing it. Commands without one fall back to the generic line, as they always did.
     */
    private static String usageOf(final ExecutableCommand<?> command) {
        final Usage usage = command.annotations().get(Usage.class);
        return usage == null
                ? "&cWrong usage for this command. Check if you inputed all the arguments."
                : usage.value();
    }
}
