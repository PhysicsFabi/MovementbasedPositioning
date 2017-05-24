package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by Fabi on 16.05.2017.
 */

public class LinearAlgebraTools {
    public static Vector3D projectOnPane(Vector3D pNormal, Vector3D pToProject) {
        return pToProject.subtract(pNormal.scalarMultiply(pToProject.dotProduct(pNormal)));
    }
}
