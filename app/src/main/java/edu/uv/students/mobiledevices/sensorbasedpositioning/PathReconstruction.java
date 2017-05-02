package edu.uv.students.mobiledevices.sensorbasedpositioning;

/**
 * Created by Fabi on 02.05.2017.
 */

public class PathReconstruction implements
        DirectionReconstruction.OnDirectionChangedListener,
        StepLengthReconstruction.OnStepLengthChangedListener,
        StepReconstruction.OnStepListener {

    OnPathChangedListener pathChangedListener;

    public class PathData {

    }
    public interface OnPathChangedListener {
        void onPathChanged(PathData pPathData);
    }


    @Override
    public void onStep(StepReconstruction.StepData pStepData) {

    }

    @Override
    public void onStepLengthChanged(StepLengthReconstruction.StepLengthData pStepLengthData) {

    }

    @Override
    public void onDirectionChanged(DirectionReconstruction.DirectionData pDirectionData) {

    }

    PathReconstruction(OnPathChangedListener pListener) {
        pathChangedListener = pListener;
    }
}
