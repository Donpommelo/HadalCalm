package com.mygdx.hadal.battle;

/**
 * A DamageSource indicates the effect that causes an instance of damage.
 * This is used for informational purposes; indicating what killed you in the kill feed.
 *
 */
public enum DamageSource {

    MISC(DamageSourceType.MISC),
    DISCONNECT(DamageSourceType.MISC),
    AIRBLAST(DamageSourceType.MISC),
    CURRENTS(DamageSourceType.MISC),
    MAP_BUZZSAW(DamageSourceType.MISC),
    MAP_FALL(DamageSourceType.MISC),
    MAP_POISON(DamageSourceType.MISC),

    ENEMY_ATTACK(DamageSourceType.ENEMY),

    AMITA_CANNON(DamageSourceType.WEAPON),
    ASSAULT_BITS(DamageSourceType.WEAPON),
    BANANA(DamageSourceType.WEAPON),
    BATTERING_RAM(DamageSourceType.WEAPON),
    BEE_GUN(DamageSourceType.WEAPON),
    BOILER(DamageSourceType.WEAPON),
    BOOMERANG(DamageSourceType.WEAPON),
    BOUNCING_BLADES(DamageSourceType.WEAPON),
    CHARGE_BEAM(DamageSourceType.WEAPON),
    COLA_CANNON(DamageSourceType.WEAPON),
    CR4P_CANNON(DamageSourceType.WEAPON),
    DEEP_SEA_SMELTER(DamageSourceType.WEAPON),
    DIAMOND_CUTTER(DamageSourceType.WEAPON),
    DUELING_CORKGUN(DamageSourceType.WEAPON),
    FISTICUFFS(DamageSourceType.WEAPON),
    FLOUNDERBUSS(DamageSourceType.WEAPON),
    FUGUN(DamageSourceType.WEAPON),
    GRENADE_LAUNCHER(DamageSourceType.WEAPON),
    HEXENHOWITZER(DamageSourceType.WEAPON),
    ICEBERG(DamageSourceType.WEAPON),
    IRON_BALL_LAUNCHER(DamageSourceType.WEAPON),
    KAMABOKANNON(DamageSourceType.WEAPON),
    KILLER_BEAT(DamageSourceType.WEAPON),
    LASER_GUIDED_ROCKET(DamageSourceType.WEAPON),
    LASER_RIFLE(DamageSourceType.WEAPON),
    LOVE_BOW(DamageSourceType.WEAPON),
    MACHINE_GUN(DamageSourceType.WEAPON),
    MAELSTROM(DamageSourceType.WEAPON),
    MINIGUN(DamageSourceType.WEAPON),
    MORAYGUN(DamageSourceType.WEAPON),
    MORNING_STAR(DamageSourceType.WEAPON),
    NEMATOCYDEARM(DamageSourceType.WEAPON),
    PARTY_POPPER(DamageSourceType.WEAPON),
    PEARL_REVOLVER(DamageSourceType.WEAPON),
    PEPPERGRINDER(DamageSourceType.WEAPON),
    PUFFBALLER(DamageSourceType.WEAPON),
    RETICLE_STRIKE(DamageSourceType.WEAPON),
    RIFTSPLITTER(DamageSourceType.WEAPON),
    SCRAPRIPPER(DamageSourceType.WEAPON),
    SCREECHER(DamageSourceType.WEAPON),
    SLODGE_NOZZLE(DamageSourceType.WEAPON),
    SNIPER_RIFLE(DamageSourceType.WEAPON),
    SPEARGUN(DamageSourceType.WEAPON),
    STICKYBOMB_LAUNCHER(DamageSourceType.WEAPON),
    STUTTERGUN(DamageSourceType.WEAPON),
    TESLA_COIL(DamageSourceType.WEAPON),
    TORPEDO_LAUNCHER(DamageSourceType.WEAPON),
    TRICK_GUN(DamageSourceType.WEAPON),
    TYRRAZZAN_REAPER(DamageSourceType.WEAPON),
    UNDERMINER(DamageSourceType.WEAPON),
    VAJRA(DamageSourceType.WEAPON),
    VINE_SOWER(DamageSourceType.WEAPON),
    WAVE_BEAM(DamageSourceType.WEAPON),
    X_BOMBER(DamageSourceType.WEAPON),

