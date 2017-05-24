package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils;

import android.util.Log;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.AbstractSequentialList;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection.SensorEventCollection;

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
    private double peakValue;
    private long peakTimeNs;

    public Peak(PeakType pPeakType) {
        times = new LinkedList<>();
        values = new LinkedList<>();
        peakType = pPeakType;
        peakValue = 0.0;
        peakTimeNs = 0;

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

    public long getPeakTime() {
        return peakTimeNs;
    }

    public double getPeakValue() {
        return peakValue;
    }

    public void add(long pTime, double pValue) {
        times.add(pTime);
        values.add(pValue);
        boolean newExtreme = false;
        switch (peakType) {
            case UP_PEAK:
                    if (pValue > peakValue)
                        newExtreme = true;
                break;
            case DOWN_PEAK:
                    if (pValue < peakValue)
                        newExtreme = true;
                break;
        }
        if (newExtreme) {
            peakValue = pValue;
            peakTimeNs = pTime;
        }
    }
}
