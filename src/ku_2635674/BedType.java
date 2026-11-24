package ku_2635674;

/**
 * Student ID: ku_2635674
 * Allowed bed types for a room (case study: Single, Double, Queen, King).
 * An enum is used so that no other value can ever be stored.
 */
public enum BedType {
    SINGLE("Single"),
    DOUBLE("Double"),
    QUEEN("Queen"),
    KING("King");

    private final String label;

    BedType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
