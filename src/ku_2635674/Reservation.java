package ku_2635674;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Student ID: ku_2635674
 * One booking made by a guest for one room. Holds the booking data and the
 * rules about when it may be changed or cancelled.
 */
public class Reservation {
    /** Refundable advance payment required by the case study (LKR). */
    public static final double ADVANCE = 10000.0;
    /** Hotel check-in time used for the 24-hour modify/cancel rule. */
    public static final int CHECK_IN_HOUR = 14;

    public enum Status { CONFIRMED, CANCELLED }

    private final String bookingId;
    private final Guest guest;
    private final Room room;
    private final LocalDate bookingDate;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int numGuests;
    private final double advance;
    private Status status = Status.CONFIRMED;
    /** Composition: the invoice is part of this booking and is removed when the booking changes or is cancelled. */
    private Invoice invoice;

    public Reservation(String bookingId, Guest guest, Room room, LocalDate bookingDate,
                       LocalDate checkIn, LocalDate checkOut, int numGuests) {
        this.bookingId = bookingId;
        this.guest = guest;
        this.room = room;
        this.bookingDate = bookingDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numGuests = numGuests;
        this.advance = ADVANCE;
    }

    /** Number of nights = days between check-in and check-out. */
    public int getNights() {
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /** True when it is still more than 24 hours before check-in and the booking is active. */
    public boolean isCancellable(LocalDateTime now) {
        return status == Status.CONFIRMED
                && now.isBefore(checkIn.atTime(CHECK_IN_HOUR, 0).minusHours(24));
    }

    public void modify(LocalDate newCheckIn, LocalDate newCheckOut, int newGuests) {
        this.checkIn = newCheckIn;
        this.checkOut = newCheckOut;
        this.numGuests = newGuests;
        this.invoice = null; // the old invoice is no longer correct
    }

    public void cancel() {
        this.status = Status.CANCELLED;
        this.invoice = null; // a cancelled booking has no invoice
    }

    public boolean isActive() {
        return status == Status.CONFIRMED;
    }

    /** True when this booking's stay overlaps [in, out). Check-out day is free for the next guest. */
    public boolean overlaps(LocalDate in, LocalDate out) {
        return in.isBefore(checkOut) && checkIn.isBefore(out);
    }

    public String getBookingId() { return bookingId; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public LocalDate getBookingDate() { return bookingDate; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getNumGuests() { return numGuests; }
    public double getAdvance() { return advance; }
    public Status getStatus() { return status; }
    public Invoice getInvoice() { return invoice; }

    /** Package-private: only BillingEngine attaches the invoice it creates. */
    void attachInvoice(Invoice invoice) { this.invoice = invoice; }

    @Override
    public String toString() {
        return String.format("%-7s %-18s Room %-5s %-15s %s -> %s (%d nights, %d guests) %s",
                bookingId, guest.getName(), room.getRoomId(), room.getTypeName(),
                checkIn, checkOut, getNights(), numGuests, status);
    }
}
