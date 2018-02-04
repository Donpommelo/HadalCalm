package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Lifesteal;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class RingoftheLamprey extends Artifact {

	static String name = "Ring of the Lamprey";
	static String descr = "5% Lifesteal, -20 Max Hp";
	static String descrLong = "";
	public static final int statusNum = 2;
	
	public RingoftheLamprey() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new Lifesteal(state, world, camera, rays, 0.05f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 0, -25, b, b, 50);
		return enchantment;
	}
}
