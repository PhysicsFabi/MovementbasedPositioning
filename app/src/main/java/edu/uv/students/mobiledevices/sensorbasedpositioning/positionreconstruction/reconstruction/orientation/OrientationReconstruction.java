package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.orientation;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDownwardsVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnMagneticFieldVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnOrientationChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.LinearAlgebraTools;

/**
 * Created by Fabi on 02.05.2017.
 */

public class OrientationReconstruction implements
        OnMagneticFieldVectorChangedListener,
        OnDownwardsVectorChangedListener,
        OrientationData {

    final OnOrientationChangedListener listener;
    Vector3D downwardsNormalized_ph;
    Vector3D magneticField_ph;

    Vector3D southNormalized_ph;

    final Vector3D downwardsNormalized_w = new Vector3D(.0,.0,1.0);
    final Vector3D southNormalized_w = new Vector3D(.0,1.0,.0);

    final Vector3D phoneOrientationNormalized_ph = new Vector3D(.0,1.0,.0);

    Vector3D phoneOrientationNormalized_w;
    Vector2D phoneOrientationProjectedOnGroundNormalized_w;

    Rotation transformationFromPhoneToWorld;


    public OrientationReconstruction(OnOrientationChangedListener pListener) {
        listener = pListener;
    }

    @Override
    public void onMagneticFieldVectorChanged(Vector3D pMagneticField_ph) {
        magneticField_ph = pMagneticField_ph;
        updateOrientation();
    }

    @Override
    public void onDownwardsVectorChanged(Vector3D pDownwards_ph) {
        downwardsNormalized_ph = pDownwards_ph;
        updateOrientation();
    }

    public void updateOrientation() {
        if(magneticField_ph == null || downwardsNormalized_ph == null)
            return;
        southNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, magneticField_ph).scalarMultiply(-1.0).normalize();
        transformationFromPhoneToWorld = new Rotation(downwardsNormalized_ph, southNormalized_ph, downwardsNormalized_w, southNormalized_w);

        phoneOrientationNormalized_w = transformToWorld(phoneOrientationNormalized_ph);
        phoneOrientationProjectedOnGroundNormalized_w = new Vector2D(phoneOrientationNormalized_w.getX(), phoneOrientationNormalized_w.getY());

        listener.onOrientationChanged(this);
    }

    @Override
    public Vector3D transformToPhone(Vector3D pVectorInWorldCoord) {
        return transformationFromPhoneToWorld.applyInverseTo(pVectorInWorldCoord);
    }

    @Override
    public Vector3D transformToWorld(Vector3D pVectorInPhoneCoord) {
        return transformationFromPhoneToWorld.applyTo(pVectorInPhoneCoord);
    }

    @Override
    public Vector3D getDownwardsNormalizedInPhoneCoord() {
        return downwardsNormalized_ph;
    }

    @Override
    public Vector3D getSouthNormalizedInPhoneCoord() {
        return southNormalized_ph;
    }

    @Override
    public Vector3D getPhoneOrientationNormalizedInWorldCoord() {
        return phoneOrientationNormalized_w;
    }

    @Override
    public Vector2D getPhoneOrientationProjectedOnGroundNormalizedInWorldCoord() {
        return phoneOrientationProjectedOnGroundNormalized_w;
    }
}
