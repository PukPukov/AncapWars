package Wars.AncapWars;

import AncapLibrary.Library.AncapLibrary;
import Wars.Commands.CastleCommand.Executor.CastleCommandExecutor;
import Wars.Commands.WarCommand.Executor.WarCommandExecutor;
import Wars.Configuration.AncapWarsConfiguration;
import Wars.Events.CastleCoreBreakEvent.CastleCoreBreakListener;
import Wars.Events.CastleCoreDamageEvent.CastleCoreDamageListener;
import Wars.Events.WarDeclaredEvent.WarDeclareListener;
import Wars.ForbiddenStatementsManagers.EffectsManager;
import Wars.ForbiddenStatementsManagers.InventoryManager;
import Wars.Listeners.TimerListeners.WarHeartbeatListener;
import Wars.Listeners.WarEventsListeners.WarEventsListener;
import Wars.Listeners.WarEventsListeners.WarWorldInteractListener;
import Wars.WarHexagons.WarHexagonalGrid;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarStateMap.WarStateMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import states.Main.AncapStates;
import states.Player.AncapStatesPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AncapWars extends JavaPlugin {

    private static AncapWars INSTANCE;
    private static AncapWarsConfiguration CONFIGURATION;
    private static InventoryManager INVENTORY_MANAGER;
    private static EffectsManager EFFECTS_MANAGER;
    private static WarHexagonalGrid GRID;
    private static WarStateMap WAR_STATE_MAP;

    private static final List<Listener> eventsListeners = new ArrayList(Arrays.asList(
            new CastleCoreBreakListener(),
            new WarDeclareListener(),
            new CastleCoreDamageListener(),
            new WarEventsListener(),
            new WarHeartbeatListener(),
            new WarWorldInteractListener()
    ));

    public static void sendSound(Sound sound) {
        for (AncapStatesPlayer player : AncapStates.getPlayerMap().getOnlinePlayers()) {
            player.getPlayer().playSound(player.getLocation(), sound, 1000F, 0.1F);
        }
    }

    @Override
    public void onEnable() {
        this.setInstance();
        this.loadConfig();
        this.loadConfiguration();
        this.loadWarManagers();
        this.loadGrid();
        this.loadWarStateMap();
        this.registerCommands();
        this.loadListeners();
    }

    private void loadListeners() {
        for (Listener listener : this.eventsListeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void loadConfig() {
        this.saveDefaultConfig();
    }

    private void registerCommands() {
        this.registerCommand("castle", new CastleCommandExecutor());
        this.registerCommand("war", new WarCommandExecutor());
    }

    private void registerCommand(String commandName, CommandExecutor executor) {
        this.getServer().getPluginCommand(commandName).setExecutor(executor);
    }

    private void loadConfiguration() {
        CONFIGURATION = new AncapWarsConfiguration(this.getConfig());
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void loadWarStateMap() {
        WAR_STATE_MAP = new WarStateMap();
        WAR_STATE_MAP.loadFromDb(AncapLibrary.getConfiguredDatabase());
    }

    private void loadGrid() {
        GRID = new WarHexagonalGrid(AncapStates.getInstance().getGrid());
    }

    private void loadWarManagers() {
        INVENTORY_MANAGER = new InventoryManager();
        EFFECTS_MANAGER = new EffectsManager();
    }

    public static AncapWars getInstance() {
        return INSTANCE;
    }

    public static InventoryManager getInventoryManager() {
        return INVENTORY_MANAGER;
    }

    public static EffectsManager getEffectsManager() {
        return EFFECTS_MANAGER;
    }

    public static WarHexagonalGrid getGrid() {
        return GRID;
    }

    public static WarStateMap getWarStateMap() {
        return WAR_STATE_MAP;
    }

    public static AncapWarsConfiguration getConfiguration() {
        return CONFIGURATION;
    }

    @Deprecated
    public static List<String> getWarsStateTypesNames() {
        return List.of(new String[]{"nation", "city"});
    }

    @Deprecated
    public static AncapWarrior[] getOnlinePlayers() {
        AncapStatesPlayer[] players = AncapStates.getPlayerMap().getOnlinePlayers();
        AncapWarrior[] warriors = new AncapWarrior[players.length];
        for (int i = 0; i<players.length; i++) {
            warriors[i] = new AncapWarrior(players[i]);
        }
        return warriors;
    }
}
