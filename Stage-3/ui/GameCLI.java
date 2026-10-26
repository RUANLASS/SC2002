package ui;

import engine.RestaurantGame;
import java.util.Queue;
import model.Customer;
import model.Order;
import model.Table;

public class GameCLI {

    private final RestaurantGame game;
    private final InputValidator validator;

    public GameCLI(RestaurantGame game) {
        this.game = game;
        this.validator = new InputValidator();
    }

    public void displayWelcome() {
        System.out.println("==================================================");
        System.out.println("         WELCOME TO RESTAURANT RUSH!           ");
        System.out.println("   Survive 18 turns, serve customers, and profit! ");
        System.out.println("==================================================");
    }

    public void displayStartOfTurn() {
        System.out.println("--------------------------------------------------");
        System.out.printf(">>> TURN %d / 18 | Bank: $%.2f | Abandoned: %d | Mean Satisfaction: %d <<<\n",
                game.getCurrentTurn(),
                game.getBankBalanceCents() / 100.0,
                game.getTotalAbandoned(),
                game.getMeanCustomerSatisfaction());
        System.out.println("--------------------------------------------------");
        System.out.println("Happy Hour: " + game.getHappyHour().getStatusText());
        // Display Waiting Queue
        Queue<Customer> queue = game.getWaitingQueue();
        System.out.print("Waiting Queue (" + queue.size() + " waiting): ");
        if (queue.isEmpty()) {
            System.out.println("[Empty]");
        } else {
            System.out.print("[ ");
            for (Customer c : queue) {
                System.out.printf("Cust #%d (%s, Sat:%d, Wants:%s) ",
                        c.getId(), c.getTypeName(), c.getSatisfaction(), c.getItem().getName());
            }
            System.out.println("]");
        }

        // Display Tables
        System.out.println("\nTables Status:");
        for (Table t : game.getTables()) {
            if (!t.isOccupied()) {
                System.out.printf("  Table %d: [ EMPTY ]\n", t.getTableId());
            } else {
                Customer c = t.getCurrentCustomer();
                Order o = t.getCurrentOrder();
                String orderStr = (o == null) ? "No Order Yet" : o.getItem().getName() + " (" + o.getStatus() + ")";
                System.out.printf("  Table %d: Cust #%d (%s, Sat:%d) | Order: %s\n",
                        t.getTableId(), c.getId(), c.getTypeName(), c.getSatisfaction(), orderStr);
            }
        }
        System.out.println();
    }

// --- MANAGER ACTION PHASE ---
    public void promptStaffActions() {
        System.out.println("=== ASSIGN STAFF ACTIONS (1 action each per turn) ===");

        // 0. HAPPY HOUR (Stage 3): decided at the start of the turn
        promptHappyHour();

        // 0b. HOST ACTION (Stage 3): seats first so the Waiter can take the order this turn
        promptHostAction();

        // 1. WAITER ACTION (Acts FIRST of the kitchen-side staff)
        promptWaiterAction();

        // 2. CHEF ACTION (Acts SECOND)
        promptChefAction();

        // 3. CASHIER ACTION
        promptCashierAction();
    }

    private void promptHappyHour() {
        if (!game.getHappyHour().isAvailable()) {
            return;
        }
        System.out.println("\n[Happy Hour] Available once per game. Lasts 2 turns (this turn + next):");
        System.out.println("  20% off orders taken, +15 satisfaction to customers still waiting.");
        System.out.println("  1. Activate Happy Hour now");
        System.out.println("  2. Not now");
        int choice = validator.getIntInRange("Select (1-2): ", 1, 2);
        if (choice == 1 && game.activateHappyHour()) {
            System.out.println(">> HAPPY HOUR ACTIVATED! Remaining: "
                    + game.getHappyHour().getTurnsRemaining() + " turn(s).");
            System.out.println(">> Waiting customers recovered +" + game.getHappyHour().getRecoveryPoints()
                    + " satisfaction (max 100).");
        }
    }

    private void promptHostAction() {
        while (!game.getHost().hasActed()) {
            System.out.println("\n[Host Options]:");
            System.out.println("  1. Seat a customer from queue");
            System.out.println("  2. Skip host action");

            int choice = validator.getIntInRange("Select action (1-2): ", 1, 2);
            if (choice == 2) {
                System.out.println(">> Host skipped this turn.");
                break;
            }
            int tableId = validator.getIntInRange(
                    "Enter table number to seat customer (1-2): ", 1, 2);
            if (game.hostSeatCustomer(tableId)) {
                System.out.printf(">> Success: Host seated a customer at Table %d!%n", tableId);
            } else {
                System.out.println(
                        ">> Action Failed: Queue is empty or Table is already occupied. Re-select!");
            }
        }
    }

