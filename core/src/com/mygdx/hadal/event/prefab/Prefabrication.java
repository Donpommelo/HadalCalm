package com.mygdx.hadal.event.prefab;

import com.mygdx.hadal.states.PlayState;

public abstract class Prefabrication {

	protected PlayState state;
	protected int height, width, x, y;
	
	public Prefabrication(PlayState state, int width, int height, int x, int y) {
		this(state);
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	public Prefabrication(PlayState state) {
		this.state = state;
	}
	
	public abstract void generateParts();
}
