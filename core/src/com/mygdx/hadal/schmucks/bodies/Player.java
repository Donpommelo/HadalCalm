package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
public class Player extends PhysicsSchmuck {
	
	
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
	private PlayerBodyData playerData;
	
	//The event that the player last collided with. Used for active events that the player interacts with by pressing 'E'
	private Event currentEvent;
	
	//Equipment that the player has built in to their toolset.
	private MomentumStopper mStop;
	private Airblaster airblast;
	
	//Queue of velocities that the player can manipulate
	private Queue<Vector2> momentums;
	
	private TextureAtlas atlasBody, atlasTool;
	private TextureRegion bodyBackSprite, armSprite, gemSprite, gemInactiveSprite, toolSprite;
	
	private Animation<TextureRegion> bodyStillSprite, bodyRunSprite, headSprite;
	
	public static final int hbWidth = 216;
	public static final int hbHeight = 516;
		
	private static final int bodyConnectX = -100;
	private static final int bodyConnectY = 0;
	
	private static final int headConnectX = -26;
	private static final int headConnectY = 330;
	
	private static final int armConnectX = -304;
	private static final int armConnectY = 218;
	
	private static final int armRotateX = 330;
	private static final int armRotateY = 50;
		
	public static final float scale = 0.15f;
	
	private int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
	toolHeight, toolWidth, gemHeight, gemWidth;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	private float controllerCount = 0;
	private boolean shooting = false;
	private boolean hovering = false;
	
	private final float spriteAnimationSpeed = 0.08f;
	
	private ParticleEntity hoverBubbles;//, jumpSmoke;
	
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param x: player starting x position.
	 * @param y: player starting x position.
	 */
	public Player(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int x, int y, String playerSprite, 
			PlayerBodyData oldData) {
		super(state, world, camera, rays, hbWidth * scale, hbHeight * scale, x, y, Constants.PLAYER_HITBOX);
		mStop = new MomentumStopper(this);
		airblast = new Airblaster(this);
		momentums = new Queue<Vector2>();
		
		atlasTool = (TextureAtlas) HadalGame.assetManager.get(AssetList.MULTITOOL_ATL.toString());
		
		toolSprite = atlasTool.findRegion("default");
		
		this.toolHeight = toolSprite.getRegionHeight();
		this.toolWidth = toolSprite.getRegionWidth();
		
		this.moveState = MoveStates.STAND;

		if (oldData != null) {
			this.playerData = oldData;
		}
		
		setBodySprite(playerSprite);
		loadParticles();
	}
	
	/**
	 * This method prepares the player sprite from various texture regions.
	 * @param playerSprite
	 */
	public void setBodySprite(String playerSprite) {
		atlasBody = (TextureAtlas) HadalGame.assetManager.get(playerSprite);
		bodyRunSprite = new Animation<TextureRegion>(spriteAnimationSpeed, atlasBody.findRegions("body_run"));	
		bodyStillSprite = new Animation<TextureRegion>(spriteAnimationSpeed, atlasBody.findRegions("body_stand"));	
		bodyBackSprite = atlasBody.findRegion("body_background");
		armSprite = atlasBody.findRegion("arm");
		headSprite = new Animation<TextureRegion>(spriteAnimationSpeed, atlasBody.findRegions("head"));	
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
	 * This method prepares the various particle emitting entities attached to the player.
	 */
	public void loadParticles() {
		
		final TextureAtlas particleAtlas = HadalGame.assetManager.get(AssetList.PARTICLE_ATLAS.toString());
		
		final ParticleEffect bubbles = new ParticleEffect();
		bubbles.load(Gdx.files.internal(AssetList.BUBBLE_TRAIL.toString()), particleAtlas);
		
		bubbles.findEmitter("bubble0").setContinuous(false);
		bubbles.findEmitter("bubble0").duration = 10;

		hoverBubbles = new ParticleEntity(state, world, camera, rays, this, bubbles, 3.0f);
		hoverBubbles.turnOff();
		/*
		final ParticleEffect smoke = new ParticleEffect();
		smoke.load(Gdx.files.internal(AssetList.SMOKE_PUFF.toString()), particleAtlas);
		
		jumpSmoke = new ParticleEntity(state, world, camera, rays, this, smoke, 3.0f);
		jumpSmoke.turnOff();*/
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	@Override
	public void create() {
		
		state.resetController();
		
		if (playerData == null) {
			this.playerData = new PlayerBodyData(world, this, state.getLoadout());
		} else {
			playerData.resetData(this, world);
		}
		
		this.bodyData = playerData;
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, playerDensity, 0, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				Constants.PLAYER_HITBOX, false, playerData);
				
		super.create();
	}
	
	/**
	 * The player's controller currently polls for input.
	 */
	@Override
	public void controller(float delta) {
		
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
			playerData.setExtraJumpsUsed(0);
		}
				
		//process fuel regen
		playerData.fuelGain(playerData.getFuelRegen() * delta);
		
		//If player is reloading, run the reload method of the current equipment.
		if (playerData.getCurrentTool().isReloading()) {
			playerData.getCurrentTool().reload(delta);
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
	
	/**
	 * Player's Hover power. Costs fuel and continuously pushes the player upwards.
	 */
	public void hover() {
		if (!grounded && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&
				playerData.getCurrentFuel() >= playerData.getHoverCost()) {
			if (jumpCdCount < 0) {
				
				//Player will continuously do small upwards bursts that cost fuel.
				playerData.fuelSpend(playerData.getHoverCost());
				jumpCdCount = hoverCd;
				push(0, playerData.getHoverPower());
				
				hoverBubbles.turnOn();
			}
		} else {
			hoverBubbles.turnOff();
		}
	}
	
	/**
	 * Player's jump. Player moves up if they have jumps left.
	 */
	public void jump() {
		if (grounded) {
			if (jumpCdCount < 0) {
				jumpCdCount = jumpCd;
				push(0, playerData.getJumpPower());
//				jumpSmoke.turnOn();
			}
		} else {
			if (playerData.getExtraJumpsUsed() < playerData.getExtraJumps()) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					playerData.setExtraJumpsUsed(playerData.getExtraJumpsUsed() + 1);;
					push(0, playerData.getJumpPower());
//					jumpSmoke.turnOn();
				}
			}
		}
	}
	
