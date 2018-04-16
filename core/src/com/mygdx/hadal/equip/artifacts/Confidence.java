package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.ConfidenceStatus;

import box2dLight.RayHandler;

public class Confidence extends Artifact {

	private final static String name = "Confidence";
	private final static String descr = "+50% damage at Max Hp.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public Confidence() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new ConfidenceStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
