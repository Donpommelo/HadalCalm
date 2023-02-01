package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.MassData;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.input.ActionController;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.schmucks.entities.helpers.*;
import com.mygdx.hadal.schmucks.entities.helpers.PlayerSpriteHelper.DespawnType;
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
import com.mygdx.hadal.utils.PlayerConditionUtil;
import com.mygdx.hadal.utils.WorldUtil;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * The player is the entity that the player controls.
 * @author Ningerbread Nicorice
 */
public class Player extends Schmuck {
	
	private static final int BASE_HP = 100;
	private static final float PLAYER_DENSITY = 1.0f;

	//Dimension of player sprite parts.
	public static final int HB_WIDTH = 216;
	public static final int HB_HEIGHT = 516;

	public static final float SCALE = 0.15f;
	public static final float PLAYER_MASS = 2.4489846f;
	public static final float PICKUP_RADIUS = 250.0f;

	//this makes the player animate faster in the air for the "luigi legs"
	private static final float AIR_ANIMATION_SLOW = 3.0f;

	private float scaleModifier = 0.0f;
	private float gravityModifier = 1.0f;
	private float restitutionModifier = 0.0f;
	private boolean dontMoveCamera;

	private final PlayerSpriteHelper spriteHelper;
	private final PlayerUIHelper uiHelper;
	private final PlayerEffectHelper effectHelper;
	private final HitsoundHelper hitsoundHelper;
	private final MouseHelper mouseHelper;
	private final ShootHelper shootHelper;
	private final FuelHelper fuelHelper;
	private final PhysicsHelper physicsHelper;
	private final MovementAirblastHelper airblastHelper;
	private final MovementFastfallHelper fastfallHelper;
	private final MovementJumpHelper jumpHelper;
	private final GroundedHelper groundedHelper;
	private final EventInteractHelper eventHelper;
	private final PingHelper pingHelper;

	private TextureRegion toolSprite;

	//Foot data for checking groundedness
	private FeetData feetData;

	//blinded is kept track of this way too b/c it affects visuals
	private float blinded;

	//user data
	private PlayerBodyData playerData;
	
	//This counter keeps track of elapsed time so the entity behaves the same regardless of engine tick time.
	private float controllerCount, controllerCountUniversal;
	
	//This is the controller that causes this player to perform actions
	private ActionController controller;
	
	//This is the loadout that this player starts with.
	private final Loadout startLoadout;
	
	//This is the connection id and user of the player (0 if server)
	private int connID;
	private User user;
	
	//should we reset this player's playerData stuff upon creation
	private final boolean reset;
	
	//this is the point we are starting at.
	private Event start;

