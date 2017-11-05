package me.Zolydck.AntCheat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.UUID;


public class AntiCheat extends PluginBase {
    private final String version = "0.0.1";
    private Config config;
    private MainLogger Logger;
    private Server server;
    private HashMap<UUID, Observer> PlayersToKick = new HashMap<>();
    private HashMap<UUID, Observer> PlayerObservers = new HashMap<>();
    private String cl;

    HashMap<UUID, Observer> getPlayerObservers() {
        return PlayerObservers;
    }

    public void setPlayerObservers(HashMap<UUID, Observer> playerObservers) {
        PlayerObservers = playerObservers;
    }

    HashMap<UUID, Observer> getPlayersToKick() {
        return PlayersToKick;
    }

    public void setPlayersToKick(HashMap<UUID, Observer> playersToKick) {
        PlayersToKick = playersToKick;
    }

    Observer getPlayerToKick(UUID hash) {
        return PlayersToKick.get(hash);
    }

    void setPlayerToKick(UUID hash, Observer playerToKick) {
        PlayersToKick.put(hash, playerToKick);
    }

    public void onEnable() {
        this.getServer().getScheduler().scheduleRepeatingTask(new KickTask(this), 1);
        this.saveDefaultConfig();
        cl = this.getConfig().getString("Color");

        config = this.getConfig();
        Logger = this.getServer().getLogger();
        server = this.getServer();

        server.getPluginManager().registerEvents(new EventListener(this), this);
        Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > ZolydckAntiCheat Activated");
        Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > ZolydckAntiCheat v0.0.1");
        Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Loading Modules");
        if (config.getBoolean("ForceOP")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiForceOP"); }
        if (config.getBoolean("NoClip")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiNoClip"); }
        if (config.getBoolean("Fly")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiFly"); }
        if (config.getBoolean("Fly")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiJesus"); }
        if (config.getBoolean("Fly")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiSpider"); }
        if (config.getBoolean("Glide")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiGlide"); }
        if (config.getBoolean("KillAura")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiKillAura"); }
        if (config.getBoolean("Reach")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiReach"); }
        if (config.getBoolean("Speed")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiSpeed"); }
        if (config.getBoolean("Regen")) { Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > Enabling AntiRegen"); }

        if (config.getString("Config-Version").equals(version)) {
            Logger.warning(TextFormat.ESCAPE + cl + "[ZAC] > Your Config is out of date!");
        }

        for (Player player : server.getOnlinePlayers().values()) {
            UUID hash = player.getUniqueId();
            Observer observer = null;
            UUID oldhash = null;

            if (PlayerObservers.containsKey(hash)) {
                Observer obs = PlayerObservers.get(hash);
                oldhash = hash;
                observer = obs;
                observer.setPlayer(player);
            }

            if (oldhash != null) {
                PlayerObservers.remove(oldhash);
                PlayerObservers.put(hash, observer);
                PlayerObservers.get(hash).PlayerRejoin();
            }
            else {
                observer = new Observer(player, this);
                PlayerObservers.put(hash, observer);
                PlayerObservers.get(hash).PlayerRejoin();
            }
        }
    }

    public void onDisable() {
        Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > You are no longer protected from cheats!");
        Logger.info(TextFormat.ESCAPE + cl + "[ZAC] > ShadowAntiCheat Deactivated");
        server.enablePlugin(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.getConfig().getBoolean("ForceOP")) {
            if (sender.isOp()) {
                if (!sender.hasPermission(this.getConfig().getString("ForceOP-Permission"))) {
                    if (sender instanceof Player) {
                        String sname = sender.getName();
                        String message = "[ZAC] > " + sname + " used ForceOP!";
                        this.NotifyAdmins(message);
                        Player p = (Player) sender;
                        p.kick(TextFormat.ESCAPE + cl + "[ZAC] > ForceOP detected!");
                        return true;
                    }
                }
            }
        }
        if (cmd.getName().equals("zac") || cmd.getName().equals("zolydckanticheat")) {
            sender.sendMessage(TextFormat.ESCAPE + cl + "[ZAC] > ZolydckAntiCheat v0.0.1 (mathh40)");
            return true;
        }
        return false;
    }

    private void NotifyAdmins(String message) {
        if (this.getConfig().getBoolean("Verbose")) {
            for (Player player : server.getOnlinePlayers().values()) {
                if (player != null && player.hasPermission("zac.admin")) {
                    player.sendMessage(TextFormat.ESCAPE + cl + message);
                }
            }
        }
    }
}
