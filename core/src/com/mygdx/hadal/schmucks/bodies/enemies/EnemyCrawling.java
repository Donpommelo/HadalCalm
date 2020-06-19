package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * Enemies are Schmucks that attack the player.
 * Crawling enemies move right and left along the floor.
 * These enemies can rotate to face the player.
 * @author Zachary Tu
 *
 */
public class EnemyCrawling extends Enemy {
	
	//this the frequency that the physics occurs
	private final static float controllerInterval = 1 / 60f;
		
	//this is the boss's sprite
	private Animation<TextureRegion> floatingSprite;

	private float moveDirection, moveSpeed, minRange, maxRange;
	private CrawlingState currentState;
	
	private Fixture feet;
	private FeetData feetData;
	
	public EnemyCrawling(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, String name, Sprite sprite, EnemyType type, float startAngle, short filter, int hp, float attackCd, int scrapDrop, SpawnerSchmuck spawner) {
		super(state, startPos, size, hboxSize, name, sprite, type, filter, hp, attackCd, scrapDrop, spawner);
		
		this.moveDirection = startAngle;
		this.moveSpeed = 1.0f;
		this.currentState = CrawlingState.STILL;
		
		if (!sprite.equals(Sprite.NOTHING)) {
			this.floatingSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeedFast, sprite.getFrames());
			this.floatingSprite.setPlayMode(PlayMode.LOOP_PINGPONG);
		}
	}

	@Override
	public void create() {
		super.create();
		body.setGravityScale(1.0f);
		
		Filter filter = getMainFixture().getFilterData();
		filter.maskBits = (short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_DROPTHROUGHWALL);
		getMainFixture().setFilterData(filter);
		
		if (state.isServer()) {
			this.feetData = new FeetData(UserDataTypes.FEET, this); 
			
			this.feet = this.body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(0.5f,  - hboxSize.y / 2), new Vector2(hboxSize.x - 2, hboxSize.y / 8), true, 0, 0, 0, 0,
					Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL), hitboxfilter));
			
			feet.setUserData(feetData);
		}
	}

	private Vector2 force = new Vector2();
	private Vector2 currentVel = new Vector2();
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		grounded = feetData.getNumContacts() > 0;
		
		switch (currentState) {
		case AVOID_PITS:
			if (grounded) {
				processCollision(true);
			}
			break;
		case BACK_FORTH:
			processCollision(false);
			break;
		case CHASE:
			if (target != null) {				
				if (target.isAlive()) {
					moveSpeed = 1.0f;
					float dist = getPixelPosition().x - target.getPixelPosition().x;
					if (dist > maxRange) {
						moveDirection = -1.0f;
					} else if (dist < -maxRange) {
						moveDirection = 1.0f;
					} else if (dist < minRange && dist > 0) {
						moveDirection = 1.0f;
					} else if (dist > -minRange && dist < 0) {
						moveDirection = -1.0f;
					} else {
						if (dist > 0) {
							moveDirection = -1.0f;
						} else {
							moveDirection = 1.0f;
						}
						moveSpeed = 0.0f;
					}
				}
			}
			break;
		case STILL:
			moveSpeed = 0;
			break;
		default:
			break;
		}

		//This line ensures that this runs every 1/60 second regardless of computer speed.
		controllerCount += delta;
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;
						
			currentVel.set(getLinearVelocity());

			float desiredXVel = getBodyData().getXGroundSpeed() * moveDirection * moveSpeed;
			
			float accelX = 0.0f;
			
			if (Math.abs(desiredXVel) > Math.abs(currentVel.x)) {
				accelX = getBodyData().getXGroundAccel();
			} else {
				accelX = getBodyData().getXGroundDeaccel();
			}
			
			float newX = accelX * desiredXVel + (1 - accelX) * currentVel.x;
			force.set(newX - currentVel.x, 0).scl(getMass());

			applyLinearImpulse(force);
		}
	}
	
	/**
	 * This method is used by crawling enemies to process running into walls/edges of platforms
	 */
	private Vector2 endPt = new Vector2();
	private final static float distCheck = 2.0f;
	private float shortestFraction;
	private void processCollision(boolean avoidPits) {
		
		if (moveDirection == 0) {
			moveDirection = 1.0f;
		}
		
		endPt.set(getPosition()).add(distCheck * moveDirection, 0);
		shortestFraction = 1.0f;
		
		//raycast in the direction we are walking.
		if (getPosition().x != endPt.x || getPosition().y != endPt.y) {
			
			state.getWorld().rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
					if (fixture.getFilterData().categoryBits == (short) Constants.BIT_WALL) {
						if (fraction < shortestFraction) {
							shortestFraction = fraction;
							return fraction;
						}
					}
					return -1.0f;
				}
			}, getPosition(), endPt);
		}
		
		//if we are running into a wall, we turn around
		if (shortestFraction < 1.0f) {
			moveDirection = -moveDirection;
		} else if (avoidPits) {
			
			//if we avoid pits, raycast in the direction we are walking downwards
			endPt.set(getPosition()).add(moveDirection * distCheck, -distCheck);
			shortestFraction = 1.0f;
			if (getPosition().x != endPt.x || getPosition().y != endPt.y) {
				state.getWorld().rayCast(new RayCastCallback() {

					@Override
					public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
						if (fixture.getFilterData().categoryBits == (short) (Constants.BIT_WALL) ||  fixture.getFilterData().categoryBits == (short) Constants.BIT_DROPTHROUGHWALL) {
							if (fraction < shortestFraction) {
								shortestFraction = fraction;
								return fraction;
							}
						}
						return -1.0f;
					}
				}, getPosition(), endPt);
			}
			
			//if we see nothing, we are standing next to a pit and we turn around
			if (shortestFraction == 1.0f) {
				moveDirection = -moveDirection;
			}
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		
		boolean flip = false;
		
		if (moveDirection < 0) {
			flip = true;
		} else if (moveDirection > 0) {
			flip = false;
		}

		batch.draw((TextureRegion) floatingSprite.getKeyFrame(animationTime, true), 
				(flip ? 0 : size.x) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - getHboxSize().y / 2, 
				size.x / 2,
				(flip ? 1 : -1) * size.y / 2, 
				(flip ? 1 : -1) * size.x, size.y, 1, 1, 0);
		
		super.render(batch);
	}
	
	@Override
	public void onServerSync() {
		HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), currentVel, moveDirection, entityAge, false));
		HadalGame.server.sendToAllUDP(new Packets.SyncSchmuck(entityID.toString(), moveState, getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP)));
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncEntity) {
			Packets.SyncEntity p = (Packets.SyncEntity) o;
			if (body != null) {
				serverPos.set(p.pos);
				body.setLinearVelocity(p.velocity);
			}
			moveDirection = p.angle;
		} else {
			super.onClientSync(o);
		}
	}
	
	private Vector2 originPt = new Vector2();
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		originPt.set(getPixelPosition()).add(new Vector2(startVelo).nor().scl(getHboxSize().x / 2));
		return originPt;
	}
	
	public float getMoveDirection() { return moveDirection; }

	public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }

	public void setCurrentState(CrawlingState currentState) { this.currentState = currentState; }

	public void setMinRange(float minRange) { this.minRange = minRange; }

	public void setMaxRange(float maxRange) { this.maxRange = maxRange; }

	public enum CrawlingState {
		BACK_FORTH,
		AVOID_PITS,
		CHASE,
		STILL
	}
}
