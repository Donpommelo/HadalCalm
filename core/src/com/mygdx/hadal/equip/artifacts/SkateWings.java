package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class SkateWings extends Artifact {

	private final static String name = "Skate Wings";
	private final static String descr = "+1 Jump, +20% Jump Power";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public SkateWings() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 10, 0.2f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 11, 1, b, b, 50);
		return enchantment;
	}
}
