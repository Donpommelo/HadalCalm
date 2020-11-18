package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

public class JumpKick extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 8.0f;
	
	private static final float recoil = 150.0f;

	private static final float baseDamage = 20.0f;
	private static final Vector2 hitboxSize = new Vector2(90, 120);
	private static final float lifespan = 0.5f;
	private static final float particleLifespan = 0.6f;
	private static final float knockback = 90.0f;
	
	public JumpKick(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.WOOSH.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		boolean right = weaponVelo.x > 0;

		Particle particle = Particle.MOREAU_LEFT;
		if (right) {
			particle = Particle.MOREAU_RIGHT;
		}
		new ParticleEntity(state, user.getPlayer(), particle, 1.0f, 1.0f, true, ParticleEntity.particleSyncType.TICKSYNC)
			.setScale(0.5f).setPrematureOff(particleLifespan)
			.setColor(WeaponUtils.getPlayerColor(user.getPlayer()));

		user.addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 7.5f, user, user));
		Vector2 push = new Vector2(weaponVelo).nor().scl(recoil);
		user.getPlayer().pushMomentumMitigation(push.x, push.y);
		
		Hitbox hbox = new Hitbox(state, mouseLocation, hitboxSize, lifespan, new Vector2(), user.getPlayer().getHitboxfilter(),  true, true, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user , Particle.SPARKS));
		hbox.addStrategy(new DamageStandard(state, hbox, user, baseDamage, knockback, DamageTypes.MELEE).setStaticKnockback(true));
		hbox.addStrategy(new FixedToEntity(state, hbox, user, new Vector2(), new Vector2(), false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user, SoundEffect.KICK1, 1.0f, true));
		hbox.addStrategy(new ContactUnitKnockbackDamage(state, hbox, user));
	}
}
