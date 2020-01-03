package me.petterim1.envoys;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public class Command extends cn.nukkit.command.Command { //TODO: perms

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
                s.sendMessage(Envoys.prefix + pl.c.translate("command.time.left") + pl.c.getTime());
                break;
            case "start":
                pl.c.doEnvoy();
                break;
            case "about":
                s.sendMessage(Envoys.prefix + "\n\u00A76Version " + pl.getDescription().getVersion() + "\n\u00A76Created by PetteriM1\n\u00A76This plugin can be downloaded for free from nukkitx.com");
                break;
            case "drops":
                s.sendMessage(Envoys.prefix + pl.c.translate("command.drops.locations") + pl.c.getLocations());
                break;
            case "edit":
                if (pl.editmode.contains(s.getName())) {
                    pl.editmode.remove(s.getName());
                    pl.c.quitEditmode();
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.editmode.disabled"));
                } else {
                    pl.editmode.add(s.getName());
                    s.sendMessage(Envoys.prefix + pl.c.translate("command.editmode.enabled"));
                    showEditmodeHelp(s);
                }
                break;
            default:
                showHelp(s);
        }

        return true;
    }

    private static void showHelp(CommandSender s) {
        s.sendMessage(Envoys.prefix);
        s.sendMessage("/envoys time - Show time left to next envoy");
        s.sendMessage("/envoys edit - Enter envoys setup mode");
        s.sendMessage("/envoys drops - Show list of locations envoys may spawn");
        s.sendMessage("/envoys about - Show about this plugin");
        s.sendMessage("/envoys help - Show this page");
    }

    private static void showEditmodeHelp(CommandSender s) {
        s.sendMessage("DEBUG: showEditmodeHelp");
        //TODO
    }
}
