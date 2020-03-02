package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.ReturnToUser;

public class Boomerang extends RangedWeapon {

	private final static int clipSize = 3;
	private final static int ammoSize = 18;
	private final static float shootCd = 0.75f;
	private final static float shootDelay = 0;
	private final static float reloadTime = .75f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 30.0f;
	private final static float projectileSpeed = 35.0f;
	private final static Vector2 projectileSize = new Vector2(60, 60);
	private final static float lifespan = 2.0f;
	private final static float returnAmp = 4.0f;
	
	private final static Sprite projSprite = Sprite.BOOMERANG;
	private final static Sprite weaponSprite = Sprite.MT_BOOMERANG;
	private final static Sprite eventSprite = Sprite.P_BOOMERANG;
	
	public Boomerang(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);		
		hbox.setRestitution(0.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new ReturnToUser(state, hbox, user.getBodyData(), hbox.getStartVelo().len() * returnAmp));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				
				//Set boomerang to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(10);
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					fixB.receiveDamage(baseDamage, hbox.getLinearVelocity().nor().scl(knockback), creator, true, DamageTypes.RANGED);
				}
			}
		});	
	}
}
