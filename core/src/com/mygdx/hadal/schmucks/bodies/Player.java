package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem.chargeStyle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.ActionController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
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
	public static final float uiScale = 0.4f;
	
	private TextureRegion bodyBackSprite, armSprite, gemSprite, gemInactiveSprite, toolSprite;
	private Animation<TextureRegion> bodyStillSprite, bodyRunSprite, headSprite;
	
	private int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
	toolHeight, toolWidth, gemHeight, gemWidth;
	
	private TextureRegion reload, reloadMeter, reloadBar;
	private Texture empty, full;
	
	//counters for various cooldowns.
	private float hoverCd = 0.08f;
	private float jumpCd = 0.25f;
	private float jumpCdCount = 0;
	
	private float fastFallCd = 0.05f;
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
	
	//Is the player currently shooting/hovering/fastfalling?
	private boolean shooting = false;
	private boolean hoveringAttempt = false;
	private boolean hovering = false;
	private boolean fastFalling = false;
	
	//This is the percent of reload completed, if reloading. This is used to display the reload ui for all players.
	private float reloadPercent;
	
	//This is the percent of charge completed, if charging. This is used to display the charge ui for all players.
	private float chargePercent;
		
	private ParticleEntity hoverBubbles;
	
	//This is the controller that causes this player to perform actions
	private ActionController controller;
	
	//this exists so that schmucks can steer towards the mouse.
	private MouseTracker mouse;
	
	//This is the loadout that this player starts with.
	private Loadout startLoadout;
	
	//This is the connection id of the player (0 if server)
	private int connID;
	
	//should we reset this player's playerData stuff upon creation
	private boolean reset;
		
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param x: player starting x position.
	 * @param y: player starting x position.
	 * @param startLoadout: This is the player's starting loadout
	 * 
	 */
	public Player(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connID, boolean reset) {
		super(state, startPos, new Vector2(hbWidth * scale, hbHeight * scale), state.isPvp() ? PlayState.getPVPFilter() : Constants.PLAYER_HITBOX);
		this.name = name;
		airblast = new Airblaster(this);
		
		toolSprite = Sprite.MT_DEFAULT.getFrame();
		
		this.toolHeight = toolSprite.getRegionHeight();
		this.toolWidth = toolSprite.getRegionWidth();
		
		this.moveState = SchmuckMoveStates.STAND;

		this.startLoadout = startLoadout;
		this.playerData = oldData;
		this.connID = connID;
		this.reset = reset;
		
		setBodySprite(startLoadout.character);
		loadParticles();
		
		//This schmuck trackes mouse location. Used for projectiles that home towards mouse.
		mouse = state.getMouse();
		
		this.reload = Sprite.UI_RELOAD.getFrame();
		this.reloadMeter = Sprite.UI_RELOAD_METER.getFrame();
		this.reloadBar = Sprite.UI_RELOAD_BAR.getFrame();
		
		this.empty = new Texture(AssetList.HEART_EMPTY.toString());
		this.full = new Texture(AssetList.HEART_FULL.toString());
	}
	
	/**
	 * This method prepares the player sprite from various texture regions.
	 * @param playerSprite
	 */
	public void setBodySprite(UnlockCharacter character) {

		bodyRunSprite =  new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, Sprite.getCharacterSprites(character.getSprite(), "body_run").getFrames());	
		bodyStillSprite =  new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, Sprite.getCharacterSprites(character.getSprite(), "body_stand").getFrames());	
		bodyBackSprite = Sprite.getCharacterSprites(character.getSprite(), "body_background").getFrame();
		armSprite = Sprite.getCharacterSprites(character.getSprite(), "arm").getFrame();
		headSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, Sprite.getCharacterSprites(character.getSprite(), "head").getFrames());	
		gemSprite = Sprite.getCharacterSprites(character.getSprite(), "gem_active").getFrame();
		gemInactiveSprite = Sprite.getCharacterSprites(character.getSprite(), "gem_inactive").getFrame();
		
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
		destroyed = false;
		controller = new ActionController(this, state);
		state.resetController();
		
		//If resetting, this indicates that this is a newlyspawned or respawned player. Create new data for it with the provided loadout.
		//Otherwise, take the input data and reset it to match the new world.
		if (reset) {
			playerData = new PlayerBodyData(this, startLoadout);
		} else {
			playerData.updateOldData(this, world);
		}
		bodyData = playerData;
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, playerDensity, 0, 0, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, playerData);
		
		if (reset) {
			playerData.initLoadout();
		}
		
		//Temp invuln on spawn
		playerData.addStatus(new Invulnerability(state, 3.0f, playerData, playerData));
				
		super.create();
		
		//if this is the client creating their own player, tell the server we are ready to sync player-related stuff
		if (!state.isServer() && state.getPlayer().equals(this)) {
			Packets.ClientPlayerCreated connected = new Packets.ClientPlayerCreated();
            HadalGame.client.client.sendTCP(connected);
		}
		
		//Activate on-spawn effects
		if (!state.isPractice() && reset) {
			playerData.statusProcTime(new ProcTime.PlayerCreate());
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

			if (hoveringAttempt && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&	playerData.getCurrentFuel() >= playerData.getHoverCost()) {
				if (jumpCdCount < 0) {
					hover();
					hovering = true;
				}
			} else {
				hovering = false;
			}
			
			if (fastFalling) {
				fastFall();
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
		playerData.fuelGain(playerData.getStat(Stats.FUEL_REGEN) * delta);
		
		//If player is reloading, run the reload method of the current equipment.
		if (playerData.getCurrentTool().isReloading()) {
			playerData.getCurrentTool().reload(delta);
		}
		
		if (playerData.getActiveItem().getStyle().equals(chargeStyle.byTime)) {
			playerData.getActiveItem().gainCharge(delta);
		}
		
		reloadPercent = getPlayerData().getCurrentTool().getReloadCd() / (getPlayerData().getCurrentTool().getReloadTime());
		chargePercent = getPlayerData().getCurrentTool().getChargeCd() / (getPlayerData().getCurrentTool().getChargeTime());
		
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
		if (jumpCdCount < 0) {
			
			//Player will continuously do small upwards bursts that cost fuel.
			playerData.fuelSpend(playerData.getHoverCost());
			jumpCdCount = hoverCd;
			pushMomentumMitigation(0, playerData.getHoverPower());
			
			hoverBubbles.onForBurst(0.5f);
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
					playerData.setExtraJumpsUsed(playerData.getExtraJumpsUsed() + 1);
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
			pushMomentumMitigation(0, -playerData.getFastFallPower());
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
		useToolStart(delta, playerData.getCurrentTool(), hitboxfilter, mouse.getPixelPosition(), true);
	}
	
	/**
	 * Player releases mouse. This is used to fire charge weapons.
	 */
	public void release() {
		useToolRelease(playerData.getCurrentTool(), hitboxfilter, mouse.getPixelPosition());
	}
	
	/**
	 * Player's airblast power. Boosts player, knocks enemies/hitboxes.
	 */
	public void airblast() {
		if (airblastCdCount < 0) {
			if (playerData.getCurrentFuel() >= playerData.getAirblastCost()) {
				playerData.fuelSpend(playerData.getAirblastCost());
				airblastCdCount = airblastCd;
				useToolStart(0, airblast, hitboxfilter, mouse.getPixelPosition(), false);
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
		useToolStart(0, playerData.getActiveItem(), hitboxfilter, mouse.getPixelPosition(), false);
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
		super.render(batch);
		
		//Determine player mouse location and hence where the arm should be angled.
		if (mouse.getBody() != null) {
			attackAngle = (float)(Math.atan2(
					getPixelPosition().y - mouse.getPixelPosition().y,
					getPixelPosition().x - mouse.getPixelPosition().x) * 180 / Math.PI);
		} else {
			attackAngle = attackAngleClient;
		}

		//flip determins if the player is facing left or right
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
		
		//This switch determines the total body y-offset to make the body bob up and down when running.
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
		
		//Draw a bunch of stuff
		batch.draw(toolSprite, 
				(flip ? toolWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2 + armConnectXReal * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + armConnectY * scale + yOffset, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale , armRotateY * scale,
				(flip ? -1 : 1) * toolWidth * scale, toolHeight * scale, 1, 1, attackAngle);
		
		batch.draw(bodyBackSprite, 
				(flip ? bodyBackWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2 + bodyConnectX * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + bodyConnectY + yOffset, 
				0, 0,
				(flip ? -1 : 1) * bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);
		
		batch.draw(armSprite, 
				(flip ? armWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2 + armConnectXReal * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + armConnectY * scale + yOffset, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale, armRotateY * scale,
				(flip ? -1 : 1) * armWidth * scale, armHeight * scale, 1, 1, attackAngle);
		
		batch.draw(playerData.getActiveItem().isReady() ? gemSprite : gemInactiveSprite, 
				(flip ? gemWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2  + bodyConnectX * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + bodyConnectY + yOffset, 
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
					(flip ? bodyWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2  + bodyConnectX * scale, 
					getPixelPosition().y - hbHeight * scale / 2  + bodyConnectY + yOffset, 
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
					(flip ? bodyWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2  + bodyConnectX * scale, 
					getPixelPosition().y - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else {
			bodyRunSprite.setPlayMode(PlayMode.LOOP);
			batch.draw(grounded ? (TextureRegion) bodyStillSprite.getKeyFrame(animationTime, true) : 
					(TextureRegion) bodyRunSprite.getKeyFrame(getFreezeFrame(reverse)), 
					(flip ? bodyWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2  + bodyConnectX * scale, 
					getPixelPosition().y - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		}
		
		batch.draw((TextureRegion) headSprite.getKeyFrame(animationTime, true), 
				(flip ? headWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2 + headConnectXReal * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + headConnectY * scale + yOffset, 
				0, 0,
				(flip ? -1 : 1) * headWidth * scale, headHeight * scale, 1, 1, 0);
		
		if (shaderCount > 0) {
			batch.setShader(null);
		}
		
		//render player ui
		if (playerData.getCurrentTool().isReloading()) {
			
			float x = getPixelPosition().x - reload.getRegionWidth() * uiScale / 2;
			float y = getPixelPosition().y + reload.getRegionHeight() * uiScale + Player.hbHeight * scale / 2;
			
			//Calculate reload progress
			float percent = getReloadPercent();
			
			batch.draw(reloadBar, x + 10, y + 4, reloadBar.getRegionWidth() * uiScale * percent, reloadBar.getRegionHeight() * uiScale);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, "RELOADING", x + 12, y + reload.getRegionHeight() * uiScale);
			batch.draw(reloadMeter, x, y, reload.getRegionWidth() * uiScale, reload.getRegionHeight() * uiScale);
		}
		
		if (playerData.getCurrentTool().isCharging()) {
			
			float x = getPixelPosition().x - reload.getRegionWidth() * uiScale / 2;
			float y = getPixelPosition().y + reload.getRegionHeight() * uiScale + Player.hbHeight * scale / 2;
			
			//Calculate charge progress
			batch.draw(reloadBar, x + 10, y + 4, reloadBar.getRegionWidth() * uiScale * chargePercent, reloadBar.getRegionHeight() * uiScale);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, "CHARGING", x + 12, y + reload.getRegionHeight() * uiScale);
			batch.draw(reloadMeter, x, y, reload.getRegionWidth() * uiScale, reload.getRegionHeight() * uiScale);
		}
		
		if (playerData.getCurrentTool().isOutofAmmo()) {
			
			float x = getPixelPosition().x - reload.getRegionWidth() * uiScale / 2;
			float y = getPixelPosition().y + reload.getRegionHeight() * uiScale + Player.hbHeight * scale / 2;
			
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, "OUT OF AMMO", x + 12, y + reload.getRegionHeight() * uiScale);
		}
		
		//This draws a heart by the player's sprite to indicate hp remaining
		float x = getPixelPosition().x - Player.hbWidth * scale - empty.getWidth() * uiScale + 10;
		float y = getPixelPosition().y + Player.hbHeight * scale / 2 - 5;
		
		float hpRatio = 0.0f;
		
		if (state.isServer()) {
			hpRatio = playerData.getCurrentHp() / playerData.getStat(Stats.MAX_HP);
		} else {
			hpRatio = playerData.getOverrideHpPercent();
		}
		
		batch.draw(empty, x - empty.getWidth() / 2 * uiScale, y - empty.getHeight() / 2 * uiScale,
                empty.getWidth() / 2, empty.getHeight() / 2,
                empty.getWidth(), empty.getHeight(),
                uiScale, uiScale, 0, 0, 0, empty.getWidth(), empty.getHeight(), false, false);

        batch.draw(full, x - full.getWidth() / 2 * uiScale, y - full.getHeight() / 2 * uiScale - (int)(full.getHeight() * (1 - hpRatio) * uiScale),
                full.getWidth() / 2, full.getHeight() / 2,
                full.getWidth(), full.getHeight(),
                uiScale, uiScale, 0, 0, (int) (full.getHeight() * (1 - hpRatio)),
                full.getWidth(), full.getHeight(), false, false);
		
		HadalGame.SYSTEM_FONT_SPRITE.getData().setScale(1.0f);
		HadalGame.SYSTEM_FONT_SPRITE.draw(batch, name, 
				getPixelPosition().x - Player.hbWidth * Player.scale / 2, 
				getPixelPosition().y + Player.hbHeight * Player.scale / 2 + 15);
	}
	
	/**
	 * When the player is in the air, their animation freezes. This gets the frame for that
	 * @param reverse: which direction is the player facing
	 * @return
	 */
	public int getFreezeFrame(boolean reverse) {
		if (Math.abs(getLinearVelocity().x) > Math.abs(getLinearVelocity().y)) {
			return reverse ? 5 : 2;
		} else {
			return reverse ? 1 : 6;
		}
	}
	
	/**
	 * This creates a bunch of gib ragdolls when the player dies.
	 */
	private final static float gibDuration = 3.0f;
	public void createGibs() {
		if (alive) {
			new Ragdoll(state, getPixelPosition(), new Vector2(headWidth, headHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "head"), getLinearVelocity(), gibDuration, false);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(bodyWidth, bodyHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "body_stand"), getLinearVelocity(), gibDuration, false);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(armWidth, armHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "arm"), getLinearVelocity(), gibDuration, false);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(bodyBackWidth, bodyBackHeight).scl(scale), 
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "body_background"), getLinearVelocity(), gibDuration, false);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(gemWidth, gemHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "gem_active"), getLinearVelocity(), gibDuration, false);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(toolWidth, toolHeight).scl(scale)
					, playerData.getCurrentTool().getWeaponSprite(), getLinearVelocity(), 5.0f, false);
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
		
		HadalGame.server.sendToAllUDP(new Packets.SyncPlayerAll(entityID.toString(), (float)(Math.atan2(getPixelPosition().y - mouse.getPixelPosition().y, getPixelPosition().x - mouse.getPixelPosition().x) * 180 / Math.PI),
				playerData.getCurrentHp() / playerData.getStat(Stats.MAX_HP), grounded, playerData.getCurrentSlot(), 
				playerData.getCurrentTool().isReloading(), reloadPercent, playerData.getCurrentTool().isCharging(), chargePercent));
		
		HadalGame.server.sendPacketToPlayer(this, new Packets.SyncPlayerSelf(playerData.getCurrentFuel() / playerData.getStat(Stats.MAX_FUEL), 
				playerData.getCurrentTool().getClipLeft(), playerData.getCurrentTool().getAmmoLeft(), playerData.getActiveItem().chargePercent()));
	}
	
	/**
	 * The client Player receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncPlayerAll) {
			Packets.SyncPlayerAll p = (Packets.SyncPlayerAll) o;

			attackAngleClient = p.attackAngle;
			playerData.setOverrideHpPercent(p.hpPercent);
			grounded = p.grounded;
			playerData.setCurrentSlot(p.currentSlot);
			playerData.setCurrentTool(playerData.getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrame());
			playerData.getCurrentTool().setReloading(p.reloading);
			reloadPercent = p.reloadPercent;
			playerData.getCurrentTool().setCharging(p.charging);
			chargePercent = p.chargePercent;
		} else {
			super.onClientSync(o);
		}
	}
	
	private float shortestFraction;
	private Vector2 originPt = new Vector2();
	private Vector2 endPt = new Vector2();
	private Vector2 offset = new Vector2();
	private final static float spawnDist = 2.0f;
	
	/**
	 * This method makes projectiles fired by the player spawn offset to be at the tip of the gun
	 */
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		
		originPt.set(getPosition());
		offset.set(startVelo);
		endPt.set(getPosition()).add(offset.nor().scl(spawnDist + projSize / 4 / PPM));
		shortestFraction = 1.0f;
		
		if (originPt.x != endPt.x || originPt.y != endPt.y) {

			state.getWorld().rayCast(new RayCastCallback() {

				@Override
				public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
					
					if (fixture.getFilterData().categoryBits == (short)Constants.BIT_WALL && fraction < shortestFraction) {
							shortestFraction = fraction;
							return fraction;
					}
					return -1.0f;
				}
				
			}, originPt, endPt);
		}
		return originPt.add(offset.nor().scl((spawnDist + projSize / 4 / PPM) * shortestFraction)).scl(PPM);
	}
	
	@Override
	public void dispose() { super.dispose(); }
	
	@Override
	public HadalData getHadalData() { return playerData; }
	
	@Override
	public BodyData getBodyData() { return playerData; }
	
	public PlayerBodyData getPlayerData() {	return playerData; }

	public void setToolSprite(TextureRegion sprite) { toolSprite = sprite; }

	public Event getCurrentEvent() { return currentEvent; }

	public void setCurrentEvent(Event currentEvent) { this.currentEvent = currentEvent; }

	public boolean isHovering() { return hovering; }

	public void setHoveringAttempt(boolean hoveringAttempt) { this.hoveringAttempt = hoveringAttempt; }

	public boolean isFastFalling() { return fastFalling; }

	public void setFastFalling(boolean fastFalling) { this.fastFalling = fastFalling; }

	public boolean isShooting() { return shooting; }

	public void setShooting(boolean shooting) { this.shooting = shooting; }
	
	public float getReloadPercent() { return reloadPercent; }

	public void setReloadPercent(float reloadPercent) { this.reloadPercent = reloadPercent; }

	public float getChargePercent() {return chargePercent;}

	public void setChargePercent(float chargePercent) {this.chargePercent = chargePercent;}

	public ActionController getController() { return controller;}
	
	public Loadout getStartLoadout() { return startLoadout;}

	public void setStartLoadout(Loadout startLoadout) { this.startLoadout = startLoadout;}

	public MouseTracker getMouse() { return mouse; }

	public void setMouse(MouseTracker mouse) { this.mouse = mouse; }

	public String getName() { return name; }
	
	public int getConnID() { return connID;	}
}
