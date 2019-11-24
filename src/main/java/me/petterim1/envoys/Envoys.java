package me.petterim1.envoys;

import cn.nukkit.plugin.PluginBase;

import java.util.ArrayList;
import java.util.List;

public class Envoys extends PluginBase {

    Configuration c;
    List<String> editmode = new ArrayList<>();
    static final String prefix = "\u00A78[\u00A7l\u00A7cEnvoys\u00A7r\u00A78] \u00A7r";

    public void onEnable() {
        saveDefaultConfig();
        saveResource("items.yml");
        saveResource("data.yml");
        c = new Configuration(this, getConfig());
        if (!c.init()) return;
        getServer().getCommandMap().register("envoy", new Command(this));
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getServer().getScheduler().scheduleDelayedRepeatingTask(this, new Task(this), 20, 20);
        getServer().getLogger().info(prefix + "\u00A7aEnvoys plugin by PetteriM1 loaded! Please consider leaving a rating on nukkitx.com if you like the plugin :)");
    }

    public void onDisable() {
        c.saveData();
        c.removeEnvoys();
    }
}
