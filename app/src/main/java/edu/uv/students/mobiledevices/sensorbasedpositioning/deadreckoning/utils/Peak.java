package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils;

import java.util.LinkedList;

/**
 * Created by Fabi on 24.05.2017.
 */

public class Peak {

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

    private final LinkedList<Long> times;
    private final LinkedList<Double> values;
    private final PeakType peakType;
    private double minValue;
    private long minValueTimeNs;
    private double maxValue;
    private long maxValueTimeNs;

    public Peak(PeakType pPeakType) {
        times = new LinkedList<>();
        values = new LinkedList<>();
        peakType = pPeakType;
        minValue = maxValue = 0.0;
        minValueTimeNs = maxValueTimeNs = 0;
    }

    public PeakType getPeakType() {
        return peakType;
    }

    public long getDurationNs() {
        if (times.isEmpty())
            return 0;
        return times.getLast() - times.getFirst();
    }

    public long getStartTimeNs() {
        return times.getFirst();
    }

    public long getEndTimeNs() {
        return times.getLast();
    }

    public long getMinValueTimeNs() {
        return minValueTimeNs;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public long getMaxValueTimeNs() {
        return maxValueTimeNs;
    }

    public void add(long pTime, double pValue) {
        times.add(pTime);
        values.add(pValue);
        if(pValue<minValue) {
            minValue = pValue;
            minValueTimeNs = pTime;
        } else if(pValue>maxValue) {
            maxValue = pValue;
            maxValueTimeNs = pTime;
        }
    }
}
