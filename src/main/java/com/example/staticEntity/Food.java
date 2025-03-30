package com.example.staticEntity;

import com.example.model.Entity;

public class Food extends Entity {

    @Override
    public String getSymbol() {
        return "F";
    }

    public Food(int x, int y) {
        super(x,y);
    }
}
