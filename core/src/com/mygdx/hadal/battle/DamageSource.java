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
        if (type == DamageSourceType.MISC || type == DamageSourceType.ENEMY) {
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
