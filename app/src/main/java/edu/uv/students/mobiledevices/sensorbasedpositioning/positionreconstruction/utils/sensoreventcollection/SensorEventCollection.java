package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.sensoreventcollections;
import java.util.AbstractSequentialList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;

/**
 * Created by Fabi on 16.05.2017.
 */

public interface SensorEventCollection {
    AbstractSequentialList<SensorEvent> getSensorEvents();
}
