package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Customer;
import org.example.service.CustomerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class responsible for handling the Create Customer interface.
 * This class manages the process of collecting customer details from the user,
 * validating the input, and saving the customer data to the database.
 *
 * @author isil
 */
public class CreateCustomerController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    private CustomerService customerService;
    private CustomerController customerController;

    /**
     * Constructor that initializes the CustomerService.
     */
    public CreateCustomerController() {
        this.customerService = new CustomerService();
    }

    /**
     * Handles the Save Customer button action.
     * Validates the inputs, creates a new customer, saves it to the database, and refreshes the customer list.
     */
    @FXML
    private void saveCustomer() {
        // Collect input from the user
        String name = nameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneField.getText();
        String address = addressField.getText();

        // Validate input fields
        if (!validateInputs(name, email, phoneNumber, address)) {
            return; // If validation fails, stop the save process
        }

        // Prepare an empty purchases list for the customer
        List<String> purchases = new ArrayList<>();

        // Create a new customer object, the ID is generated automatically by the system
        Customer newCustomer = new Customer(null, name, email, phoneNumber, address, purchases);

        // Save the customer to the database
        customerService.createCustomer(newCustomer);

        // Refresh the customer list in the main controller if linked
        if (customerController != null) {
            customerController.loadCustomerData();
        }

        // Display a success message
        showAlert("Success", "Customer registered successfully!");

        // Close the current window
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates user inputs to ensure required fields are correctly filled.
     *
     * @param name        the customer's name
     * @param email       the customer's email
     * @param phoneNumber the customer's phone number
     * @param address     the customer's address
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs(String name, String email, String phoneNumber, String address) {
        // Name must not contain numbers and should have valid characters
        if (name == null || name.isEmpty() || !name.matches("^[a-zA-Z\\s]*[^\\s][a-zA-Z\\s]*$")) {
            showAlert("Invalid Input", "Name must contain only letters.");
            return false;
        }

        // Email must be in a valid format
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Invalid Input", "Please enter a valid email address.");
            return false;
        }

        // Phone number must be exactly 10 digits
        if (phoneNumber == null || phoneNumber.isEmpty() || !phoneNumber.matches("\\d{10}")) {
            showAlert("Invalid Input", "Phone number must be 10 digits.");
            return false;
        }

        // Address must not be empty
        if (address == null || address.trim().isEmpty()) {
            showAlert("Invalid Input", "Address cannot be empty.");
            return false;
        }

        // Return true if all validations pass
        return true;
    }

    /**
     * Initializes the controller by setting the label texts.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() { // For create customer page
        nameLabel.setText("Name:*");
        emailLabel.setText("Email:*");
        phoneLabel.setText("Phone Number:*");
        addressLabel.setText("Address:*");
    }

    /**
     * Links this controller with the main CustomerController for refreshing data after saving.
     */
    public void setCustomerController(CustomerController customerController) {
        this.customerController = customerController;
    }

    /**
     * Displays an alert to the user.
     *
     * @param title   the title of the alert window
     * @param message the message to be displayed in the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // Create an alert for informational messages
        alert.setTitle(title); // Set the alert window title
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
