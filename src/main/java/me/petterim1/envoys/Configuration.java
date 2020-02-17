package me.petterim1.envoys;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.utils.Config;

import java.util.*;

public class Configuration {

    private Envoys pl;
    Effects e;
    private Config c;
    private Config d;
    private static final int c_ver = 2;
    private static final int c_ver_i = 1;
    boolean loaded;
    static boolean now;
    int nextEnvoy;
    int nowTicks;
    int hint;
    int hintTicks;
    String titleBasic;
    String titleSuper;
    private int bid;
    private int bm;
    private int subid;
    private int subm;
    private int minimumLoot;
    private int minimumEffects;
    private int minimumEffectsSuper;
    private double rareChance;
    private String startSound;
    private String stopSound;
    List<String> editMode = new ArrayList<>();
    private List<Location> allEnvoys;
    private List<ItemSlot> items = new ArrayList<>();
    private List<EffectSlot> effects = new ArrayList<>();
    protected Map<Location, Boolean> currentEnvoys = new HashMap<>();
    private Map<Location, Long> hologramIDs = new HashMap<>();
    private static final SplittableRandom r = new SplittableRandom();

    Configuration(Envoys pl, Config c) {
        this.pl = pl;
        this.c = c;
        this.e = new Effects(pl);
    }

    boolean init() {
        checkLicense();

        int v = c.getInt("configVersion");
        if (c_ver != v) {
            if (!tryUpdate(v)) {
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

    private boolean tryUpdate(int v) {
        if (v == 1) {
            c.set("minimumEffects", 1);
            c.set("envoy.event.end.onefound", "§7An envoy have been found! Envoys left: ");
            c.set("configVersion ", c_ver);
            c.save();
            return true;
        }

        return false;
    }

    private void loadSettings() {
        hint = c.getInt("hint", -1);
        startSound = c.getString("startSound");
        stopSound = c.getString("endSound");
        rareChance = c.getDouble("rareChance", 1.0);
        minimumLoot = c.getInt("minimumLoot", 3);
        minimumEffects = c.getInt("minimumEffects", 1);
        minimumEffectsSuper = c.getInt("minimumEffects", 1);
        bid = c.getInt("envoyBlockId", 54);
        bm = c.getInt("envoyBlockMeta");
        subid = c.getInt("rareEnvoyBlockId", 130);
        subm = c.getInt("rareEnvoyBlockMeta");
        titleBasic = c.getString("title.basic", "§7Basic Envoy");
        titleSuper = c.getString("title.super", "§7Super Envoy");
    }

    boolean setEnvoy(Location l) {
        if (allEnvoys.contains(l)) {
            return false;
        }

        allEnvoys.add(l);
        return true;
    }

    boolean delEnvoy(Location l) {
        return allEnvoys.remove(l);
    }

    String translate(String s) {
        return !c.getString(s).isEmpty() ? c.getString(s) : s;
    }

    private boolean loadItems() {
        Config i = new Config(pl.getDataFolder() + "/items.yml", Config.YAML);

        if (c_ver_i != i.getInt("version")) {
            pl.getLogger().error("Invalid item config. Plugin will be disabled.");
            pl.getServer().getPluginManager().disablePlugin(pl);
            return false;
        }

        try {
            for (String s : i.getStringList("itemsNormal")) {
                String[] info = s.split(":");
                String name;
                int enc;
                int lvl;
                if (info.length > 4 && info[4] != null) {
                    if (info.length > 6 && info[6] != null) {
                        name = info[4];
                        enc = Integer.parseInt(info[5]);
                        lvl = Integer.parseInt(info[6]);
                    } else if (info.length > 5 && info[5] != null) {
                        name = "";
                        enc = Integer.parseInt(info[4]);
                        lvl = Integer.parseInt(info[5]);
                    } else {
                        name = info[4];
                        enc = -1;
                        lvl = -1;
                    }
                } else {
                    name = "";
                    enc = -1;
                    lvl = -1;
                }
                items.add(new ItemSlot(Double.parseDouble(info[3]), false, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), name, enc, lvl));
            }
            for (String s : i.getStringList("itemsSuper")) {
                String[] info = s.split(":");
                String name;
                int enc;
                int lvl;
                if (info.length > 4 && info[4] != null) {
                    if (info.length > 6 && info[6] != null) {
                        name = info[4];
                        enc = Integer.parseInt(info[5]);
                        lvl = Integer.parseInt(info[6]);
                    } else if (info.length > 5 && info[5] != null) {
                        name = "";
                        enc = Integer.parseInt(info[4]);
                        lvl = Integer.parseInt(info[5]);
                    } else {
                        name = info[4];
                        enc = -1;
                        lvl = -1;
                    }
                } else {
                    name = "";
                    enc = -1;
                    lvl = -1;
                }
                items.add(new ItemSlot(Double.parseDouble(info[3]), true, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2]), name, enc, lvl));
            }
            int normalSize = 0;
            for (String s : i.getStringList("effectsNormal")) {
                String[] info = s.split(":");
                effects.add(new EffectSlot(Double.parseDouble(info[3]), false, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2])));
                normalSize++;
            }
            if (normalSize == 0) {
                minimumEffectsSuper = 0;
            }
            int superSize = 0;
            for (String s : i.getStringList("effectsSuper")) {
                String[] info = s.split(":");
                effects.add(new EffectSlot(Double.parseDouble(info[3]), true, Integer.parseInt(info[0]), Integer.parseInt(info[1]), Integer.parseInt(info[2])));
                superSize++;
            }
            if (superSize == 0) {
                minimumEffectsSuper = 0;
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

            List<String> rawData = d.getStringList("envoys");

            if (rawData != null) {
                allEnvoys = getSaveData(rawData);
                if (allEnvoys.size() > 0) {
                    pl.getServer().getLogger().info(Envoys.prefix + "\u00A77Saved data loaded successfully");
                }
            } else {
                pl.getServer().getLogger().info(Envoys.prefix + "\u00A77No saved data found");
            }
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
            d.set("envoys", getRawSaveData(allEnvoys));
            d.save();
        }
    }

    String getLocations() {
        StringBuilder str = new StringBuilder();
        for (Location l : allEnvoys) {
            str.append(l.x).append(", ").append(l.y).append(", ").append(l.z).append(", ").append(l.level.getName()).append('\n');
        }
        return str.toString();
    }

    void quitEditmode() {
        for (Location l : allEnvoys) {
            l.getLevel().setBlock(l, Block.get(0), true, false);
        }
    }

    boolean isEnvoyAt(Location l) {
        if (!now) {
            return false;
        }

        return currentEnvoys.containsKey(l);
    }

    void claimEnvoy(Player p, Location l) {
        l.getLevel().setBlock(l, Block.get(0), true, false);
        removeHolograms(l, false);
        boolean su = currentEnvoys.get(l);
        currentEnvoys.remove(l);
        e.spawnOpenEffect(su, l);
        giveItems(p, su);
        giveEffects(p, su);
        broadcast("found", currentEnvoys.size());
        checkLastEnvoy();
    }

    private void checkLastEnvoy() {
        if (currentEnvoys.size() <= 0) {
            endEnvoy(true);
        }
    }

    private void removeHolograms(Location l, boolean a) {
        if (a) {
            for (long v : hologramIDs.values()) {
                Entity e = l.getLevel().getEntity(v);
                if (e instanceof Hologram) {
                    e.close();
                }
            }
        } else {
            Entity e = l.getLevel().getEntity(hologramIDs.get(l));
            if (e instanceof Hologram) {
                e.close();
            }
        }
    }

    private void giveItems(Player p, boolean su) {
        StringBuilder n = new StringBuilder();
        int itemsGiven = 0;
        while (itemsGiven < minimumLoot) {
            for (ItemSlot slot : items) {
                if ((su && !slot.su) || (!su && slot.su)) {
                    if (rand(slot.chance)) {
                        n.append('[').append(slot.item.getName()).append(']');
                        p.getInventory().addItem(slot.item);
                        itemsGiven++;
                    }
                }
            }
        }
        p.sendMessage(Envoys.prefix + translate("items.given") + n);
    }

    private void giveEffects(Player p, boolean su) {
        StringBuilder n = new StringBuilder();
        int given = 0;
        while (given < (su ? minimumEffectsSuper : minimumEffects)) {
            for (EffectSlot slot : effects) {
                if ((su && !slot.su) || (!su && slot.su)) {
                    if (rand(slot.chance)) {
                        n.append('[').append(slot.effect.getName()).append(']');
                        slot.effect.add(p);
                        given++;
                    }
                }
            }
        }
        p.sendMessage(Envoys.prefix + translate("effects.given") + n);
    }

    void doEnvoy() {
        nextEnvoy = -1;
        nowTicks = c.getInt("duration");
        hintTicks = hint;
        now = true;
        broadcast("start", placeRandomEnvoys());
    }

    void endEnvoy(boolean af) {
        resetTimer();
        now = false;
        removeEnvoys();
        broadcast("end", af ? 1 : 0);
    }

    private void broadcast(String m, int d) {
        switch (m) {
            case "start":
                pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.start") + d);
                playSound(startSound);
                break;
            case "end":
                if (d == 1) {
                    pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.end.allfound"));
                } else  {
                    pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.end"));
                }
                playSound(stopSound);
                break;
            case "found":
                if (d > 0) {
                    pl.getServer().broadcastMessage(Envoys.prefix + translate("envoy.event.onefound") + d);
                }
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
        if (pl.getDescription().getAuthors().size() != 1 || !pl.getDescription().getAuthors().get(0).equals("PetteriM1") || !pl.getDescription().getVersion().startsWith("1") || !pl.getDescription().getMain().equals("me.petterim1.envoys.Envoys") || !pl.getDescription().getName().equals("Envoys") || !pl.getDescription().getDescription().equals("Envoys plugin for Nukkit")) {
            System.exit(1);
        }
    }

    void removeEnvoys() {
        for (Location l : currentEnvoys.keySet()) {
            l.getLevel().setBlock(l, Block.get(0), true, false);
            removeHolograms(l, true);
        }

        currentEnvoys.clear();
        hologramIDs.clear();
    }

    private boolean rand() {
        return r.nextDouble(10) * rareChance > 8.8;
    }

    private boolean rand(double c) {
        return r.nextDouble(5) * c > 2.0;
    }

    private int placeRandomEnvoys() {
        int c = 0;

        for (Location l : allEnvoys) {
            if (r.nextBoolean()) {
                placeEnvoy(l);
                c++;
            }
        }

        return c;
    }

    private void placeEnvoy(Location l) {
        boolean su = rand();

        currentEnvoys.put(l, su);

        if (su) {
            l.getLevel().setBlock(l, Block.get(subid, subm), true, false);
        } else {
            l.getLevel().setBlock(l, Block.get(bid, bm), true, false);
        }

        hologramIDs.put(l, e.spawnHologram(l, su));
        e.spawnPlacedEffect(l, su);
    }

    void setEditModeBlocks() {
        for (Location l : allEnvoys) {
            l.getLevel().setBlock(l, Block.get(Block.BEDROCK), true, false);
        }
    }

    private List<String> getRawSaveData(List<Location> data) {
        if (data == null) {
            return null;
        }

        List<String> result = new ArrayList<>();

        for (Location l : data) {
            result.add(l.level.getName() + ':' + (int) l.x + ':' + (int) l.y + ':' + (int) l.z);
        }

        return result;
    }

    private List<Location> getSaveData(List<String> data) {
        if (data == null) {
            return null;
        }

        List<Location> result = new ArrayList<>();

        for (String s : data) {
            String[] raw = s.split(":");
            result.add(new Location(Double.parseDouble(raw[1]), Double.parseDouble(raw[2]), Double.parseDouble(raw[3]), pl.getServer().getLevelByName(raw[0])));
        }

        return result;
    }
}
