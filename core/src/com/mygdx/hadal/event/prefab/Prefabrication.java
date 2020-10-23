package com.mygdx.hadal.event.prefab;

import com.mygdx.hadal.states.PlayState;

import java.util.ArrayList;

/**
 * A Prefabrication is a shortcut for a collection of pre-connected events.
 * @author Zoskbach Zilyde
 */
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
	
	/**
	 * This is run when the prefab is read.
	 */
	public abstract void generateParts();
	
	/**
	 * This returns a list of ids of the events that make up the prefab that should be moved if the prefab is attached to a move point.
	 */
	public ArrayList<String> getConnectedEvents() { return new ArrayList<>(); }
}
