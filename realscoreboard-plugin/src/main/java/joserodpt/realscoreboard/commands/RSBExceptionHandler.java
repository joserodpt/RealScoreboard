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

import joserodpt.realutils.command.LampExceptionHandler;
import joserodpt.realutils.text.Text;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.EmptyEntitySelectorException;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.MalformedEntitySelectorException;

/**
 * Puts the errors Lamp raises through RealScoreboard's own wording, so a mistyped command reads the
 * same as every other message the plugin sends. Anything not handled here or in
 * {@link LampExceptionHandler} keeps Lamp's own wording, which is already specific about what it
 * couldn't parse.
 */
public class RSBExceptionHandler extends LampExceptionHandler {

    public RSBExceptionHandler() {
        super(sender -> Text.send(sender, "&cThe command you're trying to run doesn't exist."),
                sender -> Text.send(sender, "&cYou don't have permission to execute this command!"),
                sender -> Text.send(sender, "&cOnly players can use this command."),
                () -> "&cWrong usage for this command. Check if you inputed all the arguments.");
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
}
