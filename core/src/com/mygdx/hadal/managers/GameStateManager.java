package com.mygdx.hadal.managers;

import java.util.Stack;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.states.*;

public class GameStateManager {
	
	private HadalGame app;
	
	private Stack<GameState> states;
	
	public enum State {
		SPLASH,
		TITLE,
		PLAY
	}
	
	public GameStateManager(HadalGame hadalGame) {
		this.app = hadalGame;
		this.states = new Stack<GameState>();
		this.setState(State.SPLASH);
	}
	
	public HadalGame application() {
		return app;
	}
	
	public void update(float delta) {
		states.peek().update(delta);
	}
	
	public void render() {
		states.peek().render();
	}
	
	public void dispose() {
		for (GameState gs : states) {
			gs.dispose();
		}
		states.clear();
	}
	
	public void resize(int w, int h) {
		states.peek().resize(w, h);
	}
	
	public void setState(State state) {
		if (states.size() >= 1) {
			states.pop().dispose();
		}
		states.push(getState(state));
	}
	
	public GameState getState(State state) {
		switch(state) {
		case SPLASH: return new SplashState(this);
		case TITLE: return null;
		case PLAY: return new PlayState(this);
		}
		return null;
	}

}
