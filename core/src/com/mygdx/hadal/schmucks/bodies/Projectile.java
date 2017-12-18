package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.ProjectileData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Projectile extends Schmuck {

	public float x,y;
	public int width, height;
	public float grav;
	public int lifespan;
	public Vector2 startVelo;
	public float gravityEffect;
	public int durability;
	public int lifeSpan;
	public short filter;
	
	public ProjectileData data;
	
	public Projectile(PlayState state, float x, float y, int width, int height, float grav, int lifespan,
			Vector2 startVelo, short filter, World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.grav = grav;
		this.lifeSpan = lifespan;
		this.filter = filter;
		
		this.startVelo = new Vector2(startVelo);

		state.create(this);
	}
	
	public void create() {
		this.body = BodyBuilder.createBox(world, x, y, width / 2, height / 2, grav, false, true, Constants.BIT_PROJECTILE, 
				(short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY), filter, data);
		this.body.setLinearVelocity(startVelo);
	}
	
	public void setUserData(ProjectileData userData) {
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
