package me.petterim1.envoys;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public class Command extends cn.nukkit.command.Command {

    private Envoys pl;

    public Command(Envoys pl) {
        super("envoys");
        this.pl = pl;
        setPermission("envoys.command.main");
        setUsage("/envoys");
        setDescription("Envoys");
    }

    @Override
    public boolean execute(CommandSender s, String c, String[] a) {
        if (!s.hasPermission(getPermission())) return false;
        if (!(s instanceof Player)) {
            s.sendMessage(pl.c.translate("command.ingame"));
            return true;
        }

        if (0 == a.length) {
            showHelp(s);
            return true;
        }

        switch (a[0].toLowerCase()) {
            case "time":
                if (!s.hasPermission("envoys.command.time")) {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.noperm") + "envoys.command.time");
                } else {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.time.left") + pl.c.getTime());
                }
                break;
            case "start":
                if (!s.hasPermission("envoys.command.start")) {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.noperm") + "envoys.command.start");
                } else {
                    if (!Configuration.now) {
                        pl.c.doEnvoy();
                    } else {
                        s.sendMessage(Envoys.prefix + pl.c.translate("command.start.alreadyrunning"));
                    }
                }
                break;
            case "stop":
                if (!s.hasPermission("envoys.command.stop")) {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.noperm") + "envoys.command.stop");
                } else {
                    if (Configuration.now) {
                        pl.c.endEnvoy(false);
                    } else {
                        s.sendMessage(Envoys.prefix + pl.c.translate("command.generic.notrunning"));
                    }
                }
                break;
            case "about":
                s.sendMessage(Envoys.prefix + "\n§6Version " + pl.getDescription().getVersion() + "\n§6Created by PetteriM1\n§6This plugin can be downloaded for free from nukkitx.com");
                break;
            case "drops":
                if (!s.hasPermission("envoys.command.drops")) {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.noperm") + "envoys.command.drops");
                } else {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.drops.locations") + '\n' + pl.c.getLocations());
                }
                break;
            case "edit":
                if (!s.hasPermission("envoys.command.edit")) {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.noperm") + "envoys.command.edit");
                } else {
                    if (pl.c.editMode.contains(s.getName())) {
                        pl.c.editMode.remove(s.getName());
                        pl.c.quitEditmode();
                        s.sendMessage(Envoys.prefix + pl.c.translate("command.editmode.disabled"));
                    } else {
                        pl.c.editMode.add(s.getName());
                        pl.c.setEditModeBlocks();
                        s.sendMessage(Envoys.prefix + pl.c.translate("command.editmode.enabled"));
                        showEditModeHelp(s);
                    }
                }
                break;
            case "left":
                if (!s.hasPermission("envoys.command.left")) {
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.noperm") + "envoys.command.left");
                } else {
                    if (Configuration.now) {
                        s.sendMessage(Envoys.prefix + pl.c.translate("command.left.locations") + '\n' + pl.c.getCurrentLocations());
                    } else {
                        s.sendMessage(Envoys.prefix + pl.c.translate("command.generic.notrunning"));
                    }
                }
                break;
            default:
                showHelp(s);
        }

        return true;
    }

    private static void showHelp(CommandSender s) {
        s.sendMessage(Envoys.prefix);
        s.sendMessage("/envoys time - Show time left to next envoys event");
        s.sendMessage("/envoys edit - Enter envoys edit mode");
        s.sendMessage("/envoys drops - Show list of locations envoys may spawn");
        s.sendMessage("/envoys left - Show list of locations envoys are spawned at");
        s.sendMessage("/envoys start - Start envoys event instantly");
        s.sendMessage("/envoys stop - Stop envoys event instantly");
        s.sendMessage("/envoys about - Show about this plugin");
        s.sendMessage("/envoys help - Show this page");
    }

    private static void showEditModeHelp(CommandSender s) {
        s.sendMessage("§7In edit mode you can edit the locations where envoys can appear. The changes will take effect in the next envoy event.");
        s.sendMessage("§5> §7To set envoy location place a bedrock block.");
        s.sendMessage("§5> §7To delete envoy location break the bedrock block.");
        s.sendMessage("§5> §7To quit the edit mode run /envoys edit command again.");
        s.sendMessage("§7Rare envoys will appear randomly at these locations. You can edit rare envoy chance in the config.");
    }
}
