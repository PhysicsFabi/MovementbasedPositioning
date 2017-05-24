package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnDownwardsVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnGyroscopeEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnMagneticFieldVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnOrientationChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnSensorAccuracyLowListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction.StepReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.magneticfield.MagneticFieldProcessor;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.orientation.OrientationData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.orientation.OrientationReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.path.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.path.PathReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.steplength.StepLengthReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;

/**
 * Created by Fabi on 02.05.2017.
 *
 * Distributes all the events (sensors, steps, ...) that happen during the program run.
 */

public class DeadReckoning implements
        OnPathChangedListener,
        OnStepListener,
        OnOrientationChangedListener,
        OnMagneticFieldVectorChangedListener,
        OnDownwardsVectorChangedListener,
        OnAccelerometerEventListener,
        OnGyroscopeEventListener,
        OnMagneticFieldEventListener,
        OnSensorAccuracyLowListener {

    private final LinkedList<OnPathChangedListener> onPathChangedListeners;
    private final LinkedList<OnStepListener> onStepListeners;
    private final LinkedList<OnOrientationChangedListener> onOrientationChangedListeners;
    private final LinkedList<OnMagneticFieldVectorChangedListener> onMagneticFieldVectorChangedListeners;
    private final LinkedList<OnDownwardsVectorChangedListener> onDownwardsVectorChangedListeners;
    private final LinkedList<OnSensorAccuracyLowListener> onSensorAccuracyLowListeners;

    private final LinkedList<OnAccelerometerEventListener> onAccelerometerEventListeners;
    private final LinkedList<OnGyroscopeEventListener> onGyroscopeEventListeners;
    private final LinkedList<OnMagneticFieldEventListener> onMagneticSensorEventListeners;



    private final StepReconstruction stepReconstruction;
    private final MagneticFieldProcessor magneticFieldProcessor;
    private final OrientationReconstruction orientationReconstruction;
    private final StepLengthReconstruction stepLengthReconstruction;
    private final PathReconstruction pathReconstruction;


    public DeadReckoning() {
        stepReconstruction = new StepReconstruction((long)(3*1e9), 0, this, this);
        magneticFieldProcessor = new MagneticFieldProcessor(this);
        orientationReconstruction = new OrientationReconstruction(this);
        stepLengthReconstruction = new StepLengthReconstruction();
        pathReconstruction = new PathReconstruction(this);

        onPathChangedListeners = new LinkedList<>();
        onStepListeners = new LinkedList<>();
        onOrientationChangedListeners = new LinkedList<>();
        onMagneticFieldVectorChangedListeners = new LinkedList<>();
        onDownwardsVectorChangedListeners = new LinkedList<>();
        onSensorAccuracyLowListeners = new LinkedList<>();


        onAccelerometerEventListeners = new LinkedList<>();
        onGyroscopeEventListeners = new LinkedList<>();
        onMagneticSensorEventListeners = new LinkedList<>();


        initEventDistribution();
    }


    private void initEventDistribution() {
        //mind the order, the registration order matches the notifying order

        // step reconstruction
        registerAccelerometerEventListener(stepReconstruction);
        registerOnOrientationChangedListener(stepReconstruction);

        // magnetic field
        registerMagneticFieldEventListener(magneticFieldProcessor);

        // orientation reconstruction
        registerOnMagneticFieldVectorChangedListener(orientationReconstruction);
        registerOnDownwardsVectorChangedListener(orientationReconstruction);


        //step length reconstruction
        registerOnStepListener(stepLengthReconstruction);

        // path reconstruction
        registerOnOrientationChangedListener(pathReconstruction);
        registerOnStepListener(pathReconstruction);
    }

    public void registerOnPathChangedListener(OnPathChangedListener pListener) {
        onPathChangedListeners.add(pListener);
    }

    public void registerOnMagneticFieldVectorChangedListener(OnMagneticFieldVectorChangedListener pListener) {
        onMagneticFieldVectorChangedListeners.add(pListener);
    }

    public void registerOnSensorAccuracyLowListener(OnSensorAccuracyLowListener pListener) {
        onSensorAccuracyLowListeners.add(pListener);
    }

    public void registerOnDownwardsVectorChangedListener(OnDownwardsVectorChangedListener pListener) {
        onDownwardsVectorChangedListeners.add(pListener);
    }

    public void registerOnStepListener(OnStepListener pListener) {
        onStepListeners.add(pListener);
    }

    public void registerOnOrientationChangedListener(OnOrientationChangedListener pListener) {
        onOrientationChangedListeners.add(pListener);
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
    public void onMagneticFieldVectorChanged(Vector3D pMagneticField_ph) {
        for(OnMagneticFieldVectorChangedListener listener : onMagneticFieldVectorChangedListeners)
            listener.onMagneticFieldVectorChanged(pMagneticField_ph);
    }

    @Override
    public void onPathChanged(PathData pPathData) {
        for(OnPathChangedListener listener : onPathChangedListeners)
            listener.onPathChanged(pPathData);
    }

    @Override
    public void onOrientationChanged(double pAngle) {
        for(OnPathChangedListener listener : onPathChangedListeners)
            listener.onOrientationChanged(pAngle);
    }

    @Override
    public void onStepSeriesChanged(LinkedList<StepData> pStepSeries) {
        for(OnStepListener listener : onStepListeners)
            listener.onStepSeriesChanged(pStepSeries);

        /*
        Log.i("STEP_DATA", "-----------------------------------------------------------");
        for(StepData stepData : pStepSeries) {
            Log.i("STEP_DATA", "\t" + stepData.startTimeNs + "\t" + stepData.endTimeNs + "\t" + stepData.stepType + "\t" + stepData.stepLengthM);
        }
        */
        /*
        if(pStepSeries.getLast().stepType==StepData.StepType.END_OF_SERIES || pStepSeries.getLast().stepType==StepData.StepType.BEGIN_AND_END_OF_SERIES) {
            for(StepData stepData : pStepSeries) {
                Log.i("STEP_DATA", "\t" + stepData.startTimeNs + "\t" + stepData.endTimeNs + "\t" + stepData.stepType + "\t" + stepData.stepLengthM);
            }
        }
        */
    }

    @Override
    public void onGyroscopeEvent(SensorEvent pSensorEvent) {
        for(OnGyroscopeEventListener listener : onGyroscopeEventListeners)
            listener.onGyroscopeEvent(pSensorEvent);
    }

    @Override
    public void onAccelerometerEvent(SensorEvent pSensorEvent) {
        for(OnAccelerometerEventListener listener : onAccelerometerEventListeners)
            listener.onAccelerometerEvent(pSensorEvent);
    }

    @Override
    public void onMagneticFieldEvent(SensorEvent pSensorEvent) {
        for(OnMagneticFieldEventListener listener : onMagneticSensorEventListeners)
            listener.onMagneticFieldEvent(pSensorEvent);
    }

    @Override
    public void onOrientationChanged(OrientationData pOrientationData) {

        for(OnOrientationChangedListener listener : onOrientationChangedListeners)
            listener.onOrientationChanged(pOrientationData);
    }

    @Override
    public void onDownwardsVectorChanged(Vector3D pDownwards_ph) {
        for(OnDownwardsVectorChangedListener listener : onDownwardsVectorChangedListeners)
            listener.onDownwardsVectorChanged(pDownwards_ph);
    }

    @Override
    public void onSensorAccuracyLow() {
        for(OnSensorAccuracyLowListener listener : onSensorAccuracyLowListeners)
            listener.onSensorAccuracyLow();
    }
}
