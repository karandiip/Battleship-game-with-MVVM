package com.soen6441.battleship.services.gameloader;

import com.soen6441.battleship.data.model.GameControllerInfo;
import com.soen6441.battleship.data.model.OfflineGameInfo;

import java.io.*;

/**
 *  The type GameLoader class resumes saved games.
 */

public class GameLoader {
    private String folderPrefix = "";

    public GameLoader() {
    }

    /**
     * Constructor of type GameLoader.
     * @param folderPrefix - file address of saved game.
     */
    public GameLoader(String folderPrefix) {
        this.folderPrefix = folderPrefix;

        if (this.folderPrefix != null && !this.folderPrefix.isEmpty()) {
            if (!this.folderPrefix.endsWith("/")) {
                this.folderPrefix += "/";
            }

            File prefixFolder = new File(this.folderPrefix);
            prefixFolder.mkdir();
        }

        if (this.folderPrefix == null) {
            this.folderPrefix = "";
        }
    }

    /**
     * Method to save game to a specified folder location.
     * @param fileName
     * @param gameInfo - game information to be stored.
     */
    public void saveGame(String fileName, GameControllerInfo gameInfo) {
        storeAsSerializable(fileName, gameInfo);
    }

    public GameControllerInfo readSavedGame(String fileName) {
        return readOfflineFile(fileName);
    }

    /**
     * Check to see if a game exists.
     * @param fileName
     * @return
     */
    public boolean doesFileExist(String fileName) {
        File file = new File(folderPrefix + "output/" + fileName);
        return file.exists();
    }

    public void deleteFile(String fileName) {
        File file = new File(folderPrefix + "output/" + fileName);
        file.delete();
    }

    /**
     * Delete game from a file.
     * @param fileName
     */
    public void deleteGameFiles(String fileName) {
        File controllerFile = new File(folderPrefix + "output/" + fileName);
        File gameInfoFile = new File(folderPrefix + "output/" + fileName + "_gameinfo");
        controllerFile.delete();
        gameInfoFile.delete();
    }

    /**
     * Serialize data to store in a file
     * @param fileName
     * @param gameInfo - Game information to be stored.
     */
    private void storeAsSerializable(String fileName, GameControllerInfo gameInfo) {
        try {
            checkAndCreateFileFolder(folderPrefix + "output/", fileName);
            File file = new File(folderPrefix + "output/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(gameInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkAndCreateFileFolder(String folderName, String fileName) throws IOException {
        if (folderName != null) {
            File folder = new File(folderName);

            if (!folder.exists()) {
                folder.mkdir();
            }
        }

        if (fileName != null) {
            File file = new File(folderName + fileName);

            if (!file.exists()) {
                file.createNewFile();
            }
        }
    }

    private GameControllerInfo readOfflineFile(String fileName) {
        try {
            File savedFiled = new File(this.folderPrefix + "output/" + fileName);
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(savedFiled);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (GameControllerInfo) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save game in a file when in offline mode.
     * @param fileName
     * @param offlineGameInfo - game information.
     */
    public void saveOfflineGameInfo(String fileName, OfflineGameInfo offlineGameInfo) {
        try {
            checkAndCreateFileFolder(folderPrefix + "output/", fileName + "_gameinfo");
            File file = new File(folderPrefix + "output/", fileName + "_gameinfo");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(offlineGameInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OfflineGameInfo readOfflineGameInfo(String fileName) {
        try {
            File savedFiled = new File(this.folderPrefix + "output/" + fileName + "_gameinfo");
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(savedFiled);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (OfflineGameInfo) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
