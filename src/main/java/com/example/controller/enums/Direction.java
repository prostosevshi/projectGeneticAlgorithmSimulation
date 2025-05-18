package com.example.controller.enums;

public enum Direction {
    UP_LEFT(-1, 1),
    UP(0, 1),
    UP_RIGHT(1, -1),
    RIGHT(1, 0),
    DOWN_RIGHT(1, 1),
    DOWN(0, 1),
    DOWN_LEFT(-1, 1),
    LEFT(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public static Direction fromMoveGene(int gene) {
        return values()[gene]; // gene 0-7
    }

    public static Direction fromInteractGene(int gene) {
        return values()[gene - 8]; // gene 8-15
    }
}
