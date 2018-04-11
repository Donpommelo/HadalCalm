package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.LoadingBackdrop;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * TODO: docs
 * @author Zachary Tu
 *
 */
public class InitState extends GameState {

	private Stage stage;
		
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
				addActor(new LoadingBackdrop(HadalGame.assetManager));
			}
		};
		app.newMenu(stage);
		loadAssets();
	}

	private void loadAssets() {
		
		HadalGame.SYSTEM_FONT_TITLE = new BitmapFont(Gdx.files.internal(AssetList.LEARNING_FONT.toString()), false);
		HadalGame.SYSTEM_FONT_UI = new BitmapFont(Gdx.files.internal(AssetList.FIXEDSYS_FONT.toString()), false);
		HadalGame.DEFAULT_TEXT_COLOR = Color.WHITE;
		
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
			getGsm().loadAssets();
			getGsm().addState(State.TITLE, InitState.class);
		}
	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

}
