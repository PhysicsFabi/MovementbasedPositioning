package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.orientation;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

/**
 * Created by Fabi on 02.05.2017.
 */

public interface OrientationData {

    Vector3D transformToPhone(Vector3D pVectorInWorldCoord);

    Vector3D transformToWorld(Vector3D pVectorInPhoneCoord);

    Vector3D getDownwardsNormalizedInPhoneCoord();

    Vector3D getSouthNormalizedInPhoneCoord();

    Vector3D getPhoneOrientationNormalizedInWorldCoord();

    Vector2D getPhoneOrientationProjectedOnGroundNormalizedInWorldCoord();
}
