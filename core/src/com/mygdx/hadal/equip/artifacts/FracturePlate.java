package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.FracturePlateStatus;

import box2dLight.RayHandler;

public class FracturePlate extends Artifact {

	private final static String name = "Fracture Plate";
	private final static String descr = "Blocks damge every 8 seconds";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public FracturePlate() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new FracturePlateStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
