package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.WorldUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * enemies are schmucks that attack the player.
 * @author Doddad Drabbondonato
 */
public class Enemy extends Schmuck {
	
	//This is the type of enemy
	private final EnemyType type;

    //this is the size of the enemy's hitbox
	protected final Vector2 hboxSize;
    
    //is this enemy a boss? (makes it show up in the boss ui)
    private boolean isBoss = false;
    
	//the default speed that the enemy moves around
	protected int moveSpeed;
	
	//This is the default cooldown between attacks for the enemy
	private float attackCd;
	
	//this is the amount of currency dropped when this enemy is defeated
	private final int scrapDrop;
	
	//This is the range that the enemy will be able to detect targets
    protected static final float aiRadius = 2000;
    
    //This is the entity this enemy is trying to attack
  	protected HadalEntity attackTarget;

  	//Can this enemy track attack targets through walls? (currently only used for gss boss)
  	protected boolean trackThroughWalls;

  	//This is the entity this enemy is trying to move towards. (if null, the enemy moves towards the attack target)
  	private HadalEntity moveTarget;
  	
	//This is the duration until the enemy will attack again
    private float aiAttackCdCount = 0.0f;
    
    //This is the duration until the enemy will perform the next action in its action queue (or secondary action queue)
    private float aiActionCdCount = 0.0f;
    private float aiSecondaryActionCdCount = 0.0f;
	
  	//These are used for raycasting to determining whether the player is in vision of the enemy.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
	//this is the angle that the enemy is currently attacking in/angle they are turning towards.
	protected float attackAngle, desiredAngle;
	
	//This is a dummy event in the map that the enemy is moving towards
	private Event eventTarget;
	
	//The action queues and current action hold the enemy' queued up actions. (secondary action is for 2 different actions occurring simultaneously)
	private final Array<EnemyAction> actions;
	private EnemyAction currentAction;
	
	private final Array<EnemyAction> secondaryActions;
	private EnemyAction currentSecondaryAction;
	
	private final TextureRegion hpSprite;
	private static final float uiScale = 0.15f;
	private static final float hpX = 10.0f;
	private static final float hpY = 30.0f;

	 //This is the event that spawner this enemy. Is null for the client and for enemies spawned in other ways.
    protected final SpawnerSchmuck spawner;
    
	public Enemy(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, EnemyType type, short filter, float baseHp, float attackCd, int scrapDrop, SpawnerSchmuck spawner) {
		super(state, startPos, size, type.getName(), filter, baseHp);
		this.hboxSize = hboxSize;
		this.type = type;
		this.attackCd = attackCd;
		this.scrapDrop = scrapDrop;
		this.spawner = spawner;

		this.hpSprite = Sprite.UI_MAIN_HEALTHBAR.getFrame();
		
		this.actions = new Array<>();
		this.secondaryActions = new Array<>();
	}
	
	@Override
	public void create() {
		super.create();
		
		this.body = BodyBuilder.createBox(world, startPos, hboxSize, 0, 1, 0, 0, false, true, Constants.BIT_ENEMY, (short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE),
				hitboxfilter, false, getBodyData());

		//on death, the enemy will activate its spawner's connected event (if existent)
		//this also increments player score if coop/single player arena
		getBodyData().addStatus(new Status(state, getBodyData()) {
			
			@Override
			public void onDeath(BodyData perp, DamageSource source) {
				if (spawner != null) {
					spawner.onDeath();
				}
				if (state.getMode().getTeamMode().equals(SettingTeamMode.TeamMode.COOP) && perp instanceof PlayerBodyData playerData) {
					state.getMode().processPlayerScoreChange(state, playerData.getPlayer(), 1);
				}
			}
		});
		
		//if boss, activate on boss spawn statuses for all players
		if (isBoss) {

			if (state.isServer()) {
				//this method should be overloaded for bosses that scale to the number of players
				multiplayerScaling(HadalGame.server.getNumPlayers());

				state.getPlayer().getPlayerData().statusProcTime(new ProcTime.AfterBossSpawn(this));
				for (User user : HadalGame.server.getUsers().values()) {
					if (user.getPlayer() != null) {
						if (user.getPlayer().getPlayerData() != null) {
							user.getPlayer().getPlayerData().statusProcTime(new ProcTime.AfterBossSpawn(this));
						}
					}
				}
			} else {
				multiplayerScaling(HadalGame.client.getNumPlayers());
			}
		}
	}

