package com.soen6441.battleship.view.gui.scenes.gameplayscene;

import com.soen6441.battleship.data.model.CellInfo;
import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.enums.CellState;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.*;
import java.util.logging.Logger;

/**
 * The type Game grid pane for 3D model.
 * This class GameGridPane3D is main frame of GUI for 3D model where when player makes any hot or enemy makes a move
 * is updated on GUI by changing the colors of the buttons in the gridPane. It responds to the mouse clicks or events,
 */

public class GameGridPane3D extends HBox implements EventHandler<MouseEvent> {
    private static final Logger logger = Logger.getLogger(GameGridPane3D.class.getName());
    private static final String GRID_BOX = "GridBox:";
    private static final int BOX_PADDING = 11;

    private final int gridSize;

    private final GridPane boxesGridPane = new GridPane();
    private final StackPane overlayPane = new StackPane();
    private IOnCoordinateHit onCoordinateHit;
    private Map<String, Box> boxes = new HashMap<>();
    private Map<String, Coordinate> boxCoordinates = new HashMap<>();
    private Set<String> shipBoxIds = new HashSet<>();

    /**
     * Instantiates a new Game 3D grid pane.
     */
    public GameGridPane3D() {
        this.gridSize = GameConfig.getsInstance().getGridSize();
        initUI();
    }


    /**
     * This method initUI adds overlay functionality to the grid,
     * when player one has played its move then the player one's grid will be locked until player two
     * has made its move.
     */

    private void initUI() {
        Box testBox = new Box(5, 5, 5);

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                new Rotate(35, Rotate.X_AXIS),
                new Rotate(-25, Rotate.Y_AXIS),
                new Rotate(-20, Rotate.Z_AXIS),
                new Translate(-20, 120, -550)
        );
        camera.setNearClip(0.1);
        camera.setFarClip(10000);

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(camera);
//        root.getChildren().add(testBox);
//        testBox.setOnMouseClicked(event -> {
//            testBox.getTransforms().addAll(
//                    new Translate(10, 0,0)
//            );
//        });

        int gridSize = GameConfig.getsInstance().getGridSize();

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                String id = buildEnemyBoxId(x, y);

                Box box = new Box(10, 10, 10);
                box.setId(id);
                box.setMaterial(new PhongMaterial(Color.SKYBLUE));
                box.setOnMouseClicked(this);
                box.setOnMouseEntered(event -> {
                    box.setMaterial(new PhongMaterial(Color.ORANGE));
                    root.getScene().setCursor(Cursor.HAND);
                    box.getTransforms().addAll(new Translate(0, 0, -0.5));
                });
                box.setOnMouseExited(event -> {
                    box.setMaterial(new PhongMaterial(Color.SKYBLUE));
                    root.getScene().setCursor(Cursor.DEFAULT);
                    box.getTransforms().addAll(new Translate(0, 0, 0.5));
                });
                root.getChildren().add(box);
                box.getTransforms().addAll(new Translate(x * 11, y * 11, 0));
                animateCube(box, x * 11, y * 11, x * 200);

                boxes.put(id, box);

