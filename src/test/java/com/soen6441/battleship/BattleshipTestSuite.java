package com.soen6441.battleship;


import com.soen6441.battleship.models.ShipTest;
import com.soen6441.battleship.services.*;
import com.soen6441.battleship.utils.RandomShipPlacerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GameControllerTest.class,
        GameGridTest.class,
        ProbabilityAITest.class,
        ScoreCalculatorTest.class,
        ShipTest.class,
        RandomShipPlacerTest.class,
        GameLoaderTest.class,
})
public class BattleshipTestSuite {
}
