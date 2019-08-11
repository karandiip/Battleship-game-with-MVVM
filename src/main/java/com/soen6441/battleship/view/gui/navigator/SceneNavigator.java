package com.soen6441.battleship.view.gui.navigator;

import com.soen6441.battleship.exceptions.InvalidRouteException;
import com.soen6441.battleship.exceptions.NavigatorNotInitialisedException;
import com.soen6441.battleship.view.gui.scenes.ISceneBuilder;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The type Scene navigator.
 * This class Scene navigator, navigates the scene to scene byt taking getting the
 * instance of the scene classes.
 */
public class SceneNavigator {
    private static final Logger logger = Logger.getLogger(SceneNavigator.class.getName());
    private static SceneNavigator sInstance;
    private Stage primaryStage;
    private Map<String, ISceneBuilder> routes = new HashMap<>();
    private String currentScene = "";

    /**
     * Init.
     *
     * @param primaryStage the primary stage
     */
    public static void init(Stage primaryStage) {
        checkNotNull(primaryStage);
        sInstance = new SceneNavigator(primaryStage);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SceneNavigator getInstance() {
        if (sInstance == null) {
            throw new NavigatorNotInitialisedException();
        }
        return sInstance;
    }

    /**
     * Constructor for the class sceneNavigator
     *
     * @param primaryStage
     */
    private SceneNavigator(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Register route.
     * This method register route takes the routename and Scenebuilder object and adds to routes map
     * for next route.
     *
     * @param routeName    the route name
     * @param sceneBuilder the scene builder
     */
    public void registerRoute(String routeName, ISceneBuilder sceneBuilder) {
        checkNotNull(routeName);
        checkNotNull(sceneBuilder);
        logger.info("Adding route to navigator: " + routeName);
        routes.put(routeName, sceneBuilder);
    }

    /**
     * Navigate.
     * This method Navigate, navigates to another scene when route map has any route initiated in it.
     *
     * @param route the route
     */
    public void navigate(String route) {
        if (!routes.containsKey(route)) {
            throw new InvalidRouteException();
        }
        this.currentScene = route;
        Scene scene = routes.get(route).buildScene().buildScene();
        primaryStage.setScene(scene);
    }

    public String getCurrentScene() {
        return currentScene;
    }
}
