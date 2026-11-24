package ku_2635674;

/**
 * Student ID: ku_2635674
 * Availability status of a room (case study: Available, Reserved, Under Maintenance).
 */
public enum RoomStatus {
    AVAILABLE("Available"),
    RESERVED("Reserved"),
    UNDER_MAINTENANCE("Under Maintenance");

    private final String label;

    RoomStatus(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
