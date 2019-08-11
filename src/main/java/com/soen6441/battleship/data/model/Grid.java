package com.soen6441.battleship.data.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.soen6441.battleship.enums.CellState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the grid as a plane of xy-coordinates.
 * The origin (x=0, y=0) is kept at top left.
 * Bottom right coordinates are (x=gridSize-1, y=gridSize-1).
 */
public class Grid implements Serializable {
    private static final Logger logger = Logger.getLogger(Grid.class.getName());
    /**
     * Size of the grid.
     * For example if gridSize=8, the plane will be 8x8.
     */
    private int gridSize;

    /**
     * 2-D array of all the individual cells inside the grid.
     */
    public List<List<CellInfo>> coordinatesList;

    public Grid() {
    }

    /**
     * @param gridSize size of the grid. Should be greater than 0.
     */
    public Grid(int gridSize) {
        this.coordinatesList = new ArrayList<>(gridSize);
        this.gridSize = gridSize;
        for (int i = 0; i < gridSize; i++) {
            List<CellInfo> subList = new ArrayList<>(gridSize);

            for (int j = 0; j < gridSize; j++) {
                subList.add(j, new CellInfo(CellState.EMPTY, null));

            }

            coordinatesList.add(i, subList);
        }
    }

    /**
     * @return the size of grid.
     */
    public int getGridSize() {
        return gridSize;
    }

    /**
     * Get {@link CellState} of a particular x y coordinate.
     * Can throw {@link ArrayIndexOutOfBoundsException} if any of the coordinate
     * is out of bounds. So make sure to pass correct coordinate.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return {@link CellState} of cell at xy.
     */
    public CellState getCellState(int x, int y) {
        return coordinatesList.get(y).get(x).getState();
    }

    /**
     * Update the {@link CellState} of a particular x y coordinate.
     *
     * @param x     x-coordinate
     * @param y     y-coordinate
     * @param state new state of the cell.
     */
    public void updateCellStatus(int x, int y, CellState state) {
        coordinatesList.get(y).get(x).setState(state);
    }

    /**
     * Link a {@link Ship} to a particular x y coordinate.
     *
     * @param x    x-coordinate
     * @param y    y-coordinate
     * @param ship to be placed on the cell.
     */
    public void setShipOnCell(int x, int y, Ship ship) {
        coordinatesList.get(y).get(x).setShip(ship);
    }

    /**
     * @param x x-coordinate
     * @param y y-coordinate
     * @return {@link CellInfo} at particular x y coordinate.
     */
    public CellInfo getCellInfo(int x, int y) {
        return coordinatesList.get(y).get(x);
    }

    /**
     * @param coordinate coordinates
     * @return {@link CellInfo} at particular x y coordinate.
     */
    public CellInfo getCellInfo(Coordinate coordinate) {
        return coordinatesList.get(coordinate.getY()).get(coordinate.getX());
    }

    @Override
    public String toString() {
        return "Grid{" +
                "gridSize=" + gridSize +
                ", coordinates=" + Arrays.toString(new String[]{"test"}) +
                '}';
    }
}
