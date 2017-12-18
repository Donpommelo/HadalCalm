package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Enemy extends Schmuck{
		
	public float height, width;
	
	public BodyData bodyData;
	
	public Enemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width, float height) {
		super(state, world, camera, rays);
		this.width = width;
		this.height = height;
		
		state.create(this);
	}
	
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, 500, 300, width, height, 1, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER),
				Constants.ENEMY_HITBOX, bodyData);
	}

	public void controller(float delta) {
		Vector3 target = new Vector3(state.getPlayer().getPosition().x, state.getPlayer().getPosition().y, 0);
		camera.project(target);
	//	bodyData.currentTool.reload();
	//	bodyData.currentTool.mouseClicked(state, bodyData, Constants.ENEMY_HITBOX, (int)target.x, (int)target.y, world, camera, rays);
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public void dispose() {
		super.dispose();
	}
}
