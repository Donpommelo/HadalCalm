package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.constants.Constants;

/**
 * A BotController manages all of a bot's behaviors and cooldowns generic for any bot-controlled unit
 * @author Clucklace Chuckheart
 */
public class BotController {

    private static final float BOT_TARGET_INTERVAL = 0.5f;
    private static final float BOT_MOVE_INTERVAL = 0.05f;
    private static final float BOT_MOVE_VARIANCE = 0.25f;

    protected final Schmuck bot;

    //this is the current path of nodes that the bot attempts to go through
    protected final Array<RallyPoint> pointPath = new Array<>();
    protected BotMood currentMood = BotMood.DILLY_DALLY;

    //this is the entity that the bot attempts to shoot at
    protected Schmuck shootTarget, lastShootTarget;

    //pickup or map objective that the bot will try pathing towards
    protected HadalEntity eventTarget;

    //does the bot have line of sight/is in range with their shoot target
    protected boolean lineOfSight, inRange;

    //distance squared between bot andd their shoot target
    protected float midrangeDifferenceSquare, targetDistanceSquare;

    private float botTargetCount = BOT_TARGET_INTERVAL;
    private float botMoveCount = BOT_MOVE_INTERVAL;
    protected final Vector2 entityWorldLocation = new Vector2();
    protected final Vector2 entityVelocity = new Vector2();

    public BotController(Schmuck bot) { this.bot = bot; }

    public void processBotAI(float delta) {
        entityWorldLocation.set(bot.getPosition());
        entityVelocity.set(bot.getLinearVelocity());
        botTargetCount += delta;
        botMoveCount += delta;

        processPreTarget(delta);

        while (botTargetCount >= BOT_TARGET_INTERVAL) {
            botTargetCount -= BOT_TARGET_INTERVAL * (1 + (-BOT_MOVE_VARIANCE + MathUtils.random() * 2 * BOT_MOVE_VARIANCE));
            acquireTarget(entityWorldLocation, entityVelocity);
        }

        while (botMoveCount >= BOT_MOVE_INTERVAL) {
            botMoveCount -= BOT_MOVE_INTERVAL * (1 + (-BOT_MOVE_VARIANCE + MathUtils.random() * 2 * BOT_MOVE_VARIANCE));
            processBotAction();
        }
    }

    /**
     * This processes bot behavior that must be done prior to moving/targeting, such as bot player boosting
     */
    public void processPreTarget(float delta) {}

    /**
     * This processes the bot's actions such as attacking or moving
     */
    public void processBotAction() {
        processBotAttacking(entityWorldLocation, entityVelocity);
        processBotMovement(entityWorldLocation, entityVelocity);
    }

    /**
     * This makes the bot attempt to attack their target
     * This processes the bot switching weapons, aiming and firing
     * Each function defers to the BotLoadoutProcessor for item-specific logic.
     * @param playerLocation: the location of the attacking bot (to avoid repeatedly calling getPosition)
     */
    public void processBotAttacking(Vector2 playerLocation, Vector2 playerVelocity) {}

    protected final Vector2 thisLocation = new Vector2();
    protected final Vector2 predictedSelfLocation = new Vector2();

    //this is the distance from a desired node that the bot will consider it "reached" before moving to the next
    protected static final float DISTANCE_THRESHOLD = 9.0f;
    protected static final float AI_RADIUS = 500;

    //these thresholds determine when the bot will fastfall (must be above their destination and not moving too fast already)
    private static final float PLAYER_MOVEMENT_MULTIPLIER = 0.2f;
    public static final float CURRENT_VELOCITY_MULTIPLIER = 0.02f;

