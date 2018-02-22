package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class NuclearPunchThrusters extends Artifact {

	private final static String name = "Nuclear Punch-THrusters";
	private final static String descr = "+50% Knockback";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public NuclearPunchThrusters() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 23, 0.5f, b, b, 50);
		return enchantment;
	}
}
