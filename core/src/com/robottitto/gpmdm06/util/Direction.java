package com.robottitto.gpmdm06.util;

public class Direction {

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;

    public static int getOppositeDirection(int direction) {
        int oppositeDirection;
        switch (direction) {
            case LEFT:
                oppositeDirection = RIGHT;
                break;
            case RIGHT:
                oppositeDirection = LEFT;
                break;
            case UP:
                oppositeDirection = DOWN;
                break;
            case DOWN:
                oppositeDirection = UP;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
        return oppositeDirection;
    }

}
