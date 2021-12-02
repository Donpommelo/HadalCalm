package com.mygdx.hadal.bots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;

public record BotPathfindingTask(PlayerBot player, Vector2 playerLocation, Vector2 playerVelocity, Array<RallyPoint> pathStarters,
         RallyPoint.RallyPointMultiplier pickupPoint, Array<RallyPoint.RallyPointMultiplier> targetPoints,
         Array<RallyPoint.RallyPointMultiplier> eventPoints) implements Runnable {

    private static final float enemyTargetThreshold = 15.0f;
    @Override
    public void run() {
        float bestDistanceSoFar = -1.0f;
        RallyPath prospectivePath = null;
        float pathDistance;

        RallyPath[] bestPath = new RallyPath[1];
        BotController.BotMood[] nextMood = new BotController.BotMood[1];
        if (!player.getBotController().getCurrentMood().equals(BotController.BotMood.WANDER)) {
            nextMood[0] = BotController.BotMood.DILLY_DALLY;
        } else {
            nextMood[0] = BotController.BotMood.WANDER;
        }

        if (pickupPoint != null) {
            prospectivePath = getShortestPathBetweenLocations(pickupPoint.point());
            pathDistance = prospectivePath != null ? prospectivePath.getDistance() * pickupPoint.multiplier() : -1;
            if (pathDistance != -1) {
                bestPath[0] = prospectivePath;
                nextMood[0] = BotController.BotMood.SEEK_WEAPON;
                bestDistanceSoFar = pathDistance;
            }
        }

        for (RallyPoint.RallyPointMultiplier targetPoint: targetPoints) {
            RallyPath tempPath = getShortestPathBetweenLocations(targetPoint.point());
            if (tempPath != null) {
                float targetDistance = tempPath.getDistance() * targetPoint.multiplier();
                if (targetDistance < enemyTargetThreshold) {
                    prospectivePath = tempPath;
                    break;
                }
                if (prospectivePath != null) {
                    if (targetDistance < prospectivePath.getDistance()) {
                        prospectivePath = tempPath;
                    }
                } else {
                    prospectivePath = tempPath;
                }
            }
        }

        pathDistance = prospectivePath != null ? prospectivePath.getDistance() : -1;
        if (pathDistance != -1 && (pathDistance < bestDistanceSoFar || bestDistanceSoFar == -1.0f)) {
            bestPath[0] = prospectivePath;
            nextMood[0] = BotController.BotMood.SEEK_ENEMY;
            bestDistanceSoFar = pathDistance;
        }

        for (RallyPoint.RallyPointMultiplier eventPoint: eventPoints) {
            RallyPath tempPath = getShortestPathBetweenLocations(eventPoint.point());
            if (tempPath != null) {
                float eventDistance = tempPath.getDistance() * eventPoint.multiplier();
                if (prospectivePath != null) {
                    if (eventDistance < prospectivePath.getDistance()) {
                        prospectivePath = tempPath;
                    }
                } else {
                    prospectivePath = tempPath;
                }
            }
        }

        pathDistance = prospectivePath != null ? prospectivePath.getDistance() : -1;
        if (pathDistance != -1 && (pathDistance < bestDistanceSoFar || bestDistanceSoFar == -1.0f)) {
            bestPath[0] = prospectivePath;
            nextMood[0] = BotController.BotMood.SEEK_EVENT;
        }

        Gdx.app.postRunnable(() -> {

            if (nextMood[0].equals(BotController.BotMood.DILLY_DALLY)) {
                nextMood[0] = BotController.BotMood.WANDER;
                player.getBotController().getPointPath().clear();
            }
            if (nextMood[0].equals(BotController.BotMood.WANDER) && player.getBotController().getPointPath().isEmpty()) {
                bestPath[0] = getPathToRandomPoint();
            }

            if (bestPath[0] != null) {
                player.getBotController().getPointPath().clear();
                player.getBotController().getPointPath().addAll(bestPath[0].getPath());
            }
            player.getBotController().setCurrentMood(nextMood[0]);
        });
    }

    //cost modifiers make it so that distance upwards is seen as more costly and distance downwards is seen as cheaper
    public static final float upCostModifier = 2.0f;
    public static final float downCostModifier = 0.5f;

    //this multiplier makes pathfinder take the player's current velocity in account when finding a suitable point
    public static final float currentVelocityMultiplier = 1.5f;

    private static final Vector2 tempPointLocation = new Vector2();
    private static final Vector2 tempBotLocation = new Vector2();
    private RallyPoint getNearestPathStarter(RallyPoint end) {
        RallyPoint closestUnobstructed = null;
        float closestDistUnobstructed = 0.0f;

        for (RallyPoint pathStarter: pathStarters) {
            tempPointLocation.set(pathStarter.getPosition());

            if (tempPointLocation.y > playerLocation.y) {
                tempPointLocation.set(tempPointLocation.x, playerLocation.y + upCostModifier *
                        (tempPointLocation.y - playerLocation.y));
            } else if (tempPointLocation.y < playerLocation.y) {
                tempPointLocation.set(tempPointLocation.x, playerLocation.y + downCostModifier *
                        (tempPointLocation.y - playerLocation.y));
            }
            tempBotLocation.set(playerLocation).mulAdd(playerVelocity, currentVelocityMultiplier);

            RallyPath shortestPath = BotManager.getShortestPathBetweenPoints(pathStarter, end);
            if (shortestPath != null) {

                //we use squares to avoid calculating a square root; we don't need the shortest path, just short enough
                float currentDistSquaredTotal = shortestPath.getDistance() * shortestPath.getDistance()
                        + tempBotLocation.dst2(tempPointLocation);
                if (closestUnobstructed == null || currentDistSquaredTotal < closestDistUnobstructed) {
                    closestUnobstructed = pathStarter;
                    closestDistUnobstructed = currentDistSquaredTotal;
                }
            }
        }
        return closestUnobstructed;
    }

    private RallyPath getShortestPathBetweenLocations(RallyPoint end) {
        RallyPoint myPoint = getNearestPathStarter(end);
        return BotManager.getShortestPathBetweenPoints(myPoint, end);
    }

    private RallyPath getPathToRandomPoint() {
        if (BotManager.rallyPoints.size > 0) {
            return getShortestPathBetweenLocations(BotManager.rallyPoints.values().toArray().get(MathUtils.random(BotManager.rallyPoints.size - 1)));
        }
        return null;
    }
}
