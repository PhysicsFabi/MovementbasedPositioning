package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection;

import android.util.Log;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.PositionReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.step.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;

/**
 * Created by Fabi on 20.05.2017.
 */

public class AccelerometerProcessor extends SlidingWindow implements OnAccelerometerEventListener {

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

    private class DivisionByPeak {

        private final double THRESHOLD_UP = 1.0;
        private final double THRESHOLD_DOWN = -1.9;

        private LinkedList<SensorEvent> events;
        private PeakType peakType;
        private double extremeAcceleration;
        private long peakTimeNs;

        public DivisionByPeak(PeakType pPeakType) {
            events = new LinkedList<>();
            peakType = pPeakType;
            extremeAcceleration = 0.0;
            peakTimeNs = 0;

        }

        public boolean add(SensorEvent pEvent) {
            Vector3D currentAcceleration_ph = new Vector3D(pEvent.values[0], pEvent.values[1], pEvent.values[2]).subtract(gravityAcceleration_ph);
            double currentDownwardsAcceleration = downwardsNormalized_ph.dotProduct(currentAcceleration_ph);
            //Log.i("DOWNWARDS EVALUATION", "\t"+pEvent.timeNs+"\t"+currentDownwardsAcceleration);
            boolean add = false;
            boolean newExtreme = false;
            switch (peakType) {
                case UP_PEAK:
                    if(currentDownwardsAcceleration>THRESHOLD_DOWN) {
                        add = true;
                        if(currentDownwardsAcceleration>extremeAcceleration)
                            newExtreme = true;
                    }
                    break;
                case DOWN_PEAK:
                    if(currentDownwardsAcceleration<THRESHOLD_UP) {
                        add = true;
                        if(currentDownwardsAcceleration<extremeAcceleration)
                            newExtreme = true;
                    }
                    break;
            }
            if(newExtreme) {
                extremeAcceleration = currentDownwardsAcceleration;
                peakTimeNs = pEvent.timeNs;
            }
            if(add) {
                events.add(pEvent);
            }
            return add;
        }

        long getDurationNs() {
            if(events.isEmpty())
                return 0;
            return events.getLast().timeNs-events.getFirst().timeNs;
        }
    }


    public final long MAX_STEP_TIME_NS = (long)0.9e9;
    public final long MIN_STEP_TIME_NS = (long)0.4e9;

    private Vector3D downwardsNormalized_ph;
    private Vector3D gravityAcceleration_ph;
    private DivisionByPeak currentDivision;
    private DivisionByPeak lastDivision;

    private PositionReconstruction positionReconstruction;


    public AccelerometerProcessor(long pWindowSizeNs, long pLowerResolutionBoundInNs, int pValuesCount, PositionReconstruction pPositionReconstruction) {
        super(pWindowSizeNs, pLowerResolutionBoundInNs, pValuesCount);
        positionReconstruction = pPositionReconstruction;
        currentDivision = new DivisionByPeak(PeakType.DOWN_PEAK);
        lastDivision = null;
    }

    public AccelerometerProcessor(long pWindowSizeNs, long pLowerResolutionBoundInNs, PositionReconstruction pPositionReconstruction) {
        this(pWindowSizeNs, pLowerResolutionBoundInNs, 3, pPositionReconstruction);
    }

    private boolean isStepInBuffer() {
        if(lastDivision==null)
            return false;
        long stepCandidateDurationNs = currentDivision.getDurationNs()+lastDivision.getDurationNs();
        return stepCandidateDurationNs > MIN_STEP_TIME_NS && stepCandidateDurationNs < MAX_STEP_TIME_NS;
    }


    @Override
    public void onAccelerometerEvent(SensorEvent pSensorEvent) {
        super.add(pSensorEvent);
        float[] movingMeans = getMovingMeans();
        gravityAcceleration_ph = new Vector3D(movingMeans[0], movingMeans[1], movingMeans[2]);
        downwardsNormalized_ph = gravityAcceleration_ph.scalarMultiply(-1.0).normalize();
        if(!currentDivision.add(pSensorEvent)) {
            if(currentDivision.peakType==PeakType.DOWN_PEAK && isStepInBuffer()) {
                StepData stepData = new StepData();
                stepData.directionNormalized_world = new Vector2D(0, 1);
                stepData.timeNs = currentDivision.peakTimeNs;
                //Log.i("STEP EVALUATION", "\t" + pSensorEvent.timeNs);
                positionReconstruction.onStep(stepData);
            }
            lastDivision = currentDivision;
            currentDivision = new DivisionByPeak(currentDivision.peakType.invert());
        }
    }



}
