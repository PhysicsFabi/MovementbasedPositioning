package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.direction.DirectionReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.path.PathReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.steplength.StepLengthReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.step.StepReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnGyroscopeEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnResetListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 *
 * Distributes all the events (sensors, steps, ...) that happen during the program run.
 */

public class PositionReconstruction implements
        OnPathChangedListener,
        OnStepListener,
        OnDirectionChangedListener,
        OnStepLengthChangedListener,
        OnAccelerometerEventListener,
        OnGyroscopeEventListener,
        OnMagneticFieldEventListener,
        OnResetListener {

    private final LinkedList<OnPathChangedListener> onPathChangedListeners;
    private final LinkedList<OnStepListener> onStepListeners;
    private final LinkedList<OnDirectionChangedListener> onDirectionChangedListeners;
    private final LinkedList<OnStepLengthChangedListener> onStepLengthChangedListeners;

    private final LinkedList<OnAccelerometerEventListener> onAccelerometerEventListeners;
    private final LinkedList<OnGyroscopeEventListener> onGyroscopeEventListeners;
    private final LinkedList<OnMagneticFieldEventListener> onMagneticSensorEventListeners;

    private final LinkedList<OnResetListener> onResetListeners;

    private StepReconstruction stepReconstruction;
    private DirectionReconstruction directionReconstruction;
    private StepLengthReconstruction stepLengthReconstruction;
    private PathReconstruction pathReconstruction;


    public PositionReconstruction() {
        onPathChangedListeners = new LinkedList<>();
        onStepListeners = new LinkedList<>();
        onDirectionChangedListeners = new LinkedList<>();
        onStepLengthChangedListeners = new LinkedList<>();

        onAccelerometerEventListeners = new LinkedList<>();
        onGyroscopeEventListeners = new LinkedList<>();
        onMagneticSensorEventListeners = new LinkedList<>();

        onResetListeners = new LinkedList<>();

        stepReconstruction = new StepReconstruction(this);
        directionReconstruction = new DirectionReconstruction(this);
        stepLengthReconstruction = new StepLengthReconstruction(this);
        pathReconstruction = new PathReconstruction(this);
        initEventDistribution();
    }

    private void initEventDistribution() {
        // step reconstruction
        registerAccelerometerEventListener(stepReconstruction);

        // direction reconstruction
        registerGyroscopeEventListener(directionReconstruction);
        registerMagneticFieldEventListener(directionReconstruction);

        //step length reconstruction

        // path reconstruction
        registerOnDirectionChangedListener(pathReconstruction);
        registerOnStepLengthChangedListener(pathReconstruction);
        registerOnStepListener(pathReconstruction);
        registerOnResetListener(pathReconstruction);
    }

    public void registerOnPathChangedListener(OnPathChangedListener pListener) {
        onPathChangedListeners.add(pListener);
    }

    public void registerOnStepListener(OnStepListener pListener) {
        onStepListeners.add(pListener);
    }

    public void registerOnDirectionChangedListener(OnDirectionChangedListener pListener) {
        onDirectionChangedListeners.add(pListener);
    }

    public void registerOnStepLengthChangedListener(OnStepLengthChangedListener pListener) {
        onStepLengthChangedListeners.add(pListener);
    }

    public void registerOnResetListener(OnResetListener pListener) {
        onResetListeners.add(pListener);
    }


    public void registerAccelerometerEventListener(OnAccelerometerEventListener pListener) {
        onAccelerometerEventListeners.add(pListener);
    }

    public void registerGyroscopeEventListener(OnGyroscopeEventListener pListener) {
        onGyroscopeEventListeners.add(pListener);
    }

    public void registerMagneticFieldEventListener(OnMagneticFieldEventListener pListener) {
        onMagneticSensorEventListeners.add(pListener);
    }

    @Override
    public void onPathChanged(PathData pPathData) {
        for(OnPathChangedListener listener : onPathChangedListeners)
            listener.onPathChanged(pPathData);
    }

    @Override
    public void onStep(StepData pStepData) {
        for(OnStepListener listener : onStepListeners)
            listener.onStep(pStepData);
    }

    @Override
    public void onDirectionChanged(DirectionData pDirectionData) {
        for(OnDirectionChangedListener listener : onDirectionChangedListeners)
            listener.onDirectionChanged(pDirectionData);
    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {
        for(OnStepLengthChangedListener listener : onStepLengthChangedListeners)
            listener.onStepLengthChanged(pStepLengthData);
    }

    @Override
    public void onReset() {
        for(OnResetListener listener : onResetListeners)
            listener.onReset();
    }

    @Override
    public void onGyroscopeEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        for(OnGyroscopeEventListener listener : onGyroscopeEventListeners)
            listener.onGyroscopeEvent(pX, pY, pZ, pTimeStamp_ns, pAccuracy);
    }

    @Override
    public void onAccelerometerEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        for(OnAccelerometerEventListener listener : onAccelerometerEventListeners)
            listener.onAccelerometerEvent(pX, pY, pZ, pTimeStamp_ns, pAccuracy);
    }

    @Override
    public void onMagneticFieldEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        for(OnMagneticFieldEventListener listener : onMagneticSensorEventListeners)
            listener.onMagneticFieldEvent(pX, pY, pZ, pTimeStamp_ns, pAccuracy);
    }
}
