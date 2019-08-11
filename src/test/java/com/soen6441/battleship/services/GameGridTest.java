package com.soen6441.battleship.services;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.enums.HitResult;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.ShipDirection;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;
import com.soen6441.battleship.exceptions.DirectionCoordinatesMismatchException;
import com.soen6441.battleship.exceptions.InvalidShipPlacementException;
import com.soen6441.battleship.services.gamegrid.IGameGrid;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The type Game grid test.
 */
public class GameGridTest {
    private IGameGrid gameGrid;
    private static Ship wrongShipHorizontal;
    private static Ship wrongShipVertical;
    private static Ship wrongShipStart;
    private static Ship wrongShipEnd;
    private static Ship correctShip;
    private static Ship correctShip2;
    private static Ship overlappingShip;
    private static Ship shipToBelowCurrentShip;
    private static Ship shipToLeftOfCurrentShip;
    private static Ship shipToRightOfCurrentShip;
    private static Ship correctShipVertical;
    private static Ship shipToLeftOfVerticalCorrectShip;
    private static Ship shipToTopOfVerticalCorrectShip;
    private static Ship shipToBelowOfVerticalCorrectShip;

    /**
     * Sets up.
     */
    @Before()
    public void setUp() {
        gameGrid = new GameGrid(8);
        wrongShipHorizontal = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(1, 1)
                .setEndCoordinates(2, 2)
                .build();

        wrongShipVertical = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(1, 1)
                .setEndCoordinates(2, 2)
                .build();

        wrongShipStart = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(-1, -1)
                .setEndCoordinates(1, -1)
                .build();

        wrongShipEnd = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(1, 1)
                .setEndCoordinates(11, 1)
                .build();

        correctShip = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(1, 1)
                .setEndCoordinates(6, 1)
                .setLength(5)
                .build();

        correctShip2 = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(0, 2)
                .setEndCoordinates(0, 5)
                .build();

        overlappingShip = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(3, 0)
                .setEndCoordinates(3, 5)
                .build();

        shipToBelowCurrentShip = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(1, 2)
                .setEndCoordinates(6, 2)
                .setLength(5)
                .build();

        correctShipVertical = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(1, 1)
                .setEndCoordinates(1, 6)
                .setLength(5)
                .build();

        shipToLeftOfVerticalCorrectShip = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(0, 1)
                .setEndCoordinates(0, 6)
                .setLength(5)
                .build();

        shipToTopOfVerticalCorrectShip = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(1, 0)
                .setEndCoordinates(1, 0)
                .setLength(1)
                .build();

        shipToBelowOfVerticalCorrectShip = new Ship.Builder()
                .setDirection(ShipDirection.VERTICAL)
                .setStartCoordinates(1, 7)
                .setEndCoordinates(1, 7)
                .setLength(1)
                .build();

        shipToLeftOfCurrentShip = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(0, 1)
                .setEndCoordinates(0, 1)
                .setLength(1)
                .build();

        shipToRightOfCurrentShip = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(7, 1)
                .setEndCoordinates(7, 1)
                .setLength(1)
                .build();
    }

    /**
     * Throws exception if ship is null.
     *
     * @throws NullPointerException the exception
     */
    @Test(expected = NullPointerException.class)
    public void throwsExceptionIfShipIsNull() throws Exception {
        gameGrid.placeShip(null);
    }

    /**
     * Throws exception on wrong coordinates x.
     *
     * @throws CoordinatesOutOfBoundsException the exception
     */
    @Test(expected = CoordinatesOutOfBoundsException.class)
    public void throwsExceptionOnWrongCoordinatesX() throws Exception {
        gameGrid.placeShip(wrongShipStart);
    }

    /**
     * Throws exception on wrong coordinates y.
     *
     * @throws CoordinatesOutOfBoundsException the exception
     */
    @Test(expected = CoordinatesOutOfBoundsException.class)
    public void throwsExceptionOnWrongCoordinatesY() throws Exception {
        gameGrid.placeShip(wrongShipEnd);
    }

    /**
     * Throws exception on wrong direction horizontal.
     *
     * @throws DirectionCoordinatesMismatchException the exception
     */
    @Test(expected = DirectionCoordinatesMismatchException.class)
    public void throwsExceptionOnWrongDirectionHorizontal() throws Exception {
        gameGrid.placeShip(wrongShipHorizontal);
    }

