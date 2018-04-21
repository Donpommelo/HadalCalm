package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

/**
 * A hitbox is a box that hits things.
 * @author Zachary Tu
 *
 */
public class Hitbox extends HadalEntity {

	//grav is the effect of gravity on the hitbox. 1 = normal gravity. 0 = no gravity.
	protected float grav;
	
	//Initial velocity of the hitbox
	protected Vector2 startVelo;
		
	//lifespan is the time in seconds that the hitbox will exist before timing out.
	protected float lifeSpan;
	
	//filter describes the type of body the hitbox will register a hit on .(player, enemy or neutral)
	protected short filter;
	
	//durability is the number of things the hitbox can hit before disappearing.
	protected int dura;
	
	//restitution is the hitbox bounciness.
	protected float rest;
	
	//friction is the hitbox slipperyness.
	protected float friction = 1.0f;
		
	//sensor is whether the hitbox passes through things it registers a hit on.
	protected boolean sensor;
	
	//hitbox user data. This contains on-hit method
	protected HitboxData data;
	
	//This is the Schmuck that created the hitbox
	protected Schmuck creator;
	
	private ArrayList<HitboxStrategy> strategies, add, remove;
	
	/**
	 * This constructor is run whenever a hitbox is created. Usually by a schmuck using a weapon.
	 * @param : pretty much the same as the fields above.
	 */
	public Hitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura, float rest,
			Vector2 startVelo, short filter, boolean sensor, Schmuck creator) {
		super(state, width, height, x, y);
		this.grav = grav;
		this.lifeSpan = lifespan;
		this.filter = filter;
		this.sensor = sensor;
		this.dura = dura;		
		this.rest = rest;
		this.creator = creator;
		
		//Create a new vector to avoid issues with multi-projectile attacks using same velo for all projectiles.
		this.startVelo = new Vector2(startVelo);
		
		this.strategies = new ArrayList<HitboxStrategy>();
		this.add = new ArrayList<HitboxStrategy>();
		this.remove = new ArrayList<HitboxStrategy>();
	}
	
	/**
	 * Create the hitbox body. User data is initialized separately.
	 */
	public void create() {
		
		this.data = new HitboxData(state, this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width / 2, height / 2, grav, 0.0f, 0, 0, false, false, Constants.BIT_PROJECTILE, 
				(short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR),
				filter, true, data);
		this.body.setLinearVelocity(startVelo);
		
		if (!sensor) {
			body.createFixture(FixtureBuilder.createFixtureDef(width / 2 - 2, height / 2 - 2, 
					new Vector2(1 / 4 / PPM,  1 / 4 / PPM), false, 0, 0, rest, friction,
				Constants.BIT_SENSOR, Constants.BIT_WALL, filter));
		}
		
		for (HitboxStrategy s : strategies) {
			s.create();
		}
	}
	
	/**
	 * Hitboxes need to keep track of lifespan.
	 * This also makes hitboxes angled in the direction of their velocity. Overload this if you don't want that.
	 */
	public void controller(float delta) {
		
		for (HitboxStrategy s : add) {
			strategies.add(s);
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
	public void push(float impulseX, float impulseY) {		
		for (HitboxStrategy s : strategies) {
			s.push(impulseX, impulseY);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		for (HitboxStrategy s : strategies) {
			s.render(batch);
		}
	}
	
	public void die() {
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

	public float getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(float lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public int getDura() {
		return dura;
	}

	public void setDura(int dura) {
		this.dura = dura;
	}

	public short getFilter() {
		return filter;
	}

	public void setFilter(short filter) {
		this.filter = filter;
	}

	public float getGrav() {
		return grav;
	}

	public void setGrav(float grav) {
		this.grav = grav;
	}

	public Vector2 getStartVelo() {
		return startVelo;
	}

	public void setStartVelo(Vector2 startVelo) {
		this.startVelo = startVelo;
	}

	public float getRest() {
		return rest;
	}

	public void setRest(float rest) {
		this.rest = rest;
	}
	
	public float getFriction() {
		return friction;
	}

	public void setFriction(float friction) {
		this.friction = friction;
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
	
}
