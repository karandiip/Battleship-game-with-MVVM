package com.soen6441.battleship.exceptions;

/**
 * Invalid route passed in {@link com.soen6441.battleship.view.gui.navigator.SceneNavigator}.
 * No route is registered with this name.
 */
public class InvalidRouteException extends RuntimeException {
    public InvalidRouteException() {
        super("Passed route is invalid!");
    }
}
