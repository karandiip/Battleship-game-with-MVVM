package com.soen6441.battleship.view.gui.scenes.gameplayscene;

import com.soen6441.battleship.data.model.CellInfo;
import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.enums.CellState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Game grid pane.
 * This class GameGridPane is main frame of GUI where when player makes any hot or enemy makes a move
 * is updated on GUI by changing the colors of the buttons in the gridPane. It responds to the mouse clicks or events,
 */
class GameGridPane extends StackPane implements EventHandler<ActionEvent> {
    private static final String GRID_BUTTON = "GridButton:";
    private final int gridSize;
    private final boolean isEnemyPlayer;

    private final GridPane buttonsGridPane = new GridPane();
    private final StackPane overlayPane = new StackPane();
    private IOnCoordinateHit onCoordinateHit;
    private Map<String, Button> buttons = new HashMap<>();
    private Map<String, Coordinate> buttonCoordinates = new HashMap<>();
    private Set<String> shipButtonsIds = new HashSet<>();

    /**
     * Instantiates a new Game grid pane.
     *
     * @param gridSize      the grid size
     * @param isEnemyPlayer the is enemy player
     */
    GameGridPane(int gridSize, boolean isEnemyPlayer) {
        this.gridSize = gridSize;
        this.isEnemyPlayer = isEnemyPlayer;
        initStack();
        initGrid();
    }

    /**
     * This method initStack adds overlay functionality to the grid,
     * when player one has played its move then the player one's grid will be locked until player two
     * has made its move.
     */
    private void initStack() {
        this.getChildren().add(buttonsGridPane);

        overlayPane.setStyle("-fx-background-color: rgba(100, 100, 100, 0.3);");
        Text overlayText = new Text("Your Turn!");
        overlayText.setFont(new Font(20));
        overlayText.setFill(Color.WHITE);
        overlayPane.getChildren().add(overlayText);
    }

    /**
     * This method initGrid is the method takes the grid size and places all the buttons on the grid pane,
     * to be displayed on GUI.
     */
    private void initGrid() {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                String id = buildButtonId(x, y);

                Button button = new Button();
                button.setId(id);
                button.setPrefHeight(50);
                button.setPrefWidth(50);
                button.setOnAction(this);
                button.setStyle("-fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: black; -fx-border-width: 0.2");
                button.setOpacity(0.5);
                button.setOnMouseEntered(event -> {
                    this.getScene().setCursor(Cursor.HAND);
                    button.setOpacity(1.0);
                });
                button.setOnMouseExited(event -> {
                    this.getScene().setCursor(Cursor.DEFAULT);
                    button.setOpacity(0.5);
                });
                buttons.put(id, button);

                Coordinate coordinate = new Coordinate(x, y);
                buttonCoordinates.put(id, coordinate);

                // Add button to grid
                this.buttonsGridPane.add(button, x, y);
            }
        }
    }

    /**
     * Disables the mouse hover effect on button.
     *
     * @param button to disable hover effect of.
     */
    private void disableHoverOnMouse(Button button) {
        button.setOnMouseEntered(null);
        button.setOnMouseExited(null);
        button.setOpacity(1.0);
    }

    /**
     * This method buildButton takes coordinates and and returns a string for to be split and make a button with the
     * coordinates associated.
     *
     * @param x
     * @param y
     * @return
     */
    private String buildButtonId(int x, int y) {
        return GRID_BUTTON + x + " " + y;
    }

    /**
     * Update grid: This method update grid updates the colors of the grid each time the player makes a move
     * and player 2 makes a move.
     *
     * @param grid the grid
     */
    void updateGrid(Grid grid) {
        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSize; y++) {
                CellInfo info = grid.getCellInfo(x, y);
                CellState cellState = info.getState();

                Button button = buttons.get(buildButtonId(x, y));

                // TODO: Move this to a separate css class
                switch (cellState) {
                    case EMPTY:
                        button.setStyle("-fx-background-color: lightgrey; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        break;
                    case SHIP:
                        // Hide the ship cell if the grid belong to enemy.
                        if (this.isEnemyPlayer) {
                            button.setStyle("-fx-background-color: lightgrey; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        } else {
                            button.setText("S");
                            button.setStyle("-fx-background-color: lightblue; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        }
                        break;
                    case EMPTY_HIT:
                        button.setText("*");
                        button.setStyle("-fx-background-color: black; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        this.disableHoverOnMouse(button);
                        break;
                    case SHIP_WITH_HIT:
                        button.setText("O");
                        button.setStyle("-fx-background-color: #ffc23e; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        this.disableHoverOnMouse(button);
                        break;
                    case DESTROYED_SHIP:
                        button.setText("X");
                        button.setStyle("-fx-background-color: red; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        this.disableHoverOnMouse(button);
                        break;
                    case TO_BE_PLACED:
                        button.setText(" ");
                        button.setStyle("-fx-background-color: purple; -fx-background-radius: 0; -fx-border-radius: 0; -fx-border-color: darkgrey; -fx-border-width: 0.2;");
                        this.disableHoverOnMouse(button);
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

    /**
     * Sets overlay enabled.
     *
     * @param isEnabled the is enabled
     */
    void setOverlayEnabled(boolean isEnabled) {
        if (isEnabled) {
            this.getChildren().add(overlayPane);
        } else {
            this.getChildren().remove(overlayPane);
        }
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getTarget() instanceof Button) {
            Button button = (Button) event.getTarget();
            String clickedButtonId = button.getId();

            // Check if the button clicked is button on grid
            if (clickedButtonId.startsWith(GRID_BUTTON)) {
                Coordinate coordinate = buttonCoordinates.get(clickedButtonId);
                if (this.onCoordinateHit != null) {
                    this.onCoordinateHit.onHit(coordinate);
                }
            }
        }
    }
}
