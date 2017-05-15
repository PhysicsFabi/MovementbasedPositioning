package edu.uv.students.mobiledevices.sensorbasedpositioning.positionreconstruction.reconstruction.step;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Fabi on 29.04.2017.
 */

public class StepSample {
    private PolynomialSplineFunction xAcceleration;
    private PolynomialSplineFunction yAcceleration;
    private PolynomialSplineFunction zAcceleration;
    private double accelerationSpanX;
    private double accelerationSpanY;
    private double accelerationSpanZ;
    private double durationInS;

    StepSample(double[] pTimeInS, double[] pAccelXInMPerS2, double[] pAccelYInMPerS2, double[] pAccelZInMPerS2) {
        durationInS = pTimeInS[pTimeInS.length-1]-pTimeInS[0];
        xAcceleration = new LinearInterpolator().interpolate(pTimeInS, pAccelXInMPerS2);
        yAcceleration = new LinearInterpolator().interpolate(pTimeInS, pAccelYInMPerS2);
        zAcceleration = new LinearInterpolator().interpolate(pTimeInS, pAccelZInMPerS2);
        accelerationSpanX = Collections.max(Arrays.asList(ArrayUtils.toObject(pAccelXInMPerS2)))-Collections.min(Arrays.asList(ArrayUtils.toObject(pAccelXInMPerS2)));
        accelerationSpanY = Collections.max(Arrays.asList(ArrayUtils.toObject(pAccelYInMPerS2)))-Collections.min(Arrays.asList(ArrayUtils.toObject(pAccelYInMPerS2)));
        accelerationSpanZ = Collections.max(Arrays.asList(ArrayUtils.toObject(pAccelZInMPerS2)))-Collections.min(Arrays.asList(ArrayUtils.toObject(pAccelZInMPerS2)));
    }

    public double getAccelerationSpanX() {
        return accelerationSpanX;
    }

    public double getAccelerationSpanY() {
        return accelerationSpanY;
    }

    public double getAccelerationSpanZ() {
        return accelerationSpanZ;
    }

    public double getXAcceleration(double pTimeInS) {
        return xAcceleration.value(pTimeInS);
    }

    public double getYAcceleration(double pTimeInS) {
        return xAcceleration.value(pTimeInS);
    }

    public double getZAcceleration(double pTimeInS) {
        return xAcceleration.value(pTimeInS);
    }

    public double getDurationInS() {
        return durationInS;
    }


    private double getVarianceAlongAxis(double[] pTimesInS, double[] pAccelInMPerS2, PolynomialSplineFunction sampleAccelerationAlongAxis) {
        double var = 0;
        int i;
        for(i=0; i<pTimesInS.length; ++i) {
            double timeInS = pTimesInS[i];
            if(timeInS>getDurationInS())
                break;
            var+=Math.pow(sampleAccelerationAlongAxis.value(timeInS)-pAccelInMPerS2[i],2);
        }
        return var/i;
    }


    public double getVarianceX(double[] pTimesInS, double[] pAccelInMPerS2) {
        return getVarianceAlongAxis(pTimesInS,pAccelInMPerS2,xAcceleration);
    }

    public double getVarianceY(double[] pTimesInS, double[] pAccelInMPerS2) {
        return getVarianceAlongAxis(pTimesInS,pAccelInMPerS2,yAcceleration);
    }

    public double getVarianceZ(double[] pTimesInS, double[] pAccelInMPerS2) {
        return getVarianceAlongAxis(pTimesInS,pAccelInMPerS2,zAcceleration);
    }

    public double getVariance(double[] pTimesInS, double[] pAccelXInMPerS2, double[] pAccelYInMPerS2, double[] pAccelZInMPerS2) {
        double var = 0;
        int i;
        for(i=0; i<pTimesInS.length; ++i) {
            double timeInS = pTimesInS[i];
            if(timeInS>getDurationInS())
                break;
            double deviationX2=Math.pow(getXAcceleration(timeInS)-pAccelXInMPerS2[i],2);
            double deviationY2=Math.pow(getYAcceleration(timeInS)-pAccelYInMPerS2[i],2);
            double deviationZ2=Math.pow(getZAcceleration(timeInS)-pAccelZInMPerS2[i],2);
            var += deviationX2+deviationY2+deviationZ2;
        }
        return var/i;
    }
}
