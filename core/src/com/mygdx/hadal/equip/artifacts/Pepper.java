package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class Pepper extends Artifact {

	private final static int statusNum = 1;
	private final static int slotCost = 1;
	
	private final static float radius = 10.0f;
	private final static float damage = 6.0f;
	private final static float particleDuration = 1.0f;
	
	private final static float procCd = 1.5f;

	public Pepper() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private float procCdCount = procCd;
			private Vector2 entityLocation = new Vector2();
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					entityLocation.set(inflicted.getSchmuck().getPosition());
					state.getWorld().QueryAABB(new QueryCallback() {

						@Override
						public boolean reportFixture(Fixture fixture) {
							if (fixture.getUserData() instanceof BodyData) {
								if (((BodyData) fixture.getUserData()).getSchmuck().getHitboxfilter() != inflicted.getSchmuck().getHitboxfilter()) {
									((BodyData) fixture.getUserData()).receiveDamage(damage, new Vector2(0, 0), inflicted, true);
									new ParticleEntity(state, ((BodyData) fixture.getUserData()).getSchmuck(), Particle.LIGHTNING, 1.0f, particleDuration, true, particleSyncType.CREATESYNC);
								}
							}
							return true;
						}
					}, 
						entityLocation.x - radius, entityLocation.y - radius, 
						entityLocation.x + radius, entityLocation.y + radius);		
				}
			}
		};
		return enchantment;
	}
}
