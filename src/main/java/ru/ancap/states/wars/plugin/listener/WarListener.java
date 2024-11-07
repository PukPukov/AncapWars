package ru.ancap.states.wars.plugin.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.ancap.commons.aware.Aware;
import ru.ancap.commons.aware.ContextAware;
import ru.ancap.commons.aware.InsecureContextHandle;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.framework.api.event.events.time.heartbeat.AncapHeartbeatEvent;
import ru.ancap.framework.api.event.events.wrapper.PVPEvent;
import ru.ancap.framework.api.event.events.wrapper.WorldInteractEvent;
import ru.ancap.framework.api.event.events.wrapper.WorldSelfDestructEvent;
import ru.ancap.framework.artifex.Artifex;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.identifier.Identifier;
import ru.ancap.framework.language.LAPI;
import ru.ancap.framework.language.additional.LAPIDomain;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.framework.util.player.StepbackMaster;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.hexagon.common.Point;
import ru.ancap.states.AncapStates;
import ru.ancap.states.event.events.HexagonOwnerChangeEvent;
import ru.ancap.states.hexagons.AncapHexagonalGrid;
import ru.ancap.states.hexagons.Codeficator;
import ru.ancap.states.message.PrecisionFormatter;
import ru.ancap.states.states.State;
import ru.ancap.states.states.city.RequestState;
import ru.ancap.states.states.event.SubjectChangeAffiliationEvent;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.WarValues;
import ru.ancap.states.wars.api.castle.BuiltCastle;
import ru.ancap.states.wars.api.field.FieldConflicts;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.AssaultRepulseEvent;
import ru.ancap.states.wars.api.war.BarrierDestroyEvent;
import ru.ancap.states.wars.api.war.War;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.bossbar.BossBars;
import ru.ancap.states.wars.event.*;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.integration.BedrockIntegration;
import ru.ancap.states.wars.messaging.Message;
import ru.ancap.states.wars.messaging.Sounds;
import ru.ancap.states.wars.plugin.config.WarConfig;
import ru.ancap.states.wars.utils.LAPIReceiver;
import ru.ancap.states.wars.war.info.AssaultExecutor;
import ru.ancap.states.wars.war.process.restriction.WarMonitor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Accessors(fluent = true) @Getter
public class WarListener implements Listener {

    private final AncapHexagonalGrid grid;
    private final AssaultExecutor assaults;
    private final FieldConflicts field;
    private final WarMonitor battleMonitor;
    private final AttackCounter attackCounter;
    private final StepbackMaster stepbackMaster;

    public final TriConsumer<WarState, WarState, WarHexagon> onOccupy = (loser, occupier, hexagon) -> {
        AncapDebug.debug("ON OCCUPY CALLED", loser, occupier, hexagon);
        occupier.transferHexagonFrom(hexagon, loser);
        AncapDebug.debug("ON OCCUPY CONTINUES");
        Bukkit.getOnlinePlayers().forEach(listeningPlayer -> listeningPlayer.sendMessage(
            Component.text(LAPI.localized(Message.Minecraft.Notify.Hexagon_legacy.OCCUPY, Identifier.of(listeningPlayer))
                .replace("%COORDINATES%", hexagon.getReadableBlockCoordinates())
                .replace("%OCCUPIER%", occupier.name())
                .replace("%LOSER%", loser.name())
            )
        ));
        AncapDebug.debug("ON OCCUPY END");
    };

    private final BiConsumer<WarHexagon, WarState> onHit = (hexagon, attacker) -> {
        hexagon.getOwner().warActor().getOnlinePlayers().forEach((player) -> 
            this.attackCounter().notifyAboutAttack(player, attacker, hexagon)
        );
    };
    
    private final ExecutorService heartbeatExecutor = Executors.newSingleThreadExecutor();
    
    @ContextAware(awareOf = Aware.THREAD, handle = InsecureContextHandle.NO_HANDLE)
    private final Consumer<Player> onHeartbeatAction = player -> {
        if (player.isDead()) return;
        Warrior warrior = Warrior.get(player);
        
        
        Bukkit.getScheduler().runTask(Artifex.PLUGIN(), () -> this.stepbackMaster().ensureOuter(
            player,
            location -> {
                return this.assaults().assault(Codeficator.hexagon(location)).type() != AssaultRuntimeType.PREPARE ||
                    warrior.canInteract(location);
                },
            () -> {
                Communicator.of(player).message(new LAPIMessage(AncapWars.class, "leave-prepared-hexagon"));
                Bukkit.getScheduler().runTask(AncapWars.loaded(), () -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
                    player.playSound(Sound.sound(Key.key("minecraft:entity.enderman.stare"), Sound.Source.AMBIENT, 2, 0));
                });
            }
        ));
        
        
        Hexagon hexagon = AncapStates.grid.hexagon(new Point(player.getLocation().getX(), player.getLocation().getZ()));
        long code = hexagon.code();
        WarHexagon warHexagon = new WarHexagon(code);
        AssaultRuntimeType type = this.assaults().assault(code).type();

        Material material;