	/**
	 * This constructor is called by the player spawn event that must be located in each map
	 * @param state: current gameState
	 * @param startPos: the player's starting location
	 * @param name: the player's name
	 * @param startLoadout: This is the player's starting loadout
	 * @param oldData: If created after a stage transition, this is the data of the previous player.
	 * @param connID: connection id. 0 if server.
	 * @param reset: do we reset the player's stats after creating them?
	 * @param start: the start point that the player spawns at.
	 */
	public Player(PlayState state, Vector2 startPos, String name, Loadout startLoadout, PlayerBodyData oldData, int connID,
				  User user, boolean reset, Event start) {
		super(state, startPos, new Vector2(HB_WIDTH * SCALE, HB_HEIGHT * SCALE), name, Constants.PLAYER_HITBOX, BASE_HP);
		this.name = name;
		toolSprite = Sprite.MT_DEFAULT.getFrame();

		this.moveState = MoveState.STAND;

		this.startLoadout = startLoadout;
		this.playerData = oldData;
		this.connID = connID;
		this.user = user;
		this.reset = reset;
		this.start = start;

		this.spriteHelper = new PlayerSpriteHelper(this, SCALE);
		setBodySprite(startLoadout.character, startLoadout.team);

		this.effectHelper = new PlayerEffectHelper(state, this);
		this.uiHelper = new PlayerUIHelper(state, this);
		this.hitsoundHelper = new HitsoundHelper(state, this);
		this.mouseHelper = new MouseHelper(state, this);
		this.shootHelper = new ShootHelper(state, this);
		this.fuelHelper = new FuelHelper(this);
		this.physicsHelper = new PhysicsHelper(this);
		this.airblastHelper = new MovementAirblastHelper(this);
		this.fastfallHelper = new MovementFastfallHelper(this);
		this.jumpHelper = new MovementJumpHelper(state, this);
		this.groundedHelper = new GroundedHelper(this);
		this.eventHelper = new EventInteractHelper(this);
		this.pingHelper = new PingHelper(state, this);
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
		effectHelper.setEffectOffset();

		this.body = BodyBuilder.createBox(world, startPos, size, gravityModifier, PLAYER_DENSITY, restitutionModifier, 0.0f, false, true, Constants.BIT_PLAYER,
				(short) (Constants.BIT_PLAYER | Constants.BIT_WALL | Constants.BIT_SENSOR | Constants.BIT_PROJECTILE | Constants.BIT_ENEMY),
				hitboxFilter, false, playerData);

		//create several extra fixtures to keep track of feet to determine when the player gets their jump back and what terrain event they are standing on.
		this.feetData = new FeetData(UserDataType.FEET, this);

		Fixture feet = FixtureBuilder.createFixtureDef(body, new Vector2(0.5f, - size.y / 2), new Vector2(size.x - 2, size.y / 8), true, 0, 0, 0, 0,
				Constants.BIT_SENSOR, (short) (Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_DROPTHROUGHWALL), hitboxFilter);

		feet.setUserData(feetData);

		Fixture pickupRadius = FixtureBuilder.createFixtureDef(body, new Vector2(), new Vector2(PICKUP_RADIUS, PICKUP_RADIUS), true, 0, 0, 0, 0,
				Constants.BIT_PICKUP_RADIUS, Constants.BIT_PROJECTILE, hitboxFilter);

		pickupRadius.setUserData(new FeetData(UserDataType.PICKUP_RADIUS, this));

		//make the player's mass constant to avoid mass changing when player is a different size
		MassData newMass = body.getMassData();
		newMass.mass = PLAYER_MASS;
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
	@Override
	public void controller(float delta) {
		processMiscellaneousUniversal(delta);
		processMiscellaneous(delta);

		processMovement(delta);
		processEquipment(delta);

		super.controller(delta);
	}

	protected void processMovement(float delta) {
		controllerCount += delta;

		//This line ensures that this runs every 1/60 second regardless of computer speed.
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;

			jumpHelper.controllerInterval();
			fastfallHelper.controllerInterval();
			groundedHelper.controllerInterval();
		}

		fuelHelper.controller(delta);
		jumpHelper.controller(delta);
		fastfallHelper.controller(delta);
		airblastHelper.controller(delta);
	}

	protected void processEquipment(float delta) {
		shootHelper.controller(delta);

		//charge active item in all modes except campaign (where items charge by dealing damage).
		if (!GameMode.CAMPAIGN.equals(state.getMode())) {
			playerData.getActiveItem().gainCharge(delta);
		}
	}

	protected void processMiscellaneous(float delta) {

		//process cds
		eventHelper.controller(delta);
		hitsoundHelper.controller(delta);
		pingHelper.controller(delta);

		//process list of units that damaged this player within the last ~5 seconds
		playerData.processRecentDamagedBy(delta);
	}

	protected void processMiscellaneousUniversal(float delta) {
		controllerCountUniversal += delta;

		//This line ensures that this runs every 1/60 second regardless of computer speed.
		while (controllerCountUniversal >= Constants.INTERVAL) {
			controllerCountUniversal -= Constants.INTERVAL;

			physicsHelper.controllerInterval();
		}

		jumpHelper.controllerUniversal(delta);
		mouseHelper.controller();
	}
	
