package engine;

import java.util.*;
import model.*;
import staff.*;

public class RestaurantGame {

    private int currentTurn = 1;
    private int bankBalanceCents = 0;
    private int totalAbandoned = 0;
    private int customerIdCounter = 1;
    private int servedCount = 0;
    private int paidCustomerCount = 0;
    private int totalCustomerSatisfaction = 0;

    private final List<Table> tables = new ArrayList<>();
    private final Queue<Customer> waitingQueue = new LinkedList<>();
    private final List<MenuItem> menu = new ArrayList<>();

    // Staff instances
    private final Waiter waiter = new Waiter();
    private final Chef chef = new Chef();
    private final Cashier cashier = new Cashier();

    // Stage 3: new staff role, combo rule and pricing policy
    private final Host host = new Host();
    private final ComboPolicy comboPolicy = new ComboPolicy();
    private final HappyHour happyHour = new HappyHour();

    // Stage 2: which customer type arrives on which turn
    private final ArrivalSchedule schedule;

    public RestaurantGame() {
        this(ArrivalSchedule.baseSetting());
    }

    public RestaurantGame(ArrivalSchedule schedule) {
        this.schedule = schedule;
        //initialise 2 tables and the menu
        for (int i = 1; i < 3; i++) {
            tables.add(new Table(i));
        }
        menu.add(new SingleMenuItem("Salad", 1, 900));
        menu.add(new SingleMenuItem("Burger", 1, 1500));
        menu.add(new SingleMenuItem("Pasta", 2, 1800));
    }

    //Step 1: Handle arrival
    public void step1_processArrivals() {
        // Reset staff actions for the new turn
        waiter.resetTurn();
        chef.resetTurn();
        cashier.resetTurn();
        host.resetTurn();

        // Arrivals on turns 1, 4, 7, 10, 13, 16 (customer type decided by the schedule)
        Customer newCust = schedule.createArrival(currentTurn, customerIdCounter);
        if (newCust != null) {
            customerIdCounter++;
            newCust.chooseFood(menu, comboPolicy); // Stage 3: may choose a combo
            waitingQueue.add(newCust);
        }

        // Stage 3: Happy Hour recovery at turn start, from the 2nd active turn on
        // (on the activation turn it is applied inside activateHappyHour()).
        if (happyHour.isActive()) {
            applyHappyHourRecovery();
        }
    }

    // --- Stage 3: Happy Hour ---

    /** Manager switches Happy Hour on at the start of a turn. Once per game. */
    public boolean activateHappyHour() {
        if (!happyHour.activate()) {
            return false;
        }
        applyHappyHourRecovery(); // recovery for the activation turn
        return true;
    }

    /** +15 satisfaction (max 100) to every customer still waiting for a table or food. */
    private void applyHappyHourRecovery() {
        int points = happyHour.getRecoveryPoints();
        for (Customer c : waitingQueue) {
            c.boostSatisfaction(points);
        }
        for (Table t : tables) {
            if (t.isOccupied() && isStillWaiting(t.getCurrentOrder())) {
                t.getCurrentCustomer().boostSatisfaction(points);
            }
        }
    }

    /** A seated customer is still waiting if not yet served (no order, ORDERED or READY). */
    private static boolean isStillWaiting(Order o) {
        return o == null || o.getStatus() == OrderStatus.ORDERED || o.getStatus() == OrderStatus.READY;
    }

    // --- STEP 2: Manager Actions Phase (Delegated to Staff) ---

    public boolean hostSeatCustomer(int tableId) {
        return host.seatCustomer(waitingQueue, getTable(tableId));
    }

    public boolean waiterSeatCustomer(int tableId) {
        return waiter.seatCustomer(waitingQueue, getTable(tableId));
    }

    public boolean waiterTakeOrder(int tableId) {
        return waiter.takeOrder(getTable(tableId), happyHour);
    }

    public boolean waiterServeDish(int tableId) {
        boolean served = waiter.serveDish(getTable(tableId), currentTurn);
        if (served) {
            servedCount++;
        }
        return served;
    }

    public boolean chefCookDish(int tableId) {
        return chef.cookDish(getTable(tableId), currentTurn);
    }

    public boolean cashierCollectPayment() {
        Cashier.PaymentResult payment = cashier.collectPayment(tables);
        if (payment != null) {
            int amount = payment.amountPaid();
            int custSat = payment.custSat();
            bankBalanceCents += amount;
            totalCustomerSatisfaction += custSat;
            paidCustomerCount++;
            return true;
        }
        return false;
    }

    // --- STEPS 4, 5, 6: Turn Resolution ---
    public void resolveTurnEndPhases() {
        // Step 4: Tick Eating
        for (Table t : tables) {
            if (t.isOccupied() && t.getCurrentOrder() != null) {
                t.getCurrentOrder().tickEating();
            }
        }

        // Step 5: Decay Satisfaction & Check Abandonment
        // A. Waiting queue
        Iterator<Customer> qIter = waitingQueue.iterator();
        while (qIter.hasNext()) {
            Customer c = qIter.next();
            c.decaySatisfaction();
            if (c.hasAbandoned()) {
                qIter.remove();
                totalAbandoned++;
            }
        }

        // B. Seated customers waiting for order / food
        for (Table t : tables) {
            if (t.isOccupied()) {
                Customer c = t.getCurrentCustomer();
                Order o = t.getCurrentOrder();
                // Decay satisfaction if food has not been served yet
                if (isStillWaiting(o)) {
                    c.decaySatisfaction();
                    if (c.hasAbandoned()) {
                        if (o != null) {
                            o.cancel();
                        }
                        t.clear();
                        totalAbandoned++;
                    }
                }
            }
        }

        // Stage 3: Happy Hour counts down at the end of each turn
        happyHour.endTurn();

        // Step 6: Advance turn
        currentTurn++;
    }

    public boolean isGameOver() {
        return currentTurn > 18;
    }

    public boolean isVictory() {
        // Targets from the brief: >= 4 paid, revenue >= $45.00, average satisfaction >= 60.
        // (average >= 60 is tested as total >= 60 * paid to avoid integer truncation)
        return paidCustomerCount >= 4
                && bankBalanceCents >= 4500
                && totalCustomerSatisfaction >= 60 * paidCustomerCount;
    }

    // Getters and Helpers
    public Table getTable(int id) {
        for (Table t : tables) {
            if (t.getTableId() == id) {
                return t;
            }
        }
        return null;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getBankBalanceCents() {
        return bankBalanceCents;
    }

    public int getTotalAbandoned() {
        return totalAbandoned;
    }

    public int getServedCount() {
        return servedCount;
    }

    public int getPaidCustomerCount() {
        return paidCustomerCount;
    }

    public int getAveragePaidCustomerSatisfaction() {
        return paidCustomerCount == 0 ? 0 : totalCustomerSatisfaction / paidCustomerCount;
    }

    public int getTurnsUsed() {
        return Math.min(currentTurn - 1, 18);
    }

    public int getMeanCustomerSatisfaction() {
        return getAveragePaidCustomerSatisfaction();
    }

    public List<Table> getTables() {
        return tables;
    }

    public Queue<Customer> getWaitingQueue() {
        return waitingQueue;
    }

    public Host getHost() {
        return host;
    }

    public HappyHour getHappyHour() {
        return happyHour;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public Chef getChef() {
        return chef;
    }

    public Cashier getCashier() {
        return cashier;
    }
}
