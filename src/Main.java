import controller.Simulation;

public class Main {
    public static void main(String[] args) {

        Simulation simulation = new Simulation(40, 12);
        simulation.setParameters(80, 80, 40);
        simulation.start();
    }
}