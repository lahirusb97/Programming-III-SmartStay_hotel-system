package ku_2635674;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Student ID: ku_2635674
 * Entry point of the SmartStay system. main() creates the manager objects,
 * loads some sample rooms and then runs the console menus. The menu classes
 * only read input and print output; every rule lives in the manager classes.
 */
public class MainMenu {
    private final GuestManager guestManager;
    private final RoomManager roomManager;
    private final ReservationManager reservationManager;
    private final BillingEngine billingEngine = new BillingEngine();
    private final Administrator admin =
            new Administrator("ADM001", "Hotel Admin", "0112345678", "admin@smartstay.lk");
    private final Scanner in;
    private final PrintStream out;

    public MainMenu(GuestManager gm, RoomManager rm, ReservationManager resm, InputStream input, PrintStream output) {
        this.guestManager = gm;
        this.roomManager = rm;
        this.reservationManager = resm;
        this.in = new Scanner(input);
        this.out = output;
    }

    public static void main(String[] args) {
        GuestManager gm = new GuestManager();
        RoomManager rm = new RoomManager();
        ReservationManager resm = new ReservationManager(rm, Clock.systemDefaultZone());
        loadSampleRooms(rm);
        new MainMenu(gm, rm, resm, System.in, System.out).showMainMenu();
    }

    /** Sample inventory so the system can be demonstrated straight away. */
    public static void loadSampleRooms(RoomManager rm) {
        rm.addRoom(new StandardRoom("S101", BedType.SINGLE));
        rm.addRoom(new StandardRoom("S102", BedType.DOUBLE));
        rm.addRoom(new DeluxeRoom("D201", BedType.QUEEN));
        rm.addRoom(new DeluxeRoom("D202", BedType.KING));
        rm.addRoom(new SuiteRoom("U301", BedType.KING));
        rm.addRoom(new FamilySuiteRoom("F401", BedType.QUEEN));
    }

    // ---------------------------------------------------------------- menus

    public void showMainMenu() {
        while (true) {
            out.println("\n========== SMARTSTAY HOTEL BOOKING SYSTEM ==========");
            out.println("1. Administrator menu");
            out.println("2. Guest menu");
            out.println("0. Exit");
            switch (ask("Choose an option: ")) {
                case "1": adminMenu(); break;
                case "2": guestMenu(); break;
                case "0": out.println("Goodbye!"); return;
                default: out.println("Invalid option, please enter 0, 1 or 2.");
            }
        }
    }

