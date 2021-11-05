package com.mygdx.hadal.bots;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

import java.util.*;

public class BotManager {

    private static final Map<Vector2, RallyPoint> rallyPoints = new HashMap<>();

    public static void initiateBots(PlayState state) {
        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getScores().getConnID() < 0) {
                initiateBot(state, user);
            }
        }
    }

    public static void initiateRallyPoints(TiledMap map) {
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

    private static void initiateBot(PlayState state, User user) {
        StartPoint newSave = state.getSavePoint(user);

        Loadout botLoadout = new Loadout();

        botLoadout.multitools = new UnlockEquip[]{ UnlockEquip.SPEARGUN, UnlockEquip.NOTHING, UnlockEquip.NOTHING, UnlockEquip.NOTHING };
        botLoadout.artifacts = new UnlockArtifact[]{ UnlockArtifact.MOON_FLUTHER, UnlockArtifact.GOOD_HEALTH, UnlockArtifact.NOTHING,  UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING,};
        botLoadout.character = UnlockCharacter.getRandCharFromPool(state);
        botLoadout.activeItem = UnlockActives.SUPPLY_DROP;
        botLoadout.character = UnlockCharacter.getRandCharFromPool(state);
        botLoadout.team = user.getTeamFilter();

        user.getScoresExtra().setLoadout(botLoadout);

        Player newPlayer = state.createPlayer(newSave, user.getScores().getName(), user.getScoresExtra().getLoadout(),
                null, user.getScores().getConnID(), true, false,
                user.getHitBoxFilter().getFilter());
        MouseTracker newMouse = new MouseTracker(state, false);
        newPlayer.setMouse(newMouse);

        user.setPlayer(newPlayer);
        user.setSpectator(false);
    }

    private static final Vector2 tempPointLocation = new Vector2();
    private static final Vector2 tempBotLocation = new Vector2();
    public static RallyPoint getNearestPoint(World world, Vector2 sourceLocation) {
        RallyPoint closestUnobstructed = null;
        RallyPoint closestObstructed = null;
        float closestDistUnobstructed = 0.0f;
        float closestDistObstructed = 0.0f;
        for (Vector2 rallyPoint: rallyPoints.keySet()) {
            tempPointLocation.set(rallyPoint);

            float raycastFraction = raycastUtility(world, sourceLocation, tempPointLocation);
            float currentDistSquared = raycastFraction * raycastFraction * sourceLocation.dst2(tempPointLocation);

            if (raycastFraction == 1.0f) {
                if (closestUnobstructed == null || currentDistSquared < closestDistUnobstructed) {
                    closestUnobstructed = rallyPoints.get(rallyPoint);
                    closestDistUnobstructed = currentDistSquared;
                }
            } else {
                if (closestUnobstructed == null && (closestObstructed == null || currentDistSquared < closestDistObstructed)) {
                    closestObstructed = rallyPoints.get(rallyPoint);
                    closestDistObstructed = currentDistSquared;
                }
            }
        }
        return closestUnobstructed != null ? closestUnobstructed : closestObstructed;
    }

    public static final float upCostModifier = 2.0f;
    public static final float downCostModifier = 0.5f;
    public static final float currentVelocityMultiplier = 1.5f;
    public static RallyPoint getNearestPathStarter(World world, Vector2 sourceLocation, Vector2 sourceVelocity, RallyPoint end) {
        RallyPoint closestUnobstructed = null;
        float closestDistUnobstructed = 0.0f;
        for (Vector2 rallyPoint: rallyPoints.keySet()) {
            tempPointLocation.set(rallyPoint);

            float raycastFraction = raycastUtility(world, sourceLocation, tempPointLocation);
            if (raycastFraction == 1.0f) {
                if (tempPointLocation.y > sourceLocation.y) {
                    tempPointLocation.set(tempPointLocation.x, sourceLocation.y + upCostModifier *
                            (tempPointLocation.y - sourceLocation.y));
                } else if (tempPointLocation.y < sourceLocation.y) {
                    tempPointLocation.set(tempPointLocation.x, sourceLocation.y + downCostModifier *
                            (tempPointLocation.y - sourceLocation.y));
                }
                tempBotLocation.set(sourceLocation).mulAdd(sourceVelocity, currentVelocityMultiplier);
                RallyPath shortestPath = getShortestPathBetweenPoints(rallyPoints.get(rallyPoint), end);
                if (shortestPath != null) {
                    float currentDistTotal = shortestPath.getDistance() + tempBotLocation.dst(tempPointLocation);
                    if (closestUnobstructed == null || currentDistTotal < closestDistUnobstructed) {
                        closestUnobstructed = rallyPoints.get(rallyPoint);
                        closestDistUnobstructed = currentDistTotal;
                    }
                }
            }
        }
        return closestUnobstructed;
    }

    public static RallyPath getShortestPathBetweenLocations(World world, Vector2 playerLocation, Vector2 targetLocation,
                Vector2 playerVelocity) {
        RallyPoint tempPoint = BotManager.getNearestPoint(world, targetLocation);
        RallyPoint myPoint = BotManager.getNearestPathStarter(world, playerLocation, playerVelocity, tempPoint);
        return BotManager.getShortestPathBetweenPoints(myPoint, tempPoint);
    }

    private static final Queue<RallyPoint> openSet = new PriorityQueue<>();
    public static RallyPath getShortestPathBetweenPoints(RallyPoint start, RallyPoint end) {

        if (start == null || end == null) { return null; }
        if (start.getShortestPaths().containsKey(end)) {
            return start.getShortestPaths().get(end);
        }

        openSet.clear();
        for (RallyPoint point: rallyPoints.values()) {
            point.setVisited(false);
            point.setRouteScore(0);
            point.setEstimatedScore(0);
            point.setPrevious(null);
        }
        openSet.add(start);

        while (!openSet.isEmpty()) {
            RallyPoint parent = openSet.poll();
            if (parent.equals(end)) {
                RallyPath path = new RallyPath();
                path.setDistance(parent.getRouteScore());
                RallyPoint current = parent;
                do {
                    path.getPath().add(0, current);
                    current = current.getPrevious();
                } while (current != null);

                //cache shortest paths for all points in the shortest path
                ArrayList<RallyPoint> tempPoints = new ArrayList<>();
                for (RallyPoint pointInPath: path.getPath()) {
                    tempPoints.add(pointInPath);
                    RallyPath cachedPath = new RallyPath();
                    cachedPath.setDistance(pointInPath.getRouteScore());
                    cachedPath.getPath().addAll(tempPoints);
                }
                start.getShortestPaths().put(end, path);
                return path;
            }
            for (RallyPoint neighbor: parent.getConnections().keySet()) {
                float routeScore = parent.getRouteScore() + parent.getConnections().get(neighbor);
                float estimatedScore = routeScore + neighbor.getPosition().dst(end.getPosition());

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
    public static Vector2 acquireAimTarget(World world, Vector2 sourceLocation, Vector2 targetLocation, Vector2 targetVelocity,
                                           float projectileSpeed) {
        aimTemp.set(targetLocation).sub(sourceLocation);
        float a = targetVelocity.dot(targetVelocity) - projectileSpeed * projectileSpeed;
        float b = 2 * aimTemp.dot(targetVelocity);
        float c = aimTemp.dot(aimTemp);

        float quadRoot = b * b - 4 * a * c;

        if (quadRoot < 0) {
            return targetLocation;
        }
        quadRoot = (float) Math.sqrt(quadRoot);
        float t1 = (-b + quadRoot) / (2 * a);
        float t2 = (-b - quadRoot) / (2 * a);

        if (t1 > 0 && (t1 < t2 || t2 < 0)) {
            leadDisplace.set(targetVelocity).scl(t1);
        } else if (t2 > 0 && (t2 < t1 || t1 < 0)) {
            leadDisplace.set(targetVelocity).scl(t2);
        }
        aimTemp.set(targetLocation).add(leadDisplace);

        float fract = BotManager.raycastUtility(world, targetLocation, aimTemp);
        if (fract < 1.0f) {
            aimTemp.set(targetLocation).add(leadDisplace.scl(fract));
        }
        return aimTemp;
    }

    private static float shortestFraction;
    public static float raycastUtility(World world, Vector2 sourceLocation, Vector2 endLocation) {
        shortestFraction = 1.0f;

        if (sourceLocation.x != endLocation.x || sourceLocation.y != endLocation.y) {
            world.rayCast((fixture1, point, normal, fraction) -> {
                if (fixture1.getFilterData().categoryBits == Constants.BIT_WALL) {
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
}
