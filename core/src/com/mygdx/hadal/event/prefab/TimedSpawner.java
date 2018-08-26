package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

public class TimedSpawner extends Prefabrication {

	private float interval, power;
	private int type;
	
	public TimedSpawner(PlayState state, int width, int height, int x, int y, float interval, int type, float power) {
		super(state, width, height, x , y);
		this.interval = interval;
		this.power = power;
		this.type = type;
	}
	
	@Override
	public void generateParts() {
		
		String pickupId = TiledObjectUtil.getPrefabTriggerId();
		String spawnerId = TiledObjectUtil.getPrefabTriggerId();
		String onTouchId = TiledObjectUtil.getPrefabTriggerId();
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String respawnId = TiledObjectUtil.getPrefabTriggerId();
		String pickupBackId = TiledObjectUtil.getPrefabTriggerId();
		String pickupEffectId = TiledObjectUtil.getPrefabTriggerId();
		String pickupParticleId = TiledObjectUtil.getPrefabTriggerId();
		String effectParticleId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject spawner = new RectangleMapObject();
		spawner.getRectangle().set(x, y, width, height);
		spawner.setName("EventMove");
		spawner.getProperties().put("align", 2);
		spawner.getProperties().put("particle_std", "event_holo");
		spawner.getProperties().put("scale", 0.25f);
		spawner.getProperties().put("sprite", "event_base");
		spawner.getProperties().put("triggeredId", spawnerId);
		spawner.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject back = new RectangleMapObject();
		back.getRectangle().set(0, 0, width, height);
		back.setName("EventMove");
		back.getProperties().put("triggeredId", pickupBackId);
		back.getProperties().put("triggeringId", pickupId);
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", interval);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", respawnId);
		
		RectangleMapObject respawn = new RectangleMapObject();
		respawn.setName("Multitrigger");
		respawn.getProperties().put("triggeredId", respawnId);
		respawn.getProperties().put("triggeringId", spawnerId + "," + timerId+ "," + effectParticleId);
		
		RectangleMapObject pickup = new RectangleMapObject();
		pickup.getRectangle().set(0, 0, width, height);
		pickup.setName("Sensor");
		pickup.getProperties().put("align", 2);
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
		use.getProperties().put("triggeringId", pickupBackId + "," + timerId + "," + pickupParticleId + "," + effectParticleId);
		
		RectangleMapObject pickupParticle = new RectangleMapObject();
		pickupParticle.setName("Particle");
		pickupParticle.getProperties().put("duration", 2.0f);
		pickupParticle.getProperties().put("triggeredId", pickupParticleId);
		
		RectangleMapObject effectParticle = new RectangleMapObject();
		effectParticle.setName("Particle");
		effectParticle.getProperties().put("particle", "event_holo");
		effectParticle.getProperties().put("triggeredId", effectParticleId);
		effectParticle.getProperties().put("triggeringId", spawnerId);

		switch (type) {
		case 0:
			pickup.getProperties().put("sprite", "event_fuel");
			effect.getProperties().put("fuel", power);
			pickupParticle.getProperties().put("particle", "energy_pickup");
			break;
		case 1:
			pickup.getProperties().put("sprite", "event_health");
			effect.getProperties().put("hp", power);
			pickupParticle.getProperties().put("particle", "health_pickup");
			break;
		}
		
		TiledObjectUtil.parseTiledEvent(state, spawner);
		TiledObjectUtil.parseTiledEvent(state, back);
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, respawn);
		TiledObjectUtil.parseTiledEvent(state, pickup);
		TiledObjectUtil.parseTiledEvent(state, effect);
		TiledObjectUtil.parseTiledEvent(state, use);
		TiledObjectUtil.parseTiledEvent(state, pickupParticle);
		TiledObjectUtil.parseTiledEvent(state, effectParticle);
	}
}
