package com.soen6441.battleship.exceptions;

/**
 * The pair of x,y coordinate are out of bounds of a plane.
 */
public class CoordinatesOutOfBoundsException extends Exception {
    public CoordinatesOutOfBoundsException() {
        super("Coordinates are out of bounds!");
    }
}
