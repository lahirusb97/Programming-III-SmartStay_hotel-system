package ku_2635674;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Student ID: ku_2635674
 * Handles everything about bookings: room search, booking rules, cancel,
 * modify, searching and the check-in date report. A Clock is injected so
 * that the "2 days ahead" and "24 hours" rules can be tested on any date.
 */
public class ReservationManager {
    public static final int MIN_DAYS_AHEAD = 2;
    public static final int MAX_EXTRA_GUESTS = 2;

    private final List<Reservation> reservations = new ArrayList<>();
    private final RoomManager roomManager;
    private final Clock clock;
    private int bookingCounter = 0;

    public ReservationManager(RoomManager roomManager, Clock clock) {
        this.roomManager = roomManager;
        this.clock = clock;
    }

    public LocalDate today() {
        return LocalDate.now(clock);
    }

    /** Rooms of the given type that are Available and free for every night of [in, out). */
    public List<Room> searchRooms(String type, LocalDate in, LocalDate out) {
        List<Room> result = new ArrayList<>();
        for (Room room : roomManager.viewAllRooms()) {
            boolean typeMatches = type == null || type.isBlank()
                    || room.getTypeName().equalsIgnoreCase(type.trim());
            if (typeMatches && room.isAvailable() && isFree(room, in, out, null)) {
                result.add(room);
            }
        }
        return result;
    }

    public Reservation makeReservation(Guest guest, Room room, LocalDate in, LocalDate out, int guests) {
        validateBooking(room, in, out, guests, null);
        bookingCounter++;
        Reservation r = new Reservation(String.format("BK%04d", bookingCounter),
                guest, room, today(), in, out, guests);
        reservations.add(r);
        return r;
    }

    public void modifyReservation(String bookingId, LocalDate in, LocalDate out, int guests) {
        Reservation r = requireReservation(bookingId);
        if (!r.isCancellable(LocalDateTime.now(clock))) {
            throw new IllegalStateException("A booking cannot be modified within 24 hours of check-in.");
        }
        validateBooking(r.getRoom(), in, out, guests, r);
        r.modify(in, out, guests);
    }

    public void cancelReservation(String bookingId) {
        Reservation r = requireReservation(bookingId);
        if (!r.isCancellable(LocalDateTime.now(clock))) {
            throw new IllegalStateException(
                    "This booking cannot be cancelled (already cancelled or within 24 hours of check-in).");
        }
        r.cancel();
    }

    public Reservation findById(String bookingId) {
        if (bookingId == null) {
            return null;
        }
        for (Reservation r : reservations) {
            if (r.getBookingId().equalsIgnoreCase(bookingId.trim())) {
                return r;
            }
        }
        return null;
    }

    public List<Reservation> searchByGuestName(String text) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getGuest().getName().toLowerCase().contains(text.trim().toLowerCase())) {
                result.add(r);
            }
        }
        return result;
    }

    /** Active reservations whose check-in is the given date. */
    public List<Reservation> viewByCheckIn(LocalDate date) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : sortedByCheckIn()) {
            if (r.isActive() && r.getCheckIn().equals(date)) {
                result.add(r);
            }
        }
        return result;
    }

    /** Insertion sort (written by hand) so that reports list the earliest check-in first. */
    public List<Reservation> sortedByCheckIn() {
        List<Reservation> sorted = new ArrayList<>(reservations);
        for (int i = 1; i < sorted.size(); i++) {
            Reservation key = sorted.get(i);
            int j = i - 1;
            while (j >= 0 && sorted.get(j).getCheckIn().isAfter(key.getCheckIn())) {
                sorted.set(j + 1, sorted.get(j));
                j--;
            }
            sorted.set(j + 1, key);
        }
        return sorted;
    }

    public boolean hasActiveBookings(String roomId) {
        for (Reservation r : reservations) {
            if (r.isActive() && r.getRoom().getRoomId().equalsIgnoreCase(roomId)) {
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getAll() {
        return reservations;
    }

    // ---------------- private helpers ----------------

    /** Date-overlap check: ignores cancelled bookings and the booking being edited. */
    private boolean isFree(Room room, LocalDate in, LocalDate out, Reservation ignore) {
        for (Reservation r : reservations) {
            if (r != ignore && r.isActive() && r.getRoom() == room && r.overlaps(in, out)) {
                return false;
            }
        }
        return true;
    }

    private void validateBooking(Room room, LocalDate in, LocalDate out, int guests, Reservation ignore) {
        if (room == null || in == null || out == null) {
            throw new IllegalArgumentException("Room, check-in and check-out are required.");
        }
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room " + room.getRoomId() + " is " + room.getStatus()
                    + " - only Available rooms can be booked.");
        }
        if (!out.isAfter(in)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }
        if (in.isBefore(today().plusDays(MIN_DAYS_AHEAD))) {
            throw new IllegalArgumentException("Bookings must be made at least " + MIN_DAYS_AHEAD
                    + " days before check-in.");
        }
        if (guests < 1 || guests > room.getCapacity() + MAX_EXTRA_GUESTS) {
            throw new IllegalArgumentException("Number of guests must be between 1 and "
                    + (room.getCapacity() + MAX_EXTRA_GUESTS) + " for this room.");
        }
        if (!isFree(room, in, out, ignore)) {
            throw new IllegalStateException("Room " + room.getRoomId() + " is already booked for those dates.");
        }
    }

    private Reservation requireReservation(String bookingId) {
        Reservation r = findById(bookingId);
        if (r == null) {
            throw new IllegalArgumentException("Booking " + bookingId + " not found.");
        }
        return r;
    }
}
