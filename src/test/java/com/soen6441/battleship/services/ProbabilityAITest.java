package com.soen6441.battleship.services;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.ShipDirection;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;
import com.soen6441.battleship.services.aiplayer.ProbabilityAIPlayer;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProbabilityAITest {
    private GamePlayer gamePlayer;
    private ProbabilityAIPlayer aiPlayer;
    private static int gridSize;

    @BeforeClass()
    public static void setUpAll() {
        gridSize = GameConfig.getsInstance().getGridSize();
    }

    @Before()
    public void setUp() {
        gamePlayer = new GamePlayer("Player", new GameGrid(gridSize));
    }

    @Test
    public void hitsCenterCoordinateIfShipPlacedAtTop() {
        placeShipAtTop();

        BehaviorSubject<Coordinate> coordinateBehaviourSubject = BehaviorSubject.create();
        TestObserver<Coordinate> testObserver = new TestObserver<>();
        coordinateBehaviourSubject.subscribe(testObserver);

        aiPlayer = new ProbabilityAIPlayer("AI Player", new GameGrid(gridSize), gamePlayer, coordinateBehaviourSubject::onNext);

        BehaviorSubject<Boolean> isMyTurn = BehaviorSubject.create();
        aiPlayer.setIsMyTurn(isMyTurn);
        isMyTurn.onNext(true);

        testObserver.assertValue(coordinate -> coordinate.equals(new Coordinate(4, 4)));
    }

    @Test
    public void hitsCellWithHighestDistributionAfterFirstHit() {
        placeShipAtTop();

        BehaviorSubject<Coordinate> coordinateBehaviourSubject = BehaviorSubject.create();
        TestObserver<Coordinate> testObserver = new TestObserver<>();
        coordinateBehaviourSubject.subscribe(testObserver);

        // Hit one coordinate
        try {
            gamePlayer.getGameGrid().hit(3, 3);
        } catch (CoordinatesOutOfBoundsException e) {
            e.printStackTrace();
        }

        aiPlayer = new ProbabilityAIPlayer("AI Player", new GameGrid(gridSize), gamePlayer, coordinateBehaviourSubject::onNext);

        BehaviorSubject<Boolean> isMyTurn = BehaviorSubject.create();
        aiPlayer.setIsMyTurn(isMyTurn);
        isMyTurn.onNext(true);

        testObserver.assertValue(coordinate -> coordinate.equals(new Coordinate(4, 4)));
    }

    private void placeShip() {
        try {
            gamePlayer.getGameGrid().placeShip(new Ship.Builder()
                    .setDirection(ShipDirection.HORIZONTAL)
                    .setStartCoordinates(1, 1)
                    .setEndCoordinates(6, 1)
                    .setLength(5)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void placeShipAtTop() {
        try {
            gamePlayer.getGameGrid().placeShip(new Ship.Builder()
                    .setDirection(ShipDirection.HORIZONTAL)
                    .setStartCoordinates(7, 0)
                    .setEndCoordinates(7, 0)
                    .setLength(1)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
