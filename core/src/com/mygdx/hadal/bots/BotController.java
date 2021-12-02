package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Invulnerability;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A BotController manages all of a bot's behaviors and cooldowns
 * @author Hurbbury Heebone
 */
public class BotController {

    private final PlayerBot player;

    //this is the current path of nodes that the bot attempts to go through
    private final Array<RallyPoint> pointPath = new Array<>();
    private BotMood currentMood = BotMood.DILLY_DALLY;

    //This is the default cooldown after jumping that a bot will try a jump again
    private static final float jumpDesireCooldown = 0.4f;
    private float jumpDesireCount;

    private static final float noFuelWaitCooldown = 3.0f;
    private float noFuelWaitCount;

    private static final float boostDesireCooldown = 0.5f;
    private boolean boostDesired;
    private float boostDesireCount;

    //this is the cooldown after a bot fires that they will release the fire button
    //It is used for specific weapons that require holding and releasing fire
    private float shootReleaseCount;

    //this is the entity that the bot attempts to shoot at
    private Schmuck shootTarget;

    private HadalEntity weaponTarget, eventTarget;

    private boolean lineOfSight, inRange;
    private float midrangeDifferenceSquare, targetDistanceSquare;

    public BotController(PlayerBot player) {
        this.player = player;
    }

    private float botTargetCount = botTargetInterval;
    private static final float botTargetInterval = 0.5f;
    private float botMoveCount = botMoveInterval;
    private static final float botMoveInterval = 0.05f;
    private final Vector2 entityWorldLocation = new Vector2();
    private final Vector2 entityVelocity = new Vector2();
    public void processBotAI(float delta) {
        entityWorldLocation.set(player.getPosition());
        entityVelocity.set(player.getLinearVelocity());
        botTargetCount += delta;
        botMoveCount += delta;

        boostDesireCount -= delta;
        if (boostDesired) {
            boostDesired = false;
            boostDesireCount = boostDesireCooldown;
            player.getController().keyDown(PlayerAction.BOOST);
            player.getController().keyUp(PlayerAction.BOOST);
        }

        while (botTargetCount >= botTargetInterval) {
            botTargetCount -= botTargetInterval;
            acquireTarget(entityWorldLocation, entityVelocity);
        }
        while (botMoveCount >= botMoveInterval) {
            botMoveCount -= botMoveInterval;
            processBotPickup();
            processBotAttacking(entityWorldLocation);
            processBotActiveItem(lineOfSight, targetDistanceSquare);
            processBotMovement(entityWorldLocation);
        }
        if (jumpDesireCount > 0.0f) {
            jumpDesireCount -= delta;
            if (jumpDesireCount <= 0.0f) {
                player.getController().keyUp(PlayerAction.JUMP);
            }
        }
        if (noFuelWaitCount > 0.0f) {
            noFuelWaitCount -= delta;
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
     */
    private void processBotAttacking(Vector2 playerLocation) {
        if (shootTarget != null) {
            shootTargetPosition.set(shootTarget.getPosition());
            BotLoadoutProcessor.processWeaponSwitching(player, playerLocation, shootTargetPosition, shootTarget.isAlive());
            BotLoadoutProcessor.processWeaponAim(player, shootTargetPosition, shootTarget.getLinearVelocity(), player.getPlayerData().getCurrentTool());
            BotLoadoutProcessor.processWeaponShooting(player, player.getPlayerData().getCurrentTool(), inRange);
        } else {
            BotLoadoutProcessor.processWeaponSwitching(player, playerLocation, shootTargetPosition, false);
            BotLoadoutProcessor.processWeaponAim(player, shootTargetPosition, playerLocation, player.getPlayerData().getCurrentTool());
            BotLoadoutProcessor.processWeaponShooting(player, player.getPlayerData().getCurrentTool(), false);
        }
    }

    /**
     * This makes the bot use their active item and defers to the BotLoadoutProcessor for item-specific logic.
     * @param shooting: whether the bot has acquired a target in sights or not
     */
    private void processBotActiveItem(boolean shooting, float distanceSquared) {
        BotLoadoutProcessor.processActiveItem(player, player.getPlayerData().getActiveItem(), shooting, distanceSquared);
    }

    private final Vector2 thisLocation = new Vector2();
    //this is the distance from a desired node that the bot will consider it "reached" before moving to the next
    private static final float distanceThreshold = 9.0f;
    private static final float boostThreshold = 300.0f;

    //these thresholds determine when the bot will fastfall (must be above their destination and not movign too fast already)
    private static final float fastfallDistThreshold = 8.0f;
    private static final float fastfallVeloThreshold = -30.0f;
    private static final float playerMovementMultiplier = 0.2f;
    /**
     * This processes the bot's movements
     * @param playerLocation: the location of the moving bot (to avoid repeatedly calling getPosition)
     */
    private void processBotMovement(Vector2 playerLocation) {
        float distSquared = 0.0f;
        float collision = 0.0f;
        boolean approachTarget = false;
        if (currentMood.equals(BotMood.SEEK_WEAPON)) {
            if (weaponTarget != null) {
                collision = BotManager.raycastUtility(player, playerLocation, weaponTarget.getPosition());
                if (collision == 1.0f) {
                    thisLocation.set(weaponTarget.getPosition()).sub(playerLocation);
                    distSquared = thisLocation.len2();
                    approachTarget = true;
                }
            }
        }
        if (currentMood.equals(BotMood.SEEK_EVENT)) {
            if (eventTarget != null) {
                collision = BotManager.raycastUtility(player, playerLocation, eventTarget.getPosition());
                if (collision == 1.0f) {
                    thisLocation.set(eventTarget.getPosition()).sub(playerLocation);
                    distSquared = thisLocation.len2();
                    approachTarget = true;
                }
            }
        }
        if (currentMood.equals(BotMood.SEEK_ENEMY)) {
            if (shootTarget != null && lineOfSight) {
                if (shootTarget.isAlive()) {
                    thisLocation.set(shootTarget.getPosition()).sub(playerLocation);
                    thisLocation.nor().scl(midrangeDifferenceSquare).scl(playerMovementMultiplier);
                    distSquared = thisLocation.len2();
                    approachTarget = true;
                }
            }
        }

        if (!pointPath.isEmpty() && !approachTarget) {
            thisLocation.set(pointPath.get(0).getPosition()).sub(playerLocation);
            collision = BotManager.raycastUtility(player, playerLocation, pointPath.get(0).getPosition());
            distSquared = thisLocation.len2();
            approachTarget = true;
        }

        if (approachTarget) {
            if (distSquared * collision > boostThreshold && boostDesireCount <= 0.0f && thisLocation.y > 0 &&
                    player.getPlayerData().getCurrentFuel() >= player.getPlayerData().getAirblastCost()) {
                player.getMouse().setDesiredLocation((
                        playerLocation.x - thisLocation.x) * PPM,(playerLocation.y - thisLocation.y) * PPM);
                boostDesired = true;
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
                        if (player.getPlayerData().getCurrentFuel() >= player.getPlayerData().getHoverCost()) {
                            if (noFuelWaitCount <= 0) {
                                player.getController().keyDown(PlayerAction.JUMP);
                                jumpDesireCount = jumpDesireCooldown;
                            }
                        } else {
                            noFuelWaitCount = noFuelWaitCooldown;
                        }
                    }
                }
            } else {
                player.getController().keyUp(PlayerAction.JUMP);
            }

            //in appropriate situations, bots will use fastfall
            if (((thisLocation.y < -fastfallDistThreshold && !player.isGrounded()) ||
                    (thisLocation.y < 0 && !player.getFeetData().getTerrain().isEmpty()))
                     && player.getLinearVelocity().y > fastfallVeloThreshold) {
                player.getController().keyDown(PlayerAction.CROUCH);
            } else {
                player.getController().keyUp(PlayerAction.CROUCH);
            }

            //if the bot is close enough to their destination, remove the node and begin moving towards the next one
            if (!pointPath.isEmpty()) {
                if (distSquared < distanceThreshold) {
                    pointPath.removeIndex(0);
                }
            }
        }
    }

