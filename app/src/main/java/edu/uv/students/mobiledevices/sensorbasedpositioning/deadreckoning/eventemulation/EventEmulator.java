package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.eventemulation;

import android.hardware.SensorManager;
import android.util.Log;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.DeadReckoning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.path.PathData;

/**
 * Created by Fabi on 11.05.2017.
 */

public class EventEmulator {
    private final DeadReckoning DeadReckoning;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    abstract static class RunnableThatLogsExceptions implements Runnable {

        @Override
        public void run() {
            try {
                runExceptionSafe();
            } catch (Throwable throwable) {
                Log.e("EVENT_EMULATOR_ERROR", "whoops!", throwable);
            }
        }

        abstract void runExceptionSafe();
    }

    enum SensorEventType {
        ACCELEROMETER,
        GYROSCOPE,
        MAGNETIC_FIELD
    }
    static class SensorEvent extends edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent {
        public final SensorEventType eventType;

        public SensorEvent(SensorEventType pEventType, long timeNs, float pX, float pY, float pZ) {
            super(timeNs,new float[]{pX, pY, pZ}, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
            eventType = pEventType;
        }
    }

    private static final String ACCELEROMETER_DATA_TAG = "ACCELEROMETER";
    private static final String GYROSCOPE_DATA_TAG = "GYROSCOPE";
    private static final String MAGNETIC_FIELD_DATA_TAG = "MAGNETIC_FIELD";
    private static final String COLUMN_SEPARATOR = ",";


    public EventEmulator(DeadReckoning DeadReckoning) {
        this.DeadReckoning = DeadReckoning;
    }

    private SensorEventType mapStringToEventType(String pString) {
        switch (pString) {
            case ACCELEROMETER_DATA_TAG:
                return SensorEventType.ACCELEROMETER;
            case GYROSCOPE_DATA_TAG:
                return SensorEventType.GYROSCOPE;
            case MAGNETIC_FIELD_DATA_TAG:
                return SensorEventType.MAGNETIC_FIELD;
            default:
                return SensorEventType.ACCELEROMETER;
        }
    }

    /**
     * Loads captured sensor events from a file and emulates them on this device
     * The file format ist
     * Column 0: One of {ACCELEROMETER, GYROSCOPE, MAGNETIC_FIELD} (STRING)
     * Column 1: The time in nanoseconds (LONG)
     * Column 2-4: the sensor data (FLOAT)
     * Column separator: "," without spaces
     * @param pInputStream the file from which to load the events
     * @param pTimeDelayNs an initial time delay before the emulation starts in nanoseconds
     */
    public void startEmulationLoadedFromFile(InputStream pInputStream, long pTimeDelayNs){
        String line;
        ArrayList<SensorEvent> sensorEvents = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(pInputStream, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                String[] dataFields = line.split(COLUMN_SEPARATOR);
                sensorEvents.add(new SensorEvent(
                        mapStringToEventType(dataFields[0]),
                        Long.valueOf(dataFields[1])+pTimeDelayNs,
                        Float.valueOf(dataFields[2]),
                        Float.valueOf(dataFields[3]),
                        Float.valueOf(dataFields[4])));
            }
        } catch (IOException e) {
            Log.e(Positioning.LOG_TAG, "Error while reading file to emulate sensor events.", e);
        }
        for (final SensorEvent sensorEvent : sensorEvents) {
            switch(sensorEvent.eventType) {
                case ACCELEROMETER:
                    scheduler.schedule(new RunnableThatLogsExceptions() {
                        @Override
                        public void runExceptionSafe() {
                            DeadReckoning.onAccelerometerEvent(sensorEvent);
                        }
                    }, sensorEvent.timeNs, TimeUnit.NANOSECONDS);
                    break;
                case GYROSCOPE:
                    scheduler.schedule(new RunnableThatLogsExceptions() {
                        @Override
                        public void runExceptionSafe() {
                            DeadReckoning.onGyroscopeEvent(sensorEvent);
                        }
                    }, sensorEvent.timeNs, TimeUnit.NANOSECONDS);
                    break;
                case MAGNETIC_FIELD:
                    scheduler.schedule(new RunnableThatLogsExceptions() {
                        @Override
                        public void runExceptionSafe() {
                            DeadReckoning.onMagneticFieldEvent(sensorEvent);
                        }
                    }, sensorEvent.timeNs, TimeUnit.NANOSECONDS);
                    break;
            }
        }
    }

    /**
     * Emulates 5 steps in west direction and then a rotation of 360 degrees
     */
    public void startEmulation01() {
        final PathData pathData = new PathData();

        long startTimePaddingMs = 3000;

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.getPositions().add(new Vector2D(1,0));
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+1000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.getPositions().add(new Vector2D(1,0));
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+2000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.getPositions().add(new Vector2D(2,0));
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+3000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.getPositions().add(new Vector2D(3,0));
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.angle = 2.0*Math.PI/4.0;
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4250, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.angle = 2*2.0*Math.PI/4.0;
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4500, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.angle = 3*2.0*Math.PI/4.0;
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4750, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                pathData.angle = 4*2.0*Math.PI/4.0;
                DeadReckoning.onPathChanged(pathData);
            }
        }, startTimePaddingMs+5000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new RunnableThatLogsExceptions(){
            @Override
            public void runExceptionSafe() {
                DeadReckoning.onSensorAccuracyLow();
            }
        }, startTimePaddingMs+6000, TimeUnit.MILLISECONDS);
    }
}
