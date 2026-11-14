package model;

/** Menu item composed of two items, priced at 90% of their combined price. */
public class ComboMeal implements MenuItem {

    private static final int COMBO_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("A combo needs two menu items.");
        }
        this.first = first;
        this.second = second;
    }

    @Override
    public String getName() {
        return first.getName() + " + " + second.getName() + " Combo";
    }

    @Override
    public int getPriceCents() {
        int combined = first.getPriceCents() + second.getPriceCents();
        return (combined * COMBO_PERCENT + 50) / 100;
    }

    @Override
    public int getPrepUnits() {
        return first.getPrepUnits() + second.getPrepUnits();
    }

    @Override
    public String getPriceDollars() {
        return String.format("$%.2f", getPriceCents() / 100.0);
    }

    public MenuItem getFirst() {
        return first;
    }

    public MenuItem getSecond() {
        return second;
    }
}
