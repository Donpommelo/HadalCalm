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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.client.KryoClient;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.server.KryoServer;
import com.mygdx.hadal.utils.UPNPUtil;
import io.socket.client.Socket;

/**
 * HadalGame is the game. This is created upon launching the game.
 * It delegates the rendering + updating logic to the GameStateManager.
 * @author Dincubus Doncubus
 */
public class HadalGame extends ApplicationAdapter {
	
	//screen dimensions
	public static final float CONFIG_WIDTH = 1280.0f;
	public static final float CONFIG_HEIGHT = 720.0f;
	
	//this is the game's version. This must match between client and host to connect.
	public static final String VERSION = "1.0.8b";

	//version url takes player to patch notes page when version is clicked in title screen
	public static final String VERSION_URL = "https://donpommelo.itch.io/hadal-calm/devlog/583408/108b";

	//this is the rate at which the screen fades from/to black.
	private static final float DEFAULT_FADE_IN_SPEED = -2.0f;
	private static final float DEFAULT_FADE_OUT_SPEED = 2.0f;

	public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;

	//Game cameras and respective viewports. camera follows player. hud is for menu/scene2d stuff
	private OrthographicCamera camera, hud;
	public static FitViewport viewportCamera, viewportUI;

	//This is the batch used to render stuff
	private SpriteBatch batch;

	//This is the Gamestate Manager. It manages the current stack of game states.
	private GameStateManager gsm;
	
	//Assetmanager loads the assets of the game.
    public static AssetManager assetManager;
    
    //musicPlayer manages the background music of the game.
    public static MusicPlayer musicPlayer;

    //Client and server for networking are static fields in the main game
    public static KryoClient client;
    public static KryoServer server;

    //socket is used to connect to matchmaking server
	public static Socket socket;

	//FONT_UI is used for most ui, FONT_UI_ALT is used for things like message window and kill messages
	//FONT_SPRITE labels sprites in the world. Its scale is always 1.0f and should be considered placeholder
	public static BitmapFont FONT_UI, FONT_UI_SKIN, FONT_UI_ALT, FONT_SPRITE;

	//currentMenu is whatever stage is being drawn in the current gameState
    private Stage currentMenu;
    
  	//This is the how faded the black screen is. (starts off black)
  	protected float fadeLevel = 1.0f;
  	
  	//Amount of delay before fade transition occurs
  	protected float fadeDelay = 0.0f;

  	//if set to true, we jump right into transition without fading in/out (set by making fadeDelta 0 in fadeSpecificSpeed())
  	private boolean skipFade;

  	//This is how much the fade changes every engine tick (starts out fading in)
  	protected float fadeDelta = DEFAULT_FADE_IN_SPEED;
  	
  	//this is a runnable that will run when the game finishes a transition, usually to another state.
  	private Runnable runAfterTransition;
  	
  	//this is a black texture used for fading in/out transitions.
    private Texture black;

