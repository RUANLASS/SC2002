package model;

/**
 Stage 2: VIP Customer subclass.
  Loses 5 satisfaction points per waiting turn (more patient than Regular).
 Receives a 10% discount on their bill (locked when order is placed).
 */
public class VIPCustomer extends Customer {

    public VIPCustomer(int id, int arriveTurn) {
        super(id, arriveTurn);
    }

    @Override
    public int getDecayRate() {
        return 5; // VIP loses 5 satisfaction per waiting turn
    }

    @Override
    public int applyDiscount(int basePriceCents) {
        // 10% off the bill, rounded to the nearest cent
        return (int) Math.round(basePriceCents * 0.90);
    }

    @Override
    public String getTypeName() {
        return "VIP";
    }
}