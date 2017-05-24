package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.sensoreventcollection;
import java.util.AbstractSequentialList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;

/**
 * Created by Fabi on 16.05.2017.
 */

public interface SensorEventCollection {
    AbstractSequentialList<SensorEvent> getSensorEvents();
}
