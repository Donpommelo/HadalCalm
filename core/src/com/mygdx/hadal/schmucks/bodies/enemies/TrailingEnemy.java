package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.enemy.StandardMelee;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import box2dLight.RayHandler;

/**
 * Enemies are Schmucks that attack the player.
 * @author Zachary Tu
 *
 */
public class TrailingEnemy extends Enemy {
				
	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	private Equipable weapon;
    
    public Vector2 direction;
    
    public static final float moveCd = 0.75f;
    public float moveCdCount = 0;
    
    public static final float moveMag = 7.5f;

    public static final float trailCd = 1/60f;
    public float trailCountCount = 0;
    
    public static final float aiCd = 0.5f;
    public float aiCdCount = 0;
    
    public static final float maxTrailSpeed = 15.0f;

    //Fixtures and user data
  	protected FixtureDef sensorDef;
  	protected Fixture sensor;
  	protected HitboxData sensorData;
  	
  	private trailingState aiState;
  	private Vector2 wallhug;

  	float shortestFraction;
  	Fixture closestFixture;
  	
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
	public TrailingEnemy(PlayState state, World world, OrthographicCamera camera, RayHandler rays, float width, float height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y);
		
		//default enemy weapon is a slow ranged projectile
		this.weapon = new StandardMelee(this);	
		
		this.aiState = trailingState.ROAMING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 0, 1, 0f, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);
		
		this.sensorData = new HitboxData(state, world, null) {
			public void onHit(HadalData fixB) {
				if (fixB == null) {
					if (aiState.equals(trailingState.ROAMING)) {
						aiState = trailingState.WALLHUGGING;
						aiCdCount = aiCd;
						wallhug = getBody().getLinearVelocity().nor().scl(moveMag / 2).rotate(45);
					}
				}
			}
		};
		
		sensorDef = FixtureBuilder.createFixtureDef(width * 2, height * 2, new Vector2(0,0), true, 0, 
				Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_PLAYER), Constants.PLAYER_HITBOX);
		sensor = body.createFixture(sensorDef);
		sensor.setUserData(sensorData);
	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	public void controller(float delta) {
		
		moveState = MoveStates.STAND;
		if (trailCountCount < 0 && aiState.equals(trailingState.TRAILING)) {
			trailCountCount += trailCd;

			float desiredXVel = 0.0f;
			float desiredYVel = 0.0f;
			
			if (target != null) {
				if (target.getBody() != null) {
					desiredXVel = target.getBody().getPosition().x > getBody().getPosition().x ? maxTrailSpeed : -maxTrailSpeed;
					desiredYVel = target.getBody().getPosition().y > getBody().getPosition().y ? maxTrailSpeed : -maxTrailSpeed;
				}
			}

			Vector2 currentVel = body.getLinearVelocity();
			
			float accelX = 0.0f;
			float accelY = 0.0f;
			
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = grounded ? bodyData.groundXAccel : bodyData.airXAccel;
			} else {
				accelX = grounded ? bodyData.groundXDeaccel : bodyData.airXDeaccel;
			}
			
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			
			if (Math.abs(desiredYVel) > Math.abs(currentVel.y)) {
				accelY = grounded ? bodyData.groundYAccel : bodyData.airYAccel;
			} else {
				accelY = grounded ? bodyData.groundYDeaccel : bodyData.airYDeaccel;
			}
			
			float newY = accelY * desiredYVel + (1 - accelY) * currentVel.y;
			
			Vector2 force = new Vector2(newX - currentVel.x, newY - currentVel.y).scl(body.getMass());
			body.applyLinearImpulse(force, body.getWorldCenter(), true);
		}
		
		switch (aiState) {
		case ROAMING:
			direction = new Vector2(
					state.getPlayer().getBody().getPosition().x - getBody().getPosition().x,
					state.getPlayer().getBody().getPosition().y - getBody().getPosition().y).nor().scl(moveMag);			
			break;
		case WALLHUGGING:
			direction = wallhug;
			break;
		case CHASING:
			Vector3 target = new Vector3(state.getPlayer().getBody().getPosition().x, state.getPlayer().getBody().getPosition().y, 0);
			camera.project(target);
			
			useToolStart(delta, weapon, Constants.ENEMY_HITBOX, (int)target.x, (int)target.y, true);
			
			direction = new Vector2(
					state.getPlayer().getBody().getPosition().x - getBody().getPosition().x,
					state.getPlayer().getBody().getPosition().y - getBody().getPosition().y).nor().scl(moveMag / 8);			
			break;
		default:
			break;
		
		}
		
		if (moveCdCount < 0) {
			moveCdCount += moveCd;
			if (direction != null) {
				push(direction.x, direction.y);
			}
		}
		
		if (aiCdCount < 0) {
			aiCdCount += aiCd;
			if (target != null) {
				aiState = trailingState.TRAILING;
			} else {
				aiState = trailingState.ROAMING;
			}
			
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
				
				if (closestFixture != null) {
					if (closestFixture.getUserData() instanceof PlayerBodyData ) {
						aiState = trailingState.CHASING;
					}
				}		
			}
				
		}

		shootCdCount-=delta;
		shootDelayCount-=delta;
		
		//If the delay on using a tool just ended, use thte tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		moveCdCount -= delta;
		trailCountCount -= delta;
		aiCdCount -= delta;
	}
	
	/**
	 * draws enemy
	 */
	public void render(SpriteBatch batch) {
		
	}
	
	public enum trailingState {		CHASING,
		ROAMING,
		TRAILING,
		WALLHUGGING
	}
}