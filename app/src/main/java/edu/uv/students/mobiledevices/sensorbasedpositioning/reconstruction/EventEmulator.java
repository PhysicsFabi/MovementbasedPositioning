package edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import edu.uv.students.mobiledevices.sensorbasedpositioning.reconstruction.data.PathData;

/**
 * Created by Fabi on 11.05.2017.
 */

public class EventEmulator {
    private EventDistributor eventDistributor;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public EventEmulator(EventDistributor eventDistributor) {
        this.eventDistributor = eventDistributor;
    }

    public void startEmulationLoadedFromFile(File pFile){

    }

    public void startEmulation01() {
        final PathData pathData = new PathData();

        long startTimePaddingMs = 3000;

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(1,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+1000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(1,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+2000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(2,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+3000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.positions.add(new Vector2D(3,0));
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4000, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4250, TimeUnit.NANOSECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 2*2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4500, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 3*2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+4750, TimeUnit.MILLISECONDS);

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                pathData.angle = 4*2.0*Math.PI/4.0;
                eventDistributor.onPathChanged(pathData);
            }
        }, startTimePaddingMs+5000, TimeUnit.MILLISECONDS);
    }
}