	/**
	 * Player uses active item.
	 */
	public void activeItem() {
		playerData.getActiveItem().mouseClicked(0, state, getPlayerData(), hitboxFilter, mouseHelper.getPixelPosition());
		playerData.getActiveItem().execute(state, getPlayerData());
	}
	
	/**
	 * Player begins reloading.
	 */
	public void reload() {
		playerData.getCurrentTool().setReloading(true, false);
	}
	
	private final Vector2 playerLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		playerLocation.set(getPixelPosition());

		boolean batchSet = effectHelper.processInvisibility(batch);

		//render player sprite using sprite helper
		spriteHelper.render(batch, mouseHelper.getAttackAngle(), moveState, animationTime, animationTimeExtra,
				groundedHelper.isGrounded(), playerLocation,
				true, null, true);

		if (batchSet) {
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		}

		boolean visible = false;
		
		//draw hp and fuel bar if using certain effects, looking at self/ally, or in spectator mode
		if (state.isSpectatorMode() || hitboxFilter == state.getPlayer().hitboxFilter) {
			visible = true;
		} else {
			if (state.getPlayer().getPlayerData() != null) {
				if (state.getPlayer().getPlayerData().getStat(Stats.HEALTH_VISIBILITY) > 0) {
					visible = true;
				}
			}
		}
		uiHelper.render(batch, playerLocation, visible);

