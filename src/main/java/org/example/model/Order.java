package org.example.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.List;

/**
 * The Order class represents an order in the Customer Management System.
 * This model class uses JavaFX properties for easy data binding with UI components.
 * It includes order-related information such as the order number, customer ID, address,
 * items in the order, order date, order status, and the total item count.
 */
public class Order {
    // JavaFX properties for data binding with UI components
    private final StringProperty orderNumber;
    private final StringProperty customerId;
    private final StringProperty address;
    private final ListProperty<String> items;
    private final ObjectProperty<LocalDate> orderDate;
    private final StringProperty orderStatus;
    private final IntegerProperty itemCount;

    public Order(String orderNumber, String customerId, String address, List<String> items, LocalDate orderDate, String orderStatus) {
        // Initialize properties using JavaFX property classes
        this.orderNumber = new SimpleStringProperty(orderNumber);
        this.customerId = new SimpleStringProperty(customerId);
        this.address = new SimpleStringProperty(address);
        this.items = new SimpleListProperty<>(FXCollections.observableArrayList(items));
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.orderStatus = new SimpleStringProperty(orderStatus);
        this.itemCount = new SimpleIntegerProperty(items.size());
    }

    // Getter and Setter methods for each property
    public String getOrderNumber() {
        return orderNumber.get();
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber.set(orderNumber);
    }

    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    public String getCustomerId() {
        return customerId.get();
    }

    public void setCustomerId(String customerId) {
        this.customerId.set(customerId);
    }

    public StringProperty customerIdProperty() {
        return customerId;
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    public List<String> getItems() {
        return items.get();
    }

    public void setItems(List<String> items) {
        this.items.set(FXCollections.observableArrayList(items));
        this.itemCount.set(items.size()); // Automatically update item count when items are set
    }

    public ListProperty<String> itemsProperty() {
        return items;
    }

    public LocalDate getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate.set(orderDate);
    }

    public ObjectProperty<LocalDate> orderDateProperty() {
        return orderDate;
    }

    public String getOrderStatus() {
        return orderStatus.get();
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus.set(orderStatus);
    }

    public StringProperty orderStatusProperty() {
        return orderStatus;
    }

    public int getItemCount() {
        return itemCount.get();
    }

    public IntegerProperty itemCountProperty() {
        return itemCount;
    }
}
