package model;

import java.util.List;

/**
 * Stage 2: Critic Customer subclass.
 Loses 12 satisfaction points per waiting turn (impatient).
 Pays full price (no discount).
 Special Behaviour 1 (Order Preference): Always orders the most complex/expensive
 item on the menu (highest preparation units, then highest price -> Pasta) to test the kitchen.
 Special Behaviour 2 (Reaction to Service): If served promptly (within 3 turns of arrival),
 the Critic is impressed by the service and receives a +10 satisfaction boost (max 100).
 */
public class CriticCustomer extends Customer {

    public CriticCustomer(int id, int arriveTurn) {
        super(id, arriveTurn);
    }

    @Override
    public int getDecayRate() {
        return 12; // Critic loses 12 satisfaction per waiting turn
    }

    @Override
    public int applyDiscount(int basePriceCents) {
        return basePriceCents; // Critic pays full price
    }

    @Override
    public String getTypeName() {
        return "Critic";
    }

    @Override
    public MenuItem chooseFood(List<MenuItem> menu) {
        // Critic always chooses the dish with the highest prep units (and highest price if tied)
        MenuItem chosen = menu.get(0);
        for (MenuItem m : menu) {
            if (m.getPrepUnits() > chosen.getPrepUnits()
                    || (m.getPrepUnits() == chosen.getPrepUnits() && m.getPriceCents() > chosen.getPriceCents())) {
                chosen = m;
            }
        }
        return chosen;
    }

    public void onServed(int currentTurn) {
        // Reaction to service: if served within 3 turns of arrival, boost satisfaction by 10
        if (currentTurn - getArrivalTurn() <= 3) {
            boostSatisfaction(10);
        }
    }
}