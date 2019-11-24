package me.petterim1.envoys;

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

    void spawnOpenEffect(boolean e, Location loc) {
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", loc.x + 0.5))
                        .add(new DoubleTag("", loc.y + 0.5))
                        .add(new DoubleTag("", loc.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)));

        if (e) {
            new Firework(pl, (byte) 3, loc.getLevel().getChunk(loc.getFloorX() >> 4, loc.getFloorZ() >> 4), nbt).spawnToAll();
            new Firework(pl, (byte) 4, loc.getLevel().getChunk(loc.getFloorX() >> 4, loc.getFloorZ() >> 4), nbt).spawnToAll();
        } else {
            new Firework(pl, (byte) 1, loc.getLevel().getChunk(loc.getFloorX() >> 4, loc.getFloorZ() >> 4), nbt).spawnToAll();
            new Firework(pl, (byte) 2, loc.getLevel().getChunk(loc.getFloorX() >> 4, loc.getFloorZ() >> 4), nbt).spawnToAll();
        }

        loc.getLevel().addLevelSoundEvent(loc, LevelSoundEventPacket.SOUND_LARGE_BLAST, -1, EntityFirework.NETWORK_ID);
    }

    void spawnRandomEffects() {
        //for (Location loc : pl.c.locations.keys()) {
    }

    Item getItem1() {
        Item i = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.YELLOW.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(tag);
        return i;
    }

    Item getItem2() {
        Item i = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.LIGHT_BLUE.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(tag);
        return i;
    }

    Item getItem3() {
        Item i = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.RED.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(tag);
        return i;
    }

    Item getItem4() {
        Item i = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        CompoundTag ex = new CompoundTag()
                .putByteArray("FireworkColor", new byte[]{(byte) DyeColor.GREEN.getDyeData()})
                .putByteArray("FireworkFade", new byte[]{})
                .putBoolean("FireworkFlicker", false)
                .putBoolean("FireworkTrail", false)
                .putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.SMALL_BALL.ordinal());
        tag.putCompound("Fireworks", new CompoundTag("Fireworks")
                .putList(new ListTag<CompoundTag>("Explosions").add(ex))
                .putByte("Flight", 1)
        );
        i.setNamedTag(tag);
        return i;
    }
}
