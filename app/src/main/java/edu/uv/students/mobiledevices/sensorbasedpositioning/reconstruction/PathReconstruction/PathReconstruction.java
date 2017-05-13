package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.PathReconstruction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnResetListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepListener;

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
