package model;

import java.util.List;

/**
 * A regular customer (Stage 1) and the base class of every customer type.
 *
 * Stage 2: everything that differs between customer types is an overridable
 * method here (decay rate, discount, food choice, reaction to service).
 * The controller and staff only ever call these methods on a Customer
 * reference, so adding a new customer type never touches them.
 */
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

    /** Satisfaction lost per waiting turn. Regular: 8. */
    public int getDecayRate() {
        return REGULAR_DECAY_RATE;
    }

    /** Bill for a dish with the given base price. Regular pays full price. */
    public int applyDiscount(int basePriceCents) {
        return basePriceCents;
    }

    public String getTypeName() {
        return "Regular";
    }

    /**
     * Food-choice rule (deterministic). Subclasses override this, not
     * chooseFood(), so the choice is always stored in one place.
     * Regular rule: cycle through the menu by customer id.
     */
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
