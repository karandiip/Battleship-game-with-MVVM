package com.soen6441.battleship.services.gamegrid;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.enums.HitResult;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;
import com.soen6441.battleship.exceptions.DirectionCoordinatesMismatchException;
import com.soen6441.battleship.exceptions.InvalidShipPlacementException;
import io.reactivex.Observable;

import java.util.List;

public interface IGameGrid {
    /**
     * Places a ship on the grid.
     *
     * @param ship The ship to be placed
     * @throws DirectionCoordinatesMismatchException If the starting and ending coordinates in ship
     *                                               don't match with the provided direction.
     * @throws CoordinatesOutOfBoundsException       If any coordinate of the ship is not on the plane.
     * @throws InvalidShipPlacementException         If there is other ship already on one of the coordinates that
     *                                               the ship is being placed on.
     */
    void placeShip(Ship ship) throws Exception;

    /**
     * Tells if ship can be placed at certain coordinates.
     *
     * @param ship The ship to be placed.
     */
    boolean canPlaceShip(Ship ship);

    /**
     * Raw grid data structure.
     *
     * @return Grid data structure contained withing {@link GameGrid}
     */
    Grid getGrid();

    /**
     * Get the list of all ship currently present on the Grid.
     *
     * @return The list of ships currently on {@link Grid}
     */
    List<Ship> getShips();

    /*
     * Mark a hit on a specific coordinate on the board.
     * */
    HitResult hit(int x, int y) throws CoordinatesOutOfBoundsException;

    /**
     * Get the grid as an observable. Updates will be triggered if there
     * is any change on the grid.
     *
     * @return Grid wrapped inside an RxObservable.
     */
    Observable<Grid> getGridAsObservable();

    /**
     * Checks if all the ships are destroyed on the board.
     *
     * @return boolean if all the ships are destroyed on the board.
     */
    boolean areAllShipsDestroyed();

    /**
     * Get the number of ships that are still not sunk.
     *
     * @return The number of remaining ships that are not destroyed.
     */
    int getUnSunkShips();

    /**
     * Get the result of hit, but not actually hitting the board.
     *
     * @param coordinate Coordinate to hit.
     * @return result of on a particular coordinate.
     */
    HitResult peekHit(Coordinate coordinate) throws CoordinatesOutOfBoundsException;

    /**
     * Update the state of a cell depending
     *
     * @param coordinate XY coordinates of cell to update.
     * @param state New state of the cell.
     */
    void updateCellState(Coordinate coordinate, CellState state);

    public void updateGrid(Grid grid);
}
