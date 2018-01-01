package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * Enemies are Schmucks that attack the player.
 * @author Zachary Tu
 *
 */
public class Enemy extends Schmuck {
				
	
	public HadalEntity target;
	
	/**
	 * Enemy constructor is run when an enemy spawner makes a new enemy.
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param width: width of enemy
	 * @param height: height of enemy
	 * @param x: enemy starting x position.
	 * @param y: enemy starting x position.
	 */
	public Enemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width, float height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y);
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);
		
		//ass super.create() if you want enemy to have feet that process groundedness
	}

	/**
	 * Enemy ai goes here. Defaut enemy behaviour just walks right/left towards player and fires weapon.
	 */
	public void controller(float delta) {
		
		

		super.controller(delta);
	}
	
	/**
	 * draws enemy
	 */
	public void render(SpriteBatch batch) {
		
	}
	
	/**
	 * Deletes enemy. Currently also increments game score.
	 */
	public void dispose() {
		state.incrementScore(1);
		super.dispose();
	}
}
