package model;

/** One-time, two-turn 20% discount with satisfaction recovery. */
public class HappyHour {

    public static final int DURATION_TURNS = 2;
    private static final int DISCOUNT_PERCENT = 20;
    private static final int RECOVERY_POINTS = 15;

    private boolean used = false;
    private int turnsRemaining = 0;

    /** Switches Happy Hour on. */
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

    /** Returns the price after the Happy Hour discount, or unchanged when inactive. */
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
