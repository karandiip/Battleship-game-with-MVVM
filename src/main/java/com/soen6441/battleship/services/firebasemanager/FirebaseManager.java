package com.soen6441.battleship.services.firebasemanager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileInputStream;
import java.util.logging.Logger;

/**
 * FirebaseManager class setups connection with the server.
 *
 */

public class FirebaseManager {
    private static final Logger logger = Logger.getLogger(FirebaseManager.class.getName());

    /**
     * Connection setup class
     */
    public void init() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("firebase-admin-key.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://soen6441-d85ed.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);

            logger.info("Connected to firebase.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
