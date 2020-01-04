package me.petterim1.envoys;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Location;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.DyeColor;

public class Effects {

    Envoys pl;

    Effects(Envoys pl) {
        this.pl = pl;
    }

    void spawnOpenEffect(boolean e, Location l) {
        if (e) {
            new Firework(pl, (byte) 3, l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l)).spawnToAll();
            new Firework(pl, (byte) 4, l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l)).spawnToAll();
        } else {
            new Firework(pl, (byte) 1, l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l)).spawnToAll();
            new Firework(pl, (byte) 2, l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l)).spawnToAll();
        }

        l.getLevel().addLevelSoundEvent(l, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1, EntityFirework.NETWORK_ID);
    }

    void spawnRandomEffects() {
        for (Location l : pl.c.currentEnvoys.keySet()) {
            EntityFirework f = new EntityFirework(l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l));
            f.setFirework(getItemHint());
            f.spawnToAll();
        }
    }

    void spawnPlacedEffect(Location l, boolean su) {
        new Firework(pl, (byte) 4, l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l)).spawnToAll();

        if (su) {
            new Firework(pl, (byte) 2, l.getLevel().getChunk(l.getFloorX() >> 4, l.getFloorZ() >> 4), getNBT(l)).spawnToAll();
        }
    }

    void spawnHologram(Location l, boolean su) {
        Entity h = new Hologram(l.getChunk(), getNBT(l, su ? pl.c.titleSuper : pl.c.titleBasic));
        h.setNameTag(su ? pl.c.titleSuper : pl.c.titleBasic);
        h.setNameTagAlwaysVisible();
        h.spawnToAll();
    }

    Item getItem1() {
        Item i = new ItemFirework();
        CompoundTag t = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.YELLOW.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        t.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(t);
        return i;
    }

    Item getItem2() {
        Item i = new ItemFirework();
        CompoundTag t = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.LIGHT_BLUE.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        t.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(t);
        return i;
    }

    Item getItem3() {
        Item i = new ItemFirework();
        CompoundTag t = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.RED.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        t.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(t);
        return i;
    }

    Item getItem4() {
        Item i = new ItemFirework();
        CompoundTag t = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.GREEN.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        t.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(t);
        return i;
    }

    Item getItemHint() {
        Item i = new ItemFirework();
        CompoundTag t = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.RED.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        t.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 2)
        );
        i.setNamedTag(t);
        return i;
    }

    private static CompoundTag getNBT(Location l) {
        return new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", l.x + 0.5))
                        .add(new DoubleTag("", l.y + 0.5))
                        .add(new DoubleTag("", l.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)));
    }

    private static CompoundTag getNBT(Location l, String n) {
        return new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", l.x + 0.5))
                        .add(new DoubleTag("", l.y + 0.5))
                        .add(new DoubleTag("", l.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putBoolean("Invulnerable", true)
                .putString("NameTag", n)
                .putFloat("Scale", 0.001f);
    }
}
