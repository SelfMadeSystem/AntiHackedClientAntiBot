package uwu.smsgamer.antihackedclientantibot.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class ConfigManager {
    private static JavaPlugin pl;

    public static void setup(JavaPlugin plugin, String... configs) {
        pl = plugin;
        for (String config : configs) {
            Bukkit.getLogger().info("Loading config: " + config);
            try {
                loadConfig(config);
                Bukkit.getLogger().info("Loaded config: " + config);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Error while loading config: " + config);
            }
        }
    }

    public static Map<String, YamlConfiguration> configs = new HashMap<>();

    public static YamlConfiguration getConfig(String name) {
        return configs.get(name);
    }

    public static void loadConfig(String name) {
        configs.remove(name);
        File configFile = new File(pl.getDataFolder(), name + ".yml");
        if (!configFile.exists())
            pl.saveResource(name + ".yml", false);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        configs.put(name, config);
    }

    public static void saveConfig(String name) {
        try {
            configs.get(name).save(pl.getDataFolder().getAbsolutePath() + File.separator + name + ".yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
