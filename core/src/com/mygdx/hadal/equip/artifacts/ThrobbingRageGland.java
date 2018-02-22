package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.RageGlandStatus;

import box2dLight.RayHandler;

public class ThrobbingRageGland extends Artifact {

	private final static String name = "Throbbing Rage Gland";
	private final static String descr = "Temporarily boosts speed and damage when taking damage.";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public ThrobbingRageGland() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new RageGlandStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
