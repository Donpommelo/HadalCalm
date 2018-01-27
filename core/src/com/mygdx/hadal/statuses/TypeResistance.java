package com.mygdx.hadal.statuses;

import java.util.Arrays;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class TypeResistance extends Status {

	private static String name = "Damage Resistance";
	private float power;
	private DamageTypes resisted;
	
	public TypeResistance(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			int i, float power, DamageTypes resisted, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, i, name, false, false, true, true, p, v, pr);
		this.resisted = resisted;
		this.power = power;
	}
	
	public TypeResistance(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			float power, DamageTypes resisted, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
		this.resisted = resisted;
		this.power = power;
	}
	
	@Override
	public float onDealDamage(float damage, BodyData vic, DamageTypes... tags) {
		if (Arrays.asList(tags).contains(resisted)) {
			damage *= power;
		}
		return damage;
	}

}
