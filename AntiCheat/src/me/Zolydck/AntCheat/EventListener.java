package me.Zolydck.AntCheat;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.utils.MainLogger;

import java.util.UUID;

public class EventListener implements Listener {

    private AntiCheat ZAC;
    private MainLogger Logger;
    private Server server;

    EventListener(AntiCheat ZAC) {
        this.ZAC = ZAC;
        this.Logger = ZAC.getServer().getLogger();
        this.server = ZAC.getServer();
    }

    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID hash = player.getUniqueId();
        String name = player.getName();
        Observer observer = null;
        UUID oldhash = null;

        if (ZAC.getPlayerObservers().containsKey(hash)) {
            Observer obs = ZAC.getPlayerObservers().get(hash);
            oldhash = hash;
            observer = obs;
            observer.setPlayer(player);
        }

        if (oldhash != null) {
            ZAC.getPlayerObservers().remove(oldhash);
            ZAC.getPlayerObservers().put(hash, observer);
            ZAC.getPlayerObservers().get(hash).PlayerRejoin();
        }
        else {
            observer = new Observer(player, this.ZAC);
            ZAC.getPlayerObservers().put(hash, observer);
            ZAC.getPlayerObservers().get(hash).PlayerRejoin();
        }
    }

    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID hash = player.getUniqueId();

        if (ZAC.getPlayerObservers().containsKey(hash)) {
            Observer observer = ZAC.getPlayerObservers().get(hash);
            if (observer != null) {
                observer.PlayerQuit();
            }
            ZAC.getPlayerObservers().remove(hash);
        }
    }

    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID hash = player.getUniqueId();

        if (ZAC.getPlayerObservers().containsKey(hash)) {
            ZAC.getPlayerObservers().get(hash).OnMove(event);
      /*
      //THIS IS IN-DEV AND NOT USEABLE
      this.Main->PlayerObservers[$hash]->getRealKnockBack($event);
      */
        }
    }

    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
        if (event.getRegainReason() != EntityRegainHealthEvent.CAUSE_MAGIC && event.getRegainReason() != EntityRegainHealthEvent.CAUSE_CUSTOM) {
            if (event.getEntity() instanceof Player) {
                Player player = (Player) event.getEntity();
                UUID hash = player.getUniqueId();
                if (ZAC.getPlayerObservers().containsKey(hash)) {
                    ZAC.getPlayerObservers().get(hash).PlayerRegainHealth(event);
                }
            }
        }
    }

    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        UUID hash = player.getUniqueId();
        if (ZAC.getPlayerObservers().containsKey(hash)) {
            ZAC.getPlayerObservers().get(hash).OnPlayerGameModeChangeEvent(event);
        }
    }

    public void onBlockPlaceEvent(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        UUID hash = player.getUniqueId();
        if (ZAC.getPlayerObservers().containsKey(hash)) {
            ZAC.getPlayerObservers().get(hash).OnBlockPlaceEvent();
        }
    }


    public void onDamage(EntityDamageEvent event) {
        String evname = event.getEventName();
        if (event instanceof EntityDamageByEntityEvent) {
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                Entity ThisEntity = event.getEntity();
                if (ThisEntity instanceof Player) {
                    Player player = (Player) event.getEntity();
                    UUID hash = player.getUniqueId();
                    if (ZAC.getPlayerObservers().containsKey(hash)) {
                        ZAC.getPlayerObservers().get(hash).PlayerWasDamaged(event);
                    }
                }

                Entity ThisDamager = ((EntityDamageByEntityEvent) event).getDamager();
                if (ThisDamager instanceof Player) {
                    Player player = (Player) event.getEntity();
                    UUID hash = player.getUniqueId();
                    if (ZAC.getPlayerObservers().containsKey(hash)) {
                        ZAC.getPlayerObservers().get(hash).PlayerHasDamaged(event);
                    }
                }
            }
        }
    }

    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID hash = player.getUniqueId();
        if (ZAC.getPlayerObservers().containsKey(hash)) {
            ZAC.getPlayerObservers().get(hash).onDeath(event);
        }
    }

    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID hash = player.getUniqueId();
        if (ZAC.getPlayerObservers().containsKey(hash)) {
            ZAC.getPlayerObservers().get(hash).onRespawn(event);
        }
    }

    public void onEntityTeleportEvent(EntityTeleportEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            UUID hash = player.getUniqueId();
            if (ZAC.getPlayerObservers().containsKey(hash)) {
                ZAC.getPlayerObservers().get(hash).onTeleport(event);
            }
        }
    }
}
