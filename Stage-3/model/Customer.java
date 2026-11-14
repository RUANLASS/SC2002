package model;

import java.util.List;

/** Regular customer and base class of every customer type. */
public class Customer {

    private static final int REGULAR_DECAY_RATE = 8;

    private final int id;
    private final int arriveTurn;
    private int satisfaction;
    private MenuItem item;

    public Customer(int id, int arriveTurn) {
        this.id = id;
        this.arriveTurn = arriveTurn;
        this.satisfaction = 100;
    }

    // ---- Behaviour that subclasses override -------------------------------

    /** Satisfaction lost per waiting turn. */
    public int getDecayRate() {
        return REGULAR_DECAY_RATE;
    }

    /** Bill for a dish with the given base price. */
    public int applyDiscount(int basePriceCents) {
        return basePriceCents;
    }

    public String getTypeName() {
        return "Regular";
    }

    /** Food-choice rule; regular customers cycle through the menu by id. */
    protected MenuItem selectItem(List<MenuItem> menu) {
        int index = (this.id - 1) % menu.size();
        return menu.get(index);
    }

    /** Hook called by the Waiter when this customer's dish is served. */
    public void onServed(int currentTurn) {
        // Regular customers do not react to service.
    }

    /** Text describing the customer's reaction to being served, or null. */
    public String getServiceReaction() {
        return null;
    }

    // ---- Shared behaviour -------------------------------------------------

    public MenuItem chooseFood(List<MenuItem> menu) {
        this.item = selectItem(menu);
        return this.item;
    }

    /** Chooses food as chooseFood(menu), then may upgrade it to a combo per the policy. */
    public MenuItem chooseFood(List<MenuItem> menu, ComboPolicy comboPolicy) {
        MenuItem preferred = selectItem(menu);
        this.item = comboPolicy.choose(this, preferred, menu);
        return this.item;
    }

    public void decaySatisfaction() {
        this.satisfaction = Math.max(0, this.satisfaction - getDecayRate());
    }

    public void boostSatisfaction(int amount) {
        this.satisfaction = Math.min(100, this.satisfaction + amount);
    }

    public boolean hasAbandoned() {
        return this.satisfaction <= 0;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arriveTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public MenuItem getItem() {
        return item;
    }
}
