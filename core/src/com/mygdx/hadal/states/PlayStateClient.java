package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrack;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.effects.FrameBufferManager;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.hub.Wallpaper;
import com.mygdx.hadal.input.CommonController;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.entities.PlayerSelfOnClient;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.statuses.Blinded;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.utils.CameraUtil;
import com.mygdx.hadal.utils.TiledObjectUtil;

import java.util.UUID;

import static com.mygdx.hadal.constants.Constants.PHYSICS_TIME;
import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * This is a version of the playstate that is provided for Clients.
 * A lot of effects are not processed in this state like statuses and damage. Instead, everything is done based on instructions by the server.
 * @author Frornswoggle Fraginald
 */
public class PlayStateClient extends PlayState {

	protected OrthogonalTiledMapRenderer tmr;

	//world manages the Box2d world and physics. b2dr renders debug lines for testing
	protected Box2DDebugRenderer b2dr;

	//do we draw the hitbox lines?
	private boolean debugHitbox;

	//This is a set of all non-hitbox entities in the world mapped from their entityID
	private final OrderedMap<UUID, HadalEntity> entities = new OrderedMap<>();
	
	//This is a set of all hitboxes mapped from their unique entityID
	private final OrderedMap<UUID, HadalEntity> hitboxes = new OrderedMap<>();

	//This is a set of all particle effects mapped from their unique entityID
	private final OrderedMap<UUID, HadalEntity> effects = new OrderedMap<>();

	//this is a list containing all the aforementioned entity lists
	private final Array<OrderedMap<UUID, HadalEntity>> entityLists = new Array<>();

	//this maps shaders to all current entities using them so they can be rendered in a batch
	private final ObjectMap<Shader, Array<HadalEntity>> dynamicShaderEntities = new ObjectMap<>();
	private final ObjectMap<Shader, Array<HadalEntity>> staticShaderEntities = new ObjectMap<>();


	//This is a list of sync instructions. It contains [entityID, object to be synced]
	private final Array<SyncPacket> sync = new Array<>();

	//This contains the position of the client's mouse, to be sent to the server
	private final Vector3 mousePosition = new Vector3();

	//This is the time since the last missed create packet we send the server. Kept track of to avoid sending too many at once.
	private final ObjectMap<UUID, Float> timeSinceLastMissedCreate = new ObjectMap<>();
	private final Array<UUID> missedCreatesToRemove = new Array<>();

	//Background and black screen used for transitions
	private final TextureRegion bg, white;
	private Shader shaderBase = Shader.NOTHING, shaderTile = Shader.NOTHING;

