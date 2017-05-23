package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils;

import java.util.AbstractSequentialList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollection.SensorEventCollection;

/**
 * Created by Fabi on 16.05.2017.
 */

public class SensorEventsProcessingTools {
    public static float[] getValueMeans(SensorEventCollection pSensorEvents) {
        return getValueMeans(pSensorEvents.getSensorEvents());
    }

    public static float[] getValueMeans(AbstractSequentialList<SensorEvent> events) {
        if(events.isEmpty())
            return null;
        float[] means = new float[events.get(0).values.length];
        for(SensorEvent event : events) {
            for(int i=0;i<means.length;++i) {
                means[i]+=event.values[i];
            }
        }
        for(int i=0;i<means.length;++i) {
            means[i]/=events.size();
        }
        return means;
    }

    public static AbstractSequentialList<SensorEvent> lowPassFilter(SensorEventCollection pSensorEvents) {
        return lowPassFilter(pSensorEvents.getSensorEvents());
    }

    public static AbstractSequentialList<SensorEvent> lowPassFilter(AbstractSequentialList<SensorEvent> pSensorEvents) {
        //TODO implement low pass filter;
        return pSensorEvents;
    }


}
