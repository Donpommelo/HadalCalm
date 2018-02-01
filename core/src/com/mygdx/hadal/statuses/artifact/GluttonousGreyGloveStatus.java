package com.mygdx.hadal.statuses.artifact;

import java.util.Arrays;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class GluttonousGreyGloveStatus extends Status {

	private static String name = "Gloved Gluttonously";
	private static final float lifesteal = 0.05f;
	
	public GluttonousGreyGloveStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
	}
	
	@Override
	public void onKill(BodyData vic) {
		this.vic.regainHp(lifesteal * vic.getMaxHp(), this.vic, true, DamageTypes.LIFESTEAL);
	}
	
	@Override
	public float onHeal(float damage, BodyData perp, DamageTypes... tags) {
		if (Arrays.asList(tags).contains(DamageTypes.MEDPAK)) {
			return 0;
		}
		return damage;
	}
	
}
