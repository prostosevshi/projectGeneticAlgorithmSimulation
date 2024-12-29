package staticEntity;

import model.Entity;

public class Grass extends Entity {

    @Override
    public String setSymbol() {
        return "G";
    }

    @Override
    public void update() {

    }

    public Grass(int x, int y) {
        super(x,y);
    }
}
