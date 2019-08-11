package com.soen6441.battleship.view.gui.scenes.shipplacement;

import com.soen6441.battleship.common.ButtonStyle;
import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.data.model.Ship;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.enums.ShipDirection;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.IShipPlacementViewModel;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The type Ship placement grid 3 d.
 */
public class ShipPlacementGrid3D extends HBox {
    private static final Logger logger = Logger.getLogger(ShipPlacementGrid.class.getName());
    private static final String GRID_BOX = "GridButton:";
    private static final double HIGHLIGHT_ELEVATION = -1.5;

    /**
     * Size of the ship currently allowed to be placed.
     */
    private int currentShipLength = 5;
    private int numOfShipsPlaced = 0;
    private Map<String, Box> boxes = new HashMap<>();
    private Map<String, Coordinate> boxCoordinates = new HashMap<>();
    private Set<String> shipBoxIds = new HashSet<>();
    private boolean isSelectingShip = false;
    private Coordinate prevSelectedBtnCoordinate;
    private PublishSubject<Boolean> isSelectingShipSubject = PublishSubject.create();
    private PublishSubject<Integer> numShipPlacedSubject = PublishSubject.create();
    private PublishSubject<Ship> shipAddedPublishSubject = PublishSubject.create();
    private final int gridSize = GameConfig.getsInstance().getGridSize();
    private Observable<Grid> gridObservable;

    private final IShipPlacementViewModel shipPlacementViewModel;

    /**
     * Instantiates a new Ship placement grid 3 d.
     *
     * @param shipPlacementViewModel the ship placement view model
     * @param gridObservable         the grid observable
     */
    public ShipPlacementGrid3D(IShipPlacementViewModel shipPlacementViewModel, Observable<Grid> gridObservable) {
        this.shipPlacementViewModel = shipPlacementViewModel;
        this.gridObservable = gridObservable;
        initUI();
        initGridObservable();
    }

    private void initGridObservable() {
        this.gridObservable.subscribe(grid -> {
            shipBoxIds.clear();
            for (int x = 0; x < gridSize; x++) {
                for (int y = 0; y < gridSize; y++) {
                    if (grid.getCellState(x, y) == CellState.SHIP) {
                        shipBoxIds.add(buildBoxId(x, y));
                    }
                }
            }
            updateUI();
        });
    }

    private void updateUI() {
        // Disable all the boxes that have been selected for a ship.
        boxes.forEach((boxId, box) -> {
            boolean isShipDisabled = shipBoxIds.contains(boxId);

            if (isShipDisabled) {
                box.setTranslateZ(HIGHLIGHT_ELEVATION);
                logger.info("Setting color to black!");
                box.setStyle(ButtonStyle.DISABLED);
                box.setMaterial(new PhongMaterial(Color.WHITE));
            } else {
                box.setTranslateZ(0.0);
                box.setOpacity(1.0);
                box.setStyle(ButtonStyle.DEFAULT);
                box.setMaterial(new PhongMaterial(Color.SKYBLUE));
            }
        });
    }

    private void initUI() {
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                new Rotate(35, Rotate.X_AXIS),
                new Rotate(-25, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.Z_AXIS),
                new Translate(10, 70, -400)
        );
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        Group root = new Group();
        root.getChildren().add(camera);

        int gridSize = GameConfig.getsInstance().getGridSize();

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                Box box = new Box(10, 10, 10);
                box.setMaterial(new PhongMaterial(Color.SKYBLUE));
                root.getChildren().add(box);
                box.getTransforms().addAll(new Translate(x * 11, y * 11, 0));
                //animateBox(box, x * 11, y * 11, x * 200);

                String id = buildBoxId(x, y);
                box.setId(id);
                box.setStyle(ButtonStyle.DEFAULT);
                boxes.put(id, box);

                // Drag over event handler is used for the receiving node to allow movement
                box.setOnDragOver(event -> {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                });

