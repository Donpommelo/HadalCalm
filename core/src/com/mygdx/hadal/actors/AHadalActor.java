package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
* Basic template Actor class for game.
* Used for ui + menus and stuff
 * @author Blajandro Blirklard
*/
public abstract class AHadalActor extends Actor {

	public AHadalActor() {}
	
	public AHadalActor(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public AHadalActor(int x, int y, int width, int height) {
		this(x, y);
		setWidth(width);
		setHeight(height);
	}
}