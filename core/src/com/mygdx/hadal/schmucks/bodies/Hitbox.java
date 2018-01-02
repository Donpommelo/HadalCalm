package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;
import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * A hitbox is a box that hits things.
 * @author Zachary Tu
 *
 */
public class Hitbox extends HadalEntity {

	//grav is the effect of gravity on the hitbox. 1 = normal gravity. 0 = no gravity.
	public float grav;
	
	//Initial velocity of the hitbox
	public Vector2 startVelo;
		
	//lifespan is the time in seconds that the hitbox will exist before timing out.
	public float lifeSpan;
	
	//filter describes the type of body the hitbox will register a hit on .(player, enemy or neutral)
	public short filter;
	
	//durability is the number of things the hitbox can hit before disappearing.
	public int dura;
	
	//restitution is the hitbox bounciness.
	public float rest;
	
	//sensor is whether the hitbox passes through things it registers a hit on.
	public boolean sensor;
	
	//hitbox user data. This contains on-hit method
	public HitboxData data;
	
	//This is the Schmuck that created the hitbox
	public Schmuck creator;
	
	/**
	 * This constructor is run whenever a hitbox is created. Usually by a schmuck using a weapon.
	 * @param : pretty much the same as the fields above.
	 */
	public Hitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura, float rest,
			Vector2 startVelo, short filter, boolean sensor, World world, OrthographicCamera camera, RayHandler rays, Schmuck creator) {
		super(state, world, camera, rays, width, height, x, y);
		this.grav = grav;
		this.lifeSpan = lifespan;
		this.filter = filter;
		this.sensor = sensor;
		this.dura = dura;		
		this.rest = rest;
		this.creator = creator;
		
		//Create a new vector to avoid issues with multi-projectile attacks using same velo for all projectiles.
		this.startVelo = new Vector2(startVelo);
	}

	/**
	 * Create the hitbox body. User data is initialized separately.
	 */
	public void create() {
		this.body = BodyBuilder.createBox(world, startX, startY, width / 2, height / 2, grav, 0.0f, rest, false, false, Constants.BIT_PROJECTILE, 
				(short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR), filter, sensor, data);
		this.body.setLinearVelocity(startVelo);
		
		//Rotate hitbox to match angle of fire.
		float newAngle = (float)(Math.atan2(startVelo.y , startVelo.x));
		Vector2 newPosition = new Vector2(startX / PPM, startY / PPM).add(startVelo.nor());
		this.body.setTransform(newPosition.x, newPosition.y, newAngle);
	}
	
	/**
	 * This sets a hitbox's user data. It should always be called immediately after creating the hitbox body by the hitboxfactory of a weapon.
	 * The reason this is done is b/c the hitbox user data needs the hitbox as an input but is created as an anonymous inner class.
	 * This lets us avoid having multiple projectile classes that need data passed to them from a weapon.
	 * @param userData: the entity's user data
	 */
	public void setUserData(HitboxData userData) {
		data = userData;
		
		//I don't know if this will ever be necessary, but better to be safe. 
		//I think this can be solved with some clever <? extends whatever> but idk
		hadalData = userData;
	}
	
	/**
	 * Hitboxes need to keep track of lifespan.
	 */
	public void controller(float delta) {
		lifeSpan -= delta;
		if (lifeSpan <= 0) {
			state.destroy(this);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}
}