    private static final float searchRadius = 60.0f;
    private static final int affinityThreshold1 = 10;
    private static final int affinityThreshold2 = 20;
    private static final int affinityThreshold3 = 25;
    private static final float affinityMultiplier1 = 0.5f;
    private static final float affinityMultiplier2 = 3.0f;
    private final Vector2 targetLocation = new Vector2();
    /**
     * This makes the bot search for player targets to pursue
     * @param playerLocation: the location of the attacking bot (to avoid repeatedly calling getPosition)
     */
    private void acquireTarget(Vector2 playerLocation, Vector2 playerVelocity) {

        //first we find best path to a weapon pickup.
        int totalAffinity = 0;
        int minAffinity = 100;
        for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getPlayerData().getMultitools()[i]);
            totalAffinity += affinity;
            minAffinity = Math.min(minAffinity, affinity);
        }
        RallyPoint pickupPoint = null;
        float weaponDesireMultiplier = 1.0f;
        if (totalAffinity < affinityThreshold3) {
            pickupPoint = BotLoadoutProcessor.getPointNearWeapon(player, playerLocation, searchRadius, minAffinity);

            //bots desire weapons more if they are not content with their current loadout and less if they are
            if (pickupPoint != null) {
                if (totalAffinity < affinityThreshold1) {
                    weaponDesireMultiplier = affinityMultiplier1;
                }
                if (totalAffinity > affinityThreshold2) {
                    weaponDesireMultiplier = affinityMultiplier2;
                }
            }
        }

        //find best enemy path by looking at all valid targets
        Array<RallyPoint.RallyPointMultiplier> targetPoints = new Array<>();
        shootTarget = null;
        float shortestDistanceSquared = -1;
        boolean unobtructedTargetFound = false;
        for (User user: HadalGame.server.getUsers().values()) {
            if (user.getPlayer() != null) {

                //we don't want to target dead, invisible or invincible players
                if (user.getPlayer().isAlive() && player.getHitboxfilter() != user.getPlayer().getHitboxfilter() &&
                        user.getPlayer().getPlayerData().getStatus(Invisibility.class) == null &&
                        user.getPlayer().getPlayerData().getStatus(Invulnerability.class) == null) {

                    //find shoot target by getting closest target with unobstructed vision
                    targetLocation.set(user.getPlayer().getPosition());
                    float distanceSquared = targetLocation.dst2(playerLocation);
                    boolean unobstructed = BotManager.raycastUtility(player, playerLocation, targetLocation) == 1.0f;
                    boolean update = false;
                    if (unobstructed) {
                        if (unobtructedTargetFound) {
                            if (shortestDistanceSquared > distanceSquared || shortestDistanceSquared == -1) {
                                update = true;
                            }
                        } else {
                            unobtructedTargetFound = true;
                            update = true;
                        }
                    } else {
                        if (!unobtructedTargetFound) {
                            if (shortestDistanceSquared > distanceSquared || shortestDistanceSquared == -1) {
                                update = true;
                            }
                        }
                    }
                    if (update) {
                        shootTarget = user.getPlayer();
                        shortestDistanceSquared = distanceSquared;
                    }
                    //calc the shortest path and compare it to paths to other targets
                    RallyPoint tempPoint = BotManager.getNearestPoint(player, targetLocation);
                    if (tempPoint != null) {
                        targetPoints.add(new RallyPoint.RallyPointMultiplier(tempPoint, 1.0f));
                    }
                }
            }
        }

        Array<RallyPoint> pathStarters = BotManager.getNearestPathStarters(player, playerLocation);
        Array<RallyPoint.RallyPointMultiplier> eventPoints = player.getState().getMode().processAIPath(player.getState(), player, playerLocation, playerVelocity);

        BotManager.requestPathfindingThread(player, playerLocation, playerVelocity, pathStarters,
                new RallyPoint.RallyPointMultiplier(pickupPoint, weaponDesireMultiplier), targetPoints, eventPoints);
    }

    public void setDistanceFromTarget(boolean lineOfSight, boolean inRange, float differenceSquares, float targetDistanceSquare) {
        this.lineOfSight = lineOfSight;
        this.inRange = inRange;
        this.midrangeDifferenceSquare = differenceSquares;
        this.targetDistanceSquare = targetDistanceSquare;
    }

    public Array<RallyPoint> getPointPath() { return pointPath; }

    public float getShootReleaseCount() { return shootReleaseCount; }

    public void setShootReleaseCount(float shootReleaseCount) { this.shootReleaseCount = shootReleaseCount; }

    public BotMood getCurrentMood() { return currentMood; }

    public void setCurrentMood(BotMood currentMood) { this.currentMood = currentMood; }

    public void setWeaponTarget(HadalEntity weaponTarget) { this.weaponTarget = weaponTarget; }

    public void setEventTarget(HadalEntity eventTarget) { this.eventTarget = eventTarget; }

    public enum BotMood {
        DILLY_DALLY,
        WANDER,
        SEEK_ENEMY,
        SEEK_EVENT,
        SEEK_WEAPON,
    }
}
