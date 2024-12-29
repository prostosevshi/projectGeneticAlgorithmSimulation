import controller.Simulation;
import movingEntity.Herbivore;
import movingEntity.Predator;
import service.GenerateGrassAction;
import service.MoveCreaturesAction;
import service.PredatorAttackAction;

public class Main {
    public static void main(String[] args) {

        Simulation simulation = new Simulation(10, 5);

        Herbivore herbivore1 = new Herbivore(2, 2, 1, 10);
        Herbivore herbivore2 = new Herbivore(5, 5, 1, 10);
        Predator predator = new Predator(4, 4, 1, 15, 5);

        simulation.getWorldMap().addEntity(herbivore1);
        simulation.getWorldMap().addEntity(herbivore2);
        simulation.getWorldMap().addEntity(predator);

        simulation.addTurnAction(new MoveCreaturesAction());    // Движение существ
        simulation.addTurnAction(new GenerateGrassAction(3));  // Генерация травы
        simulation.addTurnAction(new PredatorAttackAction());

        simulation.startSimulation();
    }
}