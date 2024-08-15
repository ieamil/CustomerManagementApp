package org.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.Order;
import org.example.service.OrderService;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * Controller class responsible for handling the update of an existing order.
 * <p>
 * This class manages the user interface and logic for updating order details
 * and saving the updated information to the Redis database.
 *
 * @author isil
 */
public class UpdateOrderController {

    @FXML
    private TextField orderNumberField;

    @FXML
    private TextField customerField;

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
    private Order currentOrder;

    /**
     * Initializes the controller by setting up the ComboBox with predefined order statuses.
     */
    @FXML
    public void initialize() {
        // Add predefined order statuses to the ComboBox
        orderStatusComboBox.getItems().addAll("Pending", "Processing", "Shipped", "Delivered", "Canceled");
    }

    /**
     * Sets the current order and the reference to the OrderManagementController.
     * Populates the form fields with the selected order's existing details.
     *
     * @param order The order to be updated.
     */
    public void setOrder(Order order, OrderManagementController orderManagementController) {
        this.currentOrder = order;
        this.orderManagementController = orderManagementController;

        // Populate form fields with existing order data
        orderNumberField.setText(order.getOrderNumber());
        customerField.setText(order.getCustomerId());
        addressField.setText(order.getAddress());
        orderDatePicker.setValue(order.getOrderDate());
        orderStatusComboBox.setValue(order.getOrderStatus());
        itemsField.setText(String.join("\n", order.getItems())); // Display each item on a new line
    }

    /**
     * Handles saving the updated order details when the save button is clicked.
     * Validates the inputs and updates the order in the database.
     */
    @FXML
    private void saveOrder() {
        // Validate user inputs
        if (!validateInputs()) {
            return; // Stop if validation fails
        }

        // Update order with new values
        String address = addressField.getText();
        String orderStatus = orderStatusComboBox.getValue();
        String items = itemsField.getText();
        LocalDate orderDate = orderDatePicker.getValue();

        // Update order object
        currentOrder.setAddress(address);
        currentOrder.setOrderDate(orderDate);
        currentOrder.setOrderStatus(orderStatus);
        currentOrder.setItems(Arrays.asList(items.split("\n"))); // Store each item as a separate entry

        // Save the updated order in Redis
        orderService.updateOrder(currentOrder);

        // Show success message
        showAlert(Alert.AlertType.INFORMATION, "Success", "Order updated successfully!");

        // Refresh the order list in the main controller
        if (orderManagementController != null) {
            orderManagementController.loadOrderData();
        }

        // Close the update order window
        Stage stage = (Stage) orderNumberField.getScene().getWindow();
        stage.close();
    }

    /**
     * Validates the inputs provided by the user.
     * Ensures that all required fields are filled with valid data.
     *
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInputs() {
        if (addressField.getText() == null || addressField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Address cannot be empty.");
            return false;
        }
        if (orderDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Order date must be selected.");
            return false;
        }
        if (orderStatusComboBox.getValue() == null || orderStatusComboBox.getValue().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Order status must be selected.");
            return false;
        }
        if (itemsField.getText() == null || itemsField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Items cannot be empty.");
            return false;
        }
        return true;
    }

    /**
     * Displays an alert dialog with the specified title and message.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
