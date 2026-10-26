package model;

/**
 * Stage 3: Happy Hour pricing policy.
 *
 *  - Can be activated ONCE per game, at the start of a turn.
 *  - Lasts for the activation turn and the next one (2 turns).
 *  - Orders taken while active get 20% off. The discount is applied AFTER the
 *    customer's own discount (e.g. VIP) and the resulting price is locked into
 *    the Order when the order is taken, so it never changes afterwards.
 *  - While active, customers still waiting for a table or food recover
 *    +15 satisfaction at turn start (the game applies this, using
 *    {@link #getRecoveryPoints()}).
 *
 * All Happy Hour state and arithmetic is encapsulated here.
 */
public class HappyHour {

    public static final int DURATION_TURNS = 2;
    private static final int DISCOUNT_PERCENT = 20;
    private static final int RECOVERY_POINTS = 15;

    private boolean used = false;
    private int turnsRemaining = 0;

    /** Switches Happy Hour on. Fails (returns false) if it was already used. */
    public boolean activate() {
        if (used) {
            return false;
        }
        used = true;
        turnsRemaining = DURATION_TURNS;
        return true;
    }

    /** Called once at the end of every turn. */
    public void endTurn() {
        if (turnsRemaining > 0) {
            turnsRemaining--;
        }
    }

    public boolean isActive() {
        return turnsRemaining > 0;
    }

    public boolean isAvailable() {
        return !used;
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public int getRecoveryPoints() {
        return RECOVERY_POINTS;
    }

    public int getDiscountPercent() {
        return DISCOUNT_PERCENT;
    }

    /**
     * Price after Happy Hour. Pass in the price that has ALREADY had the
     * customer discount applied. Returns it unchanged when not active.
     * Rounded half-up in integer cents.
     */
    public int applyTo(int priceCents) {
        if (!isActive()) {
            return priceCents;
        }
        return (priceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    /** One-line status for the CLI. */
    public String getStatusText() {
        if (isActive()) {
            return "ACTIVE - " + turnsRemaining + " turn(s) remaining";
        }
        return used ? "ENDED (already used this game)" : "AVAILABLE (can be used once)";
    }
}
