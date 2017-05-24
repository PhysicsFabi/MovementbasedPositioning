package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.sensoreventcollection;

import java.util.AbstractSequentialList;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;

/**
 * Created by Fabi on 16.05.2017.
 */

public class FixedTimeWindow implements SensorEventCollection {

    private final LinkedList<SensorEvent> events;
    private final long windowSizeNs;
    private final long lowerResolutionBoundInNs;
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
