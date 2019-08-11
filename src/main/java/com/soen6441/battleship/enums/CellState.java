package com.soen6441.battleship.enums;

/**
 * The enum Cell state is the cell properties which are empty, ship empty hit, ship with hit and destroyed ship.
 */
public enum CellState {
    /**
     * There is nothing on the cell,
     * but there will be a hit on it soon.
     */
    TO_BE_PLACED,
    /**
     * The Empty.
     * When cell has nothing on it,
     * no hit or ship whatsoever.
     */
    EMPTY,
    /**
     * The Ship.
     * When a cell has ship on it.
     */
    SHIP,
    /**
     * Cell is hit, but there was no ship on it.
     */
    EMPTY_HIT,
    /**
     * Cell is hit, and there is ship on it.
     */
    SHIP_WITH_HIT,
    /**
     * Cell contains a ship that has been destroyed.
     */
    DESTROYED_SHIP,
}
