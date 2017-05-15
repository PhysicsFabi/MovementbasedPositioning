package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.steplength;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepLengthChangedListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction {

    private final OnStepLengthChangedListener stepLengthChangedListener;
    private final StepLengthData stepLengthData;

    public StepLengthReconstruction(OnStepLengthChangedListener pListener) {
        stepLengthChangedListener = pListener;
        stepLengthData = new StepLengthData();
    }

    public void init() {
        stepLengthData.stepLength = 1.0;
        stepLengthChangedListener.onStepLengthChanged(stepLengthData);
    }
}
