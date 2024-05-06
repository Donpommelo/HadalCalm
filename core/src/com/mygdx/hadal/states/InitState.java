package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Collections;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.LoadingBackdrop;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.FadeManager;
import com.mygdx.hadal.managers.SkinManager;
import com.mygdx.hadal.managers.StateManager;

/**
 * This is the very first called state of the game. This is pretty much a loading screen where the AssetManager is loaded.
 * After loading is complete, we automatically transition into the Title Screen.
 * @author Gorfblatt Ghordelia
 */
public class InitState extends GameState {

	private LoadingBackdrop backdrop;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 */
	public InitState(HadalGame app) {
		super(app);
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				//Our only actor here is a loading screen image
				backdrop = new LoadingBackdrop();
				addActor(backdrop);
			}
		};
		app.newMenu(stage);
		loadAssets();
	}

	/**
	 * This is where we load all of the assets of the game. Done upon this state being shown.
	 */
	private void loadAssets() {		
		for (AssetList asset : AssetList.values()) {
            if (asset.getType() != null) {
            	HadalGame.assetManager.load(asset.toString(), asset.getType());
            }
        }
	}

	@Override
	public void update(float delta) {

		//we block for 17 milliseconds to attempt to maintain ~60 fps
		if (HadalGame.assetManager.update(17)) {

			//If we are done loading, go to title state and set up gsm assets (static atlases and stuff like that)
			SkinManager.loadAssets();
			StateManager.addState(app, StateManager.State.TITLE, this);
			FadeManager.setFadeLevel(1.0f);
			FadeManager.fadeIn();

			//this lets us not declare every attribute of shaders.
			ShaderProgram.pedantic = false;

			//this is necessary to prevent nested iterations from causing errors
			Collections.allocateIterators = true;
		}
	}

	@Override
	public void render(float delta) {}

	@Override
	public void dispose() {
		stage.dispose();
//		backdrop.getAtlas().dispose();
	}
}
