package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class RootBoots extends Artifact {

	static String name = "Root-Boots";
	static String descr = "+75% Knockback Resistance";
	static String descrLong = "";
	public Status[] enchantment = new Status[1];
	
	public RootBoots() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 24, 0.75f, b, b, 50);
		return enchantment;
	}
}
