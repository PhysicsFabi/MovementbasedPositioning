package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.step;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnAccelerometerEventListener;
import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.interfaces.OnStepListener;

/**
 * Created by Fabi on 02.05.2017.
 */

public class StepReconstruction implements OnAccelerometerEventListener {

    private final OnStepListener onStepListener;
    private final StepData stepData;
    private final String stepSamplesFilesDir;

    private StepSampleDatabase sampleDatabase;
    private final long TIME_WINDOW_RESOLUTION_IN_NS = 10000; //1minis
    private final int MINIMUM_SAMPLES_IN_TIME_WINDOW = 20;
    private long lastAccelerometerTimeInNs;
    private LinkedList<Double> accelerometerRecordingTInMPerS;
    private LinkedList<Double> accelerometerRecordingXInMPerS2;
    private LinkedList<Double> accelerometerRecordingYInMPerS2;
    private LinkedList<Double> accelerometerRecordingZInMPerS2;

    public StepReconstruction(OnStepListener pListener, String pStepSamplesFilesDir) {
        onStepListener = pListener;
        stepSamplesFilesDir = pStepSamplesFilesDir;
        stepData = new StepData();
        accelerometerRecordingTInMPerS = new LinkedList<>();
        accelerometerRecordingXInMPerS2 = new LinkedList<>();
        accelerometerRecordingYInMPerS2 = new LinkedList<>();
        accelerometerRecordingZInMPerS2 = new LinkedList<>();
        setUpSampleDatabase();
    }

    private void setUpSampleDatabase() {
        sampleDatabase = new StepSampleDatabase();
        double relativeAccelerationSpanThreshold = 0.65;

        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position0_00.dat"), 1.2152509773, 1.58709136519, 5.84957954823, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position0_01.dat"), 0.915862793785, 0.769968844109, 6.79075158376, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position0_02.dat"), 1.40280913519, 0.865980784476, 8.35217477306, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position0_03.dat"), 1.15649774299, 0.934764619654, 8.50161092423, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position0_04.dat"), 1.48176852256, 1.21907083716, 9.51384009994, relativeAccelerationSpanThreshold);

        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position1_00.dat"), 0.397912996158, 0.767419793046, 3.08660307415, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position1_01.dat"), 0.56774164586, 0.651866412647, 3.49157696695, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position1_02.dat"), 0.445824775221, 0.812980643932, 4.15511245478, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position1_03.dat"), 0.601818960167, 0.561851947841, 4.6089754235, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position1_04.dat"), 0.598350413907, 0.795061868869, 4.91179668383, relativeAccelerationSpanThreshold);

        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position2_00.dat"), 0.59431179822, 1.96710316827, 2.83365407926, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position2_01.dat"), 0.876590247021, 1.4299993007, 3.0134830206, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position2_02.dat"), 0.748668718946, 1.5680448202, 2.91338985713, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position2_03.dat"), 1.18208573377, 1.20939230379, 3.50581708524, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position2_04.dat"), 0.793895197906, 1.40016805996, 3.24524241205, relativeAccelerationSpanThreshold);

        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position3_00.dat"), 0.706686489971, 2.13942686806, 2.44297584266, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position3_01.dat"), 0.885443713015, 2.73979614551, 1.2377159971, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position3_02.dat"), 1.08635282556, 2.56986529664, 1.57488200107, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position3_03.dat"), 1.01064737795, 2.43051834045, 1.27036325696, relativeAccelerationSpanThreshold);
        sampleDatabase.addSample(new File(stepSamplesFilesDir+File.separator+"texting_position3_04.dat"), 1.07482053427, 2.56577014369, 1.1606789845, relativeAccelerationSpanThreshold);
    }

    public void init() {

    }

    @Override
    public void onAccelerometerEvent(float pX, float pY, float pZ, long pTimeStamp_ns, int pAccuracy) {
        long currentAccelerometerTimeInNs=System.nanoTime();
        if(currentAccelerometerTimeInNs-lastAccelerometerTimeInNs>TIME_WINDOW_RESOLUTION_IN_NS) {
            double earliestWindowTimeInS=currentAccelerometerTimeInNs*1e-9-sampleDatabase.getMaxTimeWindowSizeInS();
            Iterator<Double> t_iter = accelerometerRecordingTInMPerS.iterator();
            Iterator<Double> x_iter = accelerometerRecordingXInMPerS2.iterator();
            Iterator<Double> y_iter = accelerometerRecordingYInMPerS2.iterator();
            Iterator<Double> z_iter = accelerometerRecordingZInMPerS2.iterator();
            while(t_iter.hasNext() && t_iter.next()<earliestWindowTimeInS) {
                x_iter.next();
                y_iter.next();
                z_iter.next();
                t_iter.remove();
                x_iter.remove();
                y_iter.remove();
                z_iter.remove();
            }
            if(accelerometerRecordingTInMPerS.size()>MINIMUM_SAMPLES_IN_TIME_WINDOW) {
                t_iter = accelerometerRecordingTInMPerS.iterator();
                x_iter = accelerometerRecordingXInMPerS2.iterator();
                y_iter = accelerometerRecordingYInMPerS2.iterator();
                z_iter = accelerometerRecordingZInMPerS2.iterator();
                double[] timesInS = new double[accelerometerRecordingTInMPerS.size()];
                double[] accelXInMPerS2 = new double[accelerometerRecordingXInMPerS2.size()];
                double[] accelYInMPerS2 = new double[accelerometerRecordingYInMPerS2.size()];
                double[] accelZInMPerS2 = new double[accelerometerRecordingZInMPerS2.size()];
                for (int i = 0; i < accelerometerRecordingTInMPerS.size(); ++i) {
                    timesInS[i] = t_iter.next() - earliestWindowTimeInS;
                    accelXInMPerS2[i] = x_iter.next();
                    accelYInMPerS2[i] = y_iter.next();
                    accelZInMPerS2[i] = z_iter.next();
                }
                //Log.i(LOG_TAG,"samples: "+timesInS.length);
                //stepCounterAnalysis.addToAnalysis(timesInS, accelXInMPerS2, accelYInMPerS2, accelZInMPerS2);
                if (sampleDatabase.isVarianceOfAnySampleBelowThreshold(timesInS, accelXInMPerS2, accelYInMPerS2, accelZInMPerS2)) {
                    onStepListener.onStep(stepData);
                    accelerometerRecordingTInMPerS = new LinkedList<>();
                    accelerometerRecordingXInMPerS2 = new LinkedList<>();
                    accelerometerRecordingYInMPerS2 = new LinkedList<>();
                    accelerometerRecordingZInMPerS2 = new LinkedList<>();
                }
            }
        }
        accelerometerRecordingTInMPerS.add(((double)currentAccelerometerTimeInNs)*1e-9);
        accelerometerRecordingXInMPerS2.add((double)pX);
        accelerometerRecordingYInMPerS2.add((double)pY);
        accelerometerRecordingZInMPerS2.add((double)pZ);
        lastAccelerometerTimeInNs=currentAccelerometerTimeInNs;
    }

}
