package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class NiceShoes extends Artifact {

	static String name = "Nice Shoes";
	static String descr = "+30% Ground Speed";
	static String descrLong = "";
	public static final int statusNum = 2;
	
	public NiceShoes() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 4, 0.30f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 6, 0.50f, b, b, 50);
		return enchantment;
	}
}
