package com.mygdx.hadal.schmucks.strategies;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This strategy creates a number of fiery projectiles when its hbox dies
 * @author Zachary Tu
 *
 */
public class DieFireFrag extends HitboxStrategy {
	
	private int numFrag;
	private short filter;

	private final static int projectileWidth = 50;
	private final static int projectileHeight = 50;
	private final static float lifespan = 0.5f;
	private final static float gravity = 2.0f;

	private final static int projDura = 5;
		
	private final static float baseDamage = 8.0f;
	private final static float knockback = 5.0f;
	
	public DieFireFrag(PlayState state, Hitbox proj, BodyData user, int numFrag, short filter) {
		super(state, proj, user);
		this.filter = filter;
		this.numFrag = numFrag;
	}
	
	@Override
	public void die() {
		for (int i = 0; i < numFrag; i++) {
			
			float newDegrees = (ThreadLocalRandom.current().nextInt(0, 360));

			Hitbox hbox = new Hitbox(state, new Vector2(this.hbox.getPixelPosition()), new Vector2(projectileWidth, projectileHeight), 
					lifespan, this.hbox.getLinearVelocity().setAngle(newDegrees), filter, true, true, creator.getSchmuck(), Sprite.NOTHING);
			
			hbox.setGravity(gravity);
			hbox.setDurability(projDura);
			
			hbox.addStrategy(new ControllerDefault(state, hbox, creator));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, creator));
			hbox.addStrategy(new ContactWallDie(state, hbox, creator));
			hbox.addStrategy(new DamageStandard(state, hbox, creator, baseDamage, knockback, DamageTypes.RANGED));
			new ParticleEntity(state, hbox, Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
}
