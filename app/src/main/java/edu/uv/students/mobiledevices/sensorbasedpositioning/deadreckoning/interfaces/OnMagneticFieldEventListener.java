package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnMagneticFieldEventListener {
    void onMagneticFieldEvent(SensorEvent pSensorEvent);
}
