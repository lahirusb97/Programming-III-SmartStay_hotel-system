package ku_2635674;

/**
 * Student ID: ku_2635674
 * Abstract room. The data every room has (ID, bed type, status) lives here;
 * the values that depend on the room type (rate, breakfast, extra guest
 * charge, tax rate, capacity) are abstract and are supplied by the four
 * subclasses, exactly as in Table 1 of the case study.
 */
public abstract class Room {
    private final String roomId;
    private BedType bedType;
    private RoomStatus status = RoomStatus.AVAILABLE;

    protected Room(String roomId, BedType bedType) {
        if (roomId == null || roomId.trim().isEmpty()) {
            throw new IllegalArgumentException("Room ID is required.");
        }
        if (bedType == null) {
            throw new IllegalArgumentException("Bed type is required.");
        }
        this.roomId = roomId.trim().toUpperCase();
        this.bedType = bedType;
    }

    // ---- values that depend on the room type (Table 1) ----
    public abstract double getDailyRate();

    public abstract boolean hasBreakfast();

    public abstract double getExtraCharge();

    public abstract double getTaxRate();

    public abstract int getCapacity();

    // ---- behaviour shared by every room ----
    public boolean isAvailable() {
        return status == RoomStatus.AVAILABLE;
    }

    /** "StandardRoom" -> "Standard", "FamilySuiteRoom" -> "Family Suite". */
    public String getTypeName() {
        return getClass().getSimpleName().replace("Room", "").replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    public String getRoomId() {
        return roomId;
    }

    public BedType getBedType() {
        return bedType;
    }

    public void setBedType(BedType bedType) {
        this.bedType = bedType;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required.");
        }
        this.status = status;
    }
}
