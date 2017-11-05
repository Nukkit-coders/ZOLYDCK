package me.Zolydck.AntCheat;

import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.TextFormat;

public class KickTask extends PluginTask {

    private final AntiCheat plugin;

    public KickTask(AntiCheat plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void onRun(int i) {
        int cl = this.plugin.getConfig().getInt("Color");
        for (Observer obs : this.plugin.getPlayersToKick().values()) {
            obs.setPlayerBanCounter(obs.getPlayerBanCounter() + 1);
            if (obs.getPlayerBanCounter() > 0 && obs.getPlayerBanCounter() == this.plugin.getConfig().getInt(
                    "Max-Hacking-Times")) {
                for (String command : this.plugin.getConfig().getStringList("MaxHackingExceededCommands")) {
                    String send = obs.ScanMessage(command);
                    this.plugin.getServer().dispatchCommand(new ConsoleCommandSender(), send);
                    if (this.plugin.getConfig().getBoolean("BanPlayerMessageBool")) {
                        String bmsg = this.plugin.getConfig().getString("BanPlayerMessage");
                        String sbmsg = obs.ScanMessage(bmsg);
                        this.plugin.getServer().broadcastMessage(TextFormat.ESCAPE + "$cl" + sbmsg);
                    }
                }
                obs.setPlayerBanCounter(0);
            }
            if (obs.getPlayer() != null && obs.getPlayer().isOnline()) {
                obs.getPlayer().kick(TextFormat.ESCAPE + "$cl" + obs.getKickMessage());
                if (this.plugin.getConfig().getBoolean("KickPlayerMessageBool")) {
                    String msg = this.plugin.getConfig().getString("KickPlayerMessage");
                    String smsg = obs.ScanMessage(msg);
                    this.plugin.getServer().broadcastMessage(TextFormat.ESCAPE + "$cl" + smsg);
                }
            }
            this.plugin.getPlayersToKick().remove(obs.getClientID());
        }
    }
}
