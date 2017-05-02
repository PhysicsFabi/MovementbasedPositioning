package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.DirectionData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.StepLengthData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnGyroscopeEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepLengthChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 *
 * Distributes all the events (sensors, steps, ...) that happen during the program run.
 */

public class EventDistributor implements
        OnPathChangedListener,
        OnStepListener,
        OnDirectionChangedListener,
        OnStepLengthChangedListener,
        SensorEventListener {

    private LinkedList<OnPathChangedListener> onPathChangedListeners;
    private LinkedList<OnStepListener> onStepListeners;
    private LinkedList<OnDirectionChangedListener> onDirectionChangedListeners;
    private LinkedList<OnStepLengthChangedListener> onStepLengthChangedListeners;

    private LinkedList<OnAccelerometerEventListener> onAccelerometerEventListeners;
    private LinkedList<OnGyroscopeEventListener> onGyroscopeEventListeners;
    private LinkedList<OnMagneticFieldEventListener> onMagneticSensorEventListeners;

    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magneticSensor;


    public EventDistributor(SensorManager pSensorManager) {
        onPathChangedListeners = new LinkedList<>();
        onStepListeners = new LinkedList<>();
        onDirectionChangedListeners = new LinkedList<>();
        onStepLengthChangedListeners = new LinkedList<>();

        onAccelerometerEventListeners = new LinkedList<>();
        onGyroscopeEventListeners = new LinkedList<>();
        onMagneticSensorEventListeners = new LinkedList<>();

        accelerometer = pSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = pSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magneticSensor = pSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        pSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        pSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        pSensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void registerOnPathChangedListener(OnPathChangedListener pListener) {
        onPathChangedListeners.add(pListener);
    }

    public void registerOnStepListener(OnStepListener pListener) {
        onStepListeners.add(pListener);
    }

    public void registerOnDirectionChangedListener(OnDirectionChangedListener pListener) {
        onDirectionChangedListeners.add(pListener);
    }

    public void registerOnStepLengthChangedListener(OnStepLengthChangedListener pListener) {
        onStepLengthChangedListeners.add(pListener);
    }

    public void registerAccelerometerEventListener(OnAccelerometerEventListener pListener) {
        onAccelerometerEventListeners.add(pListener);
    }

    public void registerGyroscopeEventListener(OnGyroscopeEventListener pListener) {
        onGyroscopeEventListeners.add(pListener);
    }

    public void registerMagneticFieldEventListener(OnMagneticFieldEventListener pListener) {
        onMagneticSensorEventListeners.add(pListener);
    }

    @Override
    public void onPathChanged(PathData pPathData) {
        for(OnPathChangedListener listener : onPathChangedListeners)
            listener.onPathChanged(pPathData);
    }

    @Override
    public void onStep(StepData pStepData) {
        for(OnStepListener listener : onStepListeners)
            listener.onStep(pStepData);
    }

    @Override
    public void onDirectionChanged(DirectionData pDirectionData) {
        for(OnDirectionChangedListener listener : onDirectionChangedListeners)
            listener.onDirectionChanged(pDirectionData);
    }

    @Override
    public void onStepLengthChanged(StepLengthData pStepLengthData) {
        for(OnStepLengthChangedListener listener : onStepLengthChangedListeners)
            listener.onStepLengthChanged(pStepLengthData);
    }

    @Override
    public void onSensorChanged(SensorEvent pEvent) {
        if(pEvent.sensor==accelerometer) {
            for(OnAccelerometerEventListener listener : onAccelerometerEventListeners)
                listener.onAccelerometerEvent(pEvent.values[0], pEvent.values[1], pEvent.values[2], pEvent.timestamp, pEvent.accuracy);
        } else if(pEvent.sensor==gyroscope) {
            for(OnGyroscopeEventListener listener : onGyroscopeEventListeners)
                listener.onGyroscopeEvent(pEvent.values[0], pEvent.values[1], pEvent.values[2], pEvent.timestamp, pEvent.accuracy);
        } else if(pEvent.sensor==magneticSensor) {
            for(OnMagneticFieldEventListener listener : onMagneticSensorEventListeners)
                listener.onMagneticFieldEvent(pEvent.values[0], pEvent.values[1], pEvent.values[2], pEvent.timestamp, pEvent.accuracy);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