                Coordinate coordinate = new Coordinate(x, y);
                boxCoordinates.put(id, coordinate);
            }
        }

        for (int x = 0; x < gridSize; x++) {
            for (int y = BOX_PADDING; y < gridSize + BOX_PADDING; y++) {
                String id = buildPlayerBoxId(x, y - BOX_PADDING);

                Box box = new Box(10, 10, 10);
                box.setId(id);
                box.setOnMouseClicked(event -> {
                    System.out.println("OK");
                });
                root.getChildren().add(box);
                box.getTransforms().addAll(new Translate(x * BOX_PADDING, y * BOX_PADDING, 0));
                animateCube(box, x * BOX_PADDING, y * BOX_PADDING, x * 200);

                boxes.put(id, box);

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

    /**
     * Add animation effect to boxes.
     *
     * @param box - the box object to be animated
     * @param x - x translation towards z axis
     * @param y - y translation towards z axis
     * @param millis - duration of animation
     */

    private static void animateCube(Box box, int x, int y, int millis) {
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
     * This method buildEnemyBoxId takes coordinates and and returns a string for to be split and make a button with the
     * coordinates associated.
     *
     * @param x
     * @param y
     * @return
     */

    private String buildEnemyBoxId(int x, int y) {
        return GRID_BOX + x + " " + y;
    }

    private String buildEnemyBoxId(Coordinate coordinate) {
        return GRID_BOX + coordinate.getX() + " " + coordinate.getY();
    }

    /**
     * This method buildPlayerBoxId takes coordinates and and returns a string for to be split and make a button with the
     * coordinates associated.
     *
     * @param x
     * @param y
     * @return
     */
    private String buildPlayerBoxId(int x, int y) {
        return GRID_BOX + (x) + " " + (y + BOX_PADDING);
    }

    private String buildPlayerBoxId(Coordinate coordinate) {
        return GRID_BOX + (coordinate.getX()) + " " + (coordinate.getY() + BOX_PADDING);
    }

    /**
     * Disables the mouse hover effect on button.
     *
     * @param box to disable hover effect of.
     */
    private void disableHoverOnMouse(Box box) {
        box.setOnMouseEntered(null);
        box.setOnMouseExited(null);
        box.setOpacity(1.0);
    }

    /**
     * Update grid: This method update grid updates the colors of the grid each time the player makes a move
     * and player 2 makes a move.
     *
     * @param grid the grid
     */
    void updateEnemyGrid(Grid grid) {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                CellInfo info = grid.getCellInfo(x, y);
                CellState cellState = info.getState();

                Box box = boxes.get(buildEnemyBoxId(x, y));

                // TODO: Move this to a separate css class
                switch (cellState) {
                    case EMPTY:
                        box.setMaterial(new PhongMaterial(Color.SKYBLUE));
                        break;
                    case SHIP:
                        // Hide the ship cell if the grid belong to enemy.
                        box.setMaterial(new PhongMaterial(Color.SKYBLUE));
                        break;
                    case EMPTY_HIT:
                        box.setMaterial(new PhongMaterial(Color.BLACK));
                        this.disableHoverOnMouse(box);
                        break;
                    case SHIP_WITH_HIT:
                        box.setMaterial(new PhongMaterial(Color.YELLOW));
                        this.disableHoverOnMouse(box);
                        break;
                    case DESTROYED_SHIP:
                        box.setMaterial(new PhongMaterial(Color.RED));
                        box.setStyle("-fx-background-color: red; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        this.disableHoverOnMouse(box);
                        break;
                    case TO_BE_PLACED:
                        box.setMaterial(new PhongMaterial(Color.PURPLE));
                        this.disableHoverOnMouse(box);
                        break;
                }
            }
        }
    }

    /**
     * Update grid: This method update grid updates the colors of the grid each time the player makes a move
     * and player 2 makes a move.
     *
     * @param grid the grid
     */
    void updatePlayerGrid(Grid grid) {
        logger.info("Update Player grid");

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                CellInfo info = grid.getCellInfo(x, y);
                CellState cellState = info.getState();

                Box box = boxes.get(buildPlayerBoxId(x, y));

                // TODO: Move this to a separate css class
                switch (cellState) {
                    case EMPTY:
                        box.setMaterial(new PhongMaterial(Color.WHITE));
                        break;
                    case SHIP:
                        // Hide the ship cell if the grid belong to enemy.
                        box.setMaterial(new PhongMaterial(Color.SKYBLUE));
                        break;
                    case EMPTY_HIT:
                        box.setMaterial(new PhongMaterial(Color.SILVER));
                        this.disableHoverOnMouse(box);
                        break;
                    case SHIP_WITH_HIT:
                        box.setMaterial(new PhongMaterial(Color.YELLOW));
                        this.disableHoverOnMouse(box);
                        break;
                    case DESTROYED_SHIP:
                        box.setMaterial(new PhongMaterial(Color.RED));
                        box.setStyle("-fx-background-color: red; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        this.disableHoverOnMouse(box);
                        break;
                    case TO_BE_PLACED:
                        box.setMaterial(new PhongMaterial(Color.PURPLE));
                        this.disableHoverOnMouse(box);
                        break;
                }
            }
        }
    }

    /**
     * Sets on coordinate hit.
     *
     * @param onCoordinateHit the on coordinate hit
     */
    void setOnCoordinateHit(IOnCoordinateHit onCoordinateHit) {
        this.onCoordinateHit = onCoordinateHit;
    }

    @Override
    public void handle(MouseEvent event) {
        if (event.getTarget() instanceof Box) {
            Box button = (Box) event.getTarget();
            String clickedBoxId = button.getId();

            logger.info(clickedBoxId);

            // Check if the button clicked is button on grid
            if (clickedBoxId.startsWith(GRID_BOX)) {
                Coordinate coordinate = boxCoordinates.get(clickedBoxId);
                if (this.onCoordinateHit != null) {
                    this.onCoordinateHit.onHit(coordinate);
                }
            }
        }
    }
}
