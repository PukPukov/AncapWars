package Wars.WarHexagons;

public enum WarHexagonStatus {

    PEACE,
    FIELD_CONFLICT,
    SIEGE_PREPARE,
    KOSTIL_PREBATTLE,
    SIEGE;

    @Deprecated (forRemoval = true)
    public static WarHexagonStatus getStatusForName(String status) {
        if (status == null || status.equalsIgnoreCase("peace")) {
            return PEACE;
        }
        if (status.equalsIgnoreCase("field_conflict")) {
            return FIELD_CONFLICT;
        }
        if (status.equalsIgnoreCase("SIEGE_PREPARE")) {
            return SIEGE_PREPARE;
        }
        if (status.equalsIgnoreCase("KOSTIL_PREBATTLE")) {
            return KOSTIL_PREBATTLE;
        }
        if (status.equalsIgnoreCase("SIEGE")) {
            return SIEGE;
        }
        return null;
    }
}
