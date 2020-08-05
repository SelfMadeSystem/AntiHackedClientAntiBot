package uwu.smsgamer.antihackedclientantibot.managers;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.scheduler.BukkitTask;
import uwu.smsgamer.antihackedclientantibot.*;

import java.util.*;

public class PlayerManager implements Listener {
    public static PlayerManager instance;
    private final AntiHackedClientAntiBot pl;
    public Map<String, Long> lastVerified = new HashMap<>();
    public Map<String, List<String>> legalStrings = new HashMap<>();
    public Map<String, BaseComponent> components = new HashMap<>();
    public Map<String, Entry<Integer, Integer>> playerAt = new HashMap<>();
    public Map<String, Integer> playerVL = new HashMap<>();
    public Map<String, BukkitTask> playerTasks = new HashMap<>();

    public PlayerManager(AntiHackedClientAntiBot pl) {
        this.pl = pl;
        instance = this;
    }

    public void load() {
        YamlConfiguration config = ConfigManager.getConfig("players");
        for (Map.Entry<String, Object> entry : config.getValues(false).entrySet()) {
            lastVerified.put(entry.getKey(), (Long) entry.getValue());
        }
    }

    public void save() {
        YamlConfiguration config = ConfigManager.getConfig("players");
        for (Map.Entry<String, Long> entry : lastVerified.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        ConfigManager.saveConfig("players");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        String name = event.getPlayer().getName();

        Long l = lastVerified.get(name);
        if (l != null)
            if (l + Vars.timeBetweenChecks > System.currentTimeMillis()) {
                Utils.executeCommands(event.getPlayer(), Vars.alreadyDoneCommands);
                cleanup(name, false);
                return;
            }
        Utils.executeCommands(event.getPlayer(), Vars.joinNotVerifiedCommands);
        lastVerified.put(name, System.currentTimeMillis());
        playerAt.put(name, new Entry<>(0, 0));
        playerVL.put(name, 0);

        Utils.ComponentLegalStrings cls = Utils.generate(0);
        legalStrings.put(name, cls.legalStrings);
        components.put(name, cls.component);
        OwO owo = new OwO();
        playerTasks.put(name, owo.bukkitTask = Bukkit.getScheduler().runTaskTimer(pl, new OogaBoogaRunner(event.getPlayer(), owo), 0, 1L));
        event.getPlayer().spigot().sendMessage(cls.component);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        cleanup(name, playerAt.containsKey(name));
        BukkitTask bt = playerTasks.get(name);
        if (bt != null) bt.cancel();
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent event) {
        String name = event.getPlayer().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(true);
            if (legalStrings.get(name).contains(event.getMessage())) {
                int i = playerAt.get(name).getValue() + 1;
                if (i >= Vars.clicks.size()) {
                    playerAt.remove(name);
                    Utils.executeCommands(event.getPlayer(), Vars.completeCommands);
                    playerTasks.get(name).cancel();
                    cleanup(name, false);
                } else {
                    playerAt.put(name, new Entry<>(0, i));
                    Utils.ComponentLegalStrings cls = Utils.generate(i);
                    legalStrings.put(name, cls.legalStrings);
                    components.put(name, cls.component);
                    event.getPlayer().spigot().sendMessage(cls.component);
                    event.getPlayer().sendMessage(Vars.left.replaceAll("%s%", String.valueOf(Vars.maxTime / 20))
                      .replaceAll("%sp%", (i - Vars.maxTime) / 20 == 1 ? "" : "s")
                      .replaceAll("%c%", String.valueOf(Vars.clicks.size() - playerAt.get(name).getValue()))
                      .replaceAll("%cp%", (Vars.clicks.size() - playerAt.get(name).getValue()) == 1 ? "" : "s"));
                }
            } else {
                if (event.getMessage().startsWith(Vars.clicks.get(playerAt.get(name).val).split("%m%")[0])) {
                    flag(event.getPlayer(), false);
                    return;
                }
                for (String s : Vars.clicks.get(playerAt.get(name).val).split("%m%")[0].split(" ")) {
                    if (event.getMessage().startsWith(s)) {
                        flag(event.getPlayer(), false);
                        return;
                    }
                }
            }
        }
    }

