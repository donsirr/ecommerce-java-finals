package models;

import enums.OrderStatus;
import enums.DeliveryStatus; // Import DeliveryStatus
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    // Make VAT_RATE public static final
    public static final double VAT_RATE = 0.12;

    private User customer;
    private List<Product> items;
    private OrderStatus status; 
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private DeliveryStatus deliveryStatus;
    private String orderId;

    public Order(User customer) {
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING; 
        this.orderDate = LocalDate.now();
        this.expectedDeliveryDate = orderDate.plusDays(3);
        this.deliveryStatus = DeliveryStatus.PENDING;
        this.orderId = UUID.randomUUID().toString();
    }

    public void startDeliverySimulation() {
        Thread deliveryThread = new Thread(() -> {
            try {
                Thread.sleep(5000); // Wait for 5 seconds (simulating shipping)
                this.setDeliveryStatus(DeliveryStatus.SHIPPED); 
                System.out.println("\nOrder " + this.getOrderId() + " is now SHIPPED.");

                Thread.sleep(10000); // Wait for another 10 seconds (simulating delivery)
                this.setDeliveryStatus(DeliveryStatus.DELIVERED);
                System.out.println("\nOrder " + this.getOrderId() + " is now DELIVERED.");
            } catch (InterruptedException e) {
                System.err.println("Delivery simulation interrupted: " + e.getMessage());
            }
        });
        deliveryThread.start();
    }

    public void addItem(Product product, int quantity) {
        if (product.isAvailable() && product.getQuantity() >= quantity) {
            items.add(product);
            product.reduceQuantity(quantity);
        }
    }

    public double calculateTotal() {
        return items.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public double calculateVAT() {
        return calculateTotal() * VAT_RATE;
    }

    public void generateReceipt() {
        DecimalFormat df = new DecimalFormat("#.00");
        System.out.println("\n===== ORDER RECEIPT =====");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Order Date: " + orderDate);
        System.out.println("Delivery Date: " + expectedDeliveryDate);
        
        System.out.println("\nPurchased Items:");
        items.forEach(item -> System.out.println(
            item.getName() + " - P" + df.format(item.getPrice())
        ));
        
        System.out.println("\nSubtotal: P" + df.format(calculateTotal()));
        System.out.println("VAT (12%): P" + df.format(calculateVAT()));
        System.out.println("Total: P" + df.format(calculateTotal() + calculateVAT()));
    }

    // Getters for additional functionality if needed
    public User getCustomer() { return customer; }
    public List<Product> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getOrderId() { return orderId; }

    // Add getter and setter for deliveryStatus
    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
