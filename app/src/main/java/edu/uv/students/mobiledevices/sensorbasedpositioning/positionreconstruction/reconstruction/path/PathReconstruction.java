package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.path;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnOrientationChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.accelerometer.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.orientation.OrientationData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.steplength.StepLengthData;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathReconstruction implements
        OnOrientationChangedListener,
        OnStepLengthChangedListener,
        OnStepListener{

    private final PathData pathData;
    private OrientationData currentOrientationData;
    private StepLengthData currentStepLengthData;
    private StepData currentStepData;

    private final OnPathChangedListener pathChangedListener;

    public PathReconstruction(OnPathChangedListener pListener) {
        pathChangedListener = pListener;
        pathData = new PathData();

    }

    public void init() {
        pathChangedListener.onPathChanged(pathData);
    }

    @Override
    public void onStep(StepData pStepData) {
        currentStepData = pStepData;
        //Vector2D stepVector = pStepData.horizontalDirectionNormalized_ph.scalarMultiply(currentStepLengthData.stepLength);
        Vector3D stepDirectionNormalized_world = currentOrientationData.transformToWorld(pStepData.horizontalDirectionNormalized_ph);
        Vector2D stepVector_world = new Vector2D(
                stepDirectionNormalized_world.getX(),
                stepDirectionNormalized_world.getY()
        ).scalarMultiply(1.0);
        Vector2D nextPosition = pathData.positions.get(pathData.positions.size()-1).add(stepVector_world);
        pathData.positions.add(nextPosition);
        pathChangedListener.onPathChanged(pathData);
    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {
        currentStepLengthData = pStepLengthData;
    }

    @Override
    public void onOrientationChanged(OrientationData pOrientationData) {
        currentOrientationData = pOrientationData;
        Vector2D phoneOrientationProjectedOnGroundNormalized_w = currentOrientationData.getPhoneOrientationProjectedOnGroundNormalizedInWorldCoord();
        pathData.angle = Math.signum(phoneOrientationProjectedOnGroundNormalized_w.getY())*Math.acos(phoneOrientationProjectedOnGroundNormalized_w.getX());
        pathChangedListener.onPathChanged(pathData);
    }
}
