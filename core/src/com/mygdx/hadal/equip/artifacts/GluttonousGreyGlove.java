package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.artifact.GluttonousGreyGloveStatus;

import box2dLight.RayHandler;

public class GluttonousGreyGlove extends Artifact {

	static String name = "Gluttonous Grey Glove";
	static String descr = "Heal on Kill. No longer heal from medpaks.";
	static String descrLong = "";
	public static final int statusNum = 1;
	
	public GluttonousGreyGlove() {
		super(name, descr, descrLong, statusNum);
	}

	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new GluttonousGreyGloveStatus(state, world, camera, rays, b, b, 50);
		return enchantment;
	}
}
