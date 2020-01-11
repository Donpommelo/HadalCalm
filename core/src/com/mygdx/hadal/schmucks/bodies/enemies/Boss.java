package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Bosses are enemies with certain actions
 * @author Zachary Tu
 *
 */
public class Boss extends Enemy {
	
	//the default speed that the boss moves around
	protected int moveSpeed;
	
	//This is the default cooldown between attacks for the boss
	private float attackCd;
	
	//This is the duration until the boss will attack gain
    private float aiAttackCdCount = 0.0f;
    
    //This is the duration until the boss will perform the next action in its action queue (or secondary action queue)
    private float aiActionCdCount = 0.0f;
    private float aiSecondaryActionCdCount = 0.0f;
	
  	//These are used for raycasting to determing whether the player is in vision of the fish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
	//this is the angle that the boss is currently attacking in
	protected float attackAngle;
	
	//This is a dummy event in the map that the boss is moving towards
	private Event movementTarget;
	
	//The action queues and current action hold the boss' queued up actions. (secondary action is for 2 different actions occurring simultaneously)
	private ArrayList<BossAction> actions;
	private BossAction currentAction;
	
	private ArrayList<BossAction> secondaryActions;
	private BossAction currentSecondaryAction;
	
	//this is the boss's sprite
	protected Sprite sprite;

	public Boss(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, enemyType type, short filter, int baseHp, int moveSpeed, float attackCd,	SpawnerSchmuck spawner, Sprite sprite) {
		super(state, startPos, size, hboxSize, type, filter, baseHp, spawner);
		
		this.attackCd = attackCd;
		this.moveSpeed = moveSpeed;
		this.sprite = sprite;
		
		this.actions = new ArrayList<BossAction>();
		this.secondaryActions = new ArrayList<BossAction>();
	}
	
	@Override
	public void create() {
		super.create();
		
		this.body = BodyBuilder.createBox(world, startPos, hboxSize, 0, 10, 0, false, false, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY | Constants.BIT_PLAYER),
				hitboxfilter, false, bodyData);
		
//		body.setType(BodyType.KinematicBody);
	}

	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		//move towards movement target, if existent.
		if (movementTarget != null) {
			if (movementTarget.getBody() != null) {
				Vector2 dist = movementTarget.getPixelPosition().sub(getPixelPosition());
				
				//upon reaching target, conclude current action immediately and move on to the next action
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
		
		//decrement timers for actions
		if (aiActionCdCount > 0) {
			aiActionCdCount -= delta;
		} else {
			if (aiAttackCdCount > 0) {
				aiAttackCdCount -= delta;
			}
		}
		if (aiSecondaryActionCdCount > 0) {
			aiSecondaryActionCdCount -= delta;
		}
		
		//after attack cooldown, acquire target and initiate next attack.
		if (aiAttackCdCount <= 0) {
			aiAttackCdCount = attackCd;
			acquireTarget();
			attackInitiate();
		}

		//Action finishing action, attempt to perform next action. If action queue is empty, begin cooldown until next attack
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
		
		//Do the same with secondary action
		if (aiSecondaryActionCdCount <= 0 || currentSecondaryAction == null) {
			if (!secondaryActions.isEmpty()) {
				currentSecondaryAction = secondaryActions.remove(0);
				aiSecondaryActionCdCount = currentSecondaryAction.getDuration();
				currentSecondaryAction.execute();
			}
		}
	}
	
	public void attackInitiate() {};
	
	public void acquireTarget() {
		
		target = null;
		
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
								if (fixture.getUserData() instanceof BodyData) {
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
	
	/**
	 * This is called every engine tick. The server schmuck sends a packet to the corresponding client schmuck.
	 * This packet updates movestate, hp, fuel and flashingness
	 */
	@Override
	public void onServerSync() {
		super.onServerSync();
		HadalGame.server.sendToAllUDP(new Packets.SyncBoss(bodyData.getCurrentHp() / bodyData.getStat(Stats.MAX_HP)));
	}
	
	public void setMoveSpeed(int moveSpeed) { this.moveSpeed = moveSpeed; }
	
	public Event getMovementTarget() { return movementTarget; }

	public void setMovementTarget(Event movementTarget) { this.movementTarget = movementTarget; }

	public ArrayList<BossAction> getActions()  {return actions; }

	public ArrayList<BossAction> getSecondaryActions() { return secondaryActions; }

	public void setSecondaryActions(ArrayList<BossAction> secondaryActions) { this.secondaryActions = secondaryActions; }

	public float getAttackAngle() {	return attackAngle; }

	public void setAttackAngle(float attackAngle) {	this.attackAngle = attackAngle;	}

	public void setAttackCd(float attackCd) { this.attackCd = attackCd; }	
}
