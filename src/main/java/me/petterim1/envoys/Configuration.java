package me.petterim1.envoys;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class Configuration {

    private Envoys pl;
    Effects e;
    private Config c;
    private Config i;
    private Config d;
    private static final int c_ver = 1;
    boolean loaded;
    boolean now;
    int nextEnvoy;
    int nowTicks;
    int hint;
    int hintTicks;
    private Map<Location, Boolean> allEnvoys = new HashMap();
    private Map<Location, Boolean> currentEnvoys = new HashMap();
    private static final SplittableRandom r = new SplittableRandom();

    Configuration(Envoys pl, Config c) {
        this.pl = pl;
        this.c = c;
        this.e = new Effects(pl);
    }

    boolean init() {
        checkLicense();

        if (c_ver != c.getInt("configVersion")) {
            if (!tryUpdate()) {
                pl.getLogger().error("Invalid config. Plugin will be disabled.");
                pl.getServer().getPluginManager().disablePlugin(pl);
                return false;
            }
        }

        hint = c.getInt("hint", -1);

        if (!loadItems()) {
            return false;
        }

        loadData();
        resetTimer();
        loaded = true;
        return true;
    }

    private boolean tryUpdate() {
        return false;
    }

    boolean setEnvoy(Location loc) {
        if (allEnvoys.containsKey(loc)) {
            return false;
        }
        allEnvoys.put(loc, rand());
        return true;
    }

    boolean delEnvoy(Location loc) {
        return allEnvoys.remove(loc);
    }

    String translate(String s) {
        return !c.getString(s).isEmpty() ? c.getString(s) : s;
    }

    private boolean loadItems() {
        i = new Config(pl.getDataFolder() + "/items.yml", Config.YAML);
        if (c_ver != i.getInt("version")) {
            pl.getLogger().error("Invalid item config. Plugin will be disabled.");
            pl.getServer().getPluginManager().disablePlugin(pl);
            return false;
        }

        //TODO
        return true;
    }

    private void loadData() {
        try {
            d = new Config(pl.getDataFolder() + "/data.yml", Config.YAML);

            //TODO
        } catch (Exception ignore) {
            pl.getLogger().error("There was an error while loading saved data. Invalid data.yml file detected.");
        }
    }

    private void resetTimer() {
        nextEnvoy = c.getInt("time");
    }

    String getTime() {
        return now ? "\u00A7anow" : (((nextEnvoy % 86400 ) / 3600) + "h " + (((nextEnvoy % 86400 ) % 3600 ) / 60) + "m " + (((nextEnvoy % 86400 ) % 3600 ) % 60) + "s");
    }

    void saveData() {
        if (loaded) {
            d.set("envoys", allEnvoys);
            d.save();
        }
    }

    String getLocations() {
        StringBuilder str = new StringBuilder();
        for (Location loc : allEnvoys.keySet()) {
            str.append(loc.x).append(", ").append(loc.y).append(", ").append(loc.z).append(", ").append(loc.level.getName()).append("\n");
        }
        return str.toString();
    }

    void quitEditmode() {
        for (Location loc : allEnvoys.keySet()) {
            loc.getLevel().setBlock(loc, Block.get(0), true, false);
        }
    }

    boolean isEnvoyAt(Location loc) {
        if (!now) {
            return false;
        }

        return currentEnvoys.containsKey(loc);
    }

    void claimEnvoy(Player p, Location loc) {
        loc.getLevel().setBlock(loc, Block.get(0), true, false);
        boolean isSuper = currentEnvoys.get(loc);
        currentEnvoys.remove(loc);
        e.spawnOpenEffect(isSuper, loc);
        giveItems(p, isSuper);
        checkLastEnvoy();
    }

    private void checkLastEnvoy() {
        if (currentEnvoys.size() <= 0) {
            now = false;
        }
    }

    private void giveItems(Player p, boolean su) {
        String items = "";
        //for (Item i : ) {
        //  items += i.getName();
        //}
        p.sendMessage(Envoys.prefix + translate("items.given") + items);
    }

    void doEnvoy() {
        nextEnvoy = -1;
        nowTicks = c.getInt("duration");
        hintTicks = hint;
        now = true;
        placeRandomEnvoys();
        broadcast("start");
    }

    void endEnvoy() {
        removeEnvoys();
        now = false;
        broadcast("end");
    }

    private void broadcast(String mode) {
        switch (mode) {
            case "start":
                pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.start"));
                //playsound(startSound);
                break;
            case "end":
                pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.end"));
                //playsound(stopSound);
                break;
        }
    }

    private void playsound(String sound) {
        //TODO
    }
    
    private void checkLicense() {
        if (!pl.getDescription().getAuthors().contains("PetteriM1") || !pl.getDescription().getVersion().startsWith("1") || !pl.getDescription().getMain().equals("me.petterim1.envoys.Envoys") || !pl.getDescription().getName().equals("Envoys")) {
            System.exit(1);
        }
    }

    void removeEnvoys() {
        for (Location loc : currentEnvoys.keySet()) {
            loc.getLevel().setBlock(loc, Block.get(0), true, false);
        }
    }

    private static boolean rand() {
        switch (r.nextInt(10)) {
            case 2:
            case 6:
            case 8:
                return true;
        }
        return false;
    }

    private void placeRandomEnvoys() {
        //todo: place, broadcast placed + count
    }
}
