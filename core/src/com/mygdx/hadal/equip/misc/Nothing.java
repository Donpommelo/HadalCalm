package com.mygdx.hadal.equip.misc;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.HitboxFactory;

import box2dLight.RayHandler;

public class Nothing extends MeleeWeapon {

	private final static String name = "Nothing";
	private final static float swingCd = 0.5f;
	private final static float windup = 0.2f;
	private final static float momentum = 0.2f;
	
	
	private final static HitboxFactory onSwing = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Vector2 startAngle, float x, float y, short filter, World world,
				OrthographicCamera camera, RayHandler rays) {
					
		}

	};
	
	public Nothing(Schmuck user) {
		super(user, name, swingCd, windup, momentum, onSwing);
	}

}
