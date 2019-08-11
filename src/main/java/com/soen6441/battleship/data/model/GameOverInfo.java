package com.soen6441.battleship.data.model;

/**
 * Contains information of a GameOver event.
 * Plain Data Class.
 */
public class GameOverInfo {

    /**
     * Did the player playing the game won or was the enemy victorious.
     */
    private final boolean didPlayerWin;

    /**
     * Is the game over i.e. someone has won.
     */
    private final boolean isGameOver;


    /**
     * @param isGameOver   Is the game finished.
     * @param didPlayerWin Was the manual player playing the game victorious.
     */
    public GameOverInfo(boolean isGameOver, boolean didPlayerWin) {
        this.isGameOver = isGameOver;
        this.didPlayerWin = didPlayerWin;
    }

    /**
     * @return Did player win the game.
     */
    public boolean didPlayerWin() {
        return didPlayerWin;
    }

    /**
     * @return Is the game over.
     */
    public boolean isGameOver() {
        return isGameOver;
    }

    @Override
    public String toString() {
        return "GameOverInfo{" +
                "isGameOver=" + isGameOver +
                ", didPlayerWin=" + didPlayerWin +
                '}';
    }
}
