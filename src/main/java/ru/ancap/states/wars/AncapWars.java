package ru.ancap.states.wars;

import com.etsuni.milksplash.MilkSplash;
import com.mrivanplays.conversations.spigot.BukkitConversationManager;
import org.bukkit.Bukkit;
import org.bukkit.Utility;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.database.nosql.ConfigurationDatabase;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.framework.plugin.api.AncapPlugin;
import ru.ancap.hexagon.Hexagon;
import ru.ancap.library.BalanceMessage;
import ru.ancap.states.AncapStates;
import ru.ancap.states.commands.AncapStatesCommand;
import ru.ancap.states.wars.api.field.FieldConflicts;
import ru.ancap.states.wars.api.state.Barrier;
import ru.ancap.states.wars.api.war.AssaultOperator;
import ru.ancap.states.wars.event.AttackDeclareLoadEvent;
import ru.ancap.states.wars.fees.WarFees;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.messaging.info.HereInfoAddon;
import ru.ancap.states.wars.papi.LAPIPAPI;
import ru.ancap.states.wars.plugin.WarMap;
import ru.ancap.states.wars.plugin.config.WarConfig;
import ru.ancap.states.wars.plugin.executor.executors.*;
import ru.ancap.states.wars.plugin.listener.AssaultStatus;
import ru.ancap.states.wars.plugin.listener.AttackCounter;
import ru.ancap.states.wars.plugin.listener.BridgeListener;
import ru.ancap.states.wars.plugin.listener.WarListener;
import ru.ancap.states.wars.spoonfeeding.UnprotectedHexagonsNotification;
import ru.ancap.states.wars.war.WarMilkCleanEffectProvider;
import ru.ancap.states.wars.war.process.restriction.BattleMonitor;

import java.util.List;

public class AncapWars extends AncapPlugin {

    public static final String MESSAGE_DOMAIN = "ru.ancap.states.wars.messages.";
    
    private static AncapPlugin               loaded;
    private static PathDatabase              database;
    private static WarMap                    warMap;
    private static AssaultOperator           assaultOperator;
    private static BukkitConversationManager conversationManager;
    private static AttackCounter             attackCounter;
    private static FieldConflicts            fieldConflicts;
    private static BattleMonitor             battleMonitor;
    public  static WarListener               warListener;

    public static AncapPlugin               loaded              () { return AncapWars.loaded;              }
    public static WarMap                    warMap              () { return AncapWars.warMap;              }
    public static PathDatabase              database            () { return AncapWars.database;            }
    public static BukkitConversationManager conversationManager () { return AncapWars.conversationManager; }
    public static AssaultOperator           assaults            () { return AncapWars.assaultOperator;     }
    public static FieldConflicts            fieldConflicts      () { return AncapWars.fieldConflicts;      }

    public static void setDebug(boolean debug) {
        AncapWars.loaded().ancap().setDebug(debug);
    }

    public static boolean debug() {
        return AncapWars.loaded().ancap().debug();
    }

    public static boolean isUnstable(Hexagon hexagon) {
        AssaultStatus assaultStatus = assaults().assault(hexagon.code()).status();
        if (assaultStatus.politicalState() != AssaultStatus.PoliticalState.PEACE) return true;
        if (fieldConflicts.atFieldConflict(hexagon.code())) return true;
        return false;
    }
    
    public static boolean level0BattleGameplayModificationIn(long hexagonCode, AssaultStatus status) {
        return level1BattleGameplayModificationIn(status) || AncapWars.fieldConflicts.atFieldConflict(hexagonCode);
    }
    
    public static boolean level1BattleGameplayModificationIn(AssaultStatus status) {
        return WarConfig.loaded().globalBattle() || status.politicalState() == AssaultStatus.PoliticalState.BATTLE;
    }
    
    public static boolean dropProtection(long hexagonCode) {
        return !level0BattleGameplayModificationIn(hexagonCode, assaults().assault(hexagonCode).status());
    }
    
    @Utility
    public static AssaultStatus assaultStatus(long hexagonCode) {
        return assaults().assault(hexagonCode).status();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        new WarConfig(this.configuration()).load();
        MilkSplash.effectProvider = new WarMilkCleanEffectProvider();
        AncapWars.loaded = this;
        this.loadLocales();
        AncapWars.database = ConfigurationDatabase.builder()
            .autoSave(10000L)
            .plugin(this)
            .build();
        AncapWars.warMap = new WarMap();
        AncapWars.assaultOperator = new AssaultOperator();
        AncapWars.conversationManager = new BukkitConversationManager(this);
        new WarID(AncapWars.database().inner("id"));
        new LAPIPAPI().register();
        this.loadScheduledTasks();
        this.registerEventsListener(new BridgeListener());
        AncapWars.attackCounter = new AttackCounter(this.ancap()).start();
        AncapWars.fieldConflicts = new FieldConflicts(WarConfig.loaded().getHexagonHealth());
        this.registerEventsListener(AncapWars.assaultOperator);
        AncapWars.battleMonitor = new BattleMonitor(this);
        AncapWars.warListener = new WarListener(
            AncapStates.grid,
            AncapWars.assaultOperator,
            AncapWars.fieldConflicts,
            AncapWars.battleMonitor,
            AncapWars.attackCounter,
            this.ancap().stepbackMaster()
        );
        this.registerEventsListener(AncapWars.warListener);
        this.registerEventsListener(new UnprotectedHexagonsNotification());
        this.commandRegistrar().register("ancap-wars",      new AncapWarsInput(this.localeHandle()));
        this.commandRegistrar().register("war",             new WarCommandExecutor());
        this.commandRegistrar().register("castle",          new CastleCommandExecutor());
        this.commandRegistrar().register("wars",            new WarsCommandExecutor());
        this.commandRegistrar().register("field-conflicts", new FieldConflictsCommandExecutor(AncapWars.fieldConflicts));
        this.commandRegistrar().register("hexagon",         new HexagonInput());
        this.commandRegistrar().register("barrier",         new BarrierCommandExecutor());
        this.loadFeesCommandAddon();
        this.registerInfoAddons();
    }

    private void loadFeesCommandAddon() {
        AncapStatesCommand.feesMessages.addAll(List.of(
            new LAPIMessage(AncapWars.class, "fees.castle-creation", new Placeholder("fee", new BalanceMessage(WarFees.CASTLE_CREATION))),
            new LAPIMessage(AncapWars.class, "fees.castle-attack", new Placeholder("fee", new BalanceMessage(WarFees.CASTLE_ATTACK))),
            new LAPIMessage(AncapWars.class, "fees.devastation-repayment", new Placeholder("fee", new BalanceMessage(WarFees.DEVASTATION_REPAYMENT))),
            new LAPIMessage(AncapWars.class, "fees.core-barrier-attack", new Placeholder("fee", new BalanceMessage(WarFees.CORE_BARRIER_ATTACK)))
        ));
    }

    private void registerInfoAddons() {
        new HereInfoAddon(AncapWars.assaultOperator, AncapWars.fieldConflicts).make();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        AncapWars.database.close();
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void loadScheduledTasks() {
        warMap().barriers().stream()
            .map(Barrier::attackWait)
            .filter(assault -> assault.getTime() != null)
            .filter(assault -> assault.getTime() > System.currentTimeMillis())
            .forEach(assault -> new AttackDeclareLoadEvent(assault).callEvent());
    }

}