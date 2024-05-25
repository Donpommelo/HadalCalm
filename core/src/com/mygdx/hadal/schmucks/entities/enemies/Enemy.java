package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.PickupUtils;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.EnemyStrategy;
import com.mygdx.hadal.strategies.enemy.FollowRallyPoints;
import com.mygdx.hadal.strategies.enemy.TargetNoPathfinding;
import com.mygdx.hadal.strategies.enemy.TargetPathfinding;
import com.mygdx.hadal.utils.b2d.HadalBody;

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
    
	//This is the default cooldown between attacks for the enemy
	private float attackCd;
	
	//this is the amount of currency dropped when this enemy is defeated
	private final int scrapDrop;
	
    //This is the entity this enemy is trying to attack
  	protected HadalEntity attackTarget;

  	//This is the entity this enemy is trying to move towards. (if null, the enemy moves towards the attack target)
  	private HadalEntity moveTarget;

	private final Vector2 moveVector = new Vector2();
	private boolean approachTarget;

	//This is the duration until the enemy will attack again
    private float aiAttackCdCount = 0.0f;
    
    //This is the duration until the enemy will perform the next action in its action queue (or secondary action queue)
    private float aiActionCdCount = 0.0f;
    private float aiSecondaryActionCdCount = 0.0f;
	
	//this is the angle that the enemy is currently attacking in/angle they are turning towards.
	protected float attackAngle, desiredAngle;
	
	//The action queues and current action hold the enemy' queued up actions. (secondary action is for 2 different actions occurring simultaneously)
	private final Array<EnemyAction> actions;
	private EnemyAction currentAction;
	
	private final Array<EnemyAction> secondaryActions;
	private EnemyAction currentSecondaryAction;
	
	private final TextureRegion hpSprite;
	private static final float uiScale = 0.15f;
	private static final float hpX = 10.0f;
	private static final float hpY = 30.0f;

	private final Array<EnemyStrategy> strategies = new Array<>();
	private final Array<EnemyStrategy> add = new Array<>();
	private final Array<EnemyStrategy> remove = new Array<>();

	public Enemy(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, EnemyType type, short filter,
				 float baseHp, float attackCd, int scrapDrop) {
		super(state, startPos, size, type.getName(), filter, baseHp);
		this.hboxSize = hboxSize;
		this.type = type;
		this.attackCd = attackCd;
		this.scrapDrop = scrapDrop;

		this.hpSprite = Sprite.UI_MAIN_HEALTHBAR.getFrame();
		
		this.actions = new Array<>();
		this.secondaryActions = new Array<>();

		setupPathingStrategies();
	}
	
	@Override
	public void create() {
		super.create();
		
		this.body = new HadalBody(getBodyData(), startPos, hboxSize, BodyConstants.BIT_ENEMY,
				(short) (BodyConstants.BIT_WALL | BodyConstants.BIT_SENSOR | BodyConstants.BIT_PROJECTILE), hitboxFilter)
				.setSensor(false)
				.addToWorld(world);

		//this also increments player score if coop/single player arena
		getBodyData().addStatus(new Status(state, getBodyData()) {
			
			@Override
			public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
				for (EnemyStrategy s : strategies) {
					s.die(perp, source);
					remove.add(s);
				}
			}
		});
	}

	@Override
	public void controller(float delta) {		
		super.controller(delta);

		processEnemyStrategies();
		for (EnemyStrategy s : strategies) {
			s.controller(delta);
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
					if (approachTarget) {
						attackInitiate();
					}
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

	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		processEnemyStrategies();
	}

	private void processEnemyStrategies() {
		for (EnemyStrategy s : add) {
			strategies.add(s);
			s.create();
		}
		add.clear();

		for (EnemyStrategy s : remove) {
			strategies.removeValue(s, false);
		}
		remove.clear();
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		
		boolean visible = false;
		
		//draw hp bar if certain effects are used
		if (HadalGame.usm.getOwnPlayer() != null) {
			if (HadalGame.usm.getOwnPlayer().getPlayerData() != null) {
				if (HadalGame.usm.getOwnPlayer().getPlayerData().getStat(Stats.HEALTH_VISIBILITY) > 0) {
					visible = true;
				}
			}
		}

		if (visible && !isBoss) {
			float hpRatio;

			hpRatio = getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP);
			batch.draw(hpSprite, hpX + entityLocation.x - hboxSize.x / 2, hpY + entityLocation.y - hboxSize.y / 2,
				hpSprite.getRegionWidth() * uiScale * hpRatio, hpSprite.getRegionHeight() * uiScale);
		}

		for (EnemyStrategy s : strategies) {
			s.render(batch, animationTime);
		}
	}

	/**
	 * This is used by the enemy to find a valid target
	 */
	public void acquireTarget() {
		for (EnemyStrategy s : strategies) {
			s.acquireTarget();
		}
	}

	/**
	 * This is run when the enemy performs an action. Override in child classes.
	 */
	public void attackInitiate() {}

	@Override
	public boolean queueDeletion() {

		//defeated enemy drops eggplants in cooperative mode
		if (alive && SettingTeamMode.TeamMode.COOP.equals(state.getMode().getTeamMode())) {
			PickupUtils.spawnScrap(state, this, getPixelPosition(), getLinearVelocity(), scrapDrop, true, false);
		}
		return super.queueDeletion();
	}

	@Override
	public void onServerSync() {
		for (EnemyStrategy s : strategies) {
			Object packet = s.onServerSync();
			if (packet != null) {
				state.getSyncPackets().add(packet);
				return;
			}
		}
		super.onServerSync();
	}

	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuck p) {
			if (isBoss) {

				//clear the boss ui for clients
				if (p.hpPercent <= 0.0f) {
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
		return new Packets.CreateEnemy(entityID, getPixelPosition(), type, hitboxFilter, isBoss, name);
	}

	@Override
	public void dispose() {
		super.dispose();
		
		//this is here to prevent the client from not updating the last, fatal instance of damage in the ui
		if (isBoss) {
			state.clearBoss();
		}
	}

	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		for (EnemyStrategy s : strategies) {
			Vector2 projectileOrigin = s.getProjectileOrigin(startVelo, projSize);
			if (projectileOrigin != null) {
				return projectileOrigin;
			}
		}
		return super.getProjectileOrigin(startVelo, projSize);
	}

	public void setupPathingStrategies() {
		if (!BotManager.rallyPoints.isEmpty() && this instanceof EnemySwimming) {
			addStrategy(new TargetPathfinding(state, this));
		} else {
			addStrategy(new TargetNoPathfinding(state, this, false));
		}
		addStrategy(new FollowRallyPoints(state, this));
	}

	public Vector2 getMoveVector() { return moveVector; }

	public void setApproachTarget(boolean approachTarget) { this.approachTarget = approachTarget; }

	public boolean isApproachTarget() { return approachTarget; }

	public void addStrategy(EnemyStrategy strat) {	add.add(strat); }

	public void setMoveTarget(HadalEntity moveTarget) { this.moveTarget = moveTarget; }
	
	public void setAttackTarget(HadalEntity attackTarget) { this.attackTarget = attackTarget; }

	public HadalEntity getAttackTarget() { return attackTarget; }

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

	public void setMovementTarget(Event movementTarget, float moveSpeed) {
		for (EnemyStrategy s : strategies) {
			s.setRallyEvent(movementTarget, moveSpeed);
		}
	}

	public EnemyType getEnemyType() { return type; }

	public void setBoss(boolean isBoss) { this.isBoss = isBoss; }
	
	public boolean isBoss() { return isBoss; }
	
	public Vector2 getHboxSize() { return hboxSize; }

	public void setName(String name) { this.name = name; }

	public void setAiActionCdCount(float aiActionCdCount) { this.aiActionCdCount = aiActionCdCount; }

	public void setCurrentAction(EnemyAction currentAction) { this.currentAction = currentAction; }

	public Array<EnemyAction> getActions() { return actions; }

	public Array<EnemyAction> getSecondaryActions() { return secondaryActions; }

	public float getAttackAngle() {	return attackAngle; }

	public void setAttackAngle(float attackAngle) {	this.attackAngle = attackAngle;	}

	public float getDesiredAngle() { return desiredAngle; }

	public void setDesiredAngle(float desiredAngle) { this.desiredAngle = desiredAngle; }
	
	public void setAttackCd(float attackCd) { this.attackCd = attackCd; }
}