	public PlayStateClient(HadalGame app, UnlockLevel level, GameMode mode) {
		super(app, level, mode, false, "");
		entityLists.add(hitboxes);
		entityLists.add(entities);
		entityLists.add(effects);

		//Init background image
		this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));
		this.white = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.WHITE.toString()));

		//client still needs anchor points, world dummies
		addEntity(getAnchor().getEntityID(), getAnchor(), false, ObjectLayer.STANDARD);
		addEntity(getWorldDummy().getEntityID(), getWorldDummy(), false, ObjectLayer.STANDARD);

		//set whether to draw hitbox debug lines or not
		debugHitbox = JSONManager.setting.isDebugHitbox();
	}

	@Override
	public void initializeMap() {
		super.initializeMap();
		PlayState me = this;

		b2dr = new Box2DDebugRenderer();
		tmr = new OrthogonalTiledMapRenderer(map, batch) {

			@Override
			public void render() {
				beginRender();

				if (shaderTile.getShaderProgram() != null) {
					batch.setShader(shaderTile.getShaderProgram());
					shaderTile.shaderPlayUpdate(me, getTimer());
					shaderTile.shaderDefaultUpdate(getTimer());
				}

				for (MapLayer layer : map.getLayers()) {
					renderMapLayer(layer);
				}

				if (shaderTile.getShaderProgram() != null) {
					if (shaderTile.isBackground()) {
						batch.setShader(null);
					}
				}

				endRender();
			}
		};

		if (map.getProperties().get("customShader", false, Boolean.class)) {
			shaderBase = Wallpaper.SHADERS[JSONManager.setting.getCustomShader()];
			shaderBase.loadShader();
		} else if (map.getProperties().get("shader", String.class) != null) {
			shaderBase = Shader.valueOf(map.getProperties().get("shader", String.class));
			shaderBase.loadShader();
		}
	}

	@Override
	public void clearMemory() {
		//We clear things like music/sound/shaders to periodically free up some memory
		StateManager.clearMemory();

		//we clear shaded cosmetics to avoid having too many cached fbos
		UnlockCosmetic.clearShadedCosmetics();
	}

	@Override
	public void parseMap() {
		mode.processSettings(this);

		//client processes collisions and certain events
		TiledObjectUtil.parseTiledObjectLayer(this, map.getLayers().get("collision-layer").getObjects());
		TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get("event-layer").getObjects());
		TiledObjectUtil.parseTiledTriggerLayer();

		//parse map-specific event layers (used for different modes in the same map)
		for (String layer : mode.getExtraLayers()) {
			if (map.getLayers().get(layer) != null) {
				TiledObjectUtil.parseTiledEventLayer(this, map.getLayers().get(layer).getObjects());
			}
		}
	}

	@Override
	public void processTeams() {
		for (User user : HadalGame.usm.getUsers().values()) {
			user.setTeamAssigned(false);
		}
		AlignmentFilter.resetTeams();

		//clear fbo of unused players. Need to do this after bots are set up and teams are assigned
		FrameBufferManager.clearAllFrameBuffers();
	}

	@Override
	public void show() {

		//b/c the play state can get shown multiple times without getting removed, we must get rid of stage if already created
		if (stage == null) {
			this.stage = new Stage();
		}

		getUIManager().initUIElements(stage);

		app.newMenu(stage);
		resetController();

		//if we faded out before transitioning to this stage, we should fade in upon showing
		if (FadeManager.getFadeLevel() >= 1.0f) {
			FadeManager.fadeIn();
		}

		//play track corresponding to map properties or a random song from the combat ost list
		MusicTrack newTrack;
		if (map.getProperties().get("music", String.class) != null) {
			newTrack =  MusicPlayer.playSong(MusicTrackType.getByName(
					map.getProperties().get("music", String.class)), 1.0f);
		} else {
			newTrack = MusicPlayer.playSong(MusicTrackType.MATCH, 1.0f);
		}

		if (newTrack != null) {
			MusicIcon icon = new MusicIcon(newTrack);
			stage.addActor(icon);
			icon.animateIcon();
		}
	}

	@Override
	public void resetController() {

		//we check if we are in a playstate (not paused or in setting menu) b/c we don't reset control in those states
		if (!StateManager.states.empty()) {
			if (StateManager.states.peek() instanceof PlayState) {
				if (null != HadalGame.usm.getOwnPlayer()) {
					//Whenever the controller is reset, the client gets a new client controller.
					controller = new PlayerController(HadalGame.usm.getOwnPlayer());

					InputMultiplexer inputMultiplexer = new InputMultiplexer();
					inputMultiplexer.addProcessor(stage);
					inputMultiplexer.addProcessor(controller);
					inputMultiplexer.addProcessor(new CommonController(this));
					Gdx.input.setInputProcessor(inputMultiplexer);
				}
			}
		}
	}
	
	//these control the frequency that we process world physics.
	private float physicsAccumulator;

	//these control the frequency that we send latency checking packets to the server.
	private static final float LATENCY_CHECK = 1 / 10f;
	private float latencyAccumulator;
	private float latency;

	//separate timer used to calculate latency
	private float clientPingTimer;

	@Override
	public void update(float delta) {

		//this makes the physics separate from the game framerate
		physicsAccumulator += delta;
		while (physicsAccumulator >= PHYSICS_TIME) {
			physicsAccumulator -= PHYSICS_TIME;

			//The box2d world takes a step. This handles collisions + physics stuff.
			world.step(PHYSICS_TIME, 8, 3);
		}

		//All entities that are set to be created are created and assigned their entityID
		for (CreatePacket packet : createListClient) {
			HadalEntity oldEntity;
			if (ObjectLayer.HBOX.equals(packet.layer)) {
				oldEntity = hitboxes.get(packet.entityID);
				hitboxes.put(packet.entityID, packet.entity);
			} else if (ObjectLayer.EFFECT.equals(packet.layer)) {
				oldEntity = effects.get(packet.entityID);
				effects.put(packet.entityID, packet.entity);
			} else {
				oldEntity = entities.get(packet.entityID);
				entities.put(packet.entityID, packet.entity);
			}

			//we replace old entity with same tag b/c we must never dispose of something that hasn't been created
			//also, we must create anything that has been initialized to avoid leaking memory
			if (oldEntity != null) {
				oldEntity.dispose();
			}
			packet.entity.create();

			if (packet.entityID != null) {
				packet.entity.setEntityID(packet.entityID);
			}
			packet.entity.setReceivingSyncs(packet.synced);
		}
		createListClient.clear();
		
		//All entities that are set to be removed are removed.
		for (UUID key : removeListClient) {
			HadalEntity entity = findEntity(key);
			if (entity != null) {
				entity.dispose();
			}
			for (ObjectMap<UUID, HadalEntity> m : entityLists) {
				m.remove(key);
			}
		}
		removeListClient.clear();

		//process camera, ui, any received packets
		processCommonStateProperties(delta, false);
		clientPingTimer += delta;

		//this makes the latency checking separate from the game framerate
		latencyAccumulator += delta;
		if (latencyAccumulator >= LATENCY_CHECK) {
			latencyAccumulator = 0;
			PacketManager.clientUDP(new Packets.LatencySyn((int) (latency * 1000), clientPingTimer));
		}

		missedCreatesToRemove.clear();
		for (ObjectMap.Entry<UUID, Float> entry : timeSinceLastMissedCreate) {
			entry.value -= delta;
			if (entry.value <= 0.0f) {
				missedCreatesToRemove.add(entry.key);
			}
		}
		for (UUID id : missedCreatesToRemove) {
			timeSinceLastMissedCreate.remove(id);
		}
		
		//All sync instructions are carried out.
		while (!sync.isEmpty()) {
			SyncPacket p = sync.removeIndex(0);
		 	if (p != null) {
				HadalEntity entity = hitboxes.get(p.entityID);
		 		if (entity != null) {
		 			entity.onReceiveSync(p.packet, p.timestamp);
		 		} else {
					entity = effects.get(p.entityID);
					if (entity != null) {
						entity.onReceiveSync(p.packet, p.timestamp);
					} else {
						entity = entities.get(p.entityID);

						//if we have the entity, sync it
						if (entity != null) {
							entity.onReceiveSync(p.packet, p.timestamp);
						}
					}
				}
		 	}
		}

		//clientController is run for the objects that process on client side.
		for (ObjectMap<UUID, HadalEntity> m : entityLists) {
			for (HadalEntity entity : m.values()) {
				entity.clientController(delta);
				entity.getShaderHelper().decreaseShaderCount(delta);
				entity.increaseAnimationTime(delta);
			}
		}

		//periodically update score window if scores have been updated
		scoreSyncAccumulator += delta;
		if (scoreSyncAccumulator >= SCORE_SYNC_TIME) {
			scoreSyncAccumulator = 0;
			boolean changeMade = false;
			for (User user : HadalGame.usm.getUsers().values()) {
				if (user.isScoreUpdated()) {
					changeMade = true;
					user.setScoreUpdated(false);
				}
			}
			if (changeMade) {
				getUIManager().getScoreWindow().syncScoreTable();
			}
		}
	}

	@Override
	public void processCommonStateProperties(float delta, boolean postGame) {
		if (!postGame) {
			//Increment the game timer, if exists
			getTimerManager().incrementTimer(delta);
			getCameraManager().controller(delta);
		}
	}

	@Override
	public void render(float delta) {

		//Render Background
		batch.setProjectionMatrix(hud.combined);
		batch.disableBlending();
		batch.begin();

		//render shader
		if (shaderBase.getShaderProgram() != null) {
			batch.setShader(shaderBase.getShaderProgram());
			shaderBase.shaderPlayUpdate(this, getTimer());
			shaderBase.shaderDefaultUpdate(getTimer());
		}

		batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);

		if (shaderBase.getShaderProgram() != null) {
			if (shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}

		batch.end();
		batch.enableBlending();

		//Render Tiled Map + world
		tmr.setView(camera);
		tmr.render();

		//Render debug lines for box2d objects. THe 0 check prevents debug outlines from appearing in the freeze-frame
		if (debugHitbox && 0.0f != delta) {
			b2dr.render(world, camera.combined.scl(PPM));
			camera.combined.scl(1.0f / PPM);
		}

		//Iterate through entities in the world to render visible entities
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		Particle.drawParticlesBelow(batch, delta);
		renderEntities();
		Particle.drawParticlesAbove(batch, delta);

		if (shaderBase.getShaderProgram() != null) {
			if (!shaderBase.isBackground()) {
				batch.setShader(null);
			}
		}

		batch.end();

		//add white filter if the player is blinded
		if (null != HadalGame.usm.getOwnPlayer()) {
			if (HadalGame.usm.getOwnPlayer().getBlinded() > 0.0f) {
				batch.setProjectionMatrix(hud.combined);
				batch.begin();

				batch.setColor(1.0f, 1.0f, 1.0f, Blinded.getBlindAmount(HadalGame.usm.getOwnPlayer().getBlinded()));
				batch.draw(white, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
				batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

				batch.end();
			}
		}
	}

	public void renderEntities() {
		for (ObjectMap<UUID, HadalEntity> m : entityLists) {
			for (HadalEntity entity : m.values()) {
				renderEntity(entity);
			}
		}
		renderShadedEntities();
	}

	private final Vector2 entityLocation = new Vector2();
	/**
	 * This method renders a single entity.
	 * @param entity: the entity we are rendering
	 */
	public void renderEntity(HadalEntity entity) {
		entityLocation.set(entity.getPixelPosition());
		if (entity.isVisible(entityLocation)) {

			//for shaded entities, add them to a map instead of rendering right away so we can render them at once
			if (entity.getShaderStatic() != null && entity.getShaderStatic() != Shader.NOTHING) {
				Array<HadalEntity> shadedEntities = staticShaderEntities.get(entity.getShaderStatic());
				if (null == shadedEntities) {
					shadedEntities = new Array<>();
					staticShaderEntities.put(entity.getShaderStatic(), shadedEntities);
				}
				shadedEntities.add(entity);
			} else if (entity.getShaderHelper().getShader() != null && entity.getShaderHelper().getShader() != Shader.NOTHING) {
				Array<HadalEntity> shadedEntities = dynamicShaderEntities.get(entity.getShaderHelper().getShader());
				if (null == shadedEntities) {
					shadedEntities = new Array<>();
					dynamicShaderEntities.put(entity.getShaderHelper().getShader(), shadedEntities);
				}
				shadedEntities.add(entity);
			} else {
				entity.render(batch, entityLocation);
			}
		}
	}

	/**
	 * This renders shaded entities so we can minimize shader switches
	 */
	public void renderShadedEntities() {

		//do same thing for static shaders
		for (ObjectMap.Entry<Shader, Array<HadalEntity>> entry : staticShaderEntities) {

			//we sometimes set static shaders without loading them (overrided static shaders that are conditional)
			if (null == entry.key.getShaderProgram()) {
				entry.key.loadStaticShader();
			}
			batch.setShader(entry.key.getShaderProgram());
			for (HadalEntity entity : entry.value) {
				entityLocation.set(entity.getPixelPosition());
				entity.render(batch, entityLocation);

				if (entity.getShaderHelper().getShaderStaticCount() <= 0.0f) {
					entity.getShaderHelper().setStaticShader(Shader.NOTHING, 0.0f);
				}
			}
		}
		staticShaderEntities.clear();

		for (ObjectMap.Entry<Shader, Array<HadalEntity>> entry : dynamicShaderEntities) {
			//for each shader, render all entities using it at once so we only need to set it once
			batch.setShader(entry.key.getShaderProgram());
			for (HadalEntity entity : entry.value) {
				entityLocation.set(entity.getPixelPosition());

				//unlike static shaders, dynamic shaders need controller updated
				entity.getShaderHelper().processShaderController(getTimer());
				entity.render(batch, entityLocation);

				if (entity.getShaderHelper().getShaderCount() <= 0.0f) {
					entity.getShaderHelper().setShader(Shader.NOTHING, 0.0f);
				}
			}
		}
		dynamicShaderEntities.clear();

		batch.setShader(null);
	}
	
	/**
	 * This is called whenever the client is told to add an object to its world.
	 * @param entityID: The uuid of the entity
	 * @param entity: The entity to be added
	 * @param synced: should this object receive a regular sync packet from the server?
	 * @param layer: is this layer a hitbox (rendered underneath) or not?
	 */
	public void addEntity(UUID entityID, HadalEntity entity, boolean synced, ObjectLayer layer) {
		CreatePacket packet = new CreatePacket(entityID, entity, synced, layer);
		createListClient.add(packet);
	}

	public void addEntity(long uuidMSB, long uuidLSB, HadalEntity entity, boolean synced, ObjectLayer layer) {
		addEntity(new UUID(uuidMSB, uuidLSB), entity, synced, layer);
	}

	/**
	 * This is called whenever the client is told to remove an object from the world.
	 * @param entityID: The unique id of the object to be removed.
	 */
	public void removeEntity(UUID entityID) {
		removeListClient.add(entityID);
	}

	/**
	 * This is called whenever the client is told to synchronize an object from the world.
	 * @param uuidMSB: The most-significant bits of the uuid
	 * @param uuidLSB: The least-significant bits of the uuid
	 * @param o: The SyncEntity Packet to use to synchronize the object
	 * @param timestamp: the time of the sync on the server.
	 */
	public void syncEntity(long uuidMSB, long uuidLSB, Object o, float timestamp) {
		SyncPacket packet = new SyncPacket(new UUID(uuidMSB, uuidLSB), o, timestamp);
		sync.add(packet);
	}

	/**
	 * This looks at the entities in the world and returns the one with the given id. 
	 * @param entityID: Unique id of he object to find
	 * @return The found object (or null if nonexistent)
	 */
	@Override
	public HadalEntity findEntity(UUID entityID) {
		HadalEntity entity = entities.get(entityID);
		if (entity != null) {
			return entity;
		} else {
			entity = effects.get(entityID);
			if (entity != null) {
				return entity;
			} else {
				return hitboxes.get(entityID);
			}
		}
	}

	@Override
	public Player spawnPlayer(String name, PlayerBodyData old, User user, boolean client, Event spawn, Vector2 overiddenSpawn) {
		Player p;
		if (user.getConnID() < 0) {
			p = new PlayerBot(this, overiddenSpawn, name, old, user, isReset(), spawn);
		} else {
			if (!client) {
				p = new Player(this, overiddenSpawn, name, old, user, isReset(), spawn);
			} else {
				p = new PlayerSelfOnClient(this, overiddenSpawn, name, null, user, isReset(), spawn);
			}
		}
		return p;
	}

	/**
	 * This is run when the server responds to our latency check packet. We calculate our ping and save it.
	 */
	public void syncLatency(float serverTime, float clientTimestamp) {

		//when transitioning to new state, we don't want old timestamp to give us a negative latency
		if (clientTimestamp <= clientPingTimer) {
			latency = clientPingTimer - clientTimestamp;
			setTimer(serverTime - 2 * PlayState.SYNC_TIME);
		}
	}
	
	@Override
	public void dispose() {
		//clean up all client entities. (some entities require running their dispose() to function properly (soundEntities turning off)
		for (ObjectMap<UUID, HadalEntity> m : entityLists) {
			for (HadalEntity entity : m.values()) {
				entity.dispose();
			}
		}
		for (UUID key : removeListClient) {
			HadalEntity entity = findEntity(key);
			if (entity != null) {
				entity.dispose();
			}
		}

		b2dr.dispose();
		world.dispose();
		tmr.dispose();
		map.dispose();
		if (stage != null) {
			stage.dispose();
		}
		CameraUtil.resetCameraRotation(camera);
	}

	@Override
	public void resize() {
		getCameraManager().resize();

		if (shaderBase.getShaderProgram() != null) {
			shaderBase.getShaderProgram().bind();
			shaderBase.shaderResize();
		}

		if (shaderTile.getShaderProgram() != null) {
			shaderTile.getShaderProgram().bind();
			shaderTile.shaderResize();
		}
	}

	@Override
	public boolean isServer() { return false; }

	/**
	 * This record represents a packet telling the client to sync an object
	 */
	private record SyncPacket(UUID entityID, Object packet, float timestamp) {}

	/**
	 * This record represents a packet telling the client to create an object
	 */
	public record CreatePacket(UUID entityID, HadalEntity entity, boolean synced, ObjectLayer layer) {}

	/**
	 * This sets the game's boss, filling the boss ui.
	 * @param enemy: This is the boss whose hp will be used for the boss hp bar
	 */
	@Override
	public void setBoss(Enemy enemy) {
		getUIManager().getUiPlay().setBoss(enemy, enemy.getName());
		getUIManager().getUiExtra().setBoss();
	}

	/**
	 * This is called when the boss is defeated, clearing its hp bar from the ui.
	 * We also have to tell the client to do the same.
	 */
	@Override
	public void clearBoss() {
		getUIManager().getUiPlay().clearBoss();
		getUIManager().getUiExtra().clearBoss();
	}

	/**
	 * This sets a shader to be used as a "base-shader" for things like the background
	 */
	@Override
	public void setShaderBase(Shader shader) {
		shaderBase = shader;
		shaderBase.loadShader();
	}

	@Override
	public void setShaderTile(Shader shader) {
		shaderTile = shader;
		shaderTile.loadShader();
	}

	@Override
	public void toggleVisibleHitboxes(boolean debugHitbox) { this.debugHitbox = debugHitbox; }

	/**
	 * The destroy and create methods do nothing for the client. 
	 * Objects cannot be created and destroyed in this way for the client, only by calling add and remove Entity.
	 */
	@Override
	public void destroy(HadalEntity entity) {}
	
	@Override
	public void create(HadalEntity entity) {}
	
	public float getLatency() { return latency; }

	public Vector3 getMousePosition() { return mousePosition; }
}
