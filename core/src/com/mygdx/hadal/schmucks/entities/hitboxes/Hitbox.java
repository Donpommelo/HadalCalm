package com.mygdx.hadal.schmucks.entities.hitboxes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * A hitbox is a box that hits things.
 * @author Trolduborough Tisinger
 */
public class Hitbox extends HadalEntity {

	//default properties. these can be changed using a setter right after the hbox is initialized.
	private static final float DEFAULT_GRAVITY = 0.0f;
	private static final float DEFAULT_DENSITY = 0.0f;
	private static final int DEFAULT_DURABILITY = 1;
	private static final float DEFAULT_FRICTION = 0.0f;
	private static final float DEFAULT_SCALE = 1.0f;
	private static final float DEFAULT_RESITUTION = 0.0f;
	private static final float DEFAULT_DAMAGE_MULTIPLIER = 1.0f;

	//Initial velocity of the hitbox
	protected Vector2 startVelo;

	//lifespan is the time in seconds that the hitbox will exist before timing out.
	protected final float maxLifespan;
	protected float lifeSpan;

	//filter describes the type of schmuck the hitbox will register a hit on (player, enemy or neutral)
	protected short filter;

	//passability describes what types of entities the hitbox can collide with.
	protected short passability = (short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR);

	//grav is the effect of gravity on the hitbox. 1 = normal gravity. 0 = no gravity.
	private float gravity = DEFAULT_GRAVITY;

	//density is the used for certain physics-related hboxes. stuff that needs to rotate based on physics should have a nonzero density
	private float density = DEFAULT_DENSITY;

	//durability is the number of things the hitbox can hit before disappearing.
	private int durability = DEFAULT_DURABILITY;

	//restitution is the hitbox bounciness.
	private float restitution = DEFAULT_RESITUTION;

	//friction is the hitbox slipperiness.
	private float friction = DEFAULT_FRICTION;

	//scale is the hitbox size multiplier.
	private float scale = DEFAULT_SCALE;

	//scale is the hitbox size multiplier.
	private float damageMultiplier = DEFAULT_DAMAGE_MULTIPLIER;

	//sensor is whether the hitbox passes through things it registers a hit on.
	private boolean sensor;

	//procEffects is whether the hitbox activates statuses. The others decide which types of effects should apply to this hbox
	private final boolean procEffects;
	private boolean effectsVisual = true;
	private boolean effectsHit = true;
	private boolean effectsMovement = true;

	//can this hbox be reflected by reflection effects?
	private boolean reflectable = true;

	//Should this hbox's angle be set at creation to match velocity?
	private boolean adjustAngle = false;

	//when calculating things like backstabs, the position of the creator is used instead of the hbox (for things like laser rifle)
	private boolean positionBasedOnUser = false;

	//hitbox user data. This contains on-hit methods
	protected HitboxData data;

	//This is the Schmuck that created the hitbox
	protected final Schmuck creator;

	//strategies contains a bunch of effects that modify a hitbox.
	//add+remove are strategies that will be added/removed from the hitbox next world-step
	private final Array<HitboxStrategy> strategies = new Array<>();
	private final Array<HitboxStrategy> add = new Array<>();
	private final Array<HitboxStrategy> remove = new Array<>();

	//this is the projectile's Sprite and corresponding frames
	protected Animation<TextureRegion> projectileSprite;
	private Sprite sprite;
	private boolean looping;

	//this is the size of the sprite. Usually drawn to be the size of the hbox, but can be made larger/smaller
	protected final Vector2 spriteSize = new Vector2();

	//the synced attack this hbox is a part of as well as the attack's extra fields
	private SyncedAttack attack;
	private float[] extraFields;
	private boolean syncedMulti;

	//does this hbox respond to delete packets from the server? True for hboxes the can be deleted prematurely
	private boolean syncedDeleteNoDelay;
	private boolean syncedDelete;

	private boolean synced = false;

