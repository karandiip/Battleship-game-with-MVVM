package com.soen6441.battleship.viewmodels.playerviewmodel;

import com.soen6441.battleship.data.model.Grid;
import io.reactivex.Observable;

/**
 * The interface Player view model: is the interface of the player which takes data from model and displays the
 * updated data on user screen.
 */
public interface IPlayerViewModel {
    /**
     * Gets grid.
     *
     * @return the grid
     */
    Observable<Grid> getGrid();

    /**
     * Hit enemy.
     *
     * @param x the x
     * @param y the y
     */
    void hitEnemy(int x, int y);
}