	/**
	 * Player falls rapidly if in the air. If grounded, this also interacts with terrain events.
	 */
	public void fastFall() {
		if (!grounded) {
			if (fastFallCdCount < 0) {
				fastFallCdCount = fastFallCd;
				push(0, -playerData.getFastFallPower());
			}
		}
		if (feetData.getTerrain() != null) {
			feetData.getTerrain().getEventData().onInteract(this);
		}
	}
	
	/**
	 * Point and shoot
	 * @param delta: How long has it been since the lst engine tick if the player is holding fire. This is used for charge weapons
	 */
	public void shoot(float delta) {
		useToolStart(delta, playerData.getCurrentTool(), Constants.PLAYER_HITBOX, Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY(), true);
	}
	
	/**
	 * Player releases mouse. This is used to fire charge weapons.
	 */
	public void release() {
		//TODO: THIS LINE GOT A NULLPOINTER ONCE UPON DYING. CANNOT REPLICATE. 
		//ADDED NULL CHECK TO INPUT PROCESSOR. HOPEFULLY FIXED.
		useToolRelease(playerData.getCurrentTool(), Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY());
	}
	
	/**
	 * Player's airblast power. Boosts player, knocks enemies/hitboxes.
	 */
	public void airblast() {
		if (airblastCdCount < 0) {
			if (playerData.getCurrentFuel() >= playerData.getAirblastCost()) {
				playerData.fuelSpend(playerData.getAirblastCost());
				airblastCdCount = airblastCd;
				useToolStart(0, airblast, Constants.PLAYER_HITBOX, Gdx.input.getX() , Gdx.graphics.getHeight() - Gdx.input.getY(), false);
			}
		}
	}
	
	/**
	 * Player interacts with an event they are overlapping with
	 */
	public void interact() {
		if (currentEvent != null && interactCdCount < 0) {
			interactCdCount = interactCd;
			currentEvent.getEventData().onInteract(this);
		}
	}
	
	/**
	 * Player uses momentum saving power, freezing nearby entities and storing their momentum for later use. has a cooldown.
	 */
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
	
	/**
	 * Player begins reloading.
	 */
	public void reload() {
		playerData.getCurrentTool().setReloading(true);
	}
	
	/**
	 * Player switches to their last equiped weapon. (does nothing if they have no previously equipped weapon.)
	 */
	public void switchToLast() {
		playerData.switchToLast();
	}
	
	/**
	 * Switches to the weapon in a specific slot.
	 * @param slot
	 */
	public void switchToSlot(int slot) {
		playerData.switchWeapon(slot);
	}
	
