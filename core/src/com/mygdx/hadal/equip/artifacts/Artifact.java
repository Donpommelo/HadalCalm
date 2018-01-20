package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class Artifact {

	
	public String name, descr, descrLong;
	public Status[] enchantment;
	
	public Artifact(String name, String descr, String descrLong, int statusNum) {
		this.name = name;
		this.descr = descr;
		this.descrLong = descrLong;
		enchantment = new Status[statusNum];
	}
	
	public Status[] getEnchantment(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		return null;
	}
}