    protected float distSquared, collision;
    protected boolean approachTarget;
    /**
     * This processes the bot's movements
     * @param playerLocation: the location of the moving bot (to avoid repeatedly calling getPosition)
     */
    public void processBotMovement(Vector2 playerLocation, Vector2 playerVelocity) {
        distSquared = 0.0f;
        collision = 0.0f;
        approachTarget = false;

        //bot considers their own velocity when deciding how they should move
        predictedSelfLocation.set(playerLocation).mulAdd(playerVelocity, CURRENT_VELOCITY_MULTIPLIER);
        float fract = BotManager.raycastUtility(bot, targetLocation, predictedSelfLocation, Constants.BIT_PLAYER);
        if (fract < 1.0f) {
            predictedSelfLocation.set(playerLocation).mulAdd(playerVelocity, CURRENT_VELOCITY_MULTIPLIER * fract);
        }

        //find target and see if we have line of sight to it
        HadalEntity target = findTarget();

        if (target != null) {
            collision = BotManager.raycastUtility(bot, predictedSelfLocation, target.getPosition(), Constants.BIT_PLAYER);
            if (collision == 1.0f) {
                thisLocation.set(target.getPosition()).sub(predictedSelfLocation);
                distSquared = thisLocation.len2();
                approachTarget = true;
            }
        }

        //if seeking player, raycast towards it and set target location if found
        if (BotMood.SEEK_ENEMY.equals(currentMood)) {
            if (shootTarget != null && lineOfSight) {
                if (shootTarget.isAlive()) {
                    thisLocation.set(shootTarget.getPosition()).sub(predictedSelfLocation);
                    thisLocation.nor().scl(midrangeDifferenceSquare).scl(PLAYER_MOVEMENT_MULTIPLIER);
                    distSquared = thisLocation.len2();
                    approachTarget = true;
                }
            }
        }

        //if no targets found, follow next point in point path
        if (!pointPath.isEmpty() && !approachTarget) {
            thisLocation.set(pointPath.get(0).getPosition()).sub(predictedSelfLocation);
            collision = BotManager.raycastUtility(bot, predictedSelfLocation, pointPath.get(0).getPosition(), Constants.BIT_PLAYER);
            distSquared = thisLocation.len2();
            approachTarget = true;
        }

        //if target in vision, move towards it
        if (approachTarget) {

            performMovement();

            //if the bot is close enough to their destination, remove the node and begin moving towards the next one
            if (!pointPath.isEmpty()) {
                if (distSquared < DISTANCE_THRESHOLD) {
                    pointPath.removeIndex(0);
                }
            }
        }
    }

    /**
     * This is used for the bot to get its target
     * @return the bot's target object
     */
    public HadalEntity findTarget() {
        HadalEntity target = null;
        if (BotMood.SEEK_EVENT.equals(currentMood)) {
            target = eventTarget;
        }
        return target;
    }

    /**
     * This is run for the bot to move. Override for bots depending on its movement strategy
     */
    public void performMovement() {}

    private final Vector2 targetLocation = new Vector2();
    /**
     * This acquires all the information needed to start a bot pathfinding thread
     * @param playerLocation: the location of the attacking bot (to avoid repeatedly calling getPosition)
     * @param playerVelocity: the velocity of the attacking bot
     */
    public void acquireTarget(Vector2 playerLocation, Vector2 playerVelocity) {

        RallyPoint.RallyPointMultiplier weaponPoint = getWeaponPoint(playerLocation);
        RallyPoint.RallyPointMultiplier healthPoint = getHealthPoint(playerLocation);
        Array<RallyPoint.RallyPointMultiplier> targetPoints = getTargetPoints(playerLocation, 1.0f);

        //get nearby points and event point with multipliers
        Array<RallyPoint> pathStarters = BotManager.getNearestPathStarters(bot, playerLocation);
        Array<RallyPoint.RallyPointMultiplier> eventPoints = getEventPoints(playerLocation);

        BotManager.requestPathfindingThread(this, playerLocation, playerVelocity, pathStarters,
                weaponPoint, healthPoint, targetPoints, eventPoints);
    }

