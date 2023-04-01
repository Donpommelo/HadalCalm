package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.actives.*;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.packets.PacketsAttacks;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

import java.util.UUID;

/**
 * A SyncedAttack represents an "attack" sent from server to client containing information about hitboxes produced, hitbox strategies,
 * sound, particles etc. This is used to only send one packet when an attack is executed rather than multiple for each
 * part of the attack. Additionally, this allows for more specific hbox behavior that would be difficult to synchronize
 * with client illusions
 *
 */
public enum SyncedAttack {

    AIRBLAST() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Airblaster.createAirblast(state, user, startPosition, startVelocity);
        }
    },

    ASSAULT_BITS_BEAM() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return AssaultBits.createAssaultBitsBeam(state, user, startPosition, startVelocity);
        }
    },

    AMITA() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return AmitaCannon.createAmita(state, user, startPosition, startVelocity);
        }
    },

    BANANA() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Banana.createBanana(state, user, startPosition, startVelocity);
        }
    },

    BATTERING() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return BatteringRam.createBattering(state, user, startPosition, startVelocity, extraFields);
        }
    },

    BEE() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return BeeGun.createBee(state, user, startPosition, startVelocity, DamageSource.BEE_GUN);
        }
    },

    BOILER_FIRE() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Boiler.createBoilerFire(state, user, startPosition, startVelocity);
        }
    },

    BOMB() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createBomb(state, user, startPosition, startVelocity, DamageSource.ANARCHISTS_COOKBOOK);
        }
    },

    BOOMERANG() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Boomerang.createBoomerang(state, user, startPosition, startVelocity);
        }
    },

    BOUNCING_BLADE() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return BouncingBlade.createBouncingBlade(state, user, startPosition, startVelocity);
        }
    },

    CHARGE_BEAM() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return ChargeBeam.createChargeBeam(state, user, startPosition, startVelocity, extraFields);
        }
    },

    COLA() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return ColaCannon.createCola(state, user, startPosition, startVelocity);
        }
    },

    CR4P() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return CR4PCannon.createCR4P(state, user, weaponVelocity, startPosition, startVelocity);
        }
    },

    DEEP_SMELT() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return DeepSeaSmelter.createDeepSmelt(state, user, startPosition, startVelocity);
        }
    },

    DIAMOND_CUTTER() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return DiamondCutter.createDiamondCutter(state, user);
        }
    },

    DUELING_CORK() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return DuelingCorkgun.createDuelingCork(state, user, startPosition, startVelocity);
        }
    },

    FIST() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Fisticuffs.createFist(state, user, startPosition, startVelocity);
        }
    },

    FLOUNDER() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return Flounderbuss.createFlounder(state, user, weaponVelocity, startPosition, startVelocity);
        }
    },

    FUGU() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Fugun.createFugu(state, user, startPosition, startVelocity);
        }
    },

    GRENADE() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return GrenadeLauncher.createGrenade(state, user, startPosition, startVelocity);
        }
    },

    HEX() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Hexenhowitzer.createHex(state, user, startPosition, startVelocity, extraFields);
        }
    },

    ICEBERG() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Iceberg.createIceberg(state, user, startPosition, startVelocity);
        }
    },

    IRON_BALL() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return IronBallLauncher.createIronBall(state, user, startPosition, startVelocity);
        }
    },

    KAMABOKO() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Kamabokannon.createKamaboko(state, user, startPosition, startVelocity);
        }
    },

    KILLER_NOTES() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return KillerBeat.createKillerNotes(state, user, weaponVelocity, startPosition, startVelocity, extraFields);
        }
    },

    LASER() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return LaserRifle.createLaser(state, user, startPosition, startVelocity, extraFields);
        }
    },

    LASER_GUIDED_ROCKET() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return LaserGuidedRocket.createLaserGuidedRocket(state, user, startPosition, startVelocity);
        }
    },

    LOVE_ARROW() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return LoveBow.createLoveArrow(state, user, startPosition, startVelocity, extraFields);
        }
    },

    MACHINE_GUN_BULLET() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Machinegun.createMachineGunBullet(state, user, startPosition, startVelocity);
        }
    },

    MAELSTROM() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Maelstrom.createMaelstrom(state, user, startPosition, startVelocity);
        }
    },

    MINIGUN_BULLET() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Minigun.createMinigunBullet(state, user, startPosition, startVelocity);
        }
    },

    MORAY() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return Moraygun.createMoray(state, user, weaponVelocity, startPosition, extraFields);
        }
    },

    MORNING_STAR() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return MorningStar.createMorningStar(state, user);
        }
    },

    NEMATOCYTE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Nematocydearm.createNematocyte(state, user, startPosition, startVelocity);
        }
    },

    PEARL() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return PearlRevolver.createPearl(state, user, startPosition, startVelocity);
        }
    },

    PEPPER() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Peppergrinder.createPepper(state, user, startPosition, startVelocity, extraFields);
        }
    },

    POPPER() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return PartyPopper.createPopper(state, user, startPosition, startVelocity, extraFields);
        }
    },

    PUFFBALL() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Puffballer.createPuffball(state, user, startPosition, startVelocity, extraFields);
        }
    },

    RETICLE_STRIKE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return ReticleStrike.createReticleStrike(state, user, startPosition, startVelocity);
        }
    },

    RIFT_SPLIT() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Riftsplitter.createRiftSplit(state, user, startPosition, startVelocity);
        }
    },

    SCRAPRIP() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Scrapripper.createScraprip(state, user, startPosition, startVelocity);
        }
    },

    SCREECH() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Screecher.createScreech(state, user, startPosition, startVelocity, extraFields);
        }
    },

    SLODGE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return SlodgeNozzle.createSlodge(state, user, startPosition, startVelocity);
        }
    },

    SNIPER_BULLET() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return SniperRifle.createSniperBullet(state, user, startPosition, startVelocity);
        }
    },

    SPEAR() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Speargun.createSpear(state, user, startPosition, startVelocity, false);
        }
    },

    SPEAR_NERFED() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Speargun.createSpear(state, user, startPosition, startVelocity, true);
        }
    },

    STICKY_BOMB() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return StickyBombLauncher.createStickyBomb(state, user, startPosition, startVelocity);
        }
    },

    STUTTER_LASER() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return StutterGun.createStutterLaser(state, user, startPosition, startVelocity);
        }
    },

    TESLA_ACTIVATION() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return TeslaCoil.createTeslaActivation(state, user, startPosition, extraFields);
        }
    },

    TORPEDO() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return TorpedoLauncher.createTorpedo(state, user, startPosition, startVelocity);
        }
    },

    TRICK_SHOT() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return TrickGun.createTrickShot(state, user, startPosition, startVelocity, extraFields);
        }
    },

    TYRAZZAN_REAPER() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return TyrrazzanReaper.createTyrrazzanReaper(state, user, startPosition, startVelocity, extraFields);
        }
    },

    UNDERMINER_DRILL() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return Underminer.createUndermineDrills(state, user, startPosition, startVelocity, extraFields);
        }
    },

    VAJRA() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Vajra.createVajra(state, user, startPosition, startVelocity);
        }
    },

    VINE() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return VineSower.createVine(state, user, startPosition, startVelocity, extraFields, true);
        }
    },

    WAVE_BEAM() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WaveBeam.createWaveBeam(state, user, startPosition, startVelocity);
        }
    },

    X_BOMB() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return XBomber.createXBomb(state, user, startPosition, startVelocity);
        }
    },

    ANCHOR() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return AnchorSmash.createAnchor(state, user, startPosition, extraFields);
        }
    },

    FLASHBANG() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return Flashbang.createFlashbang(state, user, startPosition, startVelocity);
        }
    },

    GHOST_STEP() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return GhostStep.createDematerialization(state, user, startPosition, startVelocity);
        }
    },

    JUMP_KICK() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return JumpKick.createJumpkick(state, user, startPosition, startVelocity);
        }
    },

    HYDRAUlIC_UPPERCUT() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return HydraulicUppercut.createHydraulicUppercut(state, user, startPosition, startVelocity);
        }
    },

    NAUTICAL_MINE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createNauticalMine(state, user, startPosition, startVelocity, extraFields);
        }
    },

    MARINE_SNOW() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return MarineSnowglobe.createMarineSnow(state, user, startPosition);
        }
    },

    BEE_HONEYCOMB() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return BeeGun.createBee(state, user, startPosition, startVelocity, DamageSource.HONEYCOMB);
        }
    },

    BEE_FORAGER() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return BeeGun.createBee(state, user, startPosition, startVelocity, DamageSource.FORAGERS_HIVE);
        }
    },

    BEE_MOUTHFUL() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return BeeGun.createBee(state, user, startPosition, startVelocity, DamageSource.MOUTHFUL_OF_BEES);
        }
    },

    HOMING_MISSILE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createHomingMissile(state, user, startPosition, startVelocity, DamageSource.MISSILE_POD);
        }
    },

    HOMING_MISSILE_FROGMAN() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createHomingMissile(state, user, startPosition, startVelocity, DamageSource.WRATH_OF_THE_FROGMAN);
        }
    },

    ORBITAL_STAR() {
        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return OrbitalShield.createOrbitals(state, user);
        }
    },

    PROXIMITY_MINE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createProximityMine(state, user, startPosition, DamageSource.PROXIMITY_MINE, extraFields);
        }
    },

    PROXIMITY_MINE_BOOK() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createProximityMine(state, user, startPosition, DamageSource.BOOK_OF_BURIAL, extraFields);
        }
    },

    STICK_GRENADE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createStickGrenade(state, user, startPosition, startVelocity);
        }
    },

    FORCE_OF_WILL() {
        @Override
        public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
            ForceofWill.createForceOfWill(state, user, startPosition);
        }
    },

    SUPPLY_DROP() {
        @Override
        public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
            SupplyDrop.createSupplyDrop(state, startPosition);
        }
    },

    VENGEFUL_SPIRIT() {
        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return WeaponUtils.createVengefulSpirits(state, user, startPosition, DamageSource.SPIRIT_RELEASE, extraFields);
        }
    },

    VENGEFUL_SPIRIT_PEACHWOOD() {
        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return WeaponUtils.createVengefulSpirits(state, user, startPosition, DamageSource.PEACHWOOD_SWORD, extraFields);
        }
    },

    PICKUP() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return PickupUtils.createPickup(state, user, startPosition, startVelocity, extraFields);
        }
    },

    EGGPLANT() {
        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return PickupUtils.createScrap(state, user, startPosition, startVelocity, extraFields);
        }
    },

    CANDY() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return PickupUtils.createCandy(state, user, new Vector2[] {startPosition}, new Vector2[] {startVelocity})[0];
        }

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                                 Vector2[] startVelocity, float[] extraFields) {
            return PickupUtils.createCandy(state, user, startPosition, startVelocity);
        }
    },

    PING() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createPing(state, user, startPosition);
        }
    },

    EMOTE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createEmote(state, user, extraFields);
        }
    }

    ;

    /**
     * This initiates a synced attack that produces a single hitbox
     * @param state: Current playstate
     * @param user: User that is executing this attack
     * @param startPosition: Starting position of the hitbox produced by this attack
     * @param startVelocity: Starting velocity of the hitbox produced by this attack
     * @param extraFields: Any extra information required for client to replicate this attack
     * @return the hitbox that this attack produces
     */
    public Hitbox initiateSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                             int connID, boolean origin, float... extraFields) {

        Hitbox hbox = performSyncedAttackSingle(state, user, startPosition, startVelocity, extraFields);
        hbox.setAttack(this);
        hbox.setSyncedMulti(false);
        hbox.setExtraFields(extraFields);
        if (state.isServer()) {
            syncAttackSingleServer(hbox, extraFields, connID, hbox.isSynced(), false);
        } else {
            if (origin) {
                syncAttackSingleClient(hbox, extraFields, hbox.isSynced());
                ((ClientState) state).addEntity(hbox.getEntityID(), hbox, hbox.isSynced(), PlayState.ObjectLayer.HBOX);
            }
        }
        return hbox;
    }

    public Hitbox initiateSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float... extraFields) {
        return initiateSyncedAttackSingle(state, user, startPosition, startVelocity, 0, true, extraFields);
    }

    /**
     * This performs the actual attack and is overridden in each SyncedAttack that produces a single hitbox
     */
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                            float[] extraFields) { return null; }

    /**
     * Syncs an executed attack with the client by sending them a packet
     * @param hbox: Hitbox that is being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param catchup: Is this being synced as a result of catchup packet for newly joined player or missed create?
     */
    public void syncAttackSingleServer(Hitbox hbox, float[] extraFields, int connID, boolean synced, boolean catchup) {
        Object packet;

        if (synced) {
            if (0 == extraFields.length) {
                packet = new PacketsAttacks.SingleServerDependent(hbox.getEntityID(), hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), this);
            } else {
                packet = new PacketsAttacks.SingleServerDependentExtra(hbox.getEntityID(), hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), extraFields, this);
            }
        } else {
            if (0 == extraFields.length) {
                packet = new PacketsAttacks.SingleServerIndependent(hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), this);
            } else {
                packet = new PacketsAttacks.SingleServerIndependentExtra(hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), extraFields, this);
            }
        }

        if (0 == connID) {
            HadalGame.server.sendToAllUDP(packet);
        } else {
            HadalGame.server.sendToAllExceptUDP(connID, packet);
        }
    }

    public void syncAttackSingleClient(Hitbox hbox, float[] extraFields, boolean synced) {
        if (synced) {
            if (0 == extraFields.length) {
                HadalGame.client.sendUDP(new PacketsAttacks.SingleClientDependent(hbox.getEntityID(),
                        hbox.getStartPos(),
                        hbox.getStartVelo(), this));
            } else {
                HadalGame.client.sendUDP(new PacketsAttacks.SingleClientDependentExtra(hbox.getEntityID(),
                        hbox.getStartPos(),
                        hbox.getStartVelo(), extraFields, this));
            }
        } else {
            if (0 == extraFields.length) {
                HadalGame.client.sendUDP(new PacketsAttacks.SingleClientIndependent(
                        hbox.getStartPos(),
                        hbox.getStartVelo(), this));
            } else {
                HadalGame.client.sendUDP(new PacketsAttacks.SingleClientIndependentExtra(
                        hbox.getStartPos(),
                        hbox.getStartVelo(), extraFields, this));
            }
        }

    }

    /**
     * This initiates a synced attack that produces multiple hitboxes
     * @param state: Current playstate
     * @param user: User that is executing this attack
     * @param startPosition: Starting positions of each hitbox produced by this attack
     * @param startVelocity: Starting velocities of each hitbox produced by this attack
     * @param extraFields: Any extra information required for client to replicate this attack
     * @return the hitboxes that this attack produces
     */
    public Hitbox[] initiateSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
            Vector2[] startVelocity, int connID, boolean origin, float... extraFields) {
        Hitbox[] hboxes = performSyncedAttackMulti(state, user, weaponVelocity, startPosition, startVelocity, extraFields);
        for (Hitbox hbox : hboxes) {
            hbox.setAttack(this);
            hbox.setSyncedMulti(true);
            hbox.setExtraFields(extraFields);
        }
        if (0 != hboxes.length) {
            boolean isSynced = hboxes[0].isSynced();

            if (state.isServer()) {
                syncAttackMultiServer(weaponVelocity, hboxes, extraFields, connID, isSynced, false);
            } else {
                if (origin) {
                    syncAttackMultiClient(weaponVelocity, hboxes, extraFields, isSynced);
                    for (Hitbox hbox : hboxes) {
                        ((ClientState) state).addEntity(hbox.getEntityID(), hbox, isSynced, PlayState.ObjectLayer.HBOX);
                    }
                }
            }
        }
        return hboxes;
    }

    public Hitbox[] initiateSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                              Vector2[] startVelocity, float... extraFields) {
        return initiateSyncedAttackMulti(state, user, weaponVelocity, startPosition, startVelocity, 0, true, extraFields);
    }
    /**
     * This performs the actual attack and is overridden in each SyncedAttack that produces multiple hitbox
     */
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                             Vector2[] startVelocity, float[] extraFields) { return null; }

    /**
     * Syncs an executed attack with the client by sending them a packet
     * @param hboxes: Hitboxes that are being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param catchup: Is this being synced as a result of catchup packet for newly joined player or missed create?
     */
    public void syncAttackMultiServer(Vector2 weaponVelocity, Hitbox[] hboxes, float[] extraFields, int connID, boolean isSynced, boolean catchup) {
        UUID[] hboxID = new UUID[hboxes.length];
        Vector2[] positions = new Vector2[hboxes.length];
        Vector2[] velocities = new Vector2[hboxes.length];
        for (int i = 0; i < hboxes.length; i++) {
            hboxID[i] = hboxes[i].getEntityID();
            positions[i] = catchup ? hboxes[i].getPixelPosition() : hboxes[i].getStartPos();
            velocities[i] = catchup ? hboxes[i].getLinearVelocity() : hboxes[i].getStartVelo();
        }
        Object packet;
        if (isSynced) {
            if (0 == extraFields.length) {
                packet = new PacketsAttacks.MultiServerDependent(hboxID, hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, this);
            } else {
                packet = new PacketsAttacks.MultiServerDependentExtra(hboxID, hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, extraFields, this);
            }
        } else {
            if (0 == extraFields.length) {
                packet = new PacketsAttacks.MultiServerIndependent(hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, this);
            } else {
                packet = new PacketsAttacks.MultiServerIndependentExtra(hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, extraFields, this);
            }
        }

        if (0 == connID) {
            HadalGame.server.sendToAllUDP(packet);
        } else {
            HadalGame.server.sendToAllExceptUDP(connID, packet);
        }
    }

    public void syncAttackMultiClient(Vector2 weaponVelocity, Hitbox[] hboxes, float[] extraFields, boolean isSynced) {
        UUID[] hboxID = new UUID[hboxes.length];
        Vector2[] positions = new Vector2[hboxes.length];
        Vector2[] velocities = new Vector2[hboxes.length];
        for (int i = 0; i < hboxes.length; i++) {
            hboxID[i] = hboxes[i].getEntityID();
            positions[i] = hboxes[i].getStartPos();
            velocities[i] = hboxes[i].getStartVelo();
        }
        if (isSynced) {
            if (0 == extraFields.length) {
                HadalGame.client.sendUDP(new PacketsAttacks.MultiClientDependent(hboxID, weaponVelocity, positions, velocities, this));
            } else {
                HadalGame.client.sendUDP(new PacketsAttacks.MultiClientDependentExtra(hboxID, weaponVelocity, positions, velocities, extraFields, this));
            }
        } else {
            if (0 == extraFields.length) {
                HadalGame.client.sendUDP(new PacketsAttacks.MultiClientIndependent(weaponVelocity, positions, velocities, this));
            } else {
                HadalGame.client.sendUDP(new PacketsAttacks.MultiClientIndependentExtra(weaponVelocity, positions, velocities, extraFields, this));
            }
        }
    }

    public void initiateSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, int connID,
                                           boolean origin, boolean independent, float... extraFields) {
        if (independent || state.isServer()) {
            performSyncedAttackNoHbox(state, user, startPosition, extraFields);
        }

        if (state.isServer()) {
            syncAttackNoHboxServer(user, startPosition, independent, extraFields, connID);
        } else if (origin) {
            syncAttackNoHboxClient(startPosition, independent, extraFields);
        }
    }

    public void initiateSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, boolean independent, float... extraFields) {
        initiateSyncedAttackNoHbox(state, user, startPosition, 0, true, independent, extraFields);
    }

    public void performSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {}

    public void syncAttackNoHboxServer(Schmuck user, Vector2 startPos, boolean independent, float[] extraFields, int connID) {
        Object packet;
        if (0 == extraFields.length) {
            packet = new PacketsAttacks.SyncedAttackNoHboxServer(user.getEntityID(), startPos, this);
        } else {
            packet = new PacketsAttacks.SyncedAttackNoHboxExtraServer(user.getEntityID(), startPos, extraFields, this);
        }
        if (0 == connID || !independent) {
            HadalGame.server.sendToAllUDP(packet);
        } else {
            HadalGame.server.sendToAllExceptUDP(connID, packet);
        }
    }

    public void syncAttackNoHboxClient(Vector2 startPos, boolean independent, float[] extraFields) {
        if (0 == extraFields.length) {
            HadalGame.client.sendUDP(new PacketsAttacks.SyncedAttackNoHbox(startPos, independent, this));
        } else {
            HadalGame.client.sendUDP(new PacketsAttacks.SyncedAttackNoHboxExtra(startPos, independent, extraFields, this));
        }
    }
}
