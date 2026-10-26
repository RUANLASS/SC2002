package model;

import java.util.List;

/**
 * Stage 2: Critic customer.
 * Loses 12 satisfaction per waiting turn and pays full price.
 *
 * Special behaviour (documented in the README):
 *  1. Order preference: always orders the most demanding dish on the menu
 *     (most preparation units, then highest price). On the base menu: Pasta.
 *  2. Reaction to service: if the dish is served within 3 turns of the
 *     Critic's arrival (the fastest possible service), the Critic is
 *     impressed and gains 10 satisfaction (capped at 100). Slower service
 *     earns no bonus. This rewards the manager for prioritising the Critic.
 *
 * Both behaviours live in this class. The controller never checks for it.
 */
public class CriticCustomer extends Customer {

    private static final int CRITIC_DECAY_RATE = 12;
    private static final int IMPRESSED_WITHIN_TURNS = 3;
    private static final int IMPRESSED_BONUS = 10;

    private String reaction;

    public CriticCustomer(int id, int arriveTurn) {
        super(id, arriveTurn);
    }

    @Override
    public int getDecayRate() {
        return CRITIC_DECAY_RATE;
    }

    @Override
    public String getTypeName() {
        return "Critic";
    }

    @Override
    protected MenuItem selectItem(List<MenuItem> menu) {
        MenuItem hardest = menu.get(0);
        for (MenuItem m : menu) {
            boolean moreUnits = m.getPrepUnits() > hardest.getPrepUnits();
            boolean sameUnitsPricier = m.getPrepUnits() == hardest.getPrepUnits()
                    && m.getPriceCents() > hardest.getPriceCents();
            if (moreUnits || sameUnitsPricier) {
                hardest = m;
            }
        }
        return hardest;
    }

    @Override
    public void onServed(int currentTurn) {
        int waited = currentTurn - getArrivalTurn();
        if (waited <= IMPRESSED_WITHIN_TURNS) {
            boostSatisfaction(IMPRESSED_BONUS);
            reaction = "Critic #" + getId() + " is impressed by the fast service! +"
                    + IMPRESSED_BONUS + " satisfaction (now " + getSatisfaction() + ").";
        } else {
            reaction = "Critic #" + getId() + " was kept waiting " + waited
                    + " turns and is unimpressed. No bonus.";
        }
    }

    @Override
    public String getServiceReaction() {
        return reaction;
    }
}
