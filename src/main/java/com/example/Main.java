package com.example;

import com.example.controller.enums.MapStats;
import com.example.controller.simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Simulation simulation = new Simulation();
        simulation.initializeSimulation(MapStats.WIDTH, MapStats.HEIGHT);
        simulation.setParameters(MapStats.NUMBEROFFOOD, MapStats.NUMBEROFPOISON, MapStats.NUMBEROFCREATURES);
        simulation.start();
    }
}