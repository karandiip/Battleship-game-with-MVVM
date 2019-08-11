package com.soen6441.battleship.data.model;

import com.soen6441.battleship.enums.CellState;

import java.io.Serializable;

/**
 * Will contain information for a single cell on the grid.
 * Information includes:
 * 1. CellState: Current state of the cell.
 * 2. Ship: Reference to the ship object on the cell (if any).
 */
public class CellInfo implements Serializable {
    /**
     * Current state of the cell.
     * Check {@link CellState}
     */
    private CellState state;


    /**
     * Reference to the {@link Ship} object (if any)
     */
    private Ship ship;


    public CellInfo() {}

    public CellInfo(CellState state, Ship ship) {
        this.state = state;
        this.ship = ship;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }
}
