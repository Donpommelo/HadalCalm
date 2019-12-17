package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class Boss extends Enemy {
				
	protected int hp, moveSpeed;
	private float attackCd;
    private float aiAttackCdCount = 0.0f;
    private float aiActionCdCount = 0.0f;
	
  	//These are used for raycasting to determing whether the player is in vision of the fish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
	protected int width, height, hbWidth, hbHeight;
	protected float scale;
	
	protected float attackAngle;
	
	private Event movementTarget;
	
	private ArrayList<BossAction> actions;
	private BossAction currentAction;
	
	protected Sprite sprite;
	
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
	public Boss(PlayState state, int x, int y, int width, int height, int hbWidth, int hbHeight, float scale, enemyType type, short filter, int hp, int moveSpeed, float attackCd, Sprite sprite) {
		super(state, hbWidth * scale, hbHeight * scale, x, y, type, filter);
		this.width = width;
		this.height = height;
		this.hbWidth = hbWidth;
		this.hbHeight = hbHeight;
		this.scale = scale;
		
		this.attackCd = attackCd;
		this.hp = hp;
		this.moveSpeed = moveSpeed;
		
		this.sprite = sprite;
		
		this.actions = new ArrayList<BossAction>();
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY,  hbWidth * scale, hbHeight * scale, 0, 1, 0, false, false, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, bodyData);
		
		//temp way of more Hp
		this.bodyData.addStatus(new StatChangeStatus(state, Stats.MAX_HP, hp, bodyData));
	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		if (movementTarget != null) {
			if (movementTarget.getBody() != null) {
				Vector2 dist = movementTarget.getPosition().sub(getPosition()).scl(PPM);
				
				if ((int)dist.len2() <= 100) {
					setLinearVelocity(0, 0);
					movementTarget = null;
					
					aiActionCdCount = 0;
					currentAction = null;
					
				} else {
					setLinearVelocity(dist.nor().scl(moveSpeed));
				}
			}
		}
		
		if (aiActionCdCount > 0) {
			aiActionCdCount -= delta;
		} else {
			if (aiAttackCdCount > 0) {
				aiAttackCdCount -= delta;
			}
		}
		
		if (aiAttackCdCount <= 0) {
			aiAttackCdCount = attackCd;
			acquireTarget();
			attackInitiate();
		}

		if (aiActionCdCount <= 0 || currentAction == null) {
			if (!actions.isEmpty()) {
				currentAction = actions.remove(0);
				aiActionCdCount = currentAction.getDuration();
				
				currentAction.execute();
			} else {
				if (aiAttackCdCount <= 0) {
					aiAttackCdCount = attackCd;
				}
			}
		}
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {}
	
	public void attackInitiate() {};
	
	public void acquireTarget() {
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
								if (fixture.getUserData() == null) {
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
								target = ((BodyData)closestFixture.getUserData()).getSchmuck();
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
	
	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
	
	public Event getMovementTarget() {
		return movementTarget;
	}

	public void setMovementTarget(Event movementTarget) {
		this.movementTarget = movementTarget;
	}

	public ArrayList<BossAction> getActions() {
		return actions;
	}

	public float getAttackAngle() {
		return attackAngle;
	}

	public void setAttackAngle(float attackAngle) {
		this.attackAngle = attackAngle;
	}
	
}