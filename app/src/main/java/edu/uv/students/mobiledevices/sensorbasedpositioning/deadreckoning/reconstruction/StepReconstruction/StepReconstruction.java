package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnDownwardsVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnOrientationChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.orientation.OrientationData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.LinearAlgebraTools;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.Peak;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.sensoreventcollection.MovingWindow;

import static edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.Peak.PeakType.DOWN_PEAK;
import static edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.Peak.PeakType.UP_PEAK;

/**
 * Created by Fabi on 20.05.2017.
 */

public class StepReconstruction implements OnAccelerometerEventListener, OnOrientationChangedListener {

    public static final long MAX_STEP_TIME_NS = (long) 1.2e9;
    public static final long MAX_PEAK_TIME_FOR_STEP_SERIES_TERMINATION_NS = MAX_STEP_TIME_NS;
    public static final long MIN_UP_PEAK_TIME_NS = (long) 0.28e9;
    public static final long MIN_DOWN_PEAK_TIME_NS = (long) 0.145e9;
    public static final double THRESHOLD_UP_PEAK = 1.0;
    public static final double THRESHOLD_DOWN_PEAK = -1.9;
    public static final long DEFAULT_FIRST_STEP_TIME_NS = (long)0.7e9;

    LinkedList<LinkedList<StepData>> stepSeries;
    LinkedList<StepData> currentStepSeries;

    private Vector3D downwardsNormalized_ph;
    private Vector3D gravityAcceleration_ph;
    private Vector3D horizontalDirectionNormalized_ph;
    private Peak currentPeak;
    private Peak previousPeak;
    private Peak prepreviousPeak;
    boolean isCurrentStepSeriesTerminated;

    private final OnStepListener onStepListener;
    private final OnDownwardsVectorChangedListener onDownwardsVectorChangedListener;

    private final MovingWindow events;
    private OrientationData currentOrientationData;


    public StepReconstruction(long pWindowSizeNs, long pLowerResolutionBoundInNs, OnStepListener pOnStepListener, OnDownwardsVectorChangedListener pOnDownwardsVectorChangedListener) {
        onStepListener = pOnStepListener;
        onDownwardsVectorChangedListener = pOnDownwardsVectorChangedListener;
        currentPeak = new Peak(UP_PEAK);
        previousPeak = null;
        prepreviousPeak = null;
        events = new MovingWindow(pWindowSizeNs, pLowerResolutionBoundInNs, 3);
        stepSeries = new LinkedList<>();
        isCurrentStepSeriesTerminated = true;
    }

