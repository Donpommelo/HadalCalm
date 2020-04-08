package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieSound;

public class Popper extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.2f;
	private final static float reloadTime = 0.75f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 45.0f;
	private final static float recoil = 12.0f;
	private final static float knockback = 20.0f;
	private final static float projectileSpeed = 120.0f;
	private final static Vector2 projectileSize = new Vector2(45, 45);
	private final static float lifespan = 0.3f;
	
	private final static int numProj = 15;
	private final static int spread = 20;
	private final static float fragSpeed = 30.0f;
	private final static Vector2 fragSize = new Vector2(15, 15);
	private final static float fragLifespan = 1.2f;
	private final static float fragDamage = 5.0f;
	private final static float fragKnockback = 2.0f;

	private final static float projDampen = 10.0f;
	private final static float fragDampen = 3.0f;
	
	private final static Sprite projSprite = Sprite.ORB_PINK;
	private final static Sprite fragSprite = Sprite.ORB_PINK;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	public Popper(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, final short filter) {
		SoundEffect.CRACKER1.playUniversal(state, startPosition, 1.0f);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setGravity(5.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.CRACKER2, 0.4f));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.NOISEMAKER, 0.4f));

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
					
					Hitbox frag = new RangedHitbox(state, hbox.getPixelPosition(), fragSize, fragLifespan, newVelocity.setAngle(newDegrees), filter, false, true, user, fragSprite) {
						
						@Override
						public void create() {
							super.create();
							getBody().setLinearDamping(fragDampen);
						}
					};
					frag.setGravity(7.5f);
					frag.setDurability(3);
					
					frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
					frag.addStrategy(new ContactUnitLoseDurability(state, frag, user.getBodyData()));
					frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(), fragDamage, fragKnockback, DamageTypes.RANGED));
				}
			}
		});
	}
}
