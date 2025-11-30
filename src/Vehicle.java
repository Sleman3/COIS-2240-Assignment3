public abstract class Vehicle {

    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus {
        Available,
        Held,
        Rented,
        UnderMaintenance,
        OutOfService
    }

    // ---- Constructor (Task 1.5 refactored with helper) ----
    public Vehicle(String make, String model, int year) {
        this.make = capitalize_vai(make);
        this.model = capitalize_vai(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    // ---- Helper for formatting make/model (already added in Task 1.5) ----
    private String capitalize_vai(String input) {
        if (input == null) return null;

        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;

        if (trimmed.length() == 1) {
            return trimmed.toUpperCase();
        }

        return trimmed.substring(0, 1).toUpperCase() +
               trimmed.substring(1).toLowerCase();
    }

    // ---- NEW: plate validation (Task 2.1) ----
    // Returns true only if:
    // - not null
    // - not empty
    // - exactly 3 letters followed by 3 digits (e.g., AAA100)
    private boolean isValidPlate(String plate) {
        if (plate == null) return false;

        String trimmed = plate.trim();
        if (trimmed.isEmpty()) return false;

        // normalize to upper-case
        String upper = trimmed.toUpperCase();

        // 3 letters + 3 digits
        return upper.matches("[A-Z]{3}[0-9]{3}");
    }

    // ---- SETTERS / GETTERS ----

    public String getLicensePlate() {
        return licensePlate;
    }

    // Task 2.1: now uses isValidPlate and throws IllegalArgumentException if invalid
    public void setLicensePlate(String plate) {
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException(
                "Invalid license plate format. Expected 3 letters followed by 3 digits (e.g., AAA100)."
            );
        }
        this.licensePlate = plate.trim().toUpperCase();
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    // ---- Common info string for printing ----
    public String getInfo() {
        return "[" + licensePlate + "] " + make + " " + model + " (" + year + ") - " + status;
    }

    // ---- Abstract rent / return ----
    public abstract void rentVehicle();
    public abstract void returnVehicle();
}
