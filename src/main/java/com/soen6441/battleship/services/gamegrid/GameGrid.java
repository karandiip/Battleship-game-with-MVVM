package com.soen6441.battleship.services.gamegrid;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.enums.HitResult;
import com.soen6441.battleship.enums.ShipDirection;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;
import com.soen6441.battleship.exceptions.DirectionCoordinatesMismatchException;
import com.soen6441.battleship.exceptions.InvalidShipPlacementException;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.utils.GridUtils;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wraps the {@link Grid} class and adds logic to it.
 * <p>
 * Provides functionality to:
 * <ul>
 * <li>Hit the ship on a particular coordinate: {@link GameGrid#hit(int, int)}</li>
 * <li>Get the list of all ships on grid: {@link GameGrid#getShips()}</li>
 * <li>Get the grid as an observable, which is triggered whenever an update on grid is made: {@link GameGrid#getGridAsObservable()}</li>
 * <li>Place a ship on the grid: {@link GameGrid#placeShip(Ship)}</li>
 * <li>Check if every ship on grid is destroyed: {@link GameGrid#areAllShipsDestroyed()}</li>
 * </ul>
 */
public class GameGrid implements IGameGrid, Serializable {
    private final Logger logger = Logger.getLogger(GameGrid.class.getName());

    /**
     * Grid Model
     */
    private Grid grid;

    /**
     * List of all the ships successfully added to the grid.
     */
    private List<Ship> ships = new ArrayList<>();

    private final BehaviorSubject<Grid> gridBehaviorSubject = BehaviorSubject.create();

    /**
     * Constructor to create a new grid
     * @param gridSize - size of grid
     */
    public GameGrid(int gridSize) {
        this.grid = new Grid(gridSize);
        gridBehaviorSubject.onNext(this.grid);
        logger.info(() -> String.format("Grid created successfully: %s", grid));
    }

    /**
     * Constructor to load a saved gird object.
     * @param grid - Saved grid object
     */

    public GameGrid(Grid grid) {
        this.grid = grid;
        gridBehaviorSubject.onNext(this.grid);
        logger.info(() -> String.format("Grid loaded from file successfully: %s", grid));
    }

    /**
     * @return Grid model
     */
    @Override
    public Grid getGrid() {
        return grid;
    }

    /**
     * @return List of all ships currently on the grid.
     */
    @Override
    public List<Ship> getShips() {
        return ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    /**
     * Try to hit a particular cell on the grid, with the provided coordinates.
     *
     * @param x coordinate
     * @param y coordinate
     * @return HitResult of the attempted hit.
     * @throws CoordinatesOutOfBoundsException if coordinates are out of grid bounds.
     */
    @Override
    public HitResult hit(int x, int y) throws CoordinatesOutOfBoundsException {
        // Check if the coordinates are correct
        if (!isValidCell(x, y)) {
            throw new CoordinatesOutOfBoundsException();
        }

        CellState state = grid.getCellState(x, y);

        HitResult result;

        // If cell has already been hit!
        if (state == CellState.EMPTY_HIT || state == CellState.SHIP_WITH_HIT || state == CellState.DESTROYED_SHIP) {
            result = HitResult.ALREADY_HIT;
        } else if (state == CellState.SHIP
                || (state == CellState.TO_BE_PLACED && grid.getCellInfo(x, y).getShip() != null)) {  // If there is no hit, but there is ship.

            grid.updateCellStatus(x, y, CellState.SHIP_WITH_HIT);

            Ship shipToHit = grid.getCellInfo(x, y).getShip();
            shipToHit.setHits(shipToHit.getHits() + 1);

            if (shipToHit.isSunk()) {

                if (shipToHit.getDirection() == ShipDirection.HORIZONTAL) {
                    for (int shipX = shipToHit.getStartX(); shipX <= shipToHit.getEndX(); shipX++) {
                        grid.updateCellStatus(shipX, shipToHit.getStartY(), CellState.DESTROYED_SHIP);
                    }
                } else {
                    for (int shipY = shipToHit.getStartY(); shipY <= shipToHit.getEndY(); shipY++) {
                        grid.updateCellStatus(shipToHit.getStartX(), shipY, CellState.DESTROYED_SHIP);
                    }
                }
            }

            result = HitResult.HIT;
        } else {  // If the cell is empty i.e. not ship or hit on it.
            grid.updateCellStatus(x, y, CellState.EMPTY_HIT);
            result = HitResult.MISS;
        }

        gridBehaviorSubject.onNext(this.grid);
        return result;
    }

    /**
     * Peek to check a cell state
     *
     * @param coordinate Coordinate to hit.
     * @return
     * @throws CoordinatesOutOfBoundsException if coordinates are out of grid bounds.
     */

    @Override
    public HitResult peekHit(Coordinate coordinate) throws CoordinatesOutOfBoundsException {
        // Check if the coordinates are correct
        if (!isValidCell(coordinate.getX(), coordinate.getX())) {
            throw new CoordinatesOutOfBoundsException();
        }

        CellState state = grid.getCellState(coordinate.getX(), coordinate.getY());

        HitResult result;

        // If cell has already been hit!
        if (state == CellState.EMPTY_HIT || state == CellState.SHIP_WITH_HIT || state == CellState.DESTROYED_SHIP) {
            result = HitResult.ALREADY_HIT;
        } else if (state == CellState.SHIP) {  // If there is no hit, but there is ship.
            result = HitResult.HIT;
        } else {  // If the cell is empty i.e. not ship or hit on it.
            result = HitResult.MISS;
        }

        gridBehaviorSubject.onNext(this.grid);
        return result;
    }

    @Override
    public Observable<Grid> getGridAsObservable() {
        return gridBehaviorSubject;
    }

    /**
     * Places a ship on the board with the provided Ship object.
     *
     * @param ship The ship to be placed
     * @throws DirectionCoordinatesMismatchException If the starting and ending coordinates in ship
     *                                               don't match with the provided direction.
     * @throws CoordinatesOutOfBoundsException       If any coordinate of the ship is not on the plane.
     * @throws InvalidShipPlacementException         If there is another ship already on one of the coordinates that
     *                                               the ship is being placed on.
     */
    @Override
    public void placeShip(Ship ship) throws Exception {
        checkNotNull(ship);
        logger.info(String.format("Placing ship on grid: %s", ship));

        // Check if direction of ship matches the coordinates
        if ((ship.getDirection() == ShipDirection.HORIZONTAL && ship.getStartY() != ship.getEndY())
                || (ship.getDirection() == ShipDirection.VERTICAL && ship.getStartX() != ship.getEndX())) {
            throw new DirectionCoordinatesMismatchException();
        }

        // Check if all coordinates passed lie on plane.
        if (ship.getStartX() < 0
                || ship.getStartY() < 0
                || ship.getEndX() >= grid.getGridSize()
                || ship.getEndY() >= grid.getGridSize()) {
            logger.severe("Ship Coordinates are out of bounds!");
            throw new CoordinatesOutOfBoundsException();
        }

        // Check if there are no other ships surrounding the placement of current ship
        checkPointValidityForShip(ship);

        // Add to ship list
        ships.add(ship);

        if (ship.getDirection() == ShipDirection.HORIZONTAL) {
            for (int i = ship.getStartX(); i <= ship.getEndX(); i++) {
                grid.updateCellStatus(i, ship.getStartY(), CellState.SHIP);
                grid.setShipOnCell(i, ship.getStartY(), ship);
            }
        } else if (ship.getDirection() == ShipDirection.VERTICAL) {
            for (int i = ship.getStartY(); i <= ship.getEndY(); i++) {
                grid.updateCellStatus(ship.getStartX(), i, CellState.SHIP);
                grid.setShipOnCell(ship.getStartX(), i, ship);
            }
        }

        gridBehaviorSubject.onNext(this.grid);

        logger.info(String.format("Successfully placed ship on grid %s", ship));

        GridUtils.printGrid(this.grid);
    }

    /**
     * Check to verify that the ship can be placed on selected coordinates.
     *
     * @param ship The ship to be placed.
     * @return
     */
    @Override
    public boolean canPlaceShip(Ship ship) {
        try {
            checkNotNull(ship);
            logger.info(String.format("Testing if ship can be palced on grid: %s", ship));

            // Check if direction of ship matches the coordinates
            if ((ship.getDirection() == ShipDirection.HORIZONTAL && ship.getStartY() != ship.getEndY())
                    || (ship.getDirection() == ShipDirection.VERTICAL && ship.getStartX() != ship.getEndX())) {
                throw new DirectionCoordinatesMismatchException();
            }

            // Check if all coordinates passed lie on plane.
            if (ship.getStartX() < 0
                    || ship.getStartY() < 0
                    || ship.getEndX() >= grid.getGridSize()
                    || ship.getEndY() >= grid.getGridSize()) {
                logger.severe("Ship Coordinates are out of bounds!");
                throw new CoordinatesOutOfBoundsException();
            }

            // Check if there are no other ships surrounding the placement of current ship
            checkPointValidityForShip(ship);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Check if all the player ships have been destroyed.
     *
     * @return true if all ships have been destroyed.
     */
    @Override
    public boolean areAllShipsDestroyed() {
        // If no ships then all are are destroyed
        if (ships.isEmpty()) {
            return true;
        }

        boolean areShipsDestroyed = true;

        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                areShipsDestroyed = false;
                break;
            }
        }

        return areShipsDestroyed;
    }

    /**
     * Get the number of ships that are still not sunk.
     *
     * @return The number of remaining ships that are not destroyed.
     */
    @Override
    public int getUnSunkShips() {
        int count = 0;

        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void updateCellState(Coordinate coordinate, CellState state) {
        this.getGrid().updateCellStatus(coordinate.getX(), coordinate.getY(), state);
        this.gridBehaviorSubject.onNext(this.grid);
    }

    /**
     * Checks if the ship can be placed on provided coordinates.
     * - No ships on already existing coordinates.
     * - No ships in surrounding coordinates.
     *
     * @param ship to be added on the grid
     * @throws InvalidShipPlacementException if ship cannot be placed because of invalid surroundings.
     */
    private void checkPointValidityForShip(Ship ship) throws InvalidShipPlacementException {
        logger.info("Checking Ship validity...");

        int gridSize = GameConfig.getsInstance().getGridSize();

        if (ship.getDirection() == ShipDirection.HORIZONTAL) {
            // Check if no ship is on the left or right
            if (ship.getStartX() != 0) {
                checkValidAndThrow(ship.getStartX() - 1, ship.getStartY());
            }

            if (ship.getStartX() != (gridSize - 1)) {
                checkValidAndThrow(ship.getEndX() + 1, ship.getStartY());
            }

            // Normalise starting and ending coordinates to check if ship is no being placed diagonally
            // to any other ship.
            int startX = ship.getStartX();
            int endX = ship.getEndX();

            if (startX > 0) {
                startX -= 1;
            }

            if (endX < (gridSize - 1)) {
                endX += 1;
            }

            for (int x = startX; x <= endX; x++) {
                // Check if no ship is placed above
                if (ship.getStartY() != 0) {
                    checkValidAndThrow(x, ship.getStartY() - 1);
                }

                // Check if no ships is placed below
                if (ship.getStartY() != gridSize - 1) {
                    checkValidAndThrow(x, ship.getStartY() + 1);
                }

                checkValidAndThrow(x, ship.getStartY());
            }
        } else if (ship.getDirection() == ShipDirection.VERTICAL) {
            // Check if no ships are on top for below the ship
            if (ship.getStartY() >= 0) {
                checkValidAndThrow(ship.getStartX(), ship.getStartY() - 1);
            }

            if (ship.getEndY() <= (gridSize - 1)) {
                checkValidAndThrow(ship.getStartX(), ship.getEndY() + 1);
            }

            // Normalise starting and ending coordinates to check if ship is no being placed diagonally
            // to any other ship.
            int startY = ship.getStartY();
            int endY = ship.getEndY();

            if (startY > 0) {
                startY -= 1;
            }

            if (endY < (gridSize - 1)) {
                endY += 1;
            }

            for (int y = startY; y <= endY; y++) {

                // Check if no ship is placed to the left
                if (ship.getStartX() != 0) {
                    checkValidAndThrow(ship.getStartX() - 1, y);
                }

                // Check if no ship is placed to the right
                if (ship.getStartX() != (gridSize - 1)) {
                    checkValidAndThrow(ship.getStartX() + 1, y);
                }

                checkValidAndThrow(ship.getStartX(), y);
            }
        }
        logger.info("Ship validity check complete!");
    }

    /**
     *
     * @param x - x coordinate for ship
     * @param y - y coordinate for ship
     * @throws InvalidShipPlacementException - if ship cannot be place at the cell
     */

    private void checkValidAndThrow(int x, int y) throws InvalidShipPlacementException {
        if (isValidCell(x, y) && (grid.getCellState(x, y) == CellState.SHIP)) {
            throw new InvalidShipPlacementException();
        }
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0
                && x < grid.getGridSize()
                && y >= 0
                && y < grid.getGridSize();
    }

    @Override
    public String toString() {
        return "GameGrid{" +
                "grid=" + grid +
                ", ships=" + ships +
                '}';
    }

    @Override
    public void updateGrid(Grid newGrid) {
        int gridSize = GameConfig.getsInstance().getGridSize();

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (newGrid.getCellInfo(new Coordinate(i, j)) != null
                        && newGrid.getCellInfo(new Coordinate(i, j)).getShip() != null) {
                    int shipLength = newGrid.getCellInfo(new Coordinate(i, j)).getShip().getLength();
                    for (Ship ship : ships) {
                        if (shipLength == ship.getLength()) {
                            logger.info("breaking ship at " + i + " " + j);
                            newGrid.getCellInfo(new Coordinate(i, j)).setShip(ship);
                            break;
                        }
                    }
                }
            }
        }

        this.grid = newGrid;
        gridBehaviorSubject.onNext(this.grid);
    }
}
