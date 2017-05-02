package edu.uv.students.mobiledevices.sensorbasedpositioning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        initReconstruction();
    }

    private void initReconstruction() {
        eventDistributor = new EventDistributor();
        stepReconstruction = new StepReconstruction(eventDistributor);
        directionReconstruction = new DirectionReconstruction(eventDistributor);
        stepLengthReconstruction = new StepLengthReconstruction(eventDistributor);
        pathReconstruction = new PathReconstruction(eventDistributor);

        initEventDistribution();
    }

    private void initEventDistribution() {
        eventDistributor.registerOnDirectionChangedListener(pathReconstruction);
        eventDistributor.registerOnStepLengthChangedListener(pathReconstruction);
        eventDistributor.registerOnStepListener(pathReconstruction);
    }


}
