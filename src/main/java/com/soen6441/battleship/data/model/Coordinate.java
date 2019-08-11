package com.soen6441.battleship.data.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a single point on an xy plane (Grid in our case).
 */
public class Coordinate implements Serializable {
    /**
     * X coordinate.
     */
    private final int x;

    /**
     * Y coordinate.
     */
    private final int y;

    /**
     * Note: No error checking is done, this is just a data class.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * @param o other object. Ideally of type Coordinate.
     * @return If the coordinates of passed object match coordinates
     * of this object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y;
    }
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
