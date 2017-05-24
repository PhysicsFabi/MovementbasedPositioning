package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.path;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
     * This list is synchronized
     */
    private List<Vector2D> positions;

    public PathData() {
        angle = 0.0;
        reInitPositions();
    }

    public void reInitPositions() {
        positions = Collections.synchronizedList(new LinkedList<Vector2D>());
        positions.add(new Vector2D(0,0));
    }

    public List<Vector2D> getPositions() {
        return positions;
    }

}
