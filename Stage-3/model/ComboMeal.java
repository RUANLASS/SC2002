package model;

/**
 * Stage 3: a combo is built by COMPOSING two existing menu items.
 *
 * It implements MenuItem, so Order, Chef, Waiter and Cashier handle it exactly
 * like a single dish and none of them had to change (open for extension).
 *
 *  - Preparation units = units of the first item + units of the second item.
 *  - Price = 90% of the two prices combined, rounded half-up to the nearest cent
 *    (integer arithmetic, no floating point).
 *    Example: Salad + Pasta = (900 + 1800) * 90% = 2430 cents = $24.30, 3 units.
 */
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
