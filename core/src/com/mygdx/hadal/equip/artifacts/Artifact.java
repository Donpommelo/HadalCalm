package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class Artifact {

	
	protected String name, descr, descrLong;
	protected Status[] enchantment;
	
	public Artifact(String name, String descr, String descrLong, int statusNum) {
		this.name = name;
		this.descr = descr;
		this.descrLong = descrLong;
		enchantment = new Status[statusNum];
	}
	
	public Status[] loadEnchantments(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData b) {
		return null;
	}

	public String getName() {
		return name;
	}

	public String getDescr() {
		return descr;
	}

	public String getDescrLong() {
		return descrLong;
	}

	public Status[] getEnchantment() {
		return enchantment;
	}
	
}
