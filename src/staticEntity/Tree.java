package staticEntity;

import model.Entity;

public class Tree extends Entity {

    @Override
    public String setSymbol() {
        return "T";
    }

    @Override
    public void update() {

    }

    public Tree(int x, int y) {
        super(x,y);
    }
}
