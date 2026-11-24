package ku_2635674;

/**
 * Student ID: ku_2635674
 * Suite room - values taken from Table 1 of the case study.
 */
public class SuiteRoom extends Room {
    public SuiteRoom(String roomId, BedType bedType) {
        super(roomId, bedType);
    }

    @Override
    public double getDailyRate() {
        return 14000.0;
    }

    @Override
    public boolean hasBreakfast() {
        return true;
    }

    @Override
    public double getExtraCharge() {
        return 2000.0;
    }

    @Override
    public double getTaxRate() {
        return 0.15;
    }

    @Override
    public int getCapacity() {
        return 3;
    }
}
