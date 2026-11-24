package ku_2635674;

import java.util.List;

/**
 * Student ID: ku_2635674
 * A hotel administrator who manages the room records.
 * Dependency: the RoomManager is passed in to each method and is not stored.
 */
public class Administrator extends Person {
    private final String adminId;

    public Administrator(String adminId, String name, String contactNumber, String email) {
        super(name, contactNumber, email);
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("Administrator ID is required.");
        }
        this.adminId = adminId.trim();
    }

    public String getAdminId() {
        return adminId;
    }

    public void addRoom(RoomManager rooms, Room room) {
        rooms.addRoom(room);
    }

    public void updateRoom(RoomManager rooms, String roomId, BedType newBed) {
        rooms.updateRoom(roomId, newBed);
    }

    public void removeRoom(RoomManager rooms, String roomId) {
        rooms.removeRoom(roomId);
    }

    public void changeRoomStatus(RoomManager rooms, String roomId, RoomStatus status) {
        rooms.setStatus(roomId, status);
    }

    public List<Room> viewAllRooms(RoomManager rooms) {
        return rooms.viewAllRooms();
    }

    @Override
    public String getRole() {
        return "Administrator";
    }
}
