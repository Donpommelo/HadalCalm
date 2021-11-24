package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
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
 * @author Gorfblatt Ghordelia
 */
public class InitState extends GameState {

	private LoadingBackdrop backdrop;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 */
	public InitState(final GameStateManager gsm) {
		super(gsm);
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
		HadalGame.FONT_UI = new BitmapFont(Gdx.files.internal(AssetList.FIXEDSYS_FONT.toString()), false);
		HadalGame.FONT_UI.getData().markupEnabled = true;
		HadalGame.FONT_UI_SKIN = new BitmapFont(Gdx.files.internal(AssetList.FIXEDSYS_FONT.toString()), false);
		HadalGame.FONT_UI_SKIN.getData().markupEnabled = true;
		HadalGame.FONT_UI_ALT = new BitmapFont(Gdx.files.internal(AssetList.VERDANA_FONT.toString()), false);
		HadalGame.FONT_UI_ALT.getData().markupEnabled = true;
		HadalGame.FONT_SPRITE = new BitmapFont();

		HadalGame.FONT_UI.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		HadalGame.FONT_SPRITE.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		
		for (AssetList asset: AssetList.values()) {
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
			gsm.loadAssets();
			gsm.addState(State.TITLE, this);
			gsm.getApp().setFadeLevel(1.0f);
			gsm.getApp().fadeIn();
		}
	}

	@Override
	public void render(float delta) {}

	@Override
	public void dispose() {
		backdrop.getAtlas().dispose();
		stage.dispose(); 
	}
}
