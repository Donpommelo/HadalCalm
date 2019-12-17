package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.enemy.TurretAttack;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Zachary Tu
 *
 */
public class Turret extends Enemy {

	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	private Equipable weapon;

	private static final float aiCd = 0.25f;
	private float aiCdCount = 0;
	    
	private float angle;
	private float desiredAngle;
	private float startAngle;
	
	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
  	private TextureAtlas atlas;
	private TextureRegion turretBase, turretBarrel;
	private Animation<TextureRegion> fireAnimation;
	
	private static final int width = 528;
	private static final int height = 252;
	
	private static final int hbWidth = 261;
	private static final int hbHeight = 165;
	
	private static final int rotationX = 131;
	private static final int rotationY = 114;
	
	private static final float scale = 0.5f;
	
	public Turret(PlayState state, int x, int y, enemyType type, int startAngle, short filter) {
		super(state, hbWidth * scale, hbHeight * scale, x, (int)(y + hbHeight * scale / 2), type, filter);		
		this.angle = 0;
		this.startAngle = startAngle;
		this.desiredAngle = startAngle;
		
		this.weapon = new TurretAttack(this);
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.TURRET_ATL.toString());
		turretBase = atlas.findRegion("base");
		
		String barrel = "";
		switch(type) {
		case TURRET_FLAK:
			barrel = "flak";
			break;
		case TURRET_VOLLEY:
			barrel = "volley";
			break;
		default:
			break;
		
		}
		turretBarrel = atlas.findRegion(barrel);
		fireAnimation = new Animation<TextureRegion>(1 / 20f, atlas.findRegions(barrel));
		
		moveState = SchmuckMoveStates.TURRET_NOTSHOOTING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this);
		
		//temp way of more Hp
		this.bodyData.addStatus(new StatChangeStatus(state, Stats.MAX_HP, 100, bodyData));
		
		this.body = BodyBuilder.createBox(world, startX, startY, hbWidth * scale, hbHeight * scale, 0, 10, 0, true, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				hitboxfilter, false, bodyData);
	}
	
	/**
	 * Enemy ai goes here. Default enemy behavior: don't move. shoot player on sight.
	 */
	@Override
	public void controller(float delta) {
		
		increaseAnimationTime(delta);

		angle = angle + (desiredAngle - angle) * 0.05f;
		
		switch(moveState) {
			case TURRET_NOTSHOOTING:
				desiredAngle = startAngle;
				break;
			case TURRET_SHOOTING:
				
				if (target.isAlive()) {
					desiredAngle =  (float)(Math.atan2(
							target.getPosition().y - getPosition().y ,
							target.getPosition().x - getPosition().x) * 180 / Math.PI);

					if (desiredAngle < 0) {
						if (desiredAngle < -90) {
							desiredAngle = 180;
						} else {
							desiredAngle = 0;
						}
					}
					
					useToolStart(delta, weapon, hitboxfilter, (int)target.getPosition().x, (int)target.getPosition().y, true);
				}
				
				break;
		default:
			break;
		}
		
		super.controller(delta);
		
		if (aiCdCount < 0) {
		
			aiCdCount += aiCd;
			moveState = SchmuckMoveStates.TURRET_NOTSHOOTING;
			
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
									target = ((BodyData)closestFixture.getUserData()).getEntity();
									moveState = SchmuckMoveStates.TURRET_SHOOTING;
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
		
		aiCdCount -= delta;
		
		//If the delay on using a tool just ended, use the tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		if (weapon.isReloading()) {
			weapon.reload(delta);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {

		if (flashingCount > 0) {
			batch.setShader(HadalGame.shader);
		}
		
		boolean flip = false;
		
		if (Math.abs(angle) > 90) {
			flip = true;
		}
		
		float rotationYReal = rotationY;
		
		if (flip) {
			rotationYReal = height - rotationY;
		}
		
		if(moveState == SchmuckMoveStates.TURRET_NOTSHOOTING || weapon.isReloading()) {
			batch.draw(turretBarrel, 
					getPosition().x * PPM - hbWidth * scale / 2, 
					(flip ? height * scale - 12 : 0) + getPosition().y * PPM - hbHeight * scale / 2, 
					rotationX * scale, (flip ? -height * scale : 0) + rotationYReal * scale,
					width * scale, (flip ? -1 : 1) * height * scale, 1, 1, angle);
		} else {
			batch.draw(fireAnimation.getKeyFrame(getAnimationTime(), true), 
					getPosition().x * PPM - hbWidth * scale / 2, 
					(flip ? height * scale - 12: 0) + getPosition().y * PPM - hbHeight * scale / 2, 
					rotationX * scale, (flip ? -height * scale : 0) + rotationYReal * scale,
					width * scale, (flip ? -1 : 1) * height * scale, 1, 1, angle);
		}
		
		batch.draw(turretBase, 
				getPosition().x * PPM - hbWidth * scale / 2, 
				getPosition().y * PPM - hbHeight * scale / 2, 
				0, 0,
				width * scale, height * scale, 1, 1, 0.0f);	
		
		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	//Turrets send their attack angle as a body angle because I don't feel like making a specific packet for them.
	//Just in case you were confused about this weird packet.
	@Override
	public void onServerSync() {
		HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), angle));
		HadalGame.server.sendToAllUDP(new Packets.SyncSchmuck(entityID.toString(), moveState,
				getBodyData().getCurrentHp(), getBodyData().getCurrentFuel(), flashingCount));
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncEntity) {
			Packets.SyncEntity p = (Packets.SyncEntity) o;
			setTransform(p.pos, 0);
			angle = p.angle;
		} else {
			super.onClientSync(o);
		}
	}
}
