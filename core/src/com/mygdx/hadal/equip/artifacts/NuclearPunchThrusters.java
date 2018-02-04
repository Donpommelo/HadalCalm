package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class NuclearPunchThrusters extends Artifact {

	static String name = "Nuclear Punch-THrusters";
	static String descr = "+50% Knockback";
	static String descrLong = "";
	public static final int statusNum = 1;
	
	public NuclearPunchThrusters() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 23, 0.5f, b, b, 50);
		return enchantment;
	}
}