    @Override
    public void onAccelerometerEvent(SensorEvent pSensorEvent) {
        events.add(pSensorEvent);
        extractGravity();
        Vector3D currentAcceleration_ph = new Vector3D(pSensorEvent.values[0], pSensorEvent.values[1], pSensorEvent.values[2]).subtract(gravityAcceleration_ph);
        double currentDownwardsAcceleration = downwardsNormalized_ph.dotProduct(currentAcceleration_ph);
        //Log.i("DOWNWARDS_ACCELERATION", "\t"+pSensorEvent.timeNs+"\t"+currentDownwardsAcceleration);
        if (isPeakChange(currentDownwardsAcceleration)) {
            if(currentPeak.getPeakType() == DOWN_PEAK) {
                horizontalDirectionNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, new Vector3D(.0, 1.0, .0)).normalize();
                if (isCurrentStepSeriesTerminated) {
                    beginCurrentStepSeries();
                } else {
                    addToCurrentStepSeries();
                }
            }
            addNewPeak(currentPeak.getPeakType().invert());
        } else if(shouldTerminateCurrentPeak()) {
            terminateCurrentStepSeries();
            addNewPeak(UP_PEAK);
        }
        currentPeak.add(pSensorEvent.timeNs, currentDownwardsAcceleration);
    }

    private boolean shouldTerminateCurrentPeak() {
        return !isCurrentStepSeriesTerminated && currentPeak.getDurationNs() > MAX_PEAK_TIME_FOR_STEP_SERIES_TERMINATION_NS;
    }

    private void beginCurrentStepSeries() {
        StepData stepData = new StepData(StepData.StepType.BEGIN_OF_SERIES);
        stepData.endTimeNs = currentPeak.getMinValueTimeNs();
        stepData.startTimeNs = stepData.endTimeNs - DEFAULT_FIRST_STEP_TIME_NS;
        stepData.horizontalDirectionNormalized_ph = horizontalDirectionNormalized_ph;
        stepData.horizontalDirectionNormalized_world = currentOrientationData.transformToWorld(horizontalDirectionNormalized_ph);
        isCurrentStepSeriesTerminated = false;
        currentStepSeries = new LinkedList<>();
        currentStepSeries.add(stepData);
        stepSeries.add(currentStepSeries);
        onStepListener.onStepSeriesChanged(currentStepSeries);
    }

    private void addToCurrentStepSeries() {
        StepData stepData = new StepData(StepData.StepType.MIDDLE_OF_SERIES);
        stepData.startTimeNs = prepreviousPeak.getMinValueTimeNs();
        stepData.endTimeNs = currentPeak.getMinValueTimeNs();
        stepData.horizontalDirectionNormalized_ph = horizontalDirectionNormalized_ph;
        stepData.horizontalDirectionNormalized_world = currentOrientationData.transformToWorld(horizontalDirectionNormalized_ph);
        StepData previousStep = currentStepSeries.getLast();
        if(previousStep.stepType == StepData.StepType.BEGIN_OF_SERIES) {
            previousStep.startTimeNs = previousStep.endTimeNs - stepData.getDurationNs();
        }
        currentStepSeries.add(stepData);
        onStepListener.onStepSeriesChanged(currentStepSeries);
    }

    private void terminateCurrentStepSeries() {
        if(currentPeak.getPeakType() == DOWN_PEAK && prepreviousPeak!=null) {
            horizontalDirectionNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, new Vector3D(.0, 1.0, .0)).normalize();
            StepData stepData = new StepData(StepData.StepType.END_OF_SERIES);
            stepData.startTimeNs = prepreviousPeak.getMinValueTimeNs();
            stepData.endTimeNs = currentPeak.getMinValueTimeNs();
            stepData.horizontalDirectionNormalized_ph = horizontalDirectionNormalized_ph;
            stepData.horizontalDirectionNormalized_world = currentOrientationData.transformToWorld(horizontalDirectionNormalized_ph);
            currentStepSeries.add(stepData);
        } else {
            currentStepSeries.getLast().stepType = currentStepSeries.size() == 1 ?
                    StepData.StepType.BEGIN_AND_END_OF_SERIES : StepData.StepType.END_OF_SERIES;

        }
        isCurrentStepSeriesTerminated = true;
        onStepListener.onStepSeriesChanged(currentStepSeries);
    }

    private void addNewPeak(Peak.PeakType pPeakType) {
        //Log.i("PEAK_START_TIME", "\t" + currentPeak.getStartTimeNs());
        prepreviousPeak = previousPeak;
        previousPeak = currentPeak;
        currentPeak = new Peak(pPeakType);
    }

    private boolean isPeakChange(double currentDownwardsAcceleration) {
        switch (currentPeak.getPeakType()) {
            case UP_PEAK:
                if (currentDownwardsAcceleration < THRESHOLD_DOWN_PEAK && (currentPeak.getDurationNs()>MIN_UP_PEAK_TIME_NS || previousPeak==null)) {
                    return true;
                }
                break;
            case DOWN_PEAK:
                if (currentDownwardsAcceleration > THRESHOLD_UP_PEAK && currentPeak.getDurationNs()>MIN_DOWN_PEAK_TIME_NS) {
                    return true;
                }
                break;
        }
        return false;
    }

    private void extractGravity() {
        float[] movingMeans = events.getMovingMeans();
        gravityAcceleration_ph = new Vector3D(movingMeans[0], movingMeans[1], movingMeans[2]);
        downwardsNormalized_ph = gravityAcceleration_ph.scalarMultiply(-1.0).normalize();
        onDownwardsVectorChangedListener.onDownwardsVectorChanged(downwardsNormalized_ph);
    }

    @Override
    public void onOrientationChanged(OrientationData pOrientationData) {
        currentOrientationData = pOrientationData;
    }
}
