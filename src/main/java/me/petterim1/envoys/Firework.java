package me.petterim1.envoys;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.data.NBTEntityData;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;

public class Firework extends EntityFirework {

    Envoys pl;

    public Firework(Envoys pl, byte m, FullChunk c, CompoundTag t) {
        super(c, t);
        this.pl = pl;
        switch (m) {
            case 1:
                setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, pl.c.e.getItem1().getNamedTag()));
                break;
            case 2:
                setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, pl.c.e.getItem2().getNamedTag()));
                break;
            case 3:
                setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, pl.c.e.getItem3().getNamedTag()));
                break;
            case 4:
                setDataProperty(new NBTEntityData(Entity.DATA_DISPLAY_ITEM, pl.c.e.getItem4().getNamedTag()));
                break;
        }

        this.setDataProperty(new IntEntityData(Entity.DATA_DISPLAY_OFFSET, 1));
        this.setDataProperty(new ByteEntityData(Entity.DATA_HAS_DISPLAY, 1));
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (closed) {
            return false;
        }

        EntityEventPacket pk = new EntityEventPacket();
        pk.data = 0;
        pk.event = EntityEventPacket.FIREWORK_EXPLOSION;
        pk.eid = getId();
        Server.broadcastPacket(getViewers().values(), pk);
        close();

        return true;
    }
}