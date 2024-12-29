package movingEntity;

import controller.WorldMap;
import model.Entity;

import java.util.Random;

public class Creature extends Entity {

    protected int health;
    private double[] genome;
    private Random random = new Random();

    public Creature(int x, int y, double[] genome) {
        super(x, y);
        this.health = 10;
        this.genome = genome;
    }

    public void makeMove(WorldMap worldMap) {
        this.health -= 1;
        if (health <= 0) {
            worldMap.removeEntity(worldMap.getEntityAt(this.getX(), this.getY()));
            return;
        }

        double action = random.nextDouble();
        if (action < genome[0]) {
            moveTowardsFood(worldMap);
        } else if (action < genome[0] + genome[1]) {
            convertPoison(worldMap);
        } else {
            moveRandomly(worldMap);
        }
    }

    private void moveRandomly(WorldMap worldMap) {
    }

    private void convertPoison(WorldMap worldMap) {
        
    }

    private void moveTowardsFood(WorldMap worldMap) {
        
    }

    @Override
    public String getSymbol() {
        return String.valueOf(getHealth());
    }

    //getters&setters

    public int getHealth() {
        return health;
    }

    public void changeHealth(int delta) {
        this.health += delta;
    }

    public double[] getGenome() {
        return genome;
    }
}
