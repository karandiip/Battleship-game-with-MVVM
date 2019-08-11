package com.soen6441.battleship.services;


import com.soen6441.battleship.services.scorecalculator.ScoreCalculator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 *  The type Score Calculator Test.
 */

public class ScoreCalculatorTest {

    private ScoreCalculator scoreCalculator;

    /**
     *  Initial setup.
     */
    @Before
    public void setUp() {
        scoreCalculator = new ScoreCalculator();
    }

    /**
     *  Correct score to be returned on Player Win.
     */

    @Test
    public void correctScoreIsReturnedOnWin() {
        List<Long> turnTimings = new ArrayList<Long>() {
            {
                add(700L);
                add(1400L);
                add(2000L);
                add(1100L);
                add(500L);
            }
        };

        long calculatedScore = scoreCalculator.calculateScore(turnTimings, true, 0);

        assertEquals(536, calculatedScore);
    }

    /**
     *  Correct score to be returned on Player Lose.
     */

    @Test
    public void correctScoreIsReturnedOnLoose() {
        List<Long> turnTimings = new ArrayList<Long>() {
            {
                add(700L);
                add(1400L);
                add(2000L);
                add(1100L);
                add(500L);
            }
        };

        long calculatedScore = scoreCalculator.calculateScore(turnTimings, false, 2);

        assertEquals(208, calculatedScore);
    }


}
