package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.utils.Stats;

import java.util.Objects;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * BotLoadoutProcessor contains various utility methods for bot players.
 * Most of these methods pertain to loadout management and item usage
 * @author Mernard Mawplord
 */
public class BotLoadoutProcessor {

    /**
     * This generates the bot's loadout with (mostly) randomized item choices
     */
    public static Loadout getBotLoadout(PlayState state) {
        Loadout botLoadout = new Loadout();

        botLoadout.multitools = new UnlockEquip[]{
                UnlockEquip.getRandWeapFromPool(state, ""),
                UnlockEquip.getRandWeapFromPool(state, ""),
                UnlockEquip.getRandWeapFromPool(state, ""),
                UnlockEquip.getRandWeapFromPool(state, "") };
        botLoadout.artifacts = new UnlockArtifact[]{ UnlockArtifact.MOON_FLUTHER, UnlockArtifact.GOOD_HEALTH, UnlockArtifact.NOTHING,  UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING,};
        botLoadout.character = UnlockCharacter.getRandCharFromPool(state);
        botLoadout.activeItem = getRandomActiveItem();
        botLoadout.character = UnlockCharacter.getRandCharFromPool(state);
        botLoadout.team = AlignmentFilter.getRandomColor();
        return botLoadout;
    }

    /**
     * This calculates a path towards a weapon pickup event
     * @param world: the current game world
     * @param player: the bot player looking for a weapon
     * @param playerLocation: the location of the bot player
     * @param playerVelocity: the velocity of the bot player
     * @param searchRadius: this is the max distance that the bot will search search for pickups
     * @param minAffinity: the lowest affinity of a weapon the bot is holding
     * @return a reasonably short path to a desired weapon pickup event
     */
    public static RallyPath getPathToWeapon(World world, PlayerBot player, Vector2 playerLocation, Vector2 playerVelocity,
                                            float searchRadius, int minAffinity) {
        final RallyPath[] bestPath = new RallyPath[1];
        world.QueryAABB((fixture -> {
            if (fixture.getUserData() instanceof final EventData eventData) {
                if (eventData.getEvent() instanceof final PickupEquip pickup) {

                    //for all pickps found, calculate a path to it, if the bot wants it more than any of their current weapons
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

    /**
     * This is run by the bot to process whether it should pickup a weapon it is currently touching
     * @param player: the bot player picking up a weapon
     * @param pickup: the weapon they are considering picking up
     */
    public static void processWeaponPickup(PlayerBot player, PickupEquip pickup) {
        int worseSlot = 0;
        int minAffinity = 100;

        //calc affinity for each of the bot's currently held weapons and identify which is worst
        for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getPlayerData().getMultitools()[i]);
            if (affinity < minAffinity) {
                minAffinity = affinity;
                worseSlot = i;
            }
        }

        //if worse currently held weapon is worse than prospective pickup, switch to that slot and interact
        if (minAffinity < BotLoadoutProcessor.calcWeaponAffinity(pickup.getEquip())) {
            BotLoadoutProcessor.switchToSlot(player, worseSlot);
            player.getController().keyDown(PlayerAction.INTERACT);
            player.getController().keyUp(PlayerAction.INTERACT);
        }
    }

    private static final float botVisionX = 36.0f;
    private static final float botVisionY= 20.0f;
    /**
     * This is run prior to the player aiming to determine whether they should switch weapons
     * @param player: the bot player doing the weapon switching
     * @param playerLocation: the location of the bot player
     * @param targetLocation: the location of the target they are attacking
     * @param targetAlive: I the target alive?
     * @return boolean of whether the bot has a clear shot at their target
     */
    public static boolean processWeaponSwitching(PlayerBot player, Vector2 playerLocation, Vector2 targetLocation, boolean targetAlive) {
        boolean shooting = false;
        float distSquared = playerLocation.dst2(targetLocation);

        //calculate "suitability" of currently held weapon. We do this first to favor staying on the same slot in case of ties
        int bestSlot = player.getPlayerData().getCurrentSlot();

        System.out.println((playerLocation.x - targetLocation.x) + " " + (playerLocation.y - targetLocation.y));

        //find which held weapon has the highest "suitability" based on distance from a living enemy
        if (BotManager.raycastUtility(player.getWorld(), playerLocation, targetLocation) == 1.0f && targetAlive
                && Math.abs(playerLocation.x - targetLocation.x) < botVisionX
                && Math.abs(playerLocation.y - targetLocation.y) < botVisionY) {
            float bestSuitability = BotLoadoutProcessor.calcWeaponSuitability(player, player.getPlayerData().getMultitools()[bestSlot], distSquared);
            for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponSuitability(player, player.getPlayerData().getMultitools()[i], distSquared);
                if (suitability > bestSuitability) {
                    bestSuitability = suitability;
                    bestSlot = i;
                }
            }
            shooting = true;
        } else {
            //if the target is not alive or we don't have a clear line of sight, instead switch to most "proactive" weapon
            float bestProactivity = BotLoadoutProcessor.calcWeaponProactivity(player.getPlayerData().getMultitools()[bestSlot]);
            for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponProactivity(player.getPlayerData().getMultitools()[i]);
                if (suitability > bestProactivity) {
                    bestProactivity = suitability;
                    bestSlot = i;
                }
            }
        }
        BotLoadoutProcessor.switchToSlot(player, bestSlot);
        return shooting;
    }

    private static final Vector2 mousePosition = new Vector2();
    private static final Vector2 mouseTarget = new Vector2();
    private static final float aimInterpolation = 0.5f;
    /**
     * This is run prior to the player attacking to determine where they should position their mouse
     * @param player: the bot player doing the weapon switching
     * @param targetLocation: the location of the target they are attacking
     * @param targetVelocity: the velocity of the target they are attacking. Used to lead shots.
     * @param weapon: the weapon the bot is aiming with
     */
    public static void processWeaponAim(PlayerBot player, Vector2 targetLocation, Vector2 targetVelocity, Equippable weapon) {
        if (player.getPlayerData().getStatus(Blinded.class) != null) { return; }

        //atm, the only weapon with different aimiing logic is the cola-cannon, which must be shaken when uncharged
        if (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass())) == UnlockEquip.COLACANNON) {
            if (weapon.getChargeCd() >= weapon.getChargeTime() || weapon.isReloading()) {
                mouseTarget.set(BotManager.acquireAimTarget(player.getState().getWorld(), player.getPosition(),
                        targetLocation, targetVelocity, ((RangedWeapon) weapon).getProjectileSpeed()));
            } else {
                BotLoadoutProcessor.aimWobble(player);
                mouseTarget.set(targetLocation).add(player.getAimWobble());
            }
        } else {
            //default behavior: acquire target's predicted position
            if (weapon instanceof RangedWeapon ranged) {
                mouseTarget.set(BotManager.acquireAimTarget(player.getState().getWorld(), player.getPosition(),
                        targetLocation, targetVelocity, ranged.getProjectileSpeed()));
            } else {
                mouseTarget.set(targetLocation);
            }
        }
        mouseTarget.scl(PPM);
        mousePosition.set(player.getMouse().getPixelPosition());
        mousePosition.x = mousePosition.x + (mouseTarget.x - mousePosition.x) * aimInterpolation;
        mousePosition.y = mousePosition.y + (mouseTarget.y - mousePosition.y) * aimInterpolation;
        player.getMouse().setDesiredLocation(mousePosition.x, mousePosition.y);
    }

