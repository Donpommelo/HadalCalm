package com.mygdx.hadal.bots;

import java.util.ArrayList;

/**
 * A RallyPath consists of a list of Rally Points, as well as an estimated distance it would take to travel
 * @author Jelectra Juctavio
 */
public class RallyPath {

    private final ArrayList<RallyPoint> path = new ArrayList<>();
    private float distance;

    public RallyPath(ArrayList<RallyPoint> path, float distance) {
        this.path.addAll(path);
        this.distance = distance;
    }

    public ArrayList<RallyPoint> getPath() { return path; }

    public float getDistance() { return distance; }

    public void setDistance(float distance) { this.distance = distance; }
}
