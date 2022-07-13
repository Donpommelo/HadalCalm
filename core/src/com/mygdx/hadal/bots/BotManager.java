package com.mygdx.hadal.bots;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.MouseTracker;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.WorldUtil;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BotManager contains various utility methods for bot players.
 * Most of these methods are used for initiating bot rally points and using them to find paths
 * @author Svunwrangler Skewnaburger
 */
public class BotManager {

    //this is a list of all bot rally points in the current map mapped to their positions
    public static final ObjectMap<Vector2, RallyPoint> rallyPoints = new ObjectMap<>();

    /**
     * Run on first tick of server playstate. Initiate all bots
     */
    public static void initiateBots(PlayState state) {
        for (User user : HadalGame.server.getUsers().values()) {
            if (user.getScores().getConnID() < 0) {
                initiateBot(state, user);
            }
        }
    }

    /**
     * Initiate bot layer of map and keep track of all rally points
     * @param state: playstate. used to check mode for team-specific paths
     * @param map: map of the world we want to initate rally points for
     */
    public static void initiateRallyPoints(PlayState state, TiledMap map) {

        MapLayer botLayer = map.getLayers().get("bot-layer");
        if (botLayer != null) {
            for (MapObject object : botLayer.getObjects()) {
                PolylineMapObject current = (PolylineMapObject) object;

                float[] vertices = current.getPolyline().getTransformedVertices();

                Vector2[] worldVertices = new Vector2[vertices.length / 2];
                for (int i = 0; i < worldVertices.length; i++) {

                    //rounding to nearest 0.5 to avoid Tiled making coords slightly off
                    worldVertices[i] = new Vector2(
                            (int) (vertices[i * 2] * 2 / Constants.PPM) / 2.0f,
                            (int) (vertices[i * 2 + 1] * 2 / Constants.PPM) / 2.0f);
                }

                //set each vertex of the segment as a rally point and add its connections
                if (worldVertices.length == 2) {
                    if (!rallyPoints.containsKey(worldVertices[0])) {
                        rallyPoints.put(worldVertices[0], new RallyPoint(worldVertices[0]));
                    }
                    if (!rallyPoints.containsKey(worldVertices[1])) {
                        rallyPoints.put(worldVertices[1], new RallyPoint(worldVertices[1]));
                    }
                    if (object.getProperties().get("valid0", true, boolean.class)) {
                        rallyPoints.get(worldVertices[0]).addConnection(state, rallyPoints.get(worldVertices[1]),
                                object.getProperties().get("multiplier0", 1.0f, float.class),
                                object.getProperties().get("teamIndex", -1, Integer.class));
                    }
                    if (object.getProperties().get("valid1", true, boolean.class)) {
                        rallyPoints.get(worldVertices[1]).addConnection(state, rallyPoints.get(worldVertices[0]),
                                object.getProperties().get("multiplier1", 1.0f, float.class),
                                object.getProperties().get("teamIndex", -1, Integer.class));
                    }
                }
            }
        }
    }

    //executor is used to manage bot pathfinding thread
    private static ExecutorService executor;
    public static void initiatePathfindingThreads() { executor = Executors.newFixedThreadPool(1); }

    //when match ends, we must shutdown executor to close remaining threads
    public static void terminatePathfindingThreads() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * When a bot attempts to pathfind, it requests a thread to perform the search
     * Inputs info needed to navigate the bot rally point graph so the new thread doesn't need to query the world
     */
    public static void requestPathfindingThread(BotController controller, Vector2 playerLocation, Vector2 playerVelocity, Array<RallyPoint> pathStarters,
                                RallyPoint.RallyPointMultiplier weaponPoint,  RallyPoint.RallyPointMultiplier healthPoint,
                                Array< RallyPoint.RallyPointMultiplier> targetPoints, Array< RallyPoint.RallyPointMultiplier> eventPoints) {

        if (!executor.isShutdown()) {
            executor.submit(new BotPathfindingTask(controller, playerLocation, playerVelocity, pathStarters, weaponPoint,
                    healthPoint, targetPoints, eventPoints));
        }
    }

