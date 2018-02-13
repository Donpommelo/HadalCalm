package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.enemy.SpittlefishAttack;
import com.mygdx.hadal.equip.enemy.TurretAttack;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

public class Turret extends Enemy {

	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	private Equipable weapon;

	public static final float aiCd = 1.0f;
	public float aiCdCount = 0;
	    
	public float angle;
	public float desiredAngle;
	
	float shortestFraction;
  	Fixture closestFixture;
  	
  	private turretState aiState;

  	private TextureAtlas atlas;
	private TextureRegion turretBase, turretBarrel;
	private Animation<TextureRegion> fireAnimation;
	
	public static final int width = 528;
	public static final int height = 252;
	
	public static final int hbWidth = 261;
	public static final int hbHeight = 165;
	
	public static final int rotationX = 131;
	public static final int rotationY = 114;
	
	public static final float scale = 0.3f;
	
	public Turret(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, hbWidth * scale, hbHeight * scale, x, y);		
		this.weapon = new SpittlefishAttack(this);	
		this.angle = 0;
		this.desiredAngle = 0;
		
		this.weapon = new TurretAttack(this);
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.TURRET_ATL.toString());
		turretBase = atlas.findRegion("base");
//		turretBarrel = atlas.findRegion("flak");
//		fireAnimation = new Animation<TextureRegion>(1 / 20f, atlas.findRegions("flak"));
		turretBarrel = atlas.findRegion("volley");
		fireAnimation = new Animation<TextureRegion>(1 / 20f, atlas.findRegions("volley"));
		
		aiState = turretState.NOTSHOOTING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, hbWidth * scale, hbHeight * scale, 0, 1, 0, true, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);
	}
	
	/**
	 * Enemy ai goes here. Default enemy behavior: don't move. shoot player on sight.
	 */
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
		
		//If the delay on using a tool just ended, use thte tool.
		if (shootDelayCount <= 0 && usedTool != null) {
			useToolEnd();
		}
		
		if (weapon.reloading) {
			weapon.reload(delta);
		}
	}
	
	public void render(SpriteBatch batch) {

		batch.setProjectionMatrix(state.sprite.combined);
		if(aiState == turretState.NOTSHOOTING) {
			batch.draw(turretBarrel, 
					body.getPosition().x * PPM - hbWidth * scale / 2, 
					body.getPosition().y * PPM - hbHeight * scale / 2, 
					rotationX * scale, rotationY * scale,
					width * scale, height * scale, 1, 1, angle);
		} else {
			batch.draw(fireAnimation.getKeyFrame(getAnimationTime(), true), 
					body.getPosition().x * PPM - hbWidth * scale / 2, 
					body.getPosition().y * PPM - hbHeight * scale / 2, 
					rotationX * scale, rotationY * scale,
					width * scale, height * scale, 1, 1, angle);
		}
		
		batch.draw(turretBase, 
				body.getPosition().x * PPM - hbWidth * scale / 2, 
				body.getPosition().y * PPM - hbHeight * scale / 2, 
				0, 0,
				width * scale, height * scale, 1, 1, 0.0f);		
	}
	
	public enum turretState {
		SHOOTING,
		NOTSHOOTING
	}

}
