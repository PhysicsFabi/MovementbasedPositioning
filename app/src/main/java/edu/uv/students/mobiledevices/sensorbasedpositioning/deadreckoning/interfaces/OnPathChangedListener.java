package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.path.PathData;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnPathChangedListener {
    void onPathChanged(PathData pPathData);
    void onOrientationChanged(double orientation);
}
