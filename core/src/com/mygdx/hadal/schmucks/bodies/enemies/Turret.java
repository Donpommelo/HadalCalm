package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.enemy.TurretAttack;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Constants;
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
	
	private float shortestFraction;
	private Fixture closestFixture;
  	
  	private turretState aiState;

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
	
	public Turret(PlayState state, int x, int y, String type) {
		super(state, hbWidth * scale, hbHeight * scale, x, (int)(y + hbHeight * scale / 2));		
		this.angle = 0;
		this.desiredAngle = 0;
		
		this.weapon = new TurretAttack(this);
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.TURRET_ATL.toString());
		turretBase = atlas.findRegion("base");
		turretBarrel = atlas.findRegion(type);
		fireAnimation = new Animation<TextureRegion>(1 / 20f, atlas.findRegions(type));
		
		aiState = turretState.NOTSHOOTING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		this.bodyData = new BodyData(this);
		
		//temp way of more Hp
		this.bodyData.addStatus(new StatChangeStatus(state, 0, 225, bodyData));
		
		this.body = BodyBuilder.createBox(world, startX, startY, hbWidth * scale, hbHeight * scale, 0, 10, 0, true, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);
	}
	
	/**
	 * Enemy ai goes here. Default enemy behavior: don't move. shoot player on sight.
	 */
	@Override
	public void controller(float delta) {
		
		increaseAnimationTime(delta);

		switch(aiState) {
			case NOTSHOOTING:
				angle = desiredAngle;
				break;
			case SHOOTING:
				angle =  (float)(Math.atan2(
						state.getPlayer().getBody().getPosition().y - body.getPosition().y ,
						state.getPlayer().getBody().getPosition().x - body.getPosition().x) * 180 / Math.PI);
				
				Vector3 target = new Vector3(state.getPlayer().getBody().getPosition().x, state.getPlayer().getBody().getPosition().y, 0);
				camera.project(target);
				
				useToolStart(delta, weapon, Constants.ENEMY_HITBOX, (int)target.x, (int)target.y, true);
				break;
		}
				
		if (aiCdCount < 0) {
		
			aiCdCount += aiCd;
			aiState = turretState.NOTSHOOTING;
			
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
			}
			
			if (closestFixture != null) {
				if (closestFixture.getUserData() instanceof PlayerBodyData ) {
					aiState = turretState.SHOOTING;
				}
			}
		}
		
		aiCdCount -= delta;
		shootCdCount-=delta;
		shootDelayCount-=delta;
		flashingCount-=delta;
		
		//If the delay on using a tool just ended, use thte tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		if (weapon.isReloading()) {
			weapon.reload(delta);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {

		batch.setProjectionMatrix(state.sprite.combined);
		
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
		
		if(aiState == turretState.NOTSHOOTING) {
			batch.draw(turretBarrel, 
					body.getPosition().x * PPM - hbWidth * scale / 2, 
					(flip ? height * scale : 0) + body.getPosition().y * PPM - hbHeight * scale / 2, 
					rotationX * scale, (flip ? -height * scale : 0) + rotationYReal * scale,
					width * scale, (flip ? -1 : 1) * height * scale, 1, 1, angle);
		} else {
			batch.draw(fireAnimation.getKeyFrame(getAnimationTime(), true), 
					body.getPosition().x * PPM - hbWidth * scale / 2, 
					(flip ? height * scale - 12: 0) + body.getPosition().y * PPM - hbHeight * scale / 2, 
					rotationX * scale, (flip ? -height * scale : 0) + rotationYReal * scale,
					width * scale, (flip ? -1 : 1) * height * scale, 1, 1, angle);
		}
		
		batch.draw(turretBase, 
				body.getPosition().x * PPM - hbWidth * scale / 2, 
				body.getPosition().y * PPM - hbHeight * scale / 2, 
				0, 0,
				width * scale, height * scale, 1, 1, 0.0f);	
		
		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	public enum turretState {
		SHOOTING,
		NOTSHOOTING
	}

}
