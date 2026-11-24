package ku_2635674;

import java.time.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Student ID: ku_2635674
 * Self-contained test runner (no external library). Each test case has an ID
 * that matches the test case table in the report. The clock is fixed so that
 * the date rules give the same result on every run.
 */
public class TestRunner {
    static int passed = 0, failed = 0;
    static final Clock START = Clock.fixed(Instant.parse("2026-11-16T04:30:00Z"), ZoneId.of("UTC"));
    /** Clock whose time can be moved forward inside a test (to test the 24-hour rule). */
    static class MovableClock extends Clock {
        Instant now = START.instant();
        public ZoneId getZone() { return ZoneId.of("UTC"); }
        public Clock withZone(ZoneId z) { return this; }
        public Instant instant() { return now; }
    }
    static MovableClock CLOCK = new MovableClock();
    static final LocalDate TODAY = LocalDate.now(CLOCK);

    static RoomManager rm;
    static GuestManager gm;
    static ReservationManager resm;
    static BillingEngine be;
    static Guest guest;

    static void setup() {
        CLOCK.now = START.instant();
        rm = new RoomManager();
        gm = new GuestManager();
        resm = new ReservationManager(rm, CLOCK);
        be = new BillingEngine();
        MainMenu.loadSampleRooms(rm);
        guest = gm.register("Nimal Perera", "0771234567", "nimal@mail.com", "199012345678");
    }

    static void check(String id, String description, String expected, Supplier<String> action) {
        setup();
        String actual;
        try {
            actual = action.get();
        } catch (RuntimeException e) {
            actual = "Rejected: " + e.getMessage();
        }
        boolean ok = actual.equals(expected) || (expected.endsWith("*") && actual.startsWith(expected.substring(0, expected.length() - 1)));
        if (ok) passed++; else failed++;
        System.out.printf("%-6s %-4s %-52s%n       Expected: %s%n       Actual  : %s%n", id, ok ? "PASS" : "FAIL", description, expected, actual);
    }

    static Invoice book(String room, int nights, int guests) {
        Reservation r = resm.makeReservation(guest, rm.findRoom(room), TODAY.plusDays(10), TODAY.plusDays(10 + nights), guests);
        return be.generateInvoice(r);
    }

    static String money(double v) { return String.format("%,.2f", v); }

