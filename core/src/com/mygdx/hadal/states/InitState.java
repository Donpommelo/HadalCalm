package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.LoadingBackdrop;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * This is the very first called state of the game. This is pretty much a loading screen where the AssetManager is loaded.
 * After loading is complete, we automatically transition into the Title Screen.
 * @author Zachary Tu
 *
 */
public class InitState extends GameState {

	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public InitState(final GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				//Our only actor here is a loading screen image
				addActor(new LoadingBackdrop());
			}
		};
		app.newMenu(stage);
		loadAssets();
	}

	/**
	 * This is where we load all of the assets of the game. Done upon this state being shown.
	 */
	private void loadAssets() {		
		HadalGame.SYSTEM_FONT_UI = new BitmapFont(Gdx.files.internal(AssetList.FIXEDSYS_FONT.toString()), false);
		HadalGame.SYSTEM_FONT_SPRITE = new BitmapFont();
		HadalGame.DEFAULT_TEXT_COLOR = Color.WHITE;
		
		HadalGame.SYSTEM_FONT_UI.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		HadalGame.SYSTEM_FONT_SPRITE.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		for (AssetList asset: AssetList.values()) {
            if (asset.getType() != null) {
            	HadalGame.assetManager.load(asset.toString(), asset.getType());
            }
        }
	}

	/**
	 * 
	 */
	@Override
	public void update(float delta) {
		if (HadalGame.assetManager.update()) {
			
			//If we are done loading, do to title state and set up gsm assets (static atlases and stuff like that)
			getGsm().loadAssets();
			getGsm().addState(State.TITLE, InitState.class);
			gsm.getApp().setFadeLevel(1.0f);
			gsm.getApp().fadeIn();
		}
	}

	@Override
	public void render(float delta) {}

	@Override
	public void transitionState() {}
	
	@Override
	public void dispose() {	stage.dispose(); }
}
