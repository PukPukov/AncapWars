package Wars.WarPlayers;

import Wars.AncapWars.AncapWars;
import Wars.AncapWars.WarObject;
import Wars.AncapWars.WarObjectRangException;
import Wars.ForbiddenStatementsManagers.ForbiddenStatementsThread;
import Wars.Location.WarLocation;
import Wars.WarHexagons.WarHexagon;
import Wars.WarStates.WarCities.WarCity;
import Wars.WarStates.WarState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import states.Player.AncapStatesPlayer;

import java.util.List;

public class AncapWarrior extends AncapStatesPlayer implements WarObject {

    private WarState parent;

    @Deprecated
    public AncapWarrior(String string) {
        super(string);
    }

    @Deprecated
    public AncapWarrior(Player player) {
        super(player);
    }

    public AncapWarrior(AncapStatesPlayer player) {
        this(player.getID());
    }

    @Override
    public WarHexagon getHexagon() {
        return new WarHexagon(super.getHexagon());
    }

    public void prepareToWar() {
        ForbiddenStatementsThread thread = new ForbiddenStatementsThread(this);
        thread.start();
    }

    public void clearForbiddenOnWarsItems() {
        Player p = this.getPlayer();
        PlayerInventory inventory = p.getInventory();
        ItemStack[] armor = inventory.getArmorContents();
        ItemStack[] items = inventory.getContents();
        armor = AncapWars.getInventoryManager().getClearedItemStacks(armor);
        items = AncapWars.getInventoryManager().getClearedItemStacks(items);
        inventory.setArmorContents(armor);
        inventory.setContents(items);
    }

    public void clearForbiddenEffects() {
        Player p = this.getPlayer();
        PotionEffect[] effects = p.getActivePotionEffects().toArray(new PotionEffect[0]);
        PotionEffect[] effectsToRemove = AncapWars.getEffectsManager().getEffectsToRemoveFrom(effects);
        for (PotionEffect effect : effectsToRemove) {
            p.removePotionEffect(effect.getType());
        }
    }

    public boolean isWarStateLeader() {
        return this.getHighestWarState().getLeader().equals(this);
    }

    public boolean isWarStateMinister() {
        return this.getHighestWarState().isMinister(this);
    }

    public boolean canDeclareWars() {
        return this.isWarStateLeader();
    }

    public boolean canOfferPeace() {
        return this.isWarStateMinister();
    }

    public boolean canAttack(WarHexagon hexagon) {
        return false;
    }

    public boolean canEnter(WarHexagon hexagon) {
        return true;
    }

    public void pushOut() {

    }

    public void createCastle(String name) {
        WarCity city = this.getWarCity();
        city.createCastle(this.getWarLocation(), name);
    }

    // Переписать
    public WarLocation getWarLocation() {
        return new WarLocation(this.getPlayer().getLocation());
    }

    @Override
    public int getRang() {
        return 0;
    }

    @Override
    public List<WarObject> getLowerRangObjects() {
        throw new WarObjectRangException("AncapWarrior has no lower-rang objects!");
    }

    @Override
    public WarObject getParentObject() {
        return this.parent;
    }

    public WarCity getWarCity() {
        return (WarCity) this.parent;
    }
}
