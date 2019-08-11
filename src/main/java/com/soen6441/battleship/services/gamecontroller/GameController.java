package com.soen6441.battleship.services.gamecontroller;

import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.model.GamePlayer;
import com.soen6441.battleship.data.model.GameOverInfo;
import com.soen6441.battleship.data.model.GameControllerInfo;
import com.soen6441.battleship.enums.HitResult;
import com.soen6441.battleship.exceptions.CoordinatesOutOfBoundsException;
import com.soen6441.battleship.services.aiplayer.ProbabilityAIPlayer;
import com.soen6441.battleship.services.boardgenerator.RandomShipPlacer;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.services.gamecontroller.gamestrategy.ITurnStrategy;
import com.soen6441.battleship.services.gamecontroller.gamestrategy.SalvaTurnStrategy;
import com.soen6441.battleship.services.gamecontroller.gamestrategy.SimpleTurnStrategy;
import com.soen6441.battleship.services.gamegrid.GameGrid;
import com.soen6441.battleship.services.gameloader.GameLoader;
import com.soen6441.battleship.services.scorecalculator.ScoreCalculator;
import com.soen6441.battleship.utils.TimerUtil;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

import java.util.Date;
import java.util.logging.Logger;


/**
 * <p>GameController is the entity which drives the game.
 * Singleton class.
 *
 * <p>Major controls include:
 * <ul>
 * <li>Creating grids -  {@link com.soen6441.battleship.services.gamecontroller.GameController#GameController}
 * <li>Tracking turns - {@link com.soen6441.battleship.services.gamecontroller.GameController#hit}
 * <li>Handle game winner - {@link GameController#handleIsGameOver}
 * </ul>
 * <p>
 * Implements {@link com.soen6441.battleship.services.gamecontroller.IGameController} interface.
 */
public class GameController implements IGameController {
    private static final Logger logger = Logger.getLogger(GameController.class.getName());
    /**
     * GameController static instance
     */
    private static GameController sGameController;

    /**
     * Player with active turn.
     */
    private String currentPlayerName;
    /**
     * Observer object to track turns
     */
    private BehaviorSubject<String> turnChangeBehaviourSubject = BehaviorSubject.create();

    private boolean isGameOver = false;
    /**
     * Observer object to track game status
     */
    private BehaviorSubject<GameOverInfo> isGameOverBehaviourSubject = BehaviorSubject.create();
    private GamePlayer player;
    private GamePlayer enemy;
    private BehaviorSubject<Boolean> playerTurnBehaviourSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> enemyTurnBehaviourSubject = BehaviorSubject.create();

    private TimerUtil turnTimer = new TimerUtil();
    private TimerUtil gameTimer = new TimerUtil();

    private ITurnStrategy turnStrategy;

    private GameConfig gameConfig = GameConfig.getsInstance();

    /**
     * Generates(if null) and returns GameController instance.
     *
     * @return The instance of GameController.
     */
    public static IGameController getInstance() {
        if (sGameController == null) {
            sGameController = new GameController();
        }
        if (GameConfig.getsInstance().isNetworkPlay()) {
            return NetworkGameController.getInstance();
        }
        return sGameController;
    }

    /**
     * Constructor method to the class
     */
    private GameController() {
        currentPlayerName = "player";
        turnChangeBehaviourSubject.onNext(currentPlayerName);

        int gridSize = GameConfig.getsInstance().getGridSize();

        player = new GamePlayer("Player", new GameGrid(gridSize));
        enemy = new ProbabilityAIPlayer("AI", new GameGrid(gridSize), player, coordinate ->
                this.hit(coordinate.getX(), coordinate.getY()));

        player.setIsMyTurn(playerTurnBehaviourSubject);
        enemy.setIsMyTurn(enemyTurnBehaviourSubject);

        // Place random ships on board
        RandomShipPlacer randomShipPlacer = new RandomShipPlacer();
        randomShipPlacer.placeRandomShips(enemy.getGameGrid());
    }

    /**
     *  Setup the game with the user selected strategy.
     *
     */
    @Override
    public void startGame() {
        turnTimer.start();
        gameTimer.start();

        if (this.turnStrategy == null) {
            if (gameConfig.isSalvaVariation()) {
                this.turnStrategy = new SalvaTurnStrategy(this.player, this.enemy);
            } else {
                this.turnStrategy = new SimpleTurnStrategy();
            }
        }
    }

