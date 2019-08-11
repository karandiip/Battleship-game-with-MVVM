package com.soen6441.battleship.viewmodels.initiuserviewmodel;

import com.soen6441.battleship.services.gameconfig.GameConfig;

/**
 * The type Init user view model.
 */
public class InitUserViewModel implements IInitUserViewModel {
    private GameConfig gameConfig = GameConfig.getsInstance();

    @Override
    public void setName(String name) {
        gameConfig.setPlayerName(name);
    }

    @Override
    public void setSalve(boolean isSalva) {
        gameConfig.setSalvaVariation(isSalva);
    }
}
