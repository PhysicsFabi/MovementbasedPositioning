package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.accelerometer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
/**
 * Created by Fabi on 02.05.2017.
 */

public class StepData {
    public double stepLengthM;
    public long durationNs;
    public long footDownTimeNs;
    public Vector3D horizontalDirectionNormalized_ph;
}
