package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.steplength.StepLengthData;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnStepLengthChangedListener {
    void onStepLengthChanged(StepLengthData pStepLengthData);
}
