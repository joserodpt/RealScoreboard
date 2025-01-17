package joserodpt.realscoreboard.api.utils;

/*
 *   ____            _ ____                     _                         _
 *  |  _ \ ___  __ _| / ___|  ___ ___  _ __ ___| |__   ___   __ _ _ __ __| |
 *  | |_) / _ \/ _` | \___ \ / __/ _ \| '__/ _ \ '_ \ / _ \ / _` | '__/ _` |
 *  |  _ <  __/ (_| | |___) | (_| (_) | | |  __/ |_) | (_) | (_| | | | (_| |
 *  |_| \_\___|\__,_|_|____/ \___\___/|_|  \___|_.__/ \___/ \__,_|_|  \__,_|
 *
 *
 * Licensed under the MIT License
 * @author José Rodrigues © 2016-2025
 * @link https://github.com/joserodpt/RealScoreboard
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import joserodpt.realscoreboard.api.RealScoreboardAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerInput implements Listener {

	private static final Map<UUID, PlayerInput> inputs = new HashMap<>();
	private final UUID uuid;
	private final List<String> texts = Text
			.color(Arrays.asList("&l&9Type in chat your input", "&fType &4cancel &fto cancel"));
	private final InputRunnable runGo;
	private final InputRunnable runCancel;
	private final BukkitTask taskId;
	private final Boolean inputMode;

	public PlayerInput(Player p, InputRunnable correct, InputRunnable cancel) {
		this.uuid = p.getUniqueId();
		p.closeInventory();
		this.inputMode = true;
		this.runGo = correct;
		this.runCancel = cancel;
		this.taskId = new BukkitRunnable() {
			public void run() {
				p.sendTitle(texts.get(0), texts.get(1), 0, 21, 0);
			}
		}.runTaskTimer(RealScoreboardAPI.getInstance().getPlugin(), 0L, 20);

		this.register();
	}

	private void register() {
		inputs.put(this.uuid, this);
	}

	private void unregister() {
		inputs.remove(this.uuid);
	}

	@FunctionalInterface
	public interface InputRunnable {
		void run(String input);
	}

	public static Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onPlayerChat(AsyncPlayerChatEvent event) {
				Player p = event.getPlayer();
				String input = event.getMessage();
				UUID uuid = p.getUniqueId();
				if (inputs.containsKey(uuid)) {
					PlayerInput current = inputs.get(uuid);
					if (current.inputMode) {
						event.setCancelled(true);
						try {
							if (input.equalsIgnoreCase("cancel")) {
								Text.send(p, "&cInput cancelled.");
								current.taskId.cancel();
								p.sendTitle("", "", 0, 1, 0);
								Bukkit.getScheduler().scheduleSyncDelayedTask(RealScoreboardAPI.getInstance().getPlugin(), () -> current.runCancel.run(input), 3);
								current.unregister();
								return;
							}

							current.taskId.cancel();
							Bukkit.getScheduler().scheduleSyncDelayedTask(RealScoreboardAPI.getInstance().getPlugin(), () -> current.runGo.run(input), 3);
							p.sendTitle("", "", 0, 1, 0);
							current.unregister();
						} catch (Exception e) {
							Text.send(p, "&cAn error ocourred. Contact JoseGamer_PT on SpigotMC.com");
							RealScoreboardAPI.getInstance().getLogger().severe(e.getMessage());
						}
					}
				}
			}

		};
	}
}