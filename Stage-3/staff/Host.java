package staff;

import java.util.Queue;
import model.Customer;
import model.Table;

/** Seats at most one queued customer per turn. */
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
