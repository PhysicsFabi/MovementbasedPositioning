package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction.StepData;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnStepListener {
    void onStepSeriesChanged(LinkedList<StepData> pStepSeries);
}
