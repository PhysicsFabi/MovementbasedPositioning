package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.steplength;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.accelerometer.StepData;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction implements OnStepListener {

    PolynomialFunction stepDurationSToStepLengthM;
    public StepLengthReconstruction() {
        stepDurationSToStepLengthM = new PolynomialFunction(new double[]{1.2832, -1.0898});
        //y = -1,0898x + 1,2832
    }

    @Override
    public void onStep(StepData pStepData) {
        pStepData.stepLengthM=stepDurationSToStepLengthM.value(((double)pStepData.durationNs)/1e9);
    }
}
