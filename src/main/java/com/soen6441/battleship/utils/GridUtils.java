package com.soen6441.battleship.utils;

import com.soen6441.battleship.data.model.Grid;
import com.soen6441.battleship.enums.CellState;

import java.util.logging.Logger;

/**
 * Utility functions related to {@link Grid} class.
 */
public class GridUtils {
    private static final Logger logger = Logger.getLogger(GridUtils.class.getName());
    private static final String EMPTY_CHAR = " ---- ";
    private static final String SHIP_CHAR = " ship ";

    /**
     * Print the grid on console in a more understandable form.
     *
     * @param grid Grid to be printed.
     */
    public static void printGrid(Grid grid) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < grid.getGridSize(); i++) {
            for (int j = 0; j < grid.getGridSize(); j++) {
                CellState state = grid.getCellState(j, i);

                switch (state) {
                    case EMPTY:
                        stringBuilder.append(EMPTY_CHAR);
                        break;
                    case SHIP:
                        stringBuilder.append(SHIP_CHAR);
                        break;
                    case SHIP_WITH_HIT:
                        stringBuilder.append(" **** ");
                        break;
                    case DESTROYED_SHIP:
                        stringBuilder.append(" $$$$ ");
                        break;
                    case EMPTY_HIT:
                        stringBuilder.append(" xxxx ");
                        break;
                    case TO_BE_PLACED:
                        stringBuilder.append(" tttt ");
                    default:
                        break;
                }
            }
            stringBuilder.append("\n\n");
        }

        logger.info("Game Grid: \n" + stringBuilder.toString());
    }
}
