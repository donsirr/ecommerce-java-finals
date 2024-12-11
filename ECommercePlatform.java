import models.*;
import enums.*;
import java.util.*;

public class ECommercePlatform {
    private static Scanner scanner = new Scanner(System.in);
    private static List<User> users = new ArrayList<>();
    private static List<Product> products = new ArrayList<>();

    public static void main(String[] args) {
        initializeProducts();
        mainMenu();
    }

    private static void initializeProducts() {
        products.add(new Product("Smartphone", 15000, 10, 
                              ProductCategory.ELECTRONICS, "Gadget Section"));
        products.add(new Product("Rice", 50, 50, 
                              ProductCategory.GROCERIES, "Food Aisle"));
        products.add(new Product("T-Shirt", 299, 20, 
                              ProductCategory.APPAREL, "Clothing Section"));
    }

    private static void mainMenu() {
        while (true) {
            System.out.println("\n===== E-COMMERCE PLATFORM =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: registerUser(); break;
                case 2: loginUser(); break;
                case 3: 
                    System.out.println("Thank you for using our platform!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void registerUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Contact Details: ");
        String contactDetails = scanner.nextLine();
        
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        User newUser = new User(username, password, name, contactDetails, address);
        
        if (newUser.isValidPassword()) {
            boolean userExists = users.stream()
                .anyMatch(u -> u.getUsername().equals(username));
            
            if (!userExists) {
                users.add(newUser);
                System.out.println("Registration Successful!");
            } else {
                System.out.println("Username already exists. Please choose another.");
            }
        } else {
            System.out.println("Password must be at least 8 characters long.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User loggedInUser = users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);

        if (loggedInUser != null) {
            userDashboard(loggedInUser);
        } else {
            System.out.println("User not found.");
        }
    }

    private static void userDashboard(User user) {
        while (true) {
            System.out.println("\n===== USER DASHBOARD =====");
            System.out.println("1. View Profile");
            System.out.println("2. Load Wallet");
            System.out.println("3. View Products");
            System.out.println("4. Create Order");
            System.out.println("5. View Orders");
            System.out.println("6. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: viewProfile(user); break;
                case 2: loadWallet(user); break;
                case 3: viewProducts(); break;
                case 4: createOrder(user); break;
                case 5: viewOrders(user); break;
                case 6: return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void viewProfile(User user) {
        System.out.println("\n===== USER PROFILE =====");
        System.out.println("Name: " + user.getName());
        System.out.println("Contact: " + user.getContactDetails());
        System.out.println("Address: " + user.getAddress());
        System.out.println("Wallet Balance: P" + String.format("%.2f", user.getWallet().getBalance()));
    }

    private static void loadWallet(User user) {
        System.out.print("Enter amount to load: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        if (user.getWallet().addFunds(amount)) {
            System.out.println("Funds added successfully.");
            System.out.println("New Balance: P" + String.format("%.2f", user.getWallet().getBalance()));
        } else {
            System.out.println("Cannot exceed wallet limit of P100000000.");
        }
    }

    private static void viewProducts() {
        System.out.println("\n===== AVAILABLE PRODUCTS =====");
        products.forEach(p -> System.out.println(
            p.getName() + " - P" + String.format("%.2f", p.getPrice()) + 
            " (" + p.getCategory() + ") - Qty: " + p.getQuantity() + 
            " - " + (p.isAvailable() ? "In Stock" : "Out of Stock")
        ));
    }

    private static void createOrder(User user) {
        Order order = new Order(user);
        
        // Will store product and its quantity separately to handle multiple quantities
        Map<Product, Integer> orderItems = new HashMap<>();
        
        while (true) {
            viewProducts();
            System.out.print("Enter product name (or 'done' to finish): ");
            String productName = scanner.nextLine();

            if (productName.equalsIgnoreCase("done")) break;

            Product selectedProduct = products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .findFirst()
                .orElse(null);

            if (selectedProduct != null && selectedProduct.isAvailable()) {
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (quantity <= selectedProduct.getQuantity()) {
                    // Add to order items map
                    orderItems.put(selectedProduct, quantity);
                    order.addItem(selectedProduct, quantity);
                    System.out.println("Product added to cart.");
                } else {
                    System.out.println("Requested quantity exceeds available stock.");
                }
            } else {
                System.out.println("Product not available.");
            }
        }
    
        // Custom receipt generation to show quantities
        if (!orderItems.isEmpty()) {
            System.out.println("\n===== ORDER SUMMARY =====");
            double total = 0;
            for (Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                double itemTotal = product.getPrice() * quantity;
                total += itemTotal;
                
                System.out.printf("%s - Qty: %d - Unit Price: P%.2f - Total: P%.2f%n", 
                      product.getName(), quantity, product.getPrice(), itemTotal);
            }

            // Calculate total with VAT
            double vatAmount = total * Order.VAT_RATE;
            double grandTotal = total + vatAmount;

            // Wallet payment
            if (user.getWallet().deductFunds(grandTotal)) {
                System.out.printf("%nSubtotal: P%.2f%n", total);
                System.out.printf("VAT (12%%): P%.2f%n", vatAmount);
                System.out.printf("Grand Total: P%.2f%n", grandTotal);
                System.out.println("Payment successful!");

                // Set order status to "Pending" after successful payment
                order.setDeliveryStatus(DeliveryStatus.PENDING);

                // Start the delivery simulation AFTER payment is successful
                order.startDeliverySimulation();
                
                user.addOrder(order); // Add the order to the user's order list
                
            } else {
                // Restore stock if payment fails
                System.out.println("Insufficient wallet balance. Restoring stock.");
                for (Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
                    Product product = entry.getKey();
                    int quantity = entry.getValue();
                    // Add back the quantity to the product's stock
                    product.restoreQuantity(quantity);
                }
                System.out.println("Order cancelled. Stock has been restored.");
            }
        } else {
            System.out.println("No items in the order.");
        }
    }

    private static void viewOrders(User user) {
        List<Order> orders = user.getOrders();
        if (orders.isEmpty()) {
            System.out.println("You have no orders yet.");
            return;
        }

        System.out.println("\n===== YOUR ORDERS =====");
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Order Date: " + order.getOrderDate());
            System.out.println("Delivery Status: " + order.getDeliveryStatus());
            System.out.println("------------------");
        }
    }
}