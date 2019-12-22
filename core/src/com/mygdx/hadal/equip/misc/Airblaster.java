package com.mygdx.hadal.equip.misc;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxFixedToUserStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatusProcTime;
import com.mygdx.hadal.utils.Stats;

public class Airblaster extends MeleeWeapon {

	private final static String name = "Airblaster";
	private final static float swingCd = 0.4f;
	private final static float windup = 0.0f;
	private final static float baseDamage = 0.0f;
	private final static int hitboxSize = 150;
	private final static float knockback = 60.0f;
	private final static float momentum = 40.0f;
	
	public Airblaster(Schmuck user) {
		super(user, name, swingCd, windup, Sprite.MT_DEFAULT, Sprite.P_DEFAULT);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		fire(state, user, weaponVelo, user.getPosition().x * PPM, user.getPosition().y * PPM, faction);
		user.recoil(x, y, momentum * (1 + shooter.getStat(Stats.BOOST_RECOIL)));
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, final Vector2 startVelocity, float x, float y, short filter) {
		Hitbox hbox = new Hitbox(state, x, y, 
				(int) (hitboxSize * (1 + user.getBodyData().getStat(Stats.BOOST_SIZE))), 
				(int) (hitboxSize * (1 + user.getBodyData().getStat(Stats.BOOST_SIZE))),
				swingCd, startVelocity, user.getHitboxfilter(), false, false, user, Sprite.IMPACT);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback * (1 + user.getBodyData().getStat(Stats.BOOST_POW)), 
				DamageTypes.AIR, DamageTypes.DEFLECT, DamageTypes.REFLECT));
		hbox.addStrategy(new HitboxFixedToUserStrategy(state, hbox, user.getBodyData(), new Vector2(), startVelocity.nor().scl(hitboxSize / 2 / PPM), false));
		
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
		
		user.getBodyData().statusProcTime(StatusProcTime.ON_AIRBLAST, user.getBodyData(), 0.0f, null, this, null);
	}
}
