package model;

import java.util.List;

/**
 * Stage 3: the documented rule a customer follows to decide on a combo.
 *
 * RULE (deterministic, no randomness):
 *   1. The customer first picks a single dish exactly as in Stage 1 / 2
 *      (Regular and VIP cycle by id, Critic wants the most demanding dish).
 *      That dish is the customer's "preferred dish".
 *   2. A customer with an EVEN id upgrades to a combo of
 *      (preferred dish) + (the next dish on the menu, wrapping around).
 *      A customer with an ODD id keeps the single dish.
 *
 * On the base menu (Salad, Burger, Pasta) this gives:
 *   preferred Salad  -> Salad + Burger Combo  ($21.60, 2 units)
 *   preferred Burger -> Burger + Pasta Combo  ($29.70, 3 units)
 *   preferred Pasta  -> Pasta + Salad Combo   ($24.30, 3 units)
 *
 * The rule lives here, not in the turn controller or the CLI, and the
 * customer's own type-specific food preference is still respected.
 */
public class ComboPolicy {

    public boolean wantsCombo(Customer customer) {
        return customer.getId() % 2 == 0;
    }

    public MenuItem choose(Customer customer, MenuItem preferred, List<MenuItem> menu) {
        if (!wantsCombo(customer) || menu.size() < 2) {
            return preferred;
        }
        int next = (menu.indexOf(preferred) + 1) % menu.size();
        return new ComboMeal(preferred, menu.get(next));
    }
}
