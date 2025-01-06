import controller.Simulation;

public class Main {
    public static void main(String[] args) {

        Simulation simulation = new Simulation(10, 5);
        simulation.setParameters(5, 5, 2);
        simulation.start();
    }
}