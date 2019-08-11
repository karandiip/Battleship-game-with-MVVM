package com.soen6441.battleship.data.model;

import com.soen6441.battleship.data.interfaces.HitCallback;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Game player is the class to instantiate the player type from which both human player
 * and AI player will take properties.
 */
public class GamePlayer {
    protected final String name;
    protected final GameGrid gameGrid;
    protected Observable<Boolean> isMyTurn;
    protected List<Long> turnTimes = new ArrayList<>();

    /**
     * Instantiates a new Game player.
     *
     * @param name     the name
     * @param gameGrid the game grid
     */
    public GamePlayer(String name, GameGrid gameGrid) {
        this.gameGrid = gameGrid;
        this.name = name;
    }

    public GameGrid getGameGrid() {
        return this.gameGrid;
    }

    public String getName() {
        return name;
    }

    public void setIsMyTurn(Observable<Boolean> isMyTurn) {
        this.isMyTurn = isMyTurn;
    }

    public Observable<Boolean> getIsMyTurn() {
        return isMyTurn;
    }

    public List<Long> getTurnTimes() {
        return turnTimes;
    }

    public void setTurnTimes(List<Long> turnTimes) {
        this.turnTimes = turnTimes;
    }

    public void addTimeTaken(long timeToAdd) {
        this.turnTimes.add(timeToAdd);
    }
}