                //Drag entered changes the appearance of the receiving node to indicate to the player that they can place there
                box.setOnDragEntered(event -> {
                    //The drag-and-drop gesture entered the target
                    //show the user that it is an actual gesture target

                    String[] shipInfo = ((Button) event.getGestureSource()).getId().split(" ");
                    ShipDirection shipDirection = shipInfo[0].equals("v") ? ShipDirection.VERTICAL : ShipDirection.HORIZONTAL;
                    int shipLength = Integer.parseInt(shipInfo[1]);

                    if (event.getSource() instanceof Box) {
                        String boxId = ((Box) event.getSource()).getId();

                        if (boxId.startsWith(GRID_BOX)) {
                            Coordinate boxCoordinate = boxCoordinates.get(boxId);
                            highlightHoveringBoxes(shipDirection, shipLength, boxCoordinate);
                        }
                    }

                    logger.info("Drag entered at " + box.getId());

                    event.consume();

                });

                // Drag dropped draws the image to the receiving node
                box.setOnDragDropped(event -> {
                    logger.info("Ship dropped...");
                    String[] shipInfo = ((Button) event.getGestureSource()).getId().split(" ");
                    ShipDirection shipDirection = shipInfo[0].equals("v") ? ShipDirection.VERTICAL : ShipDirection.HORIZONTAL;
                    int shipLength = Integer.parseInt(shipInfo[1]);

                    //If there is an image on the drag board, read it and use it
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    Node node = event.getPickResult().getIntersectedNode();
                    logger.info("Node --->" + node);
                    if (node == box) {

                        int xShipInitial = boxCoordinates.get(box.getId()).getX();
                        int yShipInitial = boxCoordinates.get(box.getId()).getY();

                        // TODO: set image size; use correct column/row span
                        logger.info("Placing ship at " + xShipInitial + ", " + yShipInitial);

                        String boxId = ((Box) event.getSource()).getId();
                        Coordinate boxCoordinate = boxCoordinates.get(boxId);

                        success = placeShip(shipDirection, shipLength, boxCoordinate);

//                        updateUI();
                    } else {
                        logger.info("GOING IN ELSE");
                    }
                    //let the source know whether the image was successfully transferred and used
                    event.setDropCompleted(success);

                    event.consume();

                });
                //Drag exited reverts the appearance of the receiving node when the mouse is outside of the node
                box.setOnDragExited(event -> {
                    //mouse moved away, remove graphical cues
                    updateUI();
                    event.consume();
                });

