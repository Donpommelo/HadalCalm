package com.mygdx.hadal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.server.KryoServer;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.client.KryoClient;

/**
 * HadalGame is the game. This is created upon launching the game. It delegates the rendering + updating logic to the GamestateManager.
 * @author Zachary Tu
 *
 */
public class HadalGame extends ApplicationAdapter {
	
	public static int CONFIG_WIDTH;
	public static int CONFIG_HEIGHT;
	
	//Camera and Spritebatch. This is pretty standard stuff. camera follows player. hud is for menu/scene2d stuff
	private OrthographicCamera camera, hud;
	public static FitViewport viewportCamera, viewportUI;

	//This is the batch used to render stuff
	private SpriteBatch batch;

	//This is the Gamestate Manager. It manages the current game state.
	private GameStateManager gsm;
	
    public static AssetManager assetManager;
    public static MusicPlayer musicPlayer;

    //Client and server for networking are static fields in the main game
    public static KryoClient client;
    public static KryoServer server;
    
    public static BitmapFont SYSTEM_FONT_UI, SYSTEM_FONT_SPRITE;
    public static Color DEFAULT_TEXT_COLOR;
 
    private final static int DEFAULT_WIDTH = 1280;
	private final static int DEFAULT_HEIGHT = 720;
	
	//currentMenu is whatever stage is being drawn in the current gameState
    private Stage currentMenu;
    
    //this is the rate at which the screen fades from/to black.
  	private final static float defaultFadeInSpeed = -0.02f;
  	private final static float defaultFadeOutSpeed = 0.02f;
  	
  	//This is the how faded the black screen is. (starts off black)
  	protected float fadeLevel = 1.0f;
  			
  	//This is how much the fade changes every engine tick (starts out fading in)
  	protected float fadeDelta = defaultFadeInSpeed;
  	
  	private Runnable runAfterTransition;
  	
    private Texture black;
    
    private GLProfiler profiler;
    
	/**
	 * This creates a game, setting up the sprite batch to render things and the main game camera.
	 * This also initializes the Gamestate Manager.
	 */
	@Override
	public void create() {
		
		CONFIG_WIDTH = DEFAULT_WIDTH;
		CONFIG_HEIGHT = DEFAULT_HEIGHT;
		batch = new SpriteBatch();
		
	    camera = new OrthographicCamera(CONFIG_WIDTH, CONFIG_HEIGHT);
	    hud = new OrthographicCamera(CONFIG_WIDTH, CONFIG_HEIGHT);
	    viewportCamera = new FitViewport(CONFIG_WIDTH, CONFIG_HEIGHT, camera);
	    viewportCamera.apply();	    
	    viewportUI = new FitViewport(CONFIG_WIDTH, CONFIG_HEIGHT, hud);
	    viewportUI.apply();
	    
	    hud.zoom = 1;
	    
	    assetManager = new AssetManager(new InternalFileHandleResolver());
	    musicPlayer = new MusicPlayer();
	    
        gsm = new GameStateManager(this);
		gsm.addState(State.SPLASH, null);
		
		client = new KryoClient(gsm);
		server = new KryoServer(gsm);
		
		black = new Texture(Gdx.files.internal("black.png"));
		
		profiler = new GLProfiler(Gdx.graphics);
		profiler.enable();
	}
	
	/**
	 * This is run every engine tick according to libgdx.
	 * Here, we tell the gsm to tell the current state of the elapsed time.
	 */
	@Override
	public void render() {
		
		gsm.update(Gdx.graphics.getDeltaTime());
		currentMenu.act();

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		viewportCamera.apply();
		gsm.render(Gdx.graphics.getDeltaTime());
		
		currentMenu.getViewport().apply();
		currentMenu.getBatch().setColor(1, 1, 1, 1);
		currentMenu.draw();
		
		//Render fade transitions
		if (fadeLevel > 0) {
			batch.setProjectionMatrix(hud.combined);
			batch.begin();
			
			batch.setColor(1f, 1f, 1f, fadeLevel);
			batch.draw(black, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
			batch.setColor(1f, 1f, 1f, 1);
			
			batch.end();
		}
		
		//If we are in the delay period of a transition, decrement the delay
		if (fadeDelta < 0f) {
			
			//If we are fading in and not done yet, decrease fade.
			fadeLevel += fadeDelta;
			
			//If we just finished fading in, set fade to 0
			if (fadeLevel < 0f) {
				fadeLevel = 0f;
				fadeDelta = 0;
			}
		} else if (fadeDelta > 0f) {
			
			//If we are fading out and not done yet, increase fade.
			fadeLevel += fadeDelta;
			
			//If we just finished fading out, set fade to 1 and do a transition
			if (fadeLevel >= 1f) {
				fadeLevel = 1f;
				fadeDelta = 0;
				if (runAfterTransition != null) {
					Gdx.app.postRunnable(runAfterTransition);
				}
			}
		}
				
//		System.out.println(
//	            "  Drawcalls: " + profiler.getDrawCalls() +
//	            ", Calls: " + profiler.getCalls() +
//	            ", TextureBindings: " + profiler.getTextureBindings() +
//	            ", ShaderSwitches:  " + profiler.getShaderSwitches() +
//	            ", vertexCount: " + profiler.getVertexCount().value);
		profiler.reset();
	}
	
	/**
	 * Run when the window resizes. We adjust each camera.
	 */
	@Override
	public void resize(int width, int height) {
		viewportCamera.update(width, height, true);
		viewportUI.update(width, height, true);
		
		gsm.resize(width, height);
	}
	
	/**
	 * This is run when we add a new menu. (Usually when a new stage is added by a state)
	 * We want to always have the player touching the new menu
	 * @param menu
	 */
	public void newMenu(Stage menu) {
		currentMenu = menu;
		currentMenu.setViewport(viewportUI);
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
		assetManager.dispose();
	}
	
	public void fadeOut() {	fadeDelta = defaultFadeOutSpeed; }
	
	public void fadeIn() { fadeDelta = defaultFadeInSpeed; }
	
	public void setFadeLevel(float fadeLevel) { this.fadeLevel = fadeLevel; }
	
	public void setRunAfterTransition(Runnable runAfterTransition) { this.runAfterTransition = runAfterTransition; }
	
	/**
	 * This is used to set game framerate dynamically.
	 * This is extended in the desktop launcher to expose the config
	 */
	public void setFrameRate(int framerate) {};
	
	public OrthographicCamera getHud() { return hud; }
	
	public OrthographicCamera getCamera() { return camera; }

	public SpriteBatch getBatch() { return batch; }
}
