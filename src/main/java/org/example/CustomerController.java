package org.example;

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
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.model.Customer;
import org.example.service.CustomerService;

import java.util.Optional;

/**
 * Controller class responsible for managing customer data within the Customer Management System.
 * This class handles the operations for displaying, adding, updating, and deleting customers.
 * It also provides search functionality to filter customers based on specific criteria.
 * Customer information is retrieved from and stored in a Redis database.
 *
 * @author isil
 */
public class CustomerController {

    @FXML
    private TextField searchField; // Search field for filtering customer data
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> emailColumn;
    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;

    private CustomerService customerService;

    /**
     * Constructor that initializes the CustomerService.
     */
    public CustomerController() {
        this.customerService = new CustomerService();
    }

    /**
     * Initializes the controller and sets up the table columns.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {

        // Map the table columns to the Customer model properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Enable single selection mode for the table
        customerTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Load customer data and apply filtering
        loadCustomerData();
    }

    /**
     * Loads the customer data from the service and sets up filtering and sorting.
     */
    public void loadCustomerData() {
        ObservableList<Customer> customers = FXCollections.observableArrayList(customerService.getAllCustomers());

        // Create a filtered list for the customers
        FilteredList<Customer> filteredData = new FilteredList<>(customers, p -> true);

        // Apply filtering based on search input
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                // Show all customers if the search field is empty
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Convert the search term to lowercase for case-insensitive matching
                String lowerCaseFilter = newValue.toLowerCase();

                // Filter customers by ID or name
                if (customer.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (customer.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Exclude customers that do not match
            });
        });

        // Create a sorted list and bind it to the table view
        SortedList<Customer> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(customerTableView.comparatorProperty());

        customerTableView.setItems(sortedData);
    }

    /**
     * Handles the action for adding a new customer.
     * Opens the Create Customer window for user input.
     */
    @FXML
    private void handleAddCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateCustomer.fxml"));
            Parent root = loader.load();

            CreateCustomerController createCustomerController = loader.getController();
            createCustomerController.setCustomerController(this);

            Stage stage = new Stage();
            stage.setTitle("Create New Customer");
            Scene scene = new Scene(root);

            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/add_user.png")));

            // CSS file
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Handles the action for deleting a customer.
     * Prompts the user for confirmation before deletion.
     */
    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            System.out.println("Selected customer ID: " + selectedCustomer.getId()); // Debugging message

            // Show a confirmation dialog before deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Confirmation");
            alert.setHeaderText("Are you sure you want to delete this customer?");
            alert.setContentText("Customer ID: " + selectedCustomer.getId() + "\nName: " + selectedCustomer.getName());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("User confirmed deletion."); // Debugging message
                // Proceed with deletion
                customerService.deleteCustomer(selectedCustomer.getId());
                System.out.println("Customer deleted from database."); // Debugging message
                loadCustomerData(); // Refresh the table view after deletion
                showAlert("Success", "Customer deleted successfully!");
            }
        } else {
            // Show an alert if no customer is selected
            System.out.println("No customer selected."); // Debugging message
            showAlert("No Selection", "Please select a customer to delete.");
        }
    }

    /**
     * Handles the action for updating a customer's details.
     * Opens the Update Customer window for editing.
     */
    @FXML
    private void handleUpdateCustomer() {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateCustomer.fxml"));
                Parent root = loader.load();

                // Create scene to change CSS data
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm()); // CSS dosyasını ekle

                // Pass the selected customer and master controller to UpdateCustomerController
                UpdateCustomerController updateCustomerController = loader.getController();
                updateCustomerController.setCustomer(selectedCustomer, this);

                Stage stage = new Stage();
                stage.setTitle("Update Customer");
                stage.setScene(scene);
                stage.getIcons().add(new Image("/icons/update.png")); // İkon eklemek için
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "Please select a customer to update.");
        }
    }


    /**
     * Handles the action for opening the Order Management screen for the selected customer.
     */
    @FXML
    private void handleOrderManagement() {
        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OrderManagement.fxml"));
                Parent root = loader.load();

                // Pass the selected customer ID to the OrderManagementController
                OrderManagementController orderManagementController = loader.getController();
                orderManagementController.setCustomerId(selectedCustomer.getId());

                Stage stage = new Stage();
                stage.setTitle("Order Management");
                Scene scene = new Scene(root);

                // Add CSS file
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                stage.setScene(scene);
                stage.getIcons().add(new Image("/icons/order.png"));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showAlert("No Selection", "Please select a customer to view orders.");
        }
    }

    /**
     * Displays an alert with a specified title and message.
     *
     * @param title   the title of the alert window
     * @param message the message to be displayed in the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
