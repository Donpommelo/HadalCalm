package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
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

/**
 * Shocked units spread chain lightning to nearby units
 * @author Frewort Fanswald
 */
public class Shocked extends Status {

	//this is the damage of each shock
	private final float damage;
	
	//this keeps track of the time between each chain lightning activation
	private float procCdCount;
	private static final float procCd = .25f;
	
	//these manage the trail that shows the lightning particles
	private static final Vector2 trailSize = new Vector2(10, 10);
	private static final float trailSpeed = 120.0f;
	private static final float trailLifespan = 3.0f;
	
	//the distance that the lightning can jump and the number of jumps it has left.
	private final int radius, chainAmount;
	
	//this is the hitbox filter that determines who the lightning can jump to
	private final short filter;
	
	//these variables are used for the aabb box querying to determine chain target
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
		//lightning should activate on death so that killing a unit does not end the chain
		chain();
	}
	
	/**
	 * This is run to activate the next jump of the chain lightning
	 */
	private final Vector2 entityLocation = new Vector2();
	private void chain() {
		if (chainAmount > 0) {
			SoundEffect.ZAP.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.4f, false);
			
			entityLocation.set(inflicted.getSchmuck().getPosition());
			//find a target closest to the current victim
			inflicted.getSchmuck().getWorld().QueryAABB(fixture -> {
				if (fixture.getUserData() instanceof BodyData bodyData) {
					if (bodyData.getSchmuck().getHitboxfilter() != filter && bodyData.getSchmuck().isAlive() && inflicted != fixture.getUserData()) {
						if (chainAttempt == null) {
							chainAttempt = bodyData.getSchmuck();
							closestDist = chainAttempt.getPosition().dst2(entityLocation);
						} else if (closestDist > bodyData.getSchmuck().getPosition().dst2(entityLocation)) {
							chainAttempt = bodyData.getSchmuck();
							closestDist = chainAttempt.getPosition().dst2(entityLocation);
						}
					}
				}
				return true;
			}, entityLocation.x - radius, entityLocation.y - radius, entityLocation.x + radius, entityLocation.y + radius);
			
			if (chainAttempt != null) {
				
				//spread status to new victim with -1 jump and damage them.
				chainAttempt.getBodyData().addStatus(new Shocked(state, inflicter, chainAttempt.getBodyData(), damage, radius, chainAmount - 1, filter));
				chainAttempt.getBodyData().receiveDamage(damage, new Vector2(), inflicter, true, null, DamageTypes.LIGHTNING);

				//draw the trail that makes the lightning particles visible
				Vector2 trailPath = new Vector2(chainAttempt.getPosition()).sub(entityLocation);
				Hitbox trail = new RangedHitbox(state, inflicted.getSchmuck().getPixelPosition(), trailSize, trailLifespan,
						new Vector2(trailPath).nor().scl(trailSpeed), filter, true, false, inflicted.getSchmuck(), Sprite.NOTHING);
				
				trail.addStrategy(new ControllerDefault(state, trail, inflicter));
				trail.addStrategy(new AdjustAngle(state, trail, inflicter));
				trail.addStrategy(new TravelDistanceDie(state, trail, inflicter, trailPath.len()));
				trail.addStrategy(new CreateParticles(state, trail, inflicter, Particle.LIGHTNING_BOLT, 0.0f, 3.0f).setRotate(true));
			}
		}
		inflicted.removeStatus(this);
	}
}