	/**
	 * This returns the angle of the player's arm. What was this needed for again?
	 */
	@Override
	public float getAttackAngle() {
		if (armSprite.isFlipX()) {
			return (float) Math.toRadians(attackAngle - 180);
		} else {
			return (float) Math.toRadians(attackAngle);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.sprite.combined);
		
		Vector3 bodyScreenPosition = new Vector3(
				body.getPosition().x,
				body.getPosition().y, 0);
		camera.project(bodyScreenPosition);
		
		//Determine player mouse location and hence where the arm should be angled.
		attackAngle = (float)(Math.atan2(
				bodyScreenPosition.y - (Gdx.graphics.getHeight() - Gdx.input.getY()) ,
				bodyScreenPosition.x - Gdx.input.getX()) * 180 / Math.PI);
				
		boolean flip = false;
		
		if (Math.abs(attackAngle) > 90) {
			flip = true;
		}
		
		//Depending on which way the player is facing, the connection points of various body parts are slightly offset.
		float armConnectXReal = armConnectX;
		float headConnectXReal = headConnectX;
		float armRotateXReal = armRotateX;
		
		if (flip) {
			armConnectXReal = bodyWidth - armWidth - armConnectX - 200;
			headConnectXReal = bodyWidth - headWidth - headConnectX - 200;
			armRotateXReal = armWidth - armRotateX;
			attackAngle = attackAngle + 180;
		}
		
		//This switch determins the total body y-offset to make the body bob up and down when running.
		int yOffset = 0;
		if (moveState.equals(MoveStates.MOVE_LEFT) || moveState.equals(MoveStates.MOVE_RIGHT)) {
			switch(bodyRunSprite.getKeyFrameIndex(animationTime)) {
			case 0:
			case 1:
				yOffset++;
			case 2:
				yOffset++;
			case 3:
				yOffset++;
			case 4:
				yOffset++;
			case 5:
				yOffset--;
			case 6:
				yOffset--;
			case 7:
				yOffset--;
			}
		}
		
		//This makes bodies flash red when receiving damage.
		if (flashingCount > 0) {
			batch.setColor(Color.RED);
		}
		
		//Draw a bunch of stuff
		batch.draw(toolSprite, 
				(flip ? toolWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + armConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + armConnectY * scale + yOffset, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale , armRotateY * scale,
				(flip ? -1 : 1) * toolWidth * scale, toolHeight * scale, 1, 1, attackAngle);
		
		batch.draw(bodyBackSprite, 
				(flip ? bodyBackWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + bodyConnectX * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + bodyConnectY + yOffset, 
				0, 0,
				(flip ? -1 : 1) * bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);
		
		batch.draw(armSprite, 
				(flip ? armWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + armConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + armConnectY * scale + yOffset, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale, armRotateY * scale,
				(flip ? -1 : 1) * armWidth * scale, armHeight * scale, 1, 1, attackAngle);
		
		batch.draw(momentumCdCount < 0 ? gemSprite : gemInactiveSprite, 
				(flip ? gemWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + bodyConnectY + yOffset, 
				0, 0,
				(flip ? -1 : 1) * gemWidth * scale, gemHeight * scale, 1, 1, 0);
		
		if (moveState.equals(MoveStates.MOVE_LEFT)) {
			
			if (Math.abs(attackAngle) > 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(animationTime, true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else if (moveState.equals(MoveStates.MOVE_RIGHT)) {
			if (Math.abs(attackAngle) < 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(animationTime, true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else {
			batch.draw((TextureRegion) bodyStillSprite.getKeyFrame(animationTime, true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		}
		
		batch.draw((TextureRegion) headSprite.getKeyFrame(animationTime, true), 
				(flip ? headWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2 + headConnectXReal * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + headConnectY * scale + yOffset, 
				0, 0,
				(flip ? -1 : 1) * headWidth * scale, headHeight * scale, 1, 1, 0);
		
		batch.setColor(Color.WHITE);
	}
	
	@Override
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

	public float getMomentumCdCount() {
		return momentumCdCount;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event currentEvent) {
		this.currentEvent = currentEvent;
	}

	public Queue<Vector2> getMomentums() {
		return momentums;
	}

	public void setMomentums(Queue<Vector2> momentums) {
		this.momentums = momentums;
	}

	public boolean isHovering() {
		return hovering;
	}

	public void setHovering(boolean hovering) {
		this.hovering = hovering;
	}

	public boolean isShooting() {
		return shooting;
	}

	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}	
}
