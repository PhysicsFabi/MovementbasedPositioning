package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data;

import org.apache.commons.math3.linear.RealVector;

import java.util.Stack;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathData {
    /**
     * Angle counter clockwise in a right handed coordinate system
     * x is East direction
     * y is South direction
     */
    double angle;

    /**
     * Positions as 2D vectors
     * Coordinate system
     * x is East direction
     * y is South direction
     * Units are meters
     */
    Stack<RealVector> positions;
}