    /**
     * Throws exception on wrong direction vertical.
     *
     * @throws DirectionCoordinatesMismatchException the exception
     */
    @Test(expected = DirectionCoordinatesMismatchException.class)
    public void throwsExceptionOnWrongDirectionVertical() throws Exception {
        gameGrid.placeShip(wrongShipVertical);
    }

    /**
     * Throws exception if overlapping.
     *
     * @throws InvalidShipPlacementException the exception
     */
    @Test(expected = InvalidShipPlacementException.class)
    public void throwsExceptionIfOverlapping() throws Exception {
        gameGrid.placeShip(correctShip);
        gameGrid.placeShip(overlappingShip);
    }

    /**
     * Places ship correctly.
     *
     * @throws Exception the exception
     */
    @Test()
    public void placesShipCorrectly() throws Exception {
        gameGrid.placeShip(correctShip);
    }

    /**
     * Places 2 nd ship correctly.
     *
     * @throws Exception the exception
     */
    @Test()
    public void places2ndShipCorrectly() throws Exception {
        gameGrid.placeShip(correctShip2);
    }

    /**
     * Hits on a ship.
     *
     * @throws Exception the exception
     */
    @Test()
    public void hitsOnAShip() throws Exception {
        gameGrid.placeShip(correctShip);
        HitResult result = gameGrid.hit(3, 1);
        assertEquals(HitResult.HIT, result);
    }

    /**
     * Hit miss on a wrong coordinate.
     *
     * @throws Exception the exception
     */
    @Test()
    public void hitMissOnAWrongCoordinate() throws Exception {
        gameGrid.placeShip(correctShip);
        HitResult result = gameGrid.hit(3, 2);
        assertEquals(HitResult.MISS, result);
    }

    /**
     * Throws exception on wrong coordinates.
     *
     * @throws CoordinatesOutOfBoundsException the exception
     */
    @Test(expected = CoordinatesOutOfBoundsException.class)
    public void throwsExceptionOnWrongCoordinates() throws Exception {
        gameGrid.placeShip(correctShip);
        gameGrid.hit(-1, 0);
    }

    /**
     * Detects if hit was already made on coordinate.
     *
     * @throws Exception the exception
     */
    @Test()
    public void detectsIfHitWasAlreadyMadeOnCoordinate() throws Exception {
        gameGrid.placeShip(correctShip);
        HitResult result1 = gameGrid.hit(0, 0);
        assertEquals(HitResult.MISS, result1);
        HitResult result2 = gameGrid.hit(0, 0);
        assertEquals(HitResult.ALREADY_HIT, result2);
    }

    /**
     * Grid observable updates on adding ship.
     *
     * @throws Exception the exception
     */
    @Test()
    public void gridObservableUpdatesOnAddingShip() throws Exception {
        Observable<Grid> gridObservable = gameGrid.getGridAsObservable();
        TestObserver<Grid> testObserver = new TestObserver<>();
        gameGrid.placeShip(correctShip);
        gridObservable.subscribe(testObserver);
        testObserver.assertValue(updatedGrid -> updatedGrid.getCellState(1, 1) == CellState.SHIP);
    }

    /**
     * Grid observable updates on hit.
     *
     * @throws Exception the exception
     */
    @Test()
    public void gridObservableUpdatesOnHit() throws Exception {
        Observable<Grid> gridObservable = gameGrid.getGridAsObservable();
        TestObserver<Grid> testObserver = new TestObserver<>();
        gridObservable.subscribe(testObserver);

        gameGrid.placeShip(correctShip);
        gameGrid.hit(1, 1);
        gameGrid.hit(0, 0);

        testObserver.assertValueAt(1, grid -> grid.getCellState(1, 1) == CellState.SHIP_WITH_HIT);
        testObserver.assertValueAt(2, grid -> grid.getCellState(0, 0) == CellState.EMPTY_HIT);
    }