    private static final float ENEMY_MULTIPLIER = 0.5f;
    /**
     * This gets a list of schmuck targets for the bot to consider attacking
     */
    public Array<RallyPoint.RallyPointMultiplier> getTargetPoints(Vector2 playerLocation, float multiplier) {
        Array<RallyPoint.RallyPointMultiplier> targetPoints = new Array<>();
        lastShootTarget = shootTarget;
        shootTarget = null;
        float shortestDistanceSquared = -1;
        boolean unobtructedTargetFound = false;
        for (User user : HadalGame.server.getUsers().values()) {
            if (user.getPlayer() != null) {

                //we don't want to target dead, invisible or invincible players
                if (user.getPlayer().isAlive() && bot.getHitboxfilter() != user.getPlayer().getHitboxfilter() &&
                        user.getPlayer().getPlayerData().getStatus(Invisibility.class) == null &&
                        user.getPlayer().getPlayerData().getStatus(Invulnerability.class) == null) {

                    //find shoot target by getting closest target with unobstructed vision
                    targetLocation.set(user.getPlayer().getPosition());
                    float distanceSquared = targetLocation.dst2(playerLocation);
                    boolean unobstructed = BotManager.raycastUtility(bot, playerLocation, targetLocation, Constants.BIT_PROJECTILE) == 1.0f;
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
                    RallyPoint tempPoint = BotManager.getNearestPoint(bot, targetLocation);
                    if (tempPoint != null) {
                        targetPoints.add(new RallyPoint.RallyPointMultiplier(tempPoint, multiplier));
                    }
                }
            }
        }

        final float shortestPlayerDistanceSquared = shortestDistanceSquared;
        //an additional check to find non-player targets (with decreased multiplier)
        bot.getWorld().QueryAABB((fixture -> {
            if (fixture.getUserData() instanceof final BodyData bodyData) {
                if (bodyData.getSchmuck().getHitboxfilter() != bot.getHitboxfilter() &&
                        bodyData.getSchmuck() instanceof Enemy enemy) {
                    targetLocation.set(enemy.getPosition());
                    RallyPoint tempPoint = BotManager.getNearestPoint(bot, targetLocation);
                    if (tempPoint != null) {
                        targetPoints.add(new RallyPoint.RallyPointMultiplier(tempPoint, ENEMY_MULTIPLIER));
                        if (targetLocation.dst2(playerLocation) < shortestPlayerDistanceSquared * ENEMY_MULTIPLIER
                                || shortestPlayerDistanceSquared == -1) {
                            shootTarget = enemy;
                        }
                        return false;
                    }
                }
            }
            return true;
        }), entityWorldLocation.x - AI_RADIUS, entityWorldLocation.y - AI_RADIUS,
                entityWorldLocation.x + AI_RADIUS, entityWorldLocation.y + AI_RADIUS);

        return targetPoints;
    }

    /**
     * This gets a list of event targets for the bot to consider moving towards
     */
    public Array<RallyPoint.RallyPointMultiplier> getEventPoints(Vector2 playerLocation) { return new Array<>(); }

    /**
     * This gets a list of weapon targets for the bot to consider moving towards
     */
    public RallyPoint.RallyPointMultiplier getWeaponPoint(Vector2 playerLocation) { return null; }

    /**
     * This gets a list of health targets for the bot to consider moving towards
     */
    public RallyPoint.RallyPointMultiplier getHealthPoint(Vector2 playerLocation) { return null; }

    /**
     * Called when targeting to find distance to target. Used to move in a direction that optimizes range
     */
    public void setDistanceFromTarget(boolean lineOfSight, boolean inRange, float differenceSquares, float targetDistanceSquare) {
        this.lineOfSight = lineOfSight;
        this.inRange = inRange;
        this.midrangeDifferenceSquare = differenceSquares;
        this.targetDistanceSquare = targetDistanceSquare;
    }

    public Schmuck getBot() { return bot; }

    public Array<RallyPoint> getPointPath() { return pointPath; }

    public BotMood getCurrentMood() { return currentMood; }

    public void setCurrentMood(BotMood currentMood) { this.currentMood = currentMood; }

    public void setEventTarget(HadalEntity eventTarget) { this.eventTarget = eventTarget; }

    public enum BotMood {
        DILLY_DALLY,
        WANDER,
        SEEK_ENEMY,
        SEEK_EVENT,
        SEEK_WEAPON,
        SEEK_HEALTH,
    }
}
