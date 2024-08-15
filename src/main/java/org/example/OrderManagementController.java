package org.example;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.model.Order;
import org.example.service.OrderService;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing order-related functionality in the application.
 * This class handles displaying, adding, updating, and deleting orders for a selected customer.
 * <p>
 * The order information is retrieved from and stored in a Redis database.
 * This controller is designed to interact with JavaFX UI components for a smooth user experience.
 *
 * @author isil
 */
public class OrderManagementController {

    private String customerId;  // Stores the customer ID for which orders are managed.

    @FXML
    private TextField searchOrder;  // TextField for filtering orders based on search input.

    @FXML
    private TableView<Order> orderTableView;  // TableView to display the list of orders.

    @FXML
    private TableColumn<Order, String> orderNumberColumn;

    @FXML
    private TableColumn<Order, String> customerIdColumn;

    @FXML
    private TableColumn<Order, String> addressColumn;

    @FXML
    private TableColumn<Order, String> itemsColumn;

    @FXML
    private TableColumn<Order, String> orderDateColumn;

    @FXML
    private TableColumn<Order, Integer> itemCountColumn;

    @FXML
    private TableColumn<Order, String> orderStatusColumn;

    private OrderService orderService;  // Service class responsible for handling Redis operations.

    // Constructor initializing the OrderService instance.
    public OrderManagementController() {
        this.orderService = new OrderService();
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method is automatically called by the JavaFX framework.
     */
    @FXML
    public void initialize() {
        // Set cell value factories for the TableView columns.
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        itemsColumn.setCellValueFactory(cellData -> {
            List<String> items = cellData.getValue().getItems();
            String itemsString = String.join("\n", items);  // Display each item on a new line.
            return new SimpleStringProperty(itemsString);
        });
        itemCountColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getItemCount()).asObject());
        orderStatusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        // Load the order data for the selected customer.
        loadOrderData();
    }

    /**
     * Loads and displays the order data for the current customer ID.
     * It also sets up filtering and sorting functionality.
     */
    public void loadOrderData() {
        if (customerId != null) {
            ObservableList<Order> orders = FXCollections.observableArrayList(orderService.getOrdersByCustomerId(customerId));

            // Create a filtered list for the search functionality.
            FilteredList<Order> filteredData = new FilteredList<>(orders, p -> true);

            // Add a listener to the search field to filter orders dynamically.
            searchOrder.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(order -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true; // Show all orders if the search field is empty.
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    // Filter orders by order number, customer ID, or address.
                    return order.getOrderNumber().toLowerCase().contains(lowerCaseFilter) ||
                            order.getCustomerId().toLowerCase().contains(lowerCaseFilter) ||
                            order.getAddress().toLowerCase().contains(lowerCaseFilter);
                });
            });

            // Create a sorted list and bind it to the TableView comparator.
            SortedList<Order> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(orderTableView.comparatorProperty());

            // Set the sorted and filtered data to the TableView.
            orderTableView.setItems(sortedData);
        }
    }

    /**
     * Handles adding a new order.
     * Opens a new window for the user to input order details.
     */
    @FXML
    private void handleAddOrder() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddOrder.fxml"));
            Parent root = loader.load();

            AddOrderController addOrderController = loader.getController();
            addOrderController.setOrderManagementController(this);
            addOrderController.setCustomerId(customerId);  // Pass the current customer ID.

            Stage stage = new Stage();
            stage.setTitle("Add New Order");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Loading Error", "Failed to load Add Order page.");
        }
    }

    /**
     * Handles deleting a selected order.
     * Prompts the user for confirmation before deletion.
     */
    @FXML
    private void handleDeleteOrder() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();

        if (selectedOrder != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this order?");
            alert.setContentText("Order Number: " + selectedOrder.getOrderNumber());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                orderService.deleteOrder(selectedOrder.getOrderNumber());
                loadOrderData();  // Refresh the data after deletion.
                showAlert("Success", "Order deleted successfully!");
            }
        } else {
            showAlert("No Selection", "Please select an order to delete.");
        }
    }

    /**
     * Handles updating a selected order.
     * Opens a new window pre-filled with the order details for editing.
     */
    @FXML
    private void handleUpdateOrder() {
        Order selectedOrder = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateOrder.fxml"));
                Parent root = loader.load();

                UpdateOrderController updateOrderController = loader.getController();
                updateOrderController.setOrder(selectedOrder, this); // Pass the selected order and the controller.

                Stage stage = new Stage();
                stage.setTitle("Update Order");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Loading Error", "Failed to load Update Order page.");
            }
        } else {
            showAlert("No Selection", "Please select an order to update.");
        }
    }

    /**
     * Displays an alert message to the user.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sets the customer ID and loads the corresponding order data.
     *
     * @param customerId The ID of the customer whose orders are being managed.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        loadOrderData(); // Load order data when customer ID is set.
    }
}
