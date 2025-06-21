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

    public Direction rotate(int offset) {
        int newOrdinal = (this.ordinal() + offset) % 8;
        if (newOrdinal < 0) newOrdinal += 8;
        return values()[newOrdinal];
    }

    public Direction getRelativeDirection(int offset) {
        return rotate(offset);
    }
}
