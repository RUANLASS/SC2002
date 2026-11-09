package model;

import java.util.List;

public class Customer {

    private final int id;
    private final int arriveTurn;
    private int satisfaction;
    private MenuItem item;

    public Customer(int id, int arriveTurn) {
        this.id = id;
        this.arriveTurn = arriveTurn;
        this.satisfaction = 100;
    }

    public int getDecayRate() {
        return 8; //stage 1 regular guest lose 8 per turn
    }

    public void decaySatisfaction() {
        this.satisfaction = Math.max(0, this.satisfaction - getDecayRate());
    }

    public int applyDiscount(int basePriceCents) {
        return basePriceCents; //stage 1 regular guest pays full price
    }

    public String getTypeName() {
        return "regular";
    }

    public void boostSatisfaction(int amount) {
        this.satisfaction = Math.min(100, this.satisfaction + amount);
    }

    public MenuItem chooseFood(List<MenuItem> menu) {
        //cycle based on customer id % 3
        int index = (this.id - 1) % menu.size();
        this.item = menu.get(index);
        return this.item;
    }

    public boolean hasAbandoned() {
        return this.satisfaction <= 0;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTurn() {
        return arriveTurn;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public MenuItem getItem() {
        return item;
    }
}
