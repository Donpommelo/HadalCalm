package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import static com.mygdx.hadal.utils.Constants.PPM;

public class Popper extends RangedWeapon {

	private final static String name = "Popper";
	private final static int clipSize = 1;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.2f;
	private final static float reloadTime = 0.6f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 18.0f;
	private final static float knockback = 15.0f;
	private final static float projectileSpeed = 80.0f;
	private final static int projectileWidth = 90;
	private final static int projectileHeight = 90;
	private final static float lifespan = 0.3f;
	private final static float gravity = 5.0f;
	
	private final static int projDura = 3;
	
	private final static int numProj = 15;
	private final static int spread = 20;
	private final static float fragSpeed = 30.0f;
	private final static int fragWidth = 30;
	private final static int fragHeight = 30;
	private final static float fragLifespan = 3.0f;
	private final static float fragDamage = 5.0f;
	private final static float fragGravity = 7.5f;

	private final static float projDampen = 8.0f;
	private final static float fragDampen = 10.0f;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite fragSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	public Popper(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, final Vector2 startVelocity, float x, float y, final short filter) {
		final Equipable tool = this;
		
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user, projSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				super.create();
				hbox.getBody().setLinearDamping(projDampen);
			}
			
			@Override
			public void die() {
				for (int i = 0; i < numProj; i++) {
					float newDegrees = (float) (new Vector2(0, 1).angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
					Vector2 newVelocity = new Vector2(0, 1).nor().scl(fragSpeed);
					
					Hitbox frag = new HitboxSprite(state, 
							hbox.getPosition().x * PPM, hbox.getPosition().y * PPM,
							fragWidth, fragHeight, fragGravity, fragLifespan, projDura, 0, newVelocity.setAngle(newDegrees),
							filter, true, true, user, fragSprite) {
						
						@Override
						public void create() {
							super.create();
							hbox.getBody().setLinearDamping(fragDampen);
						}
					};
					
					frag.addStrategy(new HitboxDefaultStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxOnContactWallDieStrategy(state, frag, user.getBodyData()));
					frag.addStrategy(new HitboxDamageStandardStrategy(state, frag, user.getBodyData(), tool, fragDamage, knockback, DamageTypes.RANGED));
				}
			}
		});
	}
}
