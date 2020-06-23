package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invisibility;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * enemies are schumcks that attack the player.
 * @author Zachary Tu
 *
 */
public class Enemy extends Schmuck {
	
	//This is the type of enemy
	private EnemyType type;		

    //this is the size of the enemy's hitbox
	protected Vector2 hboxSize;
    
    //is this enemy a boss? (makes it show up in the boss ui)
    private boolean isBoss = false;
    
	//the default speed that the enemy moves around
	protected int moveSpeed;
	
	//This is the default cooldown between attacks for the enemy
	private float attackCd;
	
	//this is the amount of currency dropped when this enemy is defeated
	private int scrapDrop;
	
	//This is the range that the enemy will be able to detect targets
    protected static final float aiRadius = 2000;
    
    //This is the entity this enemy is trying to attack
  	protected HadalEntity target;
  	
	//This is the duration until the enemy will attack gain
    private float aiAttackCdCount = 0.0f;
    
    //This is the duration until the enemy will perform the next action in its action queue (or secondary action queue)
    private float aiActionCdCount = 0.0f;
    private float aiSecondaryActionCdCount = 0.0f;
	
  	//These are used for raycasting to determing whether the player is in vision of the fenemyish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
	//this is the angle that the enemy is currently attacking in/angle they are turning towards.
	protected float attackAngle, desiredAngle;
	
	//This is a dummy event in the map that the enemy is moving towards
	private Vector2 movementTarget;
	
	//The action queues and current action hold the enemy' queued up actions. (secondary action is for 2 different actions occurring simultaneously)
	private ArrayList<EnemyAction> actions;
	private EnemyAction currentAction;
	
	private ArrayList<EnemyAction> secondaryActions;
	private EnemyAction currentSecondaryAction;
	
	//this is the enemy sprite
	protected Sprite sprite;
	private TextureRegion hpSprite;
	private final static float uiScale = 0.2f;
	private final static float hpX = 10.0f;
	private final static float hpY = 30.0f;

	 //This is the event that spwner this enemy. Is null for the client and for enemies spawned in other ways.
    protected SpawnerSchmuck spawner;
    
