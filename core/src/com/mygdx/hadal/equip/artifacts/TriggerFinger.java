package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class TriggerFinger extends Artifact {

	static String name = "Trigger Finger";
	static String descr = "+30% Ranged Attack Speed";
	static String descrLong = "";
	public static final int statusNum = 1;
	
	public TriggerFinger() {
		super(name, descr, descrLong, statusNum);
	}

	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		enchantment[0] = new StatChangeStatus(state, world, camera, rays, 27, 0.25f, b, b, 50);
		return enchantment;
	}
}
