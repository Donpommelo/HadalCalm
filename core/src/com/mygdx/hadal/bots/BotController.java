package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.User;

import java.util.ArrayList;

public class BotController {

    private final PlayerBot player;
    private final ArrayList<RallyPoint> pointPath = new ArrayList<>();
    private BotMood currentMood = BotMood.WANDER;

    private static final float jumpDesireCooldown = 0.4f;
    private float jumpDesireCount;

    private Schmuck shootTarget;

    public BotController(PlayerBot player) {
        this.player = player;
    }

    private float botMoodCount;
    private static final float botMoodInterval = 0.5f;
    private float botTargetCount;
    private static final float botTargetInterval = 0.1f;
    private float botMoveCount;
    private static final float botMoveInterval = 0.02f;
    private final Vector2 entityWorldLocation = new Vector2();
    public void processBotAI(float delta) {
        entityWorldLocation.set(player.getPosition());
        botMoodCount += delta;
        botTargetCount += delta;
        botMoveCount += delta;
        while (botMoodCount >= botMoodInterval) {
            botMoodCount -= botMoodInterval;
            processBotMood(entityWorldLocation);
        }
        while (botTargetCount >= botTargetInterval) {
            botTargetCount -= botTargetInterval;
            acquireTarget(entityWorldLocation);
        }
        while (botMoveCount >= botMoveInterval) {
            botMoveCount -= botMoveInterval;
            processBotMovement(entityWorldLocation);
            processBotAim(delta);
        }
        if (jumpDesireCount > 0.0f) {
            jumpDesireCount -= delta;
            if (jumpDesireCount <= 0.0f) {
                player.getController().keyUp(PlayerAction.JUMP);
            }
        }
    }

    private void processBotMood(Vector2 playerLocation) {


    }

    private void processBotLoadout() {
        //if the bot has a shootTarget, raycast to find distance and check if path is unobstructed

        //check each weapon's suitability, switch to whichever is most suitable if it is above a certain threshold
        //weapon suitability depends on distance, clear line of sight, clip left, bot preferences
        //if all are below threshold or there is no shootTarget, do proactive loadout activities

        //proactive loadout activities; reload highest suitability weapon that is missing clip
        //if all weapons are reloaded, switch to highest suitability weapon or charge weapon
    }

    private final Vector2 thisLocation = new Vector2();
    private static final float distanceThreshold = 9.0f;
    private static final float fastfallThreshold = 8.0f;
    private void processBotMovement(Vector2 playerLocation) {
        if (!pointPath.isEmpty()) {
            thisLocation.set(pointPath.get(0).getPosition()).sub(playerLocation);

            if (thisLocation.len2() < distanceThreshold) {
                pointPath.remove(0);
                if (!pointPath.isEmpty()) {
                    thisLocation.set(pointPath.get(0).getPosition()).sub(playerLocation);
                }
            }

            if (thisLocation.x > 0) {
                player.getController().keyDown(PlayerAction.WALK_RIGHT);
                player.getController().keyUp(PlayerAction.WALK_LEFT);
            }
            if (thisLocation.x < 0) {
                player.getController().keyDown(PlayerAction.WALK_LEFT);
                player.getController().keyUp(PlayerAction.WALK_RIGHT);
            }
            if (thisLocation.y > 0) {
                if (jumpDesireCount < 0) {
                    if (player.getPlayerData().getExtraJumpsUsed() < player.getPlayerData().getExtraJumps()) {
                        player.getController().keyDown(PlayerAction.JUMP);
                        player.getController().keyUp(PlayerAction.JUMP);
                        jumpDesireCount = jumpDesireCooldown;
                    }
                } else {
                    player.getController().keyDown(PlayerAction.JUMP);
                    jumpDesireCount = jumpDesireCooldown;
                }
            } else {
                player.getController().keyUp(PlayerAction.JUMP);
            }
            if (thisLocation.y < -fastfallThreshold) {
                player.getController().keyDown(PlayerAction.CROUCH);
            } else {
                player.getController().keyUp(PlayerAction.CROUCH);
            }
        }
    }

    private final Vector2 mouseLocation = new Vector2();
    private void processBotAim(float delta) {
        if (shootTarget != null) {
            mouseLocation.set(player.getMouse().getPixelPosition());
            mouseLocation.lerp(shootTarget.getPixelPosition(), delta);
            player.getMouse().setDesiredLocation(mouseLocation.x, mouseLocation.y);
        }
    }

    private void acquireTarget(Vector2 playerLocation) {
        RallyPath bestPath = null;
        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getPlayer() != null) {
                if (user.getPlayer().isAlive() && player.getHitboxfilter() != user.getPlayer().getHitboxfilter()) {
                    RallyPoint tempPoint = BotManager.getNearestPoint(player.getState().getWorld(), user.getPlayer().getPosition());
                    RallyPoint myPoint = BotManager.getNearestPathStarter(player.getState().getWorld(), playerLocation,
                            player.getLinearVelocity(), tempPoint);
                    RallyPath tempPath = BotManager.getShortestPath(myPoint, tempPoint);
                    if (tempPath != null) {
                        if (bestPath != null) {
                            if(tempPath.getDistance() < bestPath.getDistance()) {
                                bestPath = tempPath;
                                shootTarget = user.getPlayer();
                            }
                        } else {
                            bestPath = tempPath;
                            shootTarget = user.getPlayer();
                        }
                    }
                }
            }
        }

        if (bestPath != null) {
            pointPath.clear();
            pointPath.addAll(bestPath.getPath());
        }
    }

    public ArrayList<RallyPoint> getPointPath() { return pointPath; }

    private enum BotMood {
        WANDER,
        ENGAGE_ENEMY,
        SEEK_ENEMY,
    }
}
