package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollections;

import java.util.AbstractSequentialList;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;

/**
 * Created by Fabi on 16.05.2017.
 */

public class FixedTimeWindow implements SensorEventCollection {

    private LinkedList<SensorEvent> events;
    private long windowSizeNs;
    private long lowerResolutionBoundInNs;
    private long startTimeInNs;
    private boolean isFull;

    public FixedTimeWindow(long pWindowSizeNs, long pLowerResolutionBoundInNs) {
        events = new LinkedList<>();
        windowSizeNs = pWindowSizeNs;
        lowerResolutionBoundInNs = pLowerResolutionBoundInNs;
        startTimeInNs = -1;
        isFull = false;
    }

    public boolean isFull() {
        return isFull;
    }

    public void add(SensorEvent pSensorEvent) {
        if(isFull)
            return;
        if(events.isEmpty()) {
            startTimeInNs = pSensorEvent.timeNs;
            events.add(pSensorEvent);
            return;
        }
        if(pSensorEvent.timeNs-events.getLast().timeNs<lowerResolutionBoundInNs)
            return;
        if(pSensorEvent.timeNs-startTimeInNs>windowSizeNs) {
            isFull = true;
            return;
        }
        events.add(pSensorEvent);
    }


    @Override
    public AbstractSequentialList<SensorEvent> getSensorEvents() {
        return events;
    }
}
