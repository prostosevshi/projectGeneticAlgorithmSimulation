package service;

import controller.WorldMap;
import staticEntity.Grass;

import java.util.Random;

public class GenerateGrassAction implements Action {

    private final int grassCountPerTurn;

    public GenerateGrassAction(int grassCountPerTurn) {
        this.grassCountPerTurn = grassCountPerTurn;
    }

    @Override
    public void execute(WorldMap worldMap) {
        Random random = new Random();

        for (int i = 0; i < grassCountPerTurn; i++) {
            int x = random.nextInt(worldMap.getWidth());
            int y = random.nextInt(worldMap.getHeight());

            if (worldMap.isCellFree(x, y)) {
                Grass grass = new Grass(x, y);
                worldMap.addEntity(grass);
            }
        }
    }
}
