package com.soen6441.battleship.app;

import com.soen6441.battleship.services.firebasemanager.FirebaseManager;
import com.soen6441.battleship.services.gamecontroller.GameController;
import com.soen6441.battleship.services.gamecontroller.IGameController;
import com.soen6441.battleship.view.IView;
import com.soen6441.battleship.view.gui.GUIView;
import com.soen6441.battleship.viewmodels.gameviewmodel.GameViewModel;
import com.soen6441.battleship.viewmodels.gameviewmodel.IGameViewModel;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.IShipPlacementViewModel;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.ShipPlacementViewModel;

/**
 * The entry point of console based application.
 */
public class AppWithGUI {
    public static void main(String[] args) {
        new FirebaseManager().init();
        IGameController gameController = GameController.getInstance();
        IGameViewModel gameViewModel = new GameViewModel(gameController);
        IShipPlacementViewModel shipPlacementViewModel = new ShipPlacementViewModel(gameController);
        GUIView view = new GUIView();
        view.start();
    }
}
