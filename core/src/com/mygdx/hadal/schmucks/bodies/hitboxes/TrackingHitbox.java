package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * A tracking hboxga rghalirlhgairsgfkrad
 * @author Zachary Tu
 *
 */
public class TrackingHitbox extends HitboxImage {

	private boolean stuck = false;
	private HadalEntity target;
	
	private final static float baseDamage = 15.0f;
	private final static float knockback = 35.0f;
	
	public TrackingHitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura,
			float rest, Vector2 startVelo, short filter, boolean sensor, final Schmuck creator, String spriteId) {
		super(state, x, y, width, height, grav, lifespan, dura, rest, startVelo, filter, sensor, creator, spriteId);
		
		this.setUserData(new HitboxData(state, this) {
			
			@Override
			public void onHit(final HadalData fixB) {

				//If not stuck yet and hitting a body, stick to it. A schmuck is saved.
				if (!stuck) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							stuck = true;
							target = fixB.getEntity();	
							hbox.getBody().setLinearVelocity(0, 0);
						} else {
							super.onHit(fixB);
						}
					} else {
						super.onHit(fixB);
					}
				} else if (target != null){
					if (target.getHadalData() != fixB) {
						
						if (fixB != null && target.getBody() != null) {

							if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.WALL)) {
								target.getHadalData().receiveDamage(baseDamage, new Vector2(0, 0),
										creator.getBodyData(), true, DamageTypes.RANGED);

							}

							if (fixB.getType().equals(UserDataTypes.BODY)) {
								fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback),
										creator.getBodyData(), true, DamageTypes.RANGED);
							}
						} else {

							target.getHadalData().receiveDamage(baseDamage, new Vector2(0, 0),
									creator.getBodyData(), true, DamageTypes.RANGED);
						}
					}
				}
			}
		});	
		
	}
	
	@Override
	public void create() {
		super.create();
		this.body.getFixtureList().get(1).setUserData(data);	
	}
	
	@Override
	public void controller(float delta) {
		if (stuck && target != null) {
			super.controller(delta);
			
			if (target != null && target.isAlive()) {
				if (behavior != null) {
					behavior.calculateSteering(steeringOutput);
					applySteering(delta);
				}
			}
			
			if (target.isAlive() && target.getBody() != null) {
				target.getBody().setTransform(getPosition(), 0);
			} else {
				queueDeletion();
			}
		}
	}

}
