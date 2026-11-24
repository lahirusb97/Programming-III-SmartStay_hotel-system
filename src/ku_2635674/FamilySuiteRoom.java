package ku_2635674;

/**
 * Student ID: ku_2635674
 * Family Suite room - values taken from Table 1 of the case study.
 */
public class FamilySuiteRoom extends Room {
    public FamilySuiteRoom(String roomId, BedType bedType) {
        super(roomId, bedType);
    }

    @Override
    public double getDailyRate() {
        return 18000.0;
    }

    @Override
    public boolean hasBreakfast() {
        return true;
    }

    @Override
    public double getExtraCharge() {
        return 2500.0;
    }

    @Override
    public double getTaxRate() {
        return 0.15;
    }

    @Override
    public int getCapacity() {
        return 5;
    }
}
