package com.mygdx.hadal.battle;

import com.mygdx.hadal.text.GameText;
import com.mygdx.hadal.text.UIText;

/**
 * A DamageSource indicates the effect that causes an instance of damage.
 * This is used for informational purposes; indicating what killed you in the kill feed.
 *
 */
public enum DamageSource {

    MISC(DamageSourceType.MISC),
    DISCONNECT(DamageSourceType.MISC),
    AIRBLAST(DamageSourceType.MISC),
    CURRENTS(DamageSourceType.ENVIRONMENT),
    MAP_BUZZSAW(DamageSourceType.ENVIRONMENT),
    MAP_FALL(DamageSourceType.ENVIRONMENT),
    MAP_POISON(DamageSourceType.ENVIRONMENT),

    ENEMY_ATTACK(DamageSourceType.ENEMY),

    AMITA_CANNON(DamageSourceType.WEAPON, GameText.AMITA_CANNON),
    ASSAULT_BITS(DamageSourceType.WEAPON, GameText.ASSAULT_BITS),
    BANANA(DamageSourceType.WEAPON, GameText.WEAPON_BANANA),
    BATTERING_RAM(DamageSourceType.WEAPON, GameText.BATTERING_RAM),
    BEE_GUN(DamageSourceType.WEAPON, GameText.BEE_GUN),
    BOILER(DamageSourceType.WEAPON, GameText.BOILER),
    BOOMERANG(DamageSourceType.WEAPON, GameText.BOOMERANG),
    BOUNCING_BLADES(DamageSourceType.WEAPON, GameText.BOUNCING_BLADES),
    CHARGE_BEAM(DamageSourceType.WEAPON, GameText.CHARGE_BEAM),
    COLA_CANNON(DamageSourceType.WEAPON, GameText.COLA_CANNON),
    CR4P_CANNON(DamageSourceType.WEAPON, GameText.CR4P_CANNON),
    DEEP_SEA_SMELTER(DamageSourceType.WEAPON, GameText.DEEP_SEA_SMELTER),
    DIAMOND_CUTTER(DamageSourceType.WEAPON, GameText.DIAMOND_CUTTER),
    DUELING_CORKGUN(DamageSourceType.WEAPON, GameText.DUELING_CORKGUN),
    FISTICUFFS(DamageSourceType.WEAPON, GameText.FISTICUFFS),
    FLOUNDERBUSS(DamageSourceType.WEAPON, GameText.FLOUNDERBUSS),
    FUGUN(DamageSourceType.WEAPON, GameText.FUGUN),
    GRENADE_LAUNCHER(DamageSourceType.WEAPON, GameText.GRENADE_LAUNCHER),
    HEXENHOWITZER(DamageSourceType.WEAPON, GameText.HEXENHOWITZER),
    ICEBERG(DamageSourceType.WEAPON, GameText.ICEBERG),
    IRON_BALL_LAUNCHER(DamageSourceType.WEAPON, GameText.IRON_BALL_LAUNCHER),
    KAMABOKANNON(DamageSourceType.WEAPON, GameText.KAMABOKANNON),
    KILLER_BEAT(DamageSourceType.WEAPON, GameText.KILLER_BEAT),
    LASER_GUIDED_ROCKET(DamageSourceType.WEAPON, GameText.LASER_GUIDED_ROCKET),
    LASER_RIFLE(DamageSourceType.WEAPON, GameText.LASER_RIFLE),
    LOVE_BOW(DamageSourceType.WEAPON, GameText.LOVE_BOW),
    MACHINE_GUN(DamageSourceType.WEAPON, GameText.MACHINE_GUN),
    MAELSTROM(DamageSourceType.WEAPON, GameText.MAELSTROM),
    MINIGUN(DamageSourceType.WEAPON, GameText.MINIGUN),
    MORAYGUN(DamageSourceType.WEAPON, GameText.MORAYGUN),
    MORNING_STAR(DamageSourceType.WEAPON, GameText.MORNING_STAR),
    NEMATOCYDEARM(DamageSourceType.WEAPON, GameText.NEMATOCYDEARM),
    PARTY_POPPER(DamageSourceType.WEAPON, GameText.PARTY_POPPER),
    PEARL_REVOLVER(DamageSourceType.WEAPON, GameText.PEARL_REVOLVER),
    PEPPERGRINDER(DamageSourceType.WEAPON, GameText.PEPPERGRINDER),
    PUFFBALLER(DamageSourceType.WEAPON, GameText.PUFFBALLER),
    RETICLE_STRIKE(DamageSourceType.WEAPON, GameText.RETICLE_STRIKE),
    RIFTSPLITTER(DamageSourceType.WEAPON, GameText.RIFTSPLITTER),
    SCRAPRIPPER(DamageSourceType.WEAPON, GameText.SCRAPRIPPER),
    SCREECHER(DamageSourceType.WEAPON, GameText.SCREECHER),
    SLODGE_NOZZLE(DamageSourceType.WEAPON, GameText.SLODGE_NOZZLE),
    SNIPER_RIFLE(DamageSourceType.WEAPON, GameText.SNIPER_RIFLE),
    SPEARGUN(DamageSourceType.WEAPON, GameText.SPEARGUN),
    STICKYBOMB_LAUNCHER(DamageSourceType.WEAPON, GameText.STICKYBOMB_LAUNCHER),
    STUTTERGUN(DamageSourceType.WEAPON, GameText.STUTTERGUN),
    TESLA_COIL(DamageSourceType.WEAPON, GameText.TESLA_COIL),
    TORPEDO_LAUNCHER(DamageSourceType.WEAPON, GameText.TORPEDO_LAUNCHER),
    TRICK_GUN(DamageSourceType.WEAPON, GameText.TRICK_GUN),
    TYRRAZZAN_REAPER(DamageSourceType.WEAPON, GameText.TYRRAZZAN_REAPER),
    UNDERMINER(DamageSourceType.WEAPON, GameText.UNDERMINER),
    VAJRA(DamageSourceType.WEAPON, GameText.VAJRA),
    VINE_SOWER(DamageSourceType.WEAPON, GameText.VINE_SOWER),
    WAVE_BEAM(DamageSourceType.WEAPON, GameText.WAVE_BEAM),
    X_BOMBER(DamageSourceType.WEAPON, GameText.X_BOMBER),

