package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.equip.actives.*;
import com.mygdx.hadal.equip.melee.*;
import com.mygdx.hadal.equip.ranged.*;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.packets.Packets;
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

    ASSAULT_BITS_BEAM() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
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
            return BeeGun.createBee(state, user, startPosition, startVelocity);
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
            return WeaponUtils.createBomb(state, user, startPosition, startVelocity);
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
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
            return CR4PCannon.createCR4P(state, user, startPosition, startVelocity);
        }
    },

    DEEP_SMELT() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
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
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
            return Flounderbuss.createFlounder(state, user, startPosition, startVelocity);
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

    HOMING_MISSILE() {

        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createHomingMissile(state, user, startPosition, startVelocity);
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
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
            return KillerBeat.createKillerNotes(state, user, startPosition, startVelocity, extraFields);
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
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
            return Moraygun.createMoray(state, user, startPosition, extraFields);
        }
    },

    MORNING_STAR() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
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
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
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

    ORBITAL_STAR() {
        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
            return OrbitalShield.createOrbitals(state, user);
        }
    },

    PROXIMITY_MINE() {
        @Override
        public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
            return WeaponUtils.createProximityMine(state, user, startPosition, extraFields);
        }
    },

    VENGEFUL_SPIRIT() {

        @Override
        public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
            return WeaponUtils.createVengefulSpirits(state, user, startPosition, extraFields);
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
    public Hitbox initiateSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float... extraFields) {
        Hitbox hbox = performSyncedAttackSingle(state, user, startPosition, startVelocity, extraFields);
        hbox.setAttack(this);
        hbox.setSyncedMulti(false);
        hbox.setExtraFields(extraFields);
        if (state.isServer()) {
            syncAttackSingle(hbox, extraFields, false);
        }
        return hbox;
    }

    /**
     * This performs the actual attack and is overridden in each SyncedAttack that produces a single hitbox
     */
    public Hitbox performSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) { return null; }

    /**
     * Syncs an executed attack with the client by sending them a packet
     * @param hbox: Hitbox that is being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param catchup: Is this being synced as a result of catchup packet for newly joined player or missed create?
     */
    public void syncAttackSingle(Hitbox hbox, float[] extraFields, boolean catchup) {
        if (extraFields.length == 0) {
            HadalGame.server.sendToAllUDP(new Packets.CreateSyncedAttackSingle(hbox.getEntityID(), hbox.getCreator().getEntityID(),
                    catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                    catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), this));
        } else {
            HadalGame.server.sendToAllUDP(new Packets.CreateSyncedAttackSingleExtra(hbox.getEntityID(), hbox.getCreator().getEntityID(),
                    catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                    catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), extraFields, this));
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
    public Hitbox[] initiateSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float... extraFields) {
        Hitbox[] hboxes = performSyncedAttackMulti(state, user, startPosition, startVelocity, extraFields);
        for (Hitbox hbox : hboxes) {
            hbox.setAttack(this);
            hbox.setSyncedMulti(true);
            hbox.setExtraFields(extraFields);
        }
        if (state.isServer() && hboxes.length != 0) {
            syncAttackMulti(hboxes, extraFields, false);
        }
        return hboxes;
    }

    /**
     * This performs the actual attack and is overridden in each SyncedAttack that produces multiple hitbox
     */
    public Hitbox[] performSyncedAttackMulti(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) { return null; }

    /**
     * Syncs an executed attack with the client by sending them a packet
     * @param hboxes: Hitboxes that are being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param catchup: Is this being synced as a result of catchup packet for newly joined player or missed create?
     */
    public void syncAttackMulti(Hitbox[] hboxes, float[] extraFields, boolean catchup) {
        UUID[] hboxID = new UUID[hboxes.length];
        Vector2[] positions = new Vector2[hboxes.length];
        Vector2[] velocities = new Vector2[hboxes.length];
        for (int i = 0; i < hboxes.length; i++) {
            hboxID[i] = hboxes[i].getEntityID();
            positions[i] = catchup ? hboxes[i].getPixelPosition() : hboxes[i].getStartPos();
            velocities[i] = catchup ? hboxes[i].getLinearVelocity() : hboxes[i].getStartVelo();
        }
        if (extraFields.length == 0) {
            HadalGame.server.sendToAllUDP(new Packets.CreateSyncedAttackMulti(hboxID, hboxes[0].getCreator().getEntityID(),
                    positions, velocities, this));
        } else {
            HadalGame.server.sendToAllUDP(new Packets.CreateSyncedAttackMultiExtra(hboxID, hboxes[0].getCreator().getEntityID(),
                    positions, velocities, extraFields, this));
        }
    }
}
