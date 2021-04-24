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
import io.socket.client.Socket;
import org.bitlet.weupnp.GatewayDevice;
import org.bitlet.weupnp.GatewayDiscover;
import org.bitlet.weupnp.PortMappingEntry;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;

/**
 * HadalGame is the game. This is created upon launching the game. It delegates the rendering + updating logic to the GameStateManager.
 * @author Dincubus Doncubus
 */
public class HadalGame extends ApplicationAdapter {
	
	//screen dimensions
	public static final float CONFIG_WIDTH = 1280.0f;
	public static final float CONFIG_HEIGHT = 720.0f;
	
	//this is the game's version. This must match between client and host to connect.
	public static final String Version = "1.0.5h";
	public static final String versionURL = "https://donpommelo.itch.io/hadal-calm/devlog/246181/105h";

	//Camera and Spritebatch. This is pretty standard stuff. camera follows player. hud is for menu/scene2d stuff
	private OrthographicCamera camera, hud;
	public static FitViewport viewportCamera, viewportUI;

	//This is the batch used to render stuff
	private SpriteBatch batch;

	//This is the Gamestate Manager. It manages the current stack of game states.
	private GameStateManager gsm;
	
	//Assetmanager loads the assets of the game.
    public static AssetManager assetManager;
    
    //this game manages the background music of the game.
    public static MusicPlayer musicPlayer;

    //Client and server for networking are static fields in the main game
    public static KryoClient client;
    public static KryoServer server;

    //socket is used to connect to matchmaking server
	public static Socket socket;

	public static BitmapFont SYSTEM_FONT_UI, SYSTEM_FONT_UI_SMALL, SYSTEM_FONT_SPRITE;
    public static Color DEFAULT_TEXT_COLOR;
 
	//currentMenu is whatever stage is being drawn in the current gameState
    private Stage currentMenu;
    
    //this is the rate at which the screen fades from/to black.
  	private static final float defaultFadeInSpeed = -2.0f;
  	private static final float defaultFadeOutSpeed = 2.0f;
  	
  	//This is the how faded the black screen is. (starts off black)
  	protected float fadeLevel = 1.0f;
  	
  	//Amount of delay before fade transition occurs
  	protected float fadeDelay = 0.0f;
  	
  	//This is how much the fade changes every engine tick (starts out fading in)
  	protected float fadeDelta = defaultFadeInSpeed;
  	
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

		//enable upnp
		if (gsm.getSetting().isEnableUPNP()) {
			upnp("TCP", "hadal-upnp-tcp", gsm.getSetting().getPortNumber());
			upnp("UDP", "hadal-upnp-udp", gsm.getSetting().getPortNumber());
		}

		client = new KryoClient(gsm);
		server = new KryoServer(gsm);
		
	    musicPlayer = new MusicPlayer(gsm);

		black = new Texture(Gdx.files.internal("black.png"));
	}
	
	/**
	 * This is run every engine tick according to libgdx.
	 * Here, we tell the gsm to tell the current state of the elapsed time.
	 */
	@Override
	public void render() {

		float delta = Gdx.graphics.getDeltaTime();
		
		//update the state, update the ui, render the state, then render the ui.
		gsm.update(delta);
		currentMenu.act();

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		viewportCamera.apply();
		gsm.render(delta);
		
		currentMenu.getViewport().apply();
		currentMenu.getBatch().setColor(1.0f, 1.0f, 1.0f, 1.0f);
		currentMenu.draw();
		
		//Render the black image used for fade transitions
		if (fadeLevel > 0.0f) {
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
			if (fadeDelay > 0.0f) {
				fadeDelay -= delta;
			} else if (fadeDelta < 0.0f) {
				
				//If we are fading in and not done yet, decrease fade.
				fadeLevel += fadeDelta * delta;
				
				//If we just finished fading in, set fade to 0
				if (fadeLevel < 0.0f) {
					fadeLevel = 0.0f;
					fadeDelta = 0.0f;
				}
			} else if (fadeDelta > 0.0f) {
				
				//If we are fading out and not done yet, increase fade.
				fadeLevel += fadeDelta * delta;
				
				//If we just finished fading out, set fade to 1 and do a transition
				if (fadeLevel >= 1.0f) {
					fadeLevel = 1.0f;
					fadeDelta = 0.0f;
					if (runAfterTransition != null) {
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
		if (gsm != null) {
			gsm.resize(width, height);
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
		
		if (SYSTEM_FONT_UI != null) {
			SYSTEM_FONT_UI.dispose();
		}
		if (SYSTEM_FONT_UI_SMALL != null) {
			SYSTEM_FONT_UI_SMALL.dispose();
		}
		if (SYSTEM_FONT_SPRITE != null) {
			SYSTEM_FONT_SPRITE.dispose();
		}

		//this prevents an error upon x-ing out the game
		System.exit(0);
	}

	//this is the player's external ip that other clients will connect to
	public static String myIp = "";

	/**
	 * This attempts to enable upnp on the client's router
	 * @param protocol: tcp or udp
	 * @param descr: Not used for anything rn except logging
	 * @param port: what port to map to
	 */
	private static void upnp(String protocol, String descr, int port) {

		//We do these on a separate thread to avoid initial loading times
		new Thread(() -> {
			try {
				GatewayDiscover discover = new GatewayDiscover();
				discover.discover();
				GatewayDevice d = discover.getValidGateway();

				//Attempt to find router and acquire its information
				if (d != null) {
					InetAddress localAddress = d.getLocalAddress();
					myIp = d.getExternalIPAddress();

					PortMappingEntry portMapping = new PortMappingEntry();

					//delete existing mappings before attempting to create a new one
					d.deletePortMapping(port, protocol);
					if (!d.getSpecificPortMappingEntry(port, protocol, portMapping)) {

						if (!d.addPortMapping(port, port, localAddress.getHostAddress(), protocol, descr)) {
							Gdx.app.log("UPNP", "FAILED TO MAP PORT");
						} else {
							Gdx.app.log("UPNP", "SUCCESSFULLY MAPPED");
						}
					} else {
						Gdx.app.log("UPNP", "ALREADY MAPPED");
					}
				}
			} catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
				Gdx.app.log("UPNP", "ERROR WHEN MAPPING UPNP PORT");
			}
		}).start();
	}

	/**
	 * This makes the game fade at a specific speed. Can be positive or negative to fade out or in
	 */
	public void fadeSpecificSpeed(float fadeSpeed, float fadeDelay) { 
		this.fadeDelta = fadeSpeed; 
		this.fadeDelay = fadeDelay;
	}

	public void fadeOut() {	fadeDelta = defaultFadeOutSpeed; }
	
	public void fadeIn() { fadeDelta = defaultFadeInSpeed; }
		
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
