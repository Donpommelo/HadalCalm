package com.mygdx.hadal.statuses.artifact;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class RageGlandStatus extends Status {

	private static String name = "Enraged Glands";
	
	private final float damageFloor = 5;
	private final float duration = 3.0f;
	
	public RageGlandStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
	}
	
	@Override
	public float onReceiveDamage(float damage, BodyData perp, DamageTypes... tags) {
		if (damage > damageFloor) {
			vic.addStatus(new StatChangeStatus(state, world, camera, rays, duration, 4, 0.50f, perp, vic, priority));
			vic.addStatus(new StatChangeStatus(state, world, camera, rays, duration, 5, 0.50f, perp, vic, priority));
			vic.addStatus(new StatChangeStatus(state, world, camera, rays, duration, 21, 0.25f, perp, vic, priority));
			vic.addStatus(new StatChangeStatus(state, world, camera, rays, duration, 23, 0.25f, perp, vic, priority));
		}
		return damage;
	}

}
