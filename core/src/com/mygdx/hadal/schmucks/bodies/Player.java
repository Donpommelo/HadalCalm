package com.mygdx.hadal.schmucks.bodies;

import static com.mygdx.hadal.utils.Constants.PPM;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem.chargeStyle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.input.ActionController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * The player is the entity that the player controls.
 * @author Zachary Tu
 *
 */
public class Player extends PhysicsSchmuck {
	
	private final static int baseHp = 100;
	
	private final static float playerDensity = 1.0f;
	
	private final static float controllerInterval = 1 / 60f;
	
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
	
	private TextureRegion reload, reloadMeter, reloadBar;
	private Texture empty, full;
	
	private int armWidth, armHeight, headWidth, headHeight, bodyWidth, bodyHeight, bodyBackWidth, bodyBackHeight,
	toolHeight, toolWidth, gemHeight, gemWidth;
	
	//Fixtures and user data
	private Fixture feet, rightSensor, leftSensor;
	private FeetData feetData, rightData, leftData;
	
	//These track whether the schmuck has a specific artifacts equipped (to enable wall scaling.) and invisibility (to manage particles without checking statuses every tick)
	private boolean scaling, invisible;
	
	//counters for various cooldowns.
	private final static float hoverCd = 0.08f;
	private float jumpCd = 0.25f;
	private float jumpCdCount = 0;
	
	private final static float fastFallCd = 0.05f;
	private float fastFallCdCount = 0;
	
	private final static float airblastCd = 0.25f;
	private float airblastCdCount = 0;
	
	protected final static float interactCd = 0.15f;
	protected float interactCdCount = 0;
	
	private final static float airAnimationSlow = 3.0f;

	//This is the angle that the player's arm is pointing
	private float attackAngle;
	
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
	private float chargePercent, chargeDelayed;
	
	//particles and sounds used by the player
	private ParticleEntity hoverBubbles, dustCloud;
	private SoundEntity runSound, hoverSound, reloadSound;
	
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
	
