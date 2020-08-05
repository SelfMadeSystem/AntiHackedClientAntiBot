package uwu.smsgamer.antihackedclientantibot;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import uwu.smsgamer.antihackedclientantibot.managers.ConfigManager;

import java.util.*;

public class Vars {
    public static String header;
    public static String button;
    public static String footer;
    public static String left;
    public static List<Character> clickable;
    public static List<String> clicks;
    public static String allowedChars;
    public static int msgLthMin;
    public static int msgLthMax;
    public static int maxTime;
    public static int timeBetweenChecks;
    public static boolean papi; //placeholder api
    public static List<String> failedTimeout;
    public static HashMap<Integer, List<String>> failedCommands = new HashMap<>();
    public static List<String> completeCommands;
    public static List<String> alreadyDoneCommands;
    public static List<String> joinNotVerifiedCommands;
    public static boolean blockMoveHori;
    public static boolean blockMoveVertiUp;
    public static boolean blockMoveVertiDown;
    public static boolean disableInteract;
    public static boolean disableItemDrop;
    public static boolean disableItemPickup;
    public static boolean disableDamage;
    public static boolean disableInventoryOpen;
    public static boolean disableInventoryManagement;

    public static void setup() {
        YamlConfiguration config = ConfigManager.getConfig("config");
        header = ChatColor.translateAlternateColorCodes('&', config.getString("messages.header"));
        button = ChatColor.translateAlternateColorCodes('&', config.getString("messages.button"));
        footer = ChatColor.translateAlternateColorCodes('&', config.getString("messages.footer"));
        left = ChatColor.translateAlternateColorCodes('&', config.getString("messages.left"));
        clickable = new ArrayList<>();
        char[] clickables = config.getString("messages.clickable").toCharArray();
        for (char c : clickables) {
            clickable.add(c);
        }
        clicks = config.getStringList("clicks");
        allowedChars = config.getString("message-allowed-chars");
        msgLthMin = config.getInt("message-length.min");
        msgLthMax = config.getInt("message-length.max");
        maxTime = config.getInt("max-time.counted-in-ticks");
        timeBetweenChecks = config.getInt("time-between-checks.counted-in-ms");
        failedTimeout = config.getStringList("commands.failed-timout");
        for (String path : config.getConfigurationSection("commands.failed-wrong-message").getKeys(false))
            failedCommands.put(Integer.parseInt(path), config.getStringList("commands.failed-wrong-message." + path));
        completeCommands = config.getStringList("commands.complete");
        alreadyDoneCommands = config.getStringList("commands.already-done");
        joinNotVerifiedCommands = config.getStringList("commands.join-not-verified");
        blockMoveHori = config.getBoolean("block-during-check.movement.horizontal");
        blockMoveVertiUp = config.getBoolean("block-during-check.movement.vertical-up");
        blockMoveVertiDown = config.getBoolean("block-during-check.movement.vertical-down");
        disableInteract = config.getBoolean("block-during-check.disable-interact");
        disableItemDrop = config.getBoolean("block-during-check.disable-item-drop");
        disableItemPickup = config.getBoolean("block-during-check.disable-item-pickup");
        disableDamage = config.getBoolean("block-during-check.disable-damage");
        disableInventoryOpen = config.getBoolean("block-during-check.disable-inventory-open");
        disableInventoryManagement = config.getBoolean("block-during-check.disable-inventory-management");
    }
}
