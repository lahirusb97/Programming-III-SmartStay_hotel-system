package ku_2635674;

/**
 * Student ID: ku_2635674
 * Standard room - values taken from Table 1 of the case study.
 */
public class StandardRoom extends Room {
    public StandardRoom(String roomId, BedType bedType) {
        super(roomId, bedType);
    }

    @Override
    public double getDailyRate() {
        return 6000.0;
    }

    @Override
    public boolean hasBreakfast() {
        return false;
    }

    @Override
    public double getExtraCharge() {
        return 1000.0;
    }

    @Override
    public double getTaxRate() {
        return 0.10;
    }

    @Override
    public int getCapacity() {
        return 2;
    }
}
