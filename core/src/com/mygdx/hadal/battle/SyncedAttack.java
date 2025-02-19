package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.attacks.active.*;
import com.mygdx.hadal.battle.attacks.artifact.*;
import com.mygdx.hadal.battle.attacks.enemy.*;
import com.mygdx.hadal.battle.attacks.event.Candy;
import com.mygdx.hadal.battle.attacks.event.Eggplant;
import com.mygdx.hadal.battle.attacks.event.Pickup;
import com.mygdx.hadal.battle.attacks.general.*;
import com.mygdx.hadal.battle.attacks.special.Emote;
import com.mygdx.hadal.battle.attacks.special.Ping;
import com.mygdx.hadal.battle.attacks.weapon.*;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.server.packets.PacketsAttacks;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * A SyncedAttack represents an "attack" sent from server to client containing information about hitboxes produced, hitbox strategies,
 * sound, particles etc. This is used to only send one packet when an attack is executed rather than multiple for each
 * part of the attack. Additionally, this allows for more specific hbox behavior that would be difficult to synchronize
 * with client illusions.
 * <p>
 * As of 1.0.8, these also send info from client to server and can send hitbox-less packets.
 */
public enum SyncedAttack {

    AIRBLAST(new Airblast()),
    ASSAULT_BITS_BEAM(new AssaultBitBeam()),
    AMITA(new Amita()),
    BANANA(new BananaProjectile()),
    BATTERING(new Batter()),
    BEE(new Bee(DamageSource.BEE_GUN)),
    BLOODLETTER(new BloodletterProjectile()),
    BOILER_FIRE(new BoilerFire()),
    BOMB(new Bomb(DamageSource.ANARCHISTS_COOKBOOK)),
    BOOMERANG(new BoomerangProjectile()),
    BOUNCING_BLADE(new BouncingBladeProjectile()),
    CHARGE_BEAM(new ChargeBeamProjectile()),
    COLA(new Cola()),
    CR4P(new CR4P()),
    DEATH_ORB(new Nebula()),
    DEEP_SMELT(new DeepSmelt()),
    DIAMOND_CUTTER(new DiamondCutterProjectile()),
    DIATOM(new Diatom()),
    DUELING_CORK(new DuelingCork()),
    ETHEREAL_HAUNT(new SpiritBombProjectile()),
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
    LEAPFROG(new Leapfrog()),
    LOVE_ARROW(new LoveArrow()),
    MACHINE_GUN_BULLET(new MachineGunBullet()),
    MAELSTROM(new MaelstromProjectile()),
    MAGIC_BEAN(new MagicBean()),
    MINIGUN_BULLET(new MinigunBullet()),
    MORAY(new Moray()),
    MORNING_STAR(new MorningStarProjectile()),
    NEMATOCYTE(new Nematocyte()),
    PEARL(new Pearl()),
    PEPPER(new Pepper()),
    POOL_BALL(new PoolBall()),
    POPPER(new Popper()),
    PROLIFERATOR(new ProliferatorProjectile()),
    PUFFBALL(new Puffball()),
    RECOMBINANT_SHOT(new RecombinantShot()),
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
    TRIDENT(new TridentProjectile()),
    UNDERMINER_DRILL(new UnderminerDrill()),
    URCHIN_NAIL(new UrchinNail()),
    VAJRA(new VajraProjectile()),
    VINE(new Vine(DamageSource.MAGIC_BEANSTALKER)),
    WAVE_BEAM(new WaveBeamProjectile()),
    X_BOMB(new XBomb()),

    ANCHOR(new Anchor()),
    EXPLODING_RETICLE(new ExplodingReticle(DamageSource.VESTIGIAL_CHAMBER)),
    FLASHBANG(new FlashbangProjectile()),
    GHOST_STEP(new GhostStepProjectile()),
    JUMP_KICK(new JumpKickProjectile()),
    KRILL_COMMAND(new KrillCommandProjectile()),
    HYDRAUlIC_UPPERCUT(new HydraulicUppercutProjectile()),
    NAUTICAL_MINE(new NauticalMineProjectile()),
    MARINE_SNOW(new MarineSnow()),
    METEOR_STRIKE(new Meteors(DamageSource.METEOR_STRIKE)),
    METEOR_CONJURANT(new Meteors(DamageSource.CHAOS_CONJURANT)),
    BEE_HONEYCOMB(new Bee(DamageSource.HONEYCOMB)),
    BEE_FORAGER(new Bee(DamageSource.FORAGERS_HIVE)),
    BEE_MOUTHFUL(new Bee(DamageSource.MOUTHFUL_OF_BEES)),
    HOMING_MISSILE(new HomingMissile(DamageSource.MISSILE_POD)),
    HOMING_MISSILE_FROGMAN(new HomingMissile(DamageSource.WRATH_OF_THE_FROGMAN)),
    ORBITAL_STAR(new OrbitalStar()),
    PROXIMITY_MINE(new ProximityMineProjectile(DamageSource.PROXIMITY_MINE)),
    PROXIMITY_MINE_BOOK(new ProximityMineProjectile(DamageSource.BOOK_OF_BURIAL)),
    STICK_GRENADE(new StickGrenade()),

    CALL_OF_WALRUS(new CallofWalrusUse()),
    DEPTH_CHARGE(new DepthChargeUse()),
    FISH_GANG(new FishGangUse()),
    IMMOLATION(new Immolation()),
    MERIDIAN_MAKER(new MeridianMakerProjectile()),
    PORTABLE_SENTRY(new PortableSentryUse()),
    SAMSON_OPTION(new SamsonOptionUse()),
    SHOCK_VAJRA(new Shock(DamageSource.VAJRA)),
    SHOCK_BUCKET(new Shock(DamageSource.BUCKET_OF_BATTERIES)),
    SHOCK_DERMIS(new Shock(DamageSource.VOLATILE_DERMIS)),
    SHOCK_PLUS_MINUS(new Shock(DamageSource.PLUS_MINUS)),
    TAINTED_WATER(new TaintedWaterProjectile()),
    VENGEFUL_SPIRIT(new VengefulSpirit(DamageSource.SPIRIT_RELEASE)),
    VENGEFUL_SPIRIT_PEACHWOOD(new VengefulSpirit(DamageSource.PEACHWOOD_SWORD)),

    HEALING_FIELD(new HealingFieldUse(UnlockArtifact.NOTHING)),
    HEALING_FIELD_MUG(new HealingFieldUse(UnlockArtifact.NUMBER_ONE_BOSS_MUG)),
    SPRING(new SpringLoaderUse()),
    SUPPLY_DROP(new SupplyDropUse()),
    TERRAFORM(new Terraform()),

    INVINCIBILITY(new InvulnerabilityInflict()),
    INVISIBILITY_ON(new InvisibilityOn()),
    INVISIBILITY_OFF(new InvisibilityOff()),
    MELON(new MelonUse()),
    PLUS_MINUS(new PlusMinusUse()),
    RELOADER(new ReloaderUse()),
    RESERVED_FUEL(new ReservedFuelUse()),

    ARTIFACT_AMMO_ACTIVATE(new ArtifactAmmoActivate()),
    ARTIFACT_MAGIC_ACTIVATE(new ArtifactMagicActivate()),
    AMDALHS_LOTUS(new AmdhalsLotusActivate()),
    BRITTLING_POWDER(new BrittlingPowderActivate()),
    COMMUTERS_PARASOL(new CommuterParasolActivate()),
    CROWN_OF_THORNS(new CrownOfThornsActivate()),
    FRACTURE_PLATE(new FracturePlateActivate()),
    GOMEZS_AMYGDALA(new GomezsAmygdalaActivate()),
    KUMQUAT(new KumquatActivate()),
    NURDLER(new NurdlerActivate()),
    OUR_GET_ALONG_SHIRT(new OurGetAlongShirtActivate()),
    PEER_PRESSURE(new PeerPressureSummonAttack()),
    PEPPER_ARTIFACT(new PepperActivate()),
    SKIPPERS_BOX_OF_FUN(new SkippersBoxOfFunActivate()),
    VOLATILE_DERMIS(new VolatileDermisActivate()),
    WHITE_SMOKER(new WhiteSmokerActivate()),

