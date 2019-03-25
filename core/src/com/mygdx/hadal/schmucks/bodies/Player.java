package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.equip.ActiveItem.chargeStyle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.ActionController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatusProcTime;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * The player is the entity that the player controls.
 * @author Zachary Tu
 *
 */
public class Player extends PhysicsSchmuck {
	
	//Name of the player as chosen in the Title screen
	private String name;
	
	private final static float playerDensity = 1.0f;
	
	//Dimension of player sprite parts.
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
	
	private TextureAtlas atlasBody;
	private TextureRegion bodyBackSprite, armSprite, gemSprite, gemInactiveSprite, toolSprite;
	
	private Animation<TextureRegion> bodyStillSprite, bodyRunSprite, headSprite;
	
	private int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
	toolHeight, toolWidth, gemHeight, gemWidth;
	
	private final float spriteAnimationSpeed = 0.08f;
	
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
	
	//This is the angle that the player's arm is pointing
	private float attackAngle = 0;
	private float attackAngleClient = 0;
	
	//user data
	private PlayerBodyData playerData;
	
	//The event that the player last collided with. Used for active events that the player interacts with by pressing 'E'
	private Event currentEvent;
	
	//Equipment that the player has built in to their toolset.
	private Airblaster airblast;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	private float controllerCount = 0;
	
	//Is the player currently shooting/hovering?
	private boolean shooting = false;
	private boolean hovering = false;
	
	//This is the percent of reload completed, if reloading. This is used to display the reload ui for all players.
	private float reloadPercent;
	
	private ParticleEntity hoverBubbles;
	
	//This is the controller that causes this player to perform actions
	private ActionController controller;
	
	//this exists so that schmucks can steer towards the mouse.
	private MouseTracker mouse;
	
	//This is the loadout that this player starts with.
	private Loadout startLoadout;
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param x: player starting x position.
	 * @param y: player starting x position.
	 */
	public Player(PlayState state, int x, int y, String name, Loadout startLoadout, PlayerBodyData oldData) {
		super(state, hbWidth * scale, hbHeight * scale, x, y, state.isPvp() ? PlayState.getPVPFilter() : Constants.PLAYER_HITBOX);
		this.name = name;
		airblast = new Airblaster(this);
		
		toolSprite = GameStateManager.multitoolAtlas.findRegion("default");
		
		this.toolHeight = toolSprite.getRegionHeight();
		this.toolWidth = toolSprite.getRegionWidth();
		
		this.moveState = SchmuckMoveStates.STAND;

		this.startLoadout = startLoadout;
		this.playerData = oldData;
		
		setBodySprite(startLoadout.character.getSprite());
		loadParticles();
		
		//This schmuck trackes mouse location. Used for projectiles that home towards mouse.
		mouse = state.getMouse();
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
		hoverBubbles = new ParticleEntity(state, this, Particle.BUBBLE_TRAIL, 0.0f, 0.0f, false, particleSyncType.TICKSYNC);
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	@Override
	public void create() {
		alive = true;
		controller = new ActionController(this, state);
		state.getUiPlayer().addPlayer(this);
		state.resetController();
		
		//If null, this indicate sthat this is a newlyspawned player. Create new data for it with the provided loadout.
		//Otherwise, take the input data and reset it to match the new world.
		if (playerData == null) {
			playerData = new PlayerBodyData(this, startLoadout);
			bodyData = playerData;
			playerData.initLoadout();
			playerData.syncLoadout(startLoadout);
		} else {
			this.bodyData = playerData;
			playerData.resetData(this, world);
			playerData.syncLoadout(startLoadout);
		}
		
		//Temp invuln on spawn
		playerData.addStatus(new Invulnerability(state, 3.0f, playerData, playerData));
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, playerDensity, 0, 0, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, playerData);
		
		super.create();
		
		//Activate on-spawn effects
		if (!state.isPractice()) {
			playerData.statusProcTime(StatusProcTime.LEVEL_START, null, 0, null, null, null);
		}
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
		if (scaling) {
			grounded = feetData.getNumContacts() > 0 || leftData.getNumContacts() > 0 || rightData.getNumContacts() > 0;
		} else {
			grounded = feetData.getNumContacts() > 0;
		}
		
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
		
		if (playerData.getActiveItem().getStyle().equals(chargeStyle.byTime)) {
			playerData.getActiveItem().gainCharge(delta);
		}
		
		reloadPercent = getPlayerData().getCurrentTool().getReloadCd() / 
				(getPlayerData().getCurrentTool().getReloadTime());
		
		//process cds
		jumpCdCount-=delta;
		fastFallCdCount-=delta;
		airblastCdCount-=delta;
		interactCdCount-=delta;
		
		super.controller(delta);
	}
	
	/**
	 * Player's Hover power. Costs fuel and continuously pushes the player upwards.
	 */
	public void hover() {
		if (playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&
				playerData.getCurrentFuel() >= playerData.getHoverCost()) {
			if (jumpCdCount < 0) {
				
				//Player will continuously do small upwards bursts that cost fuel.
				playerData.fuelSpend(playerData.getHoverCost());
				jumpCdCount = hoverCd;
				pushMomentumMitigation(0, playerData.getHoverPower());
				
				hoverBubbles.onForBurst(0.5f);
			}
		}
	}
	
