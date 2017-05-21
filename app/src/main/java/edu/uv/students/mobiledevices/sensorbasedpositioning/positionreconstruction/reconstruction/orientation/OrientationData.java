package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.direction;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Created by Fabi on 02.05.2017.
 */

public class DirectionData {
    /**
     * direction in which the user walks
     * in radians
     * counter clockwise in a right handed coordinate system measured form the x-axis
     * x is East direction
     * y is South direction
     */
    public double walkingDirectionAngle = 0.0;

    /**
     * direction in which the user looks (respectively the tip of the phone points)
     * in radians
     * counter clockwise in a right handed coordinate system measured form the x-axis
     * x is East direction
     * y is South direction
     */
    public double pointingDirectionAngle = 0.0;

}
