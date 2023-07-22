package com.kdecosta.lynx.util;

import net.minecraft.util.math.Direction;

public class DirectionalUtil {

    public static Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case UP -> {
                return Direction.DOWN;
            }
            case DOWN -> {
                return Direction.UP;
            }
            case NORTH -> {
                return Direction.SOUTH;
            }
            case SOUTH -> {
                return Direction.NORTH;
            }
            case EAST -> {
                return Direction.WEST;
            }
            case WEST -> {
                return Direction.EAST;
            }
            default -> {
                return direction;
            }
        }
    }
}
