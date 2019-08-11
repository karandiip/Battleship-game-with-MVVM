package com.soen6441.battleship.view.gui;

import com.soen6441.battleship.common.SceneRoutes;
import com.soen6441.battleship.data.model.OfflineGameInfo;
import com.soen6441.battleship.services.gameconfig.GameConfig;
import com.soen6441.battleship.services.gamecontroller.GameController;
import com.soen6441.battleship.services.gameloader.GameLoader;
import com.soen6441.battleship.view.IView;
import com.soen6441.battleship.view.gui.navigator.SceneNavigator;
import com.soen6441.battleship.view.gui.scenes.gameplayscene.GamePlay3DScene;
import com.soen6441.battleship.view.gui.scenes.gameplayscene.GamePlayScene;
import com.soen6441.battleship.view.gui.scenes.initialuserinputscene.InitialUserInputScene;
import com.soen6441.battleship.view.gui.scenes.shipplacement.ShipPlacementScene;
import com.soen6441.battleship.viewmodels.gameviewmodel.GameViewModel;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.IShipPlacementViewModel;
import com.soen6441.battleship.viewmodels.shipplacementviewmodel.ShipPlacementViewModel;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The type Gui view: The class GUIview is the starting class of the application
 * It loads all the scenes from intial user input to game play scene.
 * The scenes gives instances to this class to load into application.
 */
public class GUIView extends Application implements IView {
    private Logger logger = Logger.getLogger(GUIView.class.getName());

    private IShipPlacementViewModel shipPlacementViewModel;

    /**
     * Instantiates a new Gui view.
     */
    public GUIView() {

    }

    /**
     * Application class default start method.
     */

    @Override
    public void start() {
        launch();
    }

    /**
     * Application class default start method.
     *
     * @param primaryStage stage object.
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneNavigator.init(primaryStage);

        SceneNavigator.getInstance().registerRoute(SceneRoutes.INITIAL_USER_INPUT, InitialUserInputScene::new);
        SceneNavigator.getInstance().registerRoute(SceneRoutes.SHIP_PLACEMENT,
                () -> new ShipPlacementScene(new ShipPlacementViewModel(GameController.getInstance())));
        SceneNavigator.getInstance().registerRoute(SceneRoutes.GAME_PLAY, () -> {
            return new GamePlayScene(new GameViewModel(GameController.getInstance()));
        });

//        SceneNavigator.getInstance().registerRoute("3d", () -> {
//            return new GamePlay3DScene();
//        });


        primaryStage.show();

        SceneNavigator.getInstance().navigate(SceneRoutes.INITIAL_USER_INPUT);
    }

    /**
     * Application class default stop method.
     *
     * @throws Exception
     */

    @Override
    public void stop() throws Exception {
        super.stop();
        GameLoader gameLoader = new GameLoader();

        OfflineGameInfo offlineGameInfo = new OfflineGameInfo();
        offlineGameInfo.setCurrentRouteName(SceneNavigator.getInstance().getCurrentScene());
        offlineGameInfo.setGameOver(GameController.getInstance().isGameComplete());

        gameLoader.saveOfflineGameInfo(GameConfig.getsInstance().getPlayerName(), offlineGameInfo);

        if (!GameController.getInstance().isGameComplete()) {
            GameController.getInstance().saveGame();
        } else {
            gameLoader.deleteFile(GameConfig.getsInstance().getPlayerName());
        }
    }
}
