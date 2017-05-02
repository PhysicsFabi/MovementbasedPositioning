package edu.uv.students.mobiledevices.sensorbasedpositioning;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction {

    OnStepLengthChangedListener stepLengthChangedListener;

    class StepLengthData {

    }

    interface OnStepLengthChangedListener  {
        void onStepLengthChanged(StepLengthData pStepLengthData);
    }

    StepLengthReconstruction(OnStepLengthChangedListener pListener) {
        stepLengthChangedListener = pListener;
    }
}
