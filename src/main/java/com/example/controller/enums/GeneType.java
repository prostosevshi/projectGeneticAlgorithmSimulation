package com.example.controller.enums;

public enum GeneType {
    MOVE,
    INTERACT,
    LOOK,
    TURN,
    //JUMP,
    UNKNOWN;

    public static GeneType fromGene(int gene) {
        if (gene >= 0 && gene <= 7) return MOVE;
        if (gene >= 8 && gene <= 15) return INTERACT;
        if (gene >= 16 && gene <= 23) return LOOK;
        if (gene >= 24 && gene <= 31) return TURN;
        //if (gene >= 32 && gene <= 39) return JUMP;
        return UNKNOWN;
    }
}
