package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Spring;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class SpringLoader extends ActiveItem {

	private final static String name = "Spring Loader";
	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 3.0f;
	
	private final static int springRadius = 64;
	private final static float springPower = 60.0f;
	private final static float springDuration = 6.0f;
	
	public SpringLoader(Schmuck user) {
		super(user, name, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		
		Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
		state.camera.unproject(mouse);
		
		new Spring(state, springRadius, springRadius,
				(int)(mouse.x), (int)(mouse.y), 
				new Vector2(0, springPower), springDuration);
	}

}
