package com.soen6441.battleship.models;

import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.ShipDirection;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * The type Ship test.
 */
public class ShipTest {
    private Ship ship;

    /**
     * Sets up each.
     */
    @Before()
    public void setUpEach() {
        ship = new Ship.Builder()
                .setName("New Ship 1")
                .setUniqueId("123")
                .setStartCoordinates(1, 1)
                .setEndCoordinates(6, 1)
                .setDirection(ShipDirection.HORIZONTAL)
                .setLength(5)
                .build();
    }

    /**
     * Hit count updates.
     */
    @Test()
    public void hitCountUpdates() {
        ship.setHits(ship.getHits() + 1);
        assertEquals(1, ship.getHits());
    }

    /**
     * Ships sinks when hits equals length.
     */
    @Test()
    public void shipsSinksWhenHitsEqualsLength() {
        ship.setHits(5);
        assertTrue(ship.isSunk());
    }

    /**
     * Ship does not sink hit not equals length.
     */
    @Test()
    public void shipDoesNotSinkHitNotEqualsLength() {
        ship.setHits(4);
        assertFalse(ship.isSunk());
    }
}