                Coordinate coordinate = new Coordinate(x, y);
                boxCoordinates.put(id, coordinate);
            }
        }

        PointLight light = new PointLight();

        light.getTransforms().addAll(
                new Translate(75, 200, -200)
        );

        root.getChildren().addAll(light);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 1200, 1200);
        subScene.setCamera(camera);
        subScene.setFill(Color.SKYBLUE);
        subScene.setPickOnBounds(true);
        Group group = new Group();
        group.getChildren().addAll(subScene);
        this.getChildren().addAll(group);
    }


    private static void animateBox(Box box, int x, int y, int millis) {
        Translate radiusTranslate = new Translate(x, y, 0);
        Translate zMovement = new Translate();

        box.getTransforms().setAll(zMovement, radiusTranslate);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(zMovement.zProperty(), 0d)),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(zMovement.zProperty(), 1d, Interpolator.EASE_BOTH))
        );

        tl.setCycleCount(Timeline.INDEFINITE);
        tl.setAutoReverse(true);
        tl.setDelay(new Duration(millis));
        tl.play();
    }

    /**
     * Highlight the box on drag over.
     * @param shipDirection
     * @param shipLength
     * @param coordinate
     */
    private void highlightHoveringBoxes(ShipDirection shipDirection, int shipLength, Coordinate coordinate) {
        int shipEndX = shipDirection == ShipDirection.HORIZONTAL ? coordinate.getX() + shipLength - 1 : coordinate.getX();
        int shipEndY = shipDirection == ShipDirection.HORIZONTAL ? coordinate.getY() : coordinate.getY() + shipLength - 1;

        Ship ship = new Ship.Builder()
                .setDirection(shipDirection)
                .setStartCoordinates(coordinate.getX(), coordinate.getY())
                .setEndCoordinates(shipEndX, shipEndY)
                .setLength(shipLength)
                .build();

        boolean canPlaceShip = this.shipPlacementViewModel.canPlaceShip(ship);

        if (shipDirection == ShipDirection.HORIZONTAL) {
            int endX = Math.min(this.gridSize, coordinate.getX() + shipLength);

            for (int x = coordinate.getX(); x < endX; x++) {
                String boxId = buildBoxId(new Coordinate(x, coordinate.getY()));
                Box box = boxes.get(boxId);

                box.setMaterial(new PhongMaterial(Color.ORANGE));
                box.setTranslateZ(HIGHLIGHT_ELEVATION);

                if (!canPlaceShip) {
                    box.setMaterial(new PhongMaterial(Color.RED));
                    box.setTranslateZ(0.0);
                }
            }
        } else {
            int endY = Math.min(this.gridSize, coordinate.getY() + shipLength);

            for (int y = coordinate.getY(); y < endY; y++) {
                String boxId = buildBoxId(new Coordinate(coordinate.getX(), y));
                Box box = boxes.get(boxId);

                box.setMaterial(new PhongMaterial(Color.ORANGE));
                box.setTranslateZ(HIGHLIGHT_ELEVATION);

                if (!canPlaceShip) {
                    box.setMaterial(new PhongMaterial(Color.RED));
                    box.setTranslateZ(0.0);
                }
            }
        }
    }

    /**
     * Place ship on the grid
     * @param shipDirection - direction of the ship
     * @param shipLength - length of the ship
     * @param coordinate - coordinate on which ship has to be placed
     * @return
     */
    private boolean placeShip(ShipDirection shipDirection, int shipLength, Coordinate coordinate) {
        int shipEndX = shipDirection == ShipDirection.HORIZONTAL ? coordinate.getX() + shipLength - 1 : coordinate.getX();
        int shipEndY = shipDirection == ShipDirection.HORIZONTAL ? coordinate.getY() : coordinate.getY() + shipLength - 1;

        Ship ship = new Ship.Builder()
                .setDirection(shipDirection)
                .setStartCoordinates(coordinate.getX(), coordinate.getY())
                .setEndCoordinates(shipEndX, shipEndY)
                .setLength(shipLength)
                .build();

        if (!shipPlacementViewModel.canPlaceShip(ship)) {
            return false;
        }

        shipPlacementViewModel.placeShip(ship);

        numOfShipsPlaced++;
        this.numShipPlacedSubject.onNext(numOfShipsPlaced);

        if (shipDirection == ShipDirection.HORIZONTAL) {
            if ((coordinate.getX() + shipLength - 1) < this.gridSize) {
                logger.info(shipDirection.toString() + " " + shipLength + " " + coordinate.toString());
                for (int i = coordinate.getX(); i < (coordinate.getX() + shipLength); i++) {
                    String boxId = buildBoxId(new Coordinate(i, coordinate.getY()));

                    shipBoxIds.add(boxId);

                    Box box = boxes.get(boxId);

                    ImageView imageView = new ImageView("https://static.thenounproject.com/png/12287-200.png");
                    imageView.setFitWidth(box.getWidth() / 2);
                    imageView.setFitHeight(box.getHeight() / 2);
                }
            }
        } else {
            if ((coordinate.getY() + shipLength - 1) < this.gridSize) {
                for (int j = coordinate.getY(); j < (coordinate.getY() + shipLength); j++) {
                    String boxId = buildBoxId(new Coordinate(coordinate.getX(), j));

                    shipBoxIds.add(boxId);

                    Box box = boxes.get(boxId);

                    ImageView imageView = new ImageView("https://static.thenounproject.com/png/12287-200.png");
                    imageView.setFitWidth(box.getWidth() / 2);
                    imageView.setFitHeight(box.getHeight() / 2);
                }
            }
        }

        return true;
    }

    private String buildBoxId(int x, int y) {
        return GRID_BOX + x + " " + y;
    }

    private String buildBoxId(Coordinate coordinate) {
        return GRID_BOX + coordinate.getX() + " " + coordinate.getY();
    }
}