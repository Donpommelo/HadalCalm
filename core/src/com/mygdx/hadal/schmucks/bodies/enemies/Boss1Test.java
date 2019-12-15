package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.BossUtils;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
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
public class Boss1Test extends Enemy {
				
	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	protected Equipable weapon;
    
    private static final float aiAttackCd = 7.0f;
    private float aiAttackCdCount = 0.0f;
    
    private static final float aiDelayCd = 3.0f;
    private float aiDelayCdCount = 100;
    
    private float angle;
	private float desiredAngle;
	
  	//These are used for raycasting to determing whether the player is in vision of the fish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
  	private TextureAtlas atlas;
	private TextureRegion fishSprite;
	protected SteeringBehavior<Vector2> face;
	
	
	private static final int width = 250;
	private static final int height = 161;
	
	private static final int hbWidth = 161;
	private static final int hbHeight = 250;
	
	private static final float scale = 1.0f;
	
	private static final String spriteId = "torpedofish_swim";
	
	private static final int moveSpeed = 20;
	
	private BossAttack currentAttack;
	private static final BossAttack[] attacks = {BossAttack.SPAWN_ADDS, BossAttack.CHARGE, BossAttack.FIRE_SPIN};
	
	private Event movementTarget;
	
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
	public Boss1Test(PlayState state, int x, int y, enemyType type, short filter) {
		super(state, hbWidth * scale, hbHeight * scale, x, y, type, filter);
		this.angle = 0;
		this.desiredAngle = 0;
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.FISH_ATL.toString());
		fishSprite = atlas.findRegion(spriteId);
		
		this.moveState = SchmuckMoveStates.BOSS_WAITING;
		this.currentAttack = BossAttack.SPAWN_ADDS;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this);
		this.body = BodyBuilder.createBox(world, startX, startY,  hbWidth * scale, hbHeight * scale, 0, 1, 0, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, bodyData);
		
		//temp way of more Hp
		this.bodyData.addStatus(new StatChangeStatus(state, Stats.MAX_HP, 1000, bodyData));

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
				} else {
					setLinearVelocity(dist.nor().scl(moveSpeed));
				}
			}
		}
		
		switch (moveState) {
		case BOSS_WAITING:
			if (target != null) {
				
				angle = angle + (desiredAngle - angle) * 0.02f;
				
				setOrientation((float) ((angle + 270) * Math.PI / 180));
				
				if (target.isAlive()) {
					desiredAngle =  (float)(Math.atan2(
							target.getPosition().y - getPosition().y ,
							target.getPosition().x - getPosition().x) * 180 / Math.PI);
				}
			}
			break;
		default:
			break;
		}
		
		if (aiAttackCdCount <= 0) {
			aiAttackCdCount = 100;
			
			acquireTarget();
			attackInitiate();
		}
		
		if (aiDelayCdCount <= 0) {
			attackExecute();
		}
		
		//process cooldowns
		aiAttackCdCount -= delta;
		aiDelayCdCount -= delta;
	}
	
	public void attackInitiate() {
		aiDelayCdCount = aiDelayCd;
		
//		int randomIndex = GameStateManager.generator.nextInt(attacks.length);
		
		currentAttack = attacks[1];
		
		switch(currentAttack) {
		case CHARGE:
			int start = GameStateManager.generator.nextInt(6);
			switch(start) {
			case 0:
				BossUtils.moveToDummy(state, this, "0");
				break;
			case 1:
				BossUtils.moveToDummy(state, this, "2");
				break;
			case 2:
				BossUtils.moveToDummy(state, this, "3");
				break;
			case 3:
				BossUtils.moveToDummy(state, this, "5");
				break;
			case 4:
				BossUtils.moveToDummy(state, this, "6");
				break;
			case 5:
				BossUtils.moveToDummy(state, this, "8");
				break;
			}
			
		case SPAWN_ADDS:
			break;
		case FIRE_SPIN:
			BossUtils.moveToDummy(state, this, "4");
			break;
		default:
			break;
		}
	}
	
	private static final int chargeSpeed = 40;
	private static final int numAdds = 3;
	private static final int addsSpread = 20;
	public void attackExecute() {
		aiDelayCdCount = 100;
		aiAttackCdCount = aiAttackCd;

		switch(currentAttack) {
		case CHARGE:
			BossUtils.charge(state, this, target, chargeSpeed, 1, 10.0f);
			break;
		case SPAWN_ADDS:
			for (int i = 0; i < numAdds; i++) {
				BossUtils.spawnAdds(state, (int)(getPosition().x * PPM), (int)(getPosition().y * PPM), enemyType.TORPEDOFISH, numAdds, addsSpread);
			}
			break;
		case FIRE_SPIN:
			
			break;
		default:
			break;
		
		}
	}
	
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
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {

		boolean flip = false;
		if (getOrientation() > Math.PI && getOrientation() < 2 * Math.PI) {
			flip = true;
		}
		
		batch.setProjectionMatrix(state.sprite.combined);

		if (flashingCount > 0) {
			batch.setShader(HadalGame.shader);
		}
		
		batch.draw(fishSprite, 
				getPosition().x * PPM - hbHeight * scale / 2, 
				(flip ? height * scale : 0) + getPosition().y * PPM - hbWidth * scale / 2, 
				hbHeight * scale / 2, 
				(flip ? -1 : 1) * hbWidth * scale / 2,
				width * scale, (flip ? -1 : 1) * height * scale, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);

		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, hbHeight * scale, hbWidth * scale, 
					(int)(getPosition().x * PPM), 
					(int)(getPosition().y * PPM), fishSprite, getLinearVelocity(), 0.5f);
		}
		return super.queueDeletion();
	}
	
	public Event getMovementTarget() {
		return movementTarget;
	}

	public void setMovementTarget(Event movementTarget) {
		this.movementTarget = movementTarget;
	}

	public enum BossAttack {
		SPAWN_ADDS,
		CHARGE,
		FIRE_SPIN
	}
}