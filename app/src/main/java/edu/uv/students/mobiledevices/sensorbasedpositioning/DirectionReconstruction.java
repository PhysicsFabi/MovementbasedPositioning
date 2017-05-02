package edu.uv.students.mobiledevices.sensorbasedpositioning;

/**
 * Created by Fabi on 02.05.2017.
 */

public class DirectionReconstruction {
    OnDirectionChangedListener directionChangedListener;

    public class DirectionData {

    }

    public interface OnDirectionChangedListener {
        void onDirectionChanged(DirectionData pDirectionData);
    }

    DirectionReconstruction(OnDirectionChangedListener pListener) {
        directionChangedListener = pListener;
    }
}
