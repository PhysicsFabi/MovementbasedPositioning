package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.magneticfield;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnMagneticFieldEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnMagneticFieldVectorChangedListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.utils.SensorEvent;

/**
 * Created by Fabi on 21.05.2017.
 */

public class MagneticFieldProcessor implements OnMagneticFieldEventListener {

    final OnMagneticFieldVectorChangedListener listener;
    Vector3D magneticFieldLowPassFiltered;

    public MagneticFieldProcessor(OnMagneticFieldVectorChangedListener pListener) {
        listener = pListener;
    }

    @Override
    public void onMagneticFieldEvent(SensorEvent pSensorEvent) {
        final float alpha = 0.8f;
        if(magneticFieldLowPassFiltered==null)
            magneticFieldLowPassFiltered = new Vector3D(pSensorEvent.values[0], pSensorEvent.values[1], pSensorEvent.values[2]);
        else {
            magneticFieldLowPassFiltered = new Vector3D(
                    alpha * magneticFieldLowPassFiltered.getX() + (1 - alpha) * pSensorEvent.values[0],
                    alpha * magneticFieldLowPassFiltered.getY() + (1 - alpha) * pSensorEvent.values[1],
                    alpha * magneticFieldLowPassFiltered.getZ() + (1 - alpha) * pSensorEvent.values[2]
            );
        }
        listener.onMagneticFieldVectorChanged(magneticFieldLowPassFiltered);
        //Log.i("MAGNETIC EVALUATION","\t"+pSensorEvent.startTimeNs+"\t"+magneticFieldLowPassFiltered.getX()+"\t"+magneticFieldLowPassFiltered.getY()+"\t"+magneticFieldLowPassFiltered.getZ());
        //Log.i("MAGNETIC EVALUATION","\t"+pSensorEvent.startTimeNs+"\t"+pSensorEvent.values[0]+"\t"+pSensorEvent.values[1]+"\t"+pSensorEvent.values[2]);
    }




}