        switch (type) {
            case WAR -> {
                if (BedrockIntegration.bcheck(player)) {
                    AssaultRuntime runtime = this.assaults().assault(code);
                    BossBars.send(player, 1, BossBar.bossBar(
                        Component.text(LAPI.localized(
                            LAPIDomain.of(AncapWars.class, "assault.bossbar"), Identifier.of(player)
                        ).replace("%CASTLE_NAME%", "" + ((BuiltCastle) runtime.barrier()).name())),
                        ((float) runtime.health()) / this.assaults().maximumCastleHealth(),
                        BossBar.Color.RED,
                        BossBar.Overlay.NOTCHED_6
                    ));
                    player.sendActionBar(Component.text("Сердце замка находится в "+PrecisionFormatter.format(player.getLocation().distance(runtime.barrier().location()), 2)+" блоках"));
                    this.battleMonitor().accept(player);
                }
            } default -> BossBars.hide(player, 1);
        }
        this.field().acceptExistence(player, code, this.onOccupy, this.onHit);
        if (this.field().atFieldConflict(code)) {
            if (BedrockIntegration.bcheck(player)) {
                double occupyLevel = this.field().occupyLevel(code);
                float occupationFloat = (float) occupyLevel;
                String occupationPercentage = PrecisionFormatter.format((occupyLevel * 100), 3);
                BossBars.send(player, 3, BossBar.bossBar(
                    Component.text(LAPI.localized(Message.Minecraft.Info.FieldConflict.BossBar.NAME, Identifier.of(player))
                        .replace("%PERCENTAGE%", "" + occupationPercentage)
                    ),
                    occupationFloat,
                    BossBar.Color.BLUE,
                    BossBar.Overlay.PROGRESS
                ));
            } 
        } else {
            BossBars.hide(player, 3);
        }
    };

    @EventHandler
    public void on(AncapHeartbeatEvent event) {
        this.heartbeatExecutor.execute(() -> Bukkit.getOnlinePlayers().forEach(this.onHeartbeatAction));
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Location location = event.getBlock().getLocation();
        long code = this.grid().hexagon(new Point(location.getX(), location.getZ())).code();
        if (this.assaults.assault(code).type() == AssaultRuntimeType.WAR) {
            if (location.getBlockY() > WarConfig.loaded().maxCastleHeight()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (this.assaults.isActiveCastleHeart(location)) {
            this.assaults.hitHeart(event, location);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(WorldInteractEvent event) {
        if (event.consumed()) return;
        for (Location location : event.locations()) {
            long code = AncapStates.grid.hexagon(location).code();
            if (!this.field.atFieldConflict(code) && this.assaults.assault(code).type() != AssaultRuntimeType.WAR) {
                return;
            }
        }
        event.consume();
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void on(WorldSelfDestructEvent event) {
        if (event.consumed()) return;
        for (Location location : event.passive()) {
            long code = AncapStates.grid.hexagon(location).code();
            if (this.field.atFieldConflict(code) || this.assaults.assault(code).type() == AssaultRuntimeType.WAR) {
                event.consume();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PVPEvent event) {
        for (Player player : event.attacked()) {
            long code = AncapStates.grid.hexagon(player.getLocation()).code();
            if (AncapWars.isAtWar(AncapStates.grid.hexagon(code))) {
                event.consume();
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(BlockExplodeEvent event) {
        this.operateExplode(event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(EntityExplodeEvent event) {
        this.operateExplode(event.getLocation(), event);
    }
    
    private void operateExplode(Location location, Cancellable event) {
        if (AncapWars.isAtWar(AncapStates.grid.hexagon(location))) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on0(CastleBuildEvent event) {
        new WorldInteractEvent(event, event.getCreator(), List.of(event.getLocation())).callEvent();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on1(CastleBuildEvent event) {
        if (event.isCancelled()) return;
        event.getCreatedCastle().initialize(Identifier.of(event.getCreator()), event.getLocation(), event.getName());
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(Component.text(
            LAPI.localized(Message.Minecraft.Notify.Castle.FOUND, Identifier.of(player))
                .replace("%PLAYER%", event.getCreator().getName())
                .replace("%NAME%", event.getName())
        )));
    }

    @EventHandler
    public void on(WarDeclareEvent event) {
        if (event.isCancelled()) return;
        new War(WarID.war().get(event.getData().name())).initialize(event.getAttacker(), event.getTarget(), event.getData());
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), Sounds.War.DECLARE, 100, 0);
            player.sendMessage(Component.text(LAPI.localized(Message.Minecraft.Notify.War.Start.DECLARE, Identifier.of(player))
                .replace("%ATTACKER%", event.getAttacker().name())
                .replace("%TARGET%", event.getTarget().name())
                .replace("%REASON%", event.getData().reason())));
        });
    }

    @EventHandler
    public void on(AssaultRepulseEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> LAPIReceiver.send(Message.Minecraft.Notify.Assault.REPULSE, player,
            "%STATE%", event.getRuntime().barrier().hexagon().getOwner().name(),
            "%ATTACKER%", event.getRuntime().attacker().name(),
            "%NAME%", ((BuiltCastle) event.getRuntime().barrier()).name()
        ));
    }

    @EventHandler
    @SneakyThrows
    public void on(BarrierDestroyEvent event) {
        CallableMessage message = event.getBarrier().deleteMessage(event.getDestroyer());
        if (message != null) Bukkit.getOnlinePlayers().stream().map(Communicator::of).forEach(communicator -> communicator.message(message));
        
        WarHexagon hexagon = event.getBarrier().hexagon();
        hexagon.devastate(event.getBarrier().location());
        if (this.assaults().assault(hexagon.code()).type() != AssaultRuntimeType.PEACE) this.assaults().setPeace(hexagon.code());
        event.getBarrier().delete();
    }

    @EventHandler
    public void on(PenaltyGiveEvent event) {
        /* event.getPenalted().getState().getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.War.Penalty.GET, player,
            "%TIME%", "" + event.getHours()
        )); потому что пенальти отключены */
        event.getPenalted().setPenalty(event.getHours());
    }

    @EventHandler
    public void on(BarrierAttackDeclareEvent event) {
        if (event.isCancelled()) return;
        WarState defender = event.getBarrier().owner();
        AttackWait attackWait = event.getBarrier().attackWait();
        attackWait.initialize(event.getAttacker(), event.getWar());
        if (!AncapWars.debug()) {
            AncapDebug.debug("Не дебаг, устанавливаем начало битвы на завтро");
            ZonedDateTime time = LocalDateTime.now().atZone(ZoneOffset.of("+03:00"));
            ZonedDateTime then = time.plusDays(1).withHour(18).withMinute(0);
            long millis = then.toInstant().toEpochMilli();
            AncapDebug.debug("Завтро это", millis, "а от чичаса это", millis - System.currentTimeMillis());
            attackWait.setAssaultStartTime(millis);
        } else {
            AncapDebug.debug("Дебаг, устанавливаем начало битвы на сигодньо через пять сьекунд");
            long assaultStartTime = System.currentTimeMillis() + (WarValues.PREPARE_SECONDS_DEBUG * 1000);
            AncapDebug.debug("Устанавливаем старттайм на "+assaultStartTime, "а от чичаса это", assaultStartTime - System.currentTimeMillis());
            attackWait.setAssaultStartTime(assaultStartTime);
        }
        new AttackDeclareLoadEvent(attackWait).callEvent();
    }

    @EventHandler
    public void on(AttackDeclareLoadEvent event) {
        this.assaults.acceptDeclareLoad(event.getAttackWait());
    }

    @EventHandler
    public void on(AssaultStartEvent event) {
        if (event.isCancelled()) return;
        event.getAttackWait().getBarrier().acceptTimedAttack(event.getAttackWait(), event.getPrepareRuntime(), event.getToSet());
    }
    
    @EventHandler
    public void on(HexagonOwnerChangeEvent event) {
        switch (event.requestState()) {
            case RequestState.Instruction<Player> instruction -> {
                this.assaults().setPeace(event.hexagon().code());
                this.field().setPeace(event.hexagon().code());
            }
            case RequestState.Request<Player> request -> {
                if (!AncapWars.isUnstable(event.hexagon())) break;
                event.setCancelled(true);
                Communicator.of(request.requester()).message(new LAPIMessage(AncapWars.class, "hexagon.cant-change-owner-at-war"));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void on(SubjectChangeAffiliationEvent event) {
        AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent");
        if (!(event.subject() instanceof State subject)) return;
        AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 2");
        WarState warSubject = WarState.of(subject.id());
        switch (event.changeType()) {
            case LEAVE -> {
                AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 3");
                warSubject.failTerritorialDefence();
            }
            case JOIN -> {
                AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 4");
                if (!(event.newAffiliate().orElseThrow() instanceof State affiliate)) throw new IllegalStateException();
                AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 5");
                WarState warAffiliate = WarState.of(affiliate.id());
                permission_check: if (event.requestState() instanceof RequestState.Request<Player> request) {
                    Warrior requester = Warrior.get(request.requester());
                    AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 6");
                    if (warSubject.viewActiveWars().isEmpty() || requester.canManageWarsOf(warAffiliate)) break permission_check;
                    AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 7");
                    event.setCancelled(true);
                    requester.sendMessage(new LAPIMessage(
                        AncapWars.class, "cant-affiliate-because-of-not-allowed-war-transfer",
                        new Placeholder("affiliate", affiliate.simpleName()),
                        new Placeholder("subject",   subject  .simpleName())
                    ));
                    return;
                }
                AncapDebug.debug("Прилетел SubjectChangeAffiliationEvent 8");
                AncapDebug.debug(
                    "Вызываем warIncorporate() после SubjectChangeAffiliationEvent",
                    "change type", event.changeType(),
                    "request state", event.requestState(),
                    "subject", event.subject(),
                    "new affiliate", event.newAffiliate()
                );
                warSubject.warIncorporate(warAffiliate);
            }
        }
        
    }
    
    @EventHandler
    public void on(PlayerItemDamageEvent event) {
        if (AncapWars.isAtWar(AncapStates.grid.hexagon(event.getPlayer()))) event.setDamage(event.getDamage() * 5);
    }
    
}