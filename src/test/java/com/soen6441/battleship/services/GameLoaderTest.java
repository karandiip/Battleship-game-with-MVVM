package com.soen6441.battleship.services;

import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.GameControllerInfo;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.enums.ShipDirection;
import com.soen6441.battleship.services.gameloader.GameLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class GameLoaderTest {
    private static final String TEST_FILE = "test_file";
    private GameLoader gameLoader;
    private Grid playerGrid;
    private Ship ship;

    @Before()
    public void setUp() {
        gameLoader = new GameLoader("output_test");
        playerGrid = new Grid(8);
        ship = new Ship.Builder()
                .setDirection(ShipDirection.HORIZONTAL)
                .setStartCoordinates(0, 0)
                .setEndCoordinates(3, 0)
                .setLength(4)
                .build();
    }

    @Test
    public void createsAFileSuccessfully() {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setStoreDate(new Date().getTime());
        gameInfo.setPlayerGrid(playerGrid);

        gameLoader.saveGame(TEST_FILE, gameInfo);
        File savedFiled = new File("output_test/output/test_file");
        assertTrue(savedFiled.exists());
    }

    @Test
    public void savedFileIsParsedCorrectly() throws Exception {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setStoreDate(123);
        gameInfo.setPlayerGrid(playerGrid);
        gameLoader.saveGame(TEST_FILE, gameInfo);
        File savedFiled = new File("output_test/output/test_file");
        FileInputStream fileInputStream = new FileInputStream(savedFiled);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        GameControllerInfo gameInfoFromFile = (GameControllerInfo) objectInputStream.readObject();
        assertNotNull(gameInfoFromFile);
    }

    @Test
    public void correctDateIsStoredAndRetrieved() throws Exception {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setStoreDate(123);
        gameInfo.setPlayerGrid(playerGrid);
        gameLoader.saveGame(TEST_FILE, gameInfo);
        GameControllerInfo offlineGameInfo = gameLoader.readSavedGame(TEST_FILE);
        assertEquals(123, offlineGameInfo.getStoreDate());
    }

    @Test
    public void correctCellStateIsStored() throws Exception {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setStoreDate(123);
        playerGrid.updateCellStatus(0, 0, CellState.DESTROYED_SHIP);
        gameInfo.setPlayerGrid(playerGrid);
        gameLoader.saveGame(TEST_FILE, gameInfo);
        GameControllerInfo offlineGameInfo = gameLoader.readSavedGame(TEST_FILE);
        assertEquals(CellState.DESTROYED_SHIP, offlineGameInfo.getPlayerGrid().getCellState(0, 0));
    }

    @Test
    public void correctShipIsStored() throws Exception {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setStoreDate(123);
        for (int x = 0; x <= 3; x++) {
            playerGrid.setShipOnCell(x, 0, ship);
            playerGrid.updateCellStatus(x, 0, CellState.SHIP);
        }
        gameInfo.setPlayerGrid(playerGrid);
        gameLoader.saveGame(TEST_FILE, gameInfo);

        GameControllerInfo offlineGameInfo = gameLoader.readSavedGame(TEST_FILE);
        assertEquals(CellState.SHIP, offlineGameInfo.getPlayerGrid().getCellState(0, 0));
        assertEquals(4, offlineGameInfo.getPlayerGrid().getCellInfo(0, 0).getShip().getLength());
        assertEquals(ShipDirection.HORIZONTAL, offlineGameInfo.getPlayerGrid().getCellInfo(0, 0).getShip().getDirection());
    }

    @Test
    public void shipHitAreStored() throws Exception {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setStoreDate(123);
        for (int x = 0; x <= 3; x++) {
            playerGrid.setShipOnCell(x, 0, ship);
            playerGrid.updateCellStatus(x, 0, CellState.SHIP);
        }
        ship.setHits(1);
        gameInfo.setPlayerGrid(playerGrid);
        gameLoader.saveGame(TEST_FILE, gameInfo);

        GameControllerInfo offlineGameInfo = gameLoader.readSavedGame(TEST_FILE);
        assertEquals(1, offlineGameInfo.getPlayerGrid().getCellInfo(0, 0).getShip().getHits());
    }

    @Test
    public void turnsAreStored() throws Exception {
        GameControllerInfo gameInfo = new GameControllerInfo();
        gameInfo.setPlayerTurns(Arrays.asList(1000L, 1200L));
        gameLoader.saveGame(TEST_FILE, gameInfo);

        GameControllerInfo offlineGameInfo = gameLoader.readSavedGame(TEST_FILE);
        assertEquals(2, offlineGameInfo.getPlayerTurns().size());
        assertTrue(offlineGameInfo.getPlayerTurns().contains(1000L));
        assertTrue(offlineGameInfo.getPlayerTurns().contains(1200L));
    }
}
