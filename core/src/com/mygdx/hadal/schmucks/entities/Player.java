package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.PlayerSpriteHelper;
import com.mygdx.hadal.effects.PlayerSpriteHelper.DespawnType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem.chargeStyle;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.misc.Airblaster;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.ActionController;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.FeetData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.text.HText;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.WorldUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import java.util.Objects;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 * The player is the entity that the player controls.
 * @author Ningerbread Nicorice
 */
public class Player extends PhysicsSchmuck {
	
	private static final int baseHp = 100;
	private static final float playerDensity = 1.0f;
	public static final float controllerInterval = 1 / 60f;
	
	//Dimension of player sprite parts.
	public static final int hbWidth = 216;
	public static final int hbHeight = 516;

	public static final float scale = 0.15f;
	public static final float uiScale = 0.4f;
	public static final float playerMass = 2.4489846f;
	private float scaleModifier = 0.0f;
	private float gravityModifier = 1.0f;
	private float restitutionModifier = 0.0f;
	private boolean dontMoveCamera;

	private final PlayerSpriteHelper spriteHelper;
	private TextureRegion toolSprite;
	private final Animation<TextureRegion> typingBubble;
	private final TextureRegion reloadMeter, reloadBar, hpBar, hpBarFade, fuelBar, fuelCutoff;

	//Fixtures and user data
	protected FeetData feetData;
	private FeetData rightData;
	private FeetData leftData;
	
	//These track whether the schmuck has a specific artifacts equipped (to enable wall scaling.)
	//invisibility and blinded are kept track of this way too b/c they effect visuals
	private boolean scaling;
	protected int invisible;
	private float blinded;
	
	//does the player have a shoot/jump or boost action buffered? (i.e used when still on cd)
	protected boolean shootBuffered, jumpBuffered, airblastBuffered;

	//counters for various cooldowns.
	protected static final float hoverCd = 0.08f;
	protected static final float jumpCd = 0.25f;
	protected float jumpCdCount;
	
	protected static final float fastFallCd = 0.05f;
	protected float fastFallCdCount;
	
	protected static final float airblastCd = 0.25f;
	protected float airblastCdCount;

	protected static final float interactCd = 0.15f;
	protected float interactCdCount;
	
	protected static final float hitSoundCd = 0.15f;
	protected float hitSoundCdCount, hitSoundLargeCdCount;
	
	protected static final float pingCd = 1.0f;
	protected float pingCdCount;

	private static final float hoverFuelRegenCd = 1.0f;
	private static final float airblastFuelRegenCd = 2.0f;
	private float fuelRegenCdCount;
	private static final float fuelRegen = 20.0f;

	//this makes the player animate faster in the air for the "luigi legs"
	private static final float airAnimationSlow = 3.0f;

	//This is the angle that the player's arm is pointing
	protected float attackAngle;
	
	//user data
	protected PlayerBodyData playerData;
	
	//The event that the player last collided with. Used for active events that the player interacts with by pressing 'E'
	private Event currentEvent;
	
	//Equipment that the player has built into their toolset.
	private final Airblaster airblast;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	protected float controllerCount;
	
	//Is the player currently shooting/hovering/fastfalling?
	private boolean shooting;
	protected boolean hoveringAttempt;
	protected boolean fastFalling;
	
	//This is the percent of reload completed, if reloading. This is used to display the reload ui for all players.
	protected float reloadPercent, reloadDelayed;
	
	//This is the percent of charge completed, if charging. This is used to display the charge ui for all players.
	protected float chargePercent, chargeDelayed;
	
	//particles and sounds used by the player
	protected ParticleEntity hoverBubbles, dustCloud;
	private SoundEntity runSound, hoverSound, reloadSound;
	
	//This is the controller that causes this player to perform actions
	private ActionController controller;
	
	//this exists so that player can aim towards the mouse.
	private MouseTracker mouse;
	
	//This is the loadout that this player starts with.
	private final Loadout startLoadout;
	
	//This is the connection id and user of the player (0 if server)
	private int connId;
	private User user;
	
	//should we reset this player's playerData stuff upon creation
	private final boolean reset;
	
	//this is the point we are starting at.
	private Event start;

