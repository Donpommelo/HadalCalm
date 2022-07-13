package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * A RallyPoint represents a single node in a graph that connects different locations in a game map
 * These are used by bots to navigate the map and pathfind
 * @author Plorfwitz Pujboo
 */
public class RallyPoint implements Comparable<RallyPoint> {

    //the position of the point. This is also used as a unique id
    private final Vector2 position;

    //this maps to each of this point's neighbors the distance between the 2 nodes and valid team index
    private final ObjectMap<RallyPoint, connectionValue> connections = new ObjectMap<>();

    //this maps to another RallyPoint + Team Index to a reasonably short RallyPath between the 2 nodes
    private final ObjectMap<Integer, ObjectMap<RallyPoint, RallyPath>> shortestPaths = new ObjectMap<>();

    //these variables are used for a* search
    private RallyPoint previous;
    private boolean visited;
    private int teamIndex;
    private float routeScore, estimatedScore;

    public RallyPoint(Vector2 position) {
        this.position = position;
    }

    private final Vector2 connectionTemp = new Vector2();

    //cost modifiers make it so that distance upwards is seen as more costly and distance downwards is seen as cheaper
    public static final float UP_COST_MODIFIER = 1.5f;
    public static final float DOWN_COST_MODIFIER = 0.5f;
    /**
     * Add a single node as a connecting neighbor to this node
     * @param state: playstate. used to check mode
     * @param point: the node to add as a neighbor
     * @param multiplier: multiplier to make the node seem further or closer than it actually is
     */
    public void addConnection(PlayState state, RallyPoint point, float multiplier, int teamIndex) {
        connectionTemp.set(point.getPosition());
        if (connectionTemp.y > position.y) {
            connectionTemp.set(connectionTemp.x, position.y + UP_COST_MODIFIER * (connectionTemp.y - position.y));
        }
        if (connectionTemp.y < position.y) {
            connectionTemp.set(connectionTemp.x, position.y + DOWN_COST_MODIFIER * (connectionTemp.y - position.y));
        }
        float distance = connectionTemp.dst(position) * multiplier;

        //only in ctf mode do we set the team index so players do not attempt to enter enemy team spawn points
        if (GameMode.CTF.equals(state.getMode())) {
            connections.put(point, new connectionValue(distance, teamIndex));
        } else {
            connections.put(point, new connectionValue(distance, -1));
        }
    }

    public RallyPath getCachedPath(Schmuck bot, RallyPoint end) {
        RallyPath path = null;
        if (shortestPaths.containsKey(-1)) {
            path = shortestPaths.get(-1).get(end);
        }
        if (shortestPaths.containsKey((int) bot.getHitboxfilter())) {
            path = shortestPaths.get((int) bot.getHitboxfilter()).get(end);
        }
        return path;
    }

    public static record connectionValue(float distance, int teamIndex) {}

    @Override
    public int compareTo(RallyPoint o) {
        return Float.compare(this.estimatedScore, o.estimatedScore);
    }

    public Vector2 getPosition() { return position; }

    public ObjectMap<RallyPoint, connectionValue> getConnections() { return connections; }

    public ObjectMap<Integer, ObjectMap<RallyPoint, RallyPath>> getShortestPaths() { return shortestPaths; }

    public RallyPoint getPrevious() { return previous; }

    public void setPrevious(RallyPoint previous) { this.previous = previous; }

    public boolean isVisited() { return visited; }

    public void setVisited(boolean visited) { this.visited = visited; }

    public int getTeamIndex() { return teamIndex; }

    public void setTeamIndex(int teamIndex) { this.teamIndex = teamIndex; }

    public float getRouteScore() { return routeScore; }

    public void setRouteScore(float routeScore) { this.routeScore = routeScore; }

    public float getEstimatedScore() { return estimatedScore; }

    public void setEstimatedScore(float estimatedScore) { this.estimatedScore = estimatedScore; }

    public record RallyPointMultiplier(RallyPoint point, float multiplier) {}
}
