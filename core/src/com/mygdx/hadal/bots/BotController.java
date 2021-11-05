package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Invulnerability;

import java.util.ArrayList;

public class BotController {

    private final PlayerBot player;
    private final ArrayList<RallyPoint> pointPath = new ArrayList<>();
    private BotMood currentMood = BotMood.WANDER;

    private static final float jumpDesireCooldown = 0.4f;
    private float jumpDesireCount;

    private float shootReleaseCount;

    private Schmuck shootTarget;

    public BotController(PlayerBot player) {
        this.player = player;
    }

    private float botTargetCount = botTargetInterval;
    private static final float botTargetInterval = 0.1f;
    private float botMoveCount;
    private static final float botMoveInterval = 0.02f;
    private final Vector2 entityWorldLocation = new Vector2();
    public void processBotAI(float delta) {
        entityWorldLocation.set(player.getPosition());
        botTargetCount += delta;
        botMoveCount += delta;
        while (botTargetCount >= botTargetInterval) {
            botTargetCount -= botTargetInterval;
            acquireTarget(entityWorldLocation);
        }
        while (botMoveCount >= botMoveInterval) {
            botMoveCount -= botMoveInterval;
            processBotPickup();
            processBotAttacking(entityWorldLocation);
            processBotMovement(entityWorldLocation);
        }
        if (jumpDesireCount > 0.0f) {
            jumpDesireCount -= delta;
            if (jumpDesireCount <= 0.0f) {
                player.getController().keyUp(PlayerAction.JUMP);
            }
        }
        if (shootReleaseCount > 0.0f) {
            shootReleaseCount -= delta;
            if (shootReleaseCount <= 0.0f) {
                player.getController().keyUp(PlayerAction.FIRE);
            }
        }
    }

    private void processBotPickup() {
        if (player.getCurrentEvent() != null) {
            if (player.getCurrentEvent() instanceof final PickupEquip pickup) {
                BotLoadoutProcessor.processWeaponPickup(player, pickup);
            }
        }
    }

    private final Vector2 shootTargetPosition = new Vector2();
    private void processBotAttacking(Vector2 playerLocation) {
        if (shootTarget != null) {
            shootTargetPosition.set(shootTarget.getPosition());
            boolean shooting = BotLoadoutProcessor.processWeaponSwitching(player, playerLocation, shootTargetPosition, shootTarget.isAlive());
            BotLoadoutProcessor.processWeaponAim(player, shootTargetPosition, shootTarget.getLinearVelocity(), player.getPlayerData().getCurrentTool());
            BotLoadoutProcessor.processWeaponShooting(player, player.getPlayerData().getCurrentTool(), shooting);
        }
    }

    private final Vector2 thisLocation = new Vector2();
    private static final float distanceThreshold = 9.0f;
    private static final float fastfallDistThreshold = 8.0f;
    private static final float fastfallVeloThreshold = -30.0f;
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
                if (jumpDesireCount <= 0) {
                    if (player.getPlayerData().getExtraJumpsUsed() < player.getPlayerData().getExtraJumps()) {
                        player.getController().keyDown(PlayerAction.JUMP);
                        player.getController().keyUp(PlayerAction.JUMP);
                        jumpDesireCount = jumpDesireCooldown;
                    } else {
                        player.getController().keyDown(PlayerAction.JUMP);
                        jumpDesireCount = jumpDesireCooldown;
                    }
                }
            } else {
                player.getController().keyUp(PlayerAction.JUMP);
            }
            if ((thisLocation.y < -fastfallDistThreshold || (thisLocation.y < 0 && !player.getFeetData().getTerrain().isEmpty()))
                    && !player.isGrounded() && player.getLinearVelocity().y > fastfallVeloThreshold) {
                player.getController().keyDown(PlayerAction.CROUCH);
            } else {
                player.getController().keyUp(PlayerAction.CROUCH);
            }
        }
    }

    private static final float searchRadius = 300.0f;
    private static final int affinityThreshold1 = 10;
    private static final int affinityThreshold2 = 20;
    private static final float affinityMultiplier1 = 0.5f;
    private static final float affinityMultiplier2 = 3.0f;
    private void acquireTarget(Vector2 playerLocation) {
        RallyPath bestWeaponPath;
        RallyPath bestEnemyPath = null;

        int totalAffinity = 0;
        int minAffinity = 100;
        for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getPlayerData().getMultitools()[i]);
            totalAffinity += affinity;
            minAffinity = Math.min(minAffinity, affinity);
        }
        bestWeaponPath = BotLoadoutProcessor.getPathToWeapon(player.getState().getWorld(), player, playerLocation,
                player.getLinearVelocity(), searchRadius, minAffinity);

        if (bestWeaponPath != null) {
            float weaponDesireMultiplier = 1.0f;
            if (totalAffinity < affinityThreshold1) {
                weaponDesireMultiplier = affinityMultiplier1;
            }
            if (totalAffinity > affinityThreshold2) {
                weaponDesireMultiplier = affinityMultiplier2;
            }
            bestWeaponPath.setDistance(bestWeaponPath.getDistance() * weaponDesireMultiplier);
        }

        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getPlayer() != null) {
                if (user.getPlayer().isAlive() && player.getHitboxfilter() != user.getPlayer().getHitboxfilter() &&
                user.getPlayer().getPlayerData().getStatus(Invisibility.class) == null &&
                user.getPlayer().getPlayerData().getStatus(Invulnerability.class) == null) {
                    RallyPath tempPath = BotManager.getShortestPathBetweenLocations(player.getState().getWorld(),
                            playerLocation, user.getPlayer().getPosition(), player.getLinearVelocity());
                    if (tempPath != null) {
                        if (bestEnemyPath != null) {
                            if (tempPath.getDistance() < bestEnemyPath.getDistance()) {
                                bestEnemyPath = tempPath;
                                shootTarget = user.getPlayer();
                            }
                        } else {
                            bestEnemyPath = tempPath;
                            shootTarget = user.getPlayer();
                        }
                    }
                }
            }
        }

        float weaponDistance = bestWeaponPath != null ? bestWeaponPath.getDistance() : -1;
        float enemyDistance = bestEnemyPath != null ? bestEnemyPath.getDistance() : -1;

        if (weaponDistance != -1 && (weaponDistance < enemyDistance || enemyDistance == -1)) {
            currentMood = BotMood.SEEK_WEAPON;
            pointPath.clear();
            pointPath.addAll(bestWeaponPath.getPath());
        } else if (enemyDistance != -1 && (enemyDistance < weaponDistance || weaponDistance == -1)) {
            currentMood = BotMood.SEEK_ENEMY;
            pointPath.clear();
            pointPath.addAll(bestEnemyPath.getPath());
        } else {
            currentMood = BotMood.WANDER;
        }
    }

    public ArrayList<RallyPoint> getPointPath() { return pointPath; }

    public float getShootReleaseCount() { return shootReleaseCount; }

    public void setShootReleaseCount(float shootReleaseCount) { this.shootReleaseCount = shootReleaseCount; }

    private enum BotMood {
        WANDER,
        ENGAGE_ENEMY,
        SEEK_ENEMY,
        SEEK_WEAPON,
    }
}
