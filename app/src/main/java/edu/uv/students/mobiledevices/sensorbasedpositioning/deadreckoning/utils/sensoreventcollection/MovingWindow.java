package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.sensoreventcollection;

import android.util.Log;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;

/**
 * Created by Fabi on 15.05.2017.
 */

public class MovingWindow implements SensorEventCollection {

    private final LinkedList<SensorEvent> events;
    private final long windowSizeNs;
    private final long lowerResolutionBoundInNs;
    private final float[] movingMeans;

    public MovingWindow(long pWindowSizeNs, long pLowerResolutionBoundInNs, int pValuesCount) {
        events = new LinkedList<>();
        windowSizeNs = pWindowSizeNs;
        lowerResolutionBoundInNs = pLowerResolutionBoundInNs;
        movingMeans = new float[pValuesCount];
    }

    public MovingWindow(long pWindowSizeNs, long pLowerResolutionBoundInNs) {
        this(pWindowSizeNs,pLowerResolutionBoundInNs,3);
    }

    public float[] getMovingMeans() {
        return movingMeans;
    }

    private void removeFromSlidingMean(SensorEvent pEvent) {
        for(int i = 0; i< movingMeans.length; ++i) {
            movingMeans[i]=((movingMeans[i]*events.size())-pEvent.values[i])/(events.size()-1);
        }
    }

    private void addToSlidingMean(SensorEvent pEvent) {
        for(int i = 0; i< movingMeans.length; ++i) {
            movingMeans[i]=((movingMeans[i]*events.size())+pEvent.values[i])/(events.size()+1);
        }
    }

    private void addNewEntry(SensorEvent pSensorEvent) {
        addToSlidingMean(pSensorEvent);
        events.add(pSensorEvent);
    }

    public void removeOldEntries(long pTimeNs) {
        long earliestWindowTimeNs = pTimeNs-windowSizeNs;
        Iterator<SensorEvent> iter = events.iterator();
        while(iter.hasNext() && iter.next().timeNs<earliestWindowTimeNs) {
            SensorEvent sensorEvent = iter.next();
            if(sensorEvent.timeNs<earliestWindowTimeNs) {
                removeFromSlidingMean(sensorEvent);
                iter.remove();
            } else {
                break;
            }
        }
    }

    public boolean add(SensorEvent pSensorEvent) {
        if(!events.isEmpty() && pSensorEvent.timeNs-events.getLast().timeNs<lowerResolutionBoundInNs)
            return false;
        removeOldEntries(pSensorEvent.timeNs);
        addNewEntry(pSensorEvent);
        Log.i("ACCELEROMETER_DATA","\t"+pSensorEvent.timeNs+"\t"+pSensorEvent.values[0]+"\t"+pSensorEvent.values[1]+"\t"+pSensorEvent.values[2]);
        Log.i("MOVING_MEAN_DATA","\t"+pSensorEvent.timeNs+"\t"+movingMeans[0]+"\t"+movingMeans[1]+"\t"+movingMeans[2]);
        return true;
    }

    @Override
    public AbstractSequentialList<SensorEvent> getSensorEvents() {
        return events;
    }
}
