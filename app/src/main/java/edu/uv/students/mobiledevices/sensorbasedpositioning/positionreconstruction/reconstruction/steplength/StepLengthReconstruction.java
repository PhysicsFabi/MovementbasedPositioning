package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.steplength;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepLengthChangedListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction {

    private final OnStepLengthChangedListener stepLengthChangedListener;

    public StepLengthReconstruction(OnStepLengthChangedListener pListener) {
        stepLengthChangedListener = pListener;
    }
}