    ANCHOR_SMASH(DamageSourceType.MAGIC),
    BENDY_BEAMS(DamageSourceType.MAGIC),
    DEPTH_CHARGE(DamageSourceType.MAGIC),
    FAFROTSKIES(DamageSourceType.MAGIC),
    FLASH_BANG(DamageSourceType.MAGIC),
    HONEYCOMB(DamageSourceType.MAGIC),
    HYDRAULIC_UPPERCUT(DamageSourceType.MAGIC),
    IMMOLATION_AURA(DamageSourceType.MAGIC),
    JUMP_KICK(DamageSourceType.MAGIC),
    MARINE_SNOWGLOBE(DamageSourceType.MAGIC),
    MERIDIAN_MAKER(DamageSourceType.MAGIC),
    METEOR_STRIKE(DamageSourceType.MAGIC),
    MISSILE_POD(DamageSourceType.MAGIC),
    NAUTICAL_MINE(DamageSourceType.MAGIC),
    ORBITAL_SHIELD(DamageSourceType.MAGIC),
    PLUS_MINUS(DamageSourceType.MAGIC),
    PROXIMITY_MINE(DamageSourceType.MAGIC),
    SAMSON_OPTION(DamageSourceType.MAGIC),
    SPIRIT_RELEASE(DamageSourceType.MAGIC),
    TAINTED_WATER(DamageSourceType.MAGIC),
    TRACTOR_BEAM(DamageSourceType.MAGIC),

    ANARCHISTS_COOKBOOK(DamageSourceType.ARTIFACT),
    ANCIENT_SYNAPSE(DamageSourceType.ARTIFACT),
    BOOK_OF_BURIAL(DamageSourceType.ARTIFACT),
    BRIGGLES_BLADED_BOOT(DamageSourceType.ARTIFACT),
    BRITTLING_POWDER(DamageSourceType.ARTIFACT),
    BUCKET_OF_BATTERIES(DamageSourceType.ARTIFACT),
    CHAOS_CONJURANT(DamageSourceType.ARTIFACT),
    CRIME_DISCOURAGEMENT_STICK(DamageSourceType.ARTIFACT),
    CROWN_OF_THORNS(DamageSourceType.ARTIFACT),
    THE_FINGER(DamageSourceType.ARTIFACT),
    FORAGERS_HIVE(DamageSourceType.ARTIFACT),
    LAMPREY_IDOL(DamageSourceType.ARTIFACT),
    MOUTHFUL_OF_BEES(DamageSourceType.ARTIFACT),
    NURDLER(DamageSourceType.ARTIFACT),
    PEACHWOOD_SWORD(DamageSourceType.ARTIFACT),
    PEPPER(DamageSourceType.ARTIFACT),
    PEER_PRESSURE(DamageSourceType.ARTIFACT),
    PETRIFIED_PAYLOAD(DamageSourceType.ARTIFACT),
    RED_TIDE_TALISMAN(DamageSourceType.ARTIFACT),
    SHILLERS_DEATHCAP(DamageSourceType.ARTIFACT),
    VESTIGIAL_CHAMBER(DamageSourceType.ARTIFACT),
    VOLATILE_DERMIS(DamageSourceType.ARTIFACT),
    WHITE_SMOKER(DamageSourceType.ARTIFACT),
    WRATH_OF_THE_FROGMAN(DamageSourceType.ARTIFACT)

    ;

//    private final String sourceText;
    private final DamageSourceType type;

    DamageSource(DamageSourceType type) {
        this.type = type;
    }

    /**
     * @return the text that shows up in the kill feed; contains the source name as well as its type
     */
    public String getKillSource() {
        if (type == DamageSourceType.MISC || type == DamageSourceType.ENEMY) {
            return toString();
        } else {
            return this + " (" + type.toString() + ")";
        }
    }
}

enum DamageSourceType {
    MISC,
    WEAPON,
    MAGIC,
    ARTIFACT,
    ENEMY
}
