package staff;

import java.util.Queue;
import model.Customer;
import model.Table;

/**
 * Stage 3: the Host seats at most one queued customer per turn.
 *
 * This is IN ADDITION to the Waiter's one task per turn, so the Waiter is free
 * to take orders or serve while the Host fills tables.
 * The Host only seats; it never takes orders or serves.
 */
public class Host extends Staff {

    public boolean seatCustomer(Queue<Customer> queue, Table table) {
        if (actionUsed || queue == null || queue.isEmpty() || table == null || table.isOccupied()) {
            return false;
        }
        table.seat(queue.poll());
        actionUsed = true;
        return true;
    }
}
