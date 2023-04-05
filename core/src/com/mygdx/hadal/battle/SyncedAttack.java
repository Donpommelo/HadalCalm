package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.attacks.active.*;
import com.mygdx.hadal.battle.attacks.event.Candy;
import com.mygdx.hadal.battle.attacks.event.Eggplant;
import com.mygdx.hadal.battle.attacks.event.Pickup;
import com.mygdx.hadal.battle.attacks.special.Emote;
import com.mygdx.hadal.battle.attacks.special.Ping;
import com.mygdx.hadal.battle.attacks.weapon.*;
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

    AIRBLAST(new Airblast()),
    ASSAULT_BITS_BEAM(new AssaultBitBeam()),
    AMITA(new Amita()),
    BANANA(new BananaProjectile()),
    BATTERING(new Batter()),
    BEE(new Bee(DamageSource.BEE_GUN)),
    BOILER_FIRE(new BoilerFire()),
    BOMB(new Bomb(DamageSource.ANARCHISTS_COOKBOOK)),
    BOOMERANG(new BoomerangProjectile()),
    BOUNCING_BLADE(new BouncingBladeProjectile()),
    CHARGE_BEAM(new ChargeBeamProjectile()),
    COLA(new Cola()),
    CR4P(new CR4P()),
    DEEP_SMELT(new DeepSmelt()),
    DIAMOND_CUTTER(new DiamondCutterProjectile()),
    DUELING_CORK(new DuelingCork()),
    FIST(new Fist()),
    FLOUNDER(new Flounder()),
    FUGU(new Fugu()),
    GRENADE(new Grenade()),
    HEX(new Hex()),
    ICEBERG(new IcebergProjectile()),
    IRON_BALL(new IronBall()),
    KAMABOKO(new Kamaboko()),
    KILLER_NOTES(new KillerNotes()),
    LASER(new Laser()),
    LASER_GUIDED_ROCKET(new LaserGuidedRocketProjectile()),
    LOVE_ARROW(new LoveArrow()),
    MACHINE_GUN_BULLET(new MachineGunBullet()),
    MAELSTROM(new MaelstromProjectile()),
    MINIGUN_BULLET(new MinigunBullet()),
    MORAY(new Moray()),
    MORNING_STAR(new MorningStarProjectile()),
    NEMATOCYTE(new Nematocyte()),
    PEARL(new Pearl()),
    PEPPER(new Pepper()),
    POPPER(new Popper()),
    PUFFBALL(new Puffball()),
    RETICLE_STRIKE(new ReticleStrikeProjectile()),
    RIFT_SPLIT(new RiftSplit()),
    SCRAPRIP(new Scraprip()),
    SCREECH(new Screech()),
    SLODGE(new Slodge()),
    SNIPER_BULLET(new SniperBullet()),
    SPEAR(new Spear(false)),
    SPEAR_NERFED(new Spear(true)),
    STICKY_BOMB(new StickyBomb()),
    STUTTER_LASER(new StutterLaser()),
    TESLA_COIL(new TeslaCoilProjectile()),
    TESLA_ACTIVATION(new TeslaActivation()),
    TORPEDO(new Torpedo()),
    TRICK_SHOT(new TrickShot()),
    TYRAZZAN_REAPER(new TyrazzanReaperProjectile()),
    UNDERMINER_DRILL(new UnderminerDrill()),
    VAJRA(new VajraProjectile()),
    VINE_SEED(new VineSeed()),
    VINE(new Vine()),

    WAVE_BEAM(new WaveBeamProjectile()),
    X_BOMB(new XBomb()),

    ANCHOR(new Anchor()),
    FLASHBANG(new FlashbangProjectile()),
    GHOST_STEP(new GhostStepProjectile()),
    JUMP_KICK(new JumpKickProjectile()),
    HYDRAUlIC_UPPERCUT(new HydraulicUppercutProjectile()),
    NAUTICAL_MINE(new NauticalMineProjectile()),
    MARINE_SNOW(new MarineSnow()),
    BEE_HONEYCOMB(new Bee(DamageSource.HONEYCOMB)),
    BEE_FORAGER(new Bee(DamageSource.FORAGERS_HIVE)),
    BEE_MOUTHFUL(new Bee(DamageSource.MOUTHFUL_OF_BEES)),
    HOMING_MISSILE(new HomingMissile(DamageSource.MISSILE_POD)),
    HOMING_MISSILE_FROGMAN(new HomingMissile(DamageSource.WRATH_OF_THE_FROGMAN)),
    ORBITAL_STAR(new OrbitalStar()),
    PROXIMITY_MINE(new ProximityMineProjectile(DamageSource.PROXIMITY_MINE)),
    PROXIMITY_MINE_BOOK(new ProximityMineProjectile(DamageSource.BOOK_OF_BURIAL)),
    STICK_GRENADE(new StickGrenade()),

    FORCE_OF_WILL(new ForceInvulnerability()),
    SUPPLY_DROP(new Weapon()),

    VENGEFUL_SPIRIT(new VengefulSpirit(DamageSource.SPIRIT_RELEASE)),
    VENGEFUL_SPIRIT_PEACHWOOD(new VengefulSpirit(DamageSource.PEACHWOOD_SWORD)),

    PICKUP(new Pickup()),
    EGGPLANT(new Eggplant()),
    CANDY(new Candy()),

    PING(new Ping()),
    EMOTE(new Emote())

    ;

    private final SyncedAttacker syncedAttacker;

    SyncedAttack(SyncedAttacker syncedAttacker) {
        this.syncedAttacker = syncedAttacker;
    }

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

        Hitbox hbox = syncedAttacker.performSyncedAttackSingle(state, user, startPosition, startVelocity, extraFields);
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
        Hitbox[] hboxes = syncedAttacker.performSyncedAttackMulti(state, user, weaponVelocity, startPosition, startVelocity, extraFields);
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
            syncedAttacker.performSyncedAttackNoHbox(state, user, startPosition, extraFields);
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
