package com.mygdx.hadal.equip.misc;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageConstant;
import com.mygdx.hadal.strategies.hitbox.FixedToUser;
import com.mygdx.hadal.utils.Stats;

public class Airblaster extends MeleeWeapon {

	private final static float swingCd = 0.4f;
	private final static float windup = 0.0f;
	private final static float baseDamage = 0.0f;
	private final static Vector2 hitboxSize = new Vector2(150, 150);
	private final static float knockback = 60.0f;
	private final static float momentum = 40.0f;
	
	public Airblaster(Schmuck user) {
		super(user, swingCd, windup, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		fire(state, user, user.getPixelPosition(), weaponVelo, faction);
		user.recoil(mouseLocation, momentum * (1 + shooter.getStat(Stats.BOOST_RECOIL)));
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, final Vector2 startVelocity, short filter) {
		SoundEffect.AIRBLAST.playUniversal(state, startPosition, 1.0f);
		
		Hitbox hbox = new Hitbox(state, startPosition, new Vector2(hitboxSize).scl(1 + user.getBodyData().getStat(Stats.BOOST_SIZE)), swingCd, new Vector2(), user.getHitboxfilter(), true, false, user, Sprite.IMPACT);
		hbox.makeUnreflectable();
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageConstant(state, hbox, user.getBodyData(), baseDamage, new Vector2(startVelocity).nor().scl(knockback * (1 + user.getBodyData().getStat(Stats.BOOST_POW))),
				DamageTypes.DEFLECT, DamageTypes.REFLECT));
		hbox.addStrategy(new FixedToUser(state, hbox, user.getBodyData(), new Vector2(), startVelocity.nor().scl(hitboxSize.x / 2 / PPM), false));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getType().equals(UserDataTypes.HITBOX)){
						if (((Hitbox)fixB.getEntity()).isAlive()) {
							((Hitbox)fixB.getEntity()).setLinearVelocity(((Hitbox)fixB.getEntity()).getLinearVelocity().setAngle(startVelocity.angle()));
						}
					}
				}
			}
		});
		
		user.getBodyData().statusProcTime(new ProcTime.Airblast(this));
	}
}
