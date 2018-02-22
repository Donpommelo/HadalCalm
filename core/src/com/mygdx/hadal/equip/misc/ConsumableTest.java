package com.mygdx.hadal.equip.misc;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Consumable;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class ConsumableTest extends Consumable {

	private final static String name = "Test";
	private final static float useCd = 0.25f;
	private final static float useDelay = 0.0f;
	private final static int charges = 3;
	
	public ConsumableTest(Schmuck user) {
		super(user, name, useCd, useDelay, charges);
	}
	
	@Override
	public void execute(PlayState state, BodyData bodyData, World world, OrthographicCamera camera, RayHandler rays) {
		
		
		
		super.execute(state, bodyData, world, camera, rays);
	}
}
