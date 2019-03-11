package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.scenes.scene2d.Stage;

import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.managers.GameStateManager;

public class ClientState extends GameState {
	
	private Stage stage;
	private ClientController controller;
	
	public ClientState(GameStateManager gsm) {
		super(gsm);
		controller = new ClientController();
		Gdx.input.setInputProcessor(controller);
	}
	
	@Override
	public void update(float delta) {
		
	}

	@Override
	public void render() {

	}
	
	@Override
	public void dispose() {
		stage.dispose();		
	}

}
