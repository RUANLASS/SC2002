package staff;

import java.util.List;
import model.*;

public class Cashier extends Staff {

    // Collects payment from the oldest customer ready to pay.
    public record PaymentResult(int amountPaid, int custSat) {}

    public PaymentResult collectPayment(List<Table> tables) {
        if (actionUsed || tables == null) {
            return null;
        }

        Table oldestTable = null;
        for (Table t : tables) {
            if (t.isOccupied() && t.getCurrentOrder() != null
                    && t.getCurrentOrder().getStatus() == OrderStatus.READY_TO_PAY) {
                if (oldestTable == null
                        || t.getCurrentCustomer().getArrivalTurn() < oldestTable.getCurrentCustomer().getArrivalTurn()) {
                    oldestTable = t;
                }
            }
        }

        if (oldestTable == null) {
            return null; // Nobody is ready to pay
        }

        Order o = oldestTable.getCurrentOrder();
        o.markPaid();
        int amountPaid = o.getLockedPriceCents();
        int custSat = oldestTable.getCurrentCustomer().getSatisfaction();
        oldestTable.clear(); // Free table for next customer
        actionUsed = true;

        return new PaymentResult(amountPaid, custSat);
    }

}
