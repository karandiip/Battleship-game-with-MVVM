package com.soen6441.battleship.exceptions;

/**
 * Trying to place ship on coordinates that are either invalid or already
 * have a ship on it.
 */
public class InvalidShipPlacementException extends Exception {
    public InvalidShipPlacementException() {
        super("Ship cannot be placed at these coordinate!");
    }
}
