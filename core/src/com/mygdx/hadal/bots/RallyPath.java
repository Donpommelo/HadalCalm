package com.mygdx.hadal.bots;

import java.util.ArrayList;

public class RallyPath {

    private final ArrayList<RallyPoint> path = new ArrayList<>();
    private float distance;


    public ArrayList<RallyPoint> getPath() { return path; }

    public float getDistance() { return distance; }

    public void setDistance(float distance) { this.distance = distance; }
}
