package edu.uv.students.mobiledevices.sensorbasedpositioning;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.DirectionReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.EventDistributor;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.PathReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.StepLengthReconstruction;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.StepReconstruction;

public class Positioning extends AppCompatActivity {

    private EventDistributor eventDistributor;

    private StepReconstruction stepReconstruction;
    private DirectionReconstruction directionReconstruction;
    private StepLengthReconstruction stepLengthReconstruction;
    private PathReconstruction pathReconstruction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positioning);

        if(!areAllRequiredSensorsPresent()) {
            // TODO display error message
            return;
        }
        initReconstruction();
    }

    private boolean areAllRequiredSensorsPresent() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        return
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null ||
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null ||
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null;
    }

    private void initReconstruction() {
        eventDistributor = new EventDistributor((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        stepReconstruction = new StepReconstruction(eventDistributor);
        directionReconstruction = new DirectionReconstruction(eventDistributor);
        stepLengthReconstruction = new StepLengthReconstruction(eventDistributor);
        pathReconstruction = new PathReconstruction(eventDistributor);
        initEventDistribution();
    }

    private void initEventDistribution() {
        // step reconstruction
        eventDistributor.registerAccelerometerEventListener(stepReconstruction);

        // direction reconstruction
        eventDistributor.registerGyroscopeEventListener(directionReconstruction);
        eventDistributor.registerMagneticFieldEventListener(directionReconstruction);

        //step length reconstruction

        // path reconstruction
        eventDistributor.registerOnDirectionChangedListener(pathReconstruction);
        eventDistributor.registerOnStepLengthChangedListener(pathReconstruction);
        eventDistributor.registerOnStepListener(pathReconstruction);

        // processing drawing
        //TODO add screen output that is interested in path changes
    }


}
