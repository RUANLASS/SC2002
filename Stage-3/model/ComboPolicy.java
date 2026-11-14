package model;

import java.util.List;

/** Decides which customers upgrade their preferred dish to a combo (even ids). */
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
