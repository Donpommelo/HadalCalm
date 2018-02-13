package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.equip.misc.MomentumStopper;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.ai.PlayerTrail;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.MoveStates;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

import box2dLight.RayHandler;

/**
 * The player is the entity that the player controls.
 * @author Zachary Tu
 *
 */
public class Player extends Schmuck {
	

	//player stats
//	private final static int playerWidth = 21;
//	private final static int playerHeight = 42;
	
	private final static float playerDensity = 0.2f;
	private final static float momentumBoost = 1.5f;
	
	//counters for various cooldowns.
	private float hoverCd = 0.08f;
	private float jumpCd = 0.25f;
	private float jumpCdCount = 0;
	
	private float fastFallCd = 0.25f;
	private float fastFallCdCount = 0;
	
	private float airblastCd = 0.25f;
	private float airblastCdCount = 0;
	
	protected float interactCd = 0.15f;
	protected float interactCdCount = 0;
	
	protected float momentumCd = 10.0f;
	protected float momentumCdCount = 0;
	
	protected float trailCd = 0.25f;
	protected float trailCdCount = 0;
	protected PlayerTrail lastTrail;
	
	private float attackAngle = 0;
	
	//user data
	public PlayerBodyData playerData;
	
	//The event that the player last collided with. Used for active events that the player interacts with by pressing 'E'
	public Event currentEvent;
	
	//Equipment that the player has built in to their toolset.
	public MomentumStopper mStop;
	public Airblaster airblast;
	
	//Queue of velocities that the player can manipulate
	public Queue<Vector2> momentums;
	
	private TextureAtlas atlasBody, atlasTool;
	private TextureRegion bodyBackSprite, armSprite, gemSprite, gemInactiveSprite, toolSprite;
	
	private Animation<TextureRegion> bodyStillSprite, bodyRunSprite, headSprite;
	
	public static final int hbWidth = 216;
	public static final int hbHeight = 516;
		
	public static final int bodyConnectX = -15;
	public static final int bodyConnectY = 0;
	
	public static final int headConnectX = -26;
	public static final int headConnectY = 330;
	
	public static final int armConnectX = -304;
	public static final int armConnectY = 218;
	
	public static final int armRotateX = 330;
	public static final int armRotateY = 50;
		
	public static final float scale = 0.15f;
	
