package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This is like a TimedSpawner, except you can specify the trigger to be an event outside of the prefab.
 * @author Lachuguel Libottoms
 */
public class SpawnerPickupTriggered extends Prefabrication {

	private static final String PICKUP_ID = "PICKUP_TRIGGER_PICKUP";
	private static final String SPAWNER_ID = "PICKUP_TRIGGER_SPAWNER";
	private static final String TOUCH_ID = "PICKUP_TRIGGER_TOUCH";
	private static final String BACK_ID = "PICKUP_TRIGGER_BACK";
	private static final String EFFECT_ID = "PICKUP_TRIGGER_EFFECT";
	private static final String PICKUP_PARTICLE_ID = "PICKUP_TRIGGER_PICKUP_PARTICLE";
	private static final String EFFECT_PARTICLE_ID = "PICKUP_TRIGGER_EFFECT_PARTICLE";

	//The id of the event that triggers this
	private final String triggeredId;
	
	private String pickupId, spawnerId;
	
	//How long does it take before the pickup spawns? How much fuel/hp does the pickup regenerate
	private final float power;
		
	//0 = fuel. 1 = Hp
	private final int type;
	
	public SpawnerPickupTriggered(PlayState state, float width, float height, float x, float y, String triggeredId, int type, float power) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.power = power;
		this.type = type;
	}
	
	@Override
	public void generateParts() {
		
		pickupId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, PICKUP_ID, x, y);
		spawnerId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, SPAWNER_ID, x, y);
		
		String onTouchId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, TOUCH_ID, x, y);
		String pickupBackId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, BACK_ID, x, y);
		String pickupEffectId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, EFFECT_ID, x, y);
		String pickupParticleId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, PICKUP_PARTICLE_ID, x, y);
		String effectParticleId = TiledObjectUtil.getPrefabTriggerIdSynced(triggeredId, EFFECT_PARTICLE_ID, x, y);
		
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
		
		RectangleMapObject respawn = new RectangleMapObject();
		respawn.setName("Multitrigger");
		respawn.getProperties().put("triggeredId", triggeredId);
		respawn.getProperties().put("triggeringId", spawnerId + "," + effectParticleId);
		
		RectangleMapObject pickup = new RectangleMapObject();
		pickup.getRectangle().set(-1000, -1000, width, height);
		pickup.setName("Sensor");
		pickup.getProperties().put("align", "CENTER_BOTTOM");
		pickup.getProperties().put("scale", 0.25f);
		pickup.getProperties().put("scale", 0.25f);
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
		use.getProperties().put("triggeringId", pickupBackId + "," + pickupParticleId + "," + effectParticleId);
		use.getProperties().put("syncServer", "ECHO_ACTIVATE_EXCLUDE");
		use.getProperties().put("syncClient", "ECHO_ACTIVATE");

		RectangleMapObject pickupParticle = new RectangleMapObject();
		pickupParticle.setName("Particle");
		pickupParticle.getProperties().put("duration", 2.0f);
		pickupParticle.getProperties().put("triggeredId", pickupParticleId);
		
		RectangleMapObject effectParticle = new RectangleMapObject();
		effectParticle.setName("Particle");
		effectParticle.getProperties().put("particle", "EVENT_HOLO");
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
		}
		
		TiledObjectUtil.parseAddTiledEvent(state, spawner);
		TiledObjectUtil.parseAddTiledEvent(state, back);
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