    public void cleanup(String player, boolean verified) {
        if (verified) lastVerified.remove(player);
        legalStrings.remove(player);
        components.remove(player);
        playerAt.remove(player);
        playerTasks.remove(player);
    }

    public void flag(Player player, boolean timeout) {
        String name = player.getName();
        if (timeout) {
            cleanup(name, true);
            Utils.executeCommands(player, Vars.failedTimeout);
        } else {
            int vl = playerVL.get(name) + 1;
            playerVL.put(name, vl);
            Utils.executeCommands(player, Vars.failedCommands.get(vl));
        }
    }

    // block thingies uwu


    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        String name = event.getPlayer().getName();
        if (playerAt.containsKey(name)) {
            double diffX = event.getFrom().getX() - event.getTo().getX();
            double diffY = event.getFrom().getY() - event.getTo().getY();
            double diffZ = event.getFrom().getZ() - event.getTo().getZ();
            Location base = event.getFrom();
            if (Vars.blockMoveHori && (diffX != 0 || diffZ != 0)) {
                base.setY(event.getTo().getY());
                event.setTo(base);
            }
            if (Vars.blockMoveVertiDown && diffY > 0) {
                base.setX(event.getTo().getX());
                base.setZ(event.getTo().getZ());
                event.setTo(base);
            }
            if (Vars.blockMoveVertiUp && diffY < 0) {
                base.setX(event.getTo().getX());
                base.setZ(event.getTo().getZ());
                event.setTo(base);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String name = event.getPlayer().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTab(TabCompleteEvent event) {
        if (!(event.getSender() instanceof Player)) return;
        String name = event.getSender().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        String name = event.getPlayer().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(Vars.disableInteract || event.isCancelled());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        String name = event.getPlayer().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(Vars.disableItemDrop || event.isCancelled());
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        String name = event.getEntity().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(Vars.disableItemPickup || event.isCancelled());
        }
    }

    @EventHandler
    public void onInvOpen(InventoryOpenEvent event) {
        String name = event.getPlayer().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(Vars.disableInventoryOpen || event.isCancelled());
        }
    }

    @EventHandler
    public void onInvOpen(InventoryInteractEvent event) {
        String name = event.getWhoClicked().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(Vars.disableInventoryManagement || event.isCancelled());
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        String name = event.getEntity().getName();
        if (playerAt.containsKey(name)) {
            event.setCancelled(Vars.disableDamage || event.isCancelled());
        }
    }

    static class Entry<K, V> {
        private final V val;
        private K key;

        public Entry(K key, V val) {
            this.key = key;
            this.val = val;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return val;
        }
    }

    static class OwO {
        public BukkitTask bukkitTask;
    }

    static class OogaBoogaRunner implements Runnable {

        final Player player;
        final String name;
        final OwO owo;

        public OogaBoogaRunner(Player player, OwO owo) {
            this.player = player;
            this.name = player.getName();
            this.owo = owo;
        }

        @Override
        public void run() {
            try {
                //code
                int i = PlayerManager.instance.playerAt.get(name).getKey() + 1;
                if (i > Vars.maxTime) {
                    owo.bukkitTask.cancel();
                    PlayerManager.instance.flag(player, true);
                    return;
                }
                if (i % 20 == 0) {
                    player.spigot().sendMessage(PlayerManager.instance.components.get(name));
                    player.sendMessage(Vars.left.replaceAll("%s%", String.valueOf((Vars.maxTime - i) / 20))
                      .replaceAll("%sp%", (Vars.maxTime - i) / 20 == 1 ? "" : "s")
                      .replaceAll("%c%", String.valueOf(Vars.clicks.size() - PlayerManager.instance.playerAt.get(name).getValue()))
                      .replaceAll("%cp%", (Vars.clicks.size() - PlayerManager.instance.playerAt.get(name).getValue()) == 1 ? "" : "s"));
                }
                PlayerManager.instance.playerAt.get(name).setKey(i);
            } catch (Exception e) {
                owo.bukkitTask.cancel();
            }
        }
    }
}
