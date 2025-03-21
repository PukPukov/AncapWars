package ru.ancap.states.wars.plugin.config;

import lombok.AllArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import ru.ancap.commons.debug.AncapDebug;
import ru.ancap.library.Balance;
import ru.ancap.states.wars.AncapWars;

@AllArgsConstructor
public class WarConfig {

    private static WarConfig loaded;
    private final ConfigurationSection base;

    public ConfigurationSection connectionSection() {
        return this.base.getConfigurationSection("connection");
    }

    public static WarConfig loaded() {
        return loaded;
    }

    public void load() {
        loaded = this;
    }

    public Balance getCastleFeeRaising() {
        return new Balance(
                this.base.getDouble("fees.castle.creation.raising.iron", 0),
                this.base.getDouble("fees.castle.creation.raising.diamond", 0),
                this.base.getDouble("fees.castle.creation.raising.netherite", 0)
        );
    }

    public Balance getStartCastleFee() {
        return new Balance(
                this.base.getDouble("fees.castle.creation.start.iron", 0),
                this.base.getDouble("fees.castle.creation.start.diamond", 0),
                this.base.getDouble("fees.castle.creation.start.netherite", 0)
        );
    }

    public Balance getCastleMaintenanceFee() {
        return new Balance(
                this.base.getDouble("fees.castle.maintenance.iron", 0),
                this.base.getDouble("fees.castle.maintenance.diamond", 0),
                this.base.getDouble("fees.castle.maintenance.netherite", 0)
        );
    }

    public long getHexagonHealth() {
        AncapDebug.debug(AncapWars.debug());
        AncapDebug.debug(this.base.getLong("hexagon.health.debug"));
        AncapDebug.debug(this.base.getLong("hexagon.health.normal"));
        return AncapWars.debug() ? this.base.getLong("hexagon.health.debug") : this.base.getLong("hexagon.health.normal");
    }

    public long getCastleHealth() {
        return AncapWars.debug() ? 
            this.base.getLong("castle.health.debug") :
            this.base.getLong("castle.health.normal");
    }

    public int maxCastleHeight() {
        return this.base.getInt("castle.core.max-height");
    }

    public long getAssaultTime() {
        boolean debug = AncapWars.debug();
        AncapDebug.debug("Режьим дебага в конфиге установлен на", debug);
        return debug ? this.base.getLong("castle.assault.time.debug") : this.base.getLong("castle.assault.time.normal");
    }
    
    public boolean globalBattle() {
        return this.base.getBoolean("global-battle");
    }
    
}