package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.Bloodlust;

import box2dLight.RayHandler;

public class Bloodylust extends Artifact {

	private final static String name = "Bloody Lust";
	private final static String descr = "75% Reload Speed. Refill 50% clip on kill.";
	private final static String descrLong = "";
	private final static int statusNum = 2;
	
	public Bloodylust() {
		super(name, descr, descrLong, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new Bloodlust(state, world, camera, rays, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 28, -0.75f, b, b, 50);
		return enchantment;
	}
}
