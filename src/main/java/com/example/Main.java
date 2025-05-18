package com.example;

import com.example.controller.simulation.Simulation;

public class Main {
    public static void main(String[] args) {

        Simulation simulation = new Simulation();
        simulation.initializeSimulation(40, 12);
        simulation.setParameters(80, 80, 64);
        simulation.start();
    }
}