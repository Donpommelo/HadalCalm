package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Shocked units spread chain lightning to nearby units
 * @author Frewort Fanswald
 */
public class Shocked extends Status {

	private static final float PROC_CD = 0.25f;

	//this is the damage of each shock
	private final float damage;

	//this keeps track of the time between each chain lightning activation
	private float procCdCount;

	//the distance that the lightning can jump and the number of jumps it has left.
	private final int radius, chainAmount;
	
	//this is the hitbox filter that determines who the lightning can jump to
	private final short filter;

	//this is the effect/item/weapon source of the shock
	private final SyncedAttack source;

	//these variables are used for the aabb box querying to determine chain target
	private Schmuck chainAttempt;
	private float closestDist;
	
	public Shocked(PlayState state, BodyData p, BodyData v, float damage, int radius, int chainAmount, short filter, SyncedAttack source) {
		super(state, 0, false, p, v);
		this.procCdCount = 0;
		this.damage = damage;
		this.radius = radius;
		this.chainAmount = chainAmount;
		this.filter = filter;
		this.source = source;

		this.setServerOnly(true);
	}
	
	@Override
	public void timePassing(float delta) {
		if (procCdCount >= PROC_CD) {
			procCdCount -= PROC_CD;
			chain();
		}
		procCdCount += delta;
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source) {
		//lightning should activate on death so that killing a unit does not end the chain
		chain();
	}
	
	/**
	 * This is run to activate the next jump of the chain lightning
	 */
	private final Vector2 entityLocation = new Vector2();
	private void chain() {
		if (chainAmount > 0) {

			entityLocation.set(inflicted.getSchmuck().getPosition());
			//find a target closest to the current victim
			inflicted.getSchmuck().getWorld().QueryAABB(fixture -> {
				if (fixture.getUserData() instanceof BodyData bodyData) {
					if (bodyData.getSchmuck().getHitboxFilter() != filter && bodyData.getSchmuck().isAlive() && inflicted != fixture.getUserData()) {
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
				if (inflicter.getSchmuck() instanceof Player player) {
					source.initiateSyncedAttackNoHbox(state, chainAttempt, inflicted.getSchmuck().getPosition(), true,
							player.getConnID(), chainAmount, radius, damage);
				}
			}
		}
		inflicted.removeStatus(this);
	}
}