	/**
	 * This creates a game, setting up the sprite batch to render things and the main game camera.
	 * This also initializes the Gamestate Manager.
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();
		
	    camera = new OrthographicCamera(CONFIG_WIDTH, CONFIG_HEIGHT);
	    hud = new OrthographicCamera(CONFIG_WIDTH, CONFIG_HEIGHT);
	    viewportCamera = new FitViewport(CONFIG_WIDTH, CONFIG_HEIGHT, camera);
	    viewportUI = new FitViewport(CONFIG_WIDTH, CONFIG_HEIGHT, hud);
	    
	    assetManager = new AssetManager(new InternalFileHandleResolver());

		gsm = new GameStateManager(this);
		gsm.addState(State.SPLASH, null);

		//enable upnp for both tcp and udp
		if (gsm.getSetting().isEnableUPNP()) {
			UPNPUtil.upnp("TCP", "hadal-upnp-tcp", gsm.getSetting().getPortNumber());
			UPNPUtil.upnp("UDP", "hadal-upnp-udp", gsm.getSetting().getPortNumber());
		}

		client = new KryoClient(gsm);
		server = new KryoServer(gsm);
		
	    musicPlayer = new MusicPlayer(gsm);

		black = new Texture(Gdx.files.internal("black.png"));
	}
	
	/**
	 * This is run every engine tick according to libgdx.
	 * Here, the gsm to tells the current state the elapsed time for game processing purposes.
	 */
	@Override
	public void render() {

		float delta = Gdx.graphics.getDeltaTime();
		
		//update the state, update the ui, render the state, then render the ui.
		gsm.update(delta);
		currentMenu.act(delta);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewportCamera.apply();
		gsm.render(delta);
		
		currentMenu.getViewport().apply();
		currentMenu.getBatch().setColor(1.0f, 1.0f, 1.0f, 1.0f);
		currentMenu.draw();
		
		//Render the black image used for fade transitions
		if (0.0f < fadeLevel) {
			batch.setProjectionMatrix(hud.combined);
			batch.begin();
			batch.setColor(1.0f, 1.0f, 1.0f, fadeLevel);
			batch.draw(black, 0.0f, 0.0f, CONFIG_WIDTH, CONFIG_HEIGHT);
			batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			batch.end();
		}
		
		//only fade when the states specifies that transitions should fade (i.e. no fade when closing pause menu)
		if (gsm.getStates().peek().processTransitions()) {
			
			//If we are in the delay period of a transition, decrement the delay
			if (0.0f < fadeDelay) {
				fadeDelay -= delta;
			} else if (skipFade) {

				//for special transitions, we skip the fade and transition immediately after delay (play -> results)
				skipFade = false;
				if (null != runAfterTransition) {
					Gdx.app.postRunnable(runAfterTransition);
				}
			} else if (0.0f > fadeDelta) {
				
				//If we are fading in and not done yet, decrease fade.
				fadeLevel += fadeDelta * delta;
				
				//If we just finished fading in, set fade to 0
				if (0.0f > fadeLevel) {
					fadeLevel = 0.0f;
					fadeDelta = 0.0f;
				}
			} else if (0.0f < fadeDelta) {
				
				//If we are fading out and not done yet, increase fade.
				fadeLevel += fadeDelta * delta;
				
				//If we just finished fading out, set fade to 1 and do a transition
				if (1.0f <= fadeLevel) {
					fadeLevel = 1.0f;
					fadeDelta = 0.0f;
					if (null != runAfterTransition) {
						Gdx.app.postRunnable(runAfterTransition);
					}
				}
			}
		}
		
		//music player controller is used for fading tracks
		musicPlayer.controller(delta);
	}
	
	/**
	 * Run when the window resizes. We adjust each camera.
	 */
	@Override
	public void resize(int width, int height) {
		viewportCamera.update(width, height, true);
		viewportUI.update(width, height, true);
		if (null != gsm) {
			gsm.resize();
		}
	}
	
	/**
	 * This is run when we add a new menu. (Usually when a new stage is added by a state)
	 * We want to always have the player's input processing to interact with new menu
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
	public void dispose() {
		gsm.dispose();
		batch.dispose();
		assetManager.dispose();
		musicPlayer.dispose();
		black.dispose();
		
		if (null != FONT_UI) {
			FONT_UI.dispose();
		}
		if (null != FONT_UI_ALT) {
			FONT_UI_ALT.dispose();
		}
		if (null != FONT_SPRITE) {
			FONT_SPRITE.dispose();
		}

		//this prevents an error upon x-ing out the game
		System.exit(0);
	}

	/**
	 * This makes the game fade at a specific speed. Can be positive or negative to fade out or in
	 * @param fadeDelay: How much delay until the fading begins?
	 * @param fadeSpeed: speed of the fading. IF sest to 0, we skip thte fade entirely
	 */
	public void fadeSpecificSpeed(float fadeSpeed, float fadeDelay) { 
		this.fadeDelta = fadeSpeed; 
		this.fadeDelay = fadeDelay;
		if (0.0f == fadeDelta) {
			skipFade = true;
		}
	}

	public void fadeOut() {	fadeDelta = DEFAULT_FADE_OUT_SPEED; }
	
	public void fadeIn() { fadeDelta = DEFAULT_FADE_IN_SPEED; }
		
	public void setFadeLevel(float fadeLevel) { this.fadeLevel = fadeLevel; }
	
	public void setRunAfterTransition(Runnable runAfterTransition) { this.runAfterTransition = runAfterTransition; }
	
	/**
	 * This is used to set game iconification dynamically.
	 * This is extended in the desktop launcher to expose the config
	 */
	public void setAutoIconify(boolean iconify) {}

	public float getFadeLevel() { return fadeLevel; }
	
	public OrthographicCamera getHud() { return hud; }
	
	public OrthographicCamera getCamera() { return camera; }

	public SpriteBatch getBatch() { return batch; }
}
