package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class LoamskinTalisman extends Artifact {

	static String name = "Loamskin Talisman";
	static String descr = "+1 Hp Regen";
	static String descrLong = "";
	public static final int statusNum = 1;
	
	public LoamskinTalisman() {
		super(name, descr, descrLong, statusNum);
	}

	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 2, 1.0f, b, b, 50);
		return enchantment;
	}
}
