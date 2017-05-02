package edu.uv.students.mobiledevices.sensorbasedpositioning;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepReconstruction {

    OnStepListener onStepListener;

    class StepData {

    }

    interface OnStepListener {
        void onStep(StepData pStepData);
    }

    StepReconstruction(OnStepListener pListener) {
        onStepListener = pListener;
    }
}
