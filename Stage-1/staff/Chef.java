package staff;

import model.*;

public class Chef extends Staff {

    // Action: Cook an ordered dish
    public boolean cookDish(Table table, int currentTurn) {
        if (actionUsed || table == null || !table.isOccupied() || table.getCurrentOrder() == null) {
            return false;
        }

        Order o = table.getCurrentOrder();
        if (o.getStatus() != OrderStatus.ORDERED) {
            return false;
        }
        o.cook();
        if (o.cooked()) {
            o.markReady(currentTurn);
        }
        actionUsed = true;
        return true;
    }

    public boolean Skip() {
        actionUsed = false;
        return false;
    }
}
