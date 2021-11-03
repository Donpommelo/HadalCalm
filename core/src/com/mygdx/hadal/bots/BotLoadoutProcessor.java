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

import static com.mygdx.hadal.utils.Constants.PPM;

public class BotLoadoutProcessor {

    public static RallyPath getPathToWeapon(World world, PlayerBot player, Vector2 playerLocation, Vector2 playerVelocity,
                                            float searchRadius, int minAffinity) {
        final RallyPath[] bestPath = new RallyPath[1];
        world.QueryAABB((fixture -> {
            if (fixture.getUserData() instanceof final EventData eventData) {
                if (eventData.getEvent() instanceof final PickupEquip pickup) {
                    if (calcWeaponAffinity(pickup.getEquip()) > minAffinity) {
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

    public static void processWeaponPickup(PlayerBot player, PickupEquip pickup) {
        int worseSlot = 0;
        int minAffinity = 100;

        for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getPlayerData().getMultitools()[i]);
            if (affinity < minAffinity) {
                minAffinity = affinity;
                worseSlot = i;
            }
        }

        if (minAffinity < BotLoadoutProcessor.calcWeaponAffinity(pickup.getEquip())) {
            BotLoadoutProcessor.switchToSlot(player, worseSlot);
            player.getController().keyDown(PlayerAction.INTERACT);
            player.getController().keyUp(PlayerAction.INTERACT);
        }
    }

    public static boolean processWeaponSwitching(PlayerBot player, Vector2 playerLocation, Vector2 targetLocation) {
        boolean shooting = false;

        float distSquared = playerLocation.dst2(targetLocation);
        int bestSlot = player.getPlayerData().getCurrentSlot();
        float bestSuitability = BotLoadoutProcessor.calcWeaponSuitability(player.getPlayerData().getMultitools()[bestSlot], distSquared);

        if (BotManager.raycastUtility(player.getWorld(), playerLocation, targetLocation) == 1.0f) {
            for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponSuitability(player.getPlayerData().getMultitools()[i], distSquared);
                if (suitability > bestSuitability) {
                    bestSuitability = suitability;
                    bestSlot = i;
                }
            }
            shooting = true;
        } else {
            for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponProactivity(player.getPlayerData().getMultitools()[i]);
                if (suitability > bestSuitability) {
                    bestSuitability = suitability;
                    bestSlot = i;
                }
            }
        }

        BotLoadoutProcessor.switchToSlot(player, bestSlot);
        return shooting;
    }

    private static final Vector2 mouseTarget = new Vector2();
    public static void processWeaponAim(PlayerBot player, Vector2 targetLocation, Vector2 targetVelocity, Equippable weapon) {
        if (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass())) == UnlockEquip.COLACANNON) {
            if (weapon.getChargeCd() >= weapon.getChargeTime() || weapon.isReloading()) {
                mouseTarget.set(BotManager.acquireAimTarget(player.getPosition(), targetLocation, targetVelocity,
                        ((RangedWeapon) weapon).getProjectileSpeed()));
            } else {
                BotLoadoutProcessor.aimWobble(player);
                mouseTarget.set(targetLocation).add(player.getAimWobble());
            }
        } else {
            if (weapon instanceof RangedWeapon ranged) {
                mouseTarget.set(BotManager.acquireAimTarget(player.getPosition(), targetLocation, targetVelocity, ranged.getProjectileSpeed()));
            } else {
                mouseTarget.set(targetLocation);
            }
        }
        mouseTarget.scl(PPM);
        player.getMouse().setDesiredLocation(mouseTarget.x, mouseTarget.y);
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
            case COLACANNON:
                if (weapon.getChargeCd() >= weapon.getChargeTime()) {
                    holdDelayRelease(player, shooting, defaultShortDelay);
                }
                break;
            case PEARL_REVOLVER:
                holdDelayRelease(player, shooting, defaultShortDelay);
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

    public static int calcWeaponAffinity(Equippable weapon) {
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

    private static int calcWeaponSuitability(Equippable weapon, float distanceSquared) {
        if (weapon instanceof final RangedWeapon ranged) {
            if (ranged.getClipLeft() == 0) {
                return 0;
            }
        }
        float minSquared = (weapon.getBotRangeMin()) * (weapon.getBotRangeMin());
        float maxSquared = (weapon.getBotRangeMax()) * (weapon.getBotRangeMax());

        if (distanceSquared < minSquared || distanceSquared > maxSquared) {
            return 0;
        }
        return 10;
    }

    private static int calcWeaponProactivity(Equippable weapon) {
        if (weapon instanceof final RangedWeapon ranged) {
            if (ranged.getClipLeft() != ranged.getClipSize()) {
                return 10;
            }
        }
        return switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case BANANA, BATTERING_RAM, CHARGE_BEAM, COLACANNON, FLOUNDERBUSS, LOVE_BOW, VINE_SOWER -> 5;
            case ASSAULT_BITS, DEEP_SEA_SMELTER -> 1;
            default -> 0;
        };
    }

    private static void switchToSlot(PlayerBot player, int slot) {
        if (slot != player.getPlayerData().getCurrentSlot()) {
            PlayerAction slotToSwitch = switch (slot) {
                default -> PlayerAction.SWITCH_TO_1;
                case 1 -> PlayerAction.SWITCH_TO_2;
                case 2 -> PlayerAction.SWITCH_TO_3;
            };
            player.getController().keyDown(slotToSwitch);
            player.getController().keyUp(slotToSwitch);
        }
    }

    private static final float trickGunDelay = 0.75f;
    private static final float defaultShortDelay = 0.2f;
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

    private static final float maxWobble = 10.0f;
    private static final float wobbleSpeed = 30.0f;
    private static void aimWobble(PlayerBot player) {
        player.getAimWobble().nor().scl(maxWobble);
        player.getAimWobble().setAngleDeg(player.getAimWobble().angleDeg() + wobbleSpeed);
    }
}
