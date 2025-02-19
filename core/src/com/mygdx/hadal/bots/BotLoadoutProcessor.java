package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.map.SettingArcade;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.statuses.FiringWeapon;

import java.util.Arrays;
import java.util.Objects;

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
                UnlockEquip.getRandWeapFromPool(state, "")};
        botLoadout.artifacts = getRandomArtifacts(state);
        botLoadout.character = UnlockCharacter.getRandCharFromPool();
        botLoadout.activeItem = getRandomActiveItem();
        botLoadout.team = AlignmentFilter.getRandomColor();
        botLoadout.cosmetics = getRandomCosmetics(botLoadout.character);
        return botLoadout;
    }

    /**
     * This calculates a path towards a weapon pickup event
     * @param player: the bot player looking for a weapon
     * @param playerLocation: the location of the bot player
     * @param searchRadius: this is the max distance that the bot will search search for pickups
     * @param minAffinity: the lowest affinity of a weapon the bot is holding
     * @return a reasonably short path to a desired weapon pickup event
     */
    public static RallyPoint.RallyPointMultiplier getPointNearWeapon(PlayerBot player, Vector2 playerLocation, float searchRadius, int minAffinity) {
        final RallyPoint[] bestPoint = new RallyPoint[1];
        final HadalEntity[] bestTarget = new HadalEntity[1];
        player.getWorld().QueryAABB((fixture -> {
            if (fixture.getUserData() instanceof EventData eventData) {
                if (eventData.getEvent() instanceof PickupEquip pickup) {

                    //for all pickups found, calculate a path to it, if the bot wants it more than any of their current weapons
                    if (calcWeaponAffinity(pickup.getEquip()) > minAffinity) {
                        RallyPoint tempPoint = BotManager.getNearestPoint(player, pickup.getPosition());

                        //tentatively, we stop immediately upon finding an appropriate pickup to path towards
                        if (tempPoint != null) {
                            bestPoint[0] = tempPoint;
                            bestTarget[0] = pickup;
                            return false;
                        }
                    }
                }
            }
            return true;
        }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
            playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        return new RallyPoint.RallyPointMultiplier(bestPoint[0], bestTarget[0], 0.0f);
    }

    /**
     * This calculates a path towards a healing entity
     * @param player: the bot player looking for a heal
     * @param playerLocation: the location of the bot player
     * @param searchRadius: this is the max distance that the bot will search search for pickups
     * @return a reasonably short path to a desired healing event
     */
    public static RallyPoint.RallyPointMultiplier getPointNearHealth(PlayerBot player, Vector2 playerLocation, float searchRadius) {
        final RallyPoint[] bestPoint = new RallyPoint[1];
        final HadalEntity[] bestTarget = new HadalEntity[1];
        player.getWorld().QueryAABB((fixture -> {
                if (fixture.getUserData() instanceof final HadalData data) {
                    if (data.getEntity().isBotHealthPickup()) {

                        //for all health found, calculate a path to it
                        RallyPoint tempPoint = BotManager.getNearestPoint(player, data.getEntity().getPosition());

                        //tentatively, we stop immediately upon finding an appropriate pickup to path towards
                        if (tempPoint != null) {
                            bestPoint[0] = tempPoint;
                            bestTarget[0] = data.getEntity();
                            return false;
                        }
                    }
                }
                return true;
            }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
            playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        return new RallyPoint.RallyPointMultiplier(bestPoint[0], bestTarget[0], 0.0f);
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
        for (int i = 0; i < player.getEquipHelper().getMultitools().length - 1; i++) {
            int affinity = BotLoadoutProcessor.calcWeaponAffinity(player.getEquipHelper().getMultitools()[i]);
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

    /**
     * This is run prior to the player aiming to determine whether they should switch weapons
     * @param player: the bot player doing the weapon switching
     * @param playerLocation: the location of the bot player
     * @param targetLocation: the location of the target they are attacking
     * @param targetAlive: Is the target alive?
     */
    public static void processWeaponSwitching(PlayerBot player, Vector2 playerLocation, Vector2 targetLocation, boolean targetAlive) {
        float distSquared = playerLocation.dst2(targetLocation);

        //calculate "suitability" of currently held weapon. We do this first to favor staying on the same slot in case of ties
        int bestSlot = player.getEquipHelper().getCurrentSlot();

        //find which held weapon has the highest "suitability" based on distance from a living enemy
        if (BotManager.raycastUtility(player, playerLocation, targetLocation, BodyConstants.BIT_PROJECTILE) == 1.0f && targetAlive
                && Math.abs(playerLocation.x - targetLocation.x) < player.getVisionX()
                && Math.abs(playerLocation.y - targetLocation.y) <  player.getVisionY()) {
            float bestSuitability = BotLoadoutProcessor.calcWeaponSuitability(player,
                    player.getEquipHelper().getMultitools()[bestSlot], distSquared, 0);
            for (int i = 0; i < player.getEquipHelper().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponSuitability(player,
                        player.getEquipHelper().getMultitools()[i], distSquared, bestSuitability);
                if (suitability > bestSuitability) {
                    bestSuitability = suitability;
                    bestSlot = i;
                }
            }
        } else {
            //if the target is not alive or we don't have a clear line of sight, instead switch to most "proactive" weapon
            float bestProactivity = BotLoadoutProcessor.calcWeaponProactivity(player.getEquipHelper().getMultitools()[bestSlot]);
            for (int i = 0; i < player.getEquipHelper().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponProactivity(player.getEquipHelper().getMultitools()[i]);
                if (suitability > bestProactivity) {
                    bestProactivity = suitability;
                    bestSlot = i;
                }
            }

            //this marks the bot as not having vision to prevent them from continuing to try and attack targets through walls
            player.getBotController().setDistanceFromTarget(false, false, -1, -1);
        }
        BotLoadoutProcessor.switchToSlot(player, bestSlot);
    }

    private static final Vector2 mousePosition = new Vector2();
    private static final Vector2 mouseTarget = new Vector2();
    /**
     * This is run prior to the player attacking to determine where they should position their mouse
     * @param player: the bot player doing the weapon switching
     * @param targetLocation: the location of the target they are attacking
     * @param targetVelocity: the velocity of the target they are attacking. Used to lead shots.
     * @param weapon: the weapon the bot is aiming with
     */
    public static void processWeaponAim(PlayerBot player, Vector2 targetLocation, Vector2 targetVelocity,
                                        Equippable weapon, boolean targetFound) {

        //this makes blind disable the bot's ability to adjust aim
        if (player.getBlinded() > Blinded.BOT_BLIND_THRESHOLD) { return; }

        //atm, the only weapon with different aiming logic is the cola-cannon, which must be shaken when uncharged
        if (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass())) == UnlockEquip.COLACANNON) {
            if (weapon.getChargeCd() >= weapon.getChargeTime() || weapon.isReloading()) {
                mouseTarget.set(BotManager.acquireAimTarget(player, player.getPosition(),
                        targetLocation, targetVelocity, ((RangedWeapon) weapon).getProjectileSpeed(), targetFound));
            } else {
                player.weaponWobble();
                mouseTarget.set(targetLocation).add(player.getWeaponWobble());
            }
        } else {
            //default behavior: acquire target's predicted position
            if (weapon instanceof RangedWeapon ranged) {
                mouseTarget.set(BotManager.acquireAimTarget(player, player.getPosition(),
                        targetLocation, targetVelocity, ranged.getProjectileSpeed(), targetFound));
            } else {
                mouseTarget.set(targetLocation);
            }
        }

        //bot's mouse lerps towards the predicted position
        mousePosition.set(player.getMouseHelper().getPosition());

        mousePosition.x = mousePosition.x + (mouseTarget.x - mousePosition.x) * player.getMouseAimSpeed();
        mousePosition.y = mousePosition.y + (mouseTarget.y - mousePosition.y) * player.getMouseAimSpeed();

        player.getMouseHelper().setDesiredLocation(mousePosition.x, mousePosition.y);
    }

    /**
     * This processes the bot shooting their weapon and has different logic depending on the type of weapon fired
     * @param player: the bot player doing the attacking
     * @param weapon: the weapon the bot is using to attack
     * @param shooting: whether the bot has a clear shot on the target or not
     */
    public static void processWeaponShooting(PlayerBot player, Equippable weapon, boolean shooting) {
        switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {
            case BANANA, BATTERING_RAM, CHARGE_BEAM, FLOUNDERBUSS, LOVE_BOW, MAGIC_BEANSTALKER, MIDNIGHT_POOL_CUE:
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
                holdDelayRelease(player, shooting, TRICK_GUN_DELAY);
                break;
            case TESLA_COIL:
                //the same hold-delay-release is used here but with a moderate delay to make the coils tend to be further apart
                holdDelayRelease(player, shooting, DEFAULT_LONG_DELAY);
                break;
            case PEARL_REVOLVER:
                //the same hold-delay-release is used here but with a small delay to make the bot rapid-fire
                holdDelayRelease(player, shooting, DEFAULT_SHORT_DELAY);
                break;
            case COLACANNON:
                //The cola cannon is fired like a normal charge weapon, except we want to release after firing
                //additionally, we don't want to hold to charge it
                if (shooting) {
                    if (weapon.getChargeCd() >= weapon.getChargeTime()) {
                        holdDelayRelease(player, true, DEFAULT_SHORT_DELAY);
                    } else {
                        player.getController().keyUp(PlayerAction.FIRE);
                    }
                } else {
                    player.getController().keyUp(PlayerAction.FIRE);
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
     * @param distSquared: the distance squared between the bot and its target
     * @param bestSuitability: the best suitability outside of this weapon. If this weapon is bettwe, adjust bot's distance
     * @return the weapon's "suitability"
     */
    private static int calcWeaponSuitability(PlayerBot player, Equippable weapon, float distSquared, float bestSuitability) {

        //suitability is determined by weapon's range compared to distance
        float maxRange = Math.min(player.getVisionX(), weapon.getBotRangeMax());
        float minSquared = weapon.getBotRangeMin() * weapon.getBotRangeMin();
        float maxSquared = maxRange * maxRange;
        float midRange = (maxRange + minSquared) / 2;

        boolean inRange = distSquared > minSquared && distSquared < maxSquared;

        int suitability = 0;
        //out of ammo weapons are never suitable
        if (weapon instanceof final RangedWeapon ranged) {
            switch (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass()))) {

                //spray-type weapons are suitable when firing to avoid switching immediately after gaining firing status
                case COLACANNON, SLODGE_NOZZLE, STUTTERGUN:
                    if (ranged.getClipLeft() == 0 && player.getPlayerData().getStatus(FiringWeapon.class) == null) {
                        suitability =  inRange ? 1 : 0;
                    }
                    break;
                default:
                    if (ranged.getClipLeft() == 0) {
                        suitability =  inRange ? 1 : 0;
                    }
                    break;
            }
        }

        if (inRange) {
            suitability = 10;
        }

        //use calculated mid-range to set bot's distance parameters to control their micro-movement
        if (suitability > bestSuitability) {
            player.getBotController().setDistanceFromTarget(true, inRange,
                    distSquared - midRange * midRange, distSquared);
        }
        return suitability;
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
            case BANANA, BATTERING_RAM, CHARGE_BEAM, COLACANNON, FLOUNDERBUSS, LOVE_BOW, MAGIC_BEANSTALKER, MIDNIGHT_POOL_CUE -> 5;
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
        if (slot != player.getEquipHelper().getCurrentSlot()) {
            PlayerAction slotToSwitch = switch (slot) {
                default -> PlayerAction.SWITCH_TO_1;
                case 1 -> PlayerAction.SWITCH_TO_2;
                case 2 -> PlayerAction.SWITCH_TO_3;
            };
            player.getController().keyDown(slotToSwitch);
            player.getController().keyUp(slotToSwitch);
        }
    }

    private static final float TRICK_GUN_DELAY = 0.75f;
    private static final float DEFAULT_SHORT_DELAY = 0.2f;
    private static final float DEFAULT_LONG_DELAY = 0.6f;
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

    private static final float SMELTER_DELAY = 1.25f;
    private static final float SMELTER_THRESHOLD = 0.85f;
    /**
     * This makes a bot hold a weapon until its close to overheating, before they release and wait
     * Only used for the deep sea smelter weapon
     */
    private static void preventOverheat(PlayerBot player, Equippable weapon, boolean shooting) {
        if (shooting) {
            if (weapon.getChargeCd() >= weapon.getChargeTime() * SMELTER_THRESHOLD) {
                player.getBotController().setShootReleaseCount(SMELTER_DELAY);
                player.getController().keyUp(PlayerAction.FIRE);
            } else if (player.getBotController().getShootReleaseCount() <= 0.0f){
                player.getController().keyDown(PlayerAction.FIRE);
            }
        } else {
            player.getController().keyUp(PlayerAction.FIRE);
        }
    }

    private static final float KILLER_BEAT_THRESHOLD = 0.9f;
    /**
     * This makes a bot wait until a weapon is almost fully charged and then fires
     * Only used for the killer beat weapon
     */
    private static void timedFire(PlayerBot player, Equippable weapon, boolean shooting) {
        if (shooting && weapon.getChargeCd() >= weapon.getChargeTime() * KILLER_BEAT_THRESHOLD) {
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

    private static final float BONUS_SUPPLY_DROP_CHANCE = 0.4f;
    private static final UnlockActives[] botItems = { UnlockActives.ANCHOR_SMASH, UnlockActives.FLASH_BANG,
            UnlockActives.HEALING_FIELD, UnlockActives.HONEYCOMB, UnlockActives.JUMP_KICK, UnlockActives.MARINE_SNOWGLOBE,
            UnlockActives.METEOR_STRIKE, UnlockActives.MELON,  UnlockActives.MISSILE_POD, UnlockActives.NAUTICAL_MINE,
            UnlockActives.ORBITAL_SHIELD, UnlockActives.PLUS_MINUS, UnlockActives.PROXIMITY_MINE, UnlockActives.SPIRIT_RELEASE,
            UnlockActives.TAINTED_WATER};
    /**
     * This returns a random active item out of the items available to bots.
     * Supply drop has an increased chance of spawning
     */
    public static UnlockActives getRandomActiveItem() {
        if (MathUtils.random() <= BONUS_SUPPLY_DROP_CHANCE) {
            return UnlockActives.SUPPLY_DROP;
        } else {
            return botItems[MathUtils.random(botItems.length - 1)];
        }
    }

    private static final int ARCADE_MAX_BOT_SLOTS = 6;

    private static final UnlockArtifact[] mobility2 = { UnlockArtifact.CASTAWAYS_TRAVELOGUE, UnlockArtifact.FENS_CLIPPED_WINGS,
            UnlockArtifact.MOON_FLUTHER, UnlockArtifact.NICE_SHOES, UnlockArtifact.VOID_HYPONOME };
    private static final UnlockArtifact[] mobility1 = { UnlockArtifact.CURSED_CILICE, UnlockArtifact.NACREOUS_RUDDER };
    private static final UnlockArtifact[] defensive3 = { UnlockArtifact.FALLACY_OF_FLESH, UnlockArtifact.HORNS_OF_AMMON };
    private static final UnlockArtifact[] defensive2 = { UnlockArtifact.BLASTEMA, UnlockArtifact.FARADAYS_CAGE, UnlockArtifact.FINIFUGALITY,
            UnlockArtifact.FRACTURE_PLATE,
            UnlockArtifact.GLUTTONOUS_GREY_GLOVE, UnlockArtifact.GOOD_HEALTH, UnlockArtifact.LOCH_SHIELD, UnlockArtifact.VISE_OF_SHAME };
    private static final UnlockArtifact[] defensive1 = { UnlockArtifact.NUMBER_ONE_BOSS_MUG, UnlockArtifact.DAS_BOOT,
            UnlockArtifact.DAY_AT_THE_FAIR, UnlockArtifact.GEMMULE, UnlockArtifact.KUMQUAT, UnlockArtifact.LOTUS_LANTERN,
            UnlockArtifact.MANGROVE_SEED,
            UnlockArtifact.MOUTHBREATHER_CERTIFICATE, UnlockArtifact.NOCTILUCENT_PROMISE, UnlockArtifact.NUTRILOG_CRUNCHBAR_PLUS,
            UnlockArtifact.SALIGRAM, UnlockArtifact.TUNICATE_TUNIC};
    private static final UnlockArtifact[] offensive3 = { UnlockArtifact.BUCKET_OF_BATTERIES, UnlockArtifact.EMAUDELINES_PRISM, UnlockArtifact.JURY_RIGGED_BINDINGS };
    private static final UnlockArtifact[] offensive2 = { UnlockArtifact.BOOK_OF_BURIAL, UnlockArtifact.BRITTLING_POWDER, UnlockArtifact.CHAOS_CONJURANT,
            UnlockArtifact.CLOCKWISE_CAGE, UnlockArtifact.CONCEPT13, UnlockArtifact.CRIME_DISCOURAGEMENT_STICK, UnlockArtifact.ERSATZ_SMILE,
            UnlockArtifact.GOMEZS_AMYGDALA, UnlockArtifact.HONEYED_TENEBRAE, UnlockArtifact.PEER_PRESSURE, UnlockArtifact.ROYAL_JUJUBE_BANG,
            UnlockArtifact.SHILLERS_DEATHCAP, UnlockArtifact.TRIGGERFISH_FINGER, UnlockArtifact.TYPHON_FANG,
            UnlockArtifact.VESTIGIAL_CHAMBER, UnlockArtifact.VOLATILE_DERMIS, UnlockArtifact.WHITE_WHALE_CHARM,
            UnlockArtifact.WRATH_OF_THE_FROGMAN };
    private static final UnlockArtifact[] offensive1 = { UnlockArtifact.EIGHT_BALL, UnlockArtifact.ABYSSAL_INSIGNIA, UnlockArtifact.BATTLE_BUOY,
            UnlockArtifact.CALL_OF_THE_VOID, UnlockArtifact.CONTEMPT_FOR_LIFE, UnlockArtifact.CROWN_OF_THORNS, UnlockArtifact.FORAGERS_HIVE, UnlockArtifact.HEARTSNATCHER,
            UnlockArtifact.IRON_SIGHTS, UnlockArtifact.KERMUNGLER,
            UnlockArtifact.MOUTHFUL_OF_BEES, UnlockArtifact.NUCLEAR_PUNCH_THRUSTERS, UnlockArtifact.NURDLER, UnlockArtifact.PEACHWOOD_SWORD,
            UnlockArtifact.PEPPER, UnlockArtifact.PETRIFIED_PAYLOAD, UnlockArtifact.RED_TIDE_TALISMAN, UnlockArtifact.SAMURAI_SHARK,
            UnlockArtifact.SWORD_OF_SYZYGY, UnlockArtifact.VOW_OF_EMPTY_HANDS };
    private static final UnlockArtifact[] misc3 = { UnlockArtifact.AU_COURANT, UnlockArtifact.CARLOCS_THESIS, UnlockArtifact.HEART_OF_SPEROS,
            UnlockArtifact.INFORMANTS_TIE, UnlockArtifact.KINESIS_LENS, UnlockArtifact.TENUOUS_GRIP_ON_REALITY };
    private static final UnlockArtifact[] misc2 = { UnlockArtifact.CURIOUS_SAUCE, UnlockArtifact.EXTRA_ROW_OF_TEETH, UnlockArtifact.ICE9 };
    private static final UnlockArtifact[] misc1 = { UnlockArtifact.ANARCHISTS_COOKBOOK, UnlockArtifact.BROOCH_OF_BETTER_DAYS,
            UnlockArtifact.BUTTONMAN_BUTTONS, UnlockArtifact.ICE9,
            UnlockArtifact.QUALIA_UMBILICA, UnlockArtifact.SINKING_FEELING, UnlockArtifact.SUMMONING_TWOFISH};

    /**
     * This gives a bot a set of random artifacts from a curated list, obeying artifact slot restrictions
     */
    public static UnlockArtifact[] getRandomArtifacts(PlayState state) {
        UnlockArtifact[] artifacts = new UnlockArtifact[Loadout.MAX_ARTIFACT_SLOTS];
        Arrays.fill(artifacts, UnlockArtifact.NOTHING);

        //easy bots or bots in single player when the player has no artifacts do not use artifacts
        if (BotPersonality.BotDifficulty.EASY.equals(state.getMode().getBotDifficulty()) ||
                (HadalGame.usm.getOwnUser() != null && HadalGame.usm.getOwnUser().getLoadoutManager().getSavedLoadout().getArtifactSlotsUsed() == 0)) {
            return artifacts;
        }

        Array<UnlockArtifact> artifactOptions = new Array<>();

        int slots;
        if (SettingArcade.arcade) {
            slots = Math.min(ARCADE_MAX_BOT_SLOTS, SettingArcade.currentRound);
        } else {
            slots = JSONManager.setting.getArtifactSlots();
        }
        int currentSlot = 0;
        boolean mobilityFound = 1 == slots;

        while (slots > 0) {
            artifactOptions.clear();

            //if there is >1 slot available, bots will prioritize having at least 1 mobility item
            if (!mobilityFound) {
                mobilityFound = true;
                artifactOptions.addAll(mobility2);
                artifactOptions.addAll(mobility1);
            } else {
                if (slots >= 3) {
                    artifactOptions.addAll(defensive3);
                    artifactOptions.addAll(offensive3);
                    artifactOptions.addAll(misc3);
                }
                if (slots >= 2) {
                    artifactOptions.addAll(mobility2);
                    artifactOptions.addAll(defensive2);
                    artifactOptions.addAll(offensive2);
                    artifactOptions.addAll(misc2);
                }
                artifactOptions.addAll(mobility1);
                artifactOptions.addAll(defensive1);
                artifactOptions.addAll(offensive1);
                artifactOptions.addAll(misc1);
            }

            if (artifactOptions.size > 0 && currentSlot < artifacts.length) {
                UnlockArtifact newArtifact = artifactOptions.get(MathUtils.random(artifactOptions.size - 1));
                boolean artifactUnique = true;

                //we want to avoid trying to equip the same artifact multiple times
                for (UnlockArtifact artifact : artifacts) {
                    if (newArtifact == artifact) {
                        artifactUnique = false;
                        break;
                    }
                }

                if (artifactUnique) {
                    artifacts[currentSlot] = artifactOptions.get(MathUtils.random(artifactOptions.size - 1));
                    slots -= artifacts[currentSlot].getArtifact().getSlotCost();
                    currentSlot++;
                }
            }
        }

        return artifacts;
    }

    private static final int DEFAULT_NOTHING_WEIGHT = 20;
    /**
     * This applies random cosmetics to the newly created bot
     */
    public static UnlockCosmetic[] getRandomCosmetics(UnlockCharacter character) {
        UnlockCosmetic[] cosmetics = new UnlockCosmetic[Loadout.MAX_COSMETIC_SLOTS];
        Arrays.fill(cosmetics, UnlockCosmetic.NOTHING_HAT1);

        //iterate through all cosmetic slots and for each, add all applicable cosmetics to a list, then choose one randomly
        int index = 0;
        for (CosmeticSlot slot : CosmeticSlot.values()) {
            Array<UnlockCosmetic> cosmeticOptions = new Array<>();
            for (UnlockCosmetic cosmetic : UnlockCosmetic.values()) {
                if (cosmetic.getCosmeticSlot().equals(slot)) {
                    if (cosmetic.getCosmetics().containsKey(character)) {
                        for (int i = 0; i < cosmetic.getCosmetics().get(character).getBotRandomWeight(); i++) {
                            cosmeticOptions.add(cosmetic);
                        }
                    } else if (cosmetic.isBlank()) {
                        for (int i = 0; i < DEFAULT_NOTHING_WEIGHT; i++) {
                            cosmeticOptions.add(cosmetic);
                        }
                    }
                }

            }
            if (cosmeticOptions.size > 0) {
                cosmetics[index] = cosmeticOptions.get(MathUtils.random(cosmeticOptions.size - 1));
            }
            index++;
        }
        return cosmetics;
    }

    private static final float HEAL_THRESHOLD = 0.8f;
    /**
     * This processes the bot's active item
     * @param player: the bot using the active item
     * @param weapon: the item being used
     * @param shooting: whether the bot's target is in sight or not
     */
    public static void processActiveItem(PlayerBot player, ActiveItem weapon, boolean shooting, float distanceSquared) {
        switch (Objects.requireNonNull(UnlockActives.getUnlockFromActive(weapon.getClass()))) {
            //bots will use healing items when below a threshold of hp
            case HEALING_FIELD, MELON:
                if (player.getPlayerData().getCurrentHp() < player.getPlayerData().getStat(Stats.MAX_HP) * HEAL_THRESHOLD
                        && weapon.isUsable()) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
            //some active items are used immediately when ready
            case SUPPLY_DROP, PROXIMITY_MINE:
                if (weapon.isUsable()) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
            //active items that require being withing range of enemies
            case JUMP_KICK, MARINE_SNOWGLOBE, ORBITAL_SHIELD, PLUS_MINUS, TAINTED_WATER:
                if (weapon.isUsable() && distanceSquared > 0.0f &&
                        weapon.getBotRangeMin() * weapon.getBotRangeMin() > distanceSquared) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
            default:
                if (shooting && weapon.isUsable()) {
                    player.getController().keyDown(PlayerAction.ACTIVE_ITEM);
                } else {
                    player.getController().keyUp(PlayerAction.ACTIVE_ITEM);
                }
                break;
        }
    }
}
