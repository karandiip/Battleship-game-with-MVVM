package com.soen6441.battleship.enums;

/**
 * The enum Hit result class has the hit properties of the cells/buttons which are hit, miss and alreadyhit.
 */
public enum HitResult {
    /**
     * Ship is hit successfully.
     */
    HIT,
    /**
     * Hit attempt was a miss.
     */
    MISS,
    /**
     * Hit attempt was already made for the coordinate.
     */
    ALREADY_HIT,
}
