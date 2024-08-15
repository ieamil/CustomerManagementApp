package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.example.model.Order;
import org.example.service.OrderService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class responsible for handling the Add Order interface.
 * This class manages the process of collecting order details from the user,
 * validating the input, and saving the order data to the database.
 *
 * @author isil
 */
public class AddOrderController {

    @FXML
    private TextField addressField;

    @FXML
    private DatePicker orderDatePicker;

    @FXML
    private ComboBox<String> orderStatusComboBox;

    @FXML
    private TextArea itemsField;

    private OrderService orderService = new OrderService();
    private OrderManagementController orderManagementController;
    private String customerId;  // To hold the ID of the customer for whom the order is being placed

    /**
     * Initializes the controller and sets default values.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        // Add order statuses to the ComboBox
        orderStatusComboBox.getItems().addAll("Pending", "Processing", "Shipped", "Delivered", "Canceled");

        // Set a default value
        orderStatusComboBox.setValue("Pending");
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Links this controller with the OrderManagementController for refreshing data after saving.
     */
    public void setOrderManagementController(OrderManagementController orderManagementController) {
        this.orderManagementController = orderManagementController;
    }

    /**
     * Handles the Save Order button action.
     * Validates the inputs, creates a new order, saves it to the database, and refreshes the main order list.
     */
    @FXML
    private void saveOrder() {
        // Collect input from the user
        String address = addressField.getText();
        String orderStatus = orderStatusComboBox.getValue();
        String itemsText = itemsField.getText();
        LocalDate orderDate = orderDatePicker.getValue();

        // Validate input fields
        if (!validateInputs(address, itemsText, orderDate)) {
            return; // If validation fails, stop the save process
        }

        // Split each line into a separate item
        List<String> items = Arrays.stream(itemsText.split("\n"))
                .map(String::trim) // Remove unnecessary whitespace
                .filter(item -> !item.isEmpty()) // Exclude empty lines
                .collect(Collectors.toList());

        // Generate a unique order number
        String orderNumber = orderService.generateOrderNumber();

        // Create a new order object
        Order order = new Order(orderNumber, customerId, address, items, orderDate, orderStatus);

        // Save the order to the database
        orderService.saveOrder(order);

        // Display a success message
        showAlert(AlertType.INFORMATION, "Success", "Order saved successfully!");

        // Refresh the main order list if the controller is linked
        if (orderManagementController != null) {
            orderManagementController.loadOrderData();
        }

        // Close the current window
        Stage stage = (Stage) addressField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates user inputs to ensure required fields are filled.
     *
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs(String address, String items, LocalDate orderDate) {
        if (address == null || address.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Invalid Input", "Address cannot be empty.");
            return false;
        }
        if (items == null || items.trim().isEmpty()) {
            showAlert(AlertType.ERROR, "Invalid Input", "Items cannot be empty.");
            return false;
        }
        if (orderDate == null) {
            showAlert(AlertType.ERROR, "Invalid Input", "Order date must be selected.");
            return false;
        }
        return true;
    }

    /**
     * Displays an alert to the user.
     *
     * @param alertType the type of alert (e.g., ERROR, INFORMATION)
     * @param title     the title of the alert window
     * @param message   the message to be displayed in the alert
     */
    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
