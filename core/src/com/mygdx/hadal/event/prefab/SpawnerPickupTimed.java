package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The TimedSpawner is a prefab consists of a spawner that, on a timer, spawns an fuel or health pickup.
 * @author Zessomuffin Zobalante
 */
public class SpawnerPickupTimed extends Prefabrication {

	private static final String PICKUP_ID = "PICKUP_TIMED_PICKUP";
	private static final String SPAWNER_ID = "PICKUP_TIMED_SPAWNER";
	private static final String TOUCH_ID = "PICKUP_TIMED_TOUCH";
	private static final String TIMER_ID = "PICKUP_TIMED_TIMER";
	private static final String RESPAWN_ID = "PICKUP_TIMED_RESPAWN";
	private static final String BACK_ID = "PICKUP_TIMED_BACK";
	private static final String EFFECT_ID = "PICKUP_TIMED_EFFECT";
	private static final String PICKUP_PARTICLE_ID = "PICKUP_TIMED_PICKUP_PARTICLE";
	private static final String EFFECT_PARTICLE_ID = "PICKUP_TIMED_EFFECT_PARTICLE";

	//How long does it take before the pickup spawns? How much fuel/hp does the pickup regenerate
	private final float interval, power;
	
	private String pickupId, spawnerId;
	
	//0 = fuel. 1 = hp. 2 = ammo
	private final int type;
	
	public SpawnerPickupTimed(PlayState state, float width, float height, float x, float y, float interval, int type, float power) {
		super(state, width, height, x , y);
		this.interval = interval;
		this.power = power;
		this.type = type;
	}
	
	@Override
	public void generateParts() {
		pickupId = TiledObjectUtil.getPrefabTriggerIdSynced("", PICKUP_ID, x, y);
		spawnerId = TiledObjectUtil.getPrefabTriggerIdSynced("", SPAWNER_ID, x, y);
		addPickup(state, width, height, x, y, interval, type, power, pickupId, spawnerId);
	}

	/**
	 * Helper function to create a pickup event. This is a separate function so that weapon spawners can use it in custom loadout mode
	 */
	public static void addPickup(PlayState state, float width, float height, float x, float y, float interval, int type, float power,
								 String pickupId, String spawnerId) {
		String onTouchId = TiledObjectUtil.getPrefabTriggerIdSynced("", TOUCH_ID, x, y);
		String timerId = TiledObjectUtil.getPrefabTriggerIdSynced("", TIMER_ID, x, y);
		String respawnId = TiledObjectUtil.getPrefabTriggerIdSynced("", RESPAWN_ID, x, y);
		String pickupBackId = TiledObjectUtil.getPrefabTriggerIdSynced("", BACK_ID, x, y);
		String pickupEffectId = TiledObjectUtil.getPrefabTriggerIdSynced("", EFFECT_ID, x, y);
		String pickupParticleId = TiledObjectUtil.getPrefabTriggerIdSynced("", PICKUP_PARTICLE_ID, x, y);
		String effectParticleId = TiledObjectUtil.getPrefabTriggerIdSynced("", EFFECT_PARTICLE_ID, x, y);

		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("EventMove");
		spawner.getProperties().put("syncServer", "ECHO_ACTIVATE");
		spawner.getProperties().put("syncClient", "ECHO");
		spawner.getProperties().put("particle_std", "EVENT_HOLO");
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "BASE");
		spawner.getProperties().put("triggeredId", spawnerId);
		spawner.getProperties().put("triggeringId", pickupId);

		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(-1000, -1000, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", pickupBackId);
		back.getProperties().put("triggeringId", pickupId);

		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", interval);
		timer.getProperties().put("startOn", false);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", respawnId);

		RectangleMapObject respawn = new RectangleMapObject();
		respawn.setName("Multitrigger");
		respawn.getProperties().put("triggeredId", respawnId);
		respawn.getProperties().put("triggeringId", spawnerId + "," + timerId+ "," + effectParticleId);

		RectangleMapObject pickup = new RectangleMapObject();
		pickup.getRectangle().set(x, y, width, height);
		pickup.setName("Sensor");
		pickup.getProperties().put("align", "CENTER_BOTTOM");
		pickup.getProperties().put("scale", 0.25f);
		pickup.getProperties().put("cooldown", 1.0f);
		pickup.getProperties().put("triggeredId", pickupId);
		pickup.getProperties().put("triggeringId", onTouchId);
		pickup.getProperties().put("pickup", true);

		RectangleMapObject effect = new RectangleMapObject();
		effect.setName("Player");
		effect.getProperties().put("triggeredId", onTouchId);
		effect.getProperties().put("triggeringId", pickupEffectId);


		RectangleMapObject use = new RectangleMapObject();
		use.setName("Multitrigger");
		use.getProperties().put("triggeredId", pickupEffectId);
		use.getProperties().put("triggeringId", pickupBackId + "," + timerId + "," + pickupParticleId + "," + effectParticleId);
		use.getProperties().put("syncServer", "ECHO_ACTIVATE_EXCLUDE");
		use.getProperties().put("syncClient", "ECHO_ACTIVATE");

		RectangleMapObject pickupParticle = new RectangleMapObject();
		pickupParticle.setName("Particle");
		pickupParticle.getProperties().put("duration", 2.0f);
		pickupParticle.getProperties().put("triggeredId", pickupParticleId);

		RectangleMapObject effectParticle = new RectangleMapObject();
		effectParticle.setName("Particle");
		effectParticle.getProperties().put("particle", "EVENT_HOLO");
		effectParticle.getProperties().put("startOn", true);
		effectParticle.getProperties().put("triggeredId", effectParticleId);
		effectParticle.getProperties().put("triggeringId", spawnerId);

		switch (type) {
			case 0 -> {
				pickup.getProperties().put("sprite", "FUEL");
				effect.getProperties().put("fuel", power);
				pickupParticle.getProperties().put("particle", "PICKUP_ENERGY");
			}
			case 1 -> {
				pickup.getProperties().put("sprite", "MEDPAK");
				pickup.getProperties().put("bot_health_pickup", true);
				effect.getProperties().put("hp", power);
				pickupParticle.getProperties().put("particle", "PICKUP_HEALTH");
			}
			case 2 -> {
				pickup.getProperties().put("sprite", "AMMO");
				effect.getProperties().put("ammo", power);
				pickupParticle.getProperties().put("particle", "PICKUP_AMMO");
			}
		}

		TiledObjectUtil.parseAddTiledEvent(state, spawner);
		TiledObjectUtil.parseAddTiledEvent(state, back);
		TiledObjectUtil.parseAddTiledEvent(state, timer);
		TiledObjectUtil.parseAddTiledEvent(state, respawn);
		TiledObjectUtil.parseAddTiledEvent(state, pickup);
		TiledObjectUtil.parseAddTiledEvent(state, effect);
		TiledObjectUtil.parseAddTiledEvent(state, use);
		TiledObjectUtil.parseAddTiledEvent(state, pickupParticle);
		TiledObjectUtil.parseAddTiledEvent(state, effectParticle);
	}

	@Override
	public Array<String> getConnectedEvents() {
		Array<String> events = new Array<>();
		events.add(pickupId);
		events.add(spawnerId);
		return events;
	}
}
