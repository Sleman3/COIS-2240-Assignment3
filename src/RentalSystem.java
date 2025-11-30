import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RentalSystem {

    // ===== Singleton support (Task 1.1) =====
    private static RentalSystem instance_vai;

    // Main data collections
    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;

    // Files for saving data (Task 1.2)
    private static final String VEHICLES_FILE_vai  = "vehicles.txt";
    private static final String CUSTOMERS_FILE_vai = "customers.txt";
    private static final String RECORDS_FILE_vai   = "rental_records.txt";

    // Private constructor so code outside cannot call "new RentalSystem()"
    private RentalSystem() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new RentalHistory();
    }

    // Public access to the single instance
    public static RentalSystem getInstance_vai() {
        if (instance_vai == null) {
            instance_vai = new RentalSystem();
        }
        return instance_vai;
    }

    // ==========================
    // Add / find methods
    // ==========================

    // Task 1.2: call saveVehicle after adding
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        System.out.println("Vehicle added.");
    }

    // Task 1.2: call saveCustomer after adding
    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
        System.out.println("Customer added.");
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate() != null
                    && v.getLicensePlate().equalsIgnoreCase(plate)) {
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

    // ==========================
    // Rent / return methods
    // ==========================

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);

            // Task 1.2: save rental record to file
            saveRecord(record);

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

            // Task 1.2: save return record to file
            saveRecord(record);

            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // ==========================
    // Display helpers
    // ==========================

    public void displayVehicles(Vehicle.VehicleStatus status) {
        System.out.println();
        if (status == null) {
            System.out.println("=== All Vehicles ===");
        } else {
            System.out.println("=== " + status + " Vehicles ===");
        }

        boolean found_vai = false;
        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {
                found_vai = true;
                System.out.println(v.toString());
            }
        }

        if (!found_vai) {
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

    // ==========================
    // Task 1.2 – Save methods
    // ==========================

    // Save vehicle details to vehicles.txt (append mode)
    public void saveVehicle(Vehicle vehicle) {
        try (PrintWriter out_vai = new PrintWriter(new FileWriter(VEHICLES_FILE_vai, true))) {
            // type,plate,make,model,year
            String type = vehicle.getClass().getSimpleName();
            out_vai.println(type + ","
                    + vehicle.getLicensePlate() + ","
                    + vehicle.getMake() + ","
                    + vehicle.getModel() + ","
                    + vehicle.getYear());
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    // Save customer details to customers.txt (append mode)
    public void saveCustomer(Customer customer) {
        try (PrintWriter out_vai = new PrintWriter(new FileWriter(CUSTOMERS_FILE_vai, true))) {
            // id,name
            out_vai.println(customer.getCustomerId() + "," + customer.getCustomerName());
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    // Save rental record details to rental_records.txt (append mode)
    public void saveRecord(RentalRecord record) {
        try (PrintWriter out_vai = new PrintWriter(new FileWriter(RECORDS_FILE_vai, true))) {
            // plate,customerId,date,amount,type
            out_vai.println(
                    record.getVehicle().getLicensePlate() + ","
                    + record.getCustomer().getCustomerId() + ","
                    + record.getRecordDate().toString() + ","
                    + record.getTotalAmount() + ","
                    + record.getRecordType()
            );
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    // ==========================
    // Getters (useful later)
    // ==========================

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public RentalHistory getRentalHistory() {
        return rentalHistory;
    }
}
