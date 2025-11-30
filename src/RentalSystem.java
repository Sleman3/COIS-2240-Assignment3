import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.Scanner;

public class RentalSystem {

    private static RentalSystem instance_vai;

    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;

    private static final String VEHICLES_FILE_vai = "vehicles.txt";
    private static final String CUSTOMERS_FILE_vai = "customers.txt";
    private static final String RECORDS_FILE_vai = "rental_records.txt";

    private RentalSystem() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new RentalHistory();
        loadData();
    }

    public static RentalSystem getInstance_vai() {
        if (instance_vai == null) {
            instance_vai = new RentalSystem();
        }
        return instance_vai;
    }

    // -------------------------------
    // ADD METHODS
    // -------------------------------
    public void addVehicle(Vehicle v) {
        vehicles.add(v);
        saveVehicle(v);
        System.out.println("Vehicle added.");
    }

    public void addCustomer(Customer c) {
        customers.add(c);
        saveCustomer(c);
        System.out.println("Customer added.");
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate() != null && v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) return c;
        }
        return null;
    }

    // -------------------------------
    // RENT / RETURN METHODS
    // -------------------------------
    public void rentVehicle(Vehicle v, Customer c, LocalDate date, double amount) {
        if (v.getStatus() == Vehicle.VehicleStatus.Available) {
            v.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord record = new RentalRecord(v, c, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);

            System.out.println("Vehicle rented to " + c.getCustomerName());
        } else {
            System.out.println("Vehicle is not available.");
        }
    }

    public void returnVehicle(Vehicle v, Customer c, LocalDate date, double fees) {
        if (v.getStatus() == Vehicle.VehicleStatus.Rented) {
            v.setStatus(Vehicle.VehicleStatus.Available);

            RentalRecord record = new RentalRecord(v, c, date, fees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);

            System.out.println("Vehicle returned by " + c.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    // -------------------------------
    // DISPLAY METHODS
    // -------------------------------
    public void displayVehicles(Vehicle.VehicleStatus status) {
        System.out.println("\n=== VEHICLES ===");

        boolean found = false;
        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {
                System.out.println(v);
                found = true;
            }
        }

        if (!found) System.out.println("No vehicles found.");
    }

    public void displayAllCustomers() {
        System.out.println("\n=== CUSTOMERS ===");

        if (customers.isEmpty()) {
            System.out.println("No customers.");
            return;
        }

        for (Customer c : customers) System.out.println(c);
    }

    public void displayRentalHistory() {
        System.out.println("\n=== RENTAL HISTORY ===");

        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("No rental records.");
            return;
        }

        for (RentalRecord r : rentalHistory.getRentalHistory()) {
            System.out.println(r);
        }
    }

    // -------------------------------
    // SAVE SECTION (Task 1.2)
    // -------------------------------
    public void saveVehicle(Vehicle v) {
        try (PrintWriter out = new PrintWriter(new FileWriter(VEHICLES_FILE_vai, true))) {

            if (v instanceof Car) {
                Car c = (Car) v;
                out.println("Car," + v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," +
                            v.getYear() + "," + c.getNumSeats());
            }

            else if (v instanceof Minibus) {
                Minibus m = (Minibus) v;
                out.println("Minibus," + v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," +
                            v.getYear() + "," + m.isAccessible());
            }

            else if (v instanceof PickupTruck) {
                PickupTruck t = (PickupTruck) v;
                out.println("PickupTruck," + v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," +
                            v.getYear() + "," + t.getCargoSize() + "," + t.hasTrailer());
            }

            else if (v instanceof SportCar) {
                SportCar s = (SportCar) v;
                out.println("SportCar," + v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," +
                            v.getYear() + "," + s.getNumSeats() + "," + s.horsepower + "," + s.hasTurbo);
            }

        } catch (Exception e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    public void saveCustomer(Customer c) {
        try (PrintWriter out = new PrintWriter(new FileWriter(CUSTOMERS_FILE_vai, true))) {
            out.println(c.getCustomerId() + "," + c.getCustomerName());
        } catch (Exception e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    public void saveRecord(RentalRecord r) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RECORDS_FILE_vai, true))) {
            out.println(r.getVehicle().getLicensePlate() + "," +
                        r.getCustomer().getCustomerId() + "," +
                        r.getRecordDate() + "," +
                        r.getTotalAmount() + "," +
                        r.getRecordType());
        } catch (Exception e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }

    // -------------------------------
    // LOAD SECTION (Task 1.3)
    // -------------------------------
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRecords();
    }

    private void loadVehicles() {
        File f = new File(VEHICLES_FILE_vai);
        if (!f.exists()) return;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(",");

                String type = p[0];
                String plate = p[1];
                String make = p[2];
                String model = p[3];
                int year = Integer.parseInt(p[4]);

                Vehicle v = null;

                switch (type) {
                    case "Car":
                        int seats = Integer.parseInt(p[5]);
                        v = new Car(make, model, year, seats);
                        break;

                    case "Minibus":
                        boolean acc = Boolean.parseBoolean(p[5]);
                        v = new Minibus(make, model, year, acc);
                        break;

                    case "PickupTruck":
                        double cargo = Double.parseDouble(p[5]);
                        boolean trailer = Boolean.parseBoolean(p[6]);
                        v = new PickupTruck(make, model, year, cargo, trailer);
                        break;

                    case "SportCar":
                        int seatCount = Integer.parseInt(p[5]);
                        int hp = Integer.parseInt(p[6]);
                        boolean turbo = Boolean.parseBoolean(p[7]);
                        v = new SportCar(make, model, year, seatCount, hp, turbo);
                        break;
                }

                if (v != null) {
                    v.setLicensePlate(plate);
                    vehicles.add(v);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        File f = new File(CUSTOMERS_FILE_vai);
        if (!f.exists()) return;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(",");
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                customers.add(new Customer(id, name));
            }
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRecords() {
        File f = new File(RECORDS_FILE_vai);
        if (!f.exists()) return;

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(",");

                String plate = p[0];
                int cid = Integer.parseInt(p[1]);
                LocalDate date = LocalDate.parse(p[2]);
                double amount = Double.parseDouble(p[3]);
                String type = p[4];

                Vehicle v = findVehicleByPlate(plate);
                Customer c = findCustomerById(cid);

                if (v != null && c != null) {
                    rentalHistory.addRecord(new RentalRecord(v, c, date, amount, type));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading records: " + e.getMessage());
        }
    }
}