    ANCHOR_SMASH(DamageSourceType.MAGIC, GameText.ANCHOR_SMASH),
    BENDY_BEAMS(DamageSourceType.MAGIC, GameText.BENDY_BEAMS),
    DEPTH_CHARGE(DamageSourceType.MAGIC, GameText.DEPTH_CHARGE),
    FAFROTSKIES(DamageSourceType.MAGIC, GameText.FAFROTSKIES),
    FLASH_BANG(DamageSourceType.MAGIC, GameText.FLASH_BANG),
    GHOST_STEP(DamageSourceType.MAGIC, GameText.GHOST_STEP),
    HONEYCOMB(DamageSourceType.MAGIC, GameText.HONEYCOMB),
    HYDRAULIC_UPPERCUT(DamageSourceType.MAGIC, GameText.HYDRAULIC_UPPERCUT),
    IMMOLATION_AURA(DamageSourceType.MAGIC, GameText.IMMOLATION_AURA),
    JUMP_KICK(DamageSourceType.MAGIC, GameText.JUMP_KICK),
    MARINE_SNOWGLOBE(DamageSourceType.MAGIC, GameText.MARINE_SNOWGLOBE),
    MERIDIAN_MAKER(DamageSourceType.MAGIC, GameText.MERIDIAN_MAKER),
    METEOR_STRIKE(DamageSourceType.MAGIC, GameText.METEOR_STRIKE),
    MISSILE_POD(DamageSourceType.MAGIC, GameText.MISSILE_POD),
    NAUTICAL_MINE(DamageSourceType.MAGIC, GameText.NAUTICAL_MINE),
    ORBITAL_SHIELD(DamageSourceType.MAGIC, GameText.ORBITAL_SHIELD),
    PLUS_MINUS(DamageSourceType.MAGIC, GameText.PLUS_MINUS),
    PROXIMITY_MINE(DamageSourceType.MAGIC, GameText.PROXIMITY_MINE),
    SAMSON_OPTION(DamageSourceType.MAGIC, GameText.SAMSON_OPTION),
    SPIRIT_RELEASE(DamageSourceType.MAGIC, GameText.SPIRIT_RELEASE),
    TAINTED_WATER(DamageSourceType.MAGIC, GameText.TAINTED_WATER),
    TRACTOR_BEAM(DamageSourceType.MAGIC, GameText.TRACTOR_BEAM),

