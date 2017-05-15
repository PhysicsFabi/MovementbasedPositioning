package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.path;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathReconstruction implements
        OnDirectionChangedListener,
        OnStepLengthChangedListener,
        OnStepListener{

    private PathData pathData;
    private DirectionData currentDirectionData;
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
        Vector2D directionVector = new Vector2D(Math.sin(currentDirectionData.walkingDirectionAngle), Math.cos(currentDirectionData.walkingDirectionAngle));
        Vector2D stepVector = directionVector.scalarMultiply(currentStepLengthData.stepLength);
        Vector2D nextPosition = pathData.positions.getLast().add(stepVector);
        pathData.positions.add(nextPosition);
        pathChangedListener.onPathChanged(pathData);
    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {
        currentStepLengthData = pStepLengthData;
    }

    @Override
    public void onDirectionChanged(DirectionData pDirectionData) {
        currentDirectionData = pDirectionData;
        pathData.angle = currentDirectionData.pointingDirectionAngle;
        pathChangedListener.onPathChanged(pathData);
    }
}
