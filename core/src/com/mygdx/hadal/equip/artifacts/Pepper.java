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

	private final static String name = "Pepper";
	private final static String descr = "Damages nearby enemies.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	private final static float radius = 5.0f;
	private final static float damage = 8.0f;
	private final static float particleDuration = 2.5f;
	
	public Pepper() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, name, descr, b) {
			
			private float procCdCount;
			private float procCd = 1.0f;
			
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
				if (procCdCount >= procCd) {
					procCdCount -= procCd;
					
					state.getWorld().QueryAABB(new QueryCallback() {

						@Override
						public boolean reportFixture(Fixture fixture) {
							if (fixture.getUserData() instanceof BodyData) {
								if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxfilter() != inflicted.getSchmuck().getHitboxfilter()) {
									((BodyData)fixture.getUserData()).receiveDamage(damage, new Vector2(0, 0), 
											inflicted, inflicted.getCurrentTool(), false);
									new ParticleEntity(state, ((BodyData)fixture.getUserData()).getSchmuck(), Particle.LIGHTNING, 0.0f, particleDuration, true, particleSyncType.TICKSYNC);
								}
							}
							return true;
						}
					}, 
					inflicted.getSchmuck().getPosition().x - radius, inflicted.getSchmuck().getPosition().y - radius, 
					inflicted.getSchmuck().getPosition().x + radius, inflicted.getSchmuck().getPosition().y + radius);		
				}
			}
		};
		return enchantment;
	}
}