    private void promptWaiterAction() {
        while (!game.getWaiter().hasActed()) {
            System.out.println("\n[Waiter Options]:");
            System.out.println("  1. Seat a customer from queue");
            System.out.println("  2. Take order from a table");
            System.out.println("  3. Serve ready dish to a table");
            System.out.println("  4. Skip waiter action");

            int choice = validator.getIntInRange(
                    "Select action (1-4): ", 1, 4);

            switch (choice) {
                case 1: {
                    int tableId = validator.getIntInRange(
                            "Enter table number to seat customer (1-2): ", 1, 2);

                    if (game.waiterSeatCustomer(tableId)) {
                        System.out.printf(
                                ">> Success: Customer seated at Table %d!%n", tableId);
                    } else {
                        System.out.println(
                                ">> Action Failed: Queue is empty or Table is already occupied. Re-select!");
                    }
                    break;
                }

                case 2: {
                    int tableId = validator.getIntInRange(
                            "Enter table number to take order from (1-2): ", 1, 2);

                    if (game.waiterTakeOrder(tableId)) {
                        Order placed = game.getTable(tableId).getCurrentOrder();
                        System.out.printf(
                                ">> Success: Order taken for Table %d: %s, bill $%.2f%n",
                                tableId, placed.getItem().getName(),
                                placed.getLockedPriceCents() / 100.0);
                        if (game.getHappyHour().isActive()) {
                            System.out.println(">> (Happy Hour "
                                    + game.getHappyHour().getDiscountPercent()
                                    + "% off applied; price is locked)");
                        }
                    } else {
                        System.out.println(
                                ">> Action Failed: Table empty or order already placed. Re-select!");
                    }
                    break;
                }

                case 3: {
                    int tableId = validator.getIntInRange(
                            "Enter table number to serve (1-2): ", 1, 2);

                    if (game.waiterServeDish(tableId)) {
                        System.out.printf(
                                ">> Success: Dish served to Table %d! Customer is now eating.%n",
                                tableId);
                        String reaction = game.getTable(tableId).getCurrentCustomer().getServiceReaction();
                        if (reaction != null) {
                            System.out.println(">> " + reaction);
                        }
                    } else {
                        System.out.println(
                                ">> Action Failed: Dish is NOT ready, or was just cooked this turn (lag rule)! Re-select!");
                    }
                    break;
                }

                case 4:
                    System.out.println(">> Waiter skipped this turn.");
                    break;
            }

            if (choice == 4) {
                break;
            }
        }
    }

    private void promptChefAction() {
        while (!game.getChef().hasActed()) {
            System.out.println("\n[Chef Options]:");
            System.out.println("  1. Cook an order for a table");
            System.out.println("  2. Skip chef action");

            int choice = validator.getIntInRange(
                    "Select action (1-2): ", 1, 2);

            switch (choice) {
                case 1: {
                    int tableId = validator.getIntInRange(
                            "Enter table number to cook for (1-2): ", 1, 2);
                    Order cur = game.getTable(tableId).getCurrentOrder();
                    if (game.chefCookDish(tableId)) {
                        if (cur.cooked()) {
                            System.out.printf(
                                    ">> Success: Chef prepared the dish for Table %d! (Dish is now READY)%n",
                                    tableId);
                        } else {
                            System.out.printf(
                                    ">> Success: Chef prepared the dish for Table %d! (Dish is now %d out of %d)%n",
                                    tableId,
                                    cur.getPrepCount(),
                                    cur.getItem().getPrepUnits());
                        }
                    } else {
                        System.out.println(
                                ">> Action Failed: No pending ORDERED dish at that table. Re-select!");
                    }

                    break;
                }

                case 2:
                    System.out.println(">> Chef skipped this turn.");
                    break;
            }

            if (choice == 2) {
                break;
            }
        }
    }

    private void promptCashierAction() {
        System.out.println("\n[Cashier Actions]:");
        if (game.cashierCollectPayment()) {
            System.out.println(">> Success: Payment collected from oldest finished customer! Table freed.");
        } else {
            System.out.println(">> No customer is currently READY_TO_PAY. Moving on...");
        }
    }

    // --- END-OF-TURN DISPLAY ---
    public void displayEndOfTurnSummary() {
        System.out.println("\n--------------------------------------------------");
        System.out.println(">>> END OF TURN SUMMARY <<<");
        System.out.println("Served Count: " + game.getServedCount());
        System.out.println("Unhappy Departures: " + game.getTotalAbandoned());
        System.out.printf("Revenue: $%.2f\n", game.getBankBalanceCents() / 100.0);

        // Average satisfaction of paid customers: show N/A if no one has paid yet
        String avgSat = (game.getPaidCustomerCount() == 0)
                ? "N/A"
                : String.valueOf(game.getAveragePaidCustomerSatisfaction());
        System.out.println("Average Satisfaction of Paid Customers: " + avgSat);

        System.out.println("Turns Used: " + game.getTurnsUsed());
        System.out.println("--------------------------------------------------\n");
    }

    // --- VICTORY / DEFEAT DISPLAY ---
    public void displayGameResult() {
        System.out.println("\n==================================================");
        if (game.isVictory()) {
            System.out.println("               VICTORY ACHIEVED!                  ");
            System.out.printf(" Final Bank Balance : $%.2f\n", game.getBankBalanceCents() / 100.0);
            System.out.printf(" Total Abandonments : %d\n", game.getTotalAbandoned());
            System.out.println(" Excellent restaurant management!");
        } else {
            System.out.println("                   GAME OVER                      ");
            System.out.printf(" Final Bank Balance : $%.2f\n", game.getBankBalanceCents() / 100.0);
            System.out.printf(" Total Abandonments : %d\n", game.getTotalAbandoned());
            System.out.println(" Restaurant failed to meet the target requirements.");
        }
        System.out.println("==================================================");
    }
}
