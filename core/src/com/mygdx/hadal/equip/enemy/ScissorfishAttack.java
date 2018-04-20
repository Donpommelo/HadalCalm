package com.mygdx.hadal.equip.enemy;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class ScissorfishAttack extends MeleeWeapon {

	private final static String name = "Scissorfish Scissor";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.5f;
	private final static float backSwing = 1.0f;
	private final static float baseDamage = 12.0f;
	private final static int hitboxSize = 200;
	private final static int swingArc = 100;
	private final static float knockback = 22.5f;
	private final static float momentum = 3.0f;
	
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startAngle, float x, float y, short filter) {
			
			MeleeHitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, startAngle, 
					new Vector2(0, 0), filter, user);
			
			hbox.setUserData(new HitboxData(state, hbox) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage, this.hbox.getBody().getLinearVelocity().nor().scl(knockback), 
								user.getBodyData(), true, DamageTypes.MELEE);
					}
				}
				
			});
		}

	};
	
	public ScissorfishAttack(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}

}
