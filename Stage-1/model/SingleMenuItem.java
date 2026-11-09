package model;

public class SingleMenuItem implements MenuItem {

    private final String name;
    private final int prepUnits;
    private final int priceCents;

    public SingleMenuItem(String name, int prepUnits, int priceCents) {
        this.name = name;
        this.prepUnits = prepUnits;
        this.priceCents = priceCents;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPriceCents() {
        return priceCents;
    }

    @Override
    public int getPrepUnits() {
        return prepUnits;
    }

    @Override
    public String getPriceDollars() {
        return String.format("$%.2f", priceCents / 100.0);
    }

}
