package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.step;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.PositionReconstruction;

/**
 * Created by Fabi on 29.04.2017.
 * Step sample databse
 */

class StepSampleDatabase {

    public static class StepSampleAndThresholds {
        StepSample sample;
        double xThreshold;
        double yThreshold;
        double zThreshold;
        double relativeAccelerationSpanThreshold;

        public StepSampleAndThresholds(StepSample pSample, double pXThreshold, double pYThreshold, double pZThreshold, double pRelativeAccelerationSpanThreshold) {
            sample = pSample;
            xThreshold = pXThreshold;
            yThreshold = pYThreshold;
            zThreshold = pZThreshold;
            relativeAccelerationSpanThreshold = pRelativeAccelerationSpanThreshold;

        }
    }

    private LinkedList<StepSampleAndThresholds> samplesAndThresholds;
    private double maxTimeWindowSizeInS;
    private static final String columnSeparator = "\t";

    StepSampleDatabase() {
        samplesAndThresholds = new LinkedList<>();
        maxTimeWindowSizeInS = 0.0;
    }

    public double getMaxTimeWindowSizeInS() {
        return maxTimeWindowSizeInS;
    }

    private void updateMaxTimeWindowSize(double pTimeWindowSizeInS) {
        if(pTimeWindowSizeInS> maxTimeWindowSizeInS)
            maxTimeWindowSizeInS = pTimeWindowSizeInS;
    }

    public void addSample(
            StepSample pSample,
            double pXThreshold,
            double pYThreshold,
            double pZThreshold,
            double pRelativeAccelerationSpanThreshold) {
        updateMaxTimeWindowSize(pSample.getDurationInS());
        samplesAndThresholds.add(new StepSampleAndThresholds(pSample, pXThreshold, pYThreshold, pZThreshold, pRelativeAccelerationSpanThreshold));
    }


    public void addSample(
            File pFile,
            double pXThreshold,
            double pYThreshold,
            double pZThreshold,
            double pRelativeAccelerationSpanThreshold) {
        LinkedList<Double> timesInS = new LinkedList<>();
        LinkedList<Double> accelsXInMPerS2 = new LinkedList<>();
        LinkedList<Double> accelsYInMPerS2 = new LinkedList<>();
        LinkedList<Double> accelsZInMPerS2 = new LinkedList<>();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(pFile)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.trim().split(columnSeparator);
                if(data.length!=4)
                    continue;
                double timeInS = Double.parseDouble(data[0]);
                double accelXInMPerS2 = Double.parseDouble(data[1]);
                double accelYInMPerS2 = Double.parseDouble(data[2]);
                double accelZInMPerS2 = Double.parseDouble(data[3]);
                timesInS.add(timeInS);
                accelsXInMPerS2.add(accelXInMPerS2);
                accelsYInMPerS2.add(accelYInMPerS2);
                accelsZInMPerS2.add(accelZInMPerS2);
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e(PositionReconstruction.LOG_TAG, "Error while parsing sample step file", e);
            if(bufferedReader!=null)
                try {
                    bufferedReader.close();
                } catch (IOException e1) {}
            return;
        }

        // window begin
        if(timesInS.getFirst()!=0.0) {
            timesInS.addFirst(0.0);
            accelsXInMPerS2.addFirst(accelsXInMPerS2.getFirst());
            accelsYInMPerS2.addFirst(accelsYInMPerS2.getFirst());
            accelsZInMPerS2.addFirst(accelsZInMPerS2.getFirst());
        }
        addSample(
            new StepSample(
                    ArrayUtils.toPrimitive(timesInS.toArray(new Double[timesInS.size()])),
                    ArrayUtils.toPrimitive(accelsXInMPerS2.toArray(new Double[accelsXInMPerS2.size()])),
                    ArrayUtils.toPrimitive(accelsYInMPerS2.toArray(new Double[accelsYInMPerS2.size()])),
                    ArrayUtils.toPrimitive(accelsZInMPerS2.toArray(new Double[accelsZInMPerS2.size()]))),
            pXThreshold,
            pYThreshold,
            pZThreshold,
            pRelativeAccelerationSpanThreshold
        );
    }

    public LinkedList<StepSampleAndThresholds> getSamplesAndThresholds() {
        return samplesAndThresholds;
    }

    public boolean isVarianceOfAnySampleBelowThreshold(double[] pTimeInS, double[] pAccelXInMPerS2, double[] pAccelYInMPerS2, double[] pAccelZInMPerS2) {
        for(StepSampleAndThresholds sampleAndThreshold : samplesAndThresholds) {
            boolean allAxisBelowThreshold = true;

            if(sampleAndThreshold.xThreshold!=Double.POSITIVE_INFINITY) {
                allAxisBelowThreshold &= sampleAndThreshold.sample.getVarianceX(pTimeInS, pAccelXInMPerS2) < sampleAndThreshold.xThreshold;
            }
            if(sampleAndThreshold.yThreshold!=Double.POSITIVE_INFINITY) {
                allAxisBelowThreshold &= sampleAndThreshold.sample.getVarianceY(pTimeInS, pAccelYInMPerS2) < sampleAndThreshold.yThreshold;
            }
            if(sampleAndThreshold.zThreshold!=Double.POSITIVE_INFINITY) {
                allAxisBelowThreshold &= sampleAndThreshold.sample.getVarianceZ(pTimeInS, pAccelZInMPerS2) < sampleAndThreshold.zThreshold;
            }
            double accelerationSpanX =
                    Collections.max(Arrays.asList(ArrayUtils.toObject(pAccelXInMPerS2)))-
                    Collections.min(Arrays.asList(ArrayUtils.toObject(pAccelXInMPerS2)));
            allAxisBelowThreshold &= accelerationSpanX > sampleAndThreshold.sample.getAccelerationSpanX()*sampleAndThreshold.relativeAccelerationSpanThreshold;

            double accelerationSpanY =
                    Collections.max(Arrays.asList(ArrayUtils.toObject(pAccelYInMPerS2)))-
                    Collections.min(Arrays.asList(ArrayUtils.toObject(pAccelYInMPerS2)));
            allAxisBelowThreshold &= accelerationSpanY > sampleAndThreshold.sample.getAccelerationSpanY()*sampleAndThreshold.relativeAccelerationSpanThreshold;

            double accelerationSpanZ =
                    Collections.max(Arrays.asList(ArrayUtils.toObject(pAccelZInMPerS2)))-
                    Collections.min(Arrays.asList(ArrayUtils.toObject(pAccelZInMPerS2)));
            allAxisBelowThreshold &= accelerationSpanZ > sampleAndThreshold.sample.getAccelerationSpanZ()*sampleAndThreshold.relativeAccelerationSpanThreshold;

            if(allAxisBelowThreshold)
                return true;
        }
        return false;
    }
}