    /**
     * Interface {@link IGameController} method
     *
     * @return enemy ot player object
     */
    @Override
    public GamePlayer createOrGetPlayer(String playerName) {
        if (playerName.equals("player")) {
            return player;
        } else {
            return enemy;
        }
    }

    /**
     * Interface method : {@link com.soen6441.battleship.services.gamecontroller.IGameController}
     * <p>Calls {@link GameController#handleIsGameOver()} to check if a player has won.
     *
     * @param x - x coordinate to hit on grid
     * @param y - y coordinate to hit on grid
     */
    @Override
    public void hit(int x, int y) {
        // Return if game is over
        if (isGameOver) {
            return;
        }

        logger.info(() -> String.format("%s has sent a hit on x: %d, y: %d", this.currentPlayerName, x, y));

        try {
            long timeTaken = turnTimer.stop();

            if (currentPlayerName.equals("player")) {
                player.addTimeTaken(timeTaken);
            } else {
                enemy.addTimeTaken(timeTaken);
            }

            GamePlayer playerToHit = currentPlayerName.equals("player") ? enemy : player;

            HitResult result = this.turnStrategy.hit(playerToHit, new Coordinate(x, y));

            GamePlayer playerToSwitchTurnTo = this.turnStrategy.getNextTurn(player, enemy, result);

            if (playerToSwitchTurnTo == player) {
                currentPlayerName = "player";
            } else {
                currentPlayerName = "enemy";
            }

            turnChangeBehaviourSubject.onNext(this.currentPlayerName);
        } catch (CoordinatesOutOfBoundsException e) {
            e.printStackTrace();
        }

        handleIsGameOver();

        if (!isGameOver) {
            notifyTurns();
        }
    }

    /**
     * Declares a winner by checking if all ships of either side are destroyed.
     */
    private void handleIsGameOver() {
        // Check if all ships of enemy or player are destroyed.
        boolean areAllShipsOnEnemyHit = enemy.getGameGrid().areAllShipsDestroyed();
        boolean areAllShipsOnPlayerHit = player.getGameGrid().areAllShipsDestroyed();

        this.isGameOver = areAllShipsOnEnemyHit || areAllShipsOnPlayerHit;

        GameOverInfo gameOverInfo = new GameOverInfo(this.isGameOver, areAllShipsOnEnemyHit);
        this.isGameOverBehaviourSubject.onNext(gameOverInfo);

        // Timers is stopped because we don't need it anymore as game
        // is over.
        if (isGameOver) {
            if (turnTimer.isRunning()) {
                turnTimer.stop();
            }

            if (gameTimer.isRunning()) {
                gameTimer.stop();
            }
        }
    }

    private void notifyTurns() {
        turnTimer.reset();
        turnTimer.start();
        this.playerTurnBehaviourSubject.onNext(currentPlayerName.equals("player"));
        this.enemyTurnBehaviourSubject.onNext(currentPlayerName.equals("enemy"));
    }

    /**
     * Observer method to change turns
     *
     * @return - Observer object
     */
    @Override
    public Observable<String> turnChange() {
        return turnChangeBehaviourSubject;
    }

    /**
     * Observer method to check if game is over
     *
     * @return - Observer object
     */
    @Override
    public Observable<GameOverInfo> isGameOver() {
        return this.isGameOverBehaviourSubject;
    }

    /**
     * Observer method to update turnTimer
     *
     * @return - Observer object
     */
    @Override
    public Observable<Long> turnTimer() {
        return turnTimer.asObservable();
    }

    /**
     * Observer method to update the gameTimer
     *
     * @return - Observer object
     */
    @Override
    public Observable<Long> gameTimer() {
        return gameTimer.asObservable();
    }


    /**
     *
     * @return - return the final score of the player
     */
    @Override
    public Long getFinalScore() {
        boolean didPlayerWin = this.enemy.getGameGrid().getUnSunkShips() == 0;
        return new ScoreCalculator().calculateScore(this.player.getTurnTimes(), didPlayerWin, enemy.getGameGrid().getUnSunkShips());
    }

