package com.soen6441.battleship.services.gamecontroller.gamestrategy;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.enums.HitResult;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class of type SimpleTurnStrategy implements coordinates turns between players.
 */
public class SimpleTurnStrategy implements ITurnStrategy {
    private static final Logger logger = Logger.getLogger(SimpleTurnStrategy.class.getName());
    private GamePlayer player;
    private GamePlayer enemy;
    private GamePlayer currentPlayer;


    public SimpleTurnStrategy() {
    }


    /**
     * Get turn of next player, depending on hit made.
     * @param player - current player object
     * @param enemy - other player object
     * @param result Result of previous hit
     * @return Player to which the turn should be switched.
     */
    @Override
    public GamePlayer getNextTurn(GamePlayer player, GamePlayer enemy, HitResult result) {
        if (this.player == null || this.enemy == null) {
            this.player = player;
            this.enemy = enemy;
            currentPlayer = player;
        }

        if (result != HitResult.HIT) {
            switchCurrentPlayer();
        }

        return currentPlayer;
    }

    /**
     * Get he hit result on a cell.
     * @param player - player type
     * @param coordinate - cell coordinates on which hit has been made
     * @throws CoordinatesOutOfBoundsException
     */

    @Override
    public HitResult hit(GamePlayer player, Coordinate coordinate) throws CoordinatesOutOfBoundsException {
        return player.getGameGrid().hit(coordinate.getX(), coordinate.getY());
    }

    /**
     * Method to switch turns between players.
     */
    private void switchCurrentPlayer() {
        logger.info(() -> " " + (currentPlayer == player));
        if (currentPlayer == player) {
            logger.info(() -> "Switching turn to enemy.");
            currentPlayer = enemy;
        } else {
            logger.info(() -> "Switching turn to player.");
            currentPlayer = player;
        }
    }
}
