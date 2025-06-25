package com.example.controller.enums;

import com.example.model.Entity;
import com.example.model.creature.Creature;
import com.example.model.staticEntity.Food;
import com.example.model.staticEntity.Poison;
import com.example.model.staticEntity.Rock;

public enum EntityType {

    NOTHING(2),
    FOOD(4),
    POISON(5),
    CREATURE(6),
    ROCK(3);

    private final int offset;

    EntityType(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public static EntityType fromEntity(Entity entity) {
        if (entity == null) return EntityType.NOTHING;
        if (entity instanceof Food) return EntityType.FOOD;
        if (entity instanceof Poison) return EntityType.POISON;
        if (entity instanceof Creature) return EntityType.CREATURE;
        if (entity instanceof Rock) return EntityType.ROCK;
        return EntityType.NOTHING;
    }
}
