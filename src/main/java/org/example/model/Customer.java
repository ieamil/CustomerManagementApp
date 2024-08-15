package org.example.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Represents a customer in the Customer Management System.
 * This model class includes all relevant properties such as ID, name, email, phone number, address, and a list of purchases.
 * JavaFX properties are used to easily bind data to UI components.
 *
 * @author isil
 */
public class Customer {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty phoneNumber;
    private final StringProperty address;
    private List<String> purchases;

    public Customer(String id, String name, String email, String phoneNumber, String address, List<String> purchases) {

        // StringProperty is a specialized class in JavaFX that allows for efficient binding of string values to UI components.

        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.address = new SimpleStringProperty(address);
        this.purchases = purchases; // This field was originally used for the terminal-based version of the project.
    }

    // Getter and Setter for ID
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    // Getter and Setter for Name
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    // Getter and Setter for Email
    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    // Getter and Setter for Phone Number
    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    // Getter and Setter for Address
    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty addressProperty() {
        return address;
    }

    // Getter and Setter for Purchases
    public List<String> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<String> purchases) {
        this.purchases = purchases;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id.get() +
                ", name=" + name.get() +
                ", email=" + email.get() +
                ", phoneNumber=" + phoneNumber.get() +
                ", address=" + address.get() +
                ", purchases=" + String.join(", ", purchases) + // Display purchases as a comma-separated list
                '}';
    }
}
