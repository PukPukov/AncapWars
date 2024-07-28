package ru.ancap.states.wars.api.war;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.commons.list.merge.MergeList;
import ru.ancap.commons.map.GuaranteedMap;
import ru.ancap.states.AncapStates;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.Castle;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.event.AssaultStartEvent;
import ru.ancap.states.wars.plugin.config.WarConfig;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;
import ru.ancap.states.wars.war.info.AssaultExecutor;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AssaultOperator implements AssaultExecutor, Listener {

    private final Map<Long, AssaultRuntime> runtimes = new ConcurrentHashMap<>();
    private final Map<War, Set<AssaultRuntime>> warRuntimes = new GuaranteedMap<>(ConcurrentHashMap::newKeySet);
    private final Map<String, Set<Long>> hexagons = new GuaranteedMap<>(ConcurrentHashMap::newKeySet);
    private final Set<String> stoppedAttacks = ConcurrentHashMap.newKeySet();
    private final int maxCastleHeight;
    private final long castleHealth;
    private final long battleTimeSeconds;

    public AssaultOperator() {
        this.maxCastleHeight = WarConfig.loaded().maxCastleHeight();
        this.castleHealth = WarConfig.loaded().getCastleHealth();
        this.battleTimeSeconds = WarConfig.loaded().getAssaultTime();
    }
    
    @EventHandler
    public void on(AssaultEndEvent event) {
        AncapWars.assaults().remove(event.getRuntime());
    }

    @Override
    public synchronized void acceptDeclareLoad(AttackWait attackWait) {
        AncapDebug.debug("Принимаем AssaultLoad");
        AncapDebug.debug("Режим дебага поставлен на", AncapWars.debug());
        AncapDebug.debug("Чичас", System.currentTimeMillis());
        List<WarHexagon> toSet = this.operatedHexagons(attackWait.getBarrier().hexagon(), attackWait.getAttacker());
        AncapDebug.debug("OperatedHexagons", toSet, toSet.stream().map(WarHexagon::code).toList());
        AssaultRuntime runtime = AssaultRuntime.prepareOf(attackWait.getBarrier(), attackWait.getAttacker(), attackWait.war(), attackWait);
        Long assaultStartTime = attackWait.getTime();
        if (assaultStartTime == null) assaultStartTime = 0L;
        AncapDebug.debug("AssaultStartTime = "+assaultStartTime);
        long millisToWait = assaultStartTime - System.currentTimeMillis();
        long ticksToWait = millisToWait/50;
        AncapDebug.debug("Количество тиков для ожидания "+ticksToWait);
        AncapDebug.debug("Проставляем всем гексам рантайм");
        this.putRuntime(toSet.stream().map(WarHexagon::code).collect(Collectors.toSet()), attackWait.war(), runtime);
        AncapDebug.debug("Шедулим бросок ивента");
        String assaultID = runtime.id();
        Bukkit.getScheduler().runTaskLater(
            AncapWars.loaded(),
            () -> {
                if (this.stoppedAttacks.contains(assaultID)) return;
                new AssaultStartEvent(attackWait, runtime, toSet).callEvent();
            },
            ticksToWait
        );
        assaultStartTime = attackWait.getTime();
        AncapDebug.debug("AssaultStartTime2 = "+assaultStartTime);
    }

    @Override
    public synchronized void acceptAssault(AttackWait wait, AssaultRuntime prepareRuntime, List<WarHexagon> toSet) {
        AncapDebug.debug("Принимаем Assault");
        AncapDebug.debug("BattleTimeSeconds", this.battleTimeSeconds);
        Location location = wait.getBarrier().location();
        AncapDebug.debug("Location of block", location.getX(), location.getY(), location.getZ());
        AncapDebug.debug("maxCastleHeight", this.maxCastleHeight);
        if (location.getBlockY() > this.maxCastleHeight) {
            location.setY(this.maxCastleHeight);
        }
        location.getBlock().setType(Material.CRYING_OBSIDIAN);
        this.drawHologram(wait.getBarrier(), location);
        AssaultRuntime warRuntime = AssaultRuntime.warOf((int) castleHealth, wait.getBarrier(), wait.getAttacker(), wait.war(), wait);
        this.putRuntime(toSet.stream().map(WarHexagon::code).collect(Collectors.toSet()), wait.war(), warRuntime);
        Bukkit.getScheduler().runTaskLater(
            AncapWars.loaded(),
            () -> { if (warRuntime.active()) {
                new AssaultEndEvent(AssaultEndEvent.Reason.REPULSE, warRuntime).callEvent();
                new AssaultRepulseEvent(warRuntime).callEvent();
            }},
            this.battleTimeSeconds * 20
        );
    }

    private void drawHologram(Barrier barrier, Location location) {
        /* Location holoLoc = location.clone();
        holoLoc.setY(holoLoc.getY()+1.5);
        holoLoc.setZ(holoLoc.getZ()+0.5);
        holoLoc.setX(holoLoc.getX()+0.5);
        DecentHolograms holograms = DecentHologramsAPI.get();
        Hologram hologram = new Hologram(castle.id(), holoLoc);
        HologramPage page = hologram.addPage();
        page.addLine(new HologramLine(page, holoLoc.subtract(0, 0.25, 0), castle.name()));
        page.addLine(new HologramLine(page, holoLoc.subtract(0, 0.5, 0), this.castleHealth+"/"+this.castleHealth+" §4❤"));
        if (holograms.getHologramManager().containsHologram(castle.id())) {
            holograms.getHologramManager().removeHologram(castle.id());
        }
        holograms.getHologramManager().registerHologram(hologram); */
    }

    @Override
    public synchronized boolean isActiveCastleHeart(Location location) {
        AssaultRuntime runtime = this.runtimes.get(AncapStates.grid.hexagon(location).code());
        if (runtime == null) return false;
        return runtime.barrier().location().distance(location) < 1;
    }

    @Override
    public synchronized void hitHeart(BlockBreakEvent event, Location location) {
        event.setCancelled(true);
        WarHexagon hexagon = new WarHexagon(AncapStates.grid.hexagon(location).code());
        Castle castle = hexagon.castle();
        this.drawHologram(castle, location);
        AssaultRuntime warRuntime = this.runtimes.get(hexagon.code());
        if (warRuntime.hit()) {
            event.getBlock().getLocation().createExplosion(6f);
            World world = event.getBlock().getWorld();
            world.dropItem(location, new ItemStack(Material.NETHERITE_SCRAP, new Random().nextInt(10)));
            world.dropItem(location, new ItemStack(Material.GOLD_INGOT, new Random().nextInt(10)));
            event.getBlock().breakNaturally();
            this.breakCastle(castle, warRuntime.attacker(), warRuntime);
        }
    }
    
    @Override
    public void breakCastle(Barrier barrier, WarState breaker, AssaultRuntime runtime) {
        new AssaultEndEvent(AssaultEndEvent.Reason.CONQUER, runtime).callEvent();
        new BarrierDestroyEvent(barrier, breaker).callEvent();
    }

    @Override
    public synchronized @NotNull AssaultRuntime assault(long code) {
        return this.runtimes.getOrDefault(code, AssaultRuntime.PEACE);
    }

    private List<WarHexagon> operatedHexagons(WarHexagon main, WarState attacker) {
        List<WarHexagon> paths = main.getPathsTo(attacker);
        List<WarHexagon> operated = new MergeList<>(paths, List.of(main));
        if (operated.size() == 0) throw new IllegalStateException();
        return operated;
    }

    /**
     * @return Read-only representation on write-able runtimes
     */
    public synchronized Set<AssaultRuntime> findAssaultRuntimesOf(War war) {
        return this.warRuntimes.get(war);
    }

    @Override
    public synchronized void remove(AssaultRuntime runtime) {
        for (Long code : this.hexagons.get(runtime.id())) {
            AncapDebug.debug("Ремувим", code);
            this.runtimes.remove(code);
        }
        this.warRuntimes.get(runtime.war()).remove(runtime);
        this.hexagons.remove(runtime.id());
        this.stoppedAttacks.add(runtime.id());
        runtime.setActive(false);
        runtime.waitAPI().delete();
    }
    
    @Override
    public synchronized void putRuntime(Set<Long> hexagons, War war, AssaultRuntime runtime) {
        for (Long code : hexagons) this.runtimes.put(code, runtime);
        this.hexagons.put(runtime.id(), hexagons);
        this.warRuntimes.get(war).add(runtime);
    }

    @Override
    public synchronized void setPeace(long code) {
        if (!this.runtimes.containsKey(code)) return;
        AssaultRuntime runtime = this.runtimes.get(code);
        this.runtimes.remove(code);
        this.hexagons.get(runtime.id()).remove(code);
    }
    
    @Override
    public synchronized void makeIncorporation(WarState inner, WarState outer) {
        // as far as I understand assault is bound not to state, but to castle, and war incorporation is not needed to assaults
    }

    @Override
    public synchronized long maximumCastleHealth() {
        return this.castleHealth;
    }

}