	//when about to despawn, hboxes can be set to flash. This just skips render cycles, so it doesn't use a strategy
	private float flashCount;

	private Fixture wallCollider, dropthroughCollider;

	/**
	 * This constructor is run whenever a hitbox is created. Usually by a schmuck using a weapon.
	 * parameters are pretty much the same as the fields above.
	 */
	public Hitbox(PlayState state, Vector2 startPos, Vector2 size, float lifespan, Vector2 startVelo, short filter,
				  boolean sensor, boolean procEffects, Schmuck creator, Sprite sprite) {
		super(state, startPos, size);
		this.maxLifespan = lifespan;
		this.lifeSpan = lifespan;
		this.filter = filter;
		this.sensor = sensor;
		this.procEffects = procEffects;
		this.creator = creator;

		//Create a new vector to avoid issues with multi-projectile attacks using same velo for all projectiles.
		this.startVelo = new Vector2(startVelo);

		//use Sprite.Nothing for spriteless hitboxes (like ones that just use particles)
		setSprite(sprite);
		setLayer(ObjectLayer.HBOX);
	}

	/**
	 * Create the hitbox body.
	 */
	public void create() {

		if (procEffects) {
			creator.getBodyData().statusProcTime(new ProcTime.CreateHitbox(this));
		}

		this.data = new HitboxData(this);

		this.size.scl(scale);
		this.spriteSize.scl(scale);

		this.body = BodyBuilder.createBox(world, startPos, size, gravity, density, 0.0f, 0.0f, false,
				false, Constants.BIT_PROJECTILE, passability, filter, true, data);

		//Non-sensor hitboxes have a non-sensor fixture attached to it. This is used for hboxes that collide with walls but should pass through enemies
		if (!sensor) {
			wallCollider = FixtureBuilder.createFixtureDef(body, new Vector2(), new Vector2(size), false, 0, 0, restitution, friction,
					Constants.BIT_PROJECTILE, Constants.BIT_WALL, filter);

			wallCollider.setUserData(data);
		}

		setLinearVelocity(startVelo);

		//hboxes that adjust their angle start off transformed.
		if (adjustAngle) {
			setTransform(getPosition(), MathUtils.atan2(getLinearVelocity().y, getLinearVelocity().x));
		}
	}

