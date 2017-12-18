package com.mygdx.hadal.equip;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public abstract class Equipable {	
	
	public Schmuck user;
	public String name;
	public int useCd;
	public boolean reloading;
	public float reloadCd;
	
	public Equipable(Schmuck user, String name, int useCd) {
		this.user = user;
		this.name = name;
		this.useCd = useCd;
		this.reloading = false;
		this.reloadCd = 0;
	}
	
	public abstract void mouseClicked(PlayState state, BodyData shooter, short faction, int x, int y, World world, OrthographicCamera camera, RayHandler rays);
	
	public abstract void reload();
	
	public abstract String getText();
}
