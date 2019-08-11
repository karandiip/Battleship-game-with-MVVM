package com.soen6441.battleship.services;

import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.services.gamegrid.GameGrid;

public class NetworkPlayer extends GamePlayer {
    /**
     * Instantiates a new Game player.
     *
     * @param name     the name
     * @param gameGrid the game grid
     */
    public NetworkPlayer(String name, GameGrid gameGrid) {
        super(name, gameGrid);
    }
}