		playerData.statusProcTime(new ProcTime.Render(batch, playerLocation, size));
	}
	
	@Override
	public void dispose() {
		super.dispose();
		spriteHelper.dispose(despawnType);
		effectHelper.dispose();
	}
	
	/**
	 * This is called by the server when the player is created. Sends a packet to clients to instruct them to build a new player
	 * with the desired name and loadout
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		return new Packets.CreatePlayer(entityID, connID, getPixelPosition(), name, playerData.getLoadout(),
				hitboxFilter, scaleModifier, dontMoveCamera);
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

		short statusCode = getConditionCode();

		HadalGame.server.sendToAllUDP(new PacketsSync.SyncPlayer(entityID, getPosition(), getLinearVelocity(),
				entityAge, state.getTimer(), moveState, getBodyData().getCurrentHp(),
				mouseHelper.getPosition(), playerData.getCurrentSlot(),
				playerData.getCurrentTool().isReloading() ? uiHelper.getReloadPercent() : -1.0f,
				playerData.getCurrentTool().isCharging() ? uiHelper.getChargePercent() : -1.0f,
				playerData.getCurrentFuel(),
				statusCode));
	}
	
	/**
	 * The client Player receives the packet sent above and updates the provided fields.
	 */
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncPlayer p) {
			mouseHelper.setDesiredLocation(p.mousePosition.x, p.mousePosition.y);
			getPlayerData().setCurrentSlot(p.currentSlot);
			getPlayerData().setCurrentTool(getPlayerData().getMultitools()[p.currentSlot]);
			setToolSprite(playerData.getCurrentTool().getWeaponSprite().getFrame());

			getPlayerData().getCurrentTool().setReloading(p.reloadPercent != -1.0f, true);
			uiHelper.setReloadPercent(p.reloadPercent);

			getPlayerData().getCurrentTool().setCharging(p.chargePercent != -1.0f);
			uiHelper.setChargePercent(p.chargePercent);

			getPlayerData().setCurrentFuel(p.currentFuel);

			processConditionCode(p.conditionCode);

		} else if (o instanceof Packets.DeletePlayer p) {

			//delegate to sprite helper for despawn so it can dispose of frame buffer object
			spriteHelper.despawn(p.type, getPixelPosition(), getLinearVelocity());
			setDespawnType(p.type);
			((ClientState) state).removeEntity(entityID);
		}
	}

	public void processConditionCode(short statusCode) {
		jumpHelper.setJumping(PlayerConditionUtil.codeToCondition(statusCode, Constants.JUMPING));
		groundedHelper.setGrounded(PlayerConditionUtil.codeToCondition(statusCode, Constants.GROUNDED));

		effectHelper.toggleRunningEffects(PlayerConditionUtil.codeToCondition(statusCode, Constants.RUNNING));
		effectHelper.toggleHoverEffects(PlayerConditionUtil.codeToCondition(statusCode, Constants.HOVERING));
		effectHelper.toggleReloadEffects(PlayerConditionUtil.codeToCondition(statusCode, Constants.RELOADING));
		effectHelper.setInvisible(PlayerConditionUtil.codeToCondition(statusCode, Constants.INVISIBLE));
		effectHelper.setTranslucent(PlayerConditionUtil.codeToCondition(statusCode, Constants.TRANSLUCENT));
		effectHelper.setTransparent(PlayerConditionUtil.codeToCondition(statusCode, Constants.TRANSPARENT));

		uiHelper.setTyping(PlayerConditionUtil.codeToCondition(statusCode, Constants.TYPING));
	}

	@Override
	public void clientController(float delta) {
		processMiscellaneousUniversal(delta);
		super.clientController(delta);
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
		if (groundedHelper.isGrounded()) {
			animationTime += i; 
		} else {
			animationTime += i * AIR_ANIMATION_SLOW;
		}
	}
	
	@Override
	public HadalData getHadalData() { return playerData; }
	
	@Override
	public BodyData getBodyData() { return playerData; }

	public short getConditionCode() {
		return PlayerConditionUtil.conditionToCode(
				groundedHelper.isGrounded(),
				jumpHelper.isJumping(),
				effectHelper.isRunning(),
				effectHelper.isHovering(),
				effectHelper.isInvisible(),
				effectHelper.isTranslucent(),
				effectHelper.isTransparent(),
				playerData.getCurrentTool().isReloading(),
				uiHelper.isTyping());
	}

	public void setScaleModifier(float scaleModifier) {
		this.spriteHelper.setScale(SCALE * (1.0f + scaleModifier));
		this.scaleModifier = scaleModifier;
	}

	public void setGravityModifier(float gravityModifier) { this.gravityModifier = gravityModifier; }

	public void setRestitutionModifier(float restitutionModifier) { this.restitutionModifier = restitutionModifier; }

	public PlayerBodyData getPlayerData() {	return playerData; }

	public FeetData getFeetData() { return feetData; }

	public TextureRegion getToolSprite() { return this.toolSprite; }

	public void setToolSprite(TextureRegion sprite) { toolSprite = sprite; }

	public ActionController getController() { return controller; }
	
	public Loadout getStartLoadout() { return startLoadout; }

	public int getConnID() { return connID;	}

	public void setConnID(int connID) { this.connID = connID; }

	public User getUser() { return user; }

	public void setUser(User user) { this.user = user; }

	public void setBlinded(float blinded) { this.blinded = blinded; }

	public float getBlinded() { return blinded; }

	public Event getStart() { return start; }

	public void setStart(Event start) { this.start = start; }

	public PlayerSpriteHelper getSpriteHelper() { return spriteHelper; }

	public PlayerEffectHelper getEffectHelper() { return effectHelper; }

	public PlayerUIHelper getUiHelper() { return uiHelper; }

	public HitsoundHelper getHitsoundHelper() { return hitsoundHelper; }

	public MouseHelper getMouseHelper() { return mouseHelper; }

	public ShootHelper getShootHelper() { return shootHelper; }

	public FuelHelper getFuelHelper() { return fuelHelper; }

	public MovementAirblastHelper getAirblastHelper() { return airblastHelper; }

	public MovementFastfallHelper getFastfallHelper() { return fastfallHelper; }

	public MovementJumpHelper getJumpHelper() { return jumpHelper; }

	public GroundedHelper getGroundedHelper() { return groundedHelper; }

	public EventInteractHelper getEventHelper() { return eventHelper; }

	public PingHelper getPingHelper() { return pingHelper; }

	public void setDespawnType(DespawnType despawnType) { this.despawnType = despawnType; }

	public void setDontMoveCamera(boolean dontMoveCamera) {	this.dontMoveCamera = dontMoveCamera; }

	public boolean isDontMoveCamera() { return dontMoveCamera; }
}