	private final Vector2 dist = new Vector2();
	private final Vector2 targetPosition = new Vector2();
	@Override
	public void controller(float delta) {		
		super.controller(delta);

		//move towards movement target, if existent.
		if (eventTarget != null) {
			targetPosition.set(eventTarget.getPosition());
			dist.set(targetPosition).sub(getPosition());

			//upon reaching target, conclude current action immediately and move on to the next action
			if (dist.len2() <= moveSpeed * moveSpeed * delta * delta) {
				setTransform(targetPosition, 0);
				setLinearVelocity(0, 0);
				eventTarget = null;
				
				aiActionCdCount = 0;
				currentAction = null;
			} else {
				setLinearVelocity(dist.nor().scl(moveSpeed));
			}
		}
		
		//decrement timers for actions
		if (aiActionCdCount > 0) {
			aiActionCdCount -= delta;
		} else {

			//if enemy is done with their current action, decrement attack cooldown
			if (aiAttackCdCount > 0) {
				aiAttackCdCount -= delta;
			}
		}
		if (aiSecondaryActionCdCount > 0) {
			aiSecondaryActionCdCount -= delta;
		}

		//Action finishing action, attempt to perform next action. If action queue is empty, begin cooldown until next attack
		if (aiActionCdCount <= 0 || currentAction == null) {
			if (actions.isEmpty()) {
				//if we have no more actions left, we start our cooldown until our next attack
				if (aiAttackCdCount <= 0) {
					aiAttackCdCount = attackCd;
					acquireTarget();
					attackInitiate();
				}
			}
			while (!actions.isEmpty()) {
				//get next action and add it begin executing it. Set its duration as our cooldown
				currentAction = actions.removeIndex(0);
				aiActionCdCount = currentAction.getDuration();
				currentAction.execute();
				if (aiActionCdCount > 0.0f) { break; }
			}
		}
		
		//Do the same with secondary action
		if (aiSecondaryActionCdCount <= 0 || currentSecondaryAction == null) {
			while (!secondaryActions.isEmpty()) {
				currentSecondaryAction = secondaryActions.removeIndex(0);
				aiSecondaryActionCdCount = currentSecondaryAction.getDuration();
				currentSecondaryAction.execute();
				if (aiSecondaryActionCdCount > 0.0f) { break; }
			}
		}
	}
	
	/**
	 * draws enemy
	 */
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		
		boolean visible = false;
		
		//draw hp bar if certain effects are used
		if (state.getPlayer().getPlayerData() != null) {
			if (state.getPlayer().getPlayerData().getStat(Stats.HEALTH_VISIBILITY) > 0) {
				visible = true;
			}
		}
		