    //this is the furthest distance that we will check rally points for
    private static final float MAX_POINT_DISTANCE_CHECK = 30.0f;
    private static final Vector2 tempPointLocation = new Vector2();
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
        for (Vector2 rallyPoint : rallyPoints.keys()) {
            if (Math.abs(rallyPoint.x - sourceLocation.x) > MAX_POINT_DISTANCE_CHECK ||
                    Math.abs(rallyPoint.y - sourceLocation.y) > MAX_POINT_DISTANCE_CHECK) { continue; }

            tempPointLocation.set(rallyPoint);
            float raycastFraction = raycastUtility(targeter, sourceLocation, tempPointLocation, Constants.BIT_PLAYER);
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

    /**
     * Returns nearby visible rally points to pass to pathfinding threads which cannot perform raycasts
     * @param targeter: the schmuck looking for nearest path
     * @param sourceLocation: the location of the entity we are finding a path for
     * @return a list of points that could be potential path starters for the targeter
     */
    public static Array<RallyPoint> getNearestPathStarters(Schmuck targeter, Vector2 sourceLocation) {
        Array<RallyPoint> pathStarters = new Array<>();

        //iterate through all rally points up to a set distance away
        for (RallyPoint rallyPoint : rallyPoints.values()) {
            if (Math.abs(rallyPoint.getPosition().x - sourceLocation.x) > MAX_POINT_DISTANCE_CHECK ||
                    Math.abs(rallyPoint.getPosition().y - sourceLocation.y) > MAX_POINT_DISTANCE_CHECK) { continue; }

            tempPointLocation.set(rallyPoint.getPosition());
            float raycastFraction = raycastUtility(targeter, sourceLocation, tempPointLocation, Constants.BIT_PLAYER);

            //if we have a line of sight with the point, check if its distance is less than the nearest point so far
            if (raycastFraction == 1.0f) {
                pathStarters.add(rallyPoint);
            }
        }
        return pathStarters;
    }

    //open set used for a* search. Priority queue is used to make it ordered by estimated score
    private static final Queue<RallyPoint> openSet = new PriorityQueue<>();
    /**
     * Calculate short path with a*. Called by pathfinding task in separate thread, so it canno query the world
     * @param bot: the bot looking for a path
     * @param start: starting point
     * @param end: ending point
     * @return a reasonably short path between nodes in a graph using modified a* search or null if none exists
     */
    public static RallyPath getShortestPathBetweenPoints(Schmuck bot, RallyPoint start, RallyPoint end) {
        if (start == null || end == null) { return null; }

        //if we have this path cached, just return it to same some time
        RallyPath cachedPath = null;
        boolean cachedPathFound = false;
        if (start.getShortestPaths().containsKey(-1)) {
            if (start.getShortestPaths().get(-1).containsKey(end)) {
                cachedPath = start.getShortestPaths().get(-1).get(end);
                cachedPathFound = true;
            }
        }
        if (start.getShortestPaths().containsKey((int) bot.getHitboxfilter())) {
            if (start.getShortestPaths().get((int) bot.getHitboxfilter()).containsKey(end)) {
                cachedPath = start.getShortestPaths().get((int) bot.getHitboxfilter()).get(end);
                cachedPathFound = true;
            }
        }
        if (cachedPathFound) { return cachedPath; }

        //reset variables to properly calculate distance between them and add starting point to open set
        openSet.clear();
        for (RallyPoint point : rallyPoints.values()) {
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
                RallyPath path = new RallyPath(new Array<>(), parent.getRouteScore());

                //walk back from our end node to create our path by adding each parent to the start of the list
                RallyPoint current = parent;
                do {
                    path.getPath().insert(0, current);
                    current = current.getPrevious();
                } while (current != null);

                //cache shortest paths for all points in the shortest path
                Array<RallyPoint> tempPoints = new Array<>();
                int teamIndex = -1;
                for (RallyPoint pointInPath : path.getPath()) {
                    tempPoints.add(pointInPath);

                    //keep track of the path's team index for caching purposes
                    if (pointInPath.getTeamIndex() != -1) {
                        teamIndex = pointInPath.getTeamIndex();
                    }
                    if (!start.getShortestPaths().containsKey(teamIndex)) {
                        start.getShortestPaths().put(teamIndex, new ObjectMap<>());
                    }
                    start.getShortestPaths().get(teamIndex).put(pointInPath, new RallyPath(tempPoints, pointInPath.getRouteScore()));
                }
                if (!start.getShortestPaths().containsKey(teamIndex)) {
                    start.getShortestPaths().put(teamIndex, new ObjectMap<>());
                }
                start.getShortestPaths().get(teamIndex).put(end, path);
                return path;
            }

            //iterate through all neighbors to calc their route and estimated score
            for (RallyPoint neighbor : parent.getConnections().keys()) {

                float routeScore = parent.getRouteScore() + parent.getConnections().get(neighbor).distance();

                //we only accept a path if it is not team exclusive, or if it exclusive to our own team
                int teamIndex = parent.getConnections().get(neighbor).teamIndex();
                if (teamIndex != -1) {
                    if (teamIndex < AlignmentFilter.currentTeams.length) {
                        if (bot.getHitboxfilter() != AlignmentFilter.currentTeams[teamIndex].getFilter()) {
                            neighbor.setVisited(true);
                            continue;
                        }
                    }
                }

                //dst2 used here to slightly improve performance while being "mostly accurate-ish"
                float estimatedScore = routeScore * routeScore + neighbor.getPosition().dst2(end.getPosition());

                //if the neighbor is unvisited or we just got a lower score for it than before, update it and add it to the queue
                if (!neighbor.isVisited() || neighbor.getEstimatedScore() > estimatedScore) {
                    neighbor.setPrevious(parent);
                    neighbor.setRouteScore(routeScore);
                    neighbor.setEstimatedScore(estimatedScore);
                    neighbor.setTeamIndex(parent.getConnections().get(neighbor).teamIndex());
                    openSet.add(neighbor);
                }
            }
            parent.setVisited(true);
        }

        if (!start.getShortestPaths().containsKey((int) bot.getHitboxfilter())) {
            start.getShortestPaths().put((int) bot.getHitboxfilter(), new ObjectMap<>());
        }
        start.getShortestPaths().get((int) bot.getHitboxfilter()).put(end, null);

        return null;
    }

