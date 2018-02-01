package com.mygdx.hadal.statuses.artifact;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class FracturePlateStatus extends Status {

	private static String name = "Fractured Plate";
	private float procCdCount;
	private float cd = 8.0f;
	
	public FracturePlateStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
		this.procCdCount = 0;
	}
	
	@Override
	public void timePassing(float delta) {
		procCdCount -= delta;
	}
	
	@Override
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
		if (damage > 0 && procCdCount <= 0) {
			procCdCount = cd;
			damage = 0;
		}
		return damage;
	}

}
