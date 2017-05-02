package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepReconstruction implements OnAccelerometerEventListener {

    private OnStepListener onStepListener;

    public StepReconstruction(OnStepListener pListener) {
        onStepListener = pListener;
    }

    @Override
    public void onAccelerometerEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {

    }
}