    private static final Vector2 diffTemp = new Vector2();
    private static final Vector2 aimTemp = new Vector2();
    private static final Vector2 leadDisplace = new Vector2();
    /**
     * This sets the bot's mouse to track its target, taking into account their weapon and the target's movements
     * @param targeter: the schmuck doing the targetting
     * @param sourceLocation: the location of the aiming bot
     * @param targetLocation: the location of the target the bot is aiming at
     * @param targetVelocity: the velocity of the target the bot is aiming at
     * @param projectileSpeed: the projectile speed of the bot's currently equipped weapon
     * @param wobble: should the player wobble their aim
     * @return a vector determining the location of the bot's mouse after aiming
     */
    public static Vector2 acquireAimTarget(PlayerBot targeter, Vector2 sourceLocation, Vector2 targetLocation, Vector2 targetVelocity,
                                           float projectileSpeed, boolean wobble) {
        aimTemp.set(targetLocation).sub(sourceLocation);
        diffTemp.set(aimTemp);

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
        float fract = BotManager.raycastUtility(targeter, targetLocation, aimTemp, Constants.BIT_PROJECTILE);
        if (fract < 1.0f) {
            aimTemp.set(targetLocation).add(leadDisplace.scl(fract));
        }

        //if the bot's aim is wobbling, we add their wobble to the aim
        if (wobble) {
            targeter.aimWobble();
            if (diffTemp.len2() > targeter.getCurrentWobble() * targeter.getCurrentWobble()) {
                aimTemp.add(targeter.getAimWobble());
            } else {
                aimTemp.add(diffTemp.rotate90(0).nor().scl(
                        (float) (Math.sin(targeter.getAimWobble().angleRad()) * targeter.getCurrentWobble())));
            }
        }
        return aimTemp;
    }

    private static float shortestFraction;
    /**
     * a quick raycast utility function used for various methods
     * @param targeter: the schmuck doing the targetting
     * @param sourceLocation: the location we are raycasting from
     * @param endLocation: the location we are raycasting towards
     * @param mask: mask bits of whatever will obstruct the raycast (if used for finding path for movement vs shooting)
     * @return the fraction of how far we raycasted before hitting a wall
     */
    public static float raycastUtility(Schmuck targeter, Vector2 sourceLocation, Vector2 endLocation, short mask) {
        shortestFraction = 1.0f;
        if (WorldUtil.preRaycastCheck(sourceLocation, endLocation)) {
            targeter.getWorld().rayCast((fixture1, point, normal, fraction) -> {
                if (fixture1.getFilterData().categoryBits == Constants.BIT_WALL &&
                        fixture1.getFilterData().groupIndex != targeter.getHitboxfilter() &&
                        ((fixture1.getFilterData().maskBits | mask) == fixture1.getFilterData().maskBits)) {
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

    /**
     * This initiates a single bot player, setting up their loadout, mouse and score
     */
    private static void initiateBot(PlayState state, User user) {
        Loadout botLoadout = BotLoadoutProcessor.getBotLoadout(state);

        user.getScoresExtra().setLoadout(botLoadout);

        Player newPlayer = state.createPlayer(null, user.getScores().getName(), user.getScoresExtra().getLoadout(),
                null, user.getScores().getConnID(), user, true, false, false,
                user.getHitBoxFilter().getFilter());
        MouseTracker newMouse = new MouseTracker(state, false);
        newPlayer.setMouse(newMouse);
        user.setPlayer(newPlayer);
        user.setSpectator(false);
    }
}
