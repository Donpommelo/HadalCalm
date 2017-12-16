package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public abstract class Equipable {	
	
	public int useCd;
	
	public Equipable(int useCd) {
		this.useCd = useCd;
	}
	
	public abstract void mouseClicked(PlayState state, BodyData shooter, int x, int y, World world, OrthographicCamera camera, RayHandler rays);
}
