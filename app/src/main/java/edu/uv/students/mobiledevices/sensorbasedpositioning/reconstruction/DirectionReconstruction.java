package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnGyroscopeEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnMagneticFieldEventListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class DirectionReconstruction implements OnGyroscopeEventListener, OnMagneticFieldEventListener {
    private final OnDirectionChangedListener directionChangedListener;

    public DirectionReconstruction(OnDirectionChangedListener pListener) {
        directionChangedListener = pListener;
    }

    @Override
    public void onGyroscopeEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {

    }

    @Override
    public void onMagneticFieldEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {

    }
}
