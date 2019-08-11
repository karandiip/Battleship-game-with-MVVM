package com.soen6441.battleship.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * The type Game player is the class to instantiate the GameController which sets and gets the status of the game in play.
 */

public class GameControllerInfo implements Serializable {
    private long storeDate;
    private Grid playerGrid;
    private Grid enemyGrid;
    private List<Long> playerTurns;
    private String currentTurn;
    private boolean isSalva;
    private int gridSize;
    private List<Ship> playerShips;
    private List<Ship> enemyShips;
    private int unSunkPlayerShips;
    private int unSunkEnemyShips;
    private int playerSalvaTurns;
    private List<Coordinate> playerSalvaCoordinates;
    private long gameTime;
    private long turnTime;

    public long getStoreDate() {
        return storeDate;
    }

    public void setStoreDate(long storeDate) {
        this.storeDate = storeDate;
    }

    public Grid getPlayerGrid() {
        return playerGrid;
    }

    public void setPlayerGrid(Grid playerGrid) {
        this.playerGrid = playerGrid;
    }

    public Grid getEnemyGrid() {
        return enemyGrid;
    }

    public void setEnemyGrid(Grid enemyGrid) {
        this.enemyGrid = enemyGrid;
    }

    public List<Long> getPlayerTurns() {
        return playerTurns;
    }

    public void setPlayerTurns(List<Long> playerTurns) {
        this.playerTurns = playerTurns;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isSalva() {
        return isSalva;
    }

    public void setSalva(boolean salva) {
        isSalva = salva;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public List<Ship> getPlayerShips() {
        return playerShips;
    }

    public void setPlayerShips(List<Ship> playerShips) {
        this.playerShips = playerShips;
    }

    public List<Ship> getEnemyShips() {
        return enemyShips;
    }

    public void setEnemyShips(List<Ship> enemyShips) {
        this.enemyShips = enemyShips;
    }

    public int getUnSunkPlayerShips() {
        return unSunkPlayerShips;
    }

    public void setUnSunkPlayerShips(int unSunkPlayerShips) {
        this.unSunkPlayerShips = unSunkPlayerShips;
    }

    public int getUnSunkEnemyShips() {
        return unSunkEnemyShips;
    }

    public void setUnSunkEnemyShips(int unSunkEnemyShips) {
        this.unSunkEnemyShips = unSunkEnemyShips;
    }

    public int getPlayerSalvaTurns() {
        return playerSalvaTurns;
    }

    public void setPlayerSalvaTurns(int playerSalvaTurns) {
        this.playerSalvaTurns = playerSalvaTurns;
    }

    public List<Coordinate> getPlayerSalvaCoordinates() {
        return playerSalvaCoordinates;
    }

    public void setPlayerSalvaCoordinates(List<Coordinate> playerSalvaCoordinates) {
        this.playerSalvaCoordinates = playerSalvaCoordinates;
    }

    public long getGameTime() {
        return gameTime;
    }

    public void setGameTime(long gameTime) {
        this.gameTime = gameTime;
    }

    public long getTurnTime() {
        return turnTime;
    }

    public void setTurnTime(long turnTime) {
        this.turnTime = turnTime;
    }
}
