package com.soen6441.battleship.view.gui.scenes.gameplayscene;

import com.soen6441.battleship.data.model.Coordinate;

/**
 * The interface On coordinate hit.
 */
@FunctionalInterface()
public interface IOnCoordinateHit {
    /**
     * On hit.
     *
     * @param coordinate the coordinate
     */
    void onHit(Coordinate coordinate);
}
