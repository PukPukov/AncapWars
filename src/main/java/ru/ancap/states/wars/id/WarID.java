package ru.ancap.states.wars.id;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.ancap.framework.database.nosql.PathDatabase;
import ru.ancap.states.id.ID;
import ru.ancap.states.wars.api.request.system.Path;
import ru.ancap.states.wars.connector.StateType;

import java.util.UUID;

public class WarID {

    private final PathDatabase idDatabase;

    private static Castle castleId;
    private static State stateId;
    private static War warId;

    public static Castle castle() {
        return castleId;
    }

    public static State state() {
        return stateId;
    }

    public static War war() {
        return warId;
    }

    public WarID(PathDatabase database) {
        this.idDatabase = database;
        castleId = new Castle(this);
        stateId = new State();
        warId = new War(this);
    }
    
    private boolean isBound(String type, String name) {
        return this.idDatabase.isSet(Path.dot(type, name));
    }

    @NotNull
    private String get(String type, String name) {
        String id = this.idDatabase.readString(type+"."+name);
        if (id == null) {
            String generated = UUID.randomUUID().toString();
            bind(type, name, generated);
            return generated;
        } else {
            return id;
        }
    }

    void bind(String type, String name, String id) {
        this.idDatabase.write(type+"."+name, id);
    }

    @AllArgsConstructor
    public static class State {

        public String getWarId(StateType type, String name) {

            return switch (type) {
                case CITY -> ID.getCityID(name);
                case NATION -> ID.getNationID(name);
            };
        }
    }

    public static class Castle extends AbstractTyped {

        public Castle(WarID id) {
            super(id, "castle");
        }

    }

    public static class War extends AbstractTyped {

        public War(WarID id) {
            super(id, "war");
        }

    }

    @AllArgsConstructor
    public abstract static class AbstractTyped {

        private final WarID warID;
        private final String type;

        public boolean isBound(String name) {
            return this.warID.isBound(this.type, name);
        }

        @NotNull
        public String get(String name) {
            return this.warID.get(this.type, name.toLowerCase());
        }

        public void bind(String name, String id) {
            this.warID.bind(this.type, name.toLowerCase(), id);
        }

    }
}
