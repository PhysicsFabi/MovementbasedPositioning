package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by Fabi on 22.05.2017.
 */

public interface OnMagneticFieldVectorChangedListener {
    void onMagneticFieldVectorChanged(Vector3D pMagneticField_ph);
}
