package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.TravelDistanceDie;

public class Shocked extends Status {

	private float damage;
	private float procCdCount;
	private final static float procCd = .25f;
	
	private final static Vector2 trailSize = new Vector2(10, 10);
	private final static float trailSpeed = 60.0f;
	private final static float trailLifespan = 3.0f;
	
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

				Vector2 trailPath = new Vector2(chainAttempt.getPosition()).sub(inflicted.getSchmuck().getPosition());
				
				Hitbox trail = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), trailSize, trailLifespan, new Vector2(trailPath).nor().scl(trailSpeed), filter, true, false, inflicted.getSchmuck(), Sprite.NOTHING);
				trail.addStrategy(new ControllerDefault(state, trail, inflicter));
				trail.addStrategy(new AdjustAngle(state, trail, inflicter));
				trail.addStrategy(new TravelDistanceDie(state, trail, inflicter, trailPath.len()));
				trail.addStrategy(new CreateParticles(state, trail, inflicter, Particle.LIGHTNING_BOLT, 0.0f, 3.0f).setRotate(true));
			}
		}
		
		inflicted.removeStatus(this);
	}
}
