package com.soen6441.battleship.exceptions;

/**
 * Singleton instance of Navigator is not initialised before use.
 */
public class NavigatorNotInitialisedException extends RuntimeException {
    public NavigatorNotInitialisedException() {
        super("Navigator not initialised. Make sure to call Navigator.init before using it!");
    }
}
