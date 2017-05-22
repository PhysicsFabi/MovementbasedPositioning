package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.orientation;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.LinearAlgebraTools;

/**
 * Created by Fabi on 02.05.2017.
 */

public class OrientationData {

    private final Vector3D magneticFieldNormalized_ph;
    private final Vector3D downwardsNormalized_ph;
    private final Vector3D northNormalized_ph;
    private final Rotation transformationFromCurrentToWorld;

    private final Vector3D phoneOrientationNormalized_world;
    private final Vector2D phoneOrientationProjectedOnGroundNormalized_world;

    public OrientationData(Vector3D magneticFieldNormalized_ph, Vector3D downwardsNormalized_ph, Vector3D northNormalized_ph, Rotation transformationFromCurrentToWorld) {
        this.magneticFieldNormalized_ph = magneticFieldNormalized_ph;
        this.downwardsNormalized_ph = downwardsNormalized_ph;
        this.northNormalized_ph = northNormalized_ph;
        this.transformationFromCurrentToWorld = transformationFromCurrentToWorld;
        phoneOrientationNormalized_world = transformToWorld(new Vector3D(0,1,0));
        phoneOrientationProjectedOnGroundNormalized_world = new Vector2D(phoneOrientationNormalized_world.getX(), phoneOrientationNormalized_world.getY()).normalize();
    }

    public Vector3D transformToPhone(Vector3D pVectorInWorldCoord) {
        return transformationFromCurrentToWorld.applyInverseTo(pVectorInWorldCoord);
    }

    public Vector3D transformToWorld(Vector3D pVectorInPhoneCoord) {
        return transformationFromCurrentToWorld.applyTo(pVectorInPhoneCoord);
    }

    public Vector3D getMagneticFieldNormalizedInPhoneCoord() {
        return magneticFieldNormalized_ph;
    }

    public Vector3D getDownwardsNormalizedInPhoneCoord() {
        return downwardsNormalized_ph;
    }

    public Vector3D getNorthNormalizedInPhoneCoord() {
        return northNormalized_ph;
    }

    public Vector3D getPhoneOrientationNormalizedInWorldCoord() {
        return phoneOrientationNormalized_world;
    }

    public Vector2D getPhoneOrientationProjectedOnGroundNormalizedInWorldCoord() {
        return phoneOrientationProjectedOnGroundNormalized_world;
    }
}