    /**
     * This processes the bot shooting their weapon and has different logic depending on the type of weapon fired
     * @param player: the bot player doing the attacking
     * @param weapon: the weapon the bot is using to attack
     * @param shooting: whether the bot has a clear shot on the target or not
     */
    public static void processWeaponShooting(PlayerBot player, Equippable weapon, boolean shooting) {
        switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case BANANA, BATTERING_RAM, CHARGE_BEAM, FLOUNDERBUSS, LOVE_BOW, VINE_SOWER:
                //when attacking, charge weapons should be held until charged when they are released
                if (shooting) {
                    if (weapon.getChargeCd() >= weapon.getChargeTime()) {
                        player.getController().keyUp(PlayerAction.FIRE);
                    } else {
                        player.getController().keyDown(PlayerAction.FIRE);
                    }
                } else {
                    //when no target is found, bots should preemptively charge their weapon
                    player.getController().keyDown(PlayerAction.FIRE);
                }
                break;
            case TRICK_GUN:
                //holding, and delaying release makes the bot kinda shoot projectiles that bend towards the player. kinda.
                holdDelayRelease(player, shooting, trickGunDelay);
                break;
            case PEARL_REVOLVER:
                //the same hold-delay-release is used here but with a small delay to make the bot rapid-fire
                holdDelayRelease(player, shooting, defaultShortDelay);
                break;
            case COLACANNON:
                //The cola cannon is fired like a normal charge weapon, except we want to release after firing
                //additionally, we don't want to hold to charge it
                if (shooting) {
                    if (weapon.getChargeCd() >= weapon.getChargeTime()) {
                        holdDelayRelease(player, true, defaultShortDelay);
                    }
                }
                break;
            case DEEP_SEA_SMELTER:
                preventOverheat(player, weapon, shooting);
                break;
            case KILLER_BEAT:
                timedFire(player, weapon, shooting);
                break;
            case STICKY_BOMB_LAUNCHER:
                manualReload(player, weapon, shooting);
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

    /**
     * This calculates a weapon's "affinity": how much a bot desires to pick this weapon up
     * @param weapon: the weapon we are finding affinity of
     * @return the weapon's "affinity"
     */
    public static int calcWeaponAffinity(Equippable weapon) {

        //out-of-ammo weapons are low affinity, so bots will prioritize switching them out
        if (weapon instanceof final RangedWeapon ranged) {
            if (ranged.isOutofAmmo()) {
                return 0;
            }
        }

        //this just makes some less desirable weapons lower affinity so bots are inclined to switch away from starting weapons
        return switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case NOTHING -> 0;
            case SPEARGUN, SPEARGUN_NERFED -> 1;
            default -> 10;
        };
    }

