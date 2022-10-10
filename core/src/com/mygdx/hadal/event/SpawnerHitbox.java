package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Hitbox Spawner spawns a hitbox when activated. Spawned hitboxes can have a variety of properties and effects.
 * 
 * Triggered Behavior: When triggered, this will spawn a hitbox.
 * Triggering Behavior: if existent, the spawned hitboxes will be aimed at the body of the connected event (if it has a body).
 * Alt-Triggered Behavior: When alt-triggered, this spawner changes the number of schmucks it will spawn at once.
 * 
 * Fields:
 * There are a lot of fields. They are all just self-explanatory properties of the hitboxes
 * 
 * @author Purtoise Pleton
 */
public class SpawnerHitbox extends Event {
	
	//These are all properties of the spawned hitbox
	private final Vector2 projSize, startVelo;
	private final float lifespan, gravity, restitution, friction, damage, knockback, speed;
	private final boolean sensor, dieOnWall, dieOnSchmuck, adjustAngle;
	
	private final Sprite sprite;
	private final Particle particle;
	
	public SpawnerHitbox(PlayState state, Vector2 startPos, Vector2 size, Vector2 projSize, float lifespan, Vector2 startVelo, boolean sensor, String sprite,
			String particle, float gravity, float restitution, float friction, float damage, float knockback, boolean dieOnWall, boolean dieOnSchmuck, boolean adjustAngle) {
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
		this.adjustAngle = adjustAngle;
		
		this.speed = startVelo.len();
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this) {
			
			private final Vector2 finalVelo = new Vector2();
			@Override
			public void onActivate(EventData activator, Player p) {
				
				finalVelo.set(startVelo);
				
				//if we have a connected event with a body, the hitbox is aimed at the body
				if (event.getConnectedEvent() != null) {
					if (event.getConnectedEvent().getBody() != null) {
						finalVelo.set(event.getConnectedEvent().getPosition()).sub(event.getPosition()).nor().scl(speed);
					}
				}
				
				Hitbox hbox = new Hitbox(state, event.getPixelPosition(), projSize, lifespan, finalVelo, (short) 0, sensor, false, state.getWorldDummy(), sprite);
				hbox.setGravity(gravity);
				hbox.setRestitution(restitution);
				hbox.setFriction(friction);
				hbox.addStrategy(new ControllerDefault(state, hbox, state.getWorldDummy().getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, state.getWorldDummy().getBodyData(), damage, knockback, DamageSource.MISC));
				
				if (!Particle.NOTHING.equals(particle)) {
					hbox.addStrategy(new CreateParticles(state, hbox, state.getWorldDummy().getBodyData(), particle, 0.0f, 1.0f));
				}
				if (dieOnWall) {
					hbox.addStrategy(new ContactWallDie(state, hbox, state.getWorldDummy().getBodyData()));
				}
				if (dieOnSchmuck) {
					hbox.addStrategy(new ContactUnitDie(state, hbox, state.getWorldDummy().getBodyData()));
				}
				if (adjustAngle) {
					hbox.addStrategy(new AdjustAngle(state, hbox, state.getWorldDummy().getBodyData()));
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, 1, 1, 0, true, true,
				Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}
}
