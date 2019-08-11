package com.soen6441.battleship.view.gui.scenes.initialuserinputscene;

import com.google.firebase.database.FirebaseDatabase;
import com.soen6441.battleship.common.SceneRoutes;
import com.soen6441.battleship.data.model.OfflineGameInfo;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.services.gamecontroller.GameController;
import com.soen6441.battleship.services.gameloader.GameLoader;
import com.soen6441.battleship.services.networkmanager.NetworkClient;
import com.soen6441.battleship.services.networkmanager.NetworkEvent;
import com.soen6441.battleship.services.networkmanager.NetworkServer;
import com.soen6441.battleship.view.gui.navigator.SceneNavigator;
import com.soen6441.battleship.viewmodels.initiuserviewmodel.InitUserViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * The type User input scene controller is the controller for Welcome screen fxml,
 * takes grid size and name from the user and passes data to view model and ship placement.
 */
public class UserInputSceneController {
    final private Logger logger = Logger.getLogger(UserInputSceneController.class.getName());
    private InitUserViewModel initUserViewModel = new InitUserViewModel();

    private String playerName;
    @FXML
    private TextField nameField;
    @FXML
    private TextField roomTextField;
    @FXML
    private RadioButton salvaNoRadioButton;
    @FXML
    private RadioButton salvaYesRadioButton;

    @FXML
    void initialize() {
        salvaNoRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                initUserViewModel.setSalve(false);
            }
        });

        salvaYesRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                initUserViewModel.setSalve(true);
            }
        });
    }

    /**
     * Start action:  This method start action is called on all the events from GUI
     * displays the alert messages on the GUI and validates the input from the user.
     *
     * @param event the event
     */
    @FXML
    protected void startAction(ActionEvent event) {
        //Alert message for the invalid inputs
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (nameField.getText().trim().isEmpty()
                || nameField.getText() == null
                || !Pattern.matches(".*[a-zA-Z]+.*", nameField.getText())) {

            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid input type from user!");
            alert.setContentText("Player Name should be in alphabets from A-Z !");
            alert.showAndWait();
            logger.config("Empty or invalid name field.");
        } else {
            playerName = nameField.getText();
            initUserViewModel.setName(playerName);
            //Navigate to ship placement screen

            // Load offline game
            GameLoader gameLoader = new GameLoader();
            OfflineGameInfo offlineGameInfo = gameLoader.readOfflineGameInfo(playerName);

            if (offlineGameInfo != null && !offlineGameInfo.isGameOver()) {
                Alert loadConfirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Load existing game?",
                        ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                loadConfirmationAlert.setHeaderText("Load existing game?");
                loadConfirmationAlert.setContentText("You left your previous game without finishing it, do you want to load and continue?");

                loadConfirmationAlert.showAndWait();

                if (loadConfirmationAlert.getResult() == ButtonType.YES) {
                    GameController.getInstance().loadOfflineGame();
                    SceneNavigator.getInstance().navigate(offlineGameInfo.getCurrentRouteName());
                } else if (loadConfirmationAlert.getResult() == ButtonType.NO) {
                    new GameLoader().deleteGameFiles(playerName);
                    SceneNavigator.getInstance().navigate(SceneRoutes.SHIP_PLACEMENT);
                }
            } else {
                SceneNavigator.getInstance().navigate(SceneRoutes.SHIP_PLACEMENT);
            }
        }
    }

    @FXML
    void startGameServer(ActionEvent event) {
        //Alert message for the invalid inputs
        String room = roomTextField.getText();
        if (room == null || room.isEmpty()) {
            return;
        }
        GameConfig.getsInstance().setRoomName(room);

        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (nameField.getText().trim().isEmpty()
                || nameField.getText() == null
                || !Pattern.matches(".*[a-zA-Z]+.*", nameField.getText())) {

            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid input type from user!");
            alert.setContentText("Player Name should be in alphabets from A-Z !");
            alert.showAndWait();
            logger.config("Empty or invalid name field.");
        } else {
            playerName = nameField.getText();
            initUserViewModel.setName(playerName);

            GameConfig.getsInstance().setNetworkPlay(true);
            GameConfig.getsInstance().setServer(true);

            try {
                FirebaseDatabase.getInstance().getReference("games")
                        .child(room)
                        .removeValueAsync().get();

                FirebaseDatabase.getInstance().getReference("games")
                        .child(room)
                        .child("playerTurn")
                        .setValueAsync(GameConfig.getsInstance().getFBPlayerName()).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            SceneNavigator.getInstance().navigate(SceneRoutes.SHIP_PLACEMENT);
        }
    }

    @FXML
    void connectGameServer(ActionEvent event) {
        String room = roomTextField.getText();
        if (room == null || room.isEmpty()) {
            return;
        }
        GameConfig.getsInstance().setRoomName(room);

        //Alert message for the invalid inputs
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (nameField.getText().trim().isEmpty()
                || nameField.getText() == null
                || !Pattern.matches(".*[a-zA-Z]+.*", nameField.getText())) {

            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid input type from user!");
            alert.setContentText("Player Name should be in alphabets from A-Z !");
            alert.showAndWait();
            logger.config("Empty or invalid name field.");
        } else {
            playerName = nameField.getText();
            initUserViewModel.setName(playerName);

            GameConfig.getsInstance().setNetworkPlay(true);
            GameConfig.getsInstance().setServer(false);
            //NetworkClient.getInstance().init(NetworkEvent.Players.PLAYER2, playerName);
            SceneNavigator.getInstance().navigate(SceneRoutes.SHIP_PLACEMENT);
        }
    }
}