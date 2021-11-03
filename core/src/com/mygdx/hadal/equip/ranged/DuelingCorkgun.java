package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;

public class DuelingCorkgun extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 36;
	private static final float shootCd = 0.4f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 0.8f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 20.0f;
	private static final float recoil = 11.0f;
	private static final float knockback = 90.0f;
	private static final float projectileSpeed = 55.0f;
	private static final Vector2 projectileSize = new Vector2(46, 40);
	private static final float lifespan = 1.0f;
	
	private static final Sprite projSprite = Sprite.CORK;
	private static final Sprite weaponSprite = Sprite.MT_ICEBERG;
	private static final Sprite eventSprite = Sprite.P_ICEBERG;
	
	public DuelingCorkgun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.CORK.playUniversal(state, startPosition, 1.5f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION));
		hbox.addStrategy(new ContactUnitKnockbackDamage(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.BULLET, DamageTypes.RANGED));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE1, 0.5f, true));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WALL_HIT1, 0.5f));
	}
}
