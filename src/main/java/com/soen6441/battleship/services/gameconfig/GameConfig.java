package com.soen6441.battleship.services.gameconfig;

/**
 * Class type GameConfig configures the properties based on user selection.
 */

public class GameConfig {
    private static GameConfig sInstance;
    private String playerName = "Player";
    private boolean isSalvaVariation = false;
    private int gridSize = 10;
    private boolean isNetworkPlay = false;
    private String roomName = "default";
    private boolean isServer;

    private GameConfig() {
    }

    /**
     * Create GameConfig instance.
     * @return gameConfig instance.
     */
    public static GameConfig getsInstance() {
        if (sInstance == null) {
            sInstance = new GameConfig();
        }
        return sInstance;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isSalvaVariation() {
        return isSalvaVariation;
    }

    public void setSalvaVariation(boolean salvaVariation) {
        isSalvaVariation = salvaVariation;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public boolean isNetworkPlay() {
        return isNetworkPlay;
    }

    public void setNetworkPlay(boolean networkPlay) {
        isNetworkPlay = networkPlay;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    /**
     * Returns enemy name.
     *
     * @return player with active turn
     */
    public String getFBEnemyName() {
        if (this.isServer) {
            return "Player2";
        } else {
            return "Player1";
        }
    }

    /**
     *Returns player name.
     *
     * @return player with active turn
     */
    public String getFBPlayerName() {

        if (this.isServer) {
            return "Player1";
        } else {
            return "Player2";
        }
    }
}