    public void adminMenu() {
        while (true) {
            out.println("\n---- Administrator (" + admin.getName() + ") ----");
            out.println("1. Add room");
            out.println("2. Update room bed type");
            out.println("3. Remove room");
            out.println("4. Change room status");
            out.println("5. View all rooms");
            out.println("0. Back");
            String choice = ask("Choose an option: ");
            if (choice.equals("0")) {
                return;
            }
            try {
                switch (choice) {
                    case "1": addRoom(); break;
                    case "2": admin.updateRoom(roomManager, ask("Room number: "), readBedType()); out.println("Room updated."); break;
                    case "3": removeRoom(); break;
                    case "4": admin.changeRoomStatus(roomManager, ask("Room number: "), readStatus()); out.println("Status updated."); break;
                    case "5": printRooms(admin.viewAllRooms(roomManager)); break;
                    default: out.println("Invalid option.");
                }
            } catch (RuntimeException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }
    }

    public void guestMenu() {
        while (true) {
            out.println("\n---- Guest ----");
            out.println("1. Register as guest");
            out.println("2. Search available rooms");
            out.println("3. Make a reservation");
            out.println("4. Search reservation by guest name");
            out.println("5. Search reservation by booking ID");
            out.println("6. View reservations by check-in date");
            out.println("7. Cancel a reservation");
            out.println("8. Generate invoice");
            out.println("0. Back");
            String choice = ask("Choose an option: ");
            if (choice.equals("0")) {
                return;
            }
            try {
                switch (choice) {
                    case "1": registerGuest(); break;
                    case "2": searchRooms(); break;
                    case "3": makeReservation(); break;
                    case "4": printReservations(reservationManager.searchByGuestName(ask("Guest name: "))); break;
                    case "5": searchById(); break;
                    case "6": printReservations(reservationManager.viewByCheckIn(readDate("Check-in date (yyyy-MM-dd): "))); break;
                    case "7": cancelReservation(); break;
                    case "8": invoice(); break;
                    default: out.println("Invalid option.");
                }
            } catch (RuntimeException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }
    }

    // ------------------------------------------------------- admin actions

    private void addRoom() {
        out.println("Room types: 1) Standard  2) Deluxe  3) Suite  4) Family Suite");
        String type = ask("Choose type: ");
        String id = ask("Room number: ");
        BedType bed = readBedType();
        Room room;
        switch (type) {
            case "1": room = new StandardRoom(id, bed); break;
            case "2": room = new DeluxeRoom(id, bed); break;
            case "3": room = new SuiteRoom(id, bed); break;
            case "4": room = new FamilySuiteRoom(id, bed); break;
            default: throw new IllegalArgumentException("Unknown room type.");
        }
        admin.addRoom(roomManager, room);
        out.println("Room " + room.getRoomId() + " (" + room.getTypeName() + ") added.");
    }

    private void removeRoom() {
        String id = ask("Room number: ");
        if (reservationManager.hasActiveBookings(id)) {
            throw new IllegalStateException("Room has active reservations and cannot be removed.");
        }
        admin.removeRoom(roomManager, id);
        out.println("Room removed.");
    }

    // ------------------------------------------------------- guest actions

    private void registerGuest() {
        Guest g = guestManager.register(ask("Full name: "), ask("Contact number: "),
                ask("Email: "), ask("NIC / Passport: "));
        out.println("Registered " + g.getName() + " (" + g.getRole() + ").");
    }

    private void searchRooms() {
        String type = ask("Room type (Standard/Deluxe/Suite/Family Suite, blank = any): ");
        LocalDate checkIn = readDate("Check-in (yyyy-MM-dd): ");
        LocalDate checkOut = readDate("Check-out (yyyy-MM-dd): ");
        printRooms(reservationManager.searchRooms(type, checkIn, checkOut));
    }

    private void makeReservation() {
        Guest guest = guestManager.findByNic(ask("Your NIC / Passport: "));
        if (guest == null) {
            throw new IllegalArgumentException("Guest not registered. Please register first.");
        }
        Room room = roomManager.findRoom(ask("Room number: "));
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }
        LocalDate checkIn = readDate("Check-in (yyyy-MM-dd): ");
        LocalDate checkOut = readDate("Check-out (yyyy-MM-dd): ");
        int guests = readInt("Number of guests: ");
        Reservation r = reservationManager.makeReservation(guest, room, checkIn, checkOut, guests);
        out.printf("Reservation %s confirmed. Refundable advance of LKR %,.2f is required.%n",
                r.getBookingId(), r.getAdvance());
    }

    private void searchById() {
        Reservation r = reservationManager.findById(ask("Booking ID: "));
        out.println(r == null ? "No booking found." : r.toString());
    }

    private void cancelReservation() {
        reservationManager.cancelReservation(ask("Booking ID: "));
        out.println("Reservation cancelled. The advance will be refunded.");
    }

    private void invoice() {
        Reservation r = reservationManager.findById(ask("Booking ID: "));
        if (r == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        if (!r.isActive()) {
            throw new IllegalStateException("Cancelled bookings have no invoice.");
        }
        out.println(billingEngine.generateInvoice(r).printInvoice());
    }

    // ---------------------------------------------------------- input / output helpers

    private void printRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            out.println("No rooms found.");
            return;
        }
        out.println(String.format("%-6s %-14s %-7s %-10s %10s %-9s %-4s %-18s",
                "Room", "Type", "Bed", "Breakfast", "Rate(LKR)", "Extra", "Cap", "Status"));
        for (Room r : rooms) {
            out.println(String.format("%-6s %-14s %-7s %-10s %,10.0f %,9.0f %-4d %-18s",
                    r.getRoomId(), r.getTypeName(), r.getBedType(), r.hasBreakfast() ? "Yes" : "No",
                    r.getDailyRate(), r.getExtraCharge(), r.getCapacity(), r.getStatus()));
        }
    }

    private void printReservations(List<Reservation> list) {
        if (list.isEmpty()) {
            out.println("No reservations found.");
        }
        for (Reservation r : list) {
            out.println(r);
        }
    }

    private String ask(String prompt) {
        out.print(prompt);
        out.flush();
        String line = in.hasNextLine() ? in.nextLine().trim() : "0"; // end of input behaves like "Back/Exit"
        if (System.console() == null) {
            out.println(line); // echo typed text when input is piped, so a recorded session reads naturally
        }
        return line;
    }

    private int readInt(String prompt) {
        try {
            return Integer.parseInt(ask(prompt));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please enter a whole number.");
        }
    }

    private LocalDate readDate(String prompt) {
        try {
            return LocalDate.parse(ask(prompt));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Date must be in the format yyyy-MM-dd.");
        }
    }

    private BedType readBedType() {
        String text = ask("Bed type (Single/Double/Queen/King): ").toUpperCase();
        try {
            return BedType.valueOf(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Bed type must be Single, Double, Queen or King.");
        }
    }

    private RoomStatus readStatus() {
        String s = ask("Status (1 Available, 2 Reserved, 3 Under Maintenance): ");
        switch (s) {
            case "1": return RoomStatus.AVAILABLE;
            case "2": return RoomStatus.RESERVED;
            case "3": return RoomStatus.UNDER_MAINTENANCE;
            default: throw new IllegalArgumentException("Choose 1, 2 or 3.");
        }
    }
}
