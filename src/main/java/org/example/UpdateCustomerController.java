package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.model.Customer;
import org.example.service.CustomerService;

/**
 * Controller class for updating an existing customer's information.
 * <p>
 * This class handles the UI logic for the customer update feature.
 * The customer's updated details are validated and then saved to the Redis database.
 *
 * @author isil
 */

public class UpdateCustomerController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField addressField;

    private Customer selectedCustomer;
    private CustomerService customerService;

    // Reference to the main CustomerController
    private CustomerController customerController;

    /**
     * Constructor for the UpdateCustomerController.
     * Initializes the CustomerService to interact with the Redis database.
     */
    public UpdateCustomerController() {
        this.customerService = new CustomerService();
    }

    /**
     * Sets the selected customer and the reference to the main controller.
     * Populates the form fields with the selected customer's existing details.
     *
     * @param customer The customer to be updated.
     */
    public void setCustomer(Customer customer, CustomerController customerController) {
        this.selectedCustomer = customer;
        this.customerController = customerController;

        // Populate the text fields with the selected customer's details
        nameField.setText(customer.getName());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhoneNumber());
        addressField.setText(customer.getAddress());
    }

    /**
     * Handles the logic for saving the updated customer information.
     * Validates the inputs, updates the customer data, and refreshes the customer list in the main controller.
     */
    @FXML
    private void saveUpdatedCustomer() {
        // Read the updated values from the form fields
        String name = nameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneField.getText();
        String address = addressField.getText();

        // Validate the inputs
        if (!validateInputs(name, email, phoneNumber, address)) {
            return; // If validation fails, stop the process
        }

        // Update the selected customer's details
        selectedCustomer.setName(name);
        selectedCustomer.setEmail(email);
        selectedCustomer.setPhoneNumber(phoneNumber);
        selectedCustomer.setAddress(address);

        // Save the updated customer in Redis
        customerService.updateCustomer(selectedCustomer);

        // Show success message
        showAlert("Success", "Customer updated successfully!");

        // Refresh the customer list in the main controller
        if (customerController != null) {
            customerController.loadCustomerData();
        }

        // Close the update window
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates the user inputs for the customer details.
     */
    private boolean validateInputs(String name, String email, String phoneNumber, String address) {
        // Name must contain only letters and spaces
        if (name == null || name.isEmpty() || !name.matches("^[a-zA-Z\\s]*[^\\s][a-zA-Z\\s]*$")) {
            showAlert("Invalid Input", "Name must contain only letters.");
            return false;
        }

        // Email must match the standard format
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Invalid Input", "Please enter a valid email address.");
            return false;
        }

        // Phone number must be exactly 10 digits
        if (phoneNumber == null || phoneNumber.isEmpty() || !phoneNumber.matches("\\d{10}")) {
            showAlert("Invalid Input", "Phone number must be 10 digits.");
            return false;
        }

        // Address cannot be empty
        if (address == null || address.trim().isEmpty()) {
            showAlert("Invalid Input", "Address cannot be empty.");
            return false;
        }

        // If all validations pass, return true
        return true;
    }

    /**
     * Displays an alert dialog with the specified title and message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
