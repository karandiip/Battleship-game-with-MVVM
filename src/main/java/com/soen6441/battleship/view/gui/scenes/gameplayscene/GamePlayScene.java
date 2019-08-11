package com.soen6441.battleship.view.gui.scenes.gameplayscene;

import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.utils.TimerUtil;
import com.soen6441.battleship.view.gui.scenes.IScene;
import com.soen6441.battleship.viewmodels.gameviewmodel.IGameViewModel;
import com.sun.javafx.scene.layout.region.Margins;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * This class Game Play scene is the screen which updates the GUI after each move when players
 * makes moves. It displays the two boards and refresh the cells/buttons on every event.
 * The type Game play scene.
 */
public class GamePlayScene implements IScene {
    private Logger logger = Logger.getLogger(GamePlayer.class.getName());
    private IGameViewModel gameViewModel;

    /**
     * Instantiates a new Game play scene.
     *
     * @param gameViewModel the game view model
     */
    public GamePlayScene(IGameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }

    @Override
    public Scene buildScene() {
//        Node enemyBoard = buildEnemyBoard();
//        Node playerBoard = buildPlayerBoard();

        GameGridPane3D gameGridPane3D = new GameGridPane3D();

        gameViewModel.getPlayerGrid().subscribe(grid -> {
            Platform.runLater(() -> {
                gameGridPane3D.updatePlayerGrid(grid);
            });
        });
        gameViewModel.getEnemyGrid().subscribe(grid -> {
            Platform.runLater(() -> {
                gameGridPane3D.updateEnemyGrid(grid);
            });
        }, error -> {
            error.printStackTrace();
        });
        gameGridPane3D.setOnCoordinateHit(coordinate -> gameViewModel.sendHit(coordinate.getX(), coordinate.getY()));

        // Set up left game box
        VBox gameBox = new VBox();
        gameBox.setSpacing(40);
        gameBox.getChildren().addAll(gameGridPane3D);

        // Set up sidebar
        Node sideBar = buildSideBar();

        gameViewModel.isGameOver().subscribe(gameOverInfo -> {
            Platform.runLater(() -> {
                logger.info("GameOverInfo ---> " + gameOverInfo.toString());
                if (gameOverInfo.isGameOver()) {
                    String winnerText = "";

                    if (gameOverInfo.didPlayerWin()) {
                        winnerText = "YOU WON :D";
                    } else {
                        winnerText = "YOU LOST :(";
                    }

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Game Over!");
                    alert.setContentText(winnerText + "\nScore: " + gameViewModel.getFinalScore());
                    alert.show();
                }
            });
        });

        HBox root = new HBox();
        root.getChildren().addAll(gameBox, sideBar);

        gameViewModel.startGame();

        return new Scene(root);
    }

    /**
     * This method build enemy grid board, builds the grid pane on Stage with the given coordinate size and hides the details
     * from player.
     *
     * @return enemy grid board
     */
    private Node buildEnemyBoard() {
        // Set up enemy board
        VBox enemyBoardVBox = new VBox();
        Text enemyTitleText = new Text("Enemy Board");
        enemyTitleText.setFont(new Font(30));

        int gridSize = GameConfig.getsInstance().getGridSize();
        GameGridPane enemyGameGrid = new GameGridPane(gridSize, true);
        enemyGameGrid.setOnCoordinateHit(coordinate -> gameViewModel.sendHit(coordinate.getX(), coordinate.getY()));
        enemyBoardVBox.getChildren().addAll(enemyTitleText, enemyGameGrid);

        gameViewModel.getEnemyGrid().subscribe(grid -> {
            Platform.runLater(() -> {
                enemyGameGrid.updateGrid(grid);
            });
        });

        return enemyBoardVBox;
    }

    /**
     * This method build player grid board, builds the grid pane on Stage with the given coordinate size and shows the details
     * to player.
     *
     * @return player grid board
     */
    private Node buildPlayerBoard() {
        // Set up player board
        VBox playerBoardVBox = new VBox();
        Text playerTitleText = new Text("Player Board");
        playerTitleText.setFont(new Font(30));

        int gridSize = GameConfig.getsInstance().getGridSize();
        GameGridPane playerGameGrid = new GameGridPane(gridSize, false);
        gameViewModel.getPlayerGrid().subscribe(playerGameGrid::updateGrid);

        playerBoardVBox.getChildren().addAll(playerTitleText, playerGameGrid);

        return playerBoardVBox;
    }

    private Node buildSideBar() {
        VBox root = new VBox();
        root.setBackground(new Background(new BackgroundFill(Color.SKYBLUE, null, null)));
        root.setPadding(new Insets(16, 16, 16, 16));

        Text turnTimerText = new Text();
        turnTimerText.setFont(new Font(24));

        gameViewModel.turnTimer().subscribe(time -> {
            Platform.runLater(() -> {
                turnTimerText.setText("Turn Timer: \n" + TimerUtil.printableTime(time));
            });
        });

        Text gameTimerText = new Text();
        gameTimerText.setFont(new Font(24));

        gameViewModel.gameTimer().subscribe(time -> {
            Platform.runLater(() -> {
                gameTimerText.setText("Game Timer: \n" + TimerUtil.printableTime(time));
            });
        });

        root.getChildren().addAll(
                gameTimerText,
                turnTimerText
        );

        if (GameConfig.getsInstance().isNetworkPlay()) {
            Text turnText = new Text();
            turnText.setFont(new Font(24));
            turnText.setStyle("-fx-font-weight: bold");

            gameViewModel.playerTurnChange().subscribe(player -> {
                Platform.runLater(() -> {
                    if (player.equals("player")) {
                        turnText.setText("Your turn...");
                        turnText.setFill(Color.GREEN);
                    } else {
                        turnText.setText("Enemy's Turn!");
                        turnText.setFill(Color.RED);
                    }
                });
            });

            root.getChildren().addAll(turnText);
        }

        String gameModeText;

        if (GameConfig.getsInstance().isSalvaVariation()) {
            gameModeText = "Playing in Salva variation.";
        } else {
            gameModeText = "Playing in normal mode.";
        }

        final Text salvaVariationText = new Text(gameModeText);
        salvaVariationText.setFont(new Font(16));

        root.getChildren().addAll(salvaVariationText);

        return root;
    }
}
