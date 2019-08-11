package com.soen6441.battleship.data.interfaces;


/**
 * Functional Interface to track user hits on the grid.
 */

import com.soen6441.battleship.data.model.Coordinate;

@FunctionalInterface
public interface HitCallback {

    /**
     * Get hit coordinates.
     */
    void onHit(Coordinate coordinate);
}
