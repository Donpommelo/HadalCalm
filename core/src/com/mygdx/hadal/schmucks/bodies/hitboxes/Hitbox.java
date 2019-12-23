package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ClientIllusion.alignType;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.statuses.StatusProcTime;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import java.util.ArrayList;

/**
 * A hitbox is a box that hits things.
 * @author Zachary Tu
 *
 */
public class Hitbox extends HadalEntity {

	//Initial velocity of the hitbox
	protected Vector2 startVelo;
		
	//lifespan is the time in seconds that the hitbox will exist before timing out.
	protected float maxLifespan, lifeSpan;
	
	//filter describes the type of body the hitbox will register a hit on .(player, enemy or neutral)
	protected short filter;
	
	//grav is the effect of gravity on the hitbox. 1 = normal gravity. 0 = no gravity.
	protected float gravity = 0.0f;
		
	//durability is the number of things the hitbox can hit before disappearing.
	protected int durability = 1;
	
	//restitution is the hitbox bounciness.
	protected float restitution = 0.0f;
	
	//friction is the hitbox slipperyness.
	protected float friction = 0.0f;
		
	//sensor is whether the hitbox passes through things it registers a hit on.
	protected boolean sensor;
	
	//can this hbox be reflected by reflection effects?
	private boolean reflectable = true;
	
	//hitbox user data. This contains on-hit method
	protected HitboxData data;
	
	//This is the Schmuck that created the hitbox
	protected Schmuck creator;
	
	//strategies contains a bunch of effects that modify a hitbox.
	//add+remove are strategies that will be added/removed from the hitbox next world-step
	private ArrayList<HitboxStrategy> strategies, add, remove;
	
	protected Animation<TextureRegion> projectileSprite;
	private Sprite sprite;
	
	/**
	 * This constructor is run whenever a hitbox is created. Usually by a schmuck using a weapon.
	 * @param : pretty much the same as the fields above.
	 */
	public Hitbox(PlayState state, Vector2 startPos, Vector2 size, float lifespan, Vector2 startVelo, short filter, boolean sensor, boolean procEffects, Schmuck creator, Sprite sprite) {
		super(state, startPos, size);
		this.maxLifespan = lifespan;
		this.lifeSpan = lifespan;
		this.filter = filter;
		this.sensor = sensor;
		this.creator = creator;
		
		//Create a new vector to avoid issues with multi-projectile attacks using same velo for all projectiles.
		this.startVelo = new Vector2(startVelo);
		
		this.strategies = new ArrayList<HitboxStrategy>();
		this.add = new ArrayList<HitboxStrategy>();
		this.remove = new ArrayList<HitboxStrategy>();
		
		if (!sprite.equals(Sprite.NOTHING)) {
			this.sprite = sprite;
			projectileSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
		}
		
		if (procEffects) {
			creator.getBodyData().statusProcTime(StatusProcTime.HITBOX_CREATION, creator.getBodyData(), 0, null, creator.getBodyData().getCurrentTool(), this);
		}
	}
	
	/**
	 * Create the hitbox body. User data is initialized separately.
	 */
	public void create() {

		this.data = new HitboxData(state, this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 0.0f, 0.0f, 0.0f, false, false, Constants.BIT_PROJECTILE, 
				(short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR),
				filter, true, data);
		
		if (!sensor) {
			body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(), new Vector2(size), false, 0, 0, restitution, friction,
				Constants.BIT_SENSOR, Constants.BIT_WALL, filter));
		}
		
		setLinearVelocity(startVelo);
	}
	
	/**
	 * Hitboxes need to keep track of lifespan.
	 * This also makes hitboxes angled in the direction of their velocity. Overload this if you don't want that.
	 */
	public void controller(float delta) {
		
		increaseAnimationTime(delta);
		
		for (HitboxStrategy s : add) {
			strategies.add(s);
			s.create();
		}
		add.clear();
		
		for (HitboxStrategy s : remove) {
			strategies.remove(s);
		}
		remove.clear();
		
		for (HitboxStrategy s : strategies) {
			s.controller(delta);
		}
	}
	
	@Override
	public void push(Vector2 push) {
		
		if (!alive) {
			return;
		}
		
		for (HitboxStrategy s : strategies) {
			s.push(push);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		
		if (!alive) {
			return;
		}
		
		for (HitboxStrategy s : strategies) {
			s.render(batch);
		}
		
		if (projectileSprite != null) {
			batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
					getPixelPosition().x - size.x / 2, 
					getPixelPosition().y - size.y / 2, 
					size.x / 2, size.y / 2,
					size.x, size.y, 1, 1, 
					(float) Math.toDegrees(getOrientation()) + 180);
		}
	}
	
	public void die() {
		
		if (!alive) {
			return;
		}
		
		for (HitboxStrategy s : strategies) {
			s.die();
		}
	}
	
	@Override
	public HadalData getHadalData() {
		return data;
	}	
	
	public ArrayList<HitboxStrategy> getStrategies() {
		return strategies;
	}
	
	public void addStrategy(HitboxStrategy strat) {
		add.add(strat);
	}
	
	public void removeStrategy(HitboxStrategy strat) {
		remove.add(strat);
	}

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
	public Object onServerCreate() {
		return new Packets.CreateEntity(entityID.toString(), new Vector2(size), getPixelPosition(), sprite, ObjectSyncLayers.HBOX, alignType.HITBBOX);
	}
	
	public float getMaxLifespan() {
		return maxLifespan;
	}
	
	public float getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public void lowerDurability() {
		this.durability--;
		if (durability <= 0) {
			die();
		}
	}
	
	public void setDurability(int durability) {
		this.durability = (int) (durability + creator.getBodyData().getStat(Stats.RANGED_PROJ_DURABILITY));
	}
	
	public void setRestitution(float restitution) {
		this.restitution = restitution + creator.getBodyData().getStat(Stats.RANGED_PROJ_RESTITUTION);
	}
	
	public void setGravity(float gravity) {
		this.gravity = gravity + creator.getBodyData().getStat(Stats.RANGED_PROJ_GRAVITY);
	}

	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	public short getFilter() {
		return filter;
	}

	public void setFilter(short filter) {
		this.filter = filter;
	}

	public Vector2 getStartVelo() {
		return startVelo;
	}

	public void setStartVelo(Vector2 startVelo) {
		this.startVelo = startVelo;
	}

	public boolean isSensor() {
		return sensor;
	}

	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}

	public Schmuck getCreator() {
		return creator;
	}

	public void setCreator(Schmuck creator) {
		this.creator = creator;
	}

	public void makeUnreflectable() {
		reflectable = false;
	}
	
	public boolean isReflectable() {
		return reflectable;
	}
}
