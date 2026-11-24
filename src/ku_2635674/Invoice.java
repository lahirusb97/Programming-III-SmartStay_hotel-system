package ku_2635674;

import java.time.LocalDate;

/**
 * Student ID: ku_2635674
 * The final bill for a reservation. It is created only by BillingEngine and
 * cannot be changed afterwards. It contains every item the case study asks
 * for: room details, guest details, dates, nights, guests, extra charges,
 * discount, tax, advance deducted and the final payable amount.
 */
public class Invoice {
    private final String invoiceId;
    private final Reservation reservation;
    private final double baseCharge;
    private final double extraCharge;
    private final double discount;
    private final double tax;
    private final double advanceDeducted;
    private final double finalAmount;

    public Invoice(String invoiceId, Reservation reservation, double baseCharge, double extraCharge,
                   double discount, double tax, double advanceDeducted, double finalAmount) {
        this.invoiceId = invoiceId;
        this.reservation = reservation;
        this.baseCharge = baseCharge;
        this.extraCharge = extraCharge;
        this.discount = discount;
        this.tax = tax;
        this.advanceDeducted = advanceDeducted;
        this.finalAmount = finalAmount;
    }

    public String getInvoiceId() { return invoiceId; }
    public double getBaseCharge() { return baseCharge; }
    public double getExtraCharge() { return extraCharge; }
    public double getDiscount() { return discount; }
    public double getTax() { return tax; }
    public double getAdvanceDeducted() { return advanceDeducted; }
    public double getFinalAmount() { return finalAmount; }

    /** Builds the printable invoice text. */
    public String printInvoice() {
        Room room = reservation.getRoom();
        Guest guest = reservation.getGuest();
        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(56);
        sb.append(line).append('\n');
        sb.append("                SMARTSTAY HOTEL - INVOICE\n");
        sb.append(line).append('\n');
        sb.append(String.format("Invoice No   : %s%nDate         : %s%nBooking ID   : %s%n",
                invoiceId, LocalDate.now(), reservation.getBookingId()));
        sb.append("-".repeat(56)).append('\n');
        sb.append(String.format("GUEST%nName         : %s%nNIC/Passport : %s%nContact      : %s%nEmail        : %s%n",
                guest.getName(), guest.getNicPassport(), guest.getContactNumber(), guest.getEmail()));
        sb.append("-".repeat(56)).append('\n');
        sb.append(String.format("ROOM%nRoom No      : %s%nType         : %s%nBed type     : %s%nBreakfast    : %s%nRate / night : LKR %,.2f%n",
                room.getRoomId(), room.getTypeName(), room.getBedType(),
                room.hasBreakfast() ? "Included" : "Not included", room.getDailyRate()));
        sb.append("-".repeat(56)).append('\n');
        sb.append(String.format("STAY%nCheck-in     : %s%nCheck-out    : %s%nNights       : %d%nGuests       : %d (room capacity %d)%n",
                reservation.getCheckIn(), reservation.getCheckOut(), reservation.getNights(),
                reservation.getNumGuests(), room.getCapacity()));
        sb.append("-".repeat(56)).append('\n');
        sb.append(String.format("%-34s LKR %,12.2f%n", "Base room charge", baseCharge));
        sb.append(String.format("%-34s LKR %,12.2f%n", "Extra guest charge", extraCharge));
        sb.append(String.format("%-34s LKR %,12.2f%n", "Long-stay discount (12%)", -discount));
        sb.append(String.format("%-34s LKR %,12.2f%n",
                String.format("Tax (%.0f%%)", room.getTaxRate() * 100), tax));
        sb.append(String.format("%-34s LKR %,12.2f%n", "Advance deducted", -advanceDeducted));
        sb.append("-".repeat(56)).append('\n');
        String label = finalAmount >= 0 ? "FINAL AMOUNT PAYABLE" : "REFUND DUE TO GUEST";
        sb.append(String.format("%-34s LKR %,12.2f%n", label, Math.abs(finalAmount)));
        sb.append(line);
        return sb.toString();
    }
}
