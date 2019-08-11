package com.soen6441.battleship.app;

import com.soen6441.battleship.services.gamecontroller.GameController;
import com.soen6441.battleship.services.gamecontroller.IGameController;
import com.soen6441.battleship.view.console.ConsoleView;
import com.soen6441.battleship.viewmodels.gameviewmodel.GameViewModel;
import com.soen6441.battleship.viewmodels.gameviewmodel.IGameViewModel;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.IShipPlacementViewModel;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.ShipPlacementViewModel;

/**
 * The entry point of application with GUI.
 */
public class AppWithoutUI {
    public static void main(String[] args) {
        IGameController gameController = GameController.getInstance();
        IGameViewModel gameViewModel = new GameViewModel(gameController);
        IShipPlacementViewModel shipPlacementViewModel = new ShipPlacementViewModel(gameController);
        ConsoleView consoleView = new ConsoleView(gameViewModel, shipPlacementViewModel);
        consoleView.start();
    }
}
