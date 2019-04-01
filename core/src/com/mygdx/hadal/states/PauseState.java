package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;

/**
 * The PauseState is pulled up by pausing in game.
 * @author Zachary Tu
 *
 */
public class PauseState extends GameState {

	//This table contains the ui elements of the pause screen
	private Table table;
	
	//These are all of the display and buttons visible to the player.
	private Text pause, resumeOption, exitOption;
	
	//This is the playstate that the pause state must be placed on top of.
	private PlayState ps;
	
	//This is the name of the player who paused
	private String pauser;
	
	//This determines whether the pause state should be removed or not next engine tick.
	private boolean toRemove = false;
	
	//Dimentions of the pause menu
	private final static int width = 275;
	private final static int height = 200;
	
	/**
	 * Constructor will be called whenever a player pauses.
	 * @param gsm
	 */
	public PauseState(final GameStateManager gsm, PlayState ps, String pauser) {
		super(gsm);
		this.ps = ps;
		this.pauser = pauser;
		
		//When the server pauses, it sends a message to all clients to pause them as well.
		if (ps.isServer()) {
			HadalGame.server.server.sendToAllTCP(new Packets.Paused(pauser));
		}
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new MenuWindow(HadalGame.assetManager, gsm,
						HadalGame.CONFIG_WIDTH / 2 - width / 2, 
						HadalGame.CONFIG_HEIGHT / 2 - height / 2, width, height));
				
				table = new Table();
				table.setLayoutEnabled(true);
				table.setPosition(
						HadalGame.CONFIG_WIDTH / 2 - width / 2, 
						HadalGame.CONFIG_HEIGHT / 2 - height / 2);
				table.setSize(width, height);
				addActor(table);
				
				pause = new Text(HadalGame.assetManager, "PAUSED BY \n" + pauser, 0, 0, Color.WHITE);
				pause.setScale(0.5f);
				
				resumeOption = new Text(HadalGame.assetManager, "RESUME?", 0, 0, Color.WHITE);

				exitOption = new Text(HadalGame.assetManager, "EXIT?", 0, 0, Color.WHITE);
				
				resumeOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(PauseState.class);
			        	
			        	if (ps.isServer()) {
			        		
			        		//If the server unpauses, send a message and notification to all players to unpause.
			        		HadalGame.server.server.sendToAllTCP(new Packets.Unpaused(ps.getPlayer().getName()));
	    					HadalGame.server.addNotificationToAll(ps, ps.getPlayer().getName(), "UNPAUSED THE GAME!");
	    				} else {
	    					
	    					//If a client unpauses, tell the server so it can echo it to everyone else
	    					HadalGame.client.client.sendTCP(new Packets.Unpaused(ps.getPlayer().getName()));
	    					HadalGame.client.client.sendTCP(new Packets.Notification(ps.getPlayer().getName(), "UNPAUSED THE GAME!"));
	    				}
			        }
			    });
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	
			        	//Exiting returns to the title state and stops the server/client, disconnecting.
			        	getGsm().removeState(PauseState.class);
			        	ps.returnToTitle();
			        }
			    });
				
				table.add(pause).pad(5).expand().top().row();
				table.add(resumeOption).expand().row();
				table.add(exitOption).expand().row();
			}
		};
		app.newMenu(stage);
		
		//We get the playstate's input processor so users can send messages + view score when paused
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(ps.getPlayStateStage());
		inputMultiplexer.addProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {				
				if (keycode == PlayerAction.MESSAGE_WINDOW.getKey()) {
					ps.getController().keyDown(keycode);
				}
				
				if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
					ps.getScoreWindow().setVisibility(true);
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
					ps.getScoreWindow().setVisibility(false);
				}
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}
		});
		
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void update(float delta) {
		
		//The playstate underneath should have their camera focus and ui act (letting dialog appear + disappear)
		
		if (ps != null) {
			ps.cameraUpdate();
			ps.stage.act();
		}
		
		//If the state has been unpaused, remove it
		if (toRemove) {
        	getGsm().removeState(PauseState.class);
		}
	}

	@Override
	public void render() {
		
		//Render the playstate and playstate ui underneath
		if (ps != null) {
			ps.render();
			ps.stage.getViewport().apply();
			ps.stage.draw();
		}
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	//This is called when the pause state is designated to be removed.
	public void setToRemove(boolean toRemove) {
		this.toRemove = toRemove;
	}

	public PlayState getPs() {
		return ps;
	}

	public String getPauser() {
		return pauser;
	}	
}
