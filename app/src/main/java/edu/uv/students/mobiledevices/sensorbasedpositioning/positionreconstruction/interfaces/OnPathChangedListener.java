package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.PathData;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OnPathChangedListener {
    void onPathChanged(PathData pPathData);
}