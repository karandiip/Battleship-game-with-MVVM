package com.soen6441.battleship.viewmodels.shipplacementviewmodel;

import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.services.gamecontroller.IGameController;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import io.reactivex.Observable;

import java.util.logging.Logger;

/**
 * The type Ship placement view model: This class takes data from model and passes
 * the updated data from the view and vice versa.
 */
public class ShipPlacementViewModel implements IShipPlacementViewModel {
    private static final Logger logger = Logger.getLogger(ShipPlacementViewModel.class.getName());
    private IGameController gameController;
    private GamePlayer player;

    /**
     * Instantiates a new Ship placement view model.
     *
     * @param gameController the game controller
     */
    public ShipPlacementViewModel(IGameController gameController) {
        this.gameController = gameController;
        this.player = gameController.createOrGetPlayer("player");
    }

    @Override
    public void placeShip(Ship ship) {
        try {
            logger.info("Pacing ship...");
            player.getGameGrid().placeShip(ship);
        } catch (Exception e) {
            logger.warning("Unable to place ship!");
            logger.warning(e.getMessage());
        }
    }

    @Override
    public boolean canPlaceShip(Ship ship) {
        return player.getGameGrid().canPlaceShip(ship);
    }

    @Override
    public GameGrid getPlayerGameGrid() {
        return gameController.createOrGetPlayer("player").getGameGrid();
    }
}
