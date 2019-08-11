package com.soen6441.battleship.view.gui.scenes;

import javafx.scene.Scene;

/**
 * The interface Scene builder.
 */
@FunctionalInterface()
public interface ISceneBuilder {
    /**
     * Build scene scene.
     *
     * @return the scene
     */
    IScene buildScene();
}