    CELESTIAL_ANOINTMENT(new ArtifactIconActivate(UnlockArtifact.CELESTIAL_ANOINTMENT)),
    CLEPSYDRA(new ArtifactIconActivate(UnlockArtifact.CLEPSYDRAE)),
    GLUTTONOUS_GREY_GLOVE(new ArtifactIconActivate(UnlockArtifact.GLUTTONOUS_GREY_GLOVE)),

    CASTAWAYS_TRAVELOGUE(new ArtifactFuelActivate(UnlockArtifact.CASTAWAYS_TRAVELOGUE)),
    CURSED_CILICE(new ArtifactFuelActivate(UnlockArtifact.CURSED_CILICE)),
    MATTER_UNIVERSALIZER(new ArtifactFuelActivate(UnlockArtifact.MATTER_UNIVERSALIZER)),

    PICKUP(new Pickup()),
    EGGPLANT(new Eggplant()),
    CANDY(new Candy()),

    PING(new Ping()),
    EMOTE(new Emote()),

    CONTACT_DAMAGE(new ContactDamageContinuous()),
    CRAWLER_MELEE(new CrawlerMelee()),
    CRAWLER_RANGED(new CrawlerRanged()),
    DRONE_LASER(new DroneLaser()),
    ENEMY_KAMABOKO_SPAWN(new KamabokoSpawn()),
    ENEMY_KAMABOKO_SPRAY(new KamabokoSpray()),
    SCISSORFISH_ATTACK(new ScissorfishAttack()),
    SPITTLEFISH_ATTACK(new SpittleFishAttack()),
    TORPEDOFISH_ATTACK(new TorpedoFishAttack()),
    BOMBFISH_ATTACK(new BombfishAttack()),
    SNIPERFISH_ATTACK(new SniperFishAttack()),
    TURRET_FLAK_ATTACK(new TurretFlakAttack()),
    TURRET_VOLLEY_ATTACK(new TurretVolleyAttack()),
    SNIPER_RETICLE(new SniperReticle()),

    BOSS_BOUNCY_BALL(new BossBouncyBall()),
    BOSS_FALLING_DEBRIS(new BossFallingDebris()),
    BOSS_FIRE_BREATH(new BossFireBreath()),
    BOSS_POISON_CLOUD(new BossPoisonCloud()),
    BOSS_ROTATING_BEAM(new BossRotatingBeam()),
    BOSS_SWEEPING_BEAM(new BossSweepingBeam()),
    BOSS_SWEEPING_EXPLOSION(new BossSweepingExplosion()),
    BOSS_TRACKING_BEAM(new BossTrackingBeam()),

    FALSE_SUN_APOCALYPSE(new FalseSunApocalypse()),
    FALSE_SUN_BELL(new FalseSunBell()),
    FALSE_SUN_BULLETS(new FalseSunBullets()),
    FALSE_SUN_FIRE_SPIN(new FalseSunFireSpin()),
    FALSE_SUN_LASER_TRAIL(new FalseSunLaserTrail()),
    FALSE_SUN_LASER(new FalseSunLaser()),
    FALSE_SUN_RADIAL(new FalseSunRadial()),
    FALSE_SUN_RETICLE(new FalseSunReticle()),
    FALSE_SUN_SIGH(new FalseSunSigh()),
    FALSE_SUN_STAR_ORBIT(new FalseSunStarOrbit()),
    FALSE_SUN_WILL_O_WISP(new FalseSunWillOWisp()),

    NEPTUNE_ORBITAL(new NeptuneOrbital()),
    NEPTUNE_POISON(new NeptunePoison()),
    NEPTUNE_RADIAL(new NeptuneRadial()),
    NEPTUNE_SCYTHE(new NeptuneScythe()),
    NEPTUNE_SEED(new NeptuneSeed()),
    NEPTUNE_SHADOW(new NeptuneShadow()),
    NEPTUNE_SPOREBURST(new NeptuneSporeburst()),
    NEPTUNE_VINE(new Vine(DamageSource.ENEMY_ATTACK)),

