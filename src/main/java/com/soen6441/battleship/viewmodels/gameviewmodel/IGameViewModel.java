package com.soen6441.battleship.viewmodels.gameviewmodel;

import com.soen6441.battleship.data.model.GameOverInfo;
import com.soen6441.battleship.data.model.Grid;
import io.reactivex.Observable;

/**
 * The interface Game view model: is the interface for Gameview Model to take data from data.
 */
public interface IGameViewModel {
    /**
     * Get the player grid as an observable.
     *
     * @return the player grid
     */
    Observable<Grid> getPlayerGrid();

    /**
     * Get the enemy grid as an overvable.
     *
     * @return the enemy grid
     */
    Observable<Grid> getEnemyGrid();

    /**
     * Send a hit to the board of enemy.
     *
     * @param x the x
     * @param y the y
     */
    void sendHit(int x, int y);

    /**
     * Player turn change observable.
     *
     * @return the observable
     */
    Observable<String> playerTurnChange();

    /**
     * Get observable that fires when the game is over.
     *
     * @return the observable
     */
    Observable<GameOverInfo> isGameOver();

    Observable<Long> turnTimer();

    Observable<Long> gameTimer();

    void startGame();

    Long getFinalScore();
}
