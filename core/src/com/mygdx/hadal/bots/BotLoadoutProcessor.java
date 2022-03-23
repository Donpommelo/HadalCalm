package com.mygdx.hadal.bots;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.event.PickupEquip;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.*;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.utils.Constants;
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
        botLoadout.artifacts = getRandomArtifacts(state);
        botLoadout.character = UnlockCharacter.getRandCharFromPool(state);
        botLoadout.activeItem = getRandomActiveItem();
        botLoadout.character = UnlockCharacter.getRandCharFromPool(state);
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
    public static RallyPoint getPointNearWeapon(PlayerBot player, Vector2 playerLocation, float searchRadius, int minAffinity) {
        final RallyPoint[] bestPoint = new RallyPoint[1];
        player.getWorld().QueryAABB((fixture -> {
            if (fixture.getUserData() instanceof final EventData eventData) {
                if (eventData.getEvent() instanceof final PickupEquip pickup) {

                    //for all pickups found, calculate a path to it, if the bot wants it more than any of their current weapons
                    if (calcWeaponAffinity(pickup.getEquip()) > minAffinity) {
                        RallyPoint tempPoint = BotManager.getNearestPoint(player, pickup.getPosition());

                        //tentatively, we stop immediately upon finding an appropriate pickup to path towards
                        if (tempPoint != null) {
                            player.getBotController().setWeaponTarget(pickup);
                            bestPoint[0] = tempPoint;
                            return false;
                        }
                    }
                }
            }
            return true;
        }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
            playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        return bestPoint[0];
    }

    /**
     * This calculates a path towards a healing event
     * @param player: the bot player looking for a heal
     * @param playerLocation: the location of the bot player
     * @param searchRadius: this is the max distance that the bot will search search for pickups
     * @param minAffinity: the lowest affinity of a weapon the bot is holding
     * @return a reasonably short path to a desired healing event
     */
    public static RallyPoint getPointNearHealth(PlayerBot player, Vector2 playerLocation, float searchRadius, int minAffinity) {
        final RallyPoint[] bestPoint = new RallyPoint[1];
        player.getWorld().QueryAABB((fixture -> {
                if (fixture.getUserData() instanceof final EventData eventData) {
                    if (eventData.getEvent().isBotHealthPickup()) {

                        //for all events found, calculate a path to it
                        RallyPoint tempPoint = BotManager.getNearestPoint(player, eventData.getEvent().getPosition());

                        //tentatively, we stop immediately upon finding an appropriate pickup to path towards
                        if (tempPoint != null) {
                            player.getBotController().setHealthTarget(eventData.getEvent());
                            bestPoint[0] = tempPoint;
                            return false;
                        }
                    }
                }
                return true;
            }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
            playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        return bestPoint[0];
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
        int bestSlot = player.getPlayerData().getCurrentSlot();

        //find which held weapon has the highest "suitability" based on distance from a living enemy
        if (BotManager.raycastUtility(player, playerLocation, targetLocation, Constants.BIT_PROJECTILE) == 1.0f && targetAlive
                && Math.abs(playerLocation.x - targetLocation.x) < player.getVisionX()
                && Math.abs(playerLocation.y - targetLocation.y) <  player.getVisionY()) {
            float bestSuitability = BotLoadoutProcessor.calcWeaponSuitability(player,
                    player.getPlayerData().getMultitools()[bestSlot], distSquared, 0);
            for (int i = 0; i < player.getPlayerData().getMultitools().length - 1; i++) {
                int suitability = BotLoadoutProcessor.calcWeaponSuitability(player,
                        player.getPlayerData().getMultitools()[i], distSquared, bestSuitability);
                if (suitability > bestSuitability) {
                    bestSuitability = suitability;
                    bestSlot = i;
                }
            }
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
    public static void processWeaponAim(PlayerBot player, Vector2 targetLocation, Vector2 targetVelocity, Equippable weapon) {

        //this makes blind disable the bot's ability to adjust aim
        if (player.getBlinded() > Blinded.botBlindThreshold) { return; }

        //atm, the only weapon with different aiming logic is the cola-cannon, which must be shaken when uncharged
        if (Objects.requireNonNull(UnlockEquip.getUnlockFromEquip(weapon.getClass())) == UnlockEquip.COLACANNON) {
            if (weapon.getChargeCd() >= weapon.getChargeTime() || weapon.isReloading()) {
                mouseTarget.set(BotManager.acquireAimTarget(player, player.getPosition(),
                        targetLocation, targetVelocity, ((RangedWeapon) weapon).getProjectileSpeed()));
            } else {
                BotLoadoutProcessor.aimWobble(player);
                mouseTarget.set(targetLocation).add(player.getAimWobble());
            }
        } else {
            //default behavior: acquire target's predicted position
            if (weapon instanceof RangedWeapon ranged) {
                mouseTarget.set(BotManager.acquireAimTarget(player, player.getPosition(),
                        targetLocation, targetVelocity, ranged.getProjectileSpeed()));
            } else {
                mouseTarget.set(targetLocation);
            }
        }

        //bot's mouse lerps towards the predicted position
        mouseTarget.scl(PPM);
        mousePosition.set(player.getMouse().getPixelPosition());
        mousePosition.x = mousePosition.x + (mouseTarget.x - mousePosition.x) * player.getMouseAimSpeed();
        mousePosition.y = mousePosition.y + (mouseTarget.y - mousePosition.y) * player.getMouseAimSpeed();
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
            case TESLA_COIL:
                //the same hold-delay-release is used here but with a moderate delay to make the coils tend to be further apart
                holdDelayRelease(player, shooting, defaultLongDelay);
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

                //spray-type weapons are suitable when firing to avoid switcing immediately after gaining firing status
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
    private static final float defaultLongDelay = 0.6f;
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
     * This makes a bot hold a weapon until its close to overheating, before they release and wait
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

    private static final float maxWobble = 25.0f;
    private static final float wobbleSpeed = 45.0f;
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
            UnlockActives.HEALING_FIELD, UnlockActives.HONEYCOMB, UnlockActives.JUMP_KICK, UnlockActives.MARINE_SNOWGLOBE,
            UnlockActives.METEOR_STRIKE, UnlockActives.MELON,  UnlockActives.MISSILE_POD, UnlockActives.NAUTICAL_MINE,
            UnlockActives.ORBITAL_SHIELD, UnlockActives.PLUS_MINUS, UnlockActives.PROXIMITY_MINE, UnlockActives.SPIRIT_RELEASE,
            UnlockActives.TAINTED_WATER};
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

    private static final UnlockArtifact[] mobility2 = { UnlockArtifact.CASTAWAYS_TRAVELOGUE, UnlockArtifact.FENS_CLIPPED_WINGS,
            UnlockArtifact.MOON_FLUTHER, UnlockArtifact.NICE_SHOES, UnlockArtifact.VOID_HYPONOME };
    private static final UnlockArtifact[] mobility1 = { UnlockArtifact.CURSED_CILICE, UnlockArtifact.NACREOUS_RUDDER };
    private static final UnlockArtifact[] defensive3 = { UnlockArtifact.FALLACY_OF_FLESH, UnlockArtifact.HORNS_OF_AMMON };
    private static final UnlockArtifact[] defensive2 = { UnlockArtifact.BLASTEMA, UnlockArtifact.FARADAYS_CAGE, UnlockArtifact.FRACTURE_PLATE,
            UnlockArtifact.GLUTTONOUS_GREY_GLOVE, UnlockArtifact.GOOD_HEALTH, UnlockArtifact.VISE_OF_SHAME };
    private static final UnlockArtifact[] defensive1 = { UnlockArtifact.NUMBER_ONE_BOSS_MUG, UnlockArtifact.DAS_BOOT,
            UnlockArtifact.DAY_AT_THE_FAIR, UnlockArtifact.GEMMULE, UnlockArtifact.KUMQUAT, UnlockArtifact.LOTUS_LANTERN,
            UnlockArtifact.MOUTHBREATHER_CERTIFICATE, UnlockArtifact.NOCTILUCENT_PROMISE, UnlockArtifact.NUTRILOG_CRUNCHBAR_PLUS,
            UnlockArtifact.SALIGRAM, UnlockArtifact.TUNICATE_TUNIC};
    private static final UnlockArtifact[] offensive3 = { UnlockArtifact.BUCKET_OF_BATTERIES, UnlockArtifact.EMAUDELINES_PRISM, UnlockArtifact.JURY_RIGGED_BINDINGS };
    private static final UnlockArtifact[] offensive2 = { UnlockArtifact.BOOK_OF_BURIAL, UnlockArtifact.BRITTLING_POWDER, UnlockArtifact.CHAOS_CONJURANT,
            UnlockArtifact.CLOCKWISE_CAGE, UnlockArtifact.ERSATZ_SMILE, UnlockArtifact.GOMEZS_AMYGDALA, UnlockArtifact.PEER_PRESSURE,
            UnlockArtifact.ROYAL_JUJUBE_BANG, UnlockArtifact.SHILLERS_DEATHCAP, UnlockArtifact.TRIGGERFISH_FINGER, UnlockArtifact.TYPHON_FANG,
            UnlockArtifact.VESTIGIAL_CHAMBER, UnlockArtifact.VOLATILE_DERMIS, UnlockArtifact.WHITE_WHALE_CHARM, UnlockArtifact.WRATH_OF_THE_FROGMAN };
    private static final UnlockArtifact[] offensive1 = { UnlockArtifact.EIGHT_BALL, UnlockArtifact.ABYSSAL_INSIGNIA, UnlockArtifact.CALL_OF_THE_VOID,
            UnlockArtifact.CROWN_OF_THORNS, UnlockArtifact.FORAGERS_HIVE, UnlockArtifact.IRON_SIGHTS, UnlockArtifact.KERMUNGLER,
            UnlockArtifact.MOUTHFUL_OF_BEES, UnlockArtifact.NUCLEAR_PUNCH_THRUSTERS, UnlockArtifact.NURDLER, UnlockArtifact.PEACHWOOD_SWORD,
            UnlockArtifact.PEPPER, UnlockArtifact.PETRIFIED_PAYLOAD, UnlockArtifact.RED_TIDE_TALISMAN, UnlockArtifact.SAMURAI_SHARK,
            UnlockArtifact.SWORD_OF_SYZYGY, UnlockArtifact.VOW_OF_EMPTY_HANDS };
    private static final UnlockArtifact[] misc3 = { UnlockArtifact.AU_COURANT, UnlockArtifact.CARLOCS_THESIS, UnlockArtifact.HEART_OF_SPEROS,
            UnlockArtifact.INFORMANTS_TIE, UnlockArtifact.KINESIS_LENS, UnlockArtifact.TENUOUS_GRIP_ON_REALITY };
    private static final UnlockArtifact[] misc2 = { UnlockArtifact.CURIOUS_SAUCE, UnlockArtifact.EXTRA_ROW_OF_TEETH, UnlockArtifact.ICE9 };
    private static final UnlockArtifact[] misc1 = { UnlockArtifact.ANARCHISTS_COOKBOOK, UnlockArtifact.BUTTONMAN_BUTTONS, UnlockArtifact.ICE9,
            UnlockArtifact.SINKING_FEELING};

    public static UnlockArtifact[] getRandomArtifacts(PlayState state) {
        UnlockArtifact[] artifacts = new UnlockArtifact[]{ UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING,  UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING, UnlockArtifact.NOTHING };

        if (state.getMode().getBotDifficulty().equals(BotPersonality.BotDifficulty.EASY) ||
                (GameStateManager.currentMode.equals(GameStateManager.Mode.SINGLE) &&
                        state.getPlayer().getPlayerData().getArtifactSlotsUsed() == 0)) {
            return artifacts;
        }

        Array<UnlockArtifact> artifactOptions = new Array<>();

        int slots = state.getGsm().getSetting().getArtifactSlots();
        int currentSlot = 0;
        boolean mobilityFound = slots == 1;

        while (slots > 0) {
            artifactOptions.clear();
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
                artifacts[currentSlot] = artifactOptions.get(MathUtils.random(artifactOptions.size - 1));
                slots -= artifacts[currentSlot].getArtifact().getSlotCost();
                currentSlot++;
            }
        }

        return artifacts;
    }

    /**
     * This applies random cosmetics to the newly created bot
     */
    public static UnlockCosmetic[] getRandomCosmetics(UnlockCharacter character) {
        UnlockCosmetic[] cosmetics = new UnlockCosmetic[]{ UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1, UnlockCosmetic.NOTHING_HAT1 };

        //iterate through all cosmetic slots and for each, add all applicable cosmetics to a list, then choose one randomly
        int index = 0;
        for (CosmeticSlot slot: CosmeticSlot.values()) {
            Array<UnlockCosmetic> cosmeticOptions = new Array<>();
            for (UnlockCosmetic cosmetic: UnlockCosmetic.values()) {
                if (!cosmetic.checkCompatibleCharacters(character) && cosmetic.getCosmeticSlot().equals(slot)) {
                    cosmeticOptions.add(cosmetic);
                }
            }
            if (cosmeticOptions.size > 0) {
                cosmetics[index] = cosmeticOptions.get(MathUtils.random(cosmeticOptions.size - 1));
            }
            index++;
        }

        return cosmetics;
    }

    private static final float healThreshold = 0.8f;
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
                if (player.getPlayerData().getCurrentHp() < player.getPlayerData().getStat(Stats.MAX_HP) * healThreshold
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
                        distanceSquared < weapon.getBotRangeMin() * weapon.getBotRangeMin()) {
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
