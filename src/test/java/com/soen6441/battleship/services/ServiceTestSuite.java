package com.soen6441.battleship.services;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *  Services TestSuite.
 */


@RunWith(Suite.class)
@Suite.SuiteClasses({
        GameControllerTest.class,
        GameGridTest.class,
        ProbabilityAITest.class,
        ScoreCalculatorTest.class,
})
public class ServiceTestSuite {
}
