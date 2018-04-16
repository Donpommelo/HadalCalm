package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.EnvenomedEarthStatus;

import box2dLight.RayHandler;

public class EnvenomedEarth extends Artifact {

	private final static String name = "Envenomed Earth";
	private final static String descr = "Create poison cloud on kill.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public EnvenomedEarth() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new EnvenomedEarthStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
