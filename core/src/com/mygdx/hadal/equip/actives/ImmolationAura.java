package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

/**
 * @author Suckette Smothro
 */
public class ImmolationAura extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.0f;
	private static final float MAX_CHARGE = 13.0f;

	private static final float BASE_DAMAGE = 5.0f;
	private static final Vector2 HITBOX_SIZE = new Vector2(120, 120);
	private static final float LIFESPAN = 3.0f;
	private static final float KNOCKBACK = 0.2f;
	private static final float RECOIL = 3.0f;
	private static final float BURN_INTERVAL = 1 / 60f;

	public ImmolationAura(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.WOOSH.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		user.addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 6.0f, user, user));

		Hitbox hbox = new Hitbox(state, mouseLocation, HITBOX_SIZE, LIFESPAN, new Vector2(), user.getPlayer().getHitboxfilter(),
			true, true, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new FixedToEntity(state, hbox, user, new Vector2(), new Vector2()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user, SoundEffect.KICK1, 1.0f, true));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.FIRE, 0.0f, 1.0f).setParticleSize(40)
				.setParticleColor(HadalColor.FIERY_ROSE));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {

			private float controllerCount;
			private final Vector2 pulseVelocity = new Vector2();
			private final Vector2 hoverDirection = new Vector2(0, RECOIL);
			@Override
			public void controller(float delta) {

				controllerCount += delta;
				while (controllerCount >= BURN_INTERVAL) {
					controllerCount -= BURN_INTERVAL;

					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), HITBOX_SIZE, BURN_INTERVAL, pulseVelocity, user.getSchmuck().getHitboxfilter(),
						true, true, user.getSchmuck(), Sprite.NOTHING);
					pulse.setSyncDefault(false);
					pulse.setEffectsVisual(false);
					pulse.makeUnreflectable();

					pulse.addStrategy(new ControllerDefault(state, pulse, user));
					pulse.addStrategy(new DamageStandard(state, pulse, user, BASE_DAMAGE, KNOCKBACK,
							DamageSource.IMMOLATION_AURA, DamageTag.MELEE).setStaticKnockback(true));

					hoverDirection.setAngleDeg(user.getPlayer().getAttackAngle() + 180);
					user.getPlayer().pushMomentumMitigation(hoverDirection.x, hoverDirection.y);
				}
			}
		});
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (BASE_DAMAGE / BURN_INTERVAL)),
				String.valueOf((int) LIFESPAN)};
	}
}
