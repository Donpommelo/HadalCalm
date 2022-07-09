package com.mygdx.hadal.bots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A BotPathfindingTask is a runnable ran on a separate thread to calculate bot pathfinding behavior
 *
 * @author Folican Fistard
 */
public record BotPathfindingTask(BotController controller, Vector2 playerLocation, Vector2 playerVelocity, Array<RallyPoint> pathStarters,
                 RallyPoint.RallyPointMultiplier weaponPoint, RallyPoint.RallyPointMultiplier healthPoint,
                 Array<RallyPoint.RallyPointMultiplier> targetPoints, Array<RallyPoint.RallyPointMultiplier> eventPoints) implements Runnable {

    private static final float enemyTargetThreshold = 15.0f;
    @Override
    public void run() {
        float bestDistanceSoFar = -1.0f;
        RallyPath prospectivePath = null;
        float pathDistance;

        RallyPath[] bestPath = new RallyPath[1];
        BotController.BotMood[] nextMood = new BotController.BotMood[1];

        //Wandering bots will, by default, continue wandering along the same path if no better option is found.
        //Bots with "dilly dally" will begin wandering a new path if no better option is found
        if (BotController.BotMood.WANDER.equals(controller.getCurrentMood())) {
            nextMood[0] = BotController.BotMood.WANDER;
        } else {
            nextMood[0] = BotController.BotMood.DILLY_DALLY;
        }

        //find shortest path to weapon pickup
        if (weaponPoint != null) {
            prospectivePath = getShortestPathBetweenLocations(weaponPoint.point());
            pathDistance = prospectivePath != null ? prospectivePath.getDistance() * weaponPoint.multiplier() : -1.0f;
            if (pathDistance != -1.0f) {
                bestPath[0] = prospectivePath;
                nextMood[0] = BotController.BotMood.SEEK_WEAPON;
                bestDistanceSoFar = pathDistance;
            }
        }

        //find shortest path to health pickup
        if (healthPoint != null) {
            prospectivePath = getShortestPathBetweenLocations(healthPoint.point());
            pathDistance = prospectivePath != null ? prospectivePath.getDistance() * healthPoint.multiplier() : -1.0f;
            if (pathDistance != -1.0f && (pathDistance < bestDistanceSoFar || bestDistanceSoFar == -1.0f)) {
                bestPath[0] = prospectivePath;
                nextMood[0] = BotController.BotMood.SEEK_HEALTH;
                bestDistanceSoFar = pathDistance;
            }
        }

        //find path to each player target and find shortest distance to an enemy
        float bestTargetDistance = -1.0f;
        for (RallyPoint.RallyPointMultiplier targetPoint : targetPoints) {
            RallyPath tempPath = getShortestPathBetweenLocations(targetPoint.point());
            if (tempPath != null) {
                float targetDistance = tempPath.getDistance() * targetPoint.multiplier();
                if (targetDistance < enemyTargetThreshold) {
                    prospectivePath = tempPath;
                    bestTargetDistance = targetDistance;
                    break;
                }
                if (prospectivePath != null) {
                    if (targetDistance < bestTargetDistance || bestTargetDistance == -1.0f) {
                        prospectivePath = tempPath;
                        bestTargetDistance = targetDistance;
                    }
                } else {
                    prospectivePath = tempPath;
                    bestTargetDistance = targetDistance;
                }
            }
        }

        pathDistance = prospectivePath != null ? bestTargetDistance : -1.0f;
        if (pathDistance != -1.0f && (pathDistance < bestDistanceSoFar || bestDistanceSoFar == -1.0f)) {
            bestPath[0] = prospectivePath;
            nextMood[0] = BotController.BotMood.SEEK_ENEMY;
            bestDistanceSoFar = pathDistance;
        }

        //find shortest path to mode objective
        float bestEventDistance = -1.0f;
        for (RallyPoint.RallyPointMultiplier eventPoint : eventPoints) {
            RallyPath tempPath = getShortestPathBetweenLocations(eventPoint.point());
            if (tempPath != null) {
                float eventDistance = tempPath.getDistance() * eventPoint.multiplier();
                if (prospectivePath != null) {
                    if (eventDistance < bestEventDistance || bestEventDistance == -1.0f) {
                        prospectivePath = tempPath;
                        bestEventDistance = eventDistance;
                    }
                } else {
                    prospectivePath = tempPath;
                    bestEventDistance = eventDistance;
                }
            }
        }

        pathDistance = prospectivePath != null ? bestEventDistance : -1.0f;
        if (pathDistance != -1.0f && (pathDistance < bestDistanceSoFar || bestDistanceSoFar == -1.0f)) {
            bestPath[0] = prospectivePath;
            nextMood[0] = BotController.BotMood.SEEK_EVENT;
        }

        //if wandering, bot will continue wandering
        if (BotController.BotMood.WANDER.equals(controller.getCurrentMood())) {
            nextMood[0] = controller.getCurrentMood();
        }

        //if bot just finished a wander path or is dilly dallying, they will begin wandering to a new random point
        if ((BotController.BotMood.WANDER.equals(nextMood[0]) && controller.getPointPath().isEmpty()) ||
                BotController.BotMood.DILLY_DALLY.equals(nextMood[0])) {
            bestPath[0] = getPathToRandomPoint();
        }

        //after calculating a path, post runnable to change bot properties to pursue new line of action.
        Gdx.app.postRunnable(() -> {

            //dilly dallying becomes wandering with an empty path.
            if (BotController.BotMood.DILLY_DALLY.equals(nextMood[0])) {
                nextMood[0] = BotController.BotMood.WANDER;
                controller.getPointPath().clear();
            }
            if (bestPath[0] != null) {
                controller.getPointPath().clear();
                controller.getPointPath().addAll(bestPath[0].getPath());
            }
            controller.setCurrentMood(nextMood[0]);
        });
    }

    //cost modifiers make it so that distance upwards is seen as more costly and distance downwards is seen as cheaper
    public static final float upCostModifier = 2.0f;
    public static final float downCostModifier = 0.5f;

    //this multiplier makes pathfinder take the player's current velocity in account when finding a suitable point
    public static final float currentVelocityMultiplier = 1.5f;

    private static final Vector2 tempPointLocation = new Vector2();
    private static final Vector2 tempBotLocation = new Vector2();
    /**
     * Find a point nearby with shortest total path to a target and return a path to that point
     * @param end: the target poitn we are finding a short path for
     */
    private RallyPath getShortestPathBetweenLocations(RallyPoint end) {
        RallyPath nearestPath = null;
        RallyPoint closestUnobstructed = null;
        float closestDistUnobstructed = 0.0f;

        //iterate through path starters given by main thread
        for (RallyPoint pathStarter : pathStarters) {
            tempPointLocation.set(pathStarter.getPosition());

            //account for cost modifiers of verticality
            if (tempPointLocation.y > playerLocation.y) {
                tempPointLocation.set(tempPointLocation.x, playerLocation.y + upCostModifier *
                        (tempPointLocation.y - playerLocation.y));
            } else if (tempPointLocation.y < playerLocation.y) {
                tempPointLocation.set(tempPointLocation.x, playerLocation.y + downCostModifier *
                        (tempPointLocation.y - playerLocation.y));
            }
            tempBotLocation.set(playerLocation).mulAdd(playerVelocity, currentVelocityMultiplier);

            //find shortest-ish path and return
            RallyPath shortestPath = BotManager.getShortestPathBetweenPoints(controller.getBot(), pathStarter, end);
            if (shortestPath != null) {

                //we use squares to avoid calculating a square root; we don't need the shortest path, just short enough
                float currentDistSquaredTotal = shortestPath.getDistance() * shortestPath.getDistance()
                        + tempBotLocation.dst2(tempPointLocation);
                if (closestUnobstructed == null || currentDistSquaredTotal < closestDistUnobstructed) {
                    nearestPath = shortestPath;
                    closestUnobstructed = pathStarter;
                    closestDistUnobstructed = currentDistSquaredTotal;
                }
            } else {

                //we break if a path starter has no valid paths (since our start point must also have no paths)
                break;
            }
        }
        return nearestPath;
    }

    /**
     * @return a rally path to a random point on the map. USed for wandering behavior
     */
    private RallyPath getPathToRandomPoint() {
        if (BotManager.rallyPoints.size > 0) {
            return getShortestPathBetweenLocations(BotManager.rallyPoints.values().toArray()
                    .get(MathUtils.random(BotManager.rallyPoints.size - 1)));
        }
        return null;
    }
}
