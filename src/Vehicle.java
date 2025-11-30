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

    // ---- MAIN CONSTRUCTOR (Task 1.5 Refactored with capitalize_vai) ----
    public Vehicle(String make, String model, int year) {
        this.make = capitalize_vai(make);
        this.model = capitalize_vai(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    // ---- Helper method added for Task 1.5 ----
    private String capitalize_vai(String input) {
        if (input == null) return null;

        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;

        if (trimmed.length() == 1)
            return trimmed.toUpperCase();

        return trimmed.substring(0, 1).toUpperCase() +
               trimmed.substring(1).toLowerCase();
    }

    // ---- GETTERS & SETTERS ----

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String plate) {
        this.licensePlate = plate;
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

    // ---- Shared info for all vehicles ----
    public String getInfo() {
        return "[" + licensePlate + "] " + make + " " + model + " (" + year + ")";
    }

    // ---- ABSTRACT RENT/RETURN METHODS ----
    public abstract void rentVehicle();
    public abstract void returnVehicle();
}
