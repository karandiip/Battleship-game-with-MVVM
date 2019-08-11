package com.soen6441.battleship.services.boardgenerator;

import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.ShipDirection;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.services.gamegrid.IGameGrid;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Provides utility to place random ships on a {@link IGameGrid}.
 */
public class RandomShipPlacer {
    private static final Logger logger = Logger.getLogger(RandomShipPlacer.class.getName());
    private static final int gridSize = GameConfig.getsInstance().getGridSize();

    /**
     * Places randomly 5 ships of different lengths on a {@link IGameGrid}.
     * The placement of ships in completed random. Java's {@link Random}
     * class is used to select random coordinates.
     *
     * @param gameGrid on top of which random ships need to be placed.
     */
    public void placeRandomShips(IGameGrid gameGrid) {
        int shipLength = 5;

        Random random = new Random();

        while (shipLength > 0) {
            int startXCord = random.nextInt(gridSize);
            int startYCord = random.nextInt(gridSize);
            boolean isVertical = random.nextBoolean();
            ShipDirection shipDirection = isVertical ? ShipDirection.VERTICAL : ShipDirection.HORIZONTAL;

            int endXCord = startXCord;
            int endYCord = startYCord;

            if (shipDirection == ShipDirection.VERTICAL) {
                endYCord += shipLength - 1;
            } else {
                endXCord += shipLength - 1;
            }

            Ship ship = new Ship.Builder()
                    .setName("Ship:" + shipLength)
                    .setStartCoordinates(startXCord, startYCord)
                    .setEndCoordinates(endXCord, endYCord)
                    .setLength(shipLength)
                    .setDirection(shipDirection)
                    .build();

            try {
                gameGrid.placeShip(ship);
                shipLength--;
                logger.info(String.format("Ship added of length %d.", shipLength));
            } catch (Exception e) {
                logger.warning(() -> "Wrong coordinates. Trying again");
            }
        }

        logger.info(String.format("Total ships added %d.", gameGrid.getShips().size()));

        logger.info(() -> "Enemy ship placement successfully!");
    }
}
