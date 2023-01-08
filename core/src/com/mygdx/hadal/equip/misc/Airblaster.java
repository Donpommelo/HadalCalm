package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.constants.Stats;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Airblaster extends MeleeWeapon {

	private static final float SWING_CD = 0.25f;
	private static final float BASE_DAMAGE = 0.0f;
	private static final Vector2 HITBOX_SIZE = new Vector2(150, 150);
	private static final float KNOCKBACK = 60.0f;
	public static final float MOMENTUM = 50.0f;
	
	private static final float REFLECT_VELO_AMP = 1.25f;
	public static final float REFLECT_VELO_MIN = 20.0f;
	public static final float REFLECT_VELO_MAX = 80.0f;

	public Airblaster(Schmuck user) {
		super(user, SWING_CD, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		fire(state, user, user.getPixelPosition(), weaponVelo, faction);
		user.pushFromLocation(mouseLocation, MOMENTUM * (1 + shooter.getStat(Stats.BOOST_RECOIL)));
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.AIRBLAST.playUniversal(state, startPosition, 0.4f, false);
		
		Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, SWING_CD, new Vector2(), user.getHitboxfilter(), true, false, user, Sprite.NOTHING);
		hbox.setScale(1 + user.getBodyData().getStat(Stats.BOOST_SIZE));
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK * (1 + user.getBodyData().getStat(Stats.BOOST_POW)),
				DamageSource.AIRBLAST, DamageTag.REFLECT).setConstantKnockback(true, startVelocity));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(startVelocity),
			startVelocity.nor().scl(HITBOX_SIZE.x / 2 / PPM)));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_BLAST, 0.0f, 2.0f)
			.setParticleVelocity(startVelocity.angleRad() + 180 * MathUtils.degRad));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (UserDataType.HITBOX.equals(fixB.getType())) {
						if (fixB.getEntity().isAlive() && ((Hitbox) fixB.getEntity()).isReflectable()) {
							fixB.getEntity().setLinearVelocity(fixB.getEntity().getLinearVelocity().scl(REFLECT_VELO_AMP).
								clamp(REFLECT_VELO_MIN, REFLECT_VELO_MAX).setAngleDeg(startVelocity.angleDeg()));
						}
					}
				}
			}
		});
		
		user.getBodyData().statusProcTime(new ProcTime.Airblast(this));
	}
}
