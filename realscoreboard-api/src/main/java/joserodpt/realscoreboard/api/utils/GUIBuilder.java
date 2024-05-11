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
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealScoreboard
 */

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIBuilder {

    /*
     * Modified and optimized version of AdvInventory Original author:
     * http://spigotmc.org/members/25376/ - Homer04 Original utility version:
     * http://www.spigotmc.org/threads/133942/ Modified by AnyOD Compatible
     * https://www.spigotmc.org/threads/gui-creator-v2-making-inventories-was-never-easier.296898/
     * versions: 1.8 and up
     */

    private static final Map<UUID, GUIBuilder> inventories = new HashMap<>();
    private Inventory inv;
    private final Map<Integer, ClickRunnable> runnables = new HashMap<>();
    private final UUID uuid;

    public GUIBuilder(final String name, final int size, final UUID uuid) {
        this(Text.color(name), size, uuid, null);
    }

    public GUIBuilder(final String name, final int size, final UUID uuid, final ItemStack placeholder) {
        this.uuid = uuid;
        if (size == 0) {
            return;
        }
        this.inv = Bukkit.createInventory(null, size, Text.color(name));
        if (placeholder != null) {
            for (int i = 0; i < size; ++i) {
				this.inv.setItem(i, placeholder);
            }
        }
        this.register();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(final InventoryClickEvent e) {
                final HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    if (e.getCurrentItem() == null) {
                        return;
                    }
                    final Player p = (Player) clicker;
                    if (p != null) {
                        final UUID uuid = p.getUniqueId();
                        if (inventories.containsKey(uuid)) {
                            final GUIBuilder current = inventories.get(uuid);
                            if (!e.getInventory().getType().name()
                                    .equalsIgnoreCase(current.getInventory().getType().name())) {
                                return;
                            }
                            e.setCancelled(true);
                            final int slot = e.getSlot();
                            if (current.runnables.get(slot) != null) {
                                current.runnables.get(slot).run(e);
                            }
                        }
                    }
                }
            }

            @EventHandler
            public void onClose(final InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    final Player p = (Player) e.getPlayer();
                    final UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unRegister();
                    }
                }
            }
        };
    }

    public static ItemStack placeholder(final DyeColor d, final String n) {
        @SuppressWarnings("deprecation") final ItemStack placeholder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1, d.getDyeData());
        final ItemMeta placeholdermeta = placeholder.getItemMeta();
        placeholdermeta.setDisplayName(n);
        placeholder.setItemMeta(placeholdermeta);
        return placeholder;
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public int getSize() {
        return this.inv.getSize();
    }

    public void setItem(final ItemStack is, final Integer slot, final ClickRunnable executeOnClick) {
		this.inv.setItem(slot, is);
		this.runnables.put(slot, executeOnClick);
    }

    public void setItem(final ClickRunnable executeOnClick, final ItemStack itemstack, final Integer slot) {
        final ItemMeta im = itemstack.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		this.inv.setItem(slot, itemstack);
		this.runnables.put(slot, executeOnClick);
    }

    public void removeItem(final int slot) {
		this.inv.setItem(slot, new ItemStack(Material.AIR));
    }

    public void setItem(final ItemStack itemstack, final Integer slot) {
		this.inv.setItem(slot, itemstack);
    }

    public void openInventory(final Player player) {
        final Inventory inv = this.getInventory();
        final InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            final Inventory openTop = player.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.openInventory(inv);
            }
			this.register();
        }
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unRegister() {
        inventories.remove(this.uuid);
    }

    public void addItem(final ClickRunnable clickRunnable, final ItemStack i, final int slot) {
        final ItemMeta im = i.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
                ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		this.inv.setItem(slot, i);
		this.runnables.put(slot, clickRunnable);
    }

    @FunctionalInterface
    public interface ClickRunnable {
        void run(InventoryClickEvent event);
    }

    @FunctionalInterface
    public interface CloseRunnable {
        void run(InventoryCloseEvent event);
    }
}