package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;

import java.util.Objects;

public class BotLoadoutProcessor {

    public static RallyPath getPathToWeapon(World world, PlayerBot player, Vector2 playerLocation, Vector2 playerVelocity,
                                            float searchRadius, int minAffinity) {
        final RallyPath[] bestPath = new RallyPath[1];
        world.QueryAABB((fixture -> {
            if (fixture.getUserData() instanceof final EventData eventData) {
                if (eventData.getEvent() instanceof final PickupEquip pickup) {
                    if (calcWeaponAffinity(player, pickup.getEquip()) > minAffinity) {
                        RallyPath tempPath = BotManager.getShortestPathBetweenLocations(world, playerLocation,
                                pickup.getPosition(), playerVelocity);
                        if (tempPath != null) {
                            if (bestPath[0] != null) {
                                if (tempPath.getDistance() < bestPath[0].getDistance()) {
                                    bestPath[0] = tempPath;
                                }
                            } else {
                                bestPath[0] = tempPath;
                            }
                        }
                    }
                }
            }
            return true;
        }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
            playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        return bestPath[0];
    }

    public static void processWeaponShooting(PlayerBot player, Equippable weapon, boolean shooting) {
        switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case BANANA, BATTERING_RAM, CHARGE_BEAM, FLOUNDERBUSS, LOVE_BOW, VINE_SOWER:
                if (shooting) {
                    if (weapon.getChargeCd() >= weapon.getChargeTime()) {
                        player.getController().keyUp(PlayerAction.FIRE);
                    } else {
                        player.getController().keyDown(PlayerAction.FIRE);
                    }
                } else {
                    player.getController().keyDown(PlayerAction.FIRE);
                }
                break;
            case TRICK_GUN:
                holdDelayRelease(player, shooting, trickGunDelay);
                break;
            case PEARL_REVOLVER:
                holdDelayRelease(player, shooting, revolverDelay);
                break;
            case DEEP_SEA_SMELTER:
                preventOverheat(player, weapon, shooting);
                break;
            case KILLER_BEAT:
                timedFire(player, weapon, shooting);
                break;
            default:
                if (shooting) {
                    player.getController().keyDown(PlayerAction.FIRE);
                } else {
                    player.getController().keyUp(PlayerAction.FIRE);
                }
                break;
        }
    }

    public static int calcWeaponAffinity(PlayerBot player, Equippable weapon) {

        if (weapon instanceof final RangedWeapon ranged) {
            if (ranged.isOutofAmmo()) {
                return 0;
            }
        }
        return switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case NOTHING -> 0;
            case SPEARGUN, SPEARGUN_NERFED -> 1;
            default -> 10;
        };
    }

    private static final float trickGunDelay = 0.75f;
    private static final float revolverDelay = 0.2f;
    private static void holdDelayRelease(PlayerBot player, boolean shooting, float delay) {
        if (shooting) {
            if (player.getBotController().getShootReleaseCount() <= 0.0f) {
                player.getBotController().setShootReleaseCount(delay);
                player.getController().keyDown(PlayerAction.FIRE);
            }
        } else {
            player.getController().keyUp(PlayerAction.FIRE);
        }
    }

    private static final float smelterDelay = 1.25f;
    private static final float smelterThreshold = 0.85f;
    private static void preventOverheat(PlayerBot player, Equippable weapon, boolean shooting) {
        if (shooting) {
            if (weapon.getChargeCd() >= weapon.getChargeTime() * smelterThreshold) {
                player.getBotController().setShootReleaseCount(smelterDelay);
                player.getController().keyUp(PlayerAction.FIRE);
            } else if (player.getBotController().getShootReleaseCount() <= 0.0f){
                player.getController().keyDown(PlayerAction.FIRE);
            }
        } else {
            player.getController().keyUp(PlayerAction.FIRE);
        }
    }

    private static final float killerBeatThreshold = 0.9f;
    private static void timedFire(PlayerBot player, Equippable weapon, boolean shooting) {
        if (shooting && weapon.getChargeCd() >= weapon.getChargeTime() * killerBeatThreshold) {
            player.getController().keyDown(PlayerAction.FIRE);
        } else {
            player.getController().keyUp(PlayerAction.FIRE);
        }
    }
}