    /**
     * This calculates a weapon's "suitability": how much a bot wants to use this weapon when fighting
     * @param weapon: the weapon we are finding suitability of
     * @param distanceSquared: the distance squared between thte bot and its target
     * @return the weapon's "suitability"
     */
    private static int calcWeaponSuitability(PlayerBot player, Equippable weapon, float distanceSquared) {

        //out of ammo weapons are never suitable
        if (weapon instanceof final RangedWeapon ranged) {
            switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
                case COLACANNON, SLODGE_NOZZLE, STUTTERGUN:
                    if (ranged.getClipLeft() == 0 && player.getPlayerData().getStatus(FiringWeapon.class) == null) {
                        return 0;
                    }
                    break;
                default:
                    if (ranged.getClipLeft() == 0) {
                        return 0;
                    }
                    break;
            }
        }

        //suitability is determined by weapon's range compared to distance
        float minSquared = weapon.getBotRangeMin() * weapon.getBotRangeMin();
        float maxSquared = weapon.getBotRangeMax() * weapon.getBotRangeMax();

        if (distanceSquared < minSquared || distanceSquared > maxSquared) {
            return 0;
        }
        return 10;
    }

    /**
     * This calculates a weapon's "proactivity": how much a bot wants to use this weapon when not fighting
     * @param weapon: the weapon we are finding suitability of
     * @return the weapon's "proactivity"
     */
    private static int calcWeaponProactivity(Equippable weapon) {

        //weapons that are not at full clip are proactive because bots are inclined to reload them out of combat
        if (weapon instanceof final RangedWeapon ranged) {
            if (ranged.getClipLeft() != ranged.getClipSize()) {
                return 10;
            }
        }

        //charge weapons and similar weapons are more proaactive
        return switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case BANANA, BATTERING_RAM, CHARGE_BEAM, COLACANNON, FLOUNDERBUSS, LOVE_BOW, VINE_SOWER -> 5;
            case ASSAULT_BITS, DEEP_SEA_SMELTER -> 1;
            default -> 0;
        };
    }

    /**
     * This is a simple helper method that makes a bot switch to a specified slot
     * @param player: the bot switching weapons
     * @param slot: the slot to switch to
     */
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

    /**
     * This makes a bot hold a weapon for a set delay before releasing
     */
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
    /**
     * This makes a bot hold a weapon until its close to overhearing, before they release and wait
     * Only used for the deep sea smelter weapon
     */
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
    /**
     * This makes a bot wait until a weapon is almost fully charged and then fires
     * Only used for the killer beat weapon
     */
    private static void timedFire(PlayerBot player, Equippable weapon, boolean shooting) {
        if (shooting && weapon.getChargeCd() >= weapon.getChargeTime() * killerBeatThreshold) {
            player.getController().keyDown(PlayerAction.FIRE);
        } else {
            player.getController().keyUp(PlayerAction.FIRE);
        }
    }

    /**
     * This makes a bot manual reload when out of clip
     * Only used for stickybomb launcher
     */
    private static void manualReload(PlayerBot player, Equippable weapon, boolean shooting) {
        if (weapon.getClipLeft() == 0) {
            player.getController().keyDown(PlayerAction.RELOAD);
            player.getController().keyUp(PlayerAction.RELOAD);
        } else if (shooting) {
            player.getController().keyDown(PlayerAction.FIRE);
        } else {
            player.getController().keyUp(PlayerAction.FIRE);
        }
    }

    private static final float maxWobble = 10.0f;
    private static final float wobbleSpeed = 30.0f;
    /**
     * This makes a bot wobble their aim around in a circle around their target
     * Only used for the cola cannon
     */
    private static void aimWobble(PlayerBot player) {
        player.getAimWobble().nor().scl(maxWobble);
        player.getAimWobble().setAngleDeg(player.getAimWobble().angleDeg() + wobbleSpeed);
    }

    private static final float bonusSupplyDropChance = 0.4f;
    private static final UnlockActives[] botItems = { UnlockActives.ANCHOR_SMASH, UnlockActives.FLASH_BANG,
            UnlockActives.HONEYCOMB, UnlockActives.MISSILE_POD, UnlockActives.MELON, UnlockActives.MISSILE_POD,
            UnlockActives.NAUTICAL_MINE, UnlockActives.ORBITAL_SHIELD, UnlockActives.PROXIMITY_MINE, UnlockActives.SPIRIT_RELEASE};

    /**
     * This returns a random active item out of the items available to bots.
     * Supply drop has an increased chance of spawning
     */
    public static UnlockActives getRandomActiveItem() {
        if (MathUtils.random() <= bonusSupplyDropChance) {
            return UnlockActives.SUPPLY_DROP;
        } else {
            return botItems[MathUtils.random(botItems.length - 1)];
        }
    }

    private static final float healThreshold = 0.8f;

    /**
     * This processes the bot's active item
     * @param player: the bot using the active item
     * @param weapon: the item being used
     * @param shooting: whether the bot's target is in sight or not
     */
    public static void processActiveItem(PlayerBot player, ActiveItem weapon, boolean shooting) {
        switch (Objects.requireNonNull(UnlockActives.getUnlockFromActive(weapon.getClass()))) {
            case HEALING_FIELD, MELON:
                if (player.getPlayerData().getCurrentHp() < player.getPlayerData().getStat(Stats.MAX_HP) * healThreshold
                        && weapon.chargePercent() >= 1.0f) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
            case SUPPLY_DROP, PROXIMITY_MINE:
                if (weapon.chargePercent() >= 1.0f) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
            default:
                if (shooting && weapon.chargePercent() >= 1.0f) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
        }
    }
}
