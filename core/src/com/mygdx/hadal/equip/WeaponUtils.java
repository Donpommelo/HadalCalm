package com.mygdx.hadal.equip;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxAnimated;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class WeaponUtils {

	private static final float selfDamageReduction = 0.4f;
	
	public static Hitbox explode(PlayState state, float x, float y, final Schmuck user, 
			int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		Hitbox explosion = new HitboxAnimated(state, 
				x, y,	explosionRadius, explosionRadius, 0, 0.5f, 1, 0, new Vector2(0, 0),
				filter, true, user, "boom") {
			
			@Override
			public void controller(float delta) {
				this.body.setLinearVelocity(0, 0);
				super.controller(delta);
			}
		};

		explosion.setUserData(new HitboxData(state, explosion){
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					Vector2 kb = new Vector2(fixB.getEntity().getBody().getPosition().x - this.hbox.getBody().getPosition().x,
							fixB.getEntity().getBody().getPosition().y - this.hbox.getBody().getPosition().y);
					
					if (fixB.equals(user.getBodyData())) {
						fixB.receiveDamage(explosionDamage * selfDamageReduction, kb.nor().scl(explosionKnockback), 
								user.getBodyData(), true, DamageTypes.EXPLOSIVE, DamageTypes.DEFLECT);
					} else {
						fixB.receiveDamage(explosionDamage, kb.nor().scl(explosionKnockback), 
								user.getBodyData(), true, DamageTypes.EXPLOSIVE, DamageTypes.DEFLECT);
					}
				}
			}
		});
		
		return explosion;
	}
}
