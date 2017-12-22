package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.enemy.StandardRanged;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Enemy extends Schmuck {
				
	
	private Equipable weapon;

	public Enemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width, float height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y);
		
		this.weapon = new StandardRanged(this);
		
		state.create(this);
	}
	
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);
	}

	public void controller(float delta) {
		
		moveState = MoveStates.STAND;
		
		Vector2 player = state.getPlayer().getPosition();
		
		if (player.x > body.getPosition().x) {
			moveState = MoveStates.MOVE_RIGHT;
		} else {
			moveState = MoveStates.MOVE_LEFT;
		}
		
		Vector3 target = new Vector3(state.getPlayer().getPosition().x, state.getPlayer().getPosition().y, 0);
		camera.project(target);
		
		useToolStart(delta, weapon, Constants.ENEMY_HITBOX, (int)target.x, (int)target.y, true);

		if (weapon.reloading) {
			weapon.reload(delta);
		}
		shootCdCount-=delta;

		super.controller(delta);
	}
	
	public void render(SpriteBatch batch) {
		
	}
	
	public void dispose() {
		state.incrementScore(1);
		super.dispose();
	}
}
