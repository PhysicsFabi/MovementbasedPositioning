package edu.uv.students.mobiledevices.sensorbasedpositioning;

import java.util.LinkedList;

/**
 * Created by Fabi on 02.05.2017.
 */

public class EventDistributor implements
        PathReconstruction.OnPathChangedListener,
        StepReconstruction.OnStepListener,
        DirectionReconstruction.OnDirectionChangedListener,
        StepLengthReconstruction.OnStepLengthChangedListener {

    LinkedList<PathReconstruction.OnPathChangedListener> onPathChangedListeners;
    LinkedList<StepReconstruction.OnStepListener> onStepListeners;
    LinkedList<DirectionReconstruction.OnDirectionChangedListener> onDirectionChangedListeners;
    LinkedList<StepLengthReconstruction.OnStepLengthChangedListener> onStepLengthChangedListeners;

    EventDistributor() {
        onPathChangedListeners = new LinkedList<>();
        onStepListeners = new LinkedList<>();
        onDirectionChangedListeners = new LinkedList<>();
        onStepLengthChangedListeners = new LinkedList<>();
    }

    public void registerOnPathChangedListener(PathReconstruction.OnPathChangedListener pListener) {
        onPathChangedListeners.add(pListener);
    }

    public void registerOnStepListener(StepReconstruction.OnStepListener pListener) {
        onStepListeners.add(pListener);
    }

    public void registerOnDirectionChangedListener(DirectionReconstruction.OnDirectionChangedListener pListener) {
        onDirectionChangedListeners.add(pListener);
    }

    public void registerOnStepLengthChangedListener(StepLengthReconstruction.OnStepLengthChangedListener pListener) {
        onStepLengthChangedListeners.add(pListener);
    }

    @Override
    public void onPathChanged(PathReconstruction.PathData pPathData) {
        for(PathReconstruction.OnPathChangedListener listener : onPathChangedListeners)
            listener.onPathChanged(pPathData);
    }

    @Override
    public void onStep(StepReconstruction.StepData pStepData) {
        for(StepReconstruction.OnStepListener listener : onStepListeners)
            listener.onStep(pStepData);
    }

    @Override
    public void onDirectionChanged(DirectionReconstruction.DirectionData pDirectionData) {
        for(DirectionReconstruction.OnDirectionChangedListener listener : onDirectionChangedListeners)
            listener.onDirectionChanged(pDirectionData);
    }

    @Override
    public void onStepLengthChanged(StepLengthReconstruction.StepLengthData pStepLengthData) {
        for(StepLengthReconstruction.OnStepLengthChangedListener listener : onStepLengthChangedListeners)
            listener.onStepLengthChanged(pStepLengthData);
    }
}
