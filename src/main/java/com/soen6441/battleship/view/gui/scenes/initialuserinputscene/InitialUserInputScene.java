package com.soen6441.battleship.view.gui.scenes.initialuserinputscene;

import com.soen6441.battleship.view.gui.navigator.SceneNavigator;
import com.soen6441.battleship.view.gui.scenes.IScene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;

/**
 * The type Initial user input scene.
 * This method InitialUserInputScene takes the Java FXML file from resources and loads to GUI.
 */
public class InitialUserInputScene implements IScene {
    @Override
    public Scene buildScene() {
        File file = new File("src/main/resources/WelcomeScreen.fxml");
        Parent root = null;
        try {
            root = FXMLLoader.load(file.toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Scene(root);
    }
}
