package model;

public enum OrderStatus {
    ORDERED, // Waiter took the order; waiting for Chef
    READY, // Chef cooked it; sitting on kitchen counter
    SERVED, // Waiter delivered it to the table; customer is eating
    READY_TO_PAY, // Customer finished eating; waiting for Cashier
    PAID, // Cashier collected money
    CANCELLED       // Customer abandoned before completion
}
