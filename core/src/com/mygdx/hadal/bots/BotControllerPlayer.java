package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A BotControllerPlayer manages all of a bot's behaviors and cooldowns specific to players
 * @author Hurbbury Heebone
 */
public class BotControllerPlayer extends BotController {

    private final PlayerBot player;

    //This is the default cooldown after jumping that a bot will try a jump again
    private static final float jumpDesireCooldown = 0.3f;
    private float jumpDesireCount;

    //delay after running out of fuel before trying to hover again (prevents stalling out in midair)
    private static final float noFuelWaitCooldown = 3.0f;
    private float noFuelWaitCount;

    //delay after boosting before thte bot will want to boost again
    private static final float boostDesireCooldown = 0.5f;
    private boolean boostDesired;
    private float boostDesireCount;

    //this is the cooldown after a bot fires that they will release the fire button
    //It is used for specific weapons that require holding and releasing fire
    private float shootReleaseCount;

    //pickup or map objective that the bot will try pathing towards
    private HadalEntity weaponTarget, healthTarget;

    public BotControllerPlayer(PlayerBot player) {
        super(player);
        this.player = player;
    }

    @Override
    public void processBotAI(float delta) {
        super.processBotAI(delta);

        if (shootTarget != null) {
            player.decrementWeaponWobble(delta);
        } else {
            player.incrementWeaponWobble(delta);
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

    @Override
    public void processPreTarget(float delta) {
        //if a boost is desired, execute it (run prior to targeting/moving to account for mouse movement)
        boostDesireCount -= delta;
        if (boostDesired) {
            boostDesired = false;
            boostDesireCount = boostDesireCooldown;
            player.getController().keyDown(PlayerAction.BOOST);
            player.getController().keyUp(PlayerAction.BOOST);
        }
    }

    @Override
    public void processBotAction() {
        processBotPickup();
        processBotAttacking(entityWorldLocation, entityVelocity);
        processBotActiveItem(lineOfSight, targetDistanceSquare);
        processBotMovement(entityWorldLocation, entityVelocity);
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
    @Override
    public void processBotAttacking(Vector2 playerLocation, Vector2 playerVelocity) {
        if (shootTarget != null) {
            shootTargetPosition.set(shootTarget.getPosition());
            BotLoadoutProcessor.processWeaponSwitching(player, playerLocation, shootTargetPosition, shootTarget.isAlive());
            BotLoadoutProcessor.processWeaponAim(player, shootTargetPosition, shootTarget.getLinearVelocity(),
                    player.getPlayerData().getCurrentTool(), true);
            BotLoadoutProcessor.processWeaponShooting(player, player.getPlayerData().getCurrentTool(), inRange);
        } else {
            //with no target, we aim towards the direction we are moving (set in processbotMovement)
            thisLocation.add(playerLocation);
            BotLoadoutProcessor.processWeaponSwitching(player, playerLocation, thisLocation, false);
            BotLoadoutProcessor.processWeaponAim(player, thisLocation, playerVelocity, player.getPlayerData().getCurrentTool(), false);
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

    @Override
    public HadalEntity findTarget() {
        HadalEntity target = null;

        if (currentMood.equals(BotMood.SEEK_WEAPON)) {
            target = weaponTarget;
        }
        if (currentMood.equals(BotMood.SEEK_HEALTH)) {
            target = healthTarget;
        }
        if (currentMood.equals(BotMood.SEEK_EVENT)) {
            target = eventTarget;
        }
        return target;
    }

    //these thresholds determine when the bot will fastfall (must be above their destination and not moving too fast already)
    private static final float fastfallDistThreshold = 6.0f;
    private static final float fastfallVeloThreshold = -30.0f;
    @Override
    public void performMovement() {
        //if distance to target is above threshold, use boost
        if (distSquared * collision > player.getBoostDesireMultiplier() && boostDesireCount <= 0.0f && thisLocation.y > 0 &&
                player.getPlayerData().getCurrentFuel() >= player.getPlayerData().getAirblastCost()) {
            player.getMouse().setDesiredLocation((
                    predictedSelfLocation.x - thisLocation.x) * PPM,(predictedSelfLocation.y - thisLocation.y) * PPM);
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
    }

    private static final float searchRadius = 60.0f;
    @Override
    public Array<RallyPoint.RallyPointMultiplier> getTargetPoints(Vector2 playerLocation, float multiplier) {
        Array<RallyPoint.RallyPointMultiplier> targetPoints = super.getTargetPoints(playerLocation,
                1.0f + player.getViolenceDesireMultiplier());
        if (shootTarget != null) {
            if (!shootTarget.equals(lastShootTarget)) {
                player.resetWeaponWobble();
            }
        }
        return targetPoints;
    }

    @Override
    public Array<RallyPoint.RallyPointMultiplier> getEventPoints(Vector2 playerLocation) {
        return player.getState().getMode().processAIPath(player.getState(), player, playerLocation);
    }

    private static final int weaponThreshold1 = 10;
    private static final int weaponThreshold2 = 20;
    private static final int weaponThreshold3 = 25;
    private static final float weaponMultiplier1 = 0.25f;
    private static final float weaponMultiplier2 = 3.0f;
    @Override
    public RallyPoint.RallyPointMultiplier getWeaponPoint(Vector2 playerLocation) {
        int totalAffinity = 0;
        int minAffinity = 100;
        for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getPlayerData().getMultitools()[i]);
            totalAffinity += affinity;
            minAffinity = Math.min(minAffinity, affinity);
        }
        RallyPoint weaponPoint = null;
        float weaponDesireMultiplier = 1.0f;
        if (totalAffinity < weaponThreshold3) {
            weaponPoint = BotLoadoutProcessor.getPointNearWeapon(player, playerLocation, searchRadius, minAffinity);

            //bots desire weapons more if they are not content with their current loadout and less if they are
            if (weaponPoint != null) {
                if (totalAffinity < weaponThreshold1) {
                    weaponDesireMultiplier = weaponMultiplier1;
                }
                if (totalAffinity > weaponThreshold2) {
                    weaponDesireMultiplier = weaponMultiplier2;
                }
            }
            weaponDesireMultiplier *= (1.0f + player.getWeaponDesireMultiplier());
        }
        return new RallyPoint.RallyPointMultiplier(weaponPoint, weaponDesireMultiplier);
    }

    private static final float healthThreshold1 = 0.9f;
    private static final float healthMultiplier1 = 0.25f;
    private static final float healthMultiplier2 = 8.0f;
    @Override
    public RallyPoint.RallyPointMultiplier getHealthPoint(Vector2 playerLocation) {
        RallyPoint healthPoint = null;
        float healthDesireMultiplier = 1.0f;

        //bot's health desire scales to how hurt the bot is
        float healthPercent = player.getPlayerData().getCurrentHp() / player.getPlayerData().getStat(Stats.MAX_HP);
        if (healthPercent < healthThreshold1) {
            healthPoint = BotLoadoutProcessor.getPointNearHealth(player, playerLocation, searchRadius);

            healthDesireMultiplier *= (healthMultiplier2 * healthPercent + healthMultiplier1 * (1.0f - healthPercent));
            healthDesireMultiplier *= (1.0f + player.getHealthDesireMultiplier());
        }
        return new RallyPoint.RallyPointMultiplier(healthPoint, healthDesireMultiplier);
    }

    public float getShootReleaseCount() { return shootReleaseCount; }

    public void setShootReleaseCount(float shootReleaseCount) { this.shootReleaseCount = shootReleaseCount; }

    public void setWeaponTarget(HadalEntity weaponTarget) { this.weaponTarget = weaponTarget; }

    public void setHealthTarget(HadalEntity healthTarget) { this.healthTarget = healthTarget; }
}