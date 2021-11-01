package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

import static com.mygdx.hadal.bots.BotManager.downCostModifier;
import static com.mygdx.hadal.bots.BotManager.upCostModifier;

public class RallyPoint implements Comparable<RallyPoint> {

    private final Vector2 position;
    private final Map<RallyPoint, Float> connections = new HashMap<>();
    private final Map<RallyPoint, RallyPath> shortestPaths = new HashMap<>();

    private RallyPoint previous;
    private boolean visited;
    private float routeScore, estimatedScore;

    public RallyPoint(Vector2 position) {
        this.position = position;
    }

    private final Vector2 connectionTemp = new Vector2();
    public void addConnection(RallyPoint point, float multiplier) {
        connectionTemp.set(point.getPosition());
        if (connectionTemp.y > position.y) {
            connectionTemp.set(connectionTemp.x, position.y + upCostModifier * (connectionTemp.y - position.y));
        }
        if (connectionTemp.y < position.y) {
            connectionTemp.set(connectionTemp.x, position.y + downCostModifier * (connectionTemp.y - position.y));
        }
        float distance = point.getPosition().dst(position) * multiplier;
        connections.put(point, distance);
    }

    @Override
    public int compareTo(RallyPoint o) {
        return Float.compare(this.estimatedScore, o.estimatedScore);
    }

    public Vector2 getPosition() { return position; }

    public Map<RallyPoint, Float> getConnections() { return connections; }

    public Map<RallyPoint, RallyPath> getShortestPaths() { return shortestPaths; }

    public RallyPoint getPrevious() { return previous; }

    public void setPrevious(RallyPoint previous) { this.previous = previous; }

    public boolean isVisited() { return visited; }

    public void setVisited(boolean visited) { this.visited = visited; }

    public float getRouteScore() { return routeScore; }

    public void setRouteScore(float routeScore) { this.routeScore = routeScore; }

    public float getEstimatedScore() { return estimatedScore; }

    public void setEstimatedScore(float estimatedScore) { this.estimatedScore = estimatedScore; }
}
