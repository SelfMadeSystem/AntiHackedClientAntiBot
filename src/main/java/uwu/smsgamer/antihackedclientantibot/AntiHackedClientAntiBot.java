package uwu.smsgamer.antihackedclientantibot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uwu.smsgamer.antihackedclientantibot.managers.*;

@SuppressWarnings("unused")
public final class AntiHackedClientAntiBot extends JavaPlugin {

    public static AntiHackedClientAntiBot instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new PlayerManager(this), this);
        ConfigManager.setup(this, "config", "players");
        PlayerManager.instance.load();
        Vars.setup();
        checkPAPI();
    }

    @Override
    public void onDisable() {
        PlayerManager.instance.save();
    }

    private void checkPAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI found.");
            Vars.papi = true;
        } else {
            getLogger().info("PlaceholderAPI not found.");
            Vars.papi = false;
        }
    }
}
