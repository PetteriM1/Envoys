package me.petterim1.envoys;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class Hologram extends Entity {

    @Override
    public int getNetworkId() {
        return 61;
    }

    public Hologram(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void spawnTo(Player p) {
        if (closed) return;
        if (!Configuration.now) {
            close();
        } else {
            super.spawnTo(p);
        }
    }

    @Override
    public void kill() {
        if (!Configuration.now) {
            super.close();
        }
    }

    @Override
    public void close() {
        if (!Configuration.now) {
            super.close();
        }
    }
}
