package com.mygdx.hadal.statuses.artifact;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class EnvenomedEarthStatus extends Status {

	private static String name = "Envenomed";
	
	private final static int poisonRadius = 100;
	private final static float poisonDamage = 30/60f;
	private final static float poisonDuration = 3.0f;
	
	public EnvenomedEarthStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
	}
	
	@Override
	public void onKill(BodyData vic) {
		new Poison(state, world, camera, rays, poisonRadius, poisonRadius,
				(int)(vic.getSchmuck().getBody().getPosition().x * PPM), 
				(int)(vic.getSchmuck().getBody().getPosition().y * PPM), 
				poisonDamage, poisonDuration, perp.getSchmuck());
	}
}