	//this is the point we are starting at.
	private StartPoint start;
	
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param startPos: the player's starting location
	 * @param name: the player's name
	 * @param startLoadout: This is the player's starting loadout
	 * @param: oldData: If created after a stage transition, this is the data of the previous player.
	 * @param connID: connection id. 0 if server.
	 * @param reset: do we reset the player's stats after creating them?
	 * @param start: the start point that the player spawns at.
	 * 
	 * Note that the starting filter logic goes like this: if not pvp, we just have the default player hbox filter. If pvp, we generate a new hbox filter if 
	 * we are a new player and use our old one if we are respawning
	 */
	public Player(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connID, boolean reset, StartPoint start) {
		super(state, startPos, new Vector2(hbWidth * scale, hbHeight * scale), name, Constants.PLAYER_HITBOX, baseHp);
		this.name = name;
		airblast = new Airblaster(this);
		
		toolSprite = Sprite.MT_DEFAULT.getFrame();
		
		this.toolHeight = toolSprite.getRegionHeight();
		this.toolWidth = toolSprite.getRegionWidth();
		
		this.moveState = MoveState.STAND;

		this.startLoadout = startLoadout;
		this.playerData = oldData;
		this.connID = connID;
		this.reset = reset;
		this.start = start;
		
		//process player pvp hitbox filter. If newly spawned or coming from non-pvp map, we give a new hbox filter.
		//Otherwise they get a newly generated filter
		if (state.isPvp()) {
			if (oldData == null) {
				hitboxfilter = PlayState.getPVPFilter();
			} else {
				if (oldData.getPlayer().getHitboxfilter() == Constants.PLAYER_HITBOX) {
					hitboxfilter = PlayState.getPVPFilter();
				} else {
					hitboxfilter = oldData.getPlayer().getHitboxfilter();
				}
			}
		}
		
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
		hoverBubbles = new ParticleEntity(state, this, Particle.BUBBLE_TRAIL, 1.0f, 0.0f, false, particleSyncType.TICKSYNC, new Vector2(0, -size.y / 2));
		dustCloud = new ParticleEntity(state, this, Particle.DUST, 1.0f, 0.0f, false, particleSyncType.TICKSYNC, new Vector2(0, -size.y / 2));
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	@Override
	public void create() {
		alive = true;
		destroyed = false;
		controller = new ActionController(this, state);
		
		if (this == state.getPlayer()) {
			state.resetController();
		}
		
		//this makes the player's selected slot persist after respawning
		int currentSlot = 1;
		if (playerData != null) {
			currentSlot = playerData.getCurrentSlot() + 1;
		}
		
		//If resetting, this indicates that this is a newlyspawned or respawned player. Create new data for it with the provided loadout.
		//Otherwise, take the input data and reset it to match the new world.
		if (reset) {
			playerData = new PlayerBodyData(this, startLoadout);
		}
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1.0f, playerDensity, 0.0f, 0.0f, false, true, Constants.BIT_PLAYER, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, playerData);
		
		//On the server, we create several extra fixtures to keep track of feet/sides to determine when the player gets their jump back and what terrain event they are standing on.
		if (state.isServer()) {
			this.feetData = new FeetData(UserDataTypes.FEET, this); 
			
			this.feet = this.body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(0.5f,  - size.y / 2), new Vector2(size.x - 2, size.y / 8), true, 0, 0, 0, 0,
					Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_DROPTHROUGHWALL), hitboxfilter));
			
			feet.setUserData(feetData);
			
			this.leftData = new FeetData(UserDataTypes.FEET, this); 
			
			this.leftSensor = this.body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(-size.x / 2, 0.5f), new Vector2(size.x / 8, size.y - 2), true, 0, 0, 0, 0,
					Constants.BIT_SENSOR, (short)(Constants.BIT_WALL), hitboxfilter));
			
			leftSensor.setUserData(leftData);
			
			this.rightData = new FeetData(UserDataTypes.FEET, this); 
			
			this.rightSensor = this.body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(size.x / 2,  0.5f), new Vector2(size.x / 8, size.y - 2), true, 0, 0, 0, 0,
					Constants.BIT_SENSOR, Constants.BIT_WALL, hitboxfilter));
			
			rightSensor.setUserData(rightData);
		}

		//If the player is spawning into a new level, initialize loadout and give brief invulnerability.
		if (reset) {
			playerData.initLoadout();
			playerData.addStatus(new Invulnerability(state, 3.0f, playerData, playerData));
		} else {
			playerData.updateOldData(this);
		}

		playerData.switchWeapon(currentSlot);
				
		//if this is the client creating their own player, tell the server we are ready to sync player-related stuff
		if (!state.isServer() && state.getPlayer().equals(this)) {
			Packets.ClientPlayerCreated connected = new Packets.ClientPlayerCreated();
            HadalGame.client.sendTCP(connected);
		}
		
		//Activate on-spawn effects
		if (reset) {
			playerData.statusProcTime(new ProcTime.PlayerCreate());
		}

		//activate start point events (these usually just set up camera bounds/zoom and stuff like that
		//This line is here so that it does not occur before events are done being created.
		if (start != null && state.getPlayer().equals(this)) {
			start.playerStart(this);
		}
	}
	
	/**
	 * The player's controller currently polls for input.
	 */
	@Override
	public void controller(float delta) {
		controllerCount += delta;
		
		//This line ensures that this runs every 1/60 second regardless of computer speed.
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;

			//if the player is successfully hovering, run hover(). We check if hover is successful so that effects that run when hovering do not activate when not actually hovering (white smoker)
			if (hoveringAttempt && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&	playerData.getCurrentFuel() >= playerData.getHoverCost()) {
				if (jumpCdCount < 0) {
					hover();
					hovering = true;
				}
			} else {
				
				//turn off hover particles and sound
				hoverBubbles.turnOff();
				hovering = false;
				
				if (hoverSound != null) {
					hoverSound.turnOff();
				}
			}
			
			if (fastFalling) {
				fastFall();
			}
			
			if ((moveState.equals(MoveState.MOVE_LEFT) || moveState.equals(MoveState.MOVE_RIGHT)) && grounded && !invisible) {
				
				//turn on running particles and sound
				dustCloud.turnOn();
				
				if (runSound == null) {
					runSound = new SoundEntity(state, this, SoundEffect.RUN, 0.1f, true, true, soundSyncType.TICKSYNC);
				} else {
					runSound.turnOn();
				}
			} else {
				
				//turn off running particles and sound
				dustCloud.turnOff();
				if (runSound != null) {
					runSound.turnOff();
				}
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
			
			//turn on reloading particles and sound
			if (reloadSound == null) {
				reloadSound = new SoundEntity(state, this, SoundEffect.RELOAD, 0.2f, true, true, soundSyncType.TICKSYNC);
			} else {
				reloadSound.turnOn();
			}
		} else {
			if (reloadSound != null) {
				reloadSound.turnOff();
			}
		}
		
		//charge active item if it charges with time.
		if (playerData.getActiveItem().getStyle().equals(chargeStyle.byTime)) {
			playerData.getActiveItem().gainCharge(delta);
		}
		
		//keep track of reload/charge percent to properly sync those fields in the ui
		reloadPercent = playerData.getCurrentTool().getReloadCd() / (getPlayerData().getCurrentTool().getReloadTime());
		chargePercent = playerData.getCurrentTool().getChargeCd() / (getPlayerData().getCurrentTool().getChargeTime());
		
		//process cds
		jumpCdCount-=delta;
		fastFallCdCount-=delta;
		airblastCdCount-=delta;
		interactCdCount-=delta;
		
		
		//Determine player mouse location and hence where the arm should be angled.
		if (mouse.getBody() != null) {
			mouseAngle.set(getPixelPosition().y, getPixelPosition().x).sub(mouse.getPixelPosition().y, mouse.getPixelPosition().x);
		}
		attackAngle = (float)(Math.atan2(mouseAngle.x, mouseAngle.y) * 180 / Math.PI);
		
		//process weapon update (this is for weapons that have an effect that activates over time which is pretty rare)
		playerData.getCurrentTool().update(delta);
		
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
			
			//turn on hovering particles and sound
			hoverBubbles.turnOn();
			
			if (hoverSound == null) {
				hoverSound = new SoundEntity(state, this, SoundEffect.HOVER, 0.2f, true, true, soundSyncType.TICKSYNC);
			}
			hoverSound.turnOn();
			
		} else {
			
			//turn off hovering particles and sound
			hoverBubbles.turnOff();
			if (hoverSound != null) {
				hoverSound.turnOff();
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
				
				//activate jump particles and sound
				new ParticleEntity(state, new Vector2(getPixelPosition().x, getPixelPosition().y - hbHeight * scale / 2), Particle.WATER_BURST, 1.0f, true, particleSyncType.CREATESYNC);
				SoundEffect.JUMP.playUniversal(state, getPixelPosition(), 0.2f, false);
			}
		} else {
			if (playerData.getExtraJumpsUsed() < playerData.getExtraJumps()) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					playerData.setExtraJumpsUsed(playerData.getExtraJumpsUsed() + 1);
					pushMomentumMitigation(0, playerData.getJumpPower());
					
					//activate double-jump particles and sound
					new ParticleEntity(state, this, Particle.SPLASH, 0.0f, 0.75f, true, particleSyncType.CREATESYNC);
					SoundEffect.DOUBLEJUMP.playUniversal(state, getPixelPosition(), 0.2f, false);
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
			if (playerData.getFastFallPower() > 0) {
				push(0, -playerData.getFastFallPower());
			}
		}
		if (!feetData.getTerrain().isEmpty()) {
			feetData.getTerrain().get(0).getEventData().onInteract(this);
		}
	}
	
	/**
	 * Point and shoot
	 * @param delta: How long has it been since the lst engine tick if the player is holding fire. This is used for charge weapons
	 */
	public void shoot(float delta) {
		if (alive) {
			useToolStart(delta, playerData.getCurrentTool(), hitboxfilter, mouse.getPixelPosition(), true);
		}
	}
	
	/**
	 * Player releases mouse. This is used to fire charge weapons.
	 */
	public void release() {
		if (alive && shooting) {
			useToolRelease(playerData.getCurrentTool(), hitboxfilter, mouse.getPixelPosition());
		}
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
	
	private float armConnectXReal;
	private float headConnectXReal;
	private float armRotateXReal;
	private Vector2 mouseAngle = new Vector2();
	private boolean flip;
	@Override
	public void render(SpriteBatch batch) {
		
		//flip determines if the player is facing left or right
		flip = false;
		if (Math.abs(attackAngle) > 90) {
			flip = true;
		}
		
		//Depending on which way the player is facing, the connection points of various body parts are slightly offset.
		armConnectXReal = armConnectX;
		headConnectXReal = headConnectX;
		armRotateXReal = armRotateX;
		
		float realAttackAngle = attackAngle;
		if (flip) {
			armConnectXReal = bodyWidth - armWidth - armConnectX - 200;
			headConnectXReal = bodyWidth - headWidth - headConnectX - 200;
			armRotateXReal = armWidth - armRotateX;
			realAttackAngle += 180;
		}
		
		//This switch determines the total body y-offset to make the body bob up and down when running.
		int yOffset = 0;
		if (moveState.equals(MoveState.MOVE_LEFT) || moveState.equals(MoveState.MOVE_RIGHT)) {
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
				(flip ? -1 : 1) * toolWidth * scale, toolHeight * scale, 1, 1, realAttackAngle);
		
		batch.draw(bodyBackSprite, 
				(flip ? bodyBackWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2 + bodyConnectX * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + bodyConnectY + yOffset, 
				0, 0,
				(flip ? -1 : 1) * bodyBackWidth * scale, bodyBackHeight * scale, 1, 1, 0);
		
		batch.draw(armSprite, 
				(flip ? armWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2 + armConnectXReal * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + armConnectY * scale + yOffset, 
				(flip ? -armWidth * scale : 0) + armRotateXReal * scale, armRotateY * scale,
				(flip ? -1 : 1) * armWidth * scale, armHeight * scale, 1, 1, realAttackAngle);
		
		batch.draw(playerData.getActiveItem().isReady() ? gemSprite : gemInactiveSprite, 
				(flip ? gemWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2  + bodyConnectX * scale, 
				getPixelPosition().y - hbHeight * scale / 2 + bodyConnectY + yOffset, 
				0, 0,
				(flip ? -1 : 1) * gemWidth * scale, gemHeight * scale, 1, 1, 0);
		
		boolean reverse = false;

		if (moveState.equals(MoveState.MOVE_LEFT)) {
			
			if (Math.abs(realAttackAngle) > 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
				reverse = true;
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(animationTime), 
					(flip ? bodyWidth * scale : 0) + getPixelPosition().x - hbWidth * scale / 2  + bodyConnectX * scale, 
					getPixelPosition().y - hbHeight * scale / 2  + bodyConnectY + yOffset, 
					0, 0,
					(flip ? -1 : 1) * bodyWidth * scale, bodyHeight * scale, 1, 1, 0);
		} else if (moveState.equals(MoveState.MOVE_RIGHT)) {
			if (Math.abs(realAttackAngle) < 90) {
				bodyRunSprite.setPlayMode(PlayMode.LOOP_REVERSED);
				reverse = true;
			} else {
				bodyRunSprite.setPlayMode(PlayMode.LOOP);
			}
			
			batch.draw((TextureRegion) bodyRunSprite.getKeyFrame(animationTime), 
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
		
		float textX = getPixelPosition().x - reload.getRegionWidth() * uiScale / 2;
		float textY = getPixelPosition().y + reload.getRegionHeight() * uiScale + Player.hbHeight * scale / 2;
		
		//render player ui
		if (playerData.getCurrentTool().isReloading()) {
			
			//Calculate reload progress
			float percent = getReloadPercent();
			
			batch.draw(reloadBar, textX + 10, textY + 4, reloadBar.getRegionWidth() * uiScale * percent, reloadBar.getRegionHeight() * uiScale);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, "RELOADING", textX + 12, textY + reload.getRegionHeight() * uiScale);
			batch.draw(reloadMeter, textX, textY, reload.getRegionWidth() * uiScale, reload.getRegionHeight() * uiScale);
		}
		
		if (playerData.getCurrentTool().isCharging()) {
			
			//Calculate charge progress
			chargeDelayed = chargeDelayed + (chargePercent - chargeDelayed) * 0.1f;
			batch.draw(reloadBar, textX + 10, textY + 4, reloadBar.getRegionWidth() * uiScale * chargeDelayed, reloadBar.getRegionHeight() * uiScale);
			HadalGame.SYSTEM_FONT_SPRITE.draw(batch, playerData.getCurrentTool().getChargeText(), textX + 12, textY + reload.getRegionHeight() * uiScale);
			batch.draw(reloadMeter, textX, textY, reload.getRegionWidth() * uiScale, reload.getRegionHeight() * uiScale);
		} else {
			chargeDelayed = 0.0f;
		}
		
		//This draws a heart by the player's sprite to indicate hp remaining
		float heartX = getPixelPosition().x - Player.hbWidth * scale - empty.getWidth() * uiScale + 10;
		float heartY = getPixelPosition().y + Player.hbHeight * scale / 2 - 5;
		
		float hpRatio = 0.0f;
		
		//render "out of ammo"
		if (state.isServer()) {
			hpRatio = playerData.getCurrentHp() / playerData.getStat(Stats.MAX_HP);
			
			if (playerData.getCurrentTool().isOutofAmmo()) {
				HadalGame.SYSTEM_FONT_SPRITE.draw(batch, "OUT OF AMMO", textX + 12, textY + reload.getRegionHeight() * uiScale);
			}
		} else {
			hpRatio = playerData.getOverrideHpPercent();
			
			if (playerData.isOverrideOutOfAmmo()) {
				HadalGame.SYSTEM_FONT_SPRITE.draw(batch, "OUT OF AMMO", textX + 12, textY + reload.getRegionHeight() * uiScale);
			}
		}
		
		boolean visible = false;
		
		if (state.isServer()) {
			if (state.getPlayer().getPlayerData().getStat(Stats.HEALTH_VISIBILITY) > 0) {
				visible = true;
			}
		} else {
			if (((ClientState)state).getUiPlay().getHealthVisibility() > 0) {
				visible = true;
			}
		}
		
		if (visible || equals(state.getPlayer())) {
			batch.draw(empty, heartX - empty.getWidth() / 2 * uiScale, heartY - empty.getHeight() / 2 * uiScale,
	                empty.getWidth() / 2, empty.getHeight() / 2,
	                empty.getWidth(), empty.getHeight(),
	                uiScale, uiScale, 0, 0, 0, empty.getWidth(), empty.getHeight(), false, false);

	        batch.draw(full, heartX - full.getWidth() / 2 * uiScale, heartY - full.getHeight() / 2 * uiScale - (int)(full.getHeight() * (1 - hpRatio) * uiScale),
	                full.getWidth() / 2, full.getHeight() / 2,
	                full.getWidth(), full.getHeight(),
	                uiScale, uiScale, 0, 0, (int) (full.getHeight() * (1 - hpRatio)),
	                full.getWidth(), full.getHeight(), false, false);
		}
		
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
	private final static float gibGravity = 1.0f;
	public void createGibs() {
		if (alive) {
			new Ragdoll(state, getPixelPosition(), new Vector2(headWidth, headHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "head"), getLinearVelocity(), gibDuration, gibGravity, true, false, true);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(bodyWidth, bodyHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "body_stand"), getLinearVelocity(), gibDuration, gibGravity, true, false, true);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(armWidth, armHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "arm"), getLinearVelocity(), gibDuration, gibGravity, true, false, true);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(bodyBackWidth, bodyBackHeight).scl(scale), 
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "body_background"), getLinearVelocity(), gibDuration, gibGravity, true, false, true);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(gemWidth, gemHeight).scl(scale),
					Sprite.getCharacterSprites(playerData.getLoadout().character.getSprite(), "gem_active"), getLinearVelocity(), gibDuration, gibGravity, true, false, true);
			
			new Ragdoll(state, getPixelPosition(), new Vector2(toolWidth, toolHeight).scl(scale),
					playerData.getCurrentTool().getWeaponSprite(), getLinearVelocity(), gibDuration, gibGravity, true, false, true);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();

		//this is here to prevent the client from not updating the last, fatal instance of damage in the ui
		playerData.setOverrideHpPercent(0);
	}
	
	/**
	 * This is called by the server when the player is created. Sends a packet to clients to instruct them to build a new player
	 * with the desired name and loadout
	 */
	@Override
	public Object onServerCreate() {
		return new Packets.CreatePlayer(entityID.toString(), getPixelPosition(), name, playerData.getLoadout());
	}
	
	/**
	 * This is called every engine tick. 
	 * The server player sends one packet to the corresponding client player and one to all players.
	 * The unique packet contains info only needed for that client's ui (fuel, clip, ammo and active itemcharge percent.)
	 * The universal packet contains location, arm angle, hp, weapon, groundedness, reload/charge/outofammo notifications
	 */
	@Override
	public void onServerSync() {
		
		super.onServerSync();

		HadalGame.server.sendToAllUDP(new Packets.SyncPlayerAll(entityID.toString(), mouseAngle, grounded, playerData.getCurrentSlot(), 
				playerData.getCurrentTool().isReloading(), reloadPercent, playerData.getCurrentTool().isCharging(), chargePercent, playerData.getCurrentTool().isOutofAmmo(), getMainFixture().getFilterData().maskBits));
		
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

			serverAttackAngle.setAngleRad(p.attackAngle.angleRad());
			grounded = p.grounded;
			getPlayerData().setCurrentSlot(p.currentSlot);
			getPlayerData().setCurrentTool(getPlayerData().getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrame());
			getPlayerData().getCurrentTool().setReloading(p.reloading);
			reloadPercent = p.reloadPercent;
			getPlayerData().getCurrentTool().setCharging(p.charging);
			chargePercent = p.chargePercent;
			getPlayerData().setOverrideOutOfAmmo(p.outOfAmmo);
			
			if (p.maskBits != getMainFixture().getFilterData().maskBits) {
				Filter filter = getMainFixture().getFilterData();
				filter.maskBits = p.maskBits;
				getMainFixture().setFilterData(filter);
			}
			
		} else {
			super.onClientSync(o);
		}
	}
	
	private Vector2 serverAttackAngle = new Vector2(0, 1);
	@Override
	public void clientController(float delta) {
		super.clientController(delta);

		mouseAngle.setAngleRad(mouseAngle.angleRad()).lerp(serverAttackAngle, 1 / 2f).angleRad();
		attackAngle = (float)(Math.atan2(mouseAngle.x, mouseAngle.y) * 180 / Math.PI);
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
		
		//The -1 here is just to deal with some weird physics stuff that made this act differently on different maps for some reason.
		return originPt.add(offset.nor().scl((spawnDist + projSize / 4 / PPM) * shortestFraction - 1)).scl(PPM);
	}
	
	/**
	 * This makes the player's legs flail more when in the air
	 */
	@Override
	public void increaseAnimationTime(float i) { 
		if (grounded) {
			animationTime += i; 
		} else {
			animationTime += i * airAnimationSlow; 
		}
	}
	
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

	public void setChargePercent(float chargePercent) { this.chargePercent = chargePercent; }

	public ActionController getController() { return controller; }
	
	public Loadout getStartLoadout() { return startLoadout; }

	public void setStartLoadout(Loadout startLoadout) { this.startLoadout = startLoadout; }

	public MouseTracker getMouse() { return mouse; }

	public void setMouse(MouseTracker mouse) { this.mouse = mouse; }

	public int getConnID() { return connID;	}
	
	public void setScaling(boolean scaling) { this.scaling = scaling; }
	
	public void setInvisible(boolean invisible) { this.invisible = invisible; }

	public StartPoint getStart() { return start; }
}
