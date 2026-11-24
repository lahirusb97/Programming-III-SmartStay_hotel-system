package ku_2635674;

/**
 * Student ID: ku_2635674
 * Abstract parent of everybody who uses the system (Guest and Administrator).
 * It stores the details common to both and validates them, so that an
 * invalid person object can never be created (encapsulation).
 */
public abstract class Person {
    private final String name;
    private final String contactNumber;
    private final String email;

    protected Person(String name, String contactNumber, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required.");
        }
        // Accept spaces and dashes while typing, then require 9-15 digits (optional leading +).
        if (contactNumber == null || !contactNumber.replaceAll("[\\s-]", "").matches("\\+?[0-9]{9,15}")) {
            throw new IllegalArgumentException("Contact number must contain 9 to 15 digits.");
        }
        if (email == null || !email.trim().matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) {
            throw new IllegalArgumentException("Email address is not valid.");
        }
        this.name = name.trim();
        this.contactNumber = contactNumber.trim();
        this.email = email.trim();
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    /** Each subclass states its own role (polymorphism). */
    public abstract String getRole();
}
