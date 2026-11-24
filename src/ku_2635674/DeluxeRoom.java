package ku_2635674;

/**
 * Student ID: ku_2635674
 * Deluxe room - values taken from Table 1 of the case study.
 */
public class DeluxeRoom extends Room {
    public DeluxeRoom(String roomId, BedType bedType) {
        super(roomId, bedType);
    }

    @Override
    public double getDailyRate() {
        return 9000.0;
    }

    @Override
    public boolean hasBreakfast() {
        return true;
    }

    @Override
    public double getExtraCharge() {
        return 1500.0;
    }

    @Override
    public double getTaxRate() {
        return 0.12;
    }

    @Override
    public int getCapacity() {
        return 2;
    }
}
