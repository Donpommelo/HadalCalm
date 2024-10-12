package com.mygdx.hadal.bots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.schmucks.entities.HadalEntity;

/**
 * A BotPathfindingTask is a runnable ran on a separate thread to calculate bot pathfinding behavior
 *
 * @author Folican Fistard
 */
public record BotPathfindingTask(BotController controller, Vector2 playerLocation, Vector2 playerVelocity, Array<RallyPoint> pathStarters,
                 Array<RallyPoint.RallyPointMultiplier> targetPoints, Array<RallyPoint.RallyPointMultiplier> eventPoints) implements Runnable {

    private static final float ENEMY_TARGET_THRESHOLD = 15.0f;
    @Override
    public void run() {
        float bestDistanceSoFar = -1.0f;
        RallyPath prospectivePath = null;
        float pathDistance;

        RallyPath[] bestPath = new RallyPath[1];
        BotController.BotMood[] nextMood = new BotController.BotMood[1];
        HadalEntity[] nextTarget = new HadalEntity[1];

        //Wandering bots will, by default, continue wandering along the same path if no better option is found.
        //Bots with "dilly dally" will begin wandering a new path if no better option is found
        if (BotController.BotMood.WANDER.equals(controller.getCurrentMood())) {
            nextMood[0] = BotController.BotMood.WANDER;
        } else {
            nextMood[0] = BotController.BotMood.DILLY_DALLY;
        }

        //find path to each player target and find shortest distance to an enemy
        float bestTargetDistance = -1.0f;
        for (RallyPoint.RallyPointMultiplier targetPoint : targetPoints) {
            RallyPath tempPath = getShortestPathBetweenLocations(targetPoint.point());
            if (null != tempPath) {
                float targetDistance = tempPath.getDistance() * targetPoint.multiplier();
                if (ENEMY_TARGET_THRESHOLD > targetDistance) {
                    prospectivePath = tempPath;
                    bestTargetDistance = targetDistance;
                    break;
                }
                if (null != prospectivePath) {
                    if (targetDistance < bestTargetDistance) {
                        prospectivePath = tempPath;
                        bestTargetDistance = targetDistance;
                    }
                } else {
                    prospectivePath = tempPath;
                    bestTargetDistance = targetDistance;
                }
            }
        }

        pathDistance = null != prospectivePath ? bestTargetDistance : -1.0f;
        if (-1.0f != pathDistance) {
            bestPath[0] = prospectivePath;
            nextMood[0] = BotController.BotMood.SEEK_ENEMY;
            bestDistanceSoFar = pathDistance;
        }

        //find shortest path to mode objective
        float bestEventDistance = -1.0f;
        for (RallyPoint.RallyPointMultiplier eventPoint : eventPoints) {
            RallyPath tempPath = getShortestPathBetweenLocations(eventPoint.point());
            if (null != tempPath) {
                float eventDistance = tempPath.getDistance() * eventPoint.multiplier();
                if (null != prospectivePath) {
                    if (eventDistance < bestEventDistance || -1.0f == bestEventDistance) {
                        prospectivePath = tempPath;
                        bestEventDistance = eventDistance;
                        nextTarget[0] = eventPoint.target();
                    }
                } else {
                    prospectivePath = tempPath;
                    bestEventDistance = eventDistance;
                    nextTarget[0] = eventPoint.target();
                }
            }
        }

        pathDistance = null != prospectivePath ? bestEventDistance : -1.0f;
        if (-1.0f != pathDistance && (pathDistance < bestDistanceSoFar || -1.0f == bestDistanceSoFar)) {
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
            if (BotController.BotMood.SEEK_EVENT.equals(nextMood[0])) {
                controller.setEventTarget(nextTarget[0]);
            }
            if (null != bestPath[0]) {
                controller.getPointPath().clear();
                controller.getPointPath().addAll(bestPath[0].getPath());
            }
            controller.setCurrentMood(nextMood[0]);
        });
    }

    //cost modifiers make it so that distance upwards is seen as more costly and distance downwards is seen as cheaper
    public static final float UP_COST_MODIFIER = 2.0f;
    public static final float DOWN_COST_MODIFIER = 0.5f;

    //this multiplier makes pathfinder take the player's current velocity in account when finding a suitable point
    public static final float CURRENT_VELOCITY_MULTIPLIER = 1.5f;

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
                tempPointLocation.set(tempPointLocation.x, playerLocation.y + UP_COST_MODIFIER *
                        (tempPointLocation.y - playerLocation.y));
            } else if (tempPointLocation.y < playerLocation.y) {
                tempPointLocation.set(tempPointLocation.x, playerLocation.y + DOWN_COST_MODIFIER *
                        (tempPointLocation.y - playerLocation.y));
            }
            tempBotLocation.set(playerLocation).mulAdd(playerVelocity, CURRENT_VELOCITY_MULTIPLIER);

            //find shortest-ish path and return
            RallyPath shortestPath = BotManager.getShortestPathBetweenPoints(controller.getBot(), pathStarter, end);
            if (null != shortestPath) {

                //we use squares to avoid calculating a square root; we don't need the shortest path, just short enough
                float currentDistSquaredTotal = shortestPath.getDistance() * shortestPath.getDistance()
                        + tempBotLocation.dst2(tempPointLocation);
                if (null == closestUnobstructed || currentDistSquaredTotal < closestDistUnobstructed) {
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
        if (0 < BotManager.rallyPoints.size) {
            return getShortestPathBetweenLocations(BotManager.rallyPoints.values().toArray()
                    .get(MathUtils.random(BotManager.rallyPoints.size - 1)));
        }
        return null;
    }
}
