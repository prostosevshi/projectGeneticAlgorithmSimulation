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

    public static Direction fromLookGene(int gene) {
        return values()[gene - 16]; // gene 16-23
    }

    public static Direction fromTurnGene(int gene) {
        return values()[gene - 24]; // gene 24-31
    }

    public static Direction fromGene(int gene) {
        GeneType geneType = GeneType.fromGene(gene);

        return switch (geneType) {
            case MOVE     -> values()[gene];         // 0–7
            case INTERACT -> values()[gene - 8];     // 8–15
            case LOOK     -> values()[gene - 16];    // 16–23
            case TURN     -> values()[gene - 24];    // 24–31
            default -> throw new IllegalArgumentException("Gene " + gene + " does not map to a direction");
        };
    }
}
