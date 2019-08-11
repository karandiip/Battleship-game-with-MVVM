package com.soen6441.battleship.view.gui.scenes.shipplacement;

import com.google.firebase.database.FirebaseDatabase;
import com.soen6441.battleship.common.ButtonStyle;
import com.soen6441.battleship.common.SceneRoutes;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.view.gui.navigator.SceneNavigator;
import com.soen6441.battleship.view.gui.scenes.IScene;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.IShipPlacementViewModel;
import io.reactivex.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The type Ship placement scene: This class has is the GUI of the Ship placement scene, it contains a grid
 * which has buttons of gridsize^2.
 */
public class ShipPlacementScene implements IScene {

    private final IShipPlacementViewModel shipPlacementViewModel;
    private Map<Button, Integer> allShips = new HashMap<>();
    private final ShipPlacementGrid shipPlacementGrid;
    private final ShipPlacementGrid3D shipPlacementGrid3D;


    /**
     * Instantiates a new Ship placement scene.
     *
     * @param shipPlacementViewModel the ship placement view model
     */
    public ShipPlacementScene(IShipPlacementViewModel shipPlacementViewModel) {
        checkNotNull(shipPlacementViewModel);
        this.shipPlacementViewModel = shipPlacementViewModel;
        this.shipPlacementGrid = new ShipPlacementGrid(shipPlacementViewModel,
                shipPlacementViewModel.getPlayerGameGrid().getGridAsObservable());
        this.shipPlacementGrid3D = new ShipPlacementGrid3D(shipPlacementViewModel,
                shipPlacementViewModel.getPlayerGameGrid().getGridAsObservable());
    }

    /**
     * This method build scene is an overridden method to build scene for ship placement.
     *
     * @return scene
     */
    @Override
    public Scene buildScene() {

        shipPlacementGrid.getShipAddedObservable().subscribe(shipPlacementViewModel::placeShip);

        // TODO: Move this to a separate Node class, maybe even fxml file.
        Node toolbar = buildToolbar(
                event -> shipPlacementGrid.cancelShipSelection(),
                event -> {
                    if (GameConfig.getsInstance().isNetworkPlay()) {
                        String roomName = GameConfig.getsInstance().getRoomName();
                        String playerName = GameConfig.getsInstance().getFBPlayerName();

                        FirebaseDatabase.getInstance().getReference("games")
                                .child(roomName)
                                .child(playerName + "_ships")
                                .setValueAsync(shipPlacementViewModel.getPlayerGameGrid().getShips());

                        FirebaseDatabase.getInstance().getReference("games")
                                .child(roomName)
                                .child(playerName)
                                .setValueAsync(shipPlacementViewModel.getPlayerGameGrid().getGrid());
                    }
                    SceneNavigator.getInstance().navigate(SceneRoutes.GAME_PLAY);
                },
                shipPlacementGrid.getShipSelectionObservable(),
                shipPlacementGrid.getSelectedShipCountObservable()
        );

        // TODO: Move this to a separate Node class, maybe even fxml file.
        Node infoBar = buildInfoBar(
                shipPlacementGrid.getSelectedShipCountObservable()
        );

        Node verticalShipBar = buildVerticalShipButtons();
        Node horizontalShipBar = buildHorizontalShipButtons();


        VBox vBox = new VBox();
        vBox.getChildren().addAll(infoBar, toolbar, shipPlacementGrid3D);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(vBox, verticalShipBar, horizontalShipBar);

        return new Scene(hBox);
    }

