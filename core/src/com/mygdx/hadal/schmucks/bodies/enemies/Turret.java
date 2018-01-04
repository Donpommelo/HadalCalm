package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
	
	float shortestFraction;
  	Fixture closestFixture;
  	
  	private turretState aiState;

  	private TextureAtlas atlas;
	private TextureRegion turretBase, turretBarrel;
	
	public static final int width = 528;
	public static final int height = 252;
	
	public static final int hbWidth = 261;
	public static final int hbHeight = 165;
	
	public static final int rotationX = 130;
	public static final int rotationY = 114;
	
	public static final float scale = 1.0f;
	
	public Turret(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y) {
		super(state, world, camera, rays, hbWidth * scale, hbHeight * scale, x, y);		
		this.weapon = new SpittlefishAttack(this);	
		this.angle = 90;
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.TURRET_ATL.toString());
		turretBase = atlas.findRegion("base");
		turretBarrel = atlas.findRegion("flak");
		
		aiState = turretState.NOTSHOOTING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	public void create() {
		this.bodyData = new BodyData(world, this);
		this.body = BodyBuilder.createBox(world, startX, startY, hbWidth * scale / 2, hbHeight * scale / 2, 0, 1, 0, false, true, Constants.BIT_ENEMY, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				Constants.ENEMY_HITBOX, false, bodyData);
	}
	
	/**
	 * Enemy ai goes here. Default enemy behavior: don't move. shoot player on sight.
	 */
	public void controller(float delta) {
		
		
		switch(aiState) {
			case NOTSHOOTING:
				
			case SHOOTING:
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
	}
	
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.hud.combined);
		Vector3 bodyScreenPosition = new Vector3(body.getPosition().x, body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
				
		batch.draw(turretBarrel, 
				bodyScreenPosition.x - hbWidth * scale / 2, 
				bodyScreenPosition.y - hbHeight * scale / 2, 
				rotationX * scale, rotationY * scale,
				width * scale, height * scale, 1, 1, angle);
		
		batch.draw(turretBase, 
				bodyScreenPosition.x - hbWidth * scale / 2, 
				bodyScreenPosition.y - hbHeight * scale / 2, 
				0, 0,
				width * scale, height * scale, 1, 1, 0.0f);
		
		angle++;
		
	}
	
	public enum turretState {
		SHOOTING,
		NOTSHOOTING
	}

}
