import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalSystem {

    // ===== Singleton (Task 1.1) =====
    private static RentalSystem instance_vai;

    public static RentalSystem getInstance() {
        if (instance_vai == null) {
            instance_vai = new RentalSystem();
        }
        return instance_vai;
    }

    // ===== Data =====
    private List<Vehicle> vehicles_vai;
    private List<Customer> customers_vai;
    private RentalHistory rentalHistory_vai;

    private static final String VEHICLES_FILE = "vehicles.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String RECORDS_FILE = "rental_records.txt";

    // ===== Constructor =====
    private RentalSystem() {
        vehicles_vai = new ArrayList<>();
        customers_vai = new ArrayList<>();
        rentalHistory_vai = new RentalHistory();
        loadData_vai();   // load from files at startup
    }

    // ==============================
    //  Task 1.4 – addVehicle/addCustomer (with duplicates)
    // ==============================
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle.getLicensePlate() != null &&
                findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle with this plate already exists.");
            return false;
        }
        vehicles_vai.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Customer with this ID already exists.");
            return false;
        }
        customers_vai.add(customer);
        saveCustomer(customer);
        return true;
    }

    // ==============================
    //  Task 2.2 – rent/return now return boolean
    // ==============================
    public boolean rentVehicle(Vehicle v, Customer c, LocalDate date, double amount) {
        if (v.getStatus() != Vehicle.VehicleStatus.Available) {
            System.out.println("Vehicle is not available.");
            return false;
        }

        v.setStatus(Vehicle.VehicleStatus.Rented);
        RentalRecord record = new RentalRecord(v, c, date, amount, "RENT");
        rentalHistory_vai.addRecord(record);
        saveRecord(record);
        System.out.println("Vehicle rented successfully.");
        return true;
    }

    public boolean returnVehicle(Vehicle v, Customer c, LocalDate date, double fees) {
        if (v.getStatus() != Vehicle.VehicleStatus.Rented) {
            System.out.println("Vehicle is not currently rented.");
            return false;
        }

        v.setStatus(Vehicle.VehicleStatus.Available);
        RentalRecord record = new RentalRecord(v, c, date, fees, "RETURN");
        rentalHistory_vai.addRecord(record);
        saveRecord(record);
        System.out.println("Vehicle returned successfully.");
        return true;
    }

    // ==============================
    //  Find helpers
    // ==============================
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles_vai) {
            if (v.getLicensePlate() != null &&
                    v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers_vai) {
            if (c.getCustomerId() == id) return c;
        }
        return null;
    }

    // ==============================
    //  Display helpers
    // ==============================
    public void displayVehicles(Vehicle.VehicleStatus status) {
        System.out.println("\n=== Vehicles (" + status + ") ===");
        boolean any = false;
        for (Vehicle v : vehicles_vai) {
            if (v.getStatus() == status) {
                System.out.println(v.getInfo());
                any = true;
            }
        }
        if (!any) {
            System.out.println("No vehicles with that status.");
        }
    }

    public void displayAllCustomers() {
        System.out.println("\n=== Customers ===");
        if (customers_vai.isEmpty()) {
            System.out.println("No customers.");
        } else {
            for (Customer c : customers_vai) {
                System.out.println(c);
            }
        }
    }

    public void displayRentalHistory() {
        System.out.println("\n=== Rental History ===");
        if (rentalHistory_vai.getRentalHistory().isEmpty()) {
            System.out.println("No rental records.");
        } else {
            for (RentalRecord r : rentalHistory_vai.getRentalHistory()) {
                System.out.println(r);
            }
        }
    }
    // ===== Simple getters for GUI (Task 3) =====
    public java.util.List<Vehicle> getAllVehicles_vai() {
        return new java.util.ArrayList<>(vehicles_vai);
    }

    public java.util.List<Customer> getAllCustomers_vai() {
        return new java.util.ArrayList<>(customers_vai);
    }

    public java.util.List<RentalRecord> getAllRecords_vai() {
        return new java.util.ArrayList<>(rentalHistory_vai.getRentalHistory());
    }


    // ==============================
    //  SAVE to files (append)
    // ==============================
    public void saveVehicle(Vehicle v) {
        try (PrintWriter out = new PrintWriter(new FileWriter(VEHICLES_FILE, true))) {
            String type;
            if (v instanceof Car) type = "Car";
            else if (v instanceof Minibus) type = "Minibus";
            else if (v instanceof PickupTruck) type = "PickupTruck";
            else if (v instanceof SportCar) type = "SportCar";
            else type = "Vehicle";

            out.println(type + "," +
                    v.getLicensePlate() + "," +
                    v.getMake() + "," +
                    v.getModel() + "," +
                    v.getYear() + "," +
                    v.getStatus());
        } catch (Exception e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    public void saveCustomer(Customer c) {
        try (PrintWriter out = new PrintWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            out.println(c.getCustomerId() + "," + c.getCustomerName());
        } catch (Exception e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    public void saveRecord(RentalRecord r) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RECORDS_FILE, true))) {
            out.println(r.getVehicle().getLicensePlate() + "," +
                    r.getCustomer().getCustomerId() + "," +
                    r.getRecordDate() + "," +
                    r.getTotalAmount() + "," +
                    r.getRecordType());
        } catch (Exception e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }

    // ==============================
    //  LOAD from files
    // ==============================
    private void loadData_vai() {
        loadCustomers_vai();
        loadVehicles_vai();
        loadRecords_vai();
    }

    private void loadCustomers_vai() {
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length == 2) {
                    int id = Integer.parseInt(p[0]);
                    String name = p[1];
                    customers_vai.add(new Customer(id, name));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadVehicles_vai() {
        File f = new File(VEHICLES_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] p = line.split(",");
                    if (p.length < 6) continue;

                    String type = p[0];
                    String plate = p[1];
                    String make = p[2];
                    String model = p[3];
                    int year = Integer.parseInt(p[4]);
                    Vehicle.VehicleStatus status =
                            Vehicle.VehicleStatus.valueOf(p[5]);

                    Vehicle v = null;

                    if ("Car".equals(type)) {
                        v = new Car(make, model, year, 4);
                    } else if ("Minibus".equals(type)) {
                        v = new Minibus(make, model, year, false);
                    } else if ("PickupTruck".equals(type)) {
                        v = new PickupTruck(make, model, year, 1.0, false);
                    } else if ("SportCar".equals(type)) {
                        v = new SportCar(make, model, year, 2, 300, true);
                    } else {
                        continue;
                    }

                    v.setLicensePlate(plate);
                    v.setStatus(status);
                    vehicles_vai.add(v);

                } catch (Exception ignore) {
                    // skip bad line
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadRecords_vai() {
        File f = new File(RECORDS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] p = line.split(",");
                    if (p.length < 5) continue;

                    String plate = p[0];
                    int cid = Integer.parseInt(p[1]);
                    LocalDate date = LocalDate.parse(p[2]);
                    double amount = Double.parseDouble(p[3]);
                    String type = p[4];

                    Vehicle v = findVehicleByPlate(plate);
                    Customer c = findCustomerById(cid);

                    if (v != null && c != null) {
                        rentalHistory_vai.addRecord(
                                new RentalRecord(v, c, date, amount, type));
                    }
                } catch (Exception ignore) {
                    // skip bad line
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }
}