	/**
	 * Hitboxes track of lifespan.
	 * This is also where hbox strategies are added/removed to avoid having that happen in world.step
	 */
	@Override
	public void controller(float delta) {

		for (HitboxStrategy s : add) {
			strategies.add(s);
			s.create();
		}
		add.clear();

		for (HitboxStrategy s : remove) {
			strategies.removeValue(s, false);
		}
		remove.clear();

		for (HitboxStrategy s : strategies) {
			s.controller(delta);
		}
	}

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		controller(delta);
	}

	@Override
	public void push(Vector2 push) {

		if (!alive) { return; }

		for (HitboxStrategy s : strategies) {
			s.push(push);
		}
	}

	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {

		if (!alive) { return; }

		//this makes the hbox flash when its lifespan is low (set flash lifespan using strategy
		if (flashCount > 0.0f) { return; }

		if (projectileSprite != null) {
			batch.draw(projectileSprite.getKeyFrame(animationTime, looping),
					entityLocation.x - spriteSize.x / 2,
					entityLocation.y - spriteSize.y / 2,
					spriteSize.x / 2, spriteSize.y / 2,
					spriteSize.x, spriteSize.y, -1, 1,
					MathUtils.radDeg * getAngle());
		}
	}

	public void die() {

		if (!alive) { return; }

		for (HitboxStrategy s : strategies) {
			s.die();
			remove.add(s);
		}
	}

	public void onPickup(HadalData picker) {

		if (!alive) { return; }

		for (HitboxStrategy s : strategies) {
			s.onPickup(picker);
		}
	}

	@Override
	public Fixture getMainFixture() {
		if (body == null) { return null; }
		if (sensor) {
			return super.getMainFixture();
		} else {
			return body.getFixtureList().get(1);
		}
	}

	@Override
	public HadalData getHadalData() { return data; }

	public Array<HitboxStrategy> getStrategies() { return strategies; }

	public void addStrategy(HitboxStrategy strat) {	add.add(strat); }

	public void removeStrategy(HitboxStrategy strat) { remove.add(strat); }

	public void removeStrategy(Class<? extends HitboxStrategy> stratType) {
		for (HitboxStrategy strat : strategies) {
			if (strat.getClass().equals(stratType)) {
				remove.add(strat);
			}
		}
	}

	/**
	 * As Default: Upon created, the hitbox tells the client to create a client illusion tracking it
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		if (attack != null) {
			//for catchup packets, resend synced attack packet (otherwise, create packet should already be sent)
			if (catchup) {
				if (syncedMulti) {
					attack.syncAttackMultiServer(startVelo, new Hitbox[] {this}, extraFields, 0, synced, true);
				} else {
					attack.syncAttackSingleServer(this, extraFields, 0, synced, true);
				}
			}
			return null;
		}
		if ((isSyncDefault() || isSyncInstant()) && synced) {
			return new Packets.CreateEntity(entityID, spriteSize, getPixelPosition(), getAngle(), sprite,
					true, isSyncInstant(), ObjectLayer.HBOX, alignType.HITBOX);
		}
		return null;
	}

	@Override
	public void onServerSync() {
		if (body != null && synced && isSyncDefault()) {
			float angle = getAngle();
			if (angle == 0.0f) {
				state.getSyncPackets().add(new PacketsSync.SyncEntity(entityID, getPosition(), getLinearVelocity(),
						state.getTimer()));
			} else {
				state.getSyncPackets().add(new PacketsSync.SyncEntityAngled(entityID, getPosition(), getLinearVelocity(),
						state.getTimer(), angle));
			}
		}
	}

	@Override
	public void onServerSyncFast() {
		if (body != null && synced && isSyncInstant()) {
			float angle = getAngle();
			if (angle == 0.0f) {
				HadalGame.server.sendToAllUDP(new PacketsSync.SyncEntity(entityID, getPosition(), getLinearVelocity(),
						state.getTimer()));
			} else {
				HadalGame.server.sendToAllUDP(new PacketsSync.SyncEntityAngled(entityID, getPosition(), getLinearVelocity(),
						state.getTimer(), angle));
			}
		}
	}

	@Override
	public Object onServerDelete() {
		if (syncedDelete) {
			return new Packets.DeleteEntity(entityID, state.getTimer());
		} else {
			return null;
		}
	}

	@Override
	public void onClientDelete() {
		if (syncedDeleteNoDelay) {
			if (serverDeleteReceived && state.getTimer() >= serverDeleteTimestamp) {
				die();
				serverDeleteReceived = false;
				((ClientState) state).removeEntity(entityID);
			}
		} else {
			if (serverDeleteReceived && state.getTimer() >= serverDeleteTimestamp + PlayState.SYNC_TIME) {
				die();
				serverDeleteReceived = false;
				((ClientState) state).removeEntity(entityID);
			}
		}
	}

	/**
	 * Certain strategies lower the hbox durability. hbox dies when durability reaches 0.
	 */
	public void lowerDurability() {
		this.durability--;
		if (durability <= 0) {
			die();
		}
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
		if (!Sprite.NOTHING.equals(sprite)) {
			projectileSprite = new Animation<>(sprite.getAnimationSpeed(), sprite.getFrames());
			projectileSprite.setPlayMode(sprite.getPlayMode());
			if (!PlayMode.NORMAL.equals(projectileSprite.getPlayMode())) {
				looping = true;
			}
		}
		this.spriteSize.set(size);
	}

	public void setStartVelo(Vector2 startVelo) { this.startVelo = startVelo; }

	public void setLifeSpan(float lifeSpan) { this.lifeSpan = lifeSpan; }

	public void setDurability(int durability) { this.durability = (int) (durability + creator.getBodyData().getStat(Stats.RANGED_PROJ_DURABILITY)); }

	public void setRestitution(float restitution) {	this.restitution = Math.min(1.0f, restitution + creator.getBodyData().getStat(Stats.RANGED_PROJ_RESTITUTION)); }

	public void setGravity(float gravity) { this.gravity = gravity + creator.getBodyData().getStat(Stats.RANGED_PROJ_GRAVITY); }

	public void setDensity(float density) { this.density = density; }

	public void setScale(float scale) { this.scale = scale; }

	public void setFriction(float friction) { this.friction = friction; }

	public void setFilter(short filter) {
		if (body != null) {
			Filter oldFilter = body.getFixtureList().get(0).getFilterData();
			oldFilter.groupIndex = filter;
			body.getFixtureList().get(0).setFilterData(oldFilter);
			this.filter = filter;
		}
	}

	public void setPassability(short passability) { this.passability = passability; }

	public void setDamageMultiplier(float damageMultiplier) { this.damageMultiplier = damageMultiplier; }

	public Vector2 getStartVelo() { return startVelo; }

	public float getMaxLifespan() { return maxLifespan; }

	public float getLifeSpan() { return lifeSpan; }

	public int getDurability() { return durability; }

	public float getRestitution() { return restitution; }

	public float getGravity() { return gravity; }

	public float getScale() { return scale; }

	public Sprite getSprite() { return sprite; }

	public short getFilter() { return filter; }

	public float getDamageMultiplier() { return damageMultiplier; }

	public void setSensor(boolean sensor) { this.sensor = sensor; }

	public boolean isEffectsVisual() { return effectsVisual; }

	public void setEffectsVisual(boolean effectsVisual) { this.effectsVisual = effectsVisual; }

	public boolean isEffectsHit() { return effectsHit; }

	public void setEffectsHit(boolean effectsHit) {	this.effectsHit = effectsHit; }

	public boolean isEffectsMovement() { return effectsMovement; }

	public void setEffectsMovement(boolean effectsMovement) { this.effectsMovement = effectsMovement; }

	public Schmuck getCreator() { return creator; }

	public void makeUnreflectable() { reflectable = false; }

	public boolean isReflectable() { return reflectable; }

	public void setAdjustAngle(boolean adjustAngle) { this.adjustAngle = adjustAngle; }

	public void setPositionBasedOnUser(boolean positionBasedOnUser) { this.positionBasedOnUser = positionBasedOnUser; }

	public boolean isPositionBasedOnUser() { return positionBasedOnUser; }

	public void setSpriteSize(Vector2 spriteSize) { this.spriteSize.set(spriteSize).scl(scale); }

	public void setAttack(SyncedAttack attack) { this.attack = attack; }

	public void setSyncedMulti(boolean syncedMulti) { this.syncedMulti = syncedMulti; }

	public boolean isSynced() { return synced; }

	public void setSynced(boolean synced) { this.synced = synced; }

	public void setExtraFields(float[] extraFields) { this.extraFields = extraFields ;}

	public void setSyncedDeleteNoDelay(boolean syncedDeleteNoDelay) { this.syncedDeleteNoDelay = syncedDeleteNoDelay; }

	public void setSyncedDelete(boolean syncedDelete) { this.syncedDelete = syncedDelete; }

	public float getFlashCount() { return flashCount; }

	public void setFlashCount(float flashCount) { this.flashCount = flashCount; }

	public Fixture getWallCollider() { return wallCollider; }

	public Fixture getDropthroughCollider() { return dropthroughCollider; }

	public void setDropthroughCollider(Fixture dropthroughCollider) { this.dropthroughCollider = dropthroughCollider; }
}
