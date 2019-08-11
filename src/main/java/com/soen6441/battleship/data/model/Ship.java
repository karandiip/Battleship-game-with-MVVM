package com.soen6441.battleship.data.model;

import com.soen6441.battleship.enums.ShipDirection;

import java.io.Serializable;

/**
 * Represents a single ship.
 */
public class Ship implements Serializable {
    /**
     * Unique id of the ship.
     */
    private String uniqueID;

    /**
     * A name for the ship.
     */
    private String name;

    /**
     * Starting x-coordinate of the ship.
     */
    private int startX;

    /**
     * Starting y-coordinate of the ship.
     */
    private int startY;

    /**
     * Ending x-coordinate of the ship. (Inclusive)
     */
    private int endX;

    /**
     * Ending y-coordinate of the ship. (Inclusive)
     */
    private int endY;

    /**
     * Length of the ship.
     */
    private int length;

    /**
     * Total hits that have been take on this ship.
     */
    private int hits = 0;

    /**
     * Direction of the ship. Can be vertical or horizontal.
     */
    private ShipDirection direction;

    /**
     * Builder pattern for building a ship
     */
    public static class Builder {
        private String uniqueId;
        private String name;
        private int startX;
        private int startY;
        private int endX;
        private int endY;
        private int length;
        private ShipDirection direction;

        public Builder setStartCoordinates(int x, int y) {
            this.startX = x;
            this.startY = y;
            return this;
        }

        public Builder setEndCoordinates(int x, int y) {
            this.endX = x;
            this.endY = y;
            return this;
        }

        public Builder setDirection(ShipDirection direction) {
            this.direction = direction;
            return this;
        }

        public Builder setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setLength(int length) {
            this.length = length;
            return this;
        }

        public Ship build() {
            return new Ship(this);
        }
    }

    public Ship() {}

    private Ship(Builder builder) {
        this.uniqueID = builder.uniqueId;
        this.name = builder.name;
        this.startX = builder.startX;
        this.startY = builder.startY;
        this.endX = builder.endX;
        this.endY = builder.endY;
        this.direction = builder.direction;
        this.length = builder.length;
    }

    public String getName() {
        return name;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public ShipDirection getDirection() {
        return direction;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public int getLength() {
        return length;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    /**
     * @return boolean if the ship has been destroyed or not.
     */
    public boolean isSunk() {
        return this.length == this.hits;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "uniqueID='" + uniqueID + '\'' +
                ", name='" + name + '\'' +
                ", startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                ", length=" + length +
                ", hits=" + hits +
                ", direction=" + direction +
                '}';
    }
}
