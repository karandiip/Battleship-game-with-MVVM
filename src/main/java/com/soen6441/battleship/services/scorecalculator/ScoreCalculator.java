package com.soen6441.battleship.services.scorecalculator;

import java.util.List;

public class ScoreCalculator {
    private static final long MAX_SCORE = 1000;

    /**
     * Calculate Score final game score.
     *
     * @param turnTimings list of time taken for each turn.
     * @param didWin did the player win the game.
     * @param remainingEnemyShips number of enemy ships that player couldn't sink.
     * @return
     */
    public long calculateScore(List<Long> turnTimings, boolean didWin, int remainingEnemyShips) {
        double divisionFactor = 1.0;

        double timeAddition = 0.0;

        for (Long time : turnTimings) {
            double addition;

            if (time < 1000) {
                addition = 1;
            } else if (time < 1500) {
                addition = 1.5;
            } else if (time < 3000) {
                addition = 4;
            } else if (time < 4000) {
                addition = 5;
            } else if (time < 5000) {
                addition = 7;
            } else {
                addition = 10;
            }

            timeAddition += addition;
        }

        divisionFactor += (timeAddition / turnTimings.size());

        if (didWin) {
            divisionFactor /= 1.5;
        } else {
            divisionFactor += remainingEnemyShips;
        }

        return Math.round(MAX_SCORE / divisionFactor);
    }
}