    KING_KAMABOKO_POISON(new KingKamabokoPoison()),
    KING_KAMABOKO_SHOT(new KingKamabokoShot()),
    KING_KAMABOKO_SLODGE(new KingKamabokoSlodge()),

    SERAPH_BOMB(new SeraphBomb()),
    SERAPH_CHARGE(new SeraphCharge()),
    SERAPH_CROSS(new SeraphCross()),
    SERAPH_LOTUS(new SeraphLotus()),
    SERAPH_SPIRAL(new SeraphSpiral()),
    SERAPH_TRAIL(new SeraphTrail())


    ;

    //the synced attacker responsible for creating this attack.
    private final SyncedAttacker syncedAttacker;

    SyncedAttack(SyncedAttacker syncedAttacker) {
        this.syncedAttacker = syncedAttacker;
        this.syncedAttacker.setSyncedAttack(this);
    }

    /**
     * This initiates a synced attack that produces a single hitbox
     * @param state: Current playstate
     * @param user: User that is executing this attack
     * @param startPosition: Starting position of the hitbox produced by this attack
     * @param startVelocity: Starting velocity of the hitbox produced by this attack
     * @param connID: If this is the server syncing an attack from the client, this is that client's connID
     * @param entityID: When the server receives a synced attack from the client, we need to set server entity id before echoing
     * @param origin: Is this initiating the original attack, or relaying one from a client/the host?
     * @param extraFields: Any extra information required for client to replicate this attack
     * @return the hitbox that this attack produces
     */
    public Hitbox initiateSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity,
                                             int connID, int entityID, boolean origin, float... extraFields) {

        Hitbox hbox = syncedAttacker.performSyncedAttackSingle(state, user, startPosition, startVelocity, extraFields);
        hbox.setAttack(this);
        hbox.setSyncedMulti(false);
        hbox.setExtraFields(extraFields);
        if (state.isServer()) {
            if (entityID != 0) {
                hbox.setEntityID(entityID);
            }
            syncAttackSingleServer(hbox, extraFields, connID, hbox.isSynced(), false);
        } else {

            //If this client is the originator of this attack, execute it.
            //Otherwise, we are receiving it from the server and do not need to echo
            // Also do not need to add entity (as that is handled when the packet is received)
            if (origin) {
                syncAttackSingleClient(hbox, extraFields, hbox.isSynced());
                ((ClientState) state).addEntity(hbox.getEntityID(), hbox, hbox.isSynced(), ObjectLayer.HBOX);
            }
        }
        return hbox;
    }

    public Hitbox initiateSyncedAttackSingle(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float... extraFields) {
        return initiateSyncedAttackSingle(state, user, startPosition, startVelocity, 0, 0, true, extraFields);
    }

    /**
     * Syncs an executed attack with the client by sending them a packet
     * @param hbox: Hitbox that is being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param connID: If this is the server syncing an attack from the client, this is that client's connID
     * @param synced: Does this attack create a synced hitbox?
     * @param catchup: Is this being synced as a result of catchup packet for newly joined player or missed create?
     */
    public void syncAttackSingleServer(Hitbox hbox, float[] extraFields, int connID, boolean synced, boolean catchup) {
        Object packet;

        if (synced) {
            if (extraFields.length == 0) {
                packet = new PacketsAttacks.SingleServerDependent(hbox.getEntityID(), hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), this);
            } else {
                packet = new PacketsAttacks.SingleServerDependentExtra(hbox.getEntityID(), hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), extraFields, this);
            }
        } else {
            if (extraFields.length == 0) {
                packet = new PacketsAttacks.SingleServerIndependent(hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), this);
            } else {
                packet = new PacketsAttacks.SingleServerIndependentExtra(hbox.getCreator().getEntityID(),
                        catchup ? hbox.getPixelPosition() : hbox.getStartPos(),
                        catchup ? hbox.getLinearVelocity() : hbox.getStartVelo(), extraFields, this);
            }
        }

        //0 connID indicates a server-created attack. If client created, we don't need to send the packet to the creator.
        if (connID == 0) {
            PacketManager.serverUDPAll(packet);
        } else {
            PacketManager.serverUDPAllExcept(connID, packet);
        }
    }

    /**
     * Syncs an executed attack with the server by sending them a packet
     * @param hbox: Hitbox that is being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param synced: Does this attack create a synced hitbox?
     */
    public void syncAttackSingleClient(Hitbox hbox, float[] extraFields, boolean synced) {
        if (synced) {
            if (extraFields.length == 0) {
                PacketManager.clientUDP(new PacketsAttacks.SingleClientDependent(hbox.getEntityID(),
                        hbox.getStartPos(),
                        hbox.getStartVelo(), this));
            } else {
                PacketManager.clientUDP(new PacketsAttacks.SingleClientDependentExtra(hbox.getEntityID(),
                        hbox.getStartPos(),
                        hbox.getStartVelo(), extraFields, this));
            }
        } else {
            if (extraFields.length == 0) {
                PacketManager.clientUDP(new PacketsAttacks.SingleClientIndependent(
                        hbox.getStartPos(),
                        hbox.getStartVelo(), this));
            } else {
                PacketManager.clientUDP(new PacketsAttacks.SingleClientIndependentExtra(
                        hbox.getStartPos(),
                        hbox.getStartVelo(), extraFields, this));
            }
        }
    }

    /**
     * This initiates a synced attack that produces multiple hitboxes
     * @param state: Current playstate
     * @param user: User that is executing this attack
     * @param weaponVelocity: Where is the weapon pointing? Distinct from startVelocities in the case of spread weapons.
     * @param startPosition: Starting positions of each hitbox produced by this attack
     * @param startVelocity: Starting velocities of each hitbox produced by this attack
     * @param connID: If this is the server syncing an attack from the client, this is that client's connID
     * @param entityID: When the server receives a synced attack from the client, we need to set server entity id before echoing
     * @param origin: Is this initiating the original attack, or relaying one from a client/the host?
     * @param extraFields: Any extra information required for client to replicate this attack
     * @return the hitboxes that this attack produces
     */
    public Hitbox[] initiateSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
            Vector2[] startVelocity, int connID, int[] entityID, boolean origin, float... extraFields) {
        Hitbox[] hboxes = syncedAttacker.performSyncedAttackMulti(state, user, weaponVelocity, startPosition, startVelocity, extraFields);
        for (Hitbox hbox : hboxes) {
            hbox.setAttack(this);
            hbox.setSyncedMulti(true);
            hbox.setExtraFields(extraFields);
        }
        if (hboxes.length != 0) {
            boolean isSynced = hboxes[0].isSynced();

            if (state.isServer()) {
                if (entityID != null) {
                    for (int i = 0; i < hboxes.length; i++) {
                        if (entityID.length > i) {
                            hboxes[i].setEntityID(entityID[i]);
                        }
                    }
                }
                syncAttackMultiServer(weaponVelocity, hboxes, extraFields, connID, isSynced, false);
            } else {
                if (origin) {
                    syncAttackMultiClient(weaponVelocity, hboxes, extraFields, isSynced);
                    for (Hitbox hbox : hboxes) {
                        ((ClientState) state).addEntity(hbox.getEntityID(), hbox, isSynced, ObjectLayer.HBOX);
                    }
                }
            }
        }
        return hboxes;
    }

    public Hitbox[] initiateSyncedAttackMulti(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition,
                                              Vector2[] startVelocity, float... extraFields) {
        return initiateSyncedAttackMulti(state, user, weaponVelocity, startPosition, startVelocity, 0, null, true, extraFields);
    }

    /**
     * Syncs an executed attack with the client by sending them a packet
     * @param hboxes: Hitboxes that are being synced
     * @param extraFields: Any extra fields of the synced attack
     * @param catchup: Is this being synced as a result of catchup packet for newly joined player or missed create?
     */
    public void syncAttackMultiServer(Vector2 weaponVelocity, Hitbox[] hboxes, float[] extraFields, int connID,
                                      boolean isSynced, boolean catchup) {
        int[] hboxID = new int[hboxes.length];
        Vector2[] positions = new Vector2[hboxes.length];
        Vector2[] velocities = new Vector2[hboxes.length];
        for (int i = 0; i < hboxes.length; i++) {
            hboxID[i] = hboxes[i].getEntityID();
            positions[i] = catchup ? hboxes[i].getPixelPosition() : hboxes[i].getStartPos();
            velocities[i] = catchup ? hboxes[i].getLinearVelocity() : hboxes[i].getStartVelo();
        }
        Object packet;
        if (isSynced) {
            if (extraFields.length == 0) {
                packet = new PacketsAttacks.MultiServerDependent(hboxID, hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, this);
            } else {
                packet = new PacketsAttacks.MultiServerDependentExtra(hboxID, hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, extraFields, this);
            }
        } else {
            if (extraFields.length == 0) {
                packet = new PacketsAttacks.MultiServerIndependent(hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, this);
            } else {
                packet = new PacketsAttacks.MultiServerIndependentExtra(hboxes[0].getCreator().getEntityID(),
                        weaponVelocity, positions, velocities, extraFields, this);
            }
        }

        if (connID == 0) {
            PacketManager.serverUDPAll(packet);
        } else {
            PacketManager.serverUDPAllExcept(connID, packet);
        }
    }

    public void syncAttackMultiClient(Vector2 weaponVelocity, Hitbox[] hboxes, float[] extraFields, boolean isSynced) {
        int[] hboxID = new int[hboxes.length];
        Vector2[] positions = new Vector2[hboxes.length];
        Vector2[] velocities = new Vector2[hboxes.length];
        for (int i = 0; i < hboxes.length; i++) {
            hboxID[i] = hboxes[i].getEntityID();
            positions[i] = hboxes[i].getStartPos();
            velocities[i] = hboxes[i].getStartVelo();
        }
        if (isSynced) {
            if (extraFields.length == 0) {
                PacketManager.clientUDP(new PacketsAttacks.MultiClientDependent(hboxID, weaponVelocity, positions, velocities, this));
            } else {
                PacketManager.clientUDP(new PacketsAttacks.MultiClientDependentExtra(hboxID, weaponVelocity, positions, velocities, extraFields, this));
            }
        } else {
            if (extraFields.length == 0) {
                PacketManager.clientUDP(new PacketsAttacks.MultiClientIndependent(weaponVelocity, positions, velocities, this));
            } else {
                PacketManager.clientUDP(new PacketsAttacks.MultiClientIndependentExtra(weaponVelocity, positions, velocities, extraFields, this));
            }
        }
    }

    /**
     * Syncs an attack that does not produce a hitbox.
     *
     * @param state: Current playstate
     * @param user: User that is executing this attack
     * @param startPosition: Starting position of the hitbox produced by this attack
     * @param connID: If this is the server syncing an attack from the client, this is that client's connID
     * @param origin: Is this initiating the original attack, or relaying one from a client/the host?
     * @param independent: Is this processed independently between server and client?
     * @param extraFields: Any extra information required for client to replicate this attack
     */
    public void initiateSyncedAttackNoHbox(PlayState state, Schmuck user, Vector2 startPosition, int connID,
                                           boolean origin, boolean independent, float... extraFields) {

        //independent attacks are processed immediately
        //clients do not process dependent attacks; instead they wait for the server to run them.
        if (independent || state.isServer()) {
            syncedAttacker.performSyncedAttackNoHbox(state, user, startPosition, extraFields);
        }

        //clients relay the attack if they are the creator
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
        if (extraFields.length == 0) {
            packet = new PacketsAttacks.SyncedAttackNoHboxServer(user.getEntityID(), startPos, this);
        } else {
            packet = new PacketsAttacks.SyncedAttackNoHboxExtraServer(user.getEntityID(), startPos, extraFields, this);
        }

        if (connID == 0 || !independent) {
            PacketManager.serverUDPAll(packet);
        } else {
            PacketManager.serverUDPAllExcept(connID, packet);
        }
    }

    public void syncAttackNoHboxClient(Vector2 startPos, boolean independent, float[] extraFields) {
        if (extraFields.length == 0) {
            PacketManager.clientUDP(new PacketsAttacks.SyncedAttackNoHbox(startPos, independent, this));
        } else {
            PacketManager.clientUDP(new PacketsAttacks.SyncedAttackNoHboxExtra(startPos, independent, extraFields, this));
        }
    }
}
