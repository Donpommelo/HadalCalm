package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class ImprovedHovering extends Artifact {

	static String name = "Improved Hovering";
	static String descr = "+25% Hovering Power and -25% Hovering Cost";
	static String descrLong = "";
	public Status[] enchantment = new Status[2];
	
	public ImprovedHovering() {
		super(name, descr, descrLong);
	}

	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 12, 0.25f, b, b, 50);
		enchantment[1] = new StatChangeStatus(state, world, camera, rays, 13, -0.25f, b, b, 50);
		return enchantment;
	}
}
