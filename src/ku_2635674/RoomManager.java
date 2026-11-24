package ku_2635674;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student ID: ku_2635674
 * Administrator functions for the room inventory: add, update, remove,
 * change status and list all rooms. Rooms are kept in an ArrayList.
 */
public class RoomManager {
    private final List<Room> rooms = new ArrayList<>();

    public void addRoom(Room room) {
        if (findRoom(room.getRoomId()) != null) {
            throw new IllegalArgumentException("Room " + room.getRoomId() + " already exists.");
        }
        rooms.add(room);
    }

    /** Linear search by room number. Returns null when not found. */
    public Room findRoom(String roomId) {
        if (roomId == null) {
            return null;
        }
        for (Room r : rooms) {
            if (r.getRoomId().equalsIgnoreCase(roomId.trim())) {
                return r;
            }
        }
        return null;
    }

    public void updateRoom(String roomId, BedType newBed) {
        requireRoom(roomId).setBedType(newBed);
    }

    public void setStatus(String roomId, RoomStatus status) {
        requireRoom(roomId).setStatus(status);
    }

    public void removeRoom(String roomId) {
        rooms.remove(requireRoom(roomId));
    }

    public List<Room> viewAllRooms() {
        return Collections.unmodifiableList(rooms);
    }

    private Room requireRoom(String roomId) {
        Room r = findRoom(roomId);
        if (r == null) {
            throw new IllegalArgumentException("Room " + roomId + " not found.");
        }
        return r;
    }
}
