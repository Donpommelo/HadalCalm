package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class FloatingEnemy extends SteeringEnemy {
				
	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	protected Equipable weapon;
    
    //moveCd determines how much time until the fish processes ai again.
    private static final float aiRoamCd = 0.75f;
    private static final float aiChaseCd = 3.0f;
    private float aiCdCount = 0;
    
  	//These are used for raycasting to determing whether the player is in vision of the fish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
	private Sprite sprite;
	protected Animation<? extends TextureRegion> fishSprite;
	
	private final static float scale = 0.25f;

	protected SteeringBehavior<Vector2> roam;
	
	/**
	 * Enemy constructor is run when an enemy spawner makes a new enemy.
	 * Most fields here are stats for gdx ai chasing
	 */
	public FloatingEnemy(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, enemyType type,
			float maxLinSpd, float maxLinAcc, float maxAngSpd, float maxAngAcc, float boundingRad, float decelerationRad, short filter, int baseHp, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(size).scl(scale), new Vector2(hboxSize).scl(scale), type, maxLinSpd, maxLinAcc, filter, baseHp, spawner);
		
		this.moveState = SchmuckMoveStates.FISH_ROAMING;
		
		this.sprite = sprite;
		this.fishSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		super.create();
		
		roam = new Wander<Vector2>(this)
				.setWanderOffset(100)
				.setWanderRadius(100)
				.setWanderRate(25)
				.setWanderOrientation(10);
		setTarget(state.getPlayer(), roam);
	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	@Override
	public void controller(float delta) {
		
		switch (moveState) {
		case FISH_CHASING:
			if (target.isAlive()) {
				float bodyAngle = (float) (getOrientation() + Math.PI / 2);
				float angleToTarget = target.getPosition().sub(getPosition()).angleRad();
				
				if (Math.abs(angleToTarget - bodyAngle) <= Math.PI / 3) {
					useToolStart(delta, weapon, hitboxfilter, target.getPixelPosition(), true);
				}
			} else {
				moveState = SchmuckMoveStates.FISH_ROAMING;
				setTarget(state.getPlayer(), roam);
			}
			break;
		default:
			break;
		}
		
		super.controller(delta);
		
		//When processing ai, fish attempt to raycast towards player.
		if (aiCdCount < 0) {
			aiCdCount += aiRoamCd;
			
			moveState = SchmuckMoveStates.FISH_ROAMING;
			setTarget(state.getPlayer(), roam);
			
			final HadalEntity me = this;
			world.QueryAABB((new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() instanceof BodyData) {
						homeAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
						shortestFraction = 1.0f;
						
						
					  	if (getPosition().x != homeAttempt.getPosition().x || 
					  			getPosition().y != homeAttempt.getPosition().y) {
					  		world.rayCast(new RayCastCallback() {

								@Override
								public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
									if (fixture.getFilterData().categoryBits == (short)Constants.BIT_WALL && fraction < shortestFraction) {
										if (fraction < shortestFraction) {
											shortestFraction = fraction;
											closestFixture = fixture;
											return fraction;
										}
									} else if (fixture.getUserData() instanceof BodyData) {
										if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != hitboxfilter) {
											if (fraction < shortestFraction) {
												shortestFraction = fraction;
												closestFixture = fixture;
												return fraction;
											}
										}
									} 
									return -1.0f;
								}
								
							}, getPosition(), homeAttempt.getPosition());
							if (closestFixture != null) {
								if (closestFixture.getUserData() instanceof BodyData) {
									moveState = SchmuckMoveStates.FISH_CHASING;
									aiCdCount += aiChaseCd;

									setTarget(((BodyData)closestFixture.getUserData()).getSchmuck(), 
											new Pursue<Vector2>(me, ((BodyData)closestFixture.getUserData()).getSchmuck()));
								}
							} 
						}
					}
					return true;
				}
			}), 
				getPosition().x - aiRadius, getPosition().y - aiRadius, 
				getPosition().x + aiRadius, getPosition().y + aiRadius);				
		}
		
		//Fish must manually reload their singular weapon.
		if (weapon.isReloading()) {
			weapon.reload(delta);
		}
		
		//process cooldowns
		aiCdCount -= delta;
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {
		boolean flip = false;
		
		if (getOrientation() < 0) {
			flip = true;
		}
		
		batch.draw((TextureRegion) fishSprite.getKeyFrame(animationTime, true), 
				getPixelPosition().x - hboxSize.y / 2, 
				(flip ? size.y : 0) + getPixelPosition().y - hboxSize.x / 2, 
				hboxSize.y / 2, 
				(flip ? -1 : 1) * hboxSize.x / 2,
				size.x, (flip ? -1 : 1) * size.y, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, getPixelPosition(), size, sprite, getLinearVelocity(), 0.5f, false);
		}
		return super.queueDeletion();
	}
}