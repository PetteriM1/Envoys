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
    private Config d;
    private static final int c_ver = 1;
    boolean loaded;
    boolean now;
    int nextEnvoy;
    int nowTicks;
    int hint;
    int hintTicks;
    private int bid;
    private int bm;
    private int subid;
    private int subm;
    private int minimumLoot;
    private double rareChance;
    private String startSound;
    private String stopSound;
    private List<Location> allEnvoys = new ArrayList<>();
    private List<ItemSlot> items = new ArrayList<>();
    private List<EffectSlot> effects = new ArrayList<>();
    protected Map<Location, Boolean> currentEnvoys = new HashMap();
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
        minimumLoot = c.getInt("minimumLoot", 3);
        bid = c.getInt("envoyBlockId", 54);
        bm = c.getInt("envoyBlockMeta");
        subid = c.getInt("rareEnvoyBlockId", 130);
        subm = c.getInt("rareEnvoyBlockMeta");
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
        Config i = new Config(pl.getDataFolder() + "/items.yml", Config.YAML);

        if (c_ver != i.getInt("version")) {
            pl.getLogger().error("Invalid item config. Plugin will be disabled.");
            pl.getServer().getPluginManager().disablePlugin(pl);
            return false;
        }

        try {
            for (String s : i.getStringList("itemsNormal")) {
                String[] info = s.split(":");
                String name;
                int enchantment;
                int level;
                if (info.length > 4 && info[4] != null) {
                    if (info.length > 6 && info[6] != null) {
                        name = info[4];
                        enchantment = Integer.parseInt(info[5]);
                        level = Integer.parseInt(info[6]);
                    } else if (info.length > 5 && info[5] != null) {
                        name = "";
                        enchantment = Integer.parseInt(info[4]);
                        level = Integer.parseInt(info[5]);
                    } else {
                        name = info[4];
                        enchantment = -1;
                        level = -1;
                    }
                } else {
                    name = "";
                    enchantment = -1;
                    level = -1;
                }
                items.add(new ItemSlot(Double.parseDouble(info[3]), false, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), name, enchantment, level));
            }
            for (String s : i.getStringList("itemsSuper")) {
                String[] info = s.split(":");
                String name;
                int enchantment;
                int level;
                if (info.length > 4 && info[4] != null) {
                    if (info.length > 6 && info[6] != null) {
                        name = info[4];
                        enchantment = Integer.parseInt(info[5]);
                        level = Integer.parseInt(info[6]);
                    } else if (info.length > 5 && info[5] != null) {
                        name = "";
                        enchantment = Integer.parseInt(info[4]);
                        level = Integer.parseInt(info[5]);
                    } else {
                        name = info[4];
                        enchantment = -1;
                        level = -1;
                    }
                } else {
                    name = "";
                    enchantment = -1;
                    level = -1;
                }
                items.add(new ItemSlot(Double.parseDouble(info[3]), true, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), name, enchantment, level));
            }
            for (String s : i.getStringList("effectsNormal")) {
                String[] info = s.split(":");
                effects.add(new EffectSlot(Double.parseDouble(info[3]), false, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2])));
            }
            for (String s : i.getStringList("effectsSuper")) {
                String[] info = s.split(":");
                effects.add(new EffectSlot(Double.parseDouble(info[3]), true, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2])));
            }
        } catch (Exception e) {
            pl.getLogger().error("An error occurred while trying to load items config.", e);
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

            allEnvoys = (List<Location>) d.getList("envoys");
        } catch (Exception e) {
            pl.getLogger().error("There was an error while loading saved data. Invalid data.yml file detected.", e);
        }
    }

    private void resetTimer() {
        nextEnvoy = c.getInt("time");
    }

    String getTime() {
        return now ? "\u00A7anow" : (((nextEnvoy % 86400 ) / 3600) + "h " + (((nextEnvoy % 86400 ) % 3600 ) / 60) + "m " + (((nextEnvoy % 86400 ) % 3600 ) % 60) + 's');
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
            str.append(loc.x).append(", ").append(loc.y).append(", ").append(loc.z).append(", ").append(loc.level.getName()).append('\n');
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
        giveEffects(p, isSuper);
        checkLastEnvoy();
    }

    private void checkLastEnvoy() {
        if (currentEnvoys.size() <= 0) {
            now = false;
        }
    }

    private void giveItems(Player p, boolean su) {
        StringBuilder names = new StringBuilder();
        int itemsGiven = 0;
        while (itemsGiven < minimumLoot) {
            for (ItemSlot slot : items) {
                if ((su && !slot.su) || (!su && slot.su)) {
                    if (rand(slot.chance)) {
                        names.append('[').append(slot.item.getName()).append(']');
                        p.getInventory().addItem(slot.item);
                        itemsGiven++;
                    }
                }
            }
        }
        p.sendMessage(Envoys.prefix + translate("items.given") + names);
    }

    private void giveEffects(Player p, boolean su) {
        StringBuilder names = new StringBuilder();
        int given = 0;
        while (given < Math.min(1, effects.size())) {
            for (EffectSlot slot : effects) {
                if ((su && !slot.su) || (!su && slot.su)) {
                    if (rand(slot.chance)) {
                        names.append('[').append(slot.effect.getName()).append(']');
                        slot.effect.add(p);
                        given++;
                    }
                }
            }
        }
        p.sendMessage(Envoys.prefix + translate("effects.given") + names);
    }

    void doEnvoy() {
        nextEnvoy = -1;
        nowTicks = c.getInt("duration");
        hintTicks = hint;
        now = true;
        broadcast("start", placeRandomEnvoys());
    }

    void endEnvoy() {
        removeEnvoys();
        now = false;
        broadcast("end", 0);
    }

    private void broadcast(String mode, int data) {
        switch (mode) {
            case "start":
                pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.start") + data);
                playSound(startSound);
                break;
            case "end":
                pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.end"));
                playSound(stopSound);
                break;
        }
    }

    private void playSound(String s) {
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
        if (!pl.getDescription().getAuthors().get(0).equals("PetteriM1") || !pl.getDescription().getVersion().startsWith("1") || !pl.getDescription().getMain().equals("me.petterim1.envoys.Envoys") || !pl.getDescription().getName().equals("Envoys") || !pl.getDescription().getDescription().equals("Envoys plugin for Nukkit")) {
            System.exit(1);
        }
    }

    void removeEnvoys() {
        for (Location loc : currentEnvoys.keySet()) {
            loc.getLevel().setBlock(loc, Block.get(0), true, false);
        }

        currentEnvoys.clear();
    }

    private boolean rand() {
        return r.nextDouble(10) * rareChance > 9.0;
    }

    private boolean rand(double chance) {
        return r.nextDouble(5) * chance > 2.0;
    }

    private int placeRandomEnvoys() {
        int placed = 0;

        for (Location l : allEnvoys) {
            if (r.nextBoolean()) {

                placeEnvoy(l);
                placed++;
            }
        }

        return placed;
    }

    private void placeEnvoy(Location loc) {
        boolean isSuper = rand();

        currentEnvoys.put(loc, isSuper);

        if (isSuper) {
            loc.getLevel().setBlock(loc, Block.get(subid, subm), true, false);
        } else {
            loc.getLevel().setBlock(loc, Block.get(bid, bm), true, false);
        }

        e.spawnPlacedEffect(loc, isSuper);
    }

    void setEditmodeBlocks() {
        for (Location loc : allEnvoys) {
            loc.getLevel().setBlock(loc, Block.get(Block.BEDROCK), true, false);
        }
    }
}
