package edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.steplength;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import java.util.Iterator;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.interfaces.OnStepListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.deadreckoning.reconstruction.StepReconstruction.StepData;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepLengthReconstruction implements OnStepListener {

    public static final double DEFAULT_STEP_LENGTH_M = 0.718635;

    PolynomialFunction stepDurationSToStepLengthM;
    public StepLengthReconstruction() {
        stepDurationSToStepLengthM = new PolynomialFunction(new double[]{0.9525, -0.4288});
        //y = -0.4288x + 0.9525

    }

    @Override
    public void onStepSeriesChanged(LinkedList<StepData> pStepSeries) {
        StepData firstStep;
        StepData secondButLastStep;
        StepData lastStep;
        switch (pStepSeries.size()) {
            case 1:
                lastStep = pStepSeries.getLast();
                lastStep.stepLengthM = DEFAULT_STEP_LENGTH_M;
                if(lastStep.stepType== StepData.StepType.BEGIN_AND_END_OF_SERIES) {
                    lastStep.stepLengthM/=2.0;
                }
                break;
            case 2:
                lastStep = pStepSeries.getLast();
                lastStep.stepLengthM = stepDurationSToStepLengthM.value(((double) lastStep.getDurationNs()) / 1e9);
                if(lastStep.stepType == StepData.StepType.END_OF_SERIES) {
                    lastStep.stepLengthM/=2.0;
                    pStepSeries.getFirst().stepLengthM = lastStep.stepLengthM;
                } else {
                    pStepSeries.getFirst().stepLengthM = lastStep.stepLengthM/2.0;
                }
                break;
            case 3:
                Iterator<StepData> reverseIter = pStepSeries.descendingIterator();
                lastStep = reverseIter.next();
                secondButLastStep = reverseIter.next();
                firstStep = reverseIter.next();
                secondButLastStep.stepLengthM = stepDurationSToStepLengthM.value(((double) secondButLastStep.getDurationNs()) / 1e9);
                if(lastStep.stepType == StepData.StepType.END_OF_SERIES) {
                    lastStep.stepLengthM = secondButLastStep.stepLengthM/2.0;
                } else {
                    lastStep.stepLengthM = stepDurationSToStepLengthM.value(((double) lastStep.getDurationNs()) / 1e9);
                }
                firstStep.stepLengthM = secondButLastStep.stepLengthM/2.0;
                break;
            default:
                reverseIter = pStepSeries.descendingIterator();
                lastStep = reverseIter.next();
                secondButLastStep = reverseIter.next();
                secondButLastStep.stepLengthM = stepDurationSToStepLengthM.value(((double) secondButLastStep.getDurationNs()) / 1e9);
                if(lastStep.stepType == StepData.StepType.END_OF_SERIES) {
                    lastStep.stepLengthM = secondButLastStep.stepLengthM/2.0;
                } else {
                    lastStep.stepLengthM = stepDurationSToStepLengthM.value(((double) lastStep.getDurationNs()) / 1e9);
                }
                break;
        }
    }
}