	//is the player currently typing in chat? (yes if this float is greater that 0.0f)
	protected float typingCdCount;
	
	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param startPos: the player's starting location
	 * @param name: the player's name
	 * @param startLoadout: This is the player's starting loadout
	 * @param oldData: If created after a stage transition, this is the data of the previous player.
	 * @param connId: connection id. 0 if server.
	 * @param reset: do we reset the player's stats after creating them?
	 * @param start: the start point that the player spawns at.
	 */
	public Player(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connId,
				  User user, boolean reset, Event start) {
		super(state, startPos, new Vector2(hbWidth * scale,hbHeight * scale), name, Constants.PLAYER_HITBOX, baseHp);
		this.name = name;
		airblast = new Airblaster(this);
		toolSprite = Sprite.MT_DEFAULT.getFrame();

		this.moveState = MoveState.STAND;

		this.startLoadout = startLoadout;
		this.playerData = oldData;
		this.connId = connId;
		this.user = user;
		this.reset = reset;
		this.start = start;

		this.spriteHelper = new PlayerSpriteHelper(this, scale);
		setBodySprite(startLoadout.character, startLoadout.team);
		loadParticles();
		
		//This schmuck tracks mouse location. Used for projectiles that home towards mouse.
		mouse = state.getMouse();
		
		this.reloadMeter = Sprite.UI_RELOAD_METER.getFrame();
		this.reloadBar = Sprite.UI_RELOAD_BAR.getFrame();
		this.hpBar = Sprite.UI_MAIN_HEALTHBAR.getFrame();
		this.hpBarFade = Sprite.UI_MAIN_HEALTH_MISSING.getFrame();
		this.fuelBar = Sprite.UI_MAIN_FUELBAR.getFrame();
		this.fuelCutoff = Sprite.UI_MAIN_FUEL_CUTOFF.getFrame();
		this.typingBubble =  new Animation<>(PlayState.spriteAnimationSpeedSlow,
			Objects.requireNonNull(Sprite.NOTIFICATIONS_CHAT.getFrames()));
		typingBubble.setPlayMode(PlayMode.LOOP_PINGPONG);
	}
	
	/**
	 * This method prepares the player sprite from various texture regions.
	 * @param character: the character whose sprite we are switching to.
	 */
	public void setBodySprite(UnlockCharacter character, AlignmentFilter team) {

		spriteHelper.setBodySprite(state.getBatch(), character, team);
		
		//This line is used when the player swaps skins in loadout screen. It ensures the tool sprite is properly aligned.
		if (playerData != null) {
			playerData.setEquip();
		}
	}
	
	/**
	 * This method prepares the various particle emitting entities attached to the player.
	 */
	public void loadParticles() {
		if (state.isServer()) {
			dustCloud = new ParticleEntity(state, this, Particle.DUST, 1.0f, 0.0f, false,
					SyncType.TICKSYNC);
			hoverBubbles = new ParticleEntity(state, this, Particle.BUBBLE_TRAIL, 1.0f, 0.0f, false,
					SyncType.TICKSYNC);
		}
	}
	
