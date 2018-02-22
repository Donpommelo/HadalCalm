package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.AnarchistCookbookStatus;

import box2dLight.RayHandler;

public class AnarchistsCookbook extends Artifact {

	private final static String name = "Anarchist's Cookbook";
	private final static String descr = "Explosions";
	private final static String descrLong = "";
	private final static int statusNum = 1;
	
	public AnarchistsCookbook() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new AnarchistCookbookStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
