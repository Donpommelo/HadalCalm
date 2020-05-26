package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

public class Shocked extends Status {

	private float damage;
	private float procCdCount;
	private final static float procCd = .25f;
	private final static int projectileHeight = 15;
	
	private int radius, chainAmount;
	private short filter;
	
	private Schmuck chainAttempt;
	private float closestDist;
	
	public Shocked(PlayState state, BodyData p, BodyData v, float damage, int radius, int chainAmount, short filter) {
		super(state, 0, false, p, v);
		this.procCdCount = 0;
		this.damage = damage;
		this.radius = radius;
		this.chainAmount = chainAmount;
		this.filter = filter;
	}
	
	@Override
	public void timePassing(float delta) {
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			chain();
		}
		procCdCount += delta;
	}
	
	@Override
	public void onDeath(BodyData perp) {
		chain();
	}
	
	private void chain() {
		if (chainAmount > 0) {
			SoundEffect.ZAP.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.5f, false);
			
			inflicted.getSchmuck().getWorld().QueryAABB(new QueryCallback() {

				@Override
				public boolean reportFixture(Fixture fixture) {
					if (fixture.getUserData() instanceof BodyData) {
						
						if (((BodyData) fixture.getUserData()).getSchmuck().getHitboxfilter() != filter && inflicted != fixture.getUserData()) {
							
							if (chainAttempt == null) {
								chainAttempt = ((BodyData) fixture.getUserData()).getSchmuck(); 
								closestDist = chainAttempt.getPosition().dst2(inflicted.getSchmuck().getPosition());
							} else if (closestDist > ((BodyData) fixture.getUserData()).getSchmuck().getPosition().dst2(inflicted.getSchmuck().getPosition())) {
								chainAttempt = ((BodyData) fixture.getUserData()).getSchmuck(); 
								closestDist = chainAttempt.getPosition().dst2(inflicted.getSchmuck().getPosition());
							}
						}
					}
					return true;
				}
			}, 
				inflicted.getSchmuck().getPosition().x - radius, inflicted.getSchmuck().getPosition().y - radius, 
				inflicted.getSchmuck().getPosition().x + radius, inflicted.getSchmuck().getPosition().y + radius);
			
			if (chainAttempt != null) {
				chainAttempt.getBodyData().addStatus(new Shocked(state, inflicter, chainAttempt.getBodyData(), damage, radius, chainAmount - 1, filter));
				chainAttempt.getBodyData().receiveDamage(damage, new Vector2(), inflicter, false, DamageTypes.ENERGY);

				new ParticleEntity(state, chainAttempt, Particle.LIGHTNING, 0.0f, 0.3f, true, particleSyncType.CREATESYNC);

				Hitbox hbox = new RangedHitbox(state, new Vector2(inflicted.getSchmuck().getPixelPosition()).add(chainAttempt.getPixelPosition()).scl(0.5f), new Vector2(closestDist + 100, projectileHeight), procCd, new Vector2(0, 0), filter, true, true, inflicter.getSchmuck(), Sprite.LASER) {
					
					@Override
					public void create() {
						super.create();
						//Rotate hitbox to match angle of fire.
						float newAngle = (float)(Math.atan2(chainAttempt.getPosition().y - inflicted.getSchmuck().getPosition().y, chainAttempt.getPosition().x - inflicted.getSchmuck().getPosition().x));
						
						setTransform(getPosition().x, getPosition().y, newAngle);
					}
				};
				hbox.addStrategy(new ControllerDefault(state, hbox, inflicter));
				hbox.addStrategy(new Static(state, hbox, inflicter));
			}
		}
		
		inflicted.removeStatus(this);
	}
}
