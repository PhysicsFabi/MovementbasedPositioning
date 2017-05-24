package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.accelerometer;

import android.util.Log;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDownwardsVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.LinearAlgebraTools;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.Peak;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection.SlidingWindow;

import static edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.Peak.PeakType.DOWN_PEAK;
import static edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.Peak.PeakType.UP_PEAK;

/**
 * Created by Fabi on 20.05.2017.
 */

public class AccelerometerProcessor implements OnAccelerometerEventListener {

    public final long MAX_STEP_TIME_NS = (long) 0.8e9;
    public final long MIN_STEP_TIME_NS = (long) 0.38e9;
    public final long MIN_UP_PEAK_TIME_NS = (long) 0.28e9;
    public final long MIN_DOWN_PEAK_TIME_NS = (long) 0.145e9;
    private final double THRESHOLD_UP_PEAK = 1.0;
    private final double THRESHOLD_DOWN_PEAK = -1.9;


    private Vector3D downwardsNormalized_ph;
    private Vector3D gravityAcceleration_ph;
    private Peak currentPeak;
    private Peak previousPeak;
    private Peak prepreviousPeak;

    private final OnStepListener onStepListener;
    private final OnDownwardsVectorChangedListener onDownwardsVectorChangedListener;

    private final SlidingWindow events;


    public AccelerometerProcessor(long pWindowSizeNs, long pLowerResolutionBoundInNs, OnStepListener pOnStepListener, OnDownwardsVectorChangedListener pOnDownwardsVectorChangedListener) {
        onStepListener = pOnStepListener;
        onDownwardsVectorChangedListener = pOnDownwardsVectorChangedListener;
        currentPeak = new Peak(UP_PEAK);
        previousPeak = null;
        prepreviousPeak = null;
        events = new SlidingWindow(pWindowSizeNs, pLowerResolutionBoundInNs, 3);
    }

    private boolean isStep(StepData pStepCandidateData) {
        long stepDurationNs = pStepCandidateData.getDurationNs();
        return stepDurationNs > MIN_STEP_TIME_NS && stepDurationNs < MAX_STEP_TIME_NS;
    }


    @Override
    public void onAccelerometerEvent(SensorEvent pSensorEvent) {
        events.add(pSensorEvent);
        extractGravity();
        Vector3D currentAcceleration_ph = new Vector3D(pSensorEvent.values[0], pSensorEvent.values[1], pSensorEvent.values[2]).subtract(gravityAcceleration_ph);
        double currentDownwardsAcceleration = downwardsNormalized_ph.dotProduct(currentAcceleration_ph);
        Log.i("DOWNWARDS_ACCELERATION", "\t"+pSensorEvent.timeNs+"\t"+currentDownwardsAcceleration);
        if (isPeakChange(currentDownwardsAcceleration)) {
            if (currentPeak.getPeakType() == DOWN_PEAK) {
                if(prepreviousPeak!=null) {
                    StepData stepCandidateData = new StepData();
                    stepCandidateData.startTimeNs = prepreviousPeak.getPeakTime();
                    stepCandidateData.endTimeNs = currentPeak.getPeakTime();
                    //Log.i("STEP_DURATION", "\t" + ((double)stepCandidateData.durationNs/1e9));
                    if(isStep(stepCandidateData)) {
                        stepCandidateData.horizontalDirectionNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, new Vector3D(.0, 1.0, .0)).normalize();
                        Log.i("STEP_START_TIME", "\t" + stepCandidateData.startTimeNs);
                        onStepListener.onStep(stepCandidateData);
                    }
                }
            }
            prepreviousPeak = previousPeak;
            previousPeak = currentPeak;
            currentPeak = new Peak(currentPeak.getPeakType().invert());
            Log.i("PEAK_START_TIME", "\t" + pSensorEvent.timeNs);
        }
        currentPeak.add(pSensorEvent.timeNs, currentDownwardsAcceleration);
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

    private Vector3D integrateHorizontalAcceleration(AbstractList<SensorEvent> pSensorEvents, long fromTimeNs, long toTimeNs) {
        Vector3D velocity = new Vector3D(.0,.0,.0);
        if(pSensorEvents.isEmpty())
            return velocity;
        Iterator<SensorEvent> iter = pSensorEvents.iterator();
        SensorEvent prevEvent = iter.next();
        while(iter.hasNext()) {
            SensorEvent nextEvent = iter.next();
            if(nextEvent.timeNs>toTimeNs)
                break;
            if(prevEvent.timeNs>=fromTimeNs) {
                double durationS = ((double)(nextEvent.timeNs - prevEvent.timeNs)) / 1e9;
                Vector3D horizontalAcceleration = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, new Vector3D(prevEvent.values[0], prevEvent.values[1], prevEvent.values[2]));
                velocity = velocity.add(horizontalAcceleration.scalarMultiply(durationS));
            }
            prevEvent = nextEvent;
        }
        return velocity;
    }
}
