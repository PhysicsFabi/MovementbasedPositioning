package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.path;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnResetListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathReconstruction implements
        OnDirectionChangedListener,
        OnStepLengthChangedListener,
        OnStepListener,
        OnResetListener {

    private PathData pathData;

    private final OnPathChangedListener pathChangedListener;

    public PathReconstruction(OnPathChangedListener pListener) {
        pathChangedListener = pListener;
        onReset();
    }

    @Override
    public void onStep(StepData pStepData) {
        pathChangedListener.onPathChanged(pathData);
    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {

    }

    @Override
    public void onDirectionChanged(DirectionData pDirectionData) {

        pathChangedListener.onPathChanged(pathData);
    }

    @Override
    public void onReset() {
        pathData = new PathData();
        pathChangedListener.onPathChanged(pathData);
    }
}
