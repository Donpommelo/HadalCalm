package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * @author Zachary Tu
 *
 */
public class SpawnerHitbox extends Event {
	
	private Vector2 projSize, startVelo;
	private float lifespan, gravity, restitution, friction, damage, knockback;
	private boolean sensor, dieOnWall, dieOnSchmuck;
	
	private Sprite sprite;
	private Particle particle;
	
	public SpawnerHitbox(PlayState state, Vector2 startPos, Vector2 size, Vector2 projSize, float lifespan, Vector2 startVelo, boolean sensor, String sprite,
			String particle, float gravity, float restitution, float friction, float damage, float knockback, boolean dieOnWall, boolean dieOnSchmuck) {
		super(state, startPos, size);
		this.projSize = projSize;
		this.lifespan = lifespan;
		this.startVelo = startVelo;
		this.sensor = sensor;
		this.sprite = Sprite.valueOf(sprite);
		this.particle = Particle.valueOf(particle);
		this.gravity = gravity;
		this.restitution = restitution;
		this.friction = friction;
		this.damage = damage;
		this.knockback = knockback;
		this.dieOnWall = dieOnWall;
		this.dieOnSchmuck = dieOnSchmuck;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				Hitbox hbox = new Hitbox(state, event.getPixelPosition(), projSize, lifespan, startVelo, (short) 0, sensor, false, state.getWorldDummy(), sprite);
				hbox.setGravity(gravity);
				hbox.setRestitution(restitution);
				hbox.setFriction(friction);
				hbox.addStrategy(new DamageStandard(state, hbox, state.getWorldDummy().getBodyData(), damage, knockback));
				
				if (!particle.equals(Particle.NOTHING)) {
					hbox.addStrategy(new CreateParticles(state, hbox, state.getWorldDummy().getBodyData(), particle, 0.0f, 3.0f));
				}
				
				if (dieOnWall) {
					hbox.addStrategy(new ContactWallDie(state, hbox, state.getWorldDummy().getBodyData()));
				}
				
				if (dieOnSchmuck) {
					hbox.addStrategy(new ContactUnitDie(state, hbox, state.getWorldDummy().getBodyData()));
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true, Constants.BIT_SENSOR, (short) (0), (short) 0, true, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
}
