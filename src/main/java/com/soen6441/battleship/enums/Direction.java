package com.soen6441.battleship.enums;

/**
 * Represents a direction.
 */
public enum Direction {
    /**
     * Upward direction.
     */
    UP(0),

    /**
     * Downward direction.
     */
    DOWN(1),


    /**
     * Left direction.
     */
    LEFT(2),

    /**
     * Right direction.
     */
    RIGHT(3);

    /**
     * Unique code associated with every direction.
     */
    public final int code;

    Direction(int code) {
        this.code = code;
    }

    /**
     * @param code to get {@link Direction} from.
     * @return {@link Direction} associated to a particular code. If the code is wrong
     * by default RIGHT is returned.
     */
    public static Direction getFromCode(int code) {
        switch (code) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.DOWN;
            case 2:
                return Direction.LEFT;
            default:
                return Direction.RIGHT;
        }
    }
}
