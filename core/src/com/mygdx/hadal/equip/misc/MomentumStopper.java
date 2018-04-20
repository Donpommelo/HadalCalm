package com.mygdx.hadal.equip.misc;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.MeleeHitbox;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.event.FreezeBubble;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

public class MomentumStopper extends MeleeWeapon {
	
	private final static String name = "Momentum Stopper";
	private final static float swingCd = 0.1f;
	private final static float windup = 0.0f;
	private final static float backSwing = 0.5f;
	private final static int hitboxSize = 500;
	private final static int swingArc = 500;
	private final static float momentum = 0.0f;
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(Schmuck user, PlayState state, Vector2 startAngle, final float x, final float y, final short filter) {

			MeleeHitbox hbox = new MeleeHitbox(state, x, y, hitboxSize, swingArc, swingCd, backSwing, startAngle, 
					new Vector2(0, 0), (short) 0, user);
			
			final Schmuck user2 = user;
			
			hbox.setUserData(new HitboxData(state, hbox) {
						
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY) || fixB.getType().equals(UserDataTypes.HITBOX)) {
							
							Vector2 velo = new Vector2(
									fixB.getEntity().getBody().getLinearVelocity().x, 
									fixB.getEntity().getBody().getLinearVelocity().y);
							((Player) user2).getMomentums().addLast(velo);
							fixB.getEntity().getBody().setLinearVelocity(new Vector2(0, 0));
							
							new FreezeBubble(state, hitboxSize / 2, swingArc / 2, (int)x, (int)y, 1.0f, filter);
						}
					}
				}
			});		
		}
		
	};
	
	public MomentumStopper(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}
}
