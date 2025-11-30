import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {

    // Singleton instance (Task 1.1)
    private static RentalSystem instance_vai;

    // Main data collections
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    // Private constructor so no one can do "new RentalSystem()"
    private RentalSystem() {
    }

    // Access method for the single instance
    public static RentalSystem getInstance_vai() {
        if (instance_vai == null) {
            instance_vai = new RentalSystem();
        }
        return instance_vai;
    }

    // ----------------------------
    // Add / find methods
    // ----------------------------
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) {
                return c;
            }
        }
        return null;
    }

    // ----------------------------
    // Renting / returning
    // ----------------------------
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // ----------------------------
    // Display helpers
    // ----------------------------
    public void displayVehicles(Vehicle.VehicleStatus status) {
        System.out.println();
        if (status == null) {
            System.out.println("=== All Vehicles ===");
        } else {
            System.out.println("=== " + status + " Vehicles ===");
        }

        boolean found = false;
        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {
                found = true;
                System.out.println(v.toString());
            }
        }

        if (!found) {
            System.out.println("No vehicles to show for this status.");
        }
    }

    public void displayAllCustomers() {
        System.out.println();
        System.out.println("=== Customers ===");
        if (customers.isEmpty()) {
            System.out.println("No customers in the system.");
        } else {
            for (Customer c : customers) {
                System.out.println(c.toString());
            }
        }
    }

    public void displayRentalHistory() {
        System.out.println();
        System.out.println("=== Rental History ===");
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("No rental records yet.");
        } else {
            for (RentalRecord record : rentalHistory.getRentalHistory()) {
                System.out.println(record.toString());
            }
        }
    }
}
