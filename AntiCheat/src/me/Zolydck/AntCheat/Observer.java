package me.Zolydck.AntCheat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerGameModeChangeEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Observer {
    private int Colorized;
    private double dist_thr3;
    private double dist_thr2;
    private double dist_thr1;
    private int LastMoveTick;
    private int LastDamageTick;
    private Set<Integer> surroundings;
    private int heal_time;
    private int heal_counter;
    private double y_pos_new;
    private double y_pos_old;
    private Vector3 x_pos_new;
    private Vector3 x_pos_old;
    private double hs_hit_time;
    private Float hs_time_sum;
    private ArrayList<Float> hs_time_array;
    private int hs_arr_idx;
    private int hs_arr_size;
    private double y_speed;
    private double y_dist_sum;
    private double y_distance;
    private double y_time_sum;
    private ArrayList<Double> y_dist_array;
    private ArrayList<Float> y_time_array;
    private int y_arr_idx;
    private int y_arr_size;
    private double x_speed;
    private double x_dist_sum;
    private double x_distance;
    private double x_time_sum;
    private ArrayList<Double> x_dist_array;
    private ArrayList<Float> x_time_array;
    private int x_arr_idx;
    private int x_arr_size;
    private int prev_health_tick;
    private int prev_tick;
    private int PlayerBanCounter;
    private int SpeedAMP;
    private int PlayerKillAuraV2Counter;
    private int PlayerKillAuraCounter;
    private int PlayerHitCounter;
    private int PlayerHitFirstTick;
    private int PlayerReachFirstTick;
    private int PlayerReachCounter;
    private int PlayerNoClipCounter;
    private int PlayerGlideCounter;
    private int PlayerSpeedCounter;
    private int PlayerAirCounter;
    private String KickMessage;
    private int JoinCounter;
    private Server server;
    private MainLogger Logger;
    private UUID ClientID;
    private String PlayerName;
    private Player player;
    private AntiCheat ZAC;

    String getKickMessage() {
        return KickMessage;
    }

    UUID getClientID() {
        return ClientID;
    }

    int getPlayerBanCounter() {
        return PlayerBanCounter;
    }

    void setPlayerBanCounter(int playerBanCounter) {
        PlayerBanCounter = playerBanCounter;
    }

    Player getPlayer() {
        return player;
    }

    void setPlayer(Player p) {
        this.player = p;
    }

    Observer(Player player, AntiCheat ZAC) {
        this.player = player;
        this.PlayerName = this.player.getName();
        this.ZAC = ZAC;
        this.ClientID = player.getUniqueId();
        this.Logger = ZAC.getServer().getLogger();
        this.server = ZAC.getServer();
        this.JoinCounter = 0;
        this.KickMessage = "";

        this.PlayerAirCounter = 0;
        this.PlayerSpeedCounter = 0;
        this.PlayerGlideCounter = 0;
        this.PlayerNoClipCounter = 0;
        this.PlayerReachCounter = 0;
        this.PlayerReachFirstTick = -1;
        this.PlayerHitFirstTick = -1;
        this.PlayerHitCounter = 0;
        this.PlayerKillAuraCounter = 0;
        this.PlayerKillAuraV2Counter = 0;
        this.SpeedAMP = 0;

        //DO NOT RESET!
        this.PlayerBanCounter = 0;
        //^^^^^^^^^^^^^

        this.prev_tick = -1;
        this.prev_health_tick = -1;

        this.x_arr_size = 7;
        this.x_arr_idx = 0;
        this.x_time_array = new ArrayList<>(x_arr_size);
        this.x_dist_array = new ArrayList<>(x_arr_size);
        this.x_time_sum = 0.0;
        this.x_distance = 0.0;
        this.x_dist_sum = 0.0;
        this.x_speed = 0.0;

        this.y_arr_size = 10;
        this.y_arr_idx = 0;
        this.y_time_array = new ArrayList<>(y_arr_size);
        this.y_dist_array = new ArrayList<>(y_arr_size);
        this.y_time_sum = 0.0;
        this.y_distance = 0.0;
        this.y_dist_sum = 0.0;
        this.y_speed = 0.0;

        this.hs_arr_size = 5;
        this.hs_arr_idx = 0;
        this.hs_time_array = new ArrayList<>(hs_arr_size);
        this.hs_time_sum = 0.5f * this.hs_arr_size;
        this.hs_hit_time = 0.5;

        this.x_pos_old = new Vector3(0.0, 0.0, 0.0);
        this.x_pos_new = new Vector3(0.0, 0.0, 0.0);
        this.y_pos_old = 0.0;
        this.y_pos_new = 0.0;

        this.heal_counter = 0;
        this.heal_time = 0;

        this.surroundings = new HashSet<>();

        this.LastDamageTick = 0;
        this.LastMoveTick = 0;
        this.Colorized = this.ZAC.getConfig().getInt("Color");

        if (this.ZAC.getConfig().getInt("AKAHAD") == 1) {
            this.dist_thr1 = 4.00;
            this.dist_thr2 = 3.75;
        }
        else if (this.ZAC.getConfig().getInt("AKAHAD") == 2) {
            this.dist_thr1 = 3.75;
            this.dist_thr2 = 3.50;
        }
        else if (this.ZAC.getConfig().getInt("AKAHAD") == 3) {
            this.dist_thr1 = 3.50;
            this.dist_thr2 = 3.25;
        }
        else {
            this.dist_thr1 = 0.00;
            this.dist_thr2 = 0.00;
        }
        if (this.ZAC.getConfig().getInt("AKAHAD-MacroCapture") == 1) {
            this.dist_thr3 = 4.125;
        }
        else if (this.ZAC.getConfig().getInt("AKAHAD-MacroCapture") == 2) {
            this.dist_thr3 = 3.825;
        }
        else {
            this.dist_thr3 = 0.000;
        }
    }

    private void ResetObserver() {
        this.PlayerReachCounter = 0;
        this.PlayerReachFirstTick = -1;
        this.PlayerHitFirstTick = -1;
        this.PlayerHitCounter = 0;
        this.PlayerKillAuraCounter = 0;
        this.PlayerKillAuraV2Counter = 0;

        this.ResetMovement();
    }


    private void ResetMovement() {
        this.PlayerAirCounter = 0;
        this.PlayerSpeedCounter = 0;
        this.PlayerGlideCounter = 0;
        this.PlayerNoClipCounter = 0;
        this.LastMoveTick = 0;

        this.prev_tick = -1;

        this.x_arr_size = 7;
        this.x_arr_idx = 0;
        this.x_time_array = new ArrayList<>(this.x_arr_size);
        this.x_dist_array = new ArrayList<>(this.x_arr_size);
        this.x_time_sum = 0.0;
        this.x_distance = 0.0;
        this.x_dist_sum = 0.0;
        this.x_speed = 0.0;

        this.y_arr_size = 10;
        this.y_arr_idx = 0;
        this.y_time_array = new ArrayList<>(this.y_arr_size);
        this.y_dist_array = new ArrayList<>(this.y_arr_size);
        this.y_time_sum = 0.0;
        this.y_distance = 0.0;
        this.y_dist_sum = 0.0;
        this.y_speed = 0.0;

        this.x_pos_old = new Vector3(0.0, 0.0, 0.0);
        this.x_pos_new = new Vector3(0.0, 0.0, 0.0);
        this.y_pos_old = 0.0;
        this.y_pos_new = 0.0;

        this.hs_arr_size = 5;
        this.hs_arr_idx = 0;
        this.hs_time_array = new ArrayList<>(this.hs_arr_size);
        this.hs_time_sum = 0.5f * this.hs_arr_size;
        this.hs_hit_time = 0.5;
    }

    private Boolean ZACIsOnGround(Player p) {
        return !this.AllBlocksAir() && p.isOnGround();
    }

    String ScanMessage(String message) {
        return message.replaceAll("%PLAYER%", this.PlayerName);
    }

    private void KickPlayer(String reason) {
        if (this.ZAC.getPlayerToKick(this.ClientID) != null) {
            // Add current Observer to the array of Observers whose players shall be kicked ASAP
            this.KickMessage = reason;
            this.ZAC.setPlayerToKick(this.ClientID, this);
        }
    }

    private void NotifyAdmins(String message) {
        if (this.ZAC.getConfig().getBoolean("Verbose")) {
            String newmsg = this.ScanMessage(message);

            for (Observer obser : this.ZAC.getPlayerObservers().values()) {
                Player player = obser.player;
                if (player != null && this.player.hasPermission("zac.admin")) {
                    player.sendMessage(TextFormat.ESCAPE + "this.Colorized" + newmsg);
                }
            }
        }
    }

    void PlayerQuit() {
        if (this.ZAC.getConfig().getBoolean("I-AM-WATCHING-YOU")) {
            this.Logger.debug(TextFormat.ESCAPE + "this.Colorized" + "[ZAC] > this.PlayerName is no longer watched...");
        }
    }

    public void PlayerJoin() {
        this.JoinCounter++;
        if (this.ZAC.getConfig().getBoolean("I-AM-WATCHING-YOU")) {
            this.player.sendMessage(
                    TextFormat.ESCAPE + "this.Colorized" + "[ZAC] > this.PlayerName, I am watching you ...");
        }
    }

    void PlayerRejoin() {
        this.JoinCounter++;
        if (this.ZAC.getConfig().getBoolean("I-AM-WATCHING-YOU")) {
            this.player.sendMessage(
                    TextFormat.ESCAPE + "this.Colorized" + "[ZAC] > this.PlayerName, I am still watching you ...");
            this.Logger.debug(
                    TextFormat.ESCAPE + "this.Colorized" + "[ZAC] > this.PlayerName joined this server this.JoinCounter times since server start");
        }
    }

    private boolean AllBlocksAir() {
        Level level = this.player.getLevel();
        int posX = (int) this.player.getX();
        int posY = (int) this.player.getY();
        int posZ = (int) this.player.getZ();

        for (int xidx = posX - 1; xidx <= posX + 1; xidx = xidx + 1) {
            for (int zidx = posZ - 1; zidx <= posZ + 1; zidx = zidx + 1) {
                for (int yidx = posY - 1; yidx <= posY; yidx = yidx + 1) {
                    Vector3 pos = new Vector3(xidx, yidx, zidx);
                    int block = level.getBlock(pos).getId();
                    if (block != Block.AIR) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    void OnBlockPlaceEvent() {
        this.PlayerAirCounter--;
    }


    void PlayerRegainHealth(EntityRegainHealthEvent event) {
        if (this.ZAC.getConfig().getBoolean("Regen")) {
            if (this.player.hasPermission("zac.regen")) { return; }
            int Reason2 = event.getRegainReason();
            int tick = this.server.getTick();
            float tps = this.server.getTicksPerSecond();

            if (Reason2 != 2)  // Ignore CAUSE_MAGIC
            {
                float heal_amount = event.getAmount();
                if (heal_amount > 3) {
                    if (this.ZAC.getConfig().getString("Regen-Punishment").equals("kick")) {
                        event.setCancelled(true);
                        this.ResetObserver();
                        String message = this.ZAC.getConfig().getString("Regen-LogMessage");
                        String reason = this.ZAC.getConfig().getString("Regen-Message");
                        this.NotifyAdmins(message);
                        this.KickPlayer(reason);
                        return;
                    }
                    if (this.ZAC.getConfig().getString("Regen-Punishment").equals("block")) {
                        event.setCancelled(true);
                        String message = this.ZAC.getConfig().getString("Regen-LogMessage");
                        this.NotifyAdmins(message);
                    }
                }
                tick = this.server.getTick();
                tps = this.server.getTicksPerSecond();
                if (tps > 0.0 && this.prev_health_tick != -1.0) {
                    int tick_count = tick - this.prev_health_tick;  // server ticks since last health regain
                    float delta_t = tick_count / tps;       // seconds since last health regain
                    if (delta_t < 10) {
                        this.heal_counter = this.heal_counter + (int) heal_amount;
                        this.heal_time = this.heal_time + (int) delta_t;
                        if (this.heal_counter >= 5) {
                            double heal_rate = (double) this.heal_counter / (double) this.heal_time;
                            if (heal_rate > 0.5) {
                                if (this.ZAC.getConfig().getString("Regen-Punishment").equals("kick")) {
                                    event.setCancelled(true);
                                    this.ResetObserver();
                                    String message = this.ZAC.getConfig().getString("Regen-LogMessage");
                                    String reason = this.ZAC.getConfig().getString("Regen-Message");
                                    this.NotifyAdmins(message);
                                    this.KickPlayer(reason);
                                    return;
                                }
                                if (this.ZAC.getConfig().getString("Regen-Punishment").equals("block")) {
                                    event.setCancelled(true);
                                    String message = this.ZAC.getConfig().getString("Regen-LogMessage");
                                    this.NotifyAdmins(message);
                                }
                            }
                            this.heal_counter = 0;
                            this.heal_time = 0;
                        }
                    }
                }
                this.prev_health_tick = tick;
            }
        }
    }

    // -------------------------------------------------------------------------------------
    // OnMove: Player has made a move
    // -------------------------------------------------------------------------------------
    void OnMove(PlayerMoveEvent event) {
        this.LastMoveTick = this.server.getTick();
        this.CheckForceOP(event);
        if (this.player.getGamemode() == 1 || this.player.getGamemode() == 3) { return; }

        this.GetSurroundingBlocks();
        this.CheckSpeedFlyGlide(event);
        this.CheckNoClip(event);
    }

    // -------------------------------------------------------------------------------------
    // CheckForceOP: Check if Player player is a legit OP
    // -------------------------------------------------------------------------------------
    private void CheckForceOP(Event event) {
        if (this.ZAC.getConfig().getBoolean("ForceOP")) {
            if (this.player.isOp()) {
                if (!this.player.hasPermission(this.ZAC.getConfig().getString("ForceOP-Permission"))) {
                    event.setCancelled(true);
                    String message = "[ZAC] > %PLAYER% used ForceOP!";
                    String reason = "[ZAC] > ForceOP detected!";
                    this.NotifyAdmins(message);
                    this.KickPlayer(reason);
                }
            }
        }
    }

    private void GetSurroundingBlocks() {
        Level level = this.player.getLevel();

        double posX = this.player.getX();
        double posY = this.player.getY();
        double posZ = this.player.getZ();

        Vector3 pos1 = new Vector3(posX, posY, posZ);
        Vector3 pos2 = new Vector3(posX - 1, posY, posZ);
        Vector3 pos3 = new Vector3(posX - 1, posY, posZ - 1);
        Vector3 pos4 = new Vector3(posX, posY, posZ - 1);
        Vector3 pos5 = new Vector3(posX + 1, posY, posZ);
        Vector3 pos6 = new Vector3(posX + 1, posY, posZ + 1);
        Vector3 pos7 = new Vector3(posX, posY, posZ + 1);
        Vector3 pos8 = new Vector3(posX + 1, posY, posZ - 1);
        Vector3 pos9 = new Vector3(posX - 1, posY, posZ + 1);

        this.surroundings.add(level.getBlock(pos1).getId());
        this.surroundings.add(level.getBlock(pos2).getId());
        this.surroundings.add(level.getBlock(pos3).getId());
        this.surroundings.add(level.getBlock(pos4).getId());
        this.surroundings.add(level.getBlock(pos5).getId());
        this.surroundings.add(level.getBlock(pos6).getId());
        this.surroundings.add(level.getBlock(pos7).getId());
        this.surroundings.add(level.getBlock(pos8).getId());
        this.surroundings.add(level.getBlock(pos9).getId());
    }

    // -------------------------------------------------------------------------------------
    // CheckSpeedFlyGlide: Check if player is flying, gliding or moving too fast
    // -------------------------------------------------------------------------------------
    private void CheckSpeedFlyGlide(PlayerMoveEvent event) {
        if (this.player.hasPermission("zac.fly")) { return; }
        if (this.player.getAllowFlight()) { return; }
        if (this.ZAC.getConfig().getBoolean("Speed") || this.ZAC.getConfig().getBoolean(
                "Fly") || this.ZAC.getConfig().getBoolean("Glide")) {
            //Anti Speed, Fly and Glide
            this.x_pos_old = new Vector3(event.getFrom().getX(), 0.0, event.getFrom().getZ());
            this.x_pos_new = new Vector3(event.getTo().getX(), 0.0, event.getTo().getZ());
            this.x_distance = this.x_pos_old.distance(this.x_pos_new);

            this.y_pos_old = event.getFrom().getY();
            this.y_pos_new = event.getTo().getY();
            this.y_distance = this.y_pos_old - this.y_pos_new;

            int tick = this.server.getTick();
            float tps = this.server.getTicksPerSecond();

            if (tps > 0.0 && this.prev_tick != -1) {
                int tick_count = (tick - this.prev_tick);     // server ticks since last move
                float delta_t = tick_count / tps;   // seconds since last move

                if (delta_t < 2.0)  // "OnMove" message lag is less than 2 second to calculate a new moving speed
                {
                    this.x_time_sum = this.x_time_sum - this.x_time_array.get(
                            this.x_arr_idx) + delta_t;             // ringbuffer time     sum  (remove oldest, add new)
                    this.x_dist_sum = this.x_dist_sum - this.x_dist_array.get(
                            this.x_arr_idx) + this.x_distance;    // ringbuffer distance sum  (remove oldest, add new)
                    this.x_time_array.set(this.x_arr_idx,
                            delta_t);                                                     // overwrite oldest delta_t  with the new one
                    this.x_dist_array.set(this.x_arr_idx,
                            this.x_distance);                                            // overwrite oldest distance with the new one
                    this.x_arr_idx++;                                                                                   // Update ringbuffer position
                    if (this.x_arr_idx >= this.x_arr_size) { this.x_arr_idx = 0; }

                    this.y_time_sum = this.y_time_sum - this.y_time_array.get(
                            this.y_arr_idx) + delta_t;             // ringbuffer time     sum  (remove oldest, add new)
                    this.y_dist_sum = this.y_dist_sum - this.y_dist_array.get(
                            this.y_arr_idx) + this.y_distance;    // ringbuffer distance sum  (remove oldest, add new)
                    this.y_time_array.set(this.y_arr_idx,
                            delta_t);                                                      // overwrite oldest delta_t  with the new one
                    this.y_dist_array.set(this.y_arr_idx,
                            this.y_distance);                                             // overwrite oldest distance with the new one
                    this.y_arr_idx++;                                                                                    // Update ringbuffer position
                    if (this.y_arr_idx >= this.y_arr_size) { this.y_arr_idx = 0; }
                }

                // calculate speed: distance per time
                if (this.x_time_sum > 0) { this.x_speed = (double) this.x_dist_sum / (double) this.x_time_sum; }
                else { this.x_speed = 0.0; }

                // calculate speed: distance per time
                if (this.y_time_sum > 0) { this.y_speed = (double) this.y_dist_sum / (double) this.y_time_sum; }
                else { this.y_speed = 0.0; }

                if (this.ZAC.getConfig().getBoolean("Speed")) {
                    if (!this.player.hasPermission("zac.speed")) {
                        // Anti Speed
                        if (this.player.hasEffect(Effect.SPEED)) {
                            this.SpeedAMP = this.player.getEffect(Effect.SPEED).getAmplifier();
                            if (this.SpeedAMP < 3) {
                                if (this.x_speed > 10) {
                                    if ((tick - this.LastDamageTick) > 30)  // deactivate 1.5 seconds after receiving damage
                                    {
                                        this.PlayerSpeedCounter += 10;
                                    }
                                }
                                else {
                                    if (this.PlayerSpeedCounter > 0) {
                                        this.PlayerSpeedCounter--;
                                    }
                                }
                            }
                            else {
                                this.PlayerSpeedCounter--;
                            }
                        }
                        else if (this.x_speed > 8.5) {
                            if ((tick - this.LastDamageTick) > 30)  //deactivate 1.5 seconds after receiving damage
                            {
                                this.PlayerSpeedCounter += 10;
                            }
                        }
                        else {
                            if (this.PlayerSpeedCounter > 0) {
                                this.PlayerSpeedCounter--;
                            }
                        }

                    }
                    if (this.PlayerSpeedCounter > this.ZAC.getConfig().getInt("Speed-Threshold") * 10) {
                        if (this.ZAC.getConfig().getString("Speed-Punishment") == "kick") {
                            event.setCancelled(true);
                            this.ResetObserver();
                            String message = this.ZAC.getConfig().getString("Speed-LogMessage");
                            String reason = this.ZAC.getConfig().getString("Speed-Message");
                            this.NotifyAdmins(message);
                            this.KickPlayer(reason);
                        }
                        if (this.ZAC.getConfig().getString("Speed-Punishment") == "block") {
                            event.setCancelled(true);
                            String message = this.ZAC.getConfig().getString("Speed-LogMessage");
                            this.NotifyAdmins(message);
                            this.PlayerSpeedCounter = (this.ZAC.getConfig().getInt("Speed-Threshold") * 10) - 10;
                        }
                    }
                }
            }
            this.prev_tick = tick;
        }

        // No Fly, No Glide and Anti Speed
        if (!this.ZACIsOnGround(this.player)) {
            if (this.surroundings.contains(Block.WATER)
                    && this.surroundings.contains(Block.STILL_WATER)
                    && this.surroundings.contains(Block.LAVA)
                    && this.surroundings.contains(Block.STILL_LAVA)
                    && this.surroundings.contains(Block.LADDER)
                    && this.surroundings.contains(Block.VINE)
                    && this.surroundings.contains(Block.COBWEB)) {
                if (this.y_pos_old > this.y_pos_new) {
                    // Player moves down. Check Glide Hack
                    if (this.ZAC.getConfig().getBoolean("Glide")) {
                        if (!this.player.hasPermission("zac.glide")) {
                            this.PlayerGlideCounter++;
                        }
                    }
                }
                else if (this.y_pos_old <= this.y_pos_new) {
                    // Player moves up or horizontal
                    if (this.ZAC.getConfig().getBoolean("Fly")) {
                        this.PlayerAirCounter++;
                        if (this.PlayerGlideCounter > 0) {
                            this.PlayerGlideCounter--;
                        }
                    }
                }
            }
        }
        else {
            this.PlayerAirCounter = 0;
            this.PlayerGlideCounter = 0;
        }

        if (this.PlayerGlideCounter > 25 && this.y_speed < 20) {
            if (this.ZAC.getConfig().getString("Glide-Punishment").equals("kick")) {
                event.setCancelled(true);
                this.ResetObserver();
                String message = this.ZAC.getConfig().getString("Glide-LogMessage");
                String reason = this.ZAC.getConfig().getString("Glide-Message");
                this.NotifyAdmins(message);
                this.KickPlayer(reason);
            }
            if (this.ZAC.getConfig().getString("Glide-Punishment").equals("block")) {
                event.setCancelled(true);
                String message = this.ZAC.getConfig().getString("Glide-LogMessage");
                this.NotifyAdmins(message);
            }
        }

        if (this.PlayerAirCounter > this.ZAC.getConfig().getInt("Fly-Threshold")) {
            if (this.ZAC.getConfig().getString("Fly-Punishment").equals("kick")) {
                event.setCancelled(true);
                this.ResetObserver();
                String message = this.ZAC.getConfig().getString("Fly-LogMessage");
                String reason = this.ZAC.getConfig().getString("Fly-Message");
                this.NotifyAdmins(message);
                this.KickPlayer(reason);
            }
            if (this.ZAC.getConfig().getString("Fly-Punishment").equals("block")) {
                event.setCancelled(true);
                String message = this.ZAC.getConfig().getString("Fly-LogMessage");
                this.NotifyAdmins(message);
            }
        }
    }

    private void CheckNoClip(Event event) {
        // No Clip
        if (this.ZAC.getConfig().getBoolean("NoClip")) {
            if (this.player.hasPermission("zac.noclip")) { return; }
            Level level = this.player.getLevel();
            Vector3 pos = new Vector3(this.player.getX(), this.player.getY(), this.player.getZ());
            int BlockID = level.getBlock(pos).getId();

            //ANTI-FALSE-POSITIVES
            if (

                //BUILDING MATERIAL

                    BlockID == 1
                            || BlockID == 2
                            || BlockID == 3
                            || BlockID == 4
                            || BlockID == 5
                            || BlockID == 7
                            || BlockID == 17
                            || BlockID == 18
                            || BlockID == 20
                            || BlockID == 43
                            || BlockID == 45
                            || BlockID == 47
                            || BlockID == 48
                            || BlockID == 49
                            || BlockID == 79
                            || BlockID == 80
                            || BlockID == 87
                            || BlockID == 89
                            || BlockID == 97
                            || BlockID == 98
                            || BlockID == 110
                            || BlockID == 112
                            || BlockID == 121
                            || BlockID == 155
                            || BlockID == 157
                            || BlockID == 159
                            || BlockID == 161
                            || BlockID == 162
                            || BlockID == 170
                            || BlockID == 172
                            || BlockID == 174
                            || BlockID == 243

                            //ORES (for Prison mines)

                            || BlockID == 14  //GOLD     (-)
                            || BlockID == 15  //IRON     (-)
                            || BlockID == 16  //COAL     (-)
                            || BlockID == 21  //LAPIS    (-)
                            || BlockID == 56  //DIAMOND  (-)
                            || BlockID == 73  //REDSTONE (DARK)
                            || BlockID == 73  //REDSTONE (GLOWING)
                            || BlockID == 129 //EMERALD  (-)
                    ) {
                if (this.surroundings.contains(Block.SLAB)
                        && this.surroundings.contains(Block.WOOD_STAIRS)
                        && this.surroundings.contains(Block.COBBLE_STAIRS)
                        && this.surroundings.contains(Block.BRICK_STAIRS)
                        && this.surroundings.contains(Block.STONE_BRICK_STAIRS)
                        && this.surroundings.contains(Block.NETHER_BRICKS_STAIRS)
                        && this.surroundings.contains(Block.SPRUCE_WOOD_STAIRS)
                        && this.surroundings.contains(Block.BIRCH_WOODEN_STAIRS)
                        && this.surroundings.contains(Block.JUNGLE_WOOD_STAIRS)
                        && this.surroundings.contains(Block.QUARTZ_STAIRS)
                        && this.surroundings.contains(Block.WOOD_SLAB)
                        && this.surroundings.contains(Block.ACACIA_WOOD_STAIRS)
                        && this.surroundings.contains(Block.DARK_OAK_WOOD_STAIRS)
                        && this.surroundings.contains(Block.SNOW)) {
                    if (this.ZAC.getConfig().getString("NoClip-Punishment").equals("kick")) {
                        this.PlayerNoClipCounter += 10;
                        event.setCancelled(true);
                        String message = this.ZAC.getConfig().getString("NoClip-LogMessage");
                        this.NotifyAdmins(message);
                        if (this.PlayerNoClipCounter > this.ZAC.getConfig().getInt("NoClip-Threshold") * 10) {
                            event.setCancelled(true);
                            String reason = this.ZAC.getConfig().getString("NoClip-Message");
                            this.ResetObserver();
                            this.KickPlayer(reason);
                        }
                    }
                    if (this.ZAC.getConfig().getString("NoClip-Punishment").equals("block")) {
                        event.setCancelled(true);
                        String message = this.ZAC.getConfig().getString("NoClip-LogMessage");
                        this.NotifyAdmins(message);
                    }
                }
            }
            else {
                if (this.PlayerNoClipCounter > 0) {
                    this.PlayerNoClipCounter--;
                }
            }
        }
    }

    void OnPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        if (this.ZAC.getConfig().getBoolean("ForceGameMode")) {
            if (this.player.hasPermission("zac.forcegamemode")) { return; }
            if (!event.getPlayer().isOp()) {
                String message = this.ZAC.getConfig().getString("ForceGameMode-LogMessage");
                this.NotifyAdmins(message);
                String reason = this.ZAC.getConfig().getString("ForceGameMode-Message");
                this.KickPlayer(reason);
                event.setCancelled(true);
            }
        }
    }

    void PlayerHasDamaged(EntityDamageEvent event) {
        Entity damaged_entity = event.getEntity();
        boolean is_damaged_entity_a_player = damaged_entity instanceof Player;
        Vector3 damaged_entity_position = new Vector3(damaged_entity.getX(), damaged_entity.getY(),
                damaged_entity.getZ());
        Vector3 damaged_xz_entity_position = new Vector3(damaged_entity.getX(), 0, damaged_entity.getZ());

        Player damager = this.player;
        Vector3 damager_position = new Vector3(damager.getX(), damager.getY(), damager.getZ());
        Vector3 damager_xz_position = new Vector3(damager.getX(), 0, damager.getZ());

        Vector3 damager_direction = damager.getDirectionVector();
        damager_direction = damager_direction.normalize();

        Vector3 damager_xz_direction = damager.getDirectionVector();
        damager_xz_direction.y = 0;
        damager_xz_direction = damager_xz_direction.normalize();

        Vector3 entity_xz_direction = damaged_xz_entity_position.subtract(damager_xz_position).normalize();
        Vector3 entity_direction = damaged_entity_position.subtract(damager_position).normalize();

        double distance_xz = damager_xz_position.distance(damaged_xz_entity_position);
        double distance = damager_position.distance(damaged_entity_position);

        double dot_product_xz = damager_xz_direction.dot(entity_xz_direction);
        double angle_xz = Math.toDegrees(Math.acos(dot_product_xz));

        double dot_product = damager_direction.dot(entity_direction);
        double angle = Math.toDegrees(Math.acos(dot_product));

        int tick_count = this.server.getTick() - this.LastMoveTick;
        float tps = this.server.getTicksPerSecond();
        float delta_t = 0.0f;
        if (tps != 0) { delta_t = (tick_count) / tps; }
        else { delta_t = 0; }

        this.Logger.debug(TextFormat.ESCAPE + "this.Colorized" +
                "[ZAC] > Kill Aura Counter: this.PlayerKillAuraCounter     V2: this.PlayerKillAuraV2Counter  Speed: this.x_speed")
        ;
        if (this.player.getGamemode() == 1 || this.player.getGamemode() == 3) { return; }
        // Kill Aura
        if (this.ZAC.getConfig().getBoolean("KillAura")) {
            if (!this.player.hasPermission("zac.killaura")) {
                if (is_damaged_entity_a_player) {
                    int tick = this.server.getTick();
                    tps = this.server.getTicksPerSecond();
                    if (this.PlayerHitFirstTick == -1) {
                        this.PlayerHitFirstTick = tick;
                    }

                    tick_count = tick - this.PlayerHitFirstTick;   // server ticks since last hit
                    delta_t = (tick_count) / tps;          // seconds since last hit

                    this.hs_time_sum = this.hs_time_sum - this.hs_time_array.get(
                            this.hs_arr_idx) + delta_t;      // ringbuffer time sum  (remove oldest, add new)
                    this.hs_time_array.set(this.hs_arr_idx,
                            delta_t);                                                // overwrite oldest delta_t  with the new one
                    this.hs_arr_idx++;                                                                               // Update ringbuffer position
                    if (this.hs_arr_idx >= this.hs_arr_size) { this.hs_arr_idx = 0; }
                    this.hs_hit_time = this.hs_time_sum / this.hs_arr_size;
                    this.Logger.info(TextFormat.ESCAPE + "this.Colorized" +
                            "[ZAC] > THD this.PlayerName : hittime = this.hs_hit_time");

                    if (this.hs_hit_time < 0.0825) {
                        this.PlayerHitCounter += 2;
                    }
                    else {
                        if (this.PlayerHitCounter > 0) {
                            this.PlayerHitCounter--;
                        }
                    }
                    //Allow a maximum of 2 Unlegit hits, couter derceases x5 slower
                    if (this.PlayerHitCounter > 10) {
                        event.setCancelled(true);
                        this.ResetObserver();
                        String message = this.ZAC.getConfig().getString("KillAura-LogMessage");
                        String reason = this.ZAC.getConfig().getString("KillAura-Message");
                        this.NotifyAdmins(message);
                        this.KickPlayer(reason);
                        return;
                    }
                    this.PlayerHitFirstTick = tick;
                    if (distance_xz >= 0.5) {
                        //V2
                        if (this.dist_thr1 != 0.00) {
                            if ((distance >= this.dist_thr1) &&
                                    (delta_t < 0.50) &&
                                    (angle_xz > 22.5) &&
                                    (
                                            ((this.x_speed > 1.5) & (this.hs_hit_time < 0.5)) || (this.x_speed > 4.75)
                                    )) {
                                this.PlayerKillAuraV2Counter += 2;
                            }
                            else if ((distance >= this.dist_thr2) &&
                                    (delta_t < 0.50) &&
                                    (angle_xz > 45.0) &&
                                    (
                                            ((this.x_speed > 1.5) && (this.hs_hit_time < 0.5)) || (this.x_speed > 4.75)
                                    )) {
                                this.PlayerKillAuraV2Counter += 2;
                            }
                            else if ((distance >= this.dist_thr3) &&
                                    (delta_t < 0.50) &
                                            (
                                                    ((this.x_speed > 1.5) && (this.hs_hit_time < 0.5)) || (this.x_speed > 4.75)
                                            )) {
                                if (this.dist_thr3 != 0.000) {
                                    this.PlayerKillAuraV2Counter += 2;
                                }
                            }
                            else if (!this.ZACIsOnGround(damager)) {
                                this.PlayerKillAuraV2Counter += 4;
                            }
                            else {
                                if (this.PlayerKillAuraV2Counter > 0) {
                                    this.PlayerKillAuraV2Counter--;
                                }
                            }
                        }

                        if (angle_xz > 45) {
                            if (this.ZAC.getConfig().getBoolean("Angle")) {
                                event.setCancelled(true);
                            }
                            if (angle_xz > 90) {
                                if (this.dist_thr1 != 0.00) {
                                    this.PlayerKillAuraV2Counter += 8;
                                }
                                if (this.ZAC.getConfig().getBoolean("Angle")) {
                                    this.PlayerKillAuraCounter += 8;
                                }
                            }
                        }
                        if (this.ZAC.getConfig().getBoolean("AimbotCatcher")) {
                            this.Logger.debug(TextFormat.ESCAPE + "this.Colorized" +
                                    "[ZAC] > counter V2: this.PlayerKillAuraV2Counter");
                            //V1
                            if ((angle_xz < 1.5) && (angle < 20) && (delta_t < 0.5) && (this.x_speed > 4.75)) {
                                this.PlayerKillAuraCounter += 2;
                            }
                            if ((angle_xz >= 1.5) || (angle >= 20) || (delta_t > 2.0)) {
                                if (this.PlayerKillAuraCounter > 0) {
                                    if (!(angle_xz > 90)) {
                                        this.PlayerKillAuraCounter--;
                                    }
                                }
                            }
                            this.Logger.info(TextFormat.ESCAPE + "this.Colorized" +
                                    "[ZAC] > counter V1: this.PlayerKillAuraCounter  V2: this.PlayerKillAuraV2Counter distance: $distance_xz  deltat: $delta_t  speedx: this.x_speed anglexz: $angle_xz")
                            ;
                        }
                        this.Logger.info(TextFormat.ESCAPE + "this.Colorized" +
                                "AAA[ZAC] > counter V1: this.PlayerKillAuraCounter  V2: this.PlayerKillAuraV2Counter distance: $distance_xz  deltat: $delta_t  speedx: this.x_speed anglexz: $angle_xz")
                        ;
                    }

                    if ((this.PlayerKillAuraCounter >= this.ZAC.getConfig().getInt("KillAura-Threshold"))
                            || (this.PlayerKillAuraV2Counter >= this.ZAC.getConfig().getInt("KillAura-Threshold"))) {
                        event.setCancelled(true);
                        String message = this.ZAC.getConfig().getString("KillAura-LogMessage");
                        this.NotifyAdmins(message);
                        String reason = this.ZAC.getConfig().getString("KillAura-Message");
                        this.ResetObserver();
                        this.KickPlayer(reason);
                    }
                }
            }
        }

        //Reach Check
        if (this.ZAC.getConfig().getBoolean("Reach")) {
            if (!this.player.hasPermission("zac.reach")) {
                double reach_distance = damager_position.distance(damaged_entity_position);
                this.Logger.debug(TextFormat.ESCAPE + "this.Colorized" +
                        "[zac] > Reach distance this.PlayerName : $reach_distance");

                if (reach_distance > this.ZAC.getConfig().getInt("MaxRange")) {
                    event.setCancelled(true);
                }
            }
      /*
      if ($reach_distance > this.GetConfigEntry("KickRange"))
      {
        this.PlayerReachCounter++;
        #this.Logger->debug(TextFormat::ESCAPE."this.Colorized" . "[ZAC] > this.PlayerName  ReachCounter: this.PlayerReachCounter");
        $tick = (double)this.Server->getTick();
        $tps  = (double)this.Server->getTicksPerSecond();

        if (this.PlayerReachFirstTick == -1)
        {
          this.PlayerReachFirstTick = $tick;
        }
        if (this.PlayerReachCounter > 4 and $tps > 0)
        {
          $tick_count = (double)($tick - this.PlayerReachFirstTick); // server ticks since last reach hack
          $delta_t    = (double)($tick_count) / (double)$tps;          // seconds since first reach hack

          if ($delta_t < 60)
          {
            if (this.GetConfigEntry("Reach-Punishment") == "kick")
            {
              $event->setCancelled(true);
              this.ResetObserver();
              $message = this.GetConfigEntry("Reach-LogMessage");
              $reason  = this.GetConfigEntry("Reach-Message");
              this.NotifyAdmins($message);
              this.KickPlayer($reason);
              return;
            }
            if (this.GetConfigEntry("Reach-Punishment") == "block")
            {
              $event->setCancelled(true);
              $message = this.GetConfigEntry("Reach-LogMessage");
              this.NotifyAdmins($message);
            }
          }
          else
          {
            this.PlayerReachFirstTick = $tick;
            this.PlayerReachCounter   = 0;
          }
        }
      }
      */
        }
    }

    void PlayerWasDamaged(EntityDamageEvent event) {
        if (event.getDamage() >= 1) {
            this.LastDamageTick = this.server.getTick();  // remember time of last damage
        }
    }

    void onDeath(PlayerDeathEvent event) {
        this.ResetMovement();
        this.LastDamageTick = this.server.getTick();  // remember time of last damage
    }

    void onRespawn(PlayerRespawnEvent event) {
        this.ResetMovement();
        this.LastDamageTick = this.server.getTick();  // remember time of last damage
    }

    void onTeleport(EntityTeleportEvent event) {
        this.ResetObserver();
        this.LastDamageTick = this.server.getTick();  // remember time of last damage
    }
}
