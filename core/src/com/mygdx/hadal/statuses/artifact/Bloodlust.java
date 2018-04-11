package com.mygdx.hadal.statuses.artifact;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

import box2dLight.RayHandler;

public class Bloodlust extends Status {

	private static String name = "Bloodlust";
	private static final float cliprefill = 0.50f;
	
	public Bloodlust(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
	}
	
	@Override
	public void onKill(BodyData vic) {
		if (this.vic instanceof PlayerBodyData) {
			if (((PlayerBodyData)this.vic).getCurrentTool() instanceof RangedWeapon) {
				RangedWeapon weapon = (RangedWeapon)((PlayerBodyData)this.vic).getCurrentTool();
				weapon.gainAmmo((int)(weapon.getClipSize() * cliprefill));
			}
		}
	}
	
}
