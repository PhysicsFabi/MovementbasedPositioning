package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollections;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;

/**
 * Created by Fabi on 15.05.2017.
 */

public class SlidingWindow implements SensorEventCollection {

    private LinkedList<SensorEvent> events;
    private long windowSizeNs;
    private long lowerResolutionBoundInNs;

    public SlidingWindow(long pWindowSizeNs, long pLowerResolutionBoundInNs) {
        events = new LinkedList<>();
        windowSizeNs = pWindowSizeNs;
        lowerResolutionBoundInNs = pLowerResolutionBoundInNs;
    }

    public void removeOldEntries(long pTimeNs) {
        long earliestWindowTimeNs = pTimeNs-windowSizeNs;
        Iterator<SensorEvent> iter = events.iterator();
        while(iter.hasNext() && iter.next().timeNs<earliestWindowTimeNs) {
            iter.remove();
        }
    }

    public boolean add(SensorEvent pSensorEvent) {
        if(!events.isEmpty() && pSensorEvent.timeNs-events.getLast().timeNs<lowerResolutionBoundInNs)
            return false;
        removeOldEntries(pSensorEvent.timeNs);
        events.add(pSensorEvent);
        return true;
    }

    @Override
    public AbstractSequentialList<SensorEvent> getSensorEvents() {
        return events;
    }
}
