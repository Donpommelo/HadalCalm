package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Hitbox;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import box2dLight.RayHandler;

public class WeaponUtils {

	private static final float selfDamageReduction = 0.4f;
	
	public static void explode(PlayState state, float x, float y, World world, OrthographicCamera camera, RayHandler rays, 
			final Schmuck user, int explosionRadius, final float explosionDamage, final float explosionKnockback) {
		Hitbox explosion = new Hitbox(state, 
				x, y,	explosionRadius, explosionRadius, 0, .02f, 1, 0, new Vector2(0, 0),
				(short) 0, true, world, camera, rays, user);

		explosion.setUserData(new HitboxData(state, world, explosion){
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					Vector2 kb = new Vector2(fixB.getEntity().getBody().getPosition().x - this.hbox.getBody().getPosition().x,
							fixB.getEntity().getBody().getPosition().y - this.hbox.getBody().getPosition().y);
					
					if (fixB.equals(user.getBodyData())) {
						fixB.receiveDamage(explosionDamage * selfDamageReduction, kb.nor().scl(explosionKnockback), 
								user.getBodyData(), true, DamageTypes.EXPLOSIVE);
					} else {
						fixB.receiveDamage(explosionDamage, kb.nor().scl(explosionKnockback), 
								user.getBodyData(), true, DamageTypes.EXPLOSIVE);
					}
				}
			}
		});
	}
}
