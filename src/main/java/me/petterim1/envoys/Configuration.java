package me.petterim1.envoys;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.utils.Config;

import java.util.*;

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
    private double rareChance;
    private String startSound;
    private String stopSound;
    private List<Location> allEnvoys = new ArrayList<>();
    private List<ItemSlot> items = new ArrayList<>();
    private List<EffectSlot> effects = new ArrayList<>();
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

        loadSettings();

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

    private void loadSettings() {
        hint = c.getInt("hint", -1);
        startSound = c.getString("startSound");
        stopSound = c.getString("endSound");
        rareChance = c.getDouble("rareChance", 1.0);
    }

    boolean setEnvoy(Location loc) {
        if (allEnvoys.contains(loc)) {
            return false;
        }
        allEnvoys.add(loc);
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

        try {
            for (String s : i.getStringList("itemsNormal")) {
                //items.add(new ItemSlot(chance, false, id, meta, count, name, enchantment, level));
            }
            for (String s : i.getStringList("itemsSuper")) {
                //items.add(new ItemSlot(chance, true, id, meta, count, name, enchantment, level));
            }
            for (String s : i.getStringList("effectsNormal")) {
                //effects.add(new EffectSlot(chance, false, id, amplifier, duration));
            }
            for (String s : i.getStringList("effectsSuper")) {
                //effects.add(new EffectSlot(chance, true, id, amplifier, duration));
            }
        } catch (Exception ignore) {
            return false;
        }
        return true;
    }

    private void loadData() {
        try {
            d = new Config(pl.getDataFolder() + "/data.yml", Config.YAML);

            if (d.getBoolean("firstRun", true)) {
                pl.getServer().getLogger().info(Envoys.prefix + "\u00A7aEnvoys plugin by PetteriM1 loaded! Please consider leaving a rating on nukkitx.com if you like the plugin :)");
                d.set("firstRun", false);
                d.save();
            }

            //TODO: load locations
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
        for (Location loc : allEnvoys) {
            str.append(loc.x).append(", ").append(loc.y).append(", ").append(loc.z).append(", ").append(loc.level.getName()).append("\n");
        }
        return str.toString();
    }

    void quitEditmode() {
        for (Location loc : allEnvoys) {
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
                playsound(startSound);
                break;
            case "end":
                pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.end"));
                playsound(stopSound);
                break;
        }
    }

    private void playsound(String s) {
        for (Player p : pl.getServer().getOnlinePlayers().values()) {
            PlaySoundPacket pk = new PlaySoundPacket();
            pk.name = s;
            pk.volume = 1;
            pk.pitch = 1;
            pk.x = (int) p.x;
            pk.y = (int) p.y;
            pk.z = (int) p.z;
            p.dataPacket(pk);
        }
    }

    private void checkLicense() {
        if (!pl.getDescription().getAuthors().get(0).equals("PetteriM1") || !pl.getDescription().getVersion().startsWith("1") || !pl.getDescription().getMain().equals("me.petterim1.envoys.Envoys") || !pl.getDescription().getName().equals("Envoys")) {
            System.exit(1);
        }
    }

    void removeEnvoys() {
        for (Location loc : currentEnvoys.keySet()) {
            loc.getLevel().setBlock(loc, Block.get(0), true, false);
        }
    }

    private boolean rand() {
        return r.nextDouble(10) * rareChance > 9.0;
    }

    private void placeRandomEnvoys() {
        //todo: place, broadcast placed + count, set randoms
        for (Location l : allEnvoys) {
            /*if () {
                currentEnvoys.put(l, rand());
            }*/
        }
    }
}
