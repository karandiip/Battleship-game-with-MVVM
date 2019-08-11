package com.soen6441.battleship.utils;

import com.soen6441.battleship.services.boardgenerator.RandomShipPlacer;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *  The type Random ShipPlacer Test.
 */

public class RandomShipPlacerTest {
    private GameGrid gameGrid;


    /**
     *  Initial setup.
     */
    @Before
    public void setUp() {
        gameGrid = new GameGrid(8);
    }



    /**
     *  Place random ship on the grid.
     */
    @Test
    public void shipsArePlaceOnBoard() {
        RandomShipPlacer randomShipPlacer = new RandomShipPlacer();
        randomShipPlacer.placeRandomShips(gameGrid);
        assertEquals(5, gameGrid.getShips().size());
    }
}