    ANARCHISTS_COOKBOOK(DamageSourceType.ARTIFACT, GameText.ANARCHISTS_COOKBOOK),
    ANCIENT_SYNAPSE(DamageSourceType.ARTIFACT, GameText.ANCIENT_SYNAPSE),
    BOOK_OF_BURIAL(DamageSourceType.ARTIFACT, GameText.BOOK_OF_BURIAL),
    BRIGGLES_BLADED_BOOT(DamageSourceType.ARTIFACT, GameText.BRIGGLES_BLADED_BOOT),
    BRITTLING_POWDER(DamageSourceType.ARTIFACT, GameText.BRITTLING_POWDER),
    BUCKET_OF_BATTERIES(DamageSourceType.ARTIFACT, GameText.BUCKET_OF_BATTERIES),
    CHAOS_CONJURANT(DamageSourceType.ARTIFACT, GameText.CHAOS_CONJURANT),
    CRIME_DISCOURAGEMENT_STICK(DamageSourceType.ARTIFACT, GameText.CRIME_DISCOURAGEMENT_STICK),
    CROWN_OF_THORNS(DamageSourceType.ARTIFACT, GameText.CROWN_OF_THORNS),
    THE_FINGER(DamageSourceType.ARTIFACT, GameText.THE_FINGER),
    FORAGERS_HIVE(DamageSourceType.ARTIFACT, GameText.FORAGERS_HIVE),
    LAMPREY_IDOL(DamageSourceType.ARTIFACT, GameText.LAMPREY_IDOL),
    MOUTHFUL_OF_BEES(DamageSourceType.ARTIFACT, GameText.MOUTHFUL_OF_BEES),
    NURDLER(DamageSourceType.ARTIFACT, GameText.NURDLER),
    PEACHWOOD_SWORD(DamageSourceType.ARTIFACT, GameText.PEACHWOOD_SWORD),
    PEPPER(DamageSourceType.ARTIFACT, GameText.PEPPER),
    PEER_PRESSURE(DamageSourceType.ARTIFACT, GameText.PEER_PRESSURE),
    PETRIFIED_PAYLOAD(DamageSourceType.ARTIFACT, GameText.PETRIFIED_PAYLOAD),
    RED_TIDE_TALISMAN(DamageSourceType.ARTIFACT, GameText.RED_TIDE_TALISMAN),
    SHILLERS_DEATHCAP(DamageSourceType.ARTIFACT, GameText.SHILLERS_DEATHCAP),
    VESTIGIAL_CHAMBER(DamageSourceType.ARTIFACT, GameText.VESTIGIAL_CHAMBER),
    VOLATILE_DERMIS(DamageSourceType.ARTIFACT, GameText.VOLATILE_DERMIS),
    WHITE_SMOKER(DamageSourceType.ARTIFACT, GameText.WHITE_SMOKER),
    WRATH_OF_THE_FROGMAN(DamageSourceType.ARTIFACT, GameText.WRATH_OF_THE_FROGMAN)

    ;

    private final DamageSourceType type;
    private final String text;

    DamageSource(DamageSourceType type) {
        this.type = type;
        this.text = toString();
    }

    DamageSource(DamageSourceType type, GameText text) {
        this.type = type;
        this.text = text.text();
    }

    /**
     * @return the text that shows up in the kill feed; contains the source name as well as its type
     */
    public String getKillSource() {
        if (DamageSourceType.MISC == type || DamageSourceType.ENEMY == type) {
            return text;
        } else {
            return text + " (" + type.getText() + ")";
        }
    }
}

enum DamageSourceType {
    MISC(UIText.MISC),
    ENVIRONMENT(UIText.ENVIRONMENT),
    WEAPON(UIText.WEAPON),
    MAGIC(UIText.MAGIC),
    ARTIFACT(UIText.ARTIFACT),
    ENEMY(UIText.MONSTER)

    ;

    private final String text;

    DamageSourceType(UIText text) {
        this.text = text.text();
    }

    public String getText() { return text; }
}
