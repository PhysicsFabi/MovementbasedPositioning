package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.orientation.OrientationData;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnOrientationChangedListener {
    void onOrientationChanged(OrientationData pOrientationData);
}
