package ku_2635674;

/**
 * Student ID: ku_2635674
 * Calculates the bill in the exact order given in the case study:
 * base charge -> extra guest charge -> discount -> tax -> deduct advance.
 * All the rates are constants so that they can be changed in one place.
 */
public class BillingEngine {
    public static final double DISCOUNT_RATE = 0.12;
    public static final int DISCOUNT_NIGHTS = 5;

    private int invoiceCounter = 0;

    /** Step 1: base charge = nightly rate x number of nights. */
    public double calcBaseCharge(Room room, int nights) {
        return room.getDailyRate() * nights;
    }

    /** Step 2: guests above the room capacity pay the extra charge per guest per night. */
    public double calcExtraCharge(Room room, int guests, int nights) {
        int extraGuests = Math.max(0, guests - room.getCapacity());
        return extraGuests * room.getExtraCharge() * nights;
    }

    /** Step 3: 12% off the BASE room charge (before tax) for 5 or more nights. */
    public double applyDiscount(double baseCharge, int nights) {
        return nights >= DISCOUNT_NIGHTS ? baseCharge * DISCOUNT_RATE : 0.0;
    }

    /** Step 4: tax is charged on (base - discount + extra) at the room type's rate. */
    public double calcTax(Room room, double taxableAmount) {
        return taxableAmount * room.getTaxRate();
    }

    /** Step 5 happens here: advance is deducted to give the final payable amount. */
    public Invoice generateInvoice(Reservation r) {
        if (!r.isActive()) {
            throw new IllegalStateException("Cancelled bookings have no invoice.");
        }
        Room room = r.getRoom();
        int nights = r.getNights();
        double base = calcBaseCharge(room, nights);
        double extra = calcExtraCharge(room, r.getNumGuests(), nights);
        double discount = applyDiscount(base, nights);
        double taxable = base - discount + extra;
        double tax = calcTax(room, taxable);
        double finalAmount = taxable + tax - r.getAdvance();
        invoiceCounter++;
        Invoice invoice = new Invoice(String.format("INV%04d", invoiceCounter), r,
                base, extra, discount, tax, r.getAdvance(), finalAmount);
        r.attachInvoice(invoice); // composition: the reservation owns its invoice
        return invoice;
    }
}
