package com.mygdx.hadal.statuses;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class Lifesteal extends Status {

	private static String name = "Lifesteal";
	private float power;

	public Lifesteal(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			int i, float amount, BodyData p,	BodyData v, int pr) {
		super(state, world, camera, rays, i, name, false, false, true, true, p, v, pr);
		this.power = amount;
	}
	
	public Lifesteal(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			float amount, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
		this.power = amount;
	}
	
	public float onDealDamage(float damage, BodyData vic) {

		perp.regainHp(power * damage);
		
		return damage;
	}
	
}
