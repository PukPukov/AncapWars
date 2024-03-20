package ru.ancap.states.wars.lombok;

import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.hexagon.Hexagon;

public interface Exclude {

    interface Remove { void remove(); }
    interface PrepareToDelete { void prepareToDelete(); }
    interface Delete { void delete(); }
    interface GetNeighbors { Hexagon[] getNeighbors(); }
    interface GetDatabase { PathDatabase getDatabase(); }
    
}
