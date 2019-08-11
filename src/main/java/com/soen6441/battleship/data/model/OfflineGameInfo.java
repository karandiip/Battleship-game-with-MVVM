package com.soen6441.battleship.data.model;

import java.io.Serializable;

/**
 * Will contain information when the game-play is offline.
 *
 */

public class OfflineGameInfo implements Serializable {

    /**
     * Set the current route.
     */
    private String currentRouteName;

    /**
     * Status of game.
     */
    private boolean isGameOver;

    public String getCurrentRouteName() {
        return currentRouteName;
    }

    public void setCurrentRouteName(String currentRouteName) {
        this.currentRouteName = currentRouteName;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }
}
