package me.petterim1.envoys;

import cn.nukkit.block.BlockID;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;

public class Events implements Listener {

    private Envoys pl;

    Events(Envoys pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (pl.editmode.contains(e.getPlayer().getName())) {
            if (BlockID.BEDROCK == e.getBlock().getId()) {
                if (pl.c.delEnvoy(e.getBlock().getLocation())) {
                    e.getPlayer().sendMessage(Envoys.prefix + pl.c.translate("envoy.delete"));
                }
            }
        } else if (pl.c.isEnvoyAt(e.getBlock().getLocation())) {
            pl.c.claimEnvoy(e.getPlayer(), e.getBlock().getLocation());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (pl.editmode.contains(e.getPlayer().getName())) {
            if (BlockID.BEDROCK == e.getBlock().getId()) {
                if (pl.c.setEnvoy(e.getBlock().getLocation())) {
                    e.getPlayer().sendMessage(Envoys.prefix + pl.c.translate("envoy.set"));
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (pl.c.isEnvoyAt(e.getBlock().getLocation())) {
            pl.c.claimEnvoy(e.getPlayer(), e.getBlock().getLocation());
        }
    }
}
