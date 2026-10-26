package model;

public class Order {

    private final Customer customer;
    private final MenuItem item;
    private final int lockedPriceCents; // Fixed when order is taken
    private OrderStatus status;
    private int readyTurn = -1;         // Turn when Chef completed cooking
    private int eatingTurnsRemaining = 1; // 1 full turn to eat
    private int prepcount = 0;

    public Order(Customer customer, MenuItem item, int lockedPriceCents) {
        this.customer = customer;
        this.item = item;
        this.lockedPriceCents = lockedPriceCents;
        this.status = OrderStatus.ORDERED;
    }

    public void markReady(int currentTurn) {
        this.status = OrderStatus.READY;
        this.readyTurn = currentTurn;
    }

    public boolean canBeServed(int currentTurn) {
        return this.status == OrderStatus.READY && this.readyTurn < currentTurn;
        // cannot serve cooked food on same turn, must wait till next turn
    }

    public void cook() {
        prepcount += 1;
    }

    public boolean cooked() {
        return (this.prepcount >= item.getPrepUnits());
    }

    public void markServed() {
        this.status = OrderStatus.SERVED;
        this.eatingTurnsRemaining = 1;
    }

    // Decrements eating timer; transitions to READY_TO_PAY when done
    public void tickEating() {
        if (this.status == OrderStatus.SERVED) {
            this.eatingTurnsRemaining--;
            if (this.eatingTurnsRemaining <= 0) {
                this.status = OrderStatus.READY_TO_PAY;
            }
        }
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getPrepCount() {
        return prepcount;
    }

    public MenuItem getItem() {
        return item;
    }

    public int getLockedPriceCents() {
        return lockedPriceCents;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
