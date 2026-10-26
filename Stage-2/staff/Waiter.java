package staff;

import java.util.Queue;
import model.*;

public class Waiter extends Staff {

    //Action 1: Seat customer from queue
    public boolean seatCustomer(Queue<Customer> queue, Table table) {
        if (actionUsed || queue == null || queue.isEmpty() || table == null || table.isOccupied()) {
            return false;
        }

        table.seat(queue.poll());
        actionUsed = true;
        return true;
    }

    // Action 2: Take customer order
    public boolean takeOrder(Table table) {
        if (actionUsed || table == null || !table.isOccupied() || table.getCurrentOrder() != null) {
            return false;
        }

        Customer c = table.getCurrentCustomer();
        MenuItem item = c.getItem();
        // Price locked at order time (applies customer discount)
        int price = c.applyDiscount(item.getPriceCents());
        table.setOrder(new Order(c, item, price));
        actionUsed = true;
        return true;
    }

    // Action 3: Serve ready dish (cannot serve on the same turn it was cooked)
    public boolean serveDish(Table table, int currentTurn) {
        if (actionUsed || table == null || !table.isOccupied() || table.getCurrentOrder() == null) {
            return false;
        }

        Order o = table.getCurrentOrder();
        if (!o.canBeServed(currentTurn)) {
            return false;
        }

        o.markServed();
        // Stage 2: the customer reacts in its own way (polymorphic hook, no type check).
        o.getCustomer().onServed(currentTurn);
        actionUsed = true;
        return true;
    }

    public boolean Skip() {
        actionUsed = false;
        return false;
    }
}
