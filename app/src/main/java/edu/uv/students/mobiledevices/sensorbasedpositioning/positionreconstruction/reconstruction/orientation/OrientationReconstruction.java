package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.direction;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnDirectionChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.LinearAlgebraTools;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEvent;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.SensorEventsProcessingTools;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.utils.collections.SensorEventFixedTimeWindow;

/**
 * Created by Fabi on 02.05.2017.
 */

public class DirectionReconstruction implements OnAccelerometerEventListener, OnMagneticFieldEventListener {
    private enum STATE {
        IDLE,
        MEASURE_REFERENCE,
        MEASURE
    }

    private final OnDirectionChangedListener directionChangedListener;

    private final DirectionData directionData;

    private STATE  state;
    
    private static final long REF_MEASURE_TIME_NS = (long)(1e9);
    private Vector3D refMagneticFieldNormalized_ph;
    private Vector3D refDownwardsNormalized_ph;
    private Vector3D refNorthNormalized_ph;
    private SensorEventFixedTimeWindow refEventsMagneticField;
    private SensorEventFixedTimeWindow refEventsAccelerometer;


    private static final long MEASURE_RESOLUTION_NS = (long)(0.1*1e9);
    private long lastMeasureNs;
    private Vector3D magneticFieldNormalized_ph;
    private Vector3D downwardsNormalized_ph;
    private Vector3D northNormalized_ph;
    private Rotation transformationFromRefToCurrent;


    public DirectionReconstruction(OnDirectionChangedListener pListener) {
        directionChangedListener = pListener;
        directionData = new DirectionData();
        state = STATE.IDLE;
    }

    public void init() {
        directionData.pointingDirectionAngle = 0.0;
        directionData.walkingDirectionAngle = 0.0;
        directionChangedListener.onDirectionChanged(directionData);
        lastMeasureNs = -MEASURE_RESOLUTION_NS;
        startRefMeasurement();
    }

    private void startRefMeasurement() {
        state = STATE.IDLE;
        refEventsMagneticField = new SensorEventFixedTimeWindow(REF_MEASURE_TIME_NS, 0);
        refEventsAccelerometer = new SensorEventFixedTimeWindow(REF_MEASURE_TIME_NS, 0);
        state = STATE.MEASURE_REFERENCE;
    }

    private void checkRefMeasurementComplete() {
        if(refEventsMagneticField.isFull() && refEventsAccelerometer.isFull()) {
            endRefMeasurement();
            state = STATE.MEASURE;
        }
    }

    private void endRefMeasurement() {
        state = STATE.IDLE;
        float[] magneticFieldValues = SensorEventsProcessingTools.getValueMeans(
                SensorEventsProcessingTools.lowPassFilter(refEventsMagneticField));
        refMagneticFieldNormalized_ph = new Vector3D(magneticFieldValues[0], magneticFieldValues[1], magneticFieldValues[2]).normalize();
        float[] accelerometerValues = SensorEventsProcessingTools.getValueMeans(
                SensorEventsProcessingTools.lowPassFilter(refEventsAccelerometer));
        refDownwardsNormalized_ph = new Vector3D(accelerometerValues[0], accelerometerValues[1], accelerometerValues[2]).normalize();
        refNorthNormalized_ph = LinearAlgebraTools.projectOnPane(refDownwardsNormalized_ph, refMagneticFieldNormalized_ph).normalize();

    }

    private void resetTransformationFromRefToCurrent() {
        Vector3D rotationAxis = magneticFieldNormalized_ph.crossProduct(refMagneticFieldNormalized_ph);
        double rotation_angle_cos = magneticFieldNormalized_ph.dotProduct(refMagneticFieldNormalized_ph);
        double rotation_angle = Math.acos(rotation_angle_cos);
        transformationFromRefToCurrent = new Rotation(rotationAxis, rotation_angle, RotationConvention.VECTOR_OPERATOR);
    }

    private void setCurrentOrientation() {
        resetTransformationFromRefToCurrent();
        downwardsNormalized_ph = transformationFromRefToCurrent.applyTo(refDownwardsNormalized_ph);
        northNormalized_ph = LinearAlgebraTools.projectOnPane(downwardsNormalized_ph, magneticFieldNormalized_ph).normalize();
    }

    @Override
    public void onMagneticFieldEvent(SensorEvent pEvent) {
        switch (state) {
            case IDLE:
                return;
            case MEASURE_REFERENCE:
                refEventsMagneticField.add(pEvent);
                checkRefMeasurementComplete();
                break;
            case MEASURE:
                if(pEvent.timeNs-lastMeasureNs< MEASURE_RESOLUTION_NS)
                    return;
                lastMeasureNs = pEvent.timeNs;
                magneticFieldNormalized_ph = new Vector3D(pEvent.values[0], pEvent.values[1], pEvent.values[2]).normalize();
                setCurrentOrientation();
                break;
        }
    }


    @Override
    public void onAccelerometerEvent(SensorEvent pEvent) {
        switch (state) {
            case IDLE:
                return;
            case MEASURE_REFERENCE:
                refEventsAccelerometer.add(pEvent);
                checkRefMeasurementComplete();
                break;
            case MEASURE:
                break;
        }
    }
}