    /**
     * Ship is sunk if all hits are successful.
     *
     * @throws Exception the exception
     */
    @Test()
    public void shipIsSunkIfAllHitsAreSuccessful() throws Exception {
        gameGrid.placeShip(correctShip);
        for (int i = 1; i <= 6; i++) {
            gameGrid.hit(i, 1);
        }
        assertTrue(gameGrid.getShips().get(0).isSunk());
    }

    /**
     * Cell updates to ship destroyed.
     *
     * @throws Exception the exception
     */
    @Test()
    public void cellUpdatesToShipDestroyed() throws Exception {
        gameGrid.placeShip(correctShip);

        // Place hits on the ship
        for (int i = 1; i <= 6; i++) {
            gameGrid.hit(i, 1);
        }

        assertEquals(CellState.DESTROYED_SHIP, gameGrid.getGrid().getCellState(1, 1));
    }

    /**
     * Throws exception if overlapping.
     *
     * @throws Exception the exception
     */

    @Test()
    public void peekHitOnGrid() throws Exception {
        gameGrid.placeShip(correctShip);
        assertEquals(HitResult.MISS, gameGrid.peekHit(new Coordinate(0, 0)));
        assertEquals(HitResult.HIT, gameGrid.peekHit(new Coordinate(1, 1)));
        gameGrid.hit(1, 1);
        assertEquals(HitResult.ALREADY_HIT, gameGrid.peekHit(new Coordinate(1, 1)));
    }

    /**
     * Checks un-sunk ship count when ships are present.
     *
     * @throws Exception the exception
     */

    @Test()
    public void unSunkShipCountIsCorrect() throws Exception {
        gameGrid.placeShip(correctShip);

        assertEquals(1, gameGrid.getUnSunkShips());
    }

    /**
     * Checks un-sunk ship count when no ships are present.
     *
     * @throws Exception the exception
     */

    @Test()
    public void unSunkShipCountIsCorrectWithNoShip() throws Exception {
        gameGrid.placeShip(correctShip);

        correctShip.setHits(5);

        assertEquals(0, gameGrid.getUnSunkShips());
    }

    /**
     * Adjacent Ship placement check - Below the current ship - Horizontal placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyHorizontalDown() throws Exception {
        gameGrid.placeShip(correctShip);
        gameGrid.placeShip(shipToBelowCurrentShip);
    }

    /**
     * Adjacent Ship placement check - Above the current ship - Horizontal placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyHorizontalUp() throws Exception {
        gameGrid.placeShip(shipToBelowCurrentShip);
        gameGrid.placeShip(correctShip);
    }

    /**
     * Adjacent Ship placement check - Left to the current ship - Horizontal placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyHorizontalLeft() throws Exception {
        gameGrid.placeShip(correctShip);
        gameGrid.placeShip(shipToLeftOfCurrentShip);
    }

    /**
     * Adjacent Ship placement check - Right to the current ship - Horizontal placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyHorizontalRight() throws Exception {
        gameGrid.placeShip(correctShip);
        gameGrid.placeShip(shipToRightOfCurrentShip);
    }

    /**
     * Adjacent Ship placement check - Below the current ship - Vertical placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyVerticalBelow() throws Exception {
        gameGrid.placeShip(correctShipVertical);
        gameGrid.placeShip(shipToBelowOfVerticalCorrectShip);
    }


    /**
     * Adjacent Ship placement check - Above the current ship - Vertical placement
     *
     * @throws InvalidShipPlacementException the exception
     */


    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyVerticalTop() throws Exception {
        gameGrid.placeShip(correctShipVertical);
        gameGrid.placeShip(shipToTopOfVerticalCorrectShip);
    }


    /**
     * Adjacent Ship placement check - Left to the current ship - Vertical placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyVerticalLeft() throws Exception {
        gameGrid.placeShip(correctShipVertical);
        gameGrid.placeShip(shipToLeftOfVerticalCorrectShip);
    }

    /**
     * Adjacent Ship placement check - Right to the current ship - Vertical placement
     *
     * @throws InvalidShipPlacementException the exception
     */

    @Test(expected = InvalidShipPlacementException.class)
    public void twoShipsCannotBePlacedAdjacentlyVerticalRight() throws Exception {
        gameGrid.placeShip(shipToLeftOfVerticalCorrectShip);
        gameGrid.placeShip(correctShipVertical);
    }
}
