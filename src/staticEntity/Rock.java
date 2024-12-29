package staticEntity;

import model.Entity;

public class Rock extends Entity {

    @Override
    public String setSymbol() {
        return "R";
    }

    @Override
    public void update() {

    }

    public Rock(int x, int y) {
        super(x,y);
    }

    public Rock(){}
}
