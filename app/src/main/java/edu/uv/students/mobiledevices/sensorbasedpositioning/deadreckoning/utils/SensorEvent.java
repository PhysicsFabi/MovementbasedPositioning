package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils;

/**
 * Created by Fabi on 16.05.2017.
 */

public class SensorEvent {
    public final long timeNs;
    public final float[] values;
    public final int accuracy;

    public SensorEvent(long pTimeNs, float[] pValues, int pAccuracy) {
        timeNs = pTimeNs;
        values = pValues;
        accuracy = pAccuracy;
    }
}
