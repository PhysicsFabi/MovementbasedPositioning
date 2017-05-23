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
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEventsProcessingTools;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection.SensorEventCollection;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection.SlidingWindow;

/**
 * Created by Fabi on 20.05.2017.
 */

public class AccelerometerProcessor implements OnAccelerometerEventListener {

    public enum PeakType {
        UP_PEAK,
        DOWN_PEAK;

        public PeakType invert() {
            switch (this) {
                case UP_PEAK:
                    return DOWN_PEAK;
                case DOWN_PEAK:
                    return UP_PEAK;
            }
            return DOWN_PEAK;
        }
    }

    private class DivisionByPeak implements SensorEventCollection {

        private final double THRESHOLD_UP = 1.0;
        private final double THRESHOLD_DOWN = -1.9;

        private final LinkedList<SensorEvent> events;
        private final PeakType peakType;
        private double extremeAcceleration;
        private long peakTimeNs;

        public DivisionByPeak(PeakType pPeakType) {
            events = new LinkedList<>();
            peakType = pPeakType;
            extremeAcceleration = 0.0;
            peakTimeNs = 0;

        }

        public long getStartTimeNs() {
            return events.getFirst().timeNs;
        }

        public long getEndTimeNs() {
            return events.getLast().timeNs;
        }

        public boolean add(SensorEvent pEvent) {
            Vector3D currentAcceleration_ph = new Vector3D(pEvent.values[0], pEvent.values[1], pEvent.values[2]).subtract(gravityAcceleration_ph);
            double currentDownwardsAcceleration = downwardsNormalized_ph.dotProduct(currentAcceleration_ph);
            //Log.i("DOWNWARDS EVALUATION", "\t"+pEvent.footDownTimeNs+"\t"+currentDownwardsAcceleration);
            boolean add = false;
            boolean newExtreme = false;
            switch (peakType) {
                case UP_PEAK:
                    if (currentDownwardsAcceleration > THRESHOLD_DOWN) {
                        add = true;
                        if (currentDownwardsAcceleration > extremeAcceleration)
                            newExtreme = true;
                    }
                    break;
                case DOWN_PEAK:
                    if (currentDownwardsAcceleration < THRESHOLD_UP) {
                        add = true;
                        if (currentDownwardsAcceleration < extremeAcceleration)
                            newExtreme = true;
                    }
                    break;
            }
            if (newExtreme) {
                extremeAcceleration = currentDownwardsAcceleration;
                peakTimeNs = pEvent.timeNs;
            }
            if (add) {
                events.add(pEvent);
            }
            return add;
        }

        long getDurationNs() {
            if (events.isEmpty())
                return 0;
            return events.getLast().timeNs - events.getFirst().timeNs;
        }

        @Override
        public AbstractSequentialList<SensorEvent> getSensorEvents() {
            return events;
        }
    }


    public final long MAX_STEP_TIME_NS = (long) 0.9e9;
    public final long MIN_STEP_TIME_NS = (long) 0.4e9;
    public final long DIRECTION_DETECTION_TIME_AFTER_FOOT_DOWN_NS = (long)0.1e9;

    private Vector3D downwardsNormalized_ph;
    private Vector3D gravityAcceleration_ph;
    private DivisionByPeak currentDivision;
    private DivisionByPeak previousDivision;
    private DivisionByPeak prepreviousDivision;

    private final OnStepListener onStepListener;
    private final OnDownwardsVectorChangedListener onDownwardsVectorChangedListener;

    private final SlidingWindow events;


    public AccelerometerProcessor(long pWindowSizeNs, long pLowerResolutionBoundInNs, OnStepListener pOnStepListener, OnDownwardsVectorChangedListener pOnDownwardsVectorChangedListener) {
        onStepListener = pOnStepListener;
        onDownwardsVectorChangedListener = pOnDownwardsVectorChangedListener;
        currentDivision = new DivisionByPeak(PeakType.DOWN_PEAK);
        previousDivision = null;
        prepreviousDivision = null;
        events = new SlidingWindow(pWindowSizeNs, pLowerResolutionBoundInNs, 3);
    }

    private boolean isStepInBuffer() {
        if (previousDivision == null)
            return false;
        long stepCandidateDurationNs = currentDivision.getDurationNs() + previousDivision.getDurationNs();
        return stepCandidateDurationNs > MIN_STEP_TIME_NS && stepCandidateDurationNs < MAX_STEP_TIME_NS;
    }


    @Override
    public void onAccelerometerEvent(SensorEvent pSensorEvent) {
        events.add(pSensorEvent);
        float[] movingMeans = events.getMovingMeans();
        gravityAcceleration_ph = new Vector3D(movingMeans[0], movingMeans[1], movingMeans[2]);
        downwardsNormalized_ph = gravityAcceleration_ph.scalarMultiply(-1.0).normalize();
        onDownwardsVectorChangedListener.onDownwardsVectorChanged(downwardsNormalized_ph);


        //Vector3D horizontalAcceleration = new Vector3D(pSensorEvent.values[0], pSensorEvent.values[1], pSensorEvent.values[2]).subtract(gravityAcceleration_ph);
        //horizontalAcceleration = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, horizontalAcceleration);
        //Log.i("HORIZONTAL ACCELERATION", "\t"+pSensorEvent.timeNs+"\t"+horizontalAcceleration.getX()+"\t"+horizontalAcceleration.getY()+"\t"+horizontalAcceleration.getZ());
        //Log.i("DOWNWARDS ACCELERATION", "\t"+pSensorEvent.timeNs+"\t"+downwardsNormalized_ph);


        if (!currentDivision.add(pSensorEvent)) {
            if (currentDivision.peakType == PeakType.DOWN_PEAK && isStepInBuffer()) {
                StepData stepData = new StepData();
                stepData.footDownTimeNs = prepreviousDivision.peakTimeNs;
                /*
                Vector3D horizontalVelocity_ph0 =
                        integrateHorizontalAcceleration(
                                previousDivision.events,
                                prepreviousDivision.getStartTimeNs(),
                                prepreviousDivision.getEndTimeNs());
                Vector3D horizontalVelocity_ph1=
                        integrateHorizontalAcceleration(
                                currentDivision.events,
                                currentDivision.getStartTimeNs(),
                                currentDivision.peakTimeNs);
                Vector3D horizontalVelocity_ph = horizontalVelocity_ph0.add(horizontalVelocity_ph1);

                stepData.horizontalDirectionNormalized_ph = horizontalVelocity_ph.getNorm()==0 ? horizontalVelocity_ph : horizontalVelocity_ph.normalize();
                */
                /*
                float[] means = SensorEventsProcessingTools.getValueMeans(currentDivision);
                Vector3D horizontalAcceleration = new Vector3D(means[0], means[1], means[2]);
                stepData.horizontalDirectionNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, horizontalAcceleration).normalize();
                */
                stepData.horizontalDirectionNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, new Vector3D(.0,1.0,.0)).normalize();
                //Log.i("STEP EVALUATION", "\t" + prepreviousDivision.peakTimeNs);
                onStepListener.onStep(stepData);
            }
            prepreviousDivision = previousDivision;
            previousDivision = currentDivision;
            currentDivision = new DivisionByPeak(currentDivision.peakType.invert());
        }
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
