package model;

public class Table {

    private final int tableId;
    private Customer currentCustomer;
    private Order currentOrder;

    public Table(int tableId) {
        this.tableId = tableId;
    }

    public boolean isOccupied() {
        return this.currentCustomer != null;
    }

    public void seat(Customer c) {
        this.currentCustomer = c;
        this.currentOrder = null;
    }

    public void setOrder(Order o) {
        this.currentOrder = o;
    }

    public void clear() {
        this.currentCustomer = null;
        this.currentOrder = null;
    }

    public int getTableId() {
        return tableId;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }
}
