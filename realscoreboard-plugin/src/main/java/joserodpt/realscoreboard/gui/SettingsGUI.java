package joserodpt.realscoreboard.gui;

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

import com.google.common.collect.ImmutableList;
import joserodpt.realscoreboard.api.RealScoreboardAPI;
import joserodpt.realscoreboard.api.config.RSBConfig;
import joserodpt.realscoreboard.api.utils.Items;
import joserodpt.realscoreboard.api.utils.Pagination;
import joserodpt.realscoreboard.api.utils.PlayerInput;
import joserodpt.realscoreboard.api.utils.Text;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SettingsGUI {

    @Getter
    public static class SettingEntry {

        //0 - bool, 1 - int
        public int entryType;

        private String configPath, name;
        public SettingEntry(final String name, final String configPath, final int entryType) {
            this.name = "&f"+name;
            this.configPath = configPath;
            this.entryType = entryType;
        }

        public String getName() {
            return Text.color(name);
        }

        public ItemStack getItem() {
            if (entryType == 0) {
                boolean val = RSBConfig.file().getBoolean(configPath);
                return Items.createItemLore(val ? Material.REDSTONE_TORCH : Material.LEVER, 1, this.getName() + " &f- " + (val ? "&a&lON" : "&c&lOFF"), Collections.singletonList("&7Click here to toggle this setting."));
            } else {
                return Items.createItemLore(Material.OAK_BUTTON, Math.min(64, Math.max(1, RSBConfig.file().getInt(configPath))), this.getName() + ": " + RSBConfig.file().getInt(configPath), Collections.singletonList("&7Click here to change this value."));
            }
        }
    }

    private static final Map<UUID, SettingsGUI> inventories = new HashMap<>();
    int pageNumber = 0;
    private Pagination<SettingEntry> p;
    private final ItemStack placeholder = Items.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "");
    static ItemStack next = Items.createItem(Material.GREEN_STAINED_GLASS, 1,  "&aNext");
    static ItemStack back = Items.createItem(Material.YELLOW_STAINED_GLASS, 1,"&6Back");
    static ItemStack close = Items.createItemLore(Material.OAK_DOOR, 1, "&cClose",
           Collections.singletonList("&7Click here to close the settings."));
    private final Inventory inv;
    private final UUID uuid;
    private final RealScoreboardAPI rsa;
    private final Map<Integer, SettingEntry> display = new HashMap<>();

    private final ImmutableList<SettingEntry> list = ImmutableList.of(
            new SettingEntry("Check for Updates", "Config.Check-for-Updates", 0),
            new SettingEntry("mcMMO Support", "Config.mcMMO-Support", 0),
            new SettingEntry("Use Placeholders In Scoreboard Titles", "Config.Use-Placeholders-In-Scoreboard-Titles", 0),
            new SettingEntry("RealScoreboard Disabled By Default", "Config.RealScoreboard-Disabled-By-Default", 0),
            new SettingEntry("Auto Hide In Vanish", "Config.Auto-Hide-In-Vanish", 0),
            new SettingEntry("Switch Scoreboards Between Worlds", "Config.World-Scoreboard-Switch", 0),
            new SettingEntry("Animations Loop Delay", "Config.Animations.Loop-Delay", 1),
            new SettingEntry("Hours Offset", "Config.Hours.Offset", 1)
    );

    public SettingsGUI(Player a, RealScoreboardAPI rsa) {
        this.rsa = rsa;
        this.uuid = a.getUniqueId();
        this.inv = Bukkit.getServer().createInventory(null, 54, Text.color("&f&lReal&d&lScoreboard &8| Settings (" + list.size() + ")"));

        this.load();

        this.register();
    }

    public void load() {
        this.p = new Pagination<>(28, list);
        fillChest(p.getPage(pageNumber));
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onClick(InventoryClickEvent e) {
                HumanEntity clicker = e.getWhoClicked();
                if (clicker instanceof Player) {
                    final Player p = (Player) clicker;
                    if (e.getCurrentItem() == null) {
                        return;
                    }
                    UUID uuid = clicker.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        SettingsGUI current = inventories.get(uuid);
                        if (e.getInventory().getHolder() != current.getInventory().getHolder()) {
                            return;
                        }

                        e.setCancelled(true);

                        switch (e.getRawSlot()) {
                            case 49:
                                p.closeInventory();
                                break;
                            case 26:
                            case 35:
                                if (!current.lastPage()) {
                                    nextPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                            case 18:
                            case 27:
                                if (!current.firstPage()) {
                                    backPage(current);
                                    p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 50, 50);
                                }
                                break;
                        }

                        if (current.display.containsKey(e.getRawSlot())) {
                            SettingEntry a = current.display.get(e.getRawSlot());

                            if (a.getEntryType() == 0) {
                                RSBConfig.file().set(a.getConfigPath(), !RSBConfig.file().getBoolean(a.getConfigPath()));
                                RSBConfig.save();
                                current.load();
                            } else {
                                p.closeInventory();
                                new PlayerInput(p, input -> {
                                    int val;
                                    try {
                                        val = Integer.parseInt(input);
                                    } catch (Exception ignored) {
                                        Text.send(p, "&cNot a valid number without decimal points.");
                                        return;
                                    }

                                    RSBConfig.file().set(a.getConfigPath(), val);
                                    RSBConfig.save();
                                    Text.send(p, "&fSetting &b" + ChatColor.stripColor(a.getName()) + "&f value has been set to &a" + val);

                                    SettingsGUI v = new SettingsGUI(p, current.rsa);
                                    v.openInventory(p);
                                }, input -> {
                                    SettingsGUI v = new SettingsGUI(p, current.rsa);
                                    v.openInventory(p);
                                });
                            }
                        }
                    }
                }
            }

            private void backPage(SettingsGUI asd) {
                if (asd.p.exists(asd.pageNumber - 1)) {
                    --asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            private void nextPage(SettingsGUI asd) {
                if (asd.p.exists(asd.pageNumber + 1)) {
                    ++asd.pageNumber;
                }

                asd.fillChest(asd.p.getPage(asd.pageNumber));
            }

            @EventHandler
            public void onClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    if (e.getInventory() == null) {
                        return;
                    }
                    Player p = (Player) e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    if (inventories.containsKey(uuid)) {
                        inventories.get(uuid).unregister();
                    }
                }
            }
        };
    }

    private boolean lastPage() {
        return pageNumber == (p.totalPages() - 1);
    }

    private boolean firstPage() {
        return pageNumber == 0;
    }

    public void openInventory(Player player) {
        Inventory inv = getInventory();
        InventoryView openInv = player.getOpenInventory();
        if (openInv != null) {
            Inventory openTop = player.getOpenInventory().getTopInventory();
            if (openTop != null && openTop.getType().name().equalsIgnoreCase(inv.getType().name())) {
                openTop.setContents(inv.getContents());
            } else {
                player.openInventory(inv);
            }
            register();
        }
    }

    public void fillChest(List<SettingEntry> items) {
        inv.clear();
        display.clear();

        for (int slot : new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53}) {
            inv.setItem(slot, placeholder);
        }

        if (firstPage()) {
            inv.setItem(18, placeholder);
            inv.setItem(27, placeholder);
        } else {
            inv.setItem(18, back);
            inv.setItem(27, back);
        }

        if (lastPage()) {
            inv.setItem(26, placeholder);
            inv.setItem(35, placeholder);
        } else {
            inv.setItem(26, next);
            inv.setItem(35, next);
        }

        this.inv.setItem(49, close);

        int slot = 0;
        for (ItemStack i : inv.getContents()) {
            if (i == null) {
                if (!items.isEmpty()) {
                    SettingEntry s = items.get(0);
                    inv.setItem(slot, s.getItem());
                    display.put(slot, s);
                    items.remove(0);
                }
            }
            ++slot;
        }
    }

    public Inventory getInventory() {
        return inv;
    }

    private void register() {
        inventories.put(this.uuid, this);
    }

    private void unregister() {
        inventories.remove(this.uuid);
    }
}
