package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by Fabi on 22.05.2017.
 */

public interface OnDownwardsVectorChangedListener {
    void onDownwardsVectorChanged(Vector3D pDownwards_ph);
}
