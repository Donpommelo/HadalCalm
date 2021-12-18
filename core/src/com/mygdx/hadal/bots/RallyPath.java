package com.mygdx.hadal.bots;

import com.badlogic.gdx.utils.Array;

/**
 * A RallyPath consists of a list of Rally Points, as well as an estimated distance it would take to travel
 * @author Jelectra Juctavio
 */
public class RallyPath {

    //this is a list of points in the path in the order of traversal
    private final Array<RallyPoint> path = new Array<>();

    //approximate distance of the path
    private float distance;

    public RallyPath(Array<RallyPoint> path, float distance) {
        this.path.addAll(path);
        this.distance = distance;
    }

    public Array<RallyPoint> getPath() { return path; }

    public float getDistance() { return distance; }

    public void setDistance(float distance) { this.distance = distance; }
}
