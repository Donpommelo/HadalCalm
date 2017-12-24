package com.mygdx.hadal.event;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class EquipPickupRandom extends EquipPickup {

	public EquipPickupRandom(PlayState state, World world, OrthographicCamera camera, RayHandler rays, int width,
			int height, int x, int y) {
		super(state, world, camera, rays, width, height, x, y, (int)(Math.random() * EquipPickup.numWeapons));
	}

}
