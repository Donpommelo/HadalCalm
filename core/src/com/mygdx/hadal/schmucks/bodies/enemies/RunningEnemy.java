package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.enemy.SpittlefishAttack;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Enemies are Schmucks that attack the player.
 * @author Zachary Tu
 *
 */
public class RunningEnemy extends Enemy {
				
	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	private Equipable weapon;

	private static final float aiCd = 1.0f;
	private float aiCdCount = 0;
    
  	private runningState aiState;

  	private final float jumpPow = 5.0f;
  	
  	private float shortestFraction;
  	private Fixture closestFixture;
  	
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
	public RunningEnemy(PlayState state, float width, float height, int x, int y) {
		super(state, width, height, x, y, enemyType.MISC);
		
		//default enemy weapon is a slow ranged projectile
		this.weapon = new SpittlefishAttack(this);	
		
		this.aiState = runningState.ROAMING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				hitboxfilter, false, bodyData);
		
		//ass super.create() if you want enemy to have feet that process groundedness
	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	@Override
	public void controller(float delta) {
		
		moveState = SchmuckMoveStates.STAND;
		
		switch(aiState) {
		case ROAMING:
			if (Math.random() > 0.5f) {
				moveState = SchmuckMoveStates.MOVE_RIGHT;
			} else {
				moveState = SchmuckMoveStates.MOVE_LEFT;
			}
			break;
		case CHASING:
			Vector2 player = state.getPlayer().getBody().getPosition();
			
			if (player.x > body.getPosition().x) {
				moveState = SchmuckMoveStates.MOVE_RIGHT;
			} else {
				moveState = SchmuckMoveStates.MOVE_LEFT;
			}
			
			Vector3 target = new Vector3(state.getPlayer().getBody().getPosition().x, state.getPlayer().getBody().getPosition().y, 0);
			camera.project(target);
			
			useToolStart(delta, weapon, hitboxfilter, (int)target.x, (int)target.y, true);
			break;
		}
		
		if (aiCdCount < 0) {
			
			if (Math.random() > 0.5f) {
				push(0, jumpPow);
			}
			
			aiCdCount += aiCd;
			aiState = runningState.ROAMING;
			
			shortestFraction = 1.0f;
			
			if (getBody().getPosition().x != state.getPlayer().getBody().getPosition().x || 
					getBody().getPosition().y != state.getPlayer().getBody().getPosition().y) {
				world.rayCast(new RayCastCallback() {

					@Override
					public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
						if (fixture.getUserData() == null) {
							if (fraction < shortestFraction) {
								shortestFraction = fraction;
								closestFixture = fixture;
								return fraction;
							}
						} else if (fixture.getUserData() instanceof PlayerBodyData) {
							if (fraction < shortestFraction) {
								shortestFraction = fraction;
								closestFixture = fixture;
								return fraction;
							}
							
						} 
						return -1.0f;
					}
					
				}, getBody().getPosition(), state.getPlayer().getBody().getPosition());
			}
			
			if (closestFixture != null) {
				if (closestFixture.getUserData() instanceof PlayerBodyData ) {
					aiState = runningState.CHASING;
				}
			}			
		}

		if (weapon.isReloading()) {
			weapon.reload(delta);
		}

		aiCdCount -= delta;

		super.controller(delta);
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {
		
	}
	
	public enum runningState {
		CHASING,
		ROAMING
	}
	
}
