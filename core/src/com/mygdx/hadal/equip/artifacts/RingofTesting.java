package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Lifesteal;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.AnarchistCookbookStatus;

import box2dLight.RayHandler;

public class RingofTesting extends Artifact {

	private final static String name = "Ring of Testing";
	private final static String descr = "Tests Things";
	private final static String descrLong = "";
	private final static int statusNum = 4;
	
	public RingofTesting() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 29, .25f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 42, 1.f, b, b, 50);
		enchantment[2] = new Lifesteal(state, world, camera, rays, 0.05f, b, b, 50);
		enchantment[3] = new AnarchistCookbookStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