	/**
	 * Player's jump. Player moves up if they have jumps left.
	 */
	public void jump() {
		if (grounded) {
			if (jumpCdCount < 0) {
				jumpCdCount = jumpCd;
				pushMomentumMitigation(0, playerData.getJumpPower());
			}
		} else {
			if (playerData.getExtraJumpsUsed() < playerData.getExtraJumps()) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					playerData.setExtraJumpsUsed(playerData.getExtraJumpsUsed() + 1);;
					pushMomentumMitigation(0, playerData.getJumpPower());
				}
			}
		}
	}
	
	/**
	 * Player falls rapidly if in the air. If grounded, this also interacts with terrain events.
	 */
	public void fastFall() {
		if (fastFallCdCount < 0) {
			fastFallCdCount = fastFallCd;
			push(0, -playerData.getFastFallPower());
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
		useToolStart(delta, playerData.getCurrentTool(), hitboxfilter, (int)mouse.getBody().getPosition().x, (int)mouse.getBody().getPosition().y, true);
	}
	
	/**
	 * Player releases mouse. This is used to fire charge weapons.
	 */
	public void release() {
		useToolRelease(playerData.getCurrentTool(), hitboxfilter, (int)mouse.getBody().getPosition().x, (int)mouse.getBody().getPosition().y);
	}
	
	/**
	 * Player's airblast power. Boosts player, knocks enemies/hitboxes.
	 */
	public void airblast() {
		if (airblastCdCount < 0) {
			if (playerData.getCurrentFuel() >= playerData.getAirblastCost()) {
				playerData.fuelSpend(playerData.getAirblastCost());
				airblastCdCount = airblastCd;
				useToolStart(0, airblast, hitboxfilter, (int)mouse.getBody().getPosition().x, (int)mouse.getBody().getPosition().y, false);
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
	 * Player uses active item.
	 */
	public void activeItem() {
		useToolStart(0, playerData.getActiveItem(), hitboxfilter, (int)mouse.getBody().getPosition().x, (int)mouse.getBody().getPosition().y, false);
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
	
	private float armConnectXReal;
	private float headConnectXReal;
	private float armRotateXReal;
	@Override
	public void render(SpriteBatch batch) {
		batch.setProjectionMatrix(state.sprite.combined);
		
		//Determine player mouse location and hence where the arm should be angled.
		if (mouse.getBody() != null) {
			attackAngle = (float)(Math.atan2(
					body.getPosition().y - mouse.getBody().getPosition().y,
					body.getPosition().x - mouse.getBody().getPosition().x) * 180 / Math.PI);
		} else {
			attackAngle = attackAngleClient;
		}
		
		
/*		for (ParticleEmitter p : hoverBubbles.getEffect().getEmitters()) {
			p.getAngle().setHighMax(attackAngle);
            p.getAngle().setHighMin(attackAngle);
            p.getAngle().setLow(attackAngle, attackAngle);
		}*/
		
		boolean flip = false;
		
		if (Math.abs(attackAngle) > 90) {
			flip = true;
		}
		
		//Depending on which way the player is facing, the connection points of various body parts are slightly offset.
		armConnectXReal = armConnectX;
		headConnectXReal = headConnectX;
		armRotateXReal = armRotateX;
		
		if (flip) {
			armConnectXReal = bodyWidth - armWidth - armConnectX - 200;
			headConnectXReal = bodyWidth - headWidth - headConnectX - 200;
			armRotateXReal = armWidth - armRotateX;
			attackAngle = attackAngle + 180;
		}
		
		//This switch determins the total body y-offset to make the body bob up and down when running.
		int yOffset = 0;
		if (moveState.equals(SchmuckMoveStates.MOVE_LEFT) || moveState.equals(SchmuckMoveStates.MOVE_RIGHT)) {
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
			batch.setShader(HadalGame.shader);
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
		
		batch.draw(playerData.getActiveItem().isReady() ? gemSprite : gemInactiveSprite, 
				(flip ? gemWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
				body.getPosition().y * PPM - hbHeight * scale / 2 + bodyConnectY + yOffset, 
				0, 0,
				(flip ? -1 : 1) * gemWidth * scale, gemHeight * scale, 1, 1, 0);
		
		boolean reverse = false;
		
		if (moveState.equals(SchmuckMoveStates.MOVE_LEFT)) {
			
			if (Math.abs(attackAngle) > 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
				reverse = true;
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(grounded ? animationTime : getFreezeFrame(reverse), true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else if (moveState.equals(SchmuckMoveStates.MOVE_RIGHT)) {
			if (Math.abs(attackAngle) < 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
				reverse = true;
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(grounded ? animationTime : getFreezeFrame(reverse), true), 
					(flip ? bodyWidth * scale : 0) + body.getPosition().x * PPM - hbWidth * scale / 2  + bodyConnectX * scale, 
					body.getPosition().y * PPM - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else {
			bodyRunSprite.setPlayMode(PlayMode.LOOP);
			batch.draw(grounded ? (TextureRegion) bodyStillSprite.getKeyFrame(animationTime, true) : 
					(TextureRegion) bodyRunSprite.getKeyFrame(getFreezeFrame(reverse)), 
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
		
		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	/**
	 * When the player is in the air, their animation freezes. This gets the fram for that
	 * @param reverse: which direction is the player facing
	 * @return
	 */
	public int getFreezeFrame(boolean reverse) {
		if (Math.abs(body.getLinearVelocity().x) > Math.abs(body.getLinearVelocity().y)) {
			return reverse ? 5 : 2;
		} else {
			return reverse ? 1 : 6;
		}
	}
	
	public void createGibs() {
		if (alive) {
			new Ragdoll(state, headWidth * scale, headHeight * scale, 
					(int)(body.getPosition().x * PPM), 
					(int)(body.getPosition().y * PPM), (TextureRegion) headSprite.getKeyFrame(animationTime, true), body.getLinearVelocity(), 5.0f);
			
			new Ragdoll(state, bodyWidth * scale, bodyHeight * scale, 
					(int)(body.getPosition().x * PPM), 
					(int)(body.getPosition().y * PPM), (TextureRegion) bodyStillSprite.getKeyFrame(animationTime, true), body.getLinearVelocity(), 5.0f);
			
			new Ragdoll(state, armWidth * scale, armHeight * scale, 
					(int)(body.getPosition().x * PPM), 
					(int)(body.getPosition().y * PPM), armSprite, body.getLinearVelocity(), 5.0f);
			
			new Ragdoll(state, bodyBackWidth * scale, bodyBackHeight * scale, 
					(int)(body.getPosition().x * PPM), 
					(int)(body.getPosition().y * PPM), bodyBackSprite, body.getLinearVelocity(), 5.0f);
			
			new Ragdoll(state, toolWidth * scale, toolHeight * scale, 
					(int)(body.getPosition().x * PPM), 
					(int)(body.getPosition().y * PPM), toolSprite, body.getLinearVelocity(), 5.0f);
		}
	}
	
	/**
	 * This is called by the server when the player is created. Sends a packet to clients to instruct them to build a new player
	 * with the desired name and loadout
	 */
	@Override
	public Object onServerCreate() {
		return new Packets.CreatePlayer(entityID.toString(), name, playerData.getLoadout());
	}
	
	/**
	 * This is called every engine tick. The server player sends a packet to the corresponding client player.
	 * This packet updates mouse location, groundedness, loadout and stat information
	 */
	@Override
	public void onServerSync() {
		super.onServerSync();
		HadalGame.server.server.sendToAllUDP( new Packets.SyncPlayer(entityID.toString(),
				(float)(Math.atan2(
						body.getPosition().y - mouse.getBody().getPosition().y,
						body.getPosition().x - mouse.getBody().getPosition().x) * 180 / Math.PI),
				grounded, playerData.getCurrentSlot(), playerData.getCurrentTool().getClipLeft(), 
				playerData.getCurrentTool().getAmmoLeft(), playerData.getCurrentTool().getClipSize(),
				playerData.getMaxHp(), playerData.getMaxFuel(), playerData.getAirblastCost(),
				playerData.getActiveItem().chargePercent(), playerData.getCurrentTool().isReloading(), reloadPercent));
	}
	
	/**
	 * The client Player receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPlayer) {
			Packets.SyncPlayer p = (Packets.SyncPlayer) o;
			
			attackAngleClient = p.attackAngle;
			grounded = p.grounded;
			playerData.setCurrentSlot(p.currentSlot);
			playerData.setCurrentTool(playerData.getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrames().get(0));
			playerData.getCurrentTool().setClipLeft(p.currentClip);
			playerData.setOverrideMaxHp(p.maxHp);
			playerData.setOverrideMaxFuel(p.maxFuel);
			playerData.setOverrideClipSize(p.maxClip);
			playerData.setOverrideAmmoSize(p.currentAmmo);
			playerData.setOverrideAirblastCost(p.airblastCost);
			playerData.getActiveItem().setCurrentCharge(p.activeCharge * playerData.getActiveItem().getMaxCharge());
			playerData.getCurrentTool().setReloading(p.reloading);
			reloadPercent = p.reloadPercent;	
		} else {
			super.onClientSync(o);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		state.getUiPlayer().removePlayer(this);
		playerData.setCurrentHp(0);
	}
	
	@Override
	public HadalData getHadalData() {
		return playerData;
	}
	
	@Override
	public BodyData getBodyData() {
		return playerData;
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

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event currentEvent) {
		this.currentEvent = currentEvent;
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
	
	public float getReloadPercent() {
		return reloadPercent;
	}

	public void setReloadPercent(float reloadPercent) {
		this.reloadPercent = reloadPercent;
	}

	public ActionController getController() {
		return controller;
	}
	
	public Loadout getStartLoadout() {
		return startLoadout;
	}

	public void setStartLoadout(Loadout startLoadout) {
		this.startLoadout = startLoadout;
	}

	public MouseTracker getMouse() {
		return mouse;
	}

	public void setMouse(MouseTracker mouse) {
		this.mouse = mouse;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
