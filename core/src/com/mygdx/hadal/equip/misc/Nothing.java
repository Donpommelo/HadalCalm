package com.mygdx.hadal.equip.misc;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

public class Nothing extends MeleeWeapon {

	private final static String name = "Nothing";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.2f;
	private final static float momentum = 0.2f;
	
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startAngle, float x, float y, short filter) {
					
		}

	};
	
	public Nothing(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}

}