		if (visible && !isBoss) {
			float hpRatio;

			hpRatio = getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP);
			entityLocation.set(getPixelPosition());
			batch.draw(hpSprite, hpX + entityLocation.x - hboxSize.x / 2, hpY + entityLocation.y - hboxSize.y / 2,
				hpSprite.getRegionWidth() * uiScale * hpRatio, hpSprite.getRegionHeight() * uiScale);
		}
	}
	
	/**
	 * This is run when the enemy performs an action. Override in child classes.
	 */
	public void attackInitiate() {}
	
	/**
	 * This is used by the enemy to find a valid target
	 */
	private final Vector2 homeLocation = new Vector2();
	private final Vector2 entityWorldLocation = new Vector2();
	public void acquireTarget() {
		
		attackTarget = null;
		
		entityWorldLocation.set(getPosition());
		//query nearby units
		world.QueryAABB((fixture -> {
			if (fixture.getUserData() instanceof final BodyData bodyData) {
				homeAttempt = bodyData.getSchmuck();
				homeLocation.set(homeAttempt.getPosition());
				shortestFraction = 1.0f;

				if (WorldUtil.preRaycastCheck(entityWorldLocation, homeLocation)) {
				  	world.rayCast((fixture1, point, normal, fraction) -> {
						if (fixture1.getFilterData().categoryBits == Constants.BIT_WALL && !trackThroughWalls) {
							if (fraction < shortestFraction) {
							  shortestFraction = fraction;
							  closestFixture = fixture1;
							  return fraction;
							}
						} else if (fixture1.getUserData() instanceof final BodyData bodyData2) {
							if (bodyData2.getSchmuck().getHitboxfilter() != hitboxfilter) {
							  if (fraction < shortestFraction) {

								  //enemies will not see invisible units
								  if (bodyData2.getStatus(Invisibility.class) == null) {
									  shortestFraction = fraction;
									  closestFixture = fixture1;
									  return fraction;
								  }
							  }
							}
						}
						return -1.0f;
					}, entityWorldLocation, homeLocation);
					if (closestFixture != null) {
						if (closestFixture.getUserData() instanceof BodyData) {
							attackTarget = ((BodyData) closestFixture.getUserData()).getSchmuck();
						}
					}
				}
			}
			return true;
		}), entityWorldLocation.x - aiRadius, entityWorldLocation.y - aiRadius,
			entityWorldLocation.x + aiRadius, entityWorldLocation.y + aiRadius);
	}
	
	@Override
	public boolean queueDeletion() {
		//defeated enemy drops eggplants in cooperative mode
		if (alive && state.getMode().getTeamMode().equals(SettingTeamMode.TeamMode.COOP)) {
			WeaponUtils.spawnScrap(state, scrapDrop, getPixelPosition(), true, false);
		}
		return super.queueDeletion();
	}
	
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuck p) {
			if (isBoss) {

				//clear the boss ui for clients
				if (p.currentHp <= 0.0f) {
					state.clearBoss();
				}
			}
		}
	}
	
	/**
	 * When created in the server, tell the client what kind of enemy was created to sync
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreateEnemy(entityID, getPixelPosition(), type, hitboxfilter, isBoss, name);
	}

	@Override
	public void dispose() {
		super.dispose();
		
		//this is here to prevent the client from not updating the last, fatal instance of damage in the ui
		if (isBoss) {
			state.clearBoss();
		}
	}
	
	public void setMoveTarget(HadalEntity moveTarget) { this.moveTarget = moveTarget; }
	
	public void setAttackTarget(HadalEntity attackTarget) { this.attackTarget = attackTarget; }

	/**
	 * Get the target we are moving towards. If not set, we are moving towards the same unit we are attacking
	 */
	public HadalEntity getMoveTarget() {
		if (moveTarget != null) {
			if (moveTarget.isAlive()) {
				return moveTarget;
			}
		}
		return attackTarget;
	}

	public EnemyType getEnemyType() { return type; }

	public void setBoss(boolean isBoss) { this.isBoss = isBoss; }
	
	public boolean isBoss() { return isBoss; }
	
	public void multiplayerScaling(int numPlayers) {}
	
	public Vector2 getHboxSize() { return hboxSize; }

	public void setName(String name) { this.name = name; }

	public void setMoveSpeed(int moveSpeed) { this.moveSpeed = moveSpeed; }
	
	public void setMovementTarget(Event movementTarget) { this.eventTarget = movementTarget; }

	public Array<EnemyAction> getActions()  {return actions; }

	public Array<EnemyAction> getSecondaryActions() { return secondaryActions; }

	public float getAttackAngle() {	return attackAngle; }

	public void setAttackAngle(float attackAngle) {	this.attackAngle = attackAngle;	}

	public void setDesiredAngle(float desiredAngle) { this.desiredAngle = desiredAngle; }
	
	public void setAttackCd(float attackCd) { this.attackCd = attackCd; }
}