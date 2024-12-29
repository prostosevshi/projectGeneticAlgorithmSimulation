package staticEntity;

import model.Entity;

public class Poison extends Entity {

    @Override
    public String getSymbol() {
        return "P";
    }

    public Poison(int x, int y) {
        super(x,y);
    }
}