    /**
     * This method buildtoolbar is the GUI which gives two selections to user
     * Cancel selection and Done, once the 5 ships are placed the done button works as navigator to game play scene.
     *
     * @param onCancelSelectionHandler
     * @param onDoneHandler
     * @param selectionObservable
     * @param shipPlacedCountObservable
     * @return Node Hbox
     */
    private Node buildToolbar(
            EventHandler<ActionEvent> onCancelSelectionHandler,
            EventHandler<ActionEvent> onDoneHandler,
            Observable<Boolean> selectionObservable,
            Observable<Integer> shipPlacedCountObservable
    ) {
        HBox hBox = new HBox();
        hBox.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));


        // Button to cancel current ship selection
        Button cancelSelectionButton = new Button();
        cancelSelectionButton.setDisable(true);
        cancelSelectionButton.setText("Cancel Selection");
        cancelSelectionButton.setOnAction(onCancelSelectionHandler);
        selectionObservable.subscribe(isSelecting -> cancelSelectionButton.setDisable(!isSelecting));

        Button doneButton = new Button();
        doneButton.setDisable(true);
        doneButton.setText("Done");
        doneButton.setOnAction(onDoneHandler);
        if (shipPlacementViewModel.getPlayerGameGrid().getShips().size() >= 5) {
            doneButton.setDisable(false);
        }

        shipPlacementViewModel.getPlayerGameGrid().getGridAsObservable().subscribe(grid -> {
            doneButton.setDisable(shipPlacementViewModel.getPlayerGameGrid().getShips().size() < 5);
        });

        shipPlacedCountObservable.subscribe(count -> {

        });

        Pane spacing = new Pane();
        HBox.setHgrow(spacing, Priority.ALWAYS);

        hBox.getChildren().addAll(spacing, cancelSelectionButton, doneButton);

        return hBox;
    }

    /**
     * This method buildInfoBar displays the status of ship placement.
     *
     * @param shipPlacedCountObservable
     * @return hBox
     */
    private Node buildInfoBar(
            Observable<Integer> shipPlacedCountObservable
    ) {
        HBox hBox = new HBox();
        hBox.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10, 10, 10, 10));

        int ships = shipPlacementViewModel.getPlayerGameGrid().getShips().size();

        Text placedShipCountText = new Text(String.format("Ships Placed: %d/5", ships));

        shipPlacedCountObservable.subscribe(count -> {
            placedShipCountText.setText(String.format("Ships Placed: %d/5", shipPlacementViewModel.getPlayerGameGrid().getShips().size()));
        });

        hBox.getChildren().addAll(placedShipCountText);

        return hBox;
    }

    private Node buildVerticalShipButtons() {

        VBox vBox = new VBox(10);
        vBox.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
        vBox.setPadding(new Insets(100, 10, 10, 10));
        Text verticalShipLabel = new Text("Vertical");
        verticalShipLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        verticalShipLabel.setFill(Color.BLACK);
//        verticalShipLabel.setStrokeWidth(0.5);
//        verticalShipLabel.setStroke(Color.BLACK);

        Button ship1v = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 1, "v 1");
        Button ship2v = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 2, "v 2");
        Button ship3v = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 3, "v 3");
        Button ship4v = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 4, "v 4");
        Button ship5v = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 5, "v 5");

        Button[] shipButtons = {ship1v, ship2v, ship3v, ship4v, ship5v};

        for (Ship ship : shipPlacementViewModel.getPlayerGameGrid().getShips()) {
            shipButtons[ship.getLength() - 1].setVisible(false);
        }

        vBox.getChildren().addAll(verticalShipLabel, ship1v, ship2v, ship3v, ship4v, ship5v);
        return vBox;
    }

    private Node buildHorizontalShipButtons() {

        VBox vBox = new VBox(10);
        vBox.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
        vBox.setPadding(new Insets(100, 10, 10, 10));
        Text horizontalShipLabel = new Text("Horizontal");
        horizontalShipLabel.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 15));
        horizontalShipLabel.setFill(Color.BLACK);
//        horizontalShipLabel.setStrokeWidth(0.5);
//        horizontalShipLabel.setStroke(Color.BLACK);

        Button ship1h = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 1, "h 1");
        Button ship2h = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 2, "h 2");
        Button ship3h = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 3, "h 3");
        Button ship4h = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 4, "h 4");
        Button ship5h = createShipButtons("https://static.thenounproject.com/png/12287-200.png", 5, "h 5");

        Button[] shipButtons = {ship1h, ship2h, ship3h, ship4h, ship5h};

        for (Ship ship : shipPlacementViewModel.getPlayerGameGrid().getShips()) {
            shipButtons[ship.getLength() - 1].setVisible(false);
        }

        vBox.getChildren().addAll(horizontalShipLabel, ship1h, ship2h, ship3h, ship4h, ship5h);

        return vBox;

    }

    /**
     * Create ship button
     *
     * @param imageURL - ship image
     * @param shipSize - ship size
     * @param shipId - unique ID for the ship
     * @return
     */
    private Button createShipButtons(String imageURL, int shipSize, String shipId) {


        Button shipButton = new Button();
        shipButton.setStyle(ButtonStyle.SHIP_PLACEMENT);
        allShips.put(shipButton, shipSize);
        shipButton.setId(shipId);
        Image img = new Image(imageURL);

        ImageView view = new ImageView(img);
        view.setFitHeight(30);
        view.setFitWidth(40);

        shipButton.setGraphic(view);
        shipButton.setText(Integer.toString(shipSize));

        //Drag detected event handler is used for adding drag functionality to the boat node
        shipButton.setOnDragDetected(event -> {
            //Allow any transfer node
            Dragboard db = shipButton.startDragAndDrop(TransferMode.MOVE);

            //Put ImageView on dragboard
            ClipboardContent cbContent = new ClipboardContent();
            Image shipImage = new Image(imageURL, 100, 100, false, false);
            cbContent.putImage(shipImage);
            db.setContent(cbContent);
            event.consume();

        });

        shipButton.setOnDragDone(event -> {
            //the drag and drop gesture has ended
            //if the data was successfully moved, clear it
            if (event.getTransferMode() == TransferMode.MOVE) {
                for (Map.Entry<Button, Integer> entry : allShips.entrySet()) {
                    if (shipSize == entry.getValue()) {
                        entry.getKey().setVisible(false);
//                        System.out.println("Hello I am here");
                        shipButton.setVisible(false);
                    }
                }
            }
            event.consume();

        });

        return shipButton;

    }
}