    public static void main(String[] args) {
        System.out.println("SmartStay test run - fixed date " + TODAY + "\n");

        check("TC01", "Register guest with valid details", "Registered 1 guest",
            () -> { setup(); return "Registered " + gm.count() + " guest"; });
        check("TC02", "Register guest with invalid email", "Rejected: Email address is not valid.",
            () -> { gm.register("Kamal", "0771111111", "kamal-at-mail", "199112345678"); return "Registered"; });
        check("TC03", "Register duplicate NIC", "Rejected: A guest with this NIC/passport is already registered.",
            () -> { gm.register("Other", "0772222222", "o@mail.com", "199012345678"); return "Registered"; });
        check("TC04", "Admin adds a new room", "Rooms now: 7",
            () -> { rm.addRoom(new SuiteRoom("U302", BedType.KING)); return "Rooms now: " + rm.viewAllRooms().size(); });
        check("TC05", "Admin adds duplicate room number", "Rejected: Room S101 already exists.",
            () -> { rm.addRoom(new StandardRoom("S101", BedType.SINGLE)); return "Added"; });
        check("TC06", "Admin updates bed type", "KING",
            () -> { rm.updateRoom("S101", BedType.KING); return rm.findRoom("S101").getBedType().name(); });
        check("TC07", "Admin changes status to Under Maintenance", "Under Maintenance",
            () -> { rm.setStatus("S101", RoomStatus.UNDER_MAINTENANCE); return rm.findRoom("S101").getStatus().toString(); });
        check("TC08", "Admin removes a room", "Rooms now: 5",
            () -> { rm.removeRoom("S101"); return "Rooms now: " + rm.viewAllRooms().size(); });
        check("TC09", "Search available Deluxe rooms", "D201,D202",
            () -> { StringBuilder sb = new StringBuilder();
                for (Room r : resm.searchRooms("Deluxe", TODAY.plusDays(10), TODAY.plusDays(12))) sb.append(sb.length() > 0 ? "," : "").append(r.getRoomId());
                return sb.toString(); });
        check("TC10", "Search skips room under maintenance", "S102",
            () -> { rm.setStatus("S101", RoomStatus.UNDER_MAINTENANCE);
                return resm.searchRooms("Standard", TODAY.plusDays(10), TODAY.plusDays(12)).get(0).getRoomId(); });
        check("TC11", "Valid reservation, 3 nights", "BK0001 advance 10,000.00",
            () -> { Reservation r = resm.makeReservation(guest, rm.findRoom("D201"), TODAY.plusDays(10), TODAY.plusDays(13), 2);
                return r.getBookingId() + " advance " + money(r.getAdvance()); });
        check("TC12", "Book room under maintenance", "Rejected: Room S101 is Under Maintenance - only Available rooms can be booked.",
            () -> { rm.setStatus("S101", RoomStatus.UNDER_MAINTENANCE); book("S101", 2, 2); return "Booked"; });
        check("TC13", "Book less than 2 days ahead", "Rejected: Bookings must be made at least 2 days before check-in.",
            () -> { resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(1), TODAY.plusDays(3), 1); return "Booked"; });
        check("TC14", "Book exactly 2 days ahead (boundary)", "BK0001",
            () -> resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(2), TODAY.plusDays(4), 1).getBookingId());
        check("TC15", "Check-out before check-in", "Rejected: Check-out must be after check-in.",
            () -> { resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(10), TODAY.plusDays(8), 1); return "Booked"; });
        check("TC16", "Overlapping dates on same room", "Rejected: Room S101 is already booked for those dates.",
            () -> { book("S101", 3, 1); resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(12), TODAY.plusDays(15), 1); return "Booked"; });
        check("TC17", "Back-to-back stay allowed (check-out day = next check-in)", "BK0002",
            () -> { book("S101", 3, 1); return resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(13), TODAY.plusDays(15), 1).getBookingId(); });
        check("TC18", "Too many guests for the room", "Rejected: Number of guests must be between 1 and 4 for this room.",
            () -> { book("S101", 2, 5); return "Booked"; });

        // Billing - hand calculated values (see report)
        check("TC19", "Bill: Standard, 2 nights, 2 guests", "base 12,000.00 extra 0.00 disc 0.00 tax 1,200.00 final 3,200.00",
            () -> fmt(book("S101", 2, 2)));
        check("TC20", "Bill: Deluxe, 3 nights, 3 guests (1 extra)", "base 27,000.00 extra 4,500.00 disc 0.00 tax 3,780.00 final 25,280.00",
            () -> fmt(book("D201", 3, 3)));
        check("TC21", "Bill: Suite, 5 nights - discount applies (boundary)", "base 70,000.00 extra 0.00 disc 8,400.00 tax 9,240.00 final 60,840.00",
            () -> fmt(book("U301", 5, 2)));
        check("TC22", "Bill: Standard, 4 nights - no discount (boundary)", "base 24,000.00 extra 0.00 disc 0.00 tax 2,400.00 final 16,400.00",
            () -> fmt(book("S101", 4, 2)));
        check("TC23", "Bill: Family Suite, 6 nights, 7 guests (2 extra)", "base 108,000.00 extra 30,000.00 disc 12,960.00 tax 18,756.00 final 133,796.00",
            () -> fmt(book("F401", 6, 7)));
        check("TC24", "Invoice contains all required fields", "true",
            () -> { String t = book("D201", 3, 3).printInvoice();
                String[] must = {"Nimal Perera", "D201", "Deluxe", "Queen", "Check-in", "Check-out", "Nights", "Guests", "Extra guest", "discount", "Tax (12%)", "Advance deducted", "FINAL AMOUNT PAYABLE"};
                for (String m : must) if (!t.contains(m)) return "missing " + m;
                return "true"; });

        // Cancellation
        check("TC25", "Cancel booking made well in advance", "CANCELLED",
            () -> { Reservation r = resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(10), TODAY.plusDays(12), 1);
                resm.cancelReservation(r.getBookingId()); return r.getStatus().name(); });
        check("TC26", "Cancel within 24 hours of check-in", "Rejected: This booking cannot be cancelled (already cancelled or within 24 hours of check-in).",
            () -> { Reservation r = resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(2), TODAY.plusDays(4), 1);
                CLOCK.now = Instant.parse("2026-11-17T20:00:00Z"); // 18 h before check-in
                resm.cancelReservation(r.getBookingId()); return "CANCELLED"; });
        check("TC27", "Cancelled room becomes free again", "S101",
            () -> { Reservation r = resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(10), TODAY.plusDays(12), 1);
                resm.cancelReservation(r.getBookingId());
                return resm.searchRooms("Standard", TODAY.plusDays(10), TODAY.plusDays(12)).get(0).getRoomId(); });
        check("TC28", "Modify within 24 hours is refused", "Rejected: A booking cannot be modified within 24 hours of check-in.",
            () -> { Reservation r = resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(2), TODAY.plusDays(4), 1);
                CLOCK.now = Instant.parse("2026-11-17T20:00:00Z");
                resm.modifyReservation(r.getBookingId(), TODAY.plusDays(3), TODAY.plusDays(5), 1); return "Modified"; });

        // Searching / sorting
        check("TC29", "Search reservation by guest name (partial, any case)", "1",
            () -> { book("S101", 2, 1); return String.valueOf(resm.searchByGuestName("nimal").size()); });
        check("TC30", "Search reservation by booking ID", "BK0001",
            () -> { book("S101", 2, 1); return resm.findById("bk0001").getBookingId(); });
        check("TC31", "Unknown booking ID returns nothing", "null",
            () -> String.valueOf(resm.findById("BK9999")));
        check("TC32", "View reservations by check-in date", "BK0002",
            () -> { resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(20), TODAY.plusDays(22), 1);
                resm.makeReservation(guest, rm.findRoom("S102"), TODAY.plusDays(10), TODAY.plusDays(12), 1);
                return resm.viewByCheckIn(TODAY.plusDays(10)).get(0).getBookingId(); });
        check("TC33", "Insertion sort orders by check-in (earliest first)", "BK0002,BK0003,BK0001",
            () -> { resm.makeReservation(guest, rm.findRoom("S101"), TODAY.plusDays(20), TODAY.plusDays(22), 1);
                resm.makeReservation(guest, rm.findRoom("S102"), TODAY.plusDays(5), TODAY.plusDays(7), 1);
                resm.makeReservation(guest, rm.findRoom("D201"), TODAY.plusDays(9), TODAY.plusDays(11), 1);
                StringBuilder sb = new StringBuilder();
                for (Reservation r : resm.sortedByCheckIn()) sb.append(sb.length() > 0 ? "," : "").append(r.getBookingId());
                return sb.toString(); });
        check("TC34", "Polymorphism: each room type returns its own rate", "6000,9000,14000,18000",
            () -> { setup(); return (int) rm.findRoom("S101").getDailyRate() + "," + (int) rm.findRoom("D201").getDailyRate()
                    + "," + (int) rm.findRoom("U301").getDailyRate() + "," + (int) rm.findRoom("F401").getDailyRate(); });
        check("TC35", "Polymorphism: getRole() differs for Guest/Administrator", "Guest,Administrator",
            () -> { Person a = guest; Person b = new Administrator("A1", "Admin", "0112345678", "a@a.lk"); return a.getRole() + "," + b.getRole(); });
        check("TC36", "Composition: invoice belongs to its reservation, removed on cancel", "INV attached,removed on cancel",
            () -> { setup(); Reservation r = resm.makeReservation(guest, rm.findRoom("D201"), TODAY.plusDays(10), TODAY.plusDays(13), 2);
                Invoice inv = new BillingEngine().generateInvoice(r);
                String first = (r.getInvoice() == inv) ? "INV attached" : "not attached";
                resm.cancelReservation(r.getBookingId());
                return first + "," + (r.getInvoice() == null ? "removed on cancel" : "still there"); });
        check("TC37", "Dependency: Administrator manages rooms through RoomManager", "Rooms now: 7, U302 KING Under Maintenance",
            () -> { setup(); Administrator admin = new Administrator("A1", "Admin", "0112345678", "a@a.lk");
                admin.addRoom(rm, new SuiteRoom("U302", BedType.QUEEN));
                admin.updateRoom(rm, "U302", BedType.KING);
                admin.changeRoomStatus(rm, "U302", RoomStatus.UNDER_MAINTENANCE);
                Room u = rm.findRoom("U302");
                return "Rooms now: " + admin.viewAllRooms(rm).size() + ", U302 " + u.getBedType().name() + " " + u.getStatus(); });

        System.out.printf("%nRESULT: %d passed, %d failed, %d total%n", passed, failed, passed + failed);
        if (failed > 0) System.exit(1);
    }

    static String fmt(Invoice i) {
        return "base " + money(i.getBaseCharge()) + " extra " + money(i.getExtraCharge()) + " disc " + money(i.getDiscount())
                + " tax " + money(i.getTax()) + " final " + money(i.getFinalAmount());
    }
}
