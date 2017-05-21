package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection;

import java.util.AbstractSequentialList;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;

/**
 * Created by Fabi on 20.05.2017.
 */

public class PreprocessedSlidingWindow extends SlidingWindow {

    public enum PeakType {
        NONE,
        UP_PEAK,
        DOWN_PEAK
    }

    LinkedList<SensorEvent> currentPeak;
    PeakType currentPeakType;
    double threshold;


    public PreprocessedSlidingWindow(long pWindowSizeNs, long pLowerResolutionBoundInNs, int pValuesCount) {
        super(pWindowSizeNs, pLowerResolutionBoundInNs, pValuesCount);
    }

    public PreprocessedSlidingWindow(long pWindowSizeNs, long pLowerResolutionBoundInNs) {
        super(pWindowSizeNs, pLowerResolutionBoundInNs);
    }

    @Override
    public boolean add(SensorEvent pSensorEvent) {
        boolean res = super.add(pSensorEvent);


        return res;
    }
}
