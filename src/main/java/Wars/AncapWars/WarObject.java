package Wars.AncapWars;

import Wars.WarStates.WarState;

import java.util.List;

public interface WarObject {

    int getRang();

    List<WarObject> getLowerRangObjects();

    WarObject getParentObject();

    default WarState getHighestWarState() {
        WarObject object = this.getHighestWarObject();
        if (object instanceof WarState) {
            return (WarState) object;
        }
        return null;
    }

    default WarObject getHighestWarObject() {
        WarObject object = this;
        while (true) {
            try {
                WarObject parent = object.getParentObject();
                if (parent == null) {
                    return object;
                }
                object = parent;
            } catch (WarObjectRangException e) {
                return object;
            }
        }
    }
}
