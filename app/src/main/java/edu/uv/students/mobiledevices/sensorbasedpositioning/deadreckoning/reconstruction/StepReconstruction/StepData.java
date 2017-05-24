package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
/**
 * Created by Fabi on 02.05.2017.
 */

public class StepData {
    public enum StepType {
        BEGIN_OF_SERIES,
        MIDDLE_OF_SERIES,
        END_OF_SERIES,
        BEGIN_AND_END_OF_SERIES
    }
    public StepType stepType;
    public double stepLengthM;
    public long startTimeNs;
    public Vector3D horizontalDirectionNormalized_ph;
    public Vector3D horizontalDirectionNormalized_world;
    public long endTimeNs;

    public StepData(StepType pStepType) {
        stepType = pStepType;
    }

    public long getDurationNs() {
        return endTimeNs-startTimeNs;
    }
}
