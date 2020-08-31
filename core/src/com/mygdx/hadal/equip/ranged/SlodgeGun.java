package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.FiringWeapon;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;

public class SlodgeGun extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 25;
	private final static float shootCd = 0.1f;
	private final static float shootDelay = 0.25f;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 8.0f;
	private final static float recoil = 24.0f;
	private final static float knockback = 5.0f;
	private final static float projectileSpeed = 25.0f;
	private final static Vector2 projectileSize = new Vector2(40, 40);
	private final static float lifespan = 1.5f;
	
	private final static float procCd = .05f;

	private final static float slowDura = 4.0f;
	private final static float slow = 0.8f;
	private final static float fireDuration = 0.8f;

	private final static Sprite weaponSprite = Sprite.MT_SLODGEGUN;
	private final static Sprite eventSprite = Sprite.P_SLODGEGUN;
	
	public SlodgeGun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, Sprite.NOTHING);
		hbox.setGravity(3.0f);
		hbox.setDurability(3);
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.SLODGE, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SLODGE, 0.0f, 3.0f).setParticleSize(90));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SLODGE_STATUS));
		hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), slowDura, slow, Particle.SLODGE_STATUS));
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		if (processClip(state, shooter)) {
			SoundEffect.DARKNESS1.playUniversal(state, user.getPixelPosition(), 0.9f, false);

			shooter.addStatus(new FiringWeapon(state, fireDuration, shooter, shooter, projectileSpeed, 0, 0, projectileSize.x, procCd, this));
		}
	}
}
