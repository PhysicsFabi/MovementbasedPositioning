package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.path;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnOrientationChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.orientation.OrientationData;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathReconstruction implements
        OnOrientationChangedListener,
        OnStepListener{

    private PathData pathData;
    private LinkedList<Vector2D> previousStepSeriesPositions;
    private LinkedList<Vector2D> currentStepSeriesPositions;
    LinkedList<StepData> currentStepSeries;

    private final OnPathChangedListener pathChangedListener;

    public PathReconstruction(OnPathChangedListener pListener) {
        pathChangedListener = pListener;
        pathData = new PathData();
        previousStepSeriesPositions = new LinkedList<>();
        currentStepSeriesPositions = new LinkedList<>();
    }

    @Override
    public void onStepSeriesChanged(LinkedList<StepData> pStepSeries) {
        if(currentStepSeries!=pStepSeries) {
            previousStepSeriesPositions.addAll(currentStepSeriesPositions);
        }
        currentStepSeriesPositions = new LinkedList<>();
        Vector2D previousPosition = previousStepSeriesPositions.isEmpty() ? new Vector2D(.0, .0) : previousStepSeriesPositions.getLast();
        for(StepData stepData : pStepSeries) {
            Vector3D stepDirectionNormalized_world = stepData.horizontalDirectionNormalized_world;
            Vector2D stepVector_world = new Vector2D(
                    stepDirectionNormalized_world.getX(),
                    stepDirectionNormalized_world.getY()
            ).scalarMultiply(stepData.stepLengthM);
            Vector2D nextPosition = previousPosition.add(stepVector_world);
            currentStepSeriesPositions.add(nextPosition);
            previousPosition = nextPosition;
        }
        currentStepSeries = pStepSeries;
        pathData.reInitPositions();
        pathData.getPositions().addAll(previousStepSeriesPositions);
        pathData.getPositions().addAll(currentStepSeriesPositions);
        pathChangedListener.onPathChanged(pathData);
    }

    @Override
    public void onOrientationChanged(OrientationData pOrientationData) {
        Vector2D phoneOrientationProjectedOnGroundNormalized_w = pOrientationData.getPhoneOrientationProjectedOnGroundNormalizedInWorldCoord();
        pathData.angle = Math.signum(phoneOrientationProjectedOnGroundNormalized_w.getY())*Math.acos(phoneOrientationProjectedOnGroundNormalized_w.getX());
        pathChangedListener.onOrientationChanged(pathData.angle);
    }
}
