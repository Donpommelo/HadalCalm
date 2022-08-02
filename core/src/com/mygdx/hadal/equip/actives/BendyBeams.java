package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.hitbox.*;

/**
 * @author Flodzilla Fuzekiel
 */
public class BendyBeams extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.0f;
	private static final float MAX_CHARGE = 8.0f;
	
	private static final Vector2 PROJECTILE_SIZE = new Vector2(60, 20);
	private static final float LIFESPAN = 5.0f;
	private static final float PROJECTILE_SPEED = 45.0f;
	private static final float DURATION = 1.0f;
	private static final float PROC_CD = 0.25f;
	private static final int BEAM_NUMBER = 4;
	private static final float DAMAGE = 14.0f;
	private static final float KNOCKBACK = 20.0f;

	private static final Sprite PROJ_SPRITE = Sprite.NOTHING;

	public BendyBeams(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.LASERHARPOON.playUniversal(state, user.getPlayer().getPixelPosition(), 0.8f, false);
		
		user.addStatus(new Status(state, DURATION, false, user, user) {
			
			private float procCdCount;
			private final Vector2 startVelo = new Vector2();
			@Override
			public void timePassing(float delta) {
				super.timePassing(delta);
				if (procCdCount >= PROC_CD) {
					procCdCount -= PROC_CD;
					
					startVelo.set(weaponVelo).nor().scl(PROJECTILE_SPEED);
					for (int i = 0; i < BEAM_NUMBER; i++) {
						Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), PROJECTILE_SIZE, LIFESPAN, new Vector2(startVelo), user.getPlayer().getHitboxfilter(), true, false, user.getPlayer(), PROJ_SPRITE);
						hbox.addStrategy(new ControllerDefault(state, hbox, user));
						hbox.addStrategy(new DamageStandard(state, hbox, user, DAMAGE, KNOCKBACK, DamageSource.BENDY_BEAMS, DamageTag.ENERGY));
						hbox.addStrategy(new ContactUnitDie(state, hbox, user));
						hbox.addStrategy(new ContactWallDie(state, hbox, user));
						hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.LASER_PULSE, 0.0f, 1.0f)
								.setParticleColor(HadalColor.MALACHITE).setParticleSize(20));
						hbox.addStrategy(new AdjustAngle(state, hbox, user));
						hbox.addStrategy(new Curve(state, hbox, user, 90, 180,
								user.getPlayer().getMouse().getPixelPosition(), PROJECTILE_SPEED, 0.1f));
					}
				}
				procCdCount += delta;
			}
		});
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) (DURATION / PROC_CD * BEAM_NUMBER)),
				String.valueOf((int) DAMAGE)};
	}
}
