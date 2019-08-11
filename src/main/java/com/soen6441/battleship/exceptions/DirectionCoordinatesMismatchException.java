package com.soen6441.battleship.exceptions;

/**
 * Directions deduced from start and end coordinates don't match the
 * actual {@link com.soen6441.battleship.enums.ShipDirection} passed.
 */
public class DirectionCoordinatesMismatchException extends Exception {
    public DirectionCoordinatesMismatchException() {
        super("Coordinates don't match the direction on ship!");
    }
}
