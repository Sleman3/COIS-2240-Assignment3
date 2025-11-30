import java.time.LocalDate;
import java.util.Scanner;

public class VehicleRentalApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RentalSystem rentalSystem = RentalSystem.getInstance();

        int choice = -1;

        while (choice != 0) {

            System.out.println("\n==== Vehicle Rental System ====");
            System.out.println("1: Add Vehicle");
            System.out.println("2: Add Customer");
            System.out.println("3: Rent Vehicle");
            System.out.println("4: Return Vehicle");
            System.out.println("5: Display Available Vehicles");
            System.out.println("6: Show Rental History");
            System.out.println("0: Exit");
            System.out.print("Enter choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    System.out.println("\nSelect Vehicle Type:");
                    System.out.println(" 1: Car");
                    System.out.println(" 2: Minibus");
                    System.out.println(" 3: Pickup Truck");
                    System.out.print("Enter option: ");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter license plate: ");
                    String plate = scanner.nextLine().toUpperCase();
                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();
                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle = null;

                    if (type == 1) {
                        System.out.print("Number of seats: ");
                        int seats = scanner.nextInt();
                        scanner.nextLine();
                        vehicle = new Car(make, model, year, seats);

                    } else if (type == 2) {
                        System.out.print("Is accessible? (true/false): ");
                        boolean isAccessible = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new Minibus(make, model, year, isAccessible);

                    } else if (type == 3) {
                        System.out.print("Cargo size: ");
                        double cargo = scanner.nextDouble();
                        scanner.nextLine();
                        System.out.print("Has trailer? (true/false): ");
                        boolean hasTrailer = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new PickupTruck(make, model, year, cargo, hasTrailer);
                    }

                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate);
                        boolean added = rentalSystem.addVehicle(vehicle);
                        if (added) {
                            System.out.println("Vehicle added successfully.");
                        } else {
                            System.out.println("Error: vehicle with that plate already exists.");
                        }
                    }
                    break;

                case 2:
                    System.out.print("Enter customer ID: ");
                    int cid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter customer name: ");
                    String cname = scanner.nextLine();

                    boolean addedCust = rentalSystem.addCustomer(new Customer(cid, cname));
                    if (addedCust) {
                        System.out.println("Customer added successfully.");
                    } else {
                        System.out.println("Error: customer with that ID already exists.");
                    }
                    break;

                case 3:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);

                    System.out.print("Enter license plate to rent: ");
                    String rentPlate = scanner.nextLine().toUpperCase();

                    System.out.println("\nRegistered Customers:");
                    rentalSystem.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    int rentId = scanner.nextInt();

                    System.out.print("Enter rental amount: ");
                    double rentAmount = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vRent = rentalSystem.findVehicleByPlate(rentPlate);
                    Customer cRent = rentalSystem.findCustomerById(rentId);

                    if (vRent == null || cRent == null) {
                        System.out.println("Error: vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.rentVehicle(vRent, cRent, LocalDate.now(), rentAmount);
                    break;

                case 4:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Rented);

                    System.out.print("Enter license plate to return: ");
                    String retPlate = scanner.nextLine().toUpperCase();

                    System.out.println("\nRegistered Customers:");
                    rentalSystem.displayAllCustomers();

                    System.out.print("Enter customer ID: ");
                    int returnId = scanner.nextInt();

                    System.out.print("Enter additional fees: ");
                    double fees = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vReturn = rentalSystem.findVehicleByPlate(retPlate);
                    Customer cReturn = rentalSystem.findCustomerById(returnId);

                    if (vReturn == null || cReturn == null) {
                        System.out.println("Error: vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.returnVehicle(vReturn, cReturn, LocalDate.now(), fees);
                    break;

                case 5:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);
                    break;

                case 6:
                    rentalSystem.displayRentalHistory();
                    break;

                case 0:
                    scanner.close();
                    System.exit(0);
                    break;
            }
        }
    }
}
