package ru.ancap.states.wars.api.castle;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ancap.framework.communicate.message.CallableMessage;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.library.Balance;
import ru.ancap.states.AncapStates;
import ru.ancap.states.name.Name;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.castle.exception.CastleNameOccupiedException;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.assault.AttackWait;
import ru.ancap.states.wars.fees.WarFees;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.messaging.Message;
import ru.ancap.states.wars.plugin.config.WarConfig;
import ru.ancap.states.wars.plugin.listener.AssaultRuntime;
import ru.ancap.states.wars.utils.LAPIReceiver;

import javax.naming.InvalidNameException;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
public class BuiltCastle extends Castle {

    private final String id;
    @EqualsAndHashCode.Exclude @ToString.Exclude
    private final PathDatabase database;

    @EqualsAndHashCode.Exclude @ToString.Exclude
    private final Function<String, String> lower = String::toLowerCase;

    public BuiltCastle(String id) {
        this.id = id;
        this.database = AncapWars.database().inner("castles."+id);
    }

    @Override
    public void initialize(String creator, Location location, String name) {
        super.initialize(creator, location, name);
        this.database.write("name", name);
        WarHexagon hexagon = new WarHexagon(AncapStates.grid.hexagon(location).code());
        hexagon.setCastle(this);
    }

    public void rename(String newName) throws CastleNameOccupiedException, InvalidNameException {
        if (!Name.canBeDefinedWith(newName)) {
            throw new InvalidNameException();
        }
        if (new BuiltCastle(WarID.castle().get(newName)).exists()) {
            throw new CastleNameOccupiedException(newName);
        }
        this.database.write("name", newName);
        WarID.castle().bind(newName, this.getId());
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public int protectLevel() {
        return 2;
    }

    @Override
    public Balance attackFee() {
        return WarFees.CASTLE_ATTACK;
    }

    @Override
    public CallableMessage attackMessage(WarState attacker, WarState defender) {
        return new LAPIMessage(AncapWars.class, "assault.declare",
            new Placeholder("attacker", attacker.getName()),
            new Placeholder("defender", defender.getName()),
            new Placeholder("name", this.name())
        );
    }

    @Override
    public void acceptTimedAttack(AttackWait wait, AssaultRuntime prepareRuntime, List<WarHexagon> toSet) {
        AncapWars.assaults().acceptAssault(wait, prepareRuntime, toSet);
        Bukkit.getOnlinePlayers().forEach(player -> LAPIReceiver.send(
            Message.Minecraft.Notify.Assault.START, player,
            "%STATE%",  prepareRuntime.attacker().getName(),
            "%NAME%",   this.name(),
            "%TARGET%", wait.getBarrier().owner().getName()
        ));
    }

    @Override
    public @NotNull AttackWait attackWait() {
        return super.attackWait();
    }

    @Override
    public Location location() {
        Location location = this.getLocation();
        return new Location(location.getWorld(), location.getX(), Math.max(WarConfig.loaded().maxCastleHeight(), this.getLocation().getY()), location.getY());
    }

    @Override
    public PathDatabase database() {
        return this.getDatabase();
    }

    @Override
    public @Nullable CallableMessage deleteMessage(WarState destroyer) {
        return new LAPIMessage(Message.Minecraft.Notify.Assault.DESTROY,
            new Placeholder("name", this.name()),
            new Placeholder("state", destroyer.getName())
        );
    }

}
