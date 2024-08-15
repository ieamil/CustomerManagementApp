package org.example;

import org.example.model.Customer;
import org.example.service.CustomerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Terminal-based Customer Management Application.
 * <p>
 * This application allows users to manage customer data, including adding, viewing, updating,
 * and deleting customer records, as well as listing all customers.
 * <p>
 * Data is stored in a Redis database using the CustomerService class.
 *
 * @author isil
 */
public class CustomerManagementApp {

    private static final CustomerService customerService = new CustomerService(); // Service for handling Redis operations
    private static final Scanner scanner = new Scanner(System.in); // Input scanner

    // Regular expressions for input validation
    private static final Pattern ID_PATTERN = Pattern.compile("\\d+"); // Only digits
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z ]+"); // Only letters and spaces
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"); // Basic email format
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{10}"); // 10-digit phone number

//    When you want to run application do not forget to comment out main function

//    public static void main(String[] args) {
//        while (true) {
//            System.out.println("\nCustomer Management App");
//            System.out.println("1. Add Customer");
//            System.out.println("2. View Customer");
//            System.out.println("3. Update Customer");
//            System.out.println("4. Delete Customer");
//            System.out.println("5. List All Customers");
//            System.out.println("6. Exit");
//            System.out.print("Choose an option: ");
//
//            String input = scanner.nextLine().trim();
//
//            if (!input.matches("\\d+")) { // Only digits allowed for menu choice
//                System.out.println("Invalid input. Please enter a number between 1 and 6.");
//                continue; // Invalid input, ask again
//            }
//
//            int choice = Integer.parseInt(input);
//
//            switch (choice) {
//                case 1:
//                    addCustomer();
//                    break;
//                case 2:
//                    viewCustomer();
//                    break;
//                case 3:
//                    updateCustomer();
//                    break;
//                case 4:
//                    deleteCustomer();
//                    break;
//                case 5:
//                    listAllCustomers();
//                    break;
//                case 6:
//                    System.out.println("Exiting...");
//                    RedisConnection.closeConnection(); // Close Redis connection
//                    return;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        }
//    }

    /**
     * Adds a new customer by collecting information from the user.
     */
    private static void addCustomer() {
        String id = getInput("Enter Customer ID: ", ID_PATTERN, "Invalid ID format. Please enter numbers only.");
        if (customerService.getCustomer(id) != null) { // Check if ID is already in use
            System.out.println("This ID is already in use. Please enter a different ID.");
            return;
        }

        String name = getInput("Enter Customer Name: ", NAME_PATTERN, "Invalid name format. Please enter letters only.");
        String email = getInput("Enter Customer Email: ", EMAIL_PATTERN, "Invalid email format. Please try again.");
        String phoneNumber = getInput("Enter Customer Phone Number: ", PHONE_PATTERN, "Invalid phone number format. Please enter a 10-digit number.");
        System.out.print("Enter Customer Address: ");
        String address = scanner.nextLine().trim();
        while (address.isEmpty()) {
            System.out.println("Address cannot be empty. Please enter a valid address.");
            System.out.print("Enter Customer Address: ");
            address = scanner.nextLine().trim();
        }

        // Collect purchases for the customer
        List<String> purchases = new ArrayList<>();
        boolean addingPurchases = true;

        while (addingPurchases) {
            System.out.print("Enter Purchase (Type 'done' to finish): ");
            String purchase = scanner.nextLine().trim();

            if ("done".equalsIgnoreCase(purchase)) { // Case insensitive check for 'done'
                addingPurchases = false; // End purchase entry
            } else if (isValidPurchase(purchase)) { // Ensure non-empty, valid purchase entry
                purchases.add(purchase);
                System.out.println("Purchase added.");
            } else {
                System.out.println("Invalid purchase format. Please enter a valid purchase.");
            }
        }

        // Create and save the customer
        Customer customer = new Customer(id, name, email, phoneNumber, address, purchases);
        customerService.createCustomer(customer);
        System.out.println("Customer added successfully.");
    }

    /**
     * Validates a purchase entry (non-numeric, non-empty).
     *
     * @return true if valid, false otherwise.
     */
    private static boolean isValidPurchase(String purchase) {
        return !purchase.trim().isEmpty() && !purchase.matches("\\d+");
    }

    /**
     * Views a customer's details by ID.
     */
    private static void viewCustomer() {
        String id = getInput("Enter Customer ID to view: ", ID_PATTERN, "Invalid ID format. Please enter numbers only.");
        Customer customer = customerService.getCustomer(id);
        if (customer != null) {
            System.out.println("Customer Details:");
            System.out.println(customer);
        } else {
            System.out.println("Customer not found.");
        }
    }

    /**
     * Updates an existing customer's details.
     */
    private static void updateCustomer() {
        String id = getInput("Enter Customer ID to update: ", ID_PATTERN, "Invalid ID format. Please enter numbers only.");
        Customer customer = customerService.getCustomer(id);
        if (customer != null) {
            // Collect optional updates for each field
            String name = getOptionalInput("Enter new Customer Name (or press Enter to keep current): ", NAME_PATTERN, "Invalid name format. Please enter letters only.");
            if (name != null) customer.setName(name);

            String email = getOptionalInput("Enter new Customer Email (or press Enter to keep current): ", EMAIL_PATTERN, "Invalid email format. Please try again.");
            if (email != null) customer.setEmail(email);

            String phoneNumber = getOptionalInput("Enter new Customer Phone Number (or press Enter to keep current): ", PHONE_PATTERN, "Invalid phone number format. Please enter a 10-digit number.");
            if (phoneNumber != null) customer.setPhoneNumber(phoneNumber);

            System.out.print("Enter new Customer Address (or press Enter to keep current): ");
            String address = scanner.nextLine().trim();
            if (!address.isEmpty()) customer.setAddress(address);

            // Collect additional purchases
            List<String> purchases = new ArrayList<>(customer.getPurchases());
            boolean updatingPurchases = true;

            System.out.println("Enter new purchases or press Enter to skip and keep current purchases:");
            while (updatingPurchases) {
                System.out.print("Enter new Purchase (or type 'done' to finish): ");
                String purchase = scanner.nextLine().trim();

                if ("done".equalsIgnoreCase(purchase) || purchase.isEmpty()) {
                    updatingPurchases = false; // End purchase entry
                } else if (isValidPurchase(purchase)) {
                    purchases.add(purchase);
                    System.out.println("Purchase added.");
                } else {
                    System.out.println("Invalid purchase format. Please enter a valid purchase.");
                }
            }

            customer.setPurchases(purchases); // Update the customer's purchases
            customerService.updateCustomer(customer);
            System.out.println("Customer updated successfully.");
        } else {
            System.out.println("Customer not found.");
        }
    }

    /**
     * Deletes a customer by ID.
     */
    private static void deleteCustomer() {
        String id = getInput("Enter Customer ID to delete: ", ID_PATTERN, "Invalid ID format. Please enter numbers only.");
        try {
            customerService.deleteCustomer(id);
            System.out.println("Customer deleted successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Lists all customers in the database.
     */
    private static void listAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (!customers.isEmpty()) {
            System.out.println("Customer List:");
            for (Customer customer : customers) {
                System.out.println(customer);
            }
        } else {
            System.out.println("No customers found.");
        }
    }

    /**
     * Prompts the user for input with validation.
     */
    private static String getInput(String prompt, Pattern pattern, String errorMessage) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            } else if (pattern.matcher(input).matches()) {
                break;
            } else {
                System.out.println(errorMessage);
            }
        }
        return input;
    }

    /**
     * Prompts the user for optional input with validation.
     *
     * @param prompt       The prompt message.
     * @param pattern      The pattern to validate the input.
     * @param errorMessage The error message if validation fails.
     * @return The validated input or null if left empty.
     */
    private static String getOptionalInput(String prompt, Pattern pattern, String errorMessage) {
        String input = null;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty() || pattern.matcher(input).matches()) {
                break;
            } else {
                System.out.println(errorMessage);
            }
        }
        return input.isEmpty() ? null : input;
    }
}
