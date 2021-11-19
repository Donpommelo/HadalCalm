package com.mygdx.hadal.bots;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

import java.util.*;

/**
 * BotManager contains various utility methods for bot players.
 * Most of these methods are used for initiating bot rally points and using them to find paths
 * @author Svunwrangler Skewnaburger
 */
public class BotManager {

    //this is a list of all bot rally points in the current map mapped to their positions
    private static final Map<Vector2, RallyPoint> rallyPoints = new HashMap<>();

    /**
     * Run on first tick of server playstate. Initiate all bots
     */
    public static void initiateBots(PlayState state) {
        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getScores().getConnID() < 0) {
                initiateBot(state, user);
            }
        }
    }

    /**
     * Initiate bot layer of map and keep track of all rally points
     * @param map: map of the world we want to initate rally points for
     */
    public static void initiateRallyPoints(TiledMap map) {

        //clear existing rally points to avoid memory leak
        rallyPoints.clear();
        MapLayer botLayer = map.getLayers().get("bot-layer");
        if (botLayer != null) {
            for (MapObject object : botLayer.getObjects()) {
                PolylineMapObject current = (PolylineMapObject) object;

                float[] vertices = current.getPolyline().getTransformedVertices();
                Vector2[] worldVertices = new Vector2[vertices.length / 2];
                for (int i = 0; i < worldVertices.length; i++) {
                    worldVertices[i] = new Vector2(vertices[i * 2] / Constants.PPM, vertices[i * 2 + 1] / Constants.PPM);
                }

                //set each vertice of the segment as a rally point and add its connections
                if (worldVertices.length == 2) {
                    if (!rallyPoints.containsKey(worldVertices[0])) {
                        rallyPoints.put(worldVertices[0], new RallyPoint(worldVertices[0]));
                    }
                    if (!rallyPoints.containsKey(worldVertices[1])) {
                        rallyPoints.put(worldVertices[1], new RallyPoint(worldVertices[1]));
                    }
                    if (object.getProperties().get("valid0", true, boolean.class)) {
                        rallyPoints.get(worldVertices[0]).addConnection(rallyPoints.get(worldVertices[1]),
                                object.getProperties().get("multiplier0", 1.0f, float.class));
                    }
                    if (object.getProperties().get("valid1", true, boolean.class)) {
                        rallyPoints.get(worldVertices[1]).addConnection(rallyPoints.get(worldVertices[0]),
                                object.getProperties().get("multiplier1", 1.0f, float.class));
                    }
                }
            }
        }
    }

    //this is the furthest distance that we will check rally poitns for
    private static final float MaxPointDistanceCheck = 25.0f;
    private static final Vector2 tempPointLocation = new Vector2();
    private static final Vector2 tempBotLocation = new Vector2();
    /**
     * @param targeter: the schmuck looking for nearest point
     * @param sourceLocation: the location of the entity we are looking for a nearest point for
     * @return the rallypoint in the map closest to the input location
     */
    public static RallyPoint getNearestPoint(Schmuck targeter, Vector2 sourceLocation) {
        RallyPoint closestUnobstructed = null;
        RallyPoint closestObstructed = null;
        float closestDistUnobstructed = 0.0f;
        float closestDistObstructed = 0.0f;

        //iterate through all rally points up to a set distance away
        for (Vector2 rallyPoint: rallyPoints.keySet()) {
            if (Math.abs(rallyPoint.x - sourceLocation.x) > MaxPointDistanceCheck ||
                    Math.abs(rallyPoint.y - sourceLocation.y) > MaxPointDistanceCheck) { continue; }

            tempPointLocation.set(rallyPoint);
            float raycastFraction = raycastUtility(targeter, sourceLocation, tempPointLocation);
            //dst2 used here to slightly improve performance while being "mostly accurate-ish"
            float currentDistSquared = raycastFraction * raycastFraction * sourceLocation.dst2(tempPointLocation);

            //if we have a line of sight with the point, check if its distance is less than the nearest point so far
            if (raycastFraction == 1.0f) {
                if (closestUnobstructed == null || currentDistSquared < closestDistUnobstructed) {
                    closestUnobstructed = rallyPoints.get(rallyPoint);
                    closestDistUnobstructed = currentDistSquared;
                }
            } else {
                //we also keep track of the closest point with no line of sight, but only use it if no better point exists
                if (closestUnobstructed == null && (closestObstructed == null || currentDistSquared < closestDistObstructed)) {
                    closestObstructed = rallyPoints.get(rallyPoint);
                    closestDistObstructed = currentDistSquared;
                }
            }
        }
        return closestUnobstructed != null ? closestUnobstructed : closestObstructed;
    }

    //cost modifiers make it so that distance upwards is seen as more costly and distance downwards is seen as cheaper
    public static final float upCostModifier = 2.0f;
    public static final float downCostModifier = 0.5f;

    //this multiplier makes pathfinder take the player's current velocity in account when finding a suitable point
    public static final float currentVelocityMultiplier = 1.5f;
    /**
     * @param targeter: the schmuck looking for nearest path
     * @param sourceLocation: the location of the entity we are finding a path for
     * @param sourceVelocity: the velocity of the entity we are finding a path for
     * @param end: the rally point we are searching for a path towards
     * @return the first rally point in the path that will lead the bot along the shortest point to the end point
     */
    public static RallyPoint getNearestPathStarter(Schmuck targeter, Vector2 sourceLocation, Vector2 sourceVelocity, RallyPoint end) {
        RallyPoint closestUnobstructed = null;
        float closestDistUnobstructed = 0.0f;

        //iterate through all rally points up to a set distance away
        for (Vector2 rallyPoint: rallyPoints.keySet()) {
            if (Math.abs(rallyPoint.x - sourceLocation.x) > MaxPointDistanceCheck ||
                    Math.abs(rallyPoint.y - sourceLocation.y) > MaxPointDistanceCheck) { continue; }

            tempPointLocation.set(rallyPoint);
            float raycastFraction = raycastUtility(targeter, sourceLocation, tempPointLocation);

            //if we have a line of sight with the point, check if its distance is less than the nearest point so far
            if (raycastFraction == 1.0f) {

                //traveling up less should be less desirable and down should be more desirable
                if (tempPointLocation.y > sourceLocation.y) {
                    tempPointLocation.set(tempPointLocation.x, sourceLocation.y + upCostModifier *
                            (tempPointLocation.y - sourceLocation.y));
                } else if (tempPointLocation.y < sourceLocation.y) {
                    tempPointLocation.set(tempPointLocation.x, sourceLocation.y + downCostModifier *
                            (tempPointLocation.y - sourceLocation.y));
                }

                //traveling in the same direction you are already moving quickly should be more desirable
                tempBotLocation.set(sourceLocation).mulAdd(sourceVelocity, currentVelocityMultiplier);

                //total cosst of path is distance of point to end + distance from entity to point
                RallyPath shortestPath = getShortestPathBetweenPoints(rallyPoints.get(rallyPoint), end);
                if (shortestPath != null) {

                    //we use squares to avoid calculating a square root; we don't need the shortest path, just short enough
                    float currentDistSquaredTotal = shortestPath.getDistance() * shortestPath.getDistance()
                            + tempBotLocation.dst2(tempPointLocation);
                    if (closestUnobstructed == null || currentDistSquaredTotal < closestDistUnobstructed) {
                        closestUnobstructed = rallyPoints.get(rallyPoint);
                        closestDistUnobstructed = currentDistSquaredTotal;
                    }
                }
            }
        }
        return closestUnobstructed;
    }

    /**
     * This method returns the "shortest" path between two locations
     * @param targeter: the schmuck looking for the path
     * @param playerLocation: the starting location of the path
     * @param targetLocation: the end location of the path
     * @param playerVelocity: the velocity of the player
     * @return a reasonably short path between input locations
     */
    public static RallyPath getShortestPathBetweenLocations(Schmuck targeter, Vector2 playerLocation, Vector2 targetLocation,
                Vector2 playerVelocity) {
        RallyPoint tempPoint = BotManager.getNearestPoint(targeter, targetLocation);
        RallyPoint myPoint = BotManager.getNearestPathStarter(targeter, playerLocation, playerVelocity, tempPoint);
        return BotManager.getShortestPathBetweenPoints(myPoint, tempPoint);
    }

    //open set used for a* search. Priority queue is used to make it ordered by estimated score
    private static final Queue<RallyPoint> openSet = new PriorityQueue<>();
    /**
     * @param start: starting point
     * @param end: ending point
     * @return a reasonably short path between nodes in a graph using modified a* search or null if none exists
     */
    public static RallyPath getShortestPathBetweenPoints(RallyPoint start, RallyPoint end) {
        if (start == null || end == null) { return null; }

        //if we have this path cached, just return it to same some time
        if (start.getShortestPaths().containsKey(end)) {
            return start.getShortestPaths().get(end);
        }

        //reset variables to properly calculate distance between them and add starting point to open set
        openSet.clear();
        for (RallyPoint point: rallyPoints.values()) {
            point.setVisited(false);
            point.setRouteScore(0);
            point.setEstimatedScore(0);
            point.setPrevious(null);
        }
        openSet.add(start);

        while (!openSet.isEmpty()) {

            //because we are using a priority queue, this is the node with the lowest estimated distance
            RallyPoint parent = openSet.poll();

            //if the best node is our target, we are done and have our path and its score
            if (parent.equals(end)) {
                RallyPath path = new RallyPath(new ArrayList<>(), parent.getRouteScore());

                //walk back from our end node to create our path by addind each parent to the start of the list
                RallyPoint current = parent;
                do {
                    path.getPath().add(0, current);
                    current = current.getPrevious();
                } while (current != null);

                //cache shortest paths for all points in the shortest path
                ArrayList<RallyPoint> tempPoints = new ArrayList<>();
                for (RallyPoint pointInPath: path.getPath()) {
                    tempPoints.add(pointInPath);
                    start.getShortestPaths().put(pointInPath, new RallyPath(tempPoints, pointInPath.getRouteScore()));
                }
                start.getShortestPaths().put(end, path);
                return path;
            }

            //iterate through all neighbors to calc their route and estimated score
            for (RallyPoint neighbor: parent.getConnections().keySet()) {
                float routeScore = parent.getRouteScore() + parent.getConnections().get(neighbor);

                //dst2 used here to slightly improve performance while being "mostly accurate-ish"
                float estimatedScore = routeScore * routeScore + neighbor.getPosition().dst2(end.getPosition());

                //if the neighbor is unvisited or we just got a lower score for it than before, update it and add it to the queue
                if (!neighbor.isVisited() || neighbor.getEstimatedScore() > estimatedScore) {
                    neighbor.setPrevious(parent);
                    neighbor.setRouteScore(routeScore);
                    neighbor.setEstimatedScore(estimatedScore);
                    openSet.add(neighbor);
                }
            }
            parent.setVisited(true);
        }
        return null;
    }

    private static final Vector2 aimTemp = new Vector2();
    private static final Vector2 leadDisplace = new Vector2();
    /**
     * This sets the bot's mouse to track its target, taking into account their weapon and the target's movements
     * @param targeter: the schmuck doing the targetting
     * @param sourceLocation: the location of the aiming bot
     * @param targetLocation: the location of the target the bot is aiming at
     * @param targetVelocity: the velocity of the target the bot is aiming at
     * @param projectileSpeed: the projectile speed of the bot's currently equipped weapon
     * @return a vector determining the location of the bot's mouse after aiming
     */
    public static Vector2 acquireAimTarget(Schmuck targeter, Vector2 sourceLocation, Vector2 targetLocation, Vector2 targetVelocity,
                                           float projectileSpeed) {
        aimTemp.set(targetLocation).sub(sourceLocation);

        //we calculate the collision point between prospective projectile and target
        float a = targetVelocity.dot(targetVelocity) - projectileSpeed * projectileSpeed;
        float b = 2 * aimTemp.dot(targetVelocity);
        float c = aimTemp.dot(aimTemp);

        //wwe solve the quadratic equation to find t; the time in seconds before the expected collision
        float quadRoot = b * b - 4 * a * c;
        if (quadRoot < 0) {
            return targetLocation;
        }
        quadRoot = (float) Math.sqrt(quadRoot);
        float t1 = (-b + quadRoot) / (2 * a);
        float t2 = (-b - quadRoot) / (2 * a);

        //find the lowest time that is non-negative and set our aim to target the expected collision location
        if (t1 > 0 && (t1 < t2 || t2 < 0)) {
            leadDisplace.set(targetVelocity).scl(t1);
        } else if (t2 > 0 && (t2 < t1 || t1 < 0)) {
            leadDisplace.set(targetVelocity).scl(t2);
        }
        aimTemp.set(targetLocation).add(leadDisplace);

        //if the new aim vector goes through a wall, we want to stop at the wall location
        float fract = BotManager.raycastUtility(targeter, targetLocation, aimTemp);
        if (fract < 1.0f) {
            aimTemp.set(targetLocation).add(leadDisplace.scl(fract));
        }
        return aimTemp;
    }

    private static float shortestFraction;

    /**
     * a quick raycast utility function used for various methods
     * @param targeter: the schmuck doing the targetting
     * @param sourceLocation: the location we are raycasting from
     * @param endLocation: the location we are raycasting towards
     * @return the fraction of how far we raycasted before hitting a wall
     */
    public static float raycastUtility(Schmuck targeter, Vector2 sourceLocation, Vector2 endLocation) {
        shortestFraction = 1.0f;
        if (sourceLocation.x != endLocation.x || sourceLocation.y != endLocation.y) {
            targeter.getWorld().rayCast((fixture1, point, normal, fraction) -> {
                if (fixture1.getFilterData().categoryBits == Constants.BIT_WALL &&
                        fixture1.getFilterData().groupIndex != targeter.getHitboxfilter() &&
                        ((fixture1.getFilterData().maskBits | Constants.BIT_PLAYER) == fixture1.getFilterData().maskBits)) {
                    if (fraction < shortestFraction) {
                        shortestFraction = fraction;
                        return fraction;
                    }
                }
                return -1.0f;
            }, sourceLocation, endLocation);
        }
        return shortestFraction;
    }

    public static RallyPath getPathToRandomPoint(Schmuck targeter, Vector2 playerLocation, Vector2 playerVelocity) {
        RallyPoint point = (RallyPoint) rallyPoints.values().toArray()[MathUtils.random(rallyPoints.size() - 1)];
        return getShortestPathBetweenLocations(targeter, playerLocation, point.getPosition(), playerVelocity);
    }

    /**
     * This initiates a single bot player, setting up their loadout and score
     */
    private static void initiateBot(PlayState state, User user) {
        StartPoint newSave = state.getSavePoint(user);

        Loadout botLoadout = BotLoadoutProcessor.getBotLoadout(state);

        user.getScoresExtra().setLoadout(botLoadout);

        Player newPlayer = state.createPlayer(newSave, user.getScores().getName(), user.getScoresExtra().getLoadout(),
                null, user.getScores().getConnID(), user, true, false,
                user.getHitBoxFilter().getFilter());
        MouseTracker newMouse = new MouseTracker(state, false);
        newPlayer.setMouse(newMouse);
        user.setPlayer(newPlayer);
        user.setSpectator(false);
    }
}
