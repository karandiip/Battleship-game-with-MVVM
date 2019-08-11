package com.soen6441.battleship.services.networkmanager;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.soen6441.battleship.data.model.Coordinate;
import com.soen6441.battleship.data.repository.PlayerConnected;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Server class for online version of the game.
 */

public class NetworkServer implements Runnable {
    private final static Logger logger = Logger.getLogger(NetworkServer.class.getName());
    private static NetworkServer sInstance;
    private SocketIOClient player1IOClient;
    private SocketIOClient player2IOClient;

    private NetworkServer() {
        init();
    }

    public static NetworkServer getInstance() {
        if (sInstance == null) {
            sInstance = new NetworkServer();
        }
        return sInstance;
    }

    /**
     * Setup and connect the server. Assign a player to the server.
     */
    private void init() {
        Configuration configuration = new Configuration();
        configuration.setHostname("localhost");
        configuration.setPort(5000);

        final SocketIOServer socketIOServer = new SocketIOServer(configuration);

        socketIOServer.addEventListener("chatevent", String.class, (socketIOClient, s, ackRequest) -> {
            logger.info("chatevent " + s);
        });

        socketIOServer.addEventListener(NetworkEvent.PLAYER_CONNECTED, String.class, (socketIOClient, player, ackRequest) -> {
            logger.info("Player connected: " + player + " [" + "]");

            if (player.equals(NetworkEvent.Players.PLAYER1)) {
                player1IOClient = socketIOClient;
            }

            if (player.equals(NetworkEvent.Players.PLAYER2)) {
                player2IOClient = socketIOClient;
            }
        });

        socketIOServer.start();
    }


    @Override
    public void run() {

    }
}
