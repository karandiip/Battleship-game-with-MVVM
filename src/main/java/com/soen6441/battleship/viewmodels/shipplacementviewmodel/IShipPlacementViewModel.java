package com.soen6441.battleship.viewmodels.shipplacementviewmodel;

import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import io.reactivex.Observable;

/**
 * The interface Ship placement view model.
 */
public interface IShipPlacementViewModel {
    /**
     * Place ship: This method is and interface for the place ship method which validates the
     * data from controller and passes to model.
     *
     * @param ship the ship
     */
    void placeShip(Ship ship);

    /**
     * Can a ship be placed at certain coordinates on the grid.
     *
     * @param ship the ship
     */
    boolean canPlaceShip(Ship ship);

    GameGrid getPlayerGameGrid();
}
