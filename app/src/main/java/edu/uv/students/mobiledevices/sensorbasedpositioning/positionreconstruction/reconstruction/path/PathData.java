package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.path;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathData {
    /**
     * Angle
     * in radians
     * counter clockwise in a right handed coordinate system measured form the x-axis
     * x is East direction
     * y is South direction
     */
    public double angle;

    /**
     * Positions as 2D vectors
     * Coordinate system
     * x is East direction
     * y is South direction
     * Units are meters
     */
    public LinkedList<Vector2D> positions;

    public PathData() {
        angle = 0.0;
        positions = new LinkedList<>();
        positions.add(new Vector2D(0,0));
    }
}
