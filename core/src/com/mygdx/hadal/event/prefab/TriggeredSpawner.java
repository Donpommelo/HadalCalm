package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * This is like a TimedSpawner, except you can specify the trigger to be an event outside of the prefab.
 * @author Zachary Tu
 *
 */
public class TriggeredSpawner extends Prefabrication {

	//The id of the event that triggers this
	private String triggeredId;
	
	//How long does it take before the pickup spawns? How much fuel/hp does the pickup regenerate
	private float power;
		
		//0 = fuel. 1 = Hp
	private int type;
	
	public TriggeredSpawner(PlayState state, int width, int height, int x, int y, String triggeredId, int type, float power) {
		super(state, width, height, x , y);
		this.triggeredId = triggeredId;
		this.power = power;
		this.type = type;
	}
	
	@Override
	public void generateParts() {
		String pickupId = TiledObjectUtil.getPrefabTriggerId();
		String spawnerId = TiledObjectUtil.getPrefabTriggerId();
		String onTouchId = TiledObjectUtil.getPrefabTriggerId();
		String pickupBackId = TiledObjectUtil.getPrefabTriggerId();
		String pickupEffectId = TiledObjectUtil.getPrefabTriggerId();
		String pickupParticleId = TiledObjectUtil.getPrefabTriggerId();
		String effectParticleId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("EventMove");
		spawner.getProperties().put("align", 2);
		spawner.getProperties().put("sync", 2);
		spawner.getProperties().put("particle_std", "EVENT_HOLO");
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "BASE");
		spawner.getProperties().put("triggeredId", spawnerId);
		spawner.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(0, 0, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", pickupBackId);
		back.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject respawn = new RectangleMapObject();
		respawn.setName("Multitrigger");
		respawn.getProperties().put("triggeredId", triggeredId);
		respawn.getProperties().put("triggeringId", spawnerId + "," + effectParticleId);
		
		RectangleMapObject pickup = new RectangleMapObject();
		pickup.getRectangle().set(0, 0, width, height);
		pickup.setName("Sensor");
		pickup.getProperties().put("align", 2);
		pickup.getProperties().put("sync", 2);
		pickup.getProperties().put("scale", 0.25f);
		pickup.getProperties().put("triggeredId", pickupId);
		pickup.getProperties().put("triggeringId", onTouchId);
		
		RectangleMapObject effect = new RectangleMapObject();
		effect.setName("Player");
		effect.getProperties().put("triggeredId", onTouchId);
		effect.getProperties().put("triggeringId", pickupEffectId);
		
		RectangleMapObject use = new RectangleMapObject();
		use.setName("Multitrigger");
		use.getProperties().put("triggeredId", pickupEffectId);
		use.getProperties().put("triggeringId", pickupBackId + "," + pickupParticleId + "," + effectParticleId);
		
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
		case 0:
			pickup.getProperties().put("sprite", "FUEL");
			effect.getProperties().put("fuel", power);
			pickupParticle.getProperties().put("particle", "PICKUP_ENERGY");
			break;
		case 1:
			pickup.getProperties().put("sprite", "MEDPAK");
			effect.getProperties().put("hp", power);
			pickupParticle.getProperties().put("particle", "PICKUP_HEALTH");
			break;
		}
		
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, back);
		TiledObjectUtil.parseTiledEvent(state, respawn);
		TiledObjectUtil.parseTiledEvent(state, pickup);
		TiledObjectUtil.parseTiledEvent(state, effect);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, pickupParticle);
		TiledObjectUtil.parseTiledEvent(state, effectParticle);
	}
}
