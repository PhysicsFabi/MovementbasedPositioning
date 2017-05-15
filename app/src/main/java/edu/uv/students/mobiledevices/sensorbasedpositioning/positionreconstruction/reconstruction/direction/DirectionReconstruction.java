package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.direction;

import android.util.Log;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnGyroscopeEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnMagneticFieldEventListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class DirectionReconstruction implements OnGyroscopeEventListener, OnMagneticFieldEventListener {
    private final OnDirectionChangedListener directionChangedListener;
    private final DirectionData directionData;

    public DirectionReconstruction(OnDirectionChangedListener pListener) {
        directionChangedListener = pListener;
        directionData = new DirectionData();
    }

    public void init() {
        directionData.pointingDirectionAngle = 0.0;
        directionData.walkingDirectionAngle = 0.0;
        directionChangedListener.onDirectionChanged(directionData);
    }

    @Override
    public void onGyroscopeEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        // DirectionData directionData = new DirectionData();
        // reconstruct Direction
        // put into direction data
        // directionChangedListener.onDirectionChanged(directionData);
    }

    @Override
    public void onMagneticFieldEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        Log.i(Positioning.LOG_TAG, "MAGNETIC FIELD EVENT! time(ns): " + pTimeStamp_ns + " x: " + pX + " y: " + pY + " z: " + pZ);
    }


}
