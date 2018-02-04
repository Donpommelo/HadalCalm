package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class EelskinCover extends Artifact {

	static String name = "Eelskin Cover";
	static String descr = "Reduces Drag";
	static String descrLong = "";
	public static final int statusNum = 2;
	
	public EelskinCover() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 8, -0.60f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 9, -0.60f, b, b, 50);
		return enchantment;
	}
}
