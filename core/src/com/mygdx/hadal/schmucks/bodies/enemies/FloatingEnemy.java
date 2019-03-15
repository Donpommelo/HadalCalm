package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class FloatingEnemy extends SteeringEnemy {
				
	//This is the weapon that the enemy will attack player with next. Can change freely from enemy to enemy.
	protected Equipable weapon;
    
    //moveCd determines how much time until the fish processes ai again.
    private static final float aiRoamCd = 0.75f;
    private static final float aiChaseCd = 3.0f;
    private float aiCdCount = 0;
    
    //Ai mode of the fish
  	private floatingState aiState;
  	
  	//These are used for raycasting to determing whether the player is in vision of the fish.
  	private float shortestFraction;
  	private Schmuck homeAttempt;
	private Fixture closestFixture;
  	
  	private TextureAtlas atlas;
	private TextureRegion fishSprite;
	
	private int width, height, hbWidth, hbHeight;
	
	private float scale;

	protected SteeringBehavior<Vector2> roam;
	
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
	public FloatingEnemy(PlayState state, int x, int y,	int width, int height, int hbWidth, int hbHeight, float scale, String spriteId, enemyType type,
			float maxLinSpd, float maxLinAcc, float maxAngSpd, float maxAngAcc, float boundingRad, float decelerationRad) {
		super(state, hbWidth * scale, hbHeight * scale, x, y, type,
				maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRad);
		
		this.width = width;
		this.height = height;
		this.hbWidth = hbWidth;
		this.hbHeight = hbHeight;
		this.scale = scale;
		
		this.aiState = floatingState.ROAMING;
		
		atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.FISH_ATL.toString());
		fishSprite = atlas.findRegion(spriteId);
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		super.create();
		
		roam = new Wander<Vector2>(this)
				.setWanderOffset(100)
				.setWanderRadius(100)
				.setWanderRate(25)
				.setWanderOrientation(10);
		setTarget(state.getPlayer(), roam);
	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	@Override
	public void controller(float delta) {
		
		moveState = MoveStates.STAND;
		
		switch (aiState) {
		case CHASING:
			
			float bodyAngle = (float) (getBody().getAngle() + Math.PI / 2);
			float angleToTarget = target.getBody().getPosition().sub(getBody().getPosition()).angleRad();
			
			if (Math.abs(angleToTarget - bodyAngle) <= Math.PI / 3) {
				useToolStart(delta, weapon, hitboxfilter, (int)target.getBody().getPosition().x, (int)target.getBody().getPosition().y, true);
			}			
			break;
		default:
			break;
		
		}
		
		super.controller(delta);
		
		//When processing ai, fish attempt to raycast towards player.
		if (aiCdCount < 0) {
			aiCdCount += aiRoamCd;
			
			aiState = floatingState.ROAMING;
			setTarget(state.getPlayer(), roam);
			
			final HadalEntity me = this;
			world.QueryAABB((new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() instanceof BodyData) {
						homeAttempt = ((BodyData)fixture.getUserData()).getSchmuck();
						shortestFraction = 1.0f;
						
						
					  	if (body.getPosition().x != homeAttempt.getPosition().x || 
					  			body.getPosition().y != homeAttempt.getPosition().y) {
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
								
							}, body.getPosition(), homeAttempt.getPosition());
							if (closestFixture != null) {
								if (closestFixture.getUserData() instanceof BodyData) {
									aiState = floatingState.CHASING;
									aiCdCount += aiChaseCd;

									setTarget(((BodyData)closestFixture.getUserData()).getSchmuck(), 
											new Pursue<Vector2>(me, ((BodyData)closestFixture.getUserData()).getSchmuck()));
								}
							} 
						}
					}
					return true;
				}
			}), 
				body.getPosition().x - aiRadius, body.getPosition().y - aiRadius, 
				body.getPosition().x + aiRadius, body.getPosition().y + aiRadius);				
		}
		
		//Fish must manually reload their singular weapon.
		if (weapon.isReloading()) {
			weapon.reload(delta);
		}
		
		//process cooldowns
		aiCdCount -= delta;
	}
	
	@Override
	public float getAttackAngle() {
		return (float) (body.getAngle() + Math.PI / 2);
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {

		boolean flip = false;
		
		if (body.getAngle() < 0) {
			flip = true;
		}
		
		batch.setProjectionMatrix(state.sprite.combined);

		if (flashingCount > 0) {
			batch.setShader(HadalGame.shader);
		}
		
		batch.draw(fishSprite, 
				body.getPosition().x * PPM - hbHeight * scale / 2, 
				(flip ? height * scale : 0) + body.getPosition().y * PPM - hbWidth * scale / 2, 
				hbHeight * scale / 2, 
				(flip ? -1 : 1) * hbWidth * scale / 2,
				width * scale, (flip ? -1 : 1) * height * scale, 1, 1, 
				(float) Math.toDegrees(body.getAngle()) - 90);

		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	public enum floatingState {		CHASING,
		ESCAPING,
		ROAMING,
	}
}