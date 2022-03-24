package Wars.AncapWars;

import AncapLibrary.Library.AncapLibrary;
import Wars.Configuration.AncapWarsConfiguration;
import Wars.ForbiddenStatementsManagers.EffectsManager;
import Wars.ForbiddenStatementsManagers.InventoryManager;
import Wars.WarHexagons.WarHexagonalGrid;
import Wars.WarPlayers.AncapWarrior;
import Wars.WarStates.WarStateMap.WarStateMap;
import org.bukkit.plugin.java.JavaPlugin;
import states.Player.AncapStatesPlayer;
import states.Main.AncapStates;

import java.util.List;

public class AncapWars extends JavaPlugin {

    private static AncapWars INSTANCE;
    private static AncapWarsConfiguration CONFIGURATION;
    private static InventoryManager INVENTORY_MANAGER;
    private static EffectsManager EFFECTS_MANAGER;
    private static WarHexagonalGrid GRID;
    private static WarStateMap WAR_STATE_MAP;

    @Override
    public void onEnable() {
        this.setInstance();
        this.loadConfiguration();
        this.loadWarManagers();
        this.loadGrid();
        this.loadWarStateMap();
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
