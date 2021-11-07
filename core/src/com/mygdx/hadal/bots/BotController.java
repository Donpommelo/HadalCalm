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

/**
 * A BotController manages all of a bot's behaviors and cooldowns
 * @author Hurbbury Heebone
 */
public class BotController {

    private final PlayerBot player;

    //this is the current path of nodes that the bot attempts to go through
    private final ArrayList<RallyPoint> pointPath = new ArrayList<>();
    private BotMood currentMood = BotMood.WANDER;

    //This is the default cooldown after jumping that a bot will try a jump again
    private static final float jumpDesireCooldown = 0.4f;
    private float jumpDesireCount;

    //this is the cooldown after a bot fires that they will release the fire button
    //It is used for specific weapons that require holding and releasing fire
    private float shootReleaseCount;

    //this is the entity that the bot attempts to shoot at
    private Schmuck shootTarget;

    public BotController(PlayerBot player) {
        this.player = player;
    }

    private float botTargetCount = botTargetInterval;
    private static final float botTargetInterval = 0.5f;
    private float botMoveCount = botMoveInterval;
    private static final float botMoveInterval = 0.05f;
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
            boolean shooting = processBotAttacking(entityWorldLocation);
            processBotActiveItem(shooting);
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

    /**
     * This is run continuously to make the bot interact with weapon pickup events, if they are touching one
     */
    private void processBotPickup() {
        if (player.getCurrentEvent() != null) {
            if (player.getCurrentEvent() instanceof final PickupEquip pickup) {
                BotLoadoutProcessor.processWeaponPickup(player, pickup);
            }
        }
    }

    private final Vector2 shootTargetPosition = new Vector2();

    /**
     * This makes the bot attempt to attack their target
     * This process the bot switching weapons, aiming and firing
     * Each function defers to the BotLoadoutProcessor for item-specific logic.
     * @param playerLocation: the location of the attacking bot (to avoid repeatedly calling getPosition)
     * @return boolean of whether the bot can acquire a target
     */
    private boolean processBotAttacking(Vector2 playerLocation) {
        if (shootTarget != null) {
            shootTargetPosition.set(shootTarget.getPosition());
            boolean shooting = BotLoadoutProcessor.processWeaponSwitching(player, playerLocation, shootTargetPosition, shootTarget.isAlive());
            BotLoadoutProcessor.processWeaponAim(player, shootTargetPosition, shootTarget.getLinearVelocity(), player.getPlayerData().getCurrentTool());
            BotLoadoutProcessor.processWeaponShooting(player, player.getPlayerData().getCurrentTool(), shooting);
            return shooting;
        }
        return false;
    }

    /**
     * This makes the bot use their active item and defers to the BotLoadoutProcessor for item-specific logic.
     * @param shooting: whether the bot has acquired a target in sights or not
     */
    private void processBotActiveItem(boolean shooting) {
        BotLoadoutProcessor.processActiveItem(player, player.getPlayerData().getActiveItem(), shooting);
    }

    private final Vector2 thisLocation = new Vector2();
    //this is the distance from a desired node that the bot will consider it "reached" before moving to the next
    private static final float distanceThreshold = 9.0f;

    //these thresholds determine when the bot will fastfall (must be above their destination and not movign too fast already)
    private static final float fastfallDistThreshold = 8.0f;
    private static final float fastfallVeloThreshold = -30.0f;
    /**
     * This processes the bot's movements
     * @param playerLocation: the location of the moving bot (to avoid repeatedly calling getPosition)
     */
    private void processBotMovement(Vector2 playerLocation) {
        if (!pointPath.isEmpty()) {
            thisLocation.set(pointPath.get(0).getPosition()).sub(playerLocation);

            //if the bot is close enough to their destination, remove the node and begin moving towards the next one
            if (thisLocation.len2() < distanceThreshold) {
                pointPath.remove(0);
                if (!pointPath.isEmpty()) {
                    thisLocation.set(pointPath.get(0).getPosition()).sub(playerLocation);
                }
            }

            //x-direction movement simply decided by direction
            if (thisLocation.x > 0) {
                player.getController().keyDown(PlayerAction.WALK_RIGHT);
                player.getController().keyUp(PlayerAction.WALK_LEFT);
            }
            if (thisLocation.x < 0) {
                player.getController().keyDown(PlayerAction.WALK_LEFT);
                player.getController().keyUp(PlayerAction.WALK_RIGHT);
            }

            //if moving upwards, bot will use jumps if available and hover otherwise
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

            //in appropriate situations, bots will use fastfall
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

    /**
     * This makes the bot search for player targets to pursue
     * @param playerLocation: the location of the attacking bot (to avoid repeatedly calling getPosition)
     */
    private void acquireTarget(Vector2 playerLocation) {
        RallyPath bestWeaponPath;
        RallyPath bestEnemyPath = null;

        //first we find best path to a weapon pickup.
        int totalAffinity = 0;
        int minAffinity = 100;
        for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getPlayerData().getMultitools()[i]);
            totalAffinity += affinity;
            minAffinity = Math.min(minAffinity, affinity);
        }
        bestWeaponPath = BotLoadoutProcessor.getPathToWeapon(player.getState().getWorld(), player, playerLocation,
                player.getLinearVelocity(), searchRadius, minAffinity);

        //bots desire weapons more if they are not content with their current loadout and less if they are
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

        //find best enemy path by looking at all valid targets
        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getPlayer() != null) {

                //we don't want to target dead, invisible or invincible players
                if (user.getPlayer().isAlive() && player.getHitboxfilter() != user.getPlayer().getHitboxfilter() &&
                user.getPlayer().getPlayerData().getStatus(Invisibility.class) == null &&
                user.getPlayer().getPlayerData().getStatus(Invulnerability.class) == null) {

                    //calc the shortest path and compare it to paths to other targets
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

        //choose target and mood based on whether path to weapon or enemy is shorter (accounting for multipliers)
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
