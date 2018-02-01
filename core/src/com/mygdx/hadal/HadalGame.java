package com.mygdx.hadal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.AssetList;

/**
 * HadalGame is the game. This is created upon launching the game. It delegates the rendering + updating logic to the GamestateManager.
 * @author Zachary Tu
 *
 */
public class HadalGame extends ApplicationAdapter {
	
	//Name of the game. Currently unused.
	public static final String TITLE = "Hadal Panic";
	
	public static int CONFIG_WIDTH;
	public static int CONFIG_HEIGHT;
	
	//The main camera scales to the viewport size scaled to this for some reason.
	//TODO: replace this with a constant aspect ratio?
	public final static float BOX2DSCALE = 1.0f;
//	private final float SCALE = 0.25f;
	
	//Camera and Spritebatch. This is pretty standard stuff. camera follows player. hud is for menu/scene2d stuff
	private OrthographicCamera camera, sprite, hud;
	private SpriteBatch batch;

	//This is the Gamestate Manager. It manages the current game state.
	private GameStateManager gsm;
	
    public static AssetManager assetManager;
    public static FitViewport viewportCamera, viewportSprite;

    public static BitmapFont SYSTEM_FONT_TITLE, SYSTEM_FONT_TEXT;
    public static Color DEFAULT_TEXT_COLOR;
 
    private static int DEFAULT_WIDTH = 1080;
	private static int DEFAULT_HEIGHT = 720;
    public Stage currentMenu;
    
	/**
	 * This creates a game, setting up the sprite batch to render things and the main game camera.
	 * This also initializes the Gamestate Manager.
	 */
	@Override
	public void create() {
		CONFIG_WIDTH = DEFAULT_WIDTH;
		CONFIG_HEIGHT = DEFAULT_HEIGHT;
		batch = new SpriteBatch();
		
		camera = new OrthographicCamera(CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE);
		camera.setToOrtho(false, CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE);
		
	    sprite = new OrthographicCamera(CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE);
	    sprite.setToOrtho(false, CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE);
	    	    
		viewportCamera = new FitViewport(CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE, camera);
	    viewportCamera.apply();
		
	    viewportSprite = new FitViewport(CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE, sprite);
	    viewportSprite.apply();
	    
	    hud = new OrthographicCamera(CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE);
	    hud.setToOrtho(false, CONFIG_WIDTH * BOX2DSCALE, CONFIG_HEIGHT * BOX2DSCALE);
	    
	    assetManager = new AssetManager(new InternalFileHandleResolver());
        loadAssets();
	       
		currentMenu = new Stage();

		gsm = new GameStateManager(this);
		
	}
	
	public void loadAssets() {
		
		SYSTEM_FONT_TITLE = new BitmapFont(Gdx.files.internal(AssetList.LEARNING_FONT.toString()), false);
		SYSTEM_FONT_TEXT = new BitmapFont(Gdx.files.internal(AssetList.BUTLER_FONT.toString()), false);
		DEFAULT_TEXT_COLOR = Color.BLACK;
		
		for (AssetList asset: AssetList.values()) {
            if (asset.getType() != null) {
                assetManager.load(asset.toString(), asset.getType());
            }
        }

        assetManager.finishLoading();
	}

	/**
	 * This is run every engine tick according to libgdx.
	 * Here, we tell the gsm to tell the current state of the elapsed time.
	 */
	@Override
	public void render() {
		
		gsm.update(Gdx.graphics.getDeltaTime());
		currentMenu.act();

		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		gsm.render();
		
		currentMenu.draw();
	}
	
	/**
	 * Run when the window resizes. We tell the gsm to delegate that update to the current state.
	 */
	@Override
	public void resize (int width, int height) {
				
		gsm.resize((int)(width * BOX2DSCALE), (int)(height * BOX2DSCALE));

		viewportCamera.update((int)(width * BOX2DSCALE), (int)(height * BOX2DSCALE), true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		viewportCamera.apply();
		
		viewportSprite.update((int)(width * BOX2DSCALE), (int)(height * BOX2DSCALE), true);
        sprite.position.set(sprite.viewportWidth / 2, sprite.viewportHeight / 2, 0);
		viewportSprite.apply();

		currentMenu.getViewport().update(width, height);
		
		CONFIG_WIDTH = width;
		CONFIG_HEIGHT = height;
	}
	
	public void newMenu(Stage menu) {
		currentMenu = menu;
		Gdx.input.setInputProcessor(currentMenu);
	}
	
	/**
	 * Upon deleting, this is run. Again, we delegate to the gsm.
	 * We also need to dispose of anything else here. (such as the batch)
	 */
	@Override
	public void dispose () {
		gsm.dispose();
		batch.dispose();
	}
	
	/**
	 * Getter for the main game camera
	 * @return: the camera
	 */
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	/**
	 * Getter for the hud camera
	 * @return: the camera
	 */
	public OrthographicCamera getHud() {
		return hud;
	}
	
	public OrthographicCamera getSprite() {
		return sprite;
	}
	
	/**
	 * Getter for the main game sprite batch
	 * @return: the batch
	 */
	public SpriteBatch getBatch() {
		return batch;
	}
}
