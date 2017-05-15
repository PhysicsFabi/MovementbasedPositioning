package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.step;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepReconstruction implements OnAccelerometerEventListener {

    private final OnStepListener onStepListener;
    private final StepData stepData;

    public StepReconstruction(OnStepListener pListener) {
        onStepListener = pListener;
        stepData = new StepData();
    }

    public void init() {

    }

    @Override
    public void onAccelerometerEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        // StepData stepData = new StepData();
        // reconstruct Step
        // put everything into stepData
        // onStepListener.onStep(stepData);
    }

}