	public Enemy(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, String name, Sprite sprite, EnemyType type, short filter, float baseHp, float attackCd, int scrapDrop, SpawnerSchmuck spawner) {
		super(state, startPos, size, name, filter, baseHp);
		this.hboxSize = hboxSize;
		this.type = type;
		this.attackCd = attackCd;
		this.scrapDrop = scrapDrop;
		this.spawner = spawner;
		this.sprite = sprite;
		
		this.hpSprite = Sprite.UI_MAIN_HEALTHBAR.getFrame();
		
		this.actions = new ArrayList<EnemyAction>();
		this.secondaryActions = new ArrayList<EnemyAction>();
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
			public void onDeath(BodyData perp) {
				if (spawner != null) {
					spawner.onDeath(inflicted.getSchmuck());
				}
				if (!state.isPvp() && perp instanceof PlayerBodyData) {
					HadalGame.server.registerKill((Player) perp.getSchmuck(), null);
				}
			}
		});
		
		//if boss, activate on boss spawn statuses for all players
		if (isBoss && state.isServer()) {
			state.getPlayer().getPlayerData().statusProcTime(new ProcTime.AfterBossSpawn(this));
			for (Player player : HadalGame.server.getPlayers().values()) {
				player.getPlayerData().statusProcTime(new ProcTime.AfterBossSpawn(this));
			}
		}
	}

	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		//move towards movement target, if existent.
		if (movementTarget != null) {
			Vector2 dist = new Vector2(movementTarget).sub(getPixelPosition());
			
			//upon reaching target, conclude current action immediately and move on to the next action
			if ((int) dist.len2() <= 100) {
				setLinearVelocity(0, 0);
				movementTarget = null;
				
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
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {
		
		boolean visible = false;
		
		if (state.isServer()) {
			if (state.getPlayer().getPlayerData().getStat(Stats.HEALTH_VISIBILITY) > 0) {
				visible = true;
			}
		} else {
			if (((ClientState) state).getUiPlay().getHealthVisibility() > 0) {
				visible = true;
			}
		}
		
		if (visible && !isBoss) {
			float hpRatio = 0.0f;
			
			if (state.isServer()) {
				hpRatio = getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP);
			} else {
				hpRatio = getBodyData().getOverrideHpPercent();
			}
			
			batch.draw(hpSprite, hpX + getPixelPosition().x - hboxSize.x / 2, hpY + getPixelPosition().y - hboxSize.y / 2, hpSprite.getRegionWidth() * uiScale * hpRatio, hpSprite.getRegionHeight() * uiScale);
		}
	}
	
	/**
	 * This is run when the enemy performs an action. Override in child classes.
	 */
	public void attackInitiate() {};
	
	/**
	 * This is used by the enemy to find a valid target
	 */
	public void acquireTarget() {
		
		target = null;
		
		world.QueryAABB((new QueryCallback() {

			@Override
			public boolean reportFixture(Fixture fixture) {
				if (fixture.getUserData() instanceof BodyData) {
					homeAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
					shortestFraction = 1.0f;
					
					
				  	if (getPosition().x != homeAttempt.getPosition().x || getPosition().y != homeAttempt.getPosition().y) {
				  		world.rayCast(new RayCastCallback() {

							@Override
							public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
								if (fixture.getUserData() instanceof BodyData) {
									if (((BodyData) fixture.getUserData()).getSchmuck().getHitboxfilter() != hitboxfilter) {
										if (fraction < shortestFraction) {
											
											//enemies will not see invisible units
											if (((BodyData) fixture.getUserData()).getStatus(Invisibility.class) == null) {
												shortestFraction = fraction;
												closestFixture = fixture;
												return fraction;
											}
										}
									}
								} 
								return -1.0f;
							}
							
						}, getPosition(), homeAttempt.getPosition());
						if (closestFixture != null) {
							if (closestFixture.getUserData() instanceof BodyData) {
								target = ((BodyData) closestFixture.getUserData()).getSchmuck();
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
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			WeaponUtils.spawnScrap(state, scrapDrop, getPixelPosition(), true);
		}
		return super.queueDeletion();
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncSchmuck) {
			Packets.SyncSchmuck p = (Packets.SyncSchmuck) o;
			if (isBoss) {
				((ClientState) state).getUiPlay().setOverrideBossHpPercent(p.hpPercent);
				if (p.hpPercent <= 0.0f) {
					state.clearBoss();
				}
			}
		}
		super.onClientSync(o);
	}
	
	/**
	 * When created in the server, tell the client what kind of enemy was reated to sync
	 */
	@Override
	public Object onServerCreate() {
		return new Packets.CreateEnemy(entityID.toString(), getPosition(), type, isBoss, name);
	}

	@Override
	public void dispose() {
		super.dispose();
		
		//this is here to prevent the client from not updating the last, fatal instance of damage in the ui
		if (isBoss) {
			state.clearBoss();
		}
	}
	
	public void setTarget(HadalEntity target) {	this.target = target; }
	
	public void setBoss(boolean isBoss) { this.isBoss = isBoss; }

	public Vector2 getHboxSize() { return hboxSize; }

	public void setName(String name) { this.name = name; }

	public void setMoveSpeed(int moveSpeed) { this.moveSpeed = moveSpeed; }
	
	public Vector2 getMovementTarget() { return movementTarget; }

	public void setMovementTarget(Vector2 movementTarget) { this.movementTarget = movementTarget; }

	public ArrayList<EnemyAction> getActions()  {return actions; }

	public ArrayList<EnemyAction> getSecondaryActions() { return secondaryActions; }

	public void setSecondaryActions(ArrayList<EnemyAction> secondaryActions) { this.secondaryActions = secondaryActions; }

	public float getAttackAngle() {	return attackAngle; }

	public void setAttackAngle(float attackAngle) {	this.attackAngle = attackAngle;	}

	public float getDesiredAngle() { return desiredAngle; }

	public void setDesiredAngle(float desiredAngle) { this.desiredAngle = desiredAngle; }
	
	public void setAttackCd(float attackCd) { this.attackCd = attackCd; }

	public int getScrapDrop() {	return scrapDrop; }

	public void setScrapDrop(int scrapDrop) { this.scrapDrop = scrapDrop; }
}
