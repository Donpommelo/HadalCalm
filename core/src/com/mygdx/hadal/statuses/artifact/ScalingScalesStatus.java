package com.mygdx.hadal.statuses.artifact;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class ScalingScalesStatus extends Status {

	private static String name = "Scaling";
	
	public ScalingScalesStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
	}
}