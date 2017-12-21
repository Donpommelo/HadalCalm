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

public class Hitbox extends HadalEntity {

	public float grav;
	public int lifespan;
	public Vector2 startVelo;
	public float gravityEffect;
	public int durability;
	public int lifeSpan;
	public short filter;
	
	public int dura;
	public boolean sensor;
	
	public HitboxData data;
	
	public Hitbox(PlayState state, float x, float y, int width, int height, float grav, int lifespan, int dura,
			Vector2 startVelo, short filter, boolean sensor, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays, width, height, x, y);
		this.grav = grav;
		this.lifeSpan = lifespan;
		this.filter = filter;
		this.sensor = sensor;
		this.dura = dura;		
		
		this.startVelo = new Vector2(startVelo);

		state.create(this);
	}

	public void create() {
		this.body = BodyBuilder.createBox(world, startX, startY, width / 2, height / 2, grav, 0.0f, false, false, Constants.BIT_PROJECTILE, 
				(short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR), filter, sensor, data);
		this.body.setLinearVelocity(startVelo);
		this.body.setTransform(startX / PPM, startY / PPM, (float) Math.atan(startVelo.y / startVelo.x));
	}
	
	public void setUserData(HitboxData userData) {
		data = userData;
	}
	
	public void controller(float delta) {
		lifeSpan--;
		if (lifeSpan <= 0) {
			state.destroy(this);
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		
	}
}
