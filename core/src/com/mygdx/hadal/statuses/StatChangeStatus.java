package com.mygdx.hadal.statuses;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

import box2dLight.RayHandler;

public class StatChangeStatus extends Status {

	public static String name = "Stats Changed";
	
	public int statNum;
	public double statIncrement;
	
	public StatChangeStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			int i, int stat, float amount, BodyData p,	BodyData v, int pr) {
		super(state, world, camera, rays, i, name, false, false, true, true, p, v, pr);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	public StatChangeStatus(PlayState state, World world, OrthographicCamera camera, RayHandler rays, 
			int stat, float amount, BodyData p, BodyData v, int pr) {
		super(state, world, camera, rays, 0, name, true, false, false, false, p, v, pr);
		this.statNum = stat;
		this.statIncrement = amount;
	}
	
	@Override
	public void statChanges(BodyData bodyData){
		bodyData.buffedStats[statNum] += statIncrement;
	}

}
