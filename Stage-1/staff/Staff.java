package staff;

public abstract class Staff {

    protected boolean actionUsed;

    public boolean hasActed() {
        return actionUsed;
    }

    public void resetTurn() {
        this.actionUsed = false;
    }
}
