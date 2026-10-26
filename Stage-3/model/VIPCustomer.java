package model;

/**
 * Stage 2: VIP customer.
 * Loses 5 satisfaction per waiting turn and gets 10% off the bill.
 * Follows the same order and service flow as every other customer.
 */
public class VIPCustomer extends Customer {

    private static final int VIP_DECAY_RATE = 5;
    private static final int DISCOUNT_PERCENT = 10;

    public VIPCustomer(int id, int arriveTurn) {
        super(id, arriveTurn);
    }

    @Override
    public int getDecayRate() {
        return VIP_DECAY_RATE;
    }

    @Override
    public int applyDiscount(int basePriceCents) {
        // Integer cents, rounded half up: no floating point in money.
        return (basePriceCents * (100 - DISCOUNT_PERCENT) + 50) / 100;
    }

    @Override
    public String getTypeName() {
        return "VIP";
    }
}
