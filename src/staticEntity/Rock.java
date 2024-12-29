package staticEntity;

import model.Entity;

public class Rock extends Entity {

    @Override
    public String getSymbol() {
        return "R";
    }

    public Rock(int x, int y) {
        super(x,y);
    }
}
