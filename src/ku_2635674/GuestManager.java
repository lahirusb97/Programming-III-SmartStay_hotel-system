package ku_2635674;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Student ID: ku_2635674
 * Stores registered guests. A HashMap keyed by NIC/passport gives fast
 * look-up and automatically prevents the same person registering twice.
 */
public class GuestManager {
    private final Map<String, Guest> guests = new HashMap<>();

    public Guest register(String name, String contact, String email, String nicPassport) {
        Guest guest = new Guest(name, contact, email, nicPassport); // validates the input
        if (guests.containsKey(guest.getNicPassport())) {
            throw new IllegalArgumentException("A guest with this NIC/passport is already registered.");
        }
        guests.put(guest.getNicPassport(), guest);
        return guest;
    }

    public Guest findByNic(String nicPassport) {
        return nicPassport == null ? null : guests.get(nicPassport.trim().toUpperCase());
    }

    /** Case-insensitive partial name search. */
    public List<Guest> findByName(String text) {
        List<Guest> result = new ArrayList<>();
        for (Guest g : guests.values()) {
            if (g.getName().toLowerCase().contains(text.trim().toLowerCase())) {
                result.add(g);
            }
        }
        return result;
    }

    public int count() {
        return guests.size();
    }
}