	/**
	 * Create the player's body and initialize player's user data.
	 */
	@Override
	public void create() {
		alive = true;
		destroyed = false;

		//create the player's input controller
		controller = new ActionController(this);
		
		//this line syncs the player's inputs so that holding a button will keep that action held after map transitions
		if (this == state.getPlayer()) {
			state.resetController();
		}
		
		//this makes the player's selected slot persist after respawning
		int currentSlot = 1;
		if (playerData != null) {
			currentSlot = playerData.getCurrentSlot() + 1;
		}
		
		//If resetting, this indicates that this is a newly spawned or respawned player. Create new data for it with the provided loadout.
		//Otherwise, take the input data and reset it to match the new world.
		//Null is just for safety. Not needed b/c playerData should only be null when resetting,
		//(no existing player or joining from specatator)
		if (reset || playerData == null) {
			playerData = new PlayerBodyData(this, startLoadout);
		}

		//If the player is spawning into a new level, initialize loadout and give brief invulnerability.
		if (reset) {
			playerData.initLoadout();
			playerData.addStatus(new Invulnerability(state, 2.0f, playerData, playerData));
		} else {
			playerData.updateOldData(this);
		}
		playerData.switchWeapon(currentSlot);

		//Activate on-spawn effects
		if (reset) {
			playerData.statusProcTime(new ProcTime.PlayerCreate());
		}

		//we scale size here to account for any player size modifiers
		size.scl(1.0f + scaleModifier);

		//for server, we adjust offset of particles to account for size changes
		if (dustCloud != null && hoverBubbles != null) {
			dustCloud.setOffset(0, -size.y / 2);
			hoverBubbles.setOffset(0, -size.y / 2);
		}

		this.body = BodyBuilder.createBox(world, startPos, size, gravityModifier, playerDensity, restitutionModifier, 0.0f, false, true, Constants.BIT_PLAYER,
				(short) (Constants.BIT_PLAYER | Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxfilter, false, playerData);

		//create several extra fixtures to keep track of feet/sides to determine when the player gets their jump back and what terrain event they are standing on.
		this.feetData = new FeetData(UserDataType.FEET, this);

		Fixture feet = FixtureBuilder.createFixtureDef(body, new Vector2(0.5f, - size.y / 2), new Vector2(size.x - 2, size.y / 8), true, 0, 0, 0, 0,
				Constants.BIT_SENSOR, (short)(Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_DROPTHROUGHWALL), hitboxfilter);

		feet.setUserData(feetData);

		this.leftData = new FeetData(UserDataType.FEET, this);

		Fixture leftSensor = FixtureBuilder.createFixtureDef(body, new Vector2(-size.x / 2, 0.5f), new Vector2(size.x / 8, size.y - 2), true, 0, 0, 0, 0,
				Constants.BIT_SENSOR, Constants.BIT_WALL, hitboxfilter);

		leftSensor.setUserData(leftData);

		this.rightData = new FeetData(UserDataType.FEET, this);

		Fixture rightSensor = FixtureBuilder.createFixtureDef(body, new Vector2(size.x / 2,  0.5f), new Vector2(size.x / 8, size.y - 2), true, 0, 0, 0, 0,
				Constants.BIT_SENSOR, Constants.BIT_WALL, hitboxfilter);

		rightSensor.setUserData(rightData);

		//make the player's mass constant to avoid mass changing when player is a different size
		MassData newMass = body.getMassData();
		newMass.mass = playerMass;
		body.setMassData(newMass);

		//if this is the client creating their own player, tell the server we are ready to sync player-related stuff
		if (!state.isServer() && this.equals(state.getPlayer())) {
			Packets.ClientPlayerCreated connected = new Packets.ClientPlayerCreated();
            HadalGame.client.sendTCP(connected);
		}
		
		//activate start point events (these usually just set up camera bounds/zoom and stuff like that)
		//This line is here so that it does not occur before events are done being created.
		//We only do this for our own player. For clients, this is run when they send a player created packet
		if (this.equals(state.getPlayer())) {
			activateStartingEvents();
		}
	}

	public void activateStartingEvents() {
		if (start != null) {
			if (start.getConnectedEvent() != null) {
				start.getConnectedEvent().getEventData().preActivate(start.getEventData(), this);
			}
		}
	}

	/**
	 * The player's controller currently polls for input.
	 */
	private final Vector2 playerLocation = new Vector2();
	private final Vector2 mouseLocation = new Vector2();
	@Override
	public void controller(float delta) {
		controllerCount += delta;
		playerLocation.set(getPixelPosition());

		//This line ensures that this runs every 1/60 second regardless of computer speed.
		while (controllerCount >= controllerInterval) {
			controllerCount -= controllerInterval;

			//if the player is successfully hovering, run hover(). We check if hover is successful so that effects that run when hovering do not activate when not actually hovering (white smoker)
			if (hoveringAttempt && playerData.getExtraJumpsUsed() >= playerData.getExtraJumps() &&	playerData.getCurrentFuel() >= playerData.getHoverCost()) {
				if (jumpCdCount < 0) {
					hover();
				}
			} else {
				//turn off hover particles and sound
				hoverBubbles.turnOff();

				if (hoverSound != null) {
					hoverSound.turnOff();
				}
			}
			if (fastFalling) {
				fastFall();
			}
			
			if ((moveState.equals(MoveState.MOVE_LEFT) || moveState.equals(MoveState.MOVE_RIGHT)) && grounded && invisible == 0) {
				
				//turn on running particles and sound
				dustCloud.turnOn();
				if (runSound == null) {
					runSound = new SoundEntity(state, this, SoundEffect.RUN, 0.0f, 0.1f, 1.0f,
							true, true, SyncType.TICKSYNC);
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
				
		//process fuel regen. Base fuel regen is canceled upon using fuel.
		if (fuelRegenCdCount > 0.0f) {
			fuelRegenCdCount -= delta;
		} else {
			playerData.fuelGain(fuelRegen * delta);
		}
		playerData.fuelGain(playerData.getStat(Stats.FUEL_REGEN) * delta);

		//If player is reloading, run the reload method of the current equipment.
		if (playerData.getCurrentTool().isReloading()) {
			playerData.getCurrentTool().reload(delta);
			
			//turn on reloading particles and sound
			if (reloadSound == null) {
				reloadSound = new SoundEntity(state, this, SoundEffect.RELOAD, 0.0f, 0.2f, 1.0f,
						true, true, SyncType.TICKSYNC);
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
		jumpCdCount -= delta;
		fastFallCdCount -= delta;
		airblastCdCount -= delta;
		interactCdCount -= delta;
		pingCdCount -= delta;
		hitSoundCdCount -= delta;
		hitSoundLargeCdCount -= delta;
		typingCdCount -= delta;
		
		//if inputting certain actions during cooldown, an action is buffered
		if (jumpBuffered && jumpCdCount < 0) {
			jumpBuffered = false;
			jump();
		}
		if (airblastBuffered && airblastCdCount < 0) {
			airblastBuffered = false;
			airblast();
		}
		if (shootBuffered && shootCdCount < 0) {
			shootBuffered = false;
			shoot(delta);
		}
		
		//Determine player mouse location and hence where the arm should be angled.
		if (mouse.getBody() != null) {
			playerLocation.set(getPixelPosition());
			mouseLocation.set(mouse.getPixelPosition());
			mouseAngle.set(playerLocation.x, playerLocation.y).sub(mouseLocation.x, mouseLocation.y);
		}
		attackAngle = MathUtils.atan2(mouseAngle.y, mouseAngle.x) * MathUtils.radDeg;
		
		//process weapon update (this is for weapons that have an effect that activates over time which is pretty rare)
		playerData.getCurrentTool().update(state, delta);

		//process list of units that damaged this player within the last ~5 seconds
		playerData.processRecentDamagedBy(delta);
		
		super.controller(delta);
	}

	private final Vector2 hoverDirection = new Vector2();
	/**
	 * Player's Hover power. Costs fuel and continuously pushes the player upwards.
	 */
	public void hover() {
		if (jumpCdCount < 0) {

			//hovering sets fuel regen on cooldown
			if (fuelRegenCdCount < hoverFuelRegenCd) {
				fuelRegenCdCount = hoverFuelRegenCd;
			}

			//Player will continuously do small upwards bursts that cost fuel.
			playerData.fuelSpend(playerData.getHoverCost());
			jumpCdCount = hoverCd;

			hoverDirection.set(0, playerData.getHoverPower());

			if (playerData.getStat(Stats.HOVER_CONTROL) > 0) {
				hoverDirection.setAngleDeg(attackAngle + 180);
			}

			pushMomentumMitigation(hoverDirection.x, hoverDirection.y);

			playerData.statusProcTime(new ProcTime.whileHover(hoverDirection));

			if (invisible == 0) {
				//turn on hovering particles and sound
				hoverBubbles.turnOn();
				if (hoverSound == null) {
					hoverSound = new SoundEntity(state, this, SoundEffect.HOVER, 0.0f, 0.2f, 1.0f,
							true, true, SyncType.TICKSYNC);
				}
				hoverSound.turnOn();
			}
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
				
				if (invisible == 0) {
					//activate jump particles and sound
					new ParticleEntity(state, new Vector2(getPixelPosition().x, getPixelPosition().y - size.y / 2),
							Particle.WATER_BURST, 1.0f, true, SyncType.CREATESYNC);
					SoundEffect.JUMP.playUniversal(state, getPixelPosition(), 0.2f, false);
				}
			} else {
				jumpBuffered = true;
			}
		} else {
			if (playerData.getExtraJumpsUsed() < playerData.getExtraJumps()) {
				if (jumpCdCount < 0) {
					jumpCdCount = jumpCd;
					playerData.setExtraJumpsUsed(playerData.getExtraJumpsUsed() + 1);
					pushMomentumMitigation(0, playerData.getJumpPower());
					
					if (invisible == 0) {
						//activate double-jump particles and sound
						new ParticleEntity(state, this, Particle.SPLASH, 0.0f, 0.75f, true, SyncType.CREATESYNC);
						SoundEffect.DOUBLEJUMP.playUniversal(state, getPixelPosition(), 0.2f, false);
					}
				} else {
					jumpBuffered = true;
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
				push(0, -1, playerData.getFastFallPower());
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
	 * This is called when the player clicks the fire button. It is used to buffer fire inputs during weapon cooldowns
	 */
	public void startShooting() {
		shooting = true;
		if (shootCdCount >= 0) {
			shootBuffered = true;
		}
	}
	
	/**
	 * Player releases mouse. This is used to fire charge weapons.
	 */
	public void release() {
		if (alive && shooting) {
			useToolRelease(playerData.getCurrentTool());
		}
	}
	
	/**
	 * Player's airblast power. Boosts player, knocks enemies/hitboxes.
	 */
	public void airblast() {
		if (airblastCdCount < 0) {
			if (playerData.getCurrentFuel() >= playerData.getAirblastCost()) {

				//airblasting sets fuel regen on cooldown
				if (fuelRegenCdCount < airblastFuelRegenCd) {
					fuelRegenCdCount = airblastFuelRegenCd;
				}

				playerData.fuelSpend(playerData.getAirblastCost());
				airblastCdCount = airblastCd;
				useToolStart(0, airblast, hitboxfilter, mouse.getPixelPosition(), false);
			}
		} else {
			airblastBuffered = true;
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
		playerData.getActiveItem().mouseClicked(0, state, getBodyData(), hitboxfilter, mouse.getPixelPosition());
		playerData.getActiveItem().execute(state, getBodyData());
	}
	
	/**
	 * Player begins reloading.
	 */
	public void reload() {
		playerData.getCurrentTool().setReloading(true, false);
	}
	
	/**
	 * Player pings at mouse location
	 */
	private static final Vector2 notifOffset = new Vector2(0, 35);
	public void ping() {
		if (pingCdCount < 0) {
			pingCdCount = pingCd;
			SyncedAttack.PING.initiateSyncedAttackSingle(state, this, mouse.getPixelPosition().add(notifOffset), new Vector2());
		}
	}
	
	private static final int barX = 20;
	private static final int barY = 0;
	private static final int hpWidth = 5;
	private static final int hpHeight = 40;
	private static final int flipRange = 80;
	private static final int cutoffThickness = 3;
	private boolean barRight;
	protected final Vector2 mouseAngle = new Vector2();
	@Override
	public void render(SpriteBatch batch) {

		//process player invisibility. Completely invisible players are partially transparent to allies
		float transparency;
		boolean batchSet = false;
		if (invisible == 2) {
			if (state.getPlayer().hitboxfilter == hitboxfilter) {
				transparency = 0.3f;
				batch.setColor(1.0f,  1.0f, 1.0f, transparency);
				batchSet = true;
			} else {
				return;
			}
		}
		if (invisible == 1) {
			if (state.getPlayer().hitboxfilter != hitboxfilter) {
				transparency = 0.3f;
				batch.setColor(1.0f,  1.0f, 1.0f, transparency);
				batchSet = true;
			}
		}

		//render player sprite using sprite helper
		spriteHelper.render(batch, attackAngle, moveState, animationTime, animationTimeExtra, grounded, playerLocation);

		if (batchSet) {
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
		
		float textX = playerLocation.x - reloadMeter.getRegionWidth() * uiScale / 2;
		float textY = playerLocation.y + reloadMeter.getRegionHeight() * uiScale + size.y / 2;
		
		//render player ui
		if (playerData.getCurrentTool().isReloading()) {
			
			//Calculate reload progress
			reloadDelayed = Math.min(1.0f, reloadDelayed + (reloadPercent - reloadDelayed) * 0.25f);
			
			batch.draw(reloadBar, textX + 10, textY + 4, reloadBar.getRegionWidth() * uiScale * reloadDelayed, reloadBar.getRegionHeight() * uiScale);
			HadalGame.FONT_SPRITE.draw(batch, HText.RELOADING.text(), textX + 12, textY + reloadMeter.getRegionHeight() * uiScale);
			batch.draw(reloadMeter, textX, textY, reloadMeter.getRegionWidth() * uiScale, reloadMeter.getRegionHeight() * uiScale);
			
			if (reloadDelayed > reloadPercent) {
				reloadDelayed = 0.0f;
			}
		} else {
			reloadDelayed = 0.0f;
		}
		
		if (playerData.getCurrentTool().isCharging()) {
			
			//Calculate charge progress
			chargeDelayed = Math.min(1.0f, chargeDelayed + (chargePercent - chargeDelayed) * 0.25f);
			batch.draw(reloadBar, textX + 10, textY + 4, reloadBar.getRegionWidth() * uiScale * chargeDelayed, reloadBar.getRegionHeight() * uiScale);
			HadalGame.FONT_SPRITE.draw(batch, playerData.getCurrentTool().getChargeText(), textX + 12, textY + reloadMeter.getRegionHeight() * uiScale);
			batch.draw(reloadMeter, textX, textY, reloadMeter.getRegionWidth() * uiScale, reloadMeter.getRegionHeight() * uiScale);
		} else {
			chargeDelayed = 0.0f;
		}
		
		//render "out of ammo"
		if (state.isServer()) {
			if (playerData.getCurrentTool().isOutofAmmo()) {
				HadalGame.FONT_SPRITE.draw(batch, HText.OUT_OF_AMMO.text(), textX + 12, textY + reloadMeter.getRegionHeight() * uiScale);
			}
		} else {
			if (playerData.isOverrideOutOfAmmo()) {
				HadalGame.FONT_SPRITE.draw(batch, HText.OUT_OF_AMMO.text(), textX + 12, textY + reloadMeter.getRegionHeight() * uiScale);
			}
		}
		
		boolean visible = false;
		
		//draw hp and fuel bar if using certain effects, looking at self/ally, or in spectator mode
		if (state.isSpectatorMode() || hitboxfilter == state.getPlayer().hitboxfilter) {
			visible = true;
		} else {
			if (state.getPlayer().getPlayerData() != null) {
				if (state.getPlayer().getPlayerData().getStat(Stats.HEALTH_VISIBILITY) > 0) {
					visible = true;
				}
			}
		}

		float hpX, hpRatio, fuelRatio, fuelCutoffRatio;
		if (visible) {
			if (barRight) {
				hpX = playerLocation.x + barX;
				if (attackAngle > 180 - flipRange || attackAngle < -180 + flipRange) {
					barRight = false;
				}
			} else {
				hpX = playerLocation.x - barX - hpWidth - 5;
				if (attackAngle < flipRange && attackAngle > - flipRange) {
					barRight = true;
				}
			}

			if (equals(state.getPlayer())) {
				if (state.getGsm().getSetting().isDisplayHp()) {
					hpRatio = state.getUiPlay().getHpRatio();
					fuelRatio = state.getUiPlay().getFuelRatio();
					fuelCutoffRatio = state.getUiPlay().getFuelCutoffRatio();
					if (barRight) {
						batch.draw(fuelBar, hpX, playerLocation.y + barY, hpWidth, hpHeight * fuelRatio);
						batch.draw(hpBarFade, hpX + hpWidth, playerLocation.y + barY, hpWidth, hpHeight);
						batch.draw(hpBar, hpX + hpWidth, playerLocation.y + barY, hpWidth, hpHeight * hpRatio);
						batch.draw(fuelCutoff, hpX, playerLocation.y + barY + fuelCutoffRatio * hpHeight, hpWidth, cutoffThickness);
					} else {
						batch.draw(fuelBar, hpX - hpWidth, playerLocation.y + barY, hpWidth, hpHeight * fuelRatio);
						batch.draw(hpBarFade, hpX, playerLocation.y + barY, hpWidth, hpHeight);
						batch.draw(hpBar, hpX, playerLocation.y + barY, hpWidth, hpHeight * hpRatio);
						batch.draw(fuelCutoff, hpX - hpWidth, playerLocation.y + barY + fuelCutoffRatio * hpHeight, hpWidth, cutoffThickness);
					}
				}
			} else {
				hpRatio = playerData.getCurrentHp() / playerData.getStat(Stats.MAX_HP);
				batch.draw(hpBarFade, hpX, playerLocation.y + barY, hpWidth, hpHeight);
				batch.draw(hpBar, hpX, playerLocation.y + barY, hpWidth, hpHeight * hpRatio);
			}
		}

		if (state.getGsm().getSetting().isDisplayNames()) {
			//draw player name
			HadalGame.FONT_SPRITE.draw(batch, name,
				playerLocation.x - size.x / 2,
				playerLocation.y + size.y / 2 + 25);
		}

		//display typing bubble if typing
		if (typingCdCount > 0) {
			batch.draw(typingBubble.getKeyFrame(animationTime, true), playerLocation.x - 25, playerLocation.y + size.y / 2 + 20, 50, 40);
		}
	}
	
	/**
	 * When the player is in the air, their animation freezes. This gets the frame for that
	 * @param reverse: which direction is the player facing
	 * @return the integer frame number that should be displayed given the player's movement status
	 */
	public int getFreezeFrame(boolean reverse) {
		if (Math.abs(getLinearVelocity().x) > Math.abs(getLinearVelocity().y)) {
			return reverse ? 5 : 2;
		} else {
			return reverse ? 1 : 6;
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		spriteHelper.dispose(despawnType);

		//this is here to prevent the client from not updating the last, fatal instance of damage in the ui
		if (!state.isServer()) {
			playerData.setCurrentHp(0);
		}
	}
	
	/**
	 * When the player deals damage, we play this hitsound depending on the amount of damage dealt
	 */
	private static final float maxDamageThreshold = 60.0f;
	public void playHitSound(float damage) {
		
		if (damage <= 0.0f) { return; }
		
		if (damage > maxDamageThreshold) {
			if (hitSoundLargeCdCount < 0) {
				hitSoundLargeCdCount = hitSoundCd;
				hitSoundCdCount = hitSoundCd;
				SoundEffect.registerHitSound(state.getGsm(), this, true);
			}
		} else {
			if (hitSoundCdCount < 0) {
				hitSoundCdCount = hitSoundCd;
				SoundEffect.registerHitSound(state.getGsm(), this, false);
			}
		}
	}

	/**
	 * This is called by the server when the player is created. Sends a packet to clients to instruct them to build a new player
	 * with the desired name and loadout
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreatePlayer(entityID, connId, getPixelPosition(), name, playerData.getLoadout(),
				hitboxfilter, scaleModifier, dontMoveCamera);
	}

	//this is the type of death we have. Send to client so they can process the death on their end.
	private DespawnType despawnType = DespawnType.LEVEL_TRANSITION;
	@Override
	public Object onServerDelete() { return new Packets.DeletePlayer(entityID, state.getTimer(), despawnType); }

	/**
	 * This is called every engine tick. 
	 * The server player sends one packet to the corresponding client player and one to all players.
	 * The unique packet contains info only needed for that client's ui (fuel, clip, ammo and active item charge percent.)
	 * The universal packet contains location, arm angle, hp, weapon, groundedness, reload/charge/out of ammo notifications
	 */
	@Override
	public void onServerSync() {
		HadalGame.server.sendToAllExceptUDP(connId, new PacketsSync.SyncPlayer(entityID, getPosition(), getLinearVelocity(),
				entityAge,	state.getTimer(), moveState, getBodyData().getCurrentHp(),
				mouseAngle, grounded, playerData.getCurrentSlot(), playerData.getCurrentTool().isReloading(), reloadPercent,
				playerData.getCurrentTool().isCharging(), chargePercent, playerData.getCurrentTool().isOutofAmmo(),
				getMainFixture().getFilterData().maskBits, invisible));

		HadalGame.server.sendToUDP(connId, new PacketsSync.SyncPlayerSelf(entityID, getPosition(),
				getLinearVelocity(), entityAge,	state.getTimer(), moveState, getBodyData().getCurrentHp(),
				mouseAngle, grounded, playerData.getCurrentSlot(),
				playerData.getCurrentTool().isReloading(), reloadPercent,
				playerData.getCurrentTool().isCharging(), chargePercent, playerData.getCurrentTool().isOutofAmmo(),
				getMainFixture().getFilterData().maskBits, invisible, playerData.getCurrentFuel(),
				playerData.getCurrentTool().getClipLeft(), playerData.getCurrentTool().getAmmoLeft(),
				playerData.getActiveItem().chargePercent(), blinded));
	}
	
	/**
	 * The client Player receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {

		//this processes screen shake for the client when their player (or spectator target) receives damage
		if (o instanceof PacketsSync.SyncSchmuck p) {
			float difference = getPlayerData().getCurrentHp() - p.currentHp;
			if (difference > 0.0f && state.getUiPlay() != null) {
				if (this.equals(state.getPlayer())) {
					CameraUtil.inflictTrauma(state.getGsm(), difference);
				}
				if (state.getKillFeed() != null) {
					if (state.isSpectatorMode() || state.getKillFeed().isRespawnSpectator()) {
						if (this.equals(state.getUiSpectator().getSpectatorTarget())) {
							CameraUtil.inflictTrauma(state.getGsm(), difference);
						}
					}
				}
			}
		}
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncPlayer p) {
			serverAttackAngle.setAngleRad(p.attackAngle.angleRad());
			grounded = p.grounded;
			getPlayerData().setCurrentSlot(p.currentSlot);
			getPlayerData().setCurrentTool(getPlayerData().getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrame());
			getPlayerData().getCurrentTool().setReloading(p.reloading, true);
			reloadPercent = p.reloadPercent;
			getPlayerData().getCurrentTool().setCharging(p.charging);
			chargePercent = p.chargePercent;
			getPlayerData().setOverrideOutOfAmmo(p.outOfAmmo);
			invisible = p.invisible;

			//client's own player does not sync dropthrough passability
			if (!(this instanceof PlayerClient) && p.maskBits != getMainFixture().getFilterData().maskBits) {
				Filter filter = getMainFixture().getFilterData();
				filter.maskBits = p.maskBits;
				getMainFixture().setFilterData(filter);
			}
		} else if (o instanceof Packets.DeletePlayer p) {

			//delegate to sprite helper for despawn so it can dispose of frame buffer object
			spriteHelper.despawn(p.type, getPixelPosition(), getLinearVelocity());
			setDespawnType(p.type);
			((ClientState) state).removeEntity(entityID);
		}
	}
	
	private final Vector2 serverAttackAngle = new Vector2(0, 1);
	@Override
	public void clientController(float delta) {
		playerLocation.set(getPixelPosition());

		super.clientController(delta);
		//client mouse lerps towards the angle sent by server
		mouseAngle.setAngleRad(mouseAngle.angleRad()).lerp(serverAttackAngle, 1 / 2f).angleRad();
		attackAngle = MathUtils.atan2(mouseAngle.y, mouseAngle.x) * MathUtils.radDeg;
		typingCdCount -= delta;
	}
	
	private float shortestFraction;
	private final Vector2 originPt = new Vector2();
	private final Vector2 endPt = new Vector2();
	private final Vector2 offset = new Vector2();
	private final Vector2 playerWorldLocation = new Vector2();
	/**
	 * This method makes projectiles fired by the player spawn offset to be at the tip of the gun
	 */
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		playerWorldLocation.set(getPosition());
		originPt.set(playerWorldLocation);
		offset.set(startVelo);
		endPt.set(playerWorldLocation).add(offset.nor().scl((size.x * 2 + projSize / 4) / PPM));
		shortestFraction = 1.0f;
		
		//raycast towards the direction firing. spawn projectile closer to player if a wall is nearby
		if (WorldUtil.preRaycastCheck(originPt, endPt)) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {

				if (fixture.getFilterData().categoryBits == Constants.BIT_WALL && fraction < shortestFraction) {
					shortestFraction = fraction;
					return fraction;
				}
				return -1.0f;
			}, originPt, endPt);
		}
		
		//The -1 here is just to deal with some weird physics stuff that made this act differently on different maps for some reason.
		return originPt.add(offset.nor().scl(((size.x * 2 + projSize / 4) / PPM) * shortestFraction - 1)).scl(PPM);
	}

	/**
	 * This makes the player's legs flail more when in the air
	 */
	@Override
	public void increaseAnimationTime(float i) { 
		animationTimeExtra += i;
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

	public void setScaleModifier(float scaleModifier) {
		this.spriteHelper.setScale(scale * (1.0f + scaleModifier));
		this.scaleModifier = scaleModifier;
	}

	public void setGravityModifier(float gravityModifier) { this.gravityModifier = gravityModifier; }

	public void setRestitutionModifier(float restitutionModifier) { this.restitutionModifier = restitutionModifier; }

	public PlayerBodyData getPlayerData() {	return playerData; }

	public FeetData getFeetData() { return feetData; }

	public TextureRegion getToolSprite() { return this.toolSprite; }

	public void setToolSprite(TextureRegion sprite) { toolSprite = sprite; }

	public Event getCurrentEvent() { return currentEvent; }

	public float getAttackAngle() { return attackAngle; }

	public void setCurrentEvent(Event currentEvent) { this.currentEvent = currentEvent; }

	public void setHoveringAttempt(boolean hoveringAttempt) { this.hoveringAttempt = hoveringAttempt; }

	public boolean isFastFalling() { return fastFalling; }

	public void setFastFalling(boolean fastFalling) { this.fastFalling = fastFalling; }

	public void setShooting(boolean shooting) { this.shooting = shooting; }

	public float getChargePercent() {return chargePercent;}

	public ActionController getController() { return controller; }
	
	public Loadout getStartLoadout() { return startLoadout; }

	public MouseTracker getMouse() { return mouse; }

	public void setMouse(MouseTracker mouse) { this.mouse = mouse; }

	public int getConnId() { return connId;	}

	public void setConnId(int connId) { this.connId = connId; }

	public User getUser() { return user; }

	public void setUser(User user) { this.user = user; }

	public void setScaling(boolean scaling) { this.scaling = scaling; }
	
	public void setInvisible(int invisible) { this.invisible = invisible; }

	public void setBlinded(float blinded) { this.blinded = blinded; }

	public float getBlinded() { return blinded; }

	public void startTyping() { this.typingCdCount = 1.0f; }
	
	public Event getStart() { return start; }

	public void setStart(Event start) { this.start = start; }

	public PlayerSpriteHelper getSpriteHelper() { return spriteHelper; }

	public void setDespawnType(DespawnType despawnType) { this.despawnType = despawnType; }

	public void setDontMoveCamera(boolean dontMoveCamera) {	this.dontMoveCamera = dontMoveCamera; }

	public boolean isDontMoveCamera() { return dontMoveCamera; }
}