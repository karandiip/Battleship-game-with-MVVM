package com.soen6441.battleship.services.gamecontroller.gamestrategy;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.enums.HitResult;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The type SalvaTurnStrategy defines the behavior when the game runs in Salva mode.
 */

public class SalvaTurnStrategy implements ITurnStrategy {
    private static final Logger logger = Logger.getLogger(SimpleTurnStrategy.class.getName());
    private GamePlayer player;
    private GamePlayer enemy;
    private GamePlayer currentPlayer;
    private int playerTurns = 5;
    private int enemyTurns = 5;
    private List<Coordinate> playerCoordinateHits = new ArrayList<>();

    public SalvaTurnStrategy(GamePlayer player, GamePlayer enemy) {
        this.player = player;
        this.enemy = enemy;
        currentPlayer = player;
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

        if (currentPlayer == player) {
            playerTurns--;
        } else {
            if (result != HitResult.ALREADY_HIT) {
                enemyTurns--;
            }
        }

        logger.info(playerTurns + "<-------- Player Turns");

        if (playerTurns == 0) {
            this.currentPlayer = enemy;
            playerTurns = player.getGameGrid().getUnSunkShips();
            enemyTurns = enemy.getGameGrid().getUnSunkShips();
        }

        if (enemyTurns == 0) {
            this.currentPlayer = player;
            enemyTurns = enemy.getGameGrid().getUnSunkShips();
            playerTurns = player.getGameGrid().getUnSunkShips();
        }

        return currentPlayer;
    }


    /**
     * Get he hit result on a cell.
     * @param playerToHit - player who's grid is being hit
     * @param coordinate - cell coordinates on which hits are being made
     * @throws CoordinatesOutOfBoundsException
     */
    @Override
    public HitResult hit(GamePlayer playerToHit, Coordinate coordinate) throws CoordinatesOutOfBoundsException {
        // If manual player is playing
        if (enemy == playerToHit) {
            playerCoordinateHits.add(coordinate);

            HitResult peekHit = playerToHit.getGameGrid().peekHit(coordinate);

            if (peekHit != HitResult.ALREADY_HIT) {
                playerToHit.getGameGrid().updateCellState(coordinate, CellState.TO_BE_PLACED);
            }

            // Last turn for player
            if (playerTurns == 1) {
                for (Coordinate prevCoordinates : this.playerCoordinateHits) {
                    playerToHit.getGameGrid().hit(prevCoordinates.getX(), prevCoordinates.getY());
                }
                this.playerCoordinateHits.clear();
            }

            return HitResult.MISS;
        } else {
            return playerToHit.getGameGrid().hit(coordinate.getX(), coordinate.getY());
        }
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

    public int getPlayerTurns() {
        return playerTurns;
    }

    public void setPlayerTurns(int playerTurns) {
        this.playerTurns = playerTurns;
    }

    public List<Coordinate> getPlayerCoordinateHits() {
        return playerCoordinateHits;
    }

    public void setPlayerCoordinateHits(List<Coordinate> playerCoordinateHits) {
        if (playerCoordinateHits != null) {
            this.playerCoordinateHits = playerCoordinateHits;
        }
    }
}
