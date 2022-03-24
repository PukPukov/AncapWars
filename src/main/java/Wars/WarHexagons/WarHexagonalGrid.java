package Wars.WarHexagons;

import Wars.Location.WarLocation;
import library.HexagonalGrid;
import library.Morton64;
import library.Orientation;
import library.Point;
import states.Hexagons.AncapHexagonalGrid;

public class WarHexagonalGrid extends AncapHexagonalGrid {

    public WarHexagonalGrid(Orientation orientation, Point origin, Point size, Morton64 mort) {
        super(orientation, origin, size, mort);
    }

    public WarHexagonalGrid(HexagonalGrid grid) {
        super(grid.getOrientation(), grid.getOrigin(), grid.getSize(), grid.getMort());
    }

    public WarHexagon getHexagon(WarLocation loc) {
        return new WarHexagon(super.getHexagon(new Point(loc.getX(), loc.getZ())));
    }
}
