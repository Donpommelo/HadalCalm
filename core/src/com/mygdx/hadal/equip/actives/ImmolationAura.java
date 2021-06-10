package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

/**
 * @author Suckette Smothro
 */
public class ImmolationAura extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 13.0f;

	private static final float baseDamage = 5.0f;
	private static final Vector2 hitboxSize = new Vector2(120, 120);
	private static final float lifespan = 3.0f;
	private static final float knockback = 0.2f;
	private static final float recoil = 3.0f;
	private static final float burnInterval = 1 / 60f;

	public ImmolationAura(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.WOOSH.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		user.addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 6.0f, user, user));

		Hitbox hbox = new Hitbox(state, mouseLocation, hitboxSize, lifespan, new Vector2(), user.getPlayer().getHitboxfilter(),
			true, true, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new FixedToEntity(state, hbox, user, new Vector2(), new Vector2(), false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user, SoundEffect.KICK1, 1.0f, true));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.FIRE, 0.0f, 1.0f).setParticleSize(40));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {

			private float controllerCount;
			private final Vector2 pulseVelocity = new Vector2();
			private final Vector2 hoverDirection = new Vector2(0, recoil);
			@Override
			public void controller(float delta) {

				controllerCount += delta;
				while (controllerCount >= burnInterval) {
					controllerCount -= burnInterval;

					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), hitboxSize, burnInterval, pulseVelocity, user.getSchmuck().getHitboxfilter(),
						true, true, user.getSchmuck(), Sprite.NOTHING);
					pulse.setSyncDefault(false);
					pulse.setEffectsVisual(false);
					pulse.makeUnreflectable();

					pulse.addStrategy(new ControllerDefault(state, pulse, user));
					pulse.addStrategy(new DamageStandard(state, pulse, user, baseDamage, knockback, DamageTypes.MELEE).setStaticKnockback(true));

					hoverDirection.setAngleDeg(user.getPlayer().getAttackAngle() + 180);
					user.getPlayer().pushMomentumMitigation(hoverDirection.x, hoverDirection.y);
				}
			}
		});
	}
}
