package com.mygdx.hadal.statuses;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.misc.GrenadeDropTest;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class TimedStatusTest extends Status {

	private static String name = "TEST";
	private float procCdCount;
	private float procCd = .5f;

	private Equipable weapon;
	
	public TimedStatusTest(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			int i, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, i, name, false, false, true, true, p, v, pr);
		weapon = new GrenadeDropTest(perp.getSchmuck());
		this.procCdCount = 0;
	}
	
	public TimedStatusTest(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
		weapon = new GrenadeDropTest(perp.getSchmuck());
		this.procCdCount = 0;
	}
	
	public void timePassing(float delta) {
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			vic.schmuck.useToolStart(delta, weapon, (short) vic.schmuck.hitboxfilter, (int)0, (int)0, false);
			weapon.reload(delta);
		}
		procCdCount += delta;
	}
	
}
