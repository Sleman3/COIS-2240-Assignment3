import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;

public class RentalSystemGUI extends Application {

    private RentalSystem rentalSystem_vai;
    private TextArea outputArea_vai;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        rentalSystem_vai = RentalSystem.getInstance();

        Label title = new Label("Vehicle Rental System (GUI)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Buttons
        Button addVehicleBtn_vai = new Button("Add Vehicle");
        Button addCustomerBtn_vai = new Button("Add Customer");
        Button rentBtn_vai = new Button("Rent Vehicle");
        Button returnBtn_vai = new Button("Return Vehicle");
        Button showAvailableBtn_vai = new Button("Show Available");
        Button showHistoryBtn_vai = new Button("Show History");

        addVehicleBtn_vai.setOnAction(e -> handleAddVehicle_vai());
        addCustomerBtn_vai.setOnAction(e -> handleAddCustomer_vai());
        rentBtn_vai.setOnAction(e -> handleRent_vai());
        returnBtn_vai.setOnAction(e -> handleReturn_vai());
        showAvailableBtn_vai.setOnAction(e -> handleShowAvailable_vai());
        showHistoryBtn_vai.setOnAction(e -> handleShowHistory_vai());

        HBox buttonsRow = new HBox(10,
                addVehicleBtn_vai,
                addCustomerBtn_vai,
                rentBtn_vai,
                returnBtn_vai,
                showAvailableBtn_vai,
                showHistoryBtn_vai
        );
        buttonsRow.setPadding(new Insets(10));

        outputArea_vai = new TextArea();
        outputArea_vai.setEditable(false);
        outputArea_vai.setPrefRowCount(18);

        VBox topBox = new VBox(5, title, buttonsRow);
        topBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setCenter(outputArea_vai);
        BorderPane.setMargin(outputArea_vai, new Insets(10));

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setTitle("Rental System GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        outputArea_vai.appendText("Welcome! Use the buttons above to manage rentals.\n");
    }

    // ==========================
    //  Helper input methods
    // ==========================
    private String askString_vai(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Input");
        dialog.setContentText(prompt);

        Optional<String> res = dialog.showAndWait();
        return res.orElse(null);
    }

    private Integer askInt_vai(String prompt) {
        String s = askString_vai(prompt);
        if (s == null) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException ex) {
            showError_vai("Please enter a valid integer.");
            return null;
        }
    }

    private Double askDouble_vai(String prompt) {
        String s = askString_vai(prompt);
        if (s == null) return null;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException ex) {
            showError_vai("Please enter a valid number.");
            return null;
        }
    }

    private void showError_vai(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo_vai(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle("Info");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ==========================
    //  Button handlers
    // ==========================

    private void handleAddVehicle_vai() {
        Integer type = askInt_vai("Vehicle type:\n1 = Car\n2 = Minibus\n3 = PickupTruck");
        if (type == null) return;

        String plate = askString_vai("Enter license plate (e.g., AAA123):");
        if (plate == null || plate.isEmpty()) return;

        String make = askString_vai("Enter make:");
        if (make == null) return;

        String model = askString_vai("Enter model:");
        if (model == null) return;

        Integer year = askInt_vai("Enter year:");
        if (year == null) return;

        Vehicle v = null;

        if (type == 1) {
            Integer seats = askInt_vai("Enter number of seats:");
            if (seats == null) return;
            v = new Car(make, model, year, seats);
        } else if (type == 2) {
            String acc = askString_vai("Accessible? (true/false):");
            if (acc == null) return;
            boolean accessible = Boolean.parseBoolean(acc.trim());
            v = new Minibus(make, model, year, accessible);
        } else if (type == 3) {
            Double size = askDouble_vai("Enter cargo size:");
            if (size == null) return;
            String tr = askString_vai("Has trailer? (true/false):");
            if (tr == null) return;
            boolean hasTrailer = Boolean.parseBoolean(tr.trim());
            v = new PickupTruck(make, model, year, size, hasTrailer);
        } else {
            showError_vai("Unknown type.");
            return;
        }

        try {
            v.setLicensePlate(plate.toUpperCase());
        } catch (IllegalArgumentException ex) {
            showError_vai("Invalid plate: " + ex.getMessage());
            return;
        }

        boolean added = rentalSystem_vai.addVehicle(v);
        if (added) {
            outputArea_vai.appendText("Vehicle added: " + v.getInfo() + "\n");
            showInfo_vai("Vehicle added successfully.");
        } else {
            showError_vai("Vehicle with that plate already exists.");
        }
    }

    private void handleAddCustomer_vai() {
        Integer id = askInt_vai("Enter customer ID:");
        if (id == null) return;

        String name = askString_vai("Enter customer name:");
        if (name == null) return;

        Customer c = new Customer(id, name);
        boolean added = rentalSystem_vai.addCustomer(c);

        if (added) {
            outputArea_vai.appendText("Customer added: " + c + "\n");
            showInfo_vai("Customer added successfully.");
        } else {
            showError_vai("Customer with that ID already exists.");
        }
    }

    private void handleRent_vai() {
        String plate = askString_vai("Enter license plate to rent:");
        if (plate == null) return;

        Integer id = askInt_vai("Enter customer ID:");
        if (id == null) return;

        Double amount = askDouble_vai("Enter rental amount:");
        if (amount == null) return;

        Vehicle v = rentalSystem_vai.findVehicleByPlate(plate.toUpperCase());
        Customer c = rentalSystem_vai.findCustomerById(id);

        if (v == null || c == null) {
            showError_vai("Vehicle or customer not found.");
            return;
        }

        boolean ok = rentalSystem_vai.rentVehicle(v, c, LocalDate.now(), amount);
        if (ok) {
            outputArea_vai.appendText("Rented: " + v.getInfo() + " to " + c + "\n");
        } else {
            showError_vai("Vehicle is not available.");
        }
    }

    private void handleReturn_vai() {
        String plate = askString_vai("Enter license plate to return:");
        if (plate == null) return;

        Integer id = askInt_vai("Enter customer ID:");
        if (id == null) return;

        Double fees = askDouble_vai("Enter return fees (0 if none):");
        if (fees == null) return;

        Vehicle v = rentalSystem_vai.findVehicleByPlate(plate.toUpperCase());
        Customer c = rentalSystem_vai.findCustomerById(id);

        if (v == null || c == null) {
            showError_vai("Vehicle or customer not found.");
            return;
        }

        boolean ok = rentalSystem_vai.returnVehicle(v, c, LocalDate.now(), fees);
        if (ok) {
            outputArea_vai.appendText("Returned: " + v.getInfo() + " from " + c + "\n");
        } else {
            showError_vai("Vehicle is not currently rented.");
        }
    }

    private void handleShowAvailable_vai() {
        outputArea_vai.appendText("\n=== Available Vehicles ===\n");
        boolean any = false;
        for (Vehicle v : rentalSystem_vai.getAllVehicles_vai()) {
            if (v.getStatus() == Vehicle.VehicleStatus.Available) {
                outputArea_vai.appendText(v.getInfo() + "\n");
                any = true;
            }
        }
        if (!any) {
            outputArea_vai.appendText("No available vehicles.\n");
        }
    }

    private void handleShowHistory_vai() {
        outputArea_vai.appendText("\n=== Rental History ===\n");
        if (rentalSystem_vai.getAllRecords_vai().isEmpty()) {
            outputArea_vai.appendText("No rental records.\n");
        } else {
            for (RentalRecord r : rentalSystem_vai.getAllRecords_vai()) {
                outputArea_vai.appendText(r.toString() + "\n");
            }
        }
    }
}