    /**
     * Save the state of the game in an offline mode.
     */
    @Override
    public void saveGame() {
        GameControllerInfo offlineGameInfo = new GameControllerInfo();
        offlineGameInfo.setPlayerGrid(player.getGameGrid().getGrid());
        offlineGameInfo.setEnemyGrid(enemy.getGameGrid().getGrid());
        offlineGameInfo.setGridSize(GameConfig.getsInstance().getGridSize());
        offlineGameInfo.setStoreDate(new Date().getTime());
        offlineGameInfo.setCurrentTurn(this.currentPlayerName);
        offlineGameInfo.setSalva(GameConfig.getsInstance().isSalvaVariation());
        offlineGameInfo.setPlayerTurns(player.getTurnTimes());
        offlineGameInfo.setPlayerShips(player.getGameGrid().getShips());
        offlineGameInfo.setEnemyShips(enemy.getGameGrid().getShips());
        offlineGameInfo.setUnSunkPlayerShips(player.getGameGrid().getUnSunkShips());
        offlineGameInfo.setUnSunkEnemyShips(enemy.getGameGrid().getUnSunkShips());
        if (this.gameTimer.isRunning()) {
            offlineGameInfo.setGameTime(this.gameTimer.getTime());
        } else {
            offlineGameInfo.setGameTime(0);
        }

        if (this.turnTimer.isRunning()) {
            offlineGameInfo.setTurnTime(this.turnTimer.getTime());
        } else {
            offlineGameInfo.setTurnTime(0);
        }
        if (turnStrategy instanceof SalvaTurnStrategy) {
            offlineGameInfo.setPlayerSalvaCoordinates(((SalvaTurnStrategy) turnStrategy).getPlayerCoordinateHits());
            offlineGameInfo.setPlayerSalvaTurns(((SalvaTurnStrategy) turnStrategy).getPlayerTurns());
        }
        GameLoader gameLoader = new GameLoader();
        gameLoader.saveGame(GameConfig.getsInstance().getPlayerName(), offlineGameInfo);
    }

    /**
     * Load and resume the offline saved game.
     */
    @Override
    public void loadOfflineGame() {
        GameLoader gameLoader = new GameLoader();
        GameControllerInfo offlineGameInfo = gameLoader.readSavedGame(GameConfig.getsInstance().getPlayerName());

        GameGrid playerGameGrid = new GameGrid(offlineGameInfo.getPlayerGrid());
        playerGameGrid.setShips(offlineGameInfo.getPlayerShips());
        this.player = new GamePlayer("player", playerGameGrid);
        this.player.setTurnTimes(offlineGameInfo.getPlayerTurns());

        GameGrid enemyGameGrid = new GameGrid(offlineGameInfo.getEnemyGrid());
        enemyGameGrid.setShips(offlineGameInfo.getEnemyShips());

        this.enemy = new ProbabilityAIPlayer("AI", enemyGameGrid, this.player, coordinate ->
                this.hit(coordinate.getX(), coordinate.getY()));

        this.currentPlayerName = offlineGameInfo.getCurrentTurn();

        this.player.setIsMyTurn(playerTurnBehaviourSubject);
        this.enemy.setIsMyTurn(enemyTurnBehaviourSubject);

        GameConfig.getsInstance().setSalvaVariation(offlineGameInfo.isSalva());

        if (GameConfig.getsInstance().isSalvaVariation()) {
            turnStrategy = new SalvaTurnStrategy(this.player, this.enemy);
            logger.info("Offline Salva Turns --> " + offlineGameInfo.getPlayerSalvaTurns());
            logger.info("Offline Salva Coordinates --> " + offlineGameInfo.getPlayerSalvaCoordinates());
            ((SalvaTurnStrategy) turnStrategy).setPlayerTurns(offlineGameInfo.getPlayerSalvaTurns());
            ((SalvaTurnStrategy) turnStrategy).setPlayerCoordinateHits(offlineGameInfo.getPlayerSalvaCoordinates());
        } else {
            turnStrategy = new SimpleTurnStrategy();
        }

        this.turnTimer.setTimeElapsed(offlineGameInfo.getTurnTime());
        this.gameTimer.setTimeElapsed(offlineGameInfo.getGameTime());
    }

    /**
     *
     * @return game status.
     */
    @Override
    public boolean isGameComplete() {
        return this.isGameOver;
    }
}