	public int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
	toolHeight, toolWidth, gemHeight, gemWidth;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	public float controllerCount = 0;
	public boolean shooting = false;
	public boolean hovering = false;
	
	
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param x: player starting x position.
	 * @param y: player starting x position.
	 */
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y, String playerSprite) {
		super(state, world, camera, rays, hbWidth * scale, hbHeight * scale, x, y, Constants.PLAYER_HITBOX);
		mStop = new MomentumStopper(this);
		airblast = new Airblaster(this);
		momentums = new Queue<Vector2>();
		
		atlasTool = (TextureAtlas) HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
		
		toolSprite = atlasTool.findRegion("default");
		
		this.toolHeight = toolSprite.getRegionHeight();
		this.toolWidth = toolSprite.getRegionWidth();
		
		this.moveState = MoveStates.STAND;

		setBodySprite(playerSprite);
		
	}
	
	public void setBodySprite(String playerSprite) {
		atlasBody = (TextureAtlas) HadalGame.assetManager.get(playerSprite);
		bodyRunSprite = new Animation<TextureRegion>(0.08f, atlasBody.findRegions("body_run"));	
		bodyStillSprite = new Animation<TextureRegion>(0.08f, atlasBody.findRegions("body_stand"));	
		bodyBackSprite = atlasBody.findRegion("body_background");
		armSprite = atlasBody.findRegion("arm");
		headSprite = new Animation<TextureRegion>(0.08f, atlasBody.findRegions("head"));	
		gemSprite = atlasBody.findRegion("gem_active");
		gemInactiveSprite = atlasBody.findRegion("gem_inactive");
		
		this.armWidth = armSprite.getRegionWidth();
		this.armHeight = armSprite.getRegionHeight();
		this.headWidth = headSprite.getKeyFrame(animationTime).getRegionWidth();
		this.headHeight = headSprite.getKeyFrame(animationTime).getRegionHeight();
		this.bodyWidth = bodyRunSprite.getKeyFrame(animationTime).getRegionWidth();
		this.bodyHeight = bodyRunSprite.getKeyFrame(animationTime).getRegionHeight();
		this.bodyBackWidth = bodyBackSprite.getRegionWidth();
		this.bodyBackHeight = bodyBackSprite.getRegionHeight();
		this.gemHeight = gemSprite.getRegionHeight();
		this.gemWidth = gemSprite.getRegionWidth();
		
		//This line is used when the player swaps skins in loadout screen. It ensures the tool sprite is properly aligned.
		if (playerData != null) {
			playerData.setEquip();
		}
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	public void create() {		
		
		state.resetController();
		
		this.playerData = new PlayerBodyData(world, this, state.loadout);
		this.bodyData = playerData;
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, playerDensity, 0, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX, false, playerData);
		super.create();
	}
	
	/**
	 * The player's controller currently polls for input.
	 */
	public void controller(float delta) {
		increaseAnimationTime(delta);
		
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount -= 1/60f;

			if (hovering) {
				hover();
			}
		}
		
		if (shooting) {
			shoot(delta);
		}
		
		//Determine if the player is in the air or on ground.
		grounded = feetData.getNumContacts() > 0;
		
		//player's jumps are refreshed on the ground
		if (grounded) {
			playerData.extraJumpsUsed = 0;
		}
				
		//process fuel regen
		playerData.fuelGain(playerData.getFuelRegen() * delta);
		
		//If player is reloading, run the reload method of the current equipment.
		if (playerData.currentTool.reloading) {
			playerData.currentTool.reload(delta);
		}
		
		if (trailCdCount < 0) {
			/*trailCdCount += trailCd;
			PlayerTrail newTrail = new PlayerTrail(state, world, camera, rays, (int)(getBody().getPosition().x * PPM), (int)(getBody().getPosition().y * PPM));
			if (lastTrail != null) {
				lastTrail.setTrail(newTrail);
			}
			lastTrail = newTrail;*/
		}
		
		//process cds
		jumpCdCount-=delta;
		fastFallCdCount-=delta;
		airblastCdCount-=delta;
		interactCdCount-=delta;
		momentumCdCount-=delta;
		trailCdCount-=delta;
		
		super.controller(delta);		
		
	}
	
	public void hover() {
		if (!grounded && playerData.extraJumpsUsed >= playerData.numExtraJumps + (int) playerData.getBonusJumpNum()) {
			if (jumpCdCount < 0) {
				
				//Player will continuously do small upwards bursts that cost fuel.
				if (playerData.currentFuel >= playerData.hoverCost * (1 + playerData.getBonusHoverCost())) {
					playerData.fuelSpend(playerData.hoverCost * (1 + playerData.getBonusHoverCost()));
					jumpCdCount = hoverCd;
					push(0, playerData.hoverPow * (1 + playerData.getBonusHoverPower()));
				}
			}
		}
	}
	
	public void jump() {
		if (grounded) {
			if (jumpCdCount < 0) {
				jumpCdCount = jumpCd;
				push(0, playerData.jumpPow * (1 + playerData.getBonusJumpPower()));
			}
		} else {
			if (playerData.extraJumpsUsed < playerData.numExtraJumps + (int) playerData.getBonusJumpNum()) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					playerData.extraJumpsUsed++;
					push(0, playerData.jumpPow * (1 + playerData.getBonusJumpPower()));
				}
			}
		}
	}
	
	public void fastFall() {
		if (!grounded) {
			if (fastFallCdCount < 0) {
				fastFallCdCount = fastFallCd;
				push(0, -playerData.fastFallPow);
			}
		}
		if (feetData.terrain != null) {
			feetData.terrain.eventData.onInteract(this);
		}
	}
	
	public void shoot(float delta) {
		useToolStart(delta, playerData.currentTool, Constants.PLAYER_HITBOX, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true);
	}
	
	public void release() {
		//TODO: THIS LINE GOT A NULLPOINTER ONCE UPON DYING. CANNOT REPLICATE. 
		//ADDED NULL CHECK TO INPUT PROCESSOR. HOPEFULLY FIXED.
		useToolRelease(playerData.currentTool, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY());
	}
	
	public void airblast() {
		if (airblastCdCount < 0) {
			if (playerData.currentFuel >= playerData.airblastCost * (1 + playerData.getBonusAirblastCost())) {
				playerData.fuelSpend(playerData.airblastCost * (1 + playerData.getBonusAirblastCost()));
				airblastCdCount = airblastCd;
				useToolStart(0, airblast, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);
			}
		}
	}
	
	public void interact() {
		if (currentEvent != null && interactCdCount < 0) {
			interactCdCount = interactCd;
			currentEvent.eventData.onInteract(this);
		}
	}
	
	public void momentum() {
		if (momentums.size == 0) {
			if (momentumCdCount < 0) {
				momentumCdCount = momentumCd * (1 + playerData.getBonusMomentumCd());
				useToolStart(0, mStop, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);
			}
		} else {
			body.setLinearVelocity(momentums.removeFirst().scl(momentumBoost));
		}
	}
	
	public void reload() {
		playerData.currentTool.reloading = true;
	}
	
	public void switchToLast() {
		playerData.switchToLast();
	}
	
	public void switchToSlot(int slot) {
		playerData.switchWeapon(slot);
	}
	
	@Override
	public float getAttackAngle() {
		if (armSprite.isFlipX()) {
			return (float) Math.toRadians(attackAngle - 180);
		} else {
			return (float) Math.toRadians(attackAngle);

		}
	}
	
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.sprite.combined);
		
		Vector3 bodyScreenPosition = new Vector3(
				body.getPosition().x,
				body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		attackAngle = (float)(Math.atan2(
				bodyScreenPosition.y - (Gdx.graphics.getHeight() - Gdx.input.getY()) ,
				bodyScreenPosition.x - Gdx.input.getX()) * 180 / Math.PI);
				
		boolean flip = false;
		
		if (Math.abs(attackAngle) > 90) {
			flip = true;
		}

/*		if (bodyStillSprite.getKeyFrame(animationTime).isFlipX() != armSprite.isFlipX()) {
			bodyStillSprite.getKeyFrame(animationTime).flip(true, false);
		}
		if (headSprite.getKeyFrame(animationTime).isFlipX() != armSprite.isFlipX()) {
			headSprite.getKeyFrame(animationTime).flip(true, false);
		}*/
		
		float armConnectXReal = armConnectX;
		float headConnectXReal = headConnectX;
		float armRotateXReal = armRotateX;
		
		if (flip) {
			armConnectXReal = bodyWidth - armWidth - armConnectX - 200;
			headConnectXReal = bodyWidth - headWidth - headConnectX - 200;
			armRotateXReal = armWidth - armRotateX;
			attackAngle = attackAngle + 180;
		}
		
		batch.draw(toolSprite, 
				(flip ? toolWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + armConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + armConnectY * scale, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale , armRotateY * scale,
				(flip ? -1 : 1) * toolWidth * scale, toolHeight * scale, 1, 1, attackAngle);
		
		batch.draw(bodyBackSprite, 
				(flip ? bodyBackWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + bodyConnectX, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + bodyConnectY, 
				0, 0,
				(flip ? -1 : 1) * bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);
		
		batch.draw(armSprite, 
				(flip ? armWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + armConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + armConnectY * scale, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale, armRotateY * scale,
				(flip ? -1 : 1) * armWidth * scale, armHeight * scale, 1, 1, attackAngle);
		
		batch.draw(momentumCdCount < 0 ? gemSprite : gemInactiveSprite, 
				(flip ? gemWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + bodyConnectY, 
				0, 0,
				(flip ? -1 : 1) * gemWidth * scale, gemHeight * scale, 1, 1, 0);
		
		if (moveState.equals(MoveStates.MOVE_LEFT)) {
			
			if (Math.abs(attackAngle) > 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(animationTime, true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else if (moveState.equals(MoveStates.MOVE_RIGHT)) {
			if (Math.abs(attackAngle) < 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(animationTime, true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else {
			batch.draw((TextureRegion) bodyStillSprite.getKeyFrame(animationTime, true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		}
		
		batch.draw((TextureRegion) headSprite.getKeyFrame(animationTime, true), 
				(flip ? headWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + headConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + headConnectY * scale, 
				0, 0,
				(flip ? -1 : 1) * headWidth * scale, headHeight * scale, 1, 1, 0);
	}
	
	public void dispose() {
		super.dispose();
	}
	
	public PlayerBodyData getPlayerData() {
		return playerData;
	}
	
	public void setToolSprite(TextureRegion sprite) {
		toolSprite = sprite;
	}

	public TextureRegion getToolSprite() {
		return toolSprite;
	}
	
	public void setArmSprite(TextureRegion sprite) {
		armSprite = sprite;
	}

	public TextureRegion getArmSprite() {
		return armSprite;
	}
}
