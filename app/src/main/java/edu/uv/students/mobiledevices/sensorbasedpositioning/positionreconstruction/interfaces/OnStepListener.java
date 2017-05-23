package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.accelerometer.StepData;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnStepListener {
    void onStep(StepData pStepData);
}
