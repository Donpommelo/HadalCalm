package com.mygdx.hadal.equip.misc;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatusProcTime;
import com.mygdx.hadal.utils.HitboxFactory;

public class Airblaster extends MeleeWeapon {

	private final static String name = "Airblaster";
	private final static float swingCd = 0.25f;
	private final static float windup = 0.0f;
	private final static float backSwing = 1.0f;
	private final static float baseDamage = 0.0f;
	private final static int hitboxSize = 300;
	private final static float knockback = 60.0f;
	private final static float momentum = 40.0f;
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startAngle, float x, float y, short filter) {
						
			Hitbox hbox = new MeleeHitbox(state, x, y, 
					(int) (hitboxSize * (1 + user.getBodyData().getBonusAirblastSize())), 
					(int) (hitboxSize * (1 + user.getBodyData().getBonusAirblastSize())),
					swingCd, backSwing, startAngle, 
					startAngle.nor().scl(hitboxSize / 4 / PPM), false, user.getHitboxfilter(), user);
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, 
					knockback * (1 + user.getBodyData().getBonusAirblastPower()), 
					DamageTypes.AIR, DamageTypes.DEFLECT, DamageTypes.REFLECT));
			
			user.getBodyData().statusProcTime(StatusProcTime.ON_AIRBLAST, user.getBodyData(), 0.0f, null, tool, null);
		}
	};
	
	public Airblaster(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		onSwing.makeHitbox(user, state, this, weaponVelo, 
				shooter.getSchmuck().getBody().getPosition().x * PPM, 
				shooter.getSchmuck().getBody().getPosition().y * PPM, 
				faction);
		
		user.recoil(x, y, momentum * (1 + shooter.getMeleeMomentum()) * (1 + shooter.getBonusAirblastRecoil()));
	}
}
