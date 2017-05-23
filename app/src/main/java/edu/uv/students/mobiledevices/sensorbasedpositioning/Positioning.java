package edu.uv.students.mobiledevices.sensorbasedpositioning;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.PositionReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.eventemulation.EventEmulator;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnPathChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnSensorAccuracyLowListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.path.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.visualization.ProcessingVisualization;
import processing.core.PImage;
import processing.core.PVector;

public class Positioning extends AppCompatActivity implements
        SensorEventListener,
        OnPathChangedListener,
        OnSensorAccuracyLowListener,
        ProcessingVisualization.AndroidProcessingInterface {

    public static final String LOG_TAG = "SENSORBASED_POSITIONING";
    private static final String SCREEN_SHOT_FILE_NAME = "dead_reckoning.jpg";

    private PositionReconstruction positionReconstruction;

    private ProcessingVisualization processingVisualization;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magneticSensor;

    ArrayList<PVector> path;
    float direction;
    boolean isAccelerometerAccuracyLow;
    boolean isMagneticFieldAccuracyLow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (!areAllRequiredSensorsPresent()) {
            buildNotAllSensorsPresentDialog();
            return;
        }
        isAccelerometerAccuracyLow = false;
        isMagneticFieldAccuracyLow = false;
        initProcessing();
    }

    private void initPositionReconstruction() {
        positionReconstruction = new PositionReconstruction();
        positionReconstruction.registerOnPathChangedListener(this);
        positionReconstruction.registerOnSensorAccuracyLowListener(this);
        if(isCurrentSensorAccuracyLow()) {
            positionReconstruction.onSensorAccuracyLow();
        }

        // Choose either to initialize the real sensors
        // or, for testing, use the event emulation
        initSensors();
        //initEventEmulation();
    }

    private void initEventEmulation() {
        EventEmulator eventEmulator = new EventEmulator(positionReconstruction);
        // the EventEmulator provides different emulations for testing purposes
        eventEmulator.startEmulation01();
        //eventEmulator.startEmulationLoadedFromFile(getResources().openRawResource(R.raw.walking_in_flat),(long)(3*1e9));
    }

    private void initSensors() {
        if(accelerometer!=null)
            return;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initProcessing() {
        FragmentManager fragmentManager = getFragmentManager();
        processingVisualization = new ProcessingVisualization();
        processingVisualization.setAndroidInterface(this);
        fragmentManager.beginTransaction()
                .replace(R.id.ProcessingContainer, processingVisualization)
                .commit();
    }

    @Override
    public void onReset() {
        initPositionReconstruction();
    }

    @Override
    public void startDeadReckoning() {
        initPositionReconstruction();
    }

    @Override
    public void saveScreenshot(PImage pScreenshot) {
        String filePathAndName = getApplicationContext().getFilesDir().toString() + File.separator + SCREEN_SHOT_FILE_NAME;
        File file = new File(filePathAndName);
        pScreenshot.save(file.toString());
        if(file.exists()) {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), "edu.uv.students.mobiledevices.sensorbasedpositioning.provider", file);
            intentShareFile.setType("image/*");
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            getApplicationContext().startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }

    @Override
    public List<PVector> getPath() {
        return path;
    }

    @Override
    public void setProcessingVisualization(ProcessingVisualization pProcessingVisualization) {}

    @Override
    public float getDirection() {
        return direction;
    }

    @Override
    public void onPathChanged(PathData pPathData) {
        path = new ArrayList<>(pPathData.positions.size());
        synchronized(pPathData.positions) {
            for(Vector2D position : pPathData.positions) {
                path.add(Vector2DToPVector(position));
            }
        }
        direction = (float)pPathData.angle;
        processingVisualization.onPathChanged();
    }

    static PVector Vector2DToPVector(Vector2D vector2d) {
        return new PVector((float)vector2d.getX(), (float)vector2d.getY());
    }

    private boolean areAllRequiredSensorsPresent() {
        return
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
    }

    @Override
    public void onSensorChanged(SensorEvent pEvent) {
        if (pEvent.sensor == accelerometer) {
            positionReconstruction.onAccelerometerEvent(fromSensorEvent(pEvent));
        } else if (pEvent.sensor == magneticSensor) {
            positionReconstruction.onMagneticFieldEvent(fromSensorEvent(pEvent));
        }
    }

    public static  edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent fromSensorEvent(SensorEvent pEvent) {
        return new edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent(
                pEvent.timestamp, pEvent.values.clone(), pEvent.accuracy);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == accelerometer)
            isAccelerometerAccuracyLow = accuracy==SensorManager.SENSOR_STATUS_ACCURACY_HIGH;
        else if(sensor==magneticSensor)
            isMagneticFieldAccuracyLow = accuracy==SensorManager.SENSOR_STATUS_ACCURACY_HIGH;

        if(isCurrentSensorAccuracyLow())
            positionReconstruction.onSensorAccuracyLow();
    }

    private void buildNotAllSensorsPresentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();           }
        });
        builder.setMessage(R.string.error_missing_sensors).setTitle(R.string.error);
        builder.create();
    }

    @Override
    public void onSensorAccuracyLow() {
        processingVisualization.onLowSensorAccuracy();
    }

    boolean isCurrentSensorAccuracyLow() {
        return isAccelerometerAccuracyLow || isMagneticFieldAccuracyLow;
    }
}
