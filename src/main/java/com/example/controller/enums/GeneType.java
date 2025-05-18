package com.example.controller.enums;

public enum GeneType {
    MOVE,
    INTERACT,
    UNKNOWN;

    public static GeneType fromGene(int gene) {
        if (gene >= 0 && gene <= 7) return MOVE;
        if (gene >= 8 && gene <= 15) return INTERACT;
        return UNKNOWN;
    }
}
