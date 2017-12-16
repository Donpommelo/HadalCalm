package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.ProjectileData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Projectile extends Schmuck{

	private Fixture proj;
	
	public Vector2 startVelo;
	public float gravityEffect;
	public int durability;
	
	public Projectile(PlayState state, float x, float y, int width, int height, Vector2 startVelo, short filter,
			World world, OrthographicCamera camera, RayHandler rays) {
		super(state, world, camera, rays);
		this.body = BodyBuilder.createBox(world, x, y, width / 2, height / 2, false, true, Constants.BIT_PROJECTILE, 
				(short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY), filter);
		
		this.body.setLinearVelocity(startVelo);
		
		this.proj = this.body.getFixtureList().get(0);
	}
	
	public void setUserData(ProjectileData userData) {
		proj.setUserData(userData);
	}
}
