package edu.uv.students.mobiledevices.sensorbasedpositioning.visualization;

import android.util.Log;

import edu.uv.students.mobiledevices.sensorbasedpositioning.Positioning;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;
import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.interfaces.OnPathChangedListener;
import processing.core.PApplet;

/**
 * Created by Fabi on 03.05.2017.
 */

public class ProcessingVisualization extends PApplet implements OnPathChangedListener {
    public void settings() {
        fullScreen();
    }

    public void setup() { }

    public void draw() {
        background(0xFFFFFFFF);
        textAlign(CENTER, CENTER);
        textSize(0.2f*height);
        text("Hello from Jaime", 0 ,0, width, height);
        fill(0xFF000000);
        if (mousePressed) {
            ellipse(mouseX, mouseY, 50, 50);
        }
    }

    @Override
    public void onPathChanged(PathData pPathData) {
        Log.i(Positioning.LOG_TAG, "Path changed! time(ms): " + millis() + " angle: " + pPathData.angle + " way points: " + pPathData.positions.size() );
    }
}
