package com.mygdx.hadal;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Collections;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.constants.ServerConstants;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.managers.StateManager.State;
import com.mygdx.hadal.managers.loaders.*;
import com.mygdx.hadal.server.KryoClient;
import com.mygdx.hadal.server.KryoServer;
import com.mygdx.hadal.users.UserManager;
import com.mygdx.hadal.utils.UPNPUtil;

import java.io.IOException;

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
	public static final String VERSION = "1.1.0";

	//version url takes player to patch notes page when version is clicked in title screen
	public static final String VERSION_URL = "https://donpommelo.itch.io/hadal-calm/devlog/832347/110";

	//Game cameras and respective viewports. camera follows player. hud is for menu/scene2d stuff
	private OrthographicCamera camera, hud;
	public static FitViewport viewportCamera, viewportUI;

	//This is the batch used to render stuff
	private SpriteBatch batch;

	//Assetmanager loads the assets of the game.
    public static AssetManager assetManager;
    
    //Client and server for networking are static fields in the main game
    public static KryoClient client;
    public static KryoServer server;

    //User Manager keeps track of users for multiplayer
	public static UserManager usm;

	//currentMenu is whatever stage is being drawn in the current gameState
    private Stage currentMenu;
    
	/**
	 * This creates a game, setting up the sprite batch to render things and the main game camera.
	 * This also initializes the Gamestate Manager.
	 */
	@Override
	public void create() {
		EffectEntityManager.initLoader(new EffectEntityLoader());
		SpriteManager.initLoader(new SpriteLoader());
		ShaderManager.initLoader(new ShaderLoader());
		SoundManager.initLoader(new SoundLoader());
		RagdollManager.initLoader(new RagdollLoader());

		batch = new SpriteBatch();
		
	    camera = new OrthographicCamera(CONFIG_WIDTH, CONFIG_HEIGHT);
	    hud = new OrthographicCamera(CONFIG_WIDTH, CONFIG_HEIGHT);
	    viewportCamera = new FitViewport(CONFIG_WIDTH, CONFIG_HEIGHT, camera);
	    viewportUI = new FitViewport(CONFIG_WIDTH, CONFIG_HEIGHT, hud);
	    
	    assetManager = new AssetManager(new InternalFileHandleResolver());
	    usm = new UserManager();

		StateManager.addState(this, State.SPLASH, null);
		JSONManager.initJSON();
		JSONManager.initJSONDisplay(this);

		FadeManager.initTextures();

		//enable upnp for both tcp and udp
		if (JSONManager.setting.isEnableUPNP()) {
			UPNPUtil.upnp("TCP", "hadal-upnp-tcp", ServerConstants.PORT);
			UPNPUtil.upnp("UDP", "hadal-upnp-udp", ServerConstants.PORT);
		}

		client = new KryoClient(this, usm);
		server = new KryoServer(usm);

		//this is necessary to prevent nested iterations from causing errors
		Collections.allocateIterators = true;
	}
	
	/**
	 * This is run every engine tick according to libgdx.
	 * Here, the gsm to tells the current state the elapsed time for game processing purposes.
	 */
	@Override
	public void render() {

		float delta = Gdx.graphics.getDeltaTime();

		//update the state, update the ui, render the state, then render the ui.
		StateManager.update(delta);
		currentMenu.act(delta);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		viewportCamera.apply();
		StateManager.render(delta);

		currentMenu.getViewport().apply();
		currentMenu.getBatch().setColor(1.0f, 1.0f, 1.0f, 1.0f);
		currentMenu.draw();

		//manage fade state transitions
		FadeManager.render(this, batch);
		FadeManager.controller(delta);

		//music player controller is used for fading tracks
		MusicPlayer.controller(delta);

		CursorManager.draw(batch);
	}
	
	/**
	 * Run when the window resizes. We adjust each camera.
	 */
	@Override
	public void resize(int width, int height) {
		viewportCamera.update(width, height, true);
		viewportUI.update(width, height, true);
		StateManager.resize();
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
		StateManager.dispose();

		if (batch != null) {
			batch.dispose();
		}
		if (assetManager != null) {
			assetManager.dispose();
		}
		MusicPlayer.dispose();
		FadeManager.dispose();
		SkinManager.dispose();
		CursorManager.dispose();
		BotManager.terminatePathfindingThreads();

        try {
			if (client != null) {
				client.dispose();
			}
			if (server != null) {
				server.dispose();
			}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * This is used to set game iconification dynamically.
	 * This is extended in the desktop launcher to expose the config
	 */
	public void setAutoIconify(boolean iconify) {}

	public OrthographicCamera getHud() { return hud; }
	
	public OrthographicCamera getCamera() { return camera; }

	public SpriteBatch getBatch() { return batch; }
}
