package ku_2635674;

/**
 * Student ID: ku_2635674
 * A hotel customer. The case study requires the NIC or passport number,
 * full name, contact number and email address at registration.
 */
public class Guest extends Person {
    private final String nicPassport;

    public Guest(String name, String contactNumber, String email, String nicPassport) {
        super(name, contactNumber, email);
        // Old NIC: 9 digits + V/X, new NIC: 12 digits, passport: 1 letter + 6-8 digits.
        if (nicPassport == null
                || !nicPassport.trim().matches("(\\d{9}[VvXx])|(\\d{12})|([A-Za-z]\\d{6,8})")) {
            throw new IllegalArgumentException(
                    "NIC (e.g. 199012345678 or 901234567V) or passport (e.g. N1234567) is not valid.");
        }
        this.nicPassport = nicPassport.trim().toUpperCase();
    }

    public String getNicPassport() {
        return nicPassport;
    }

    @Override
    public String getRole() {
        return "Guest";
    }
}
