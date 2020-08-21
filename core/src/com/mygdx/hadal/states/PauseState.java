package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState.TransitionState;

/**
 * The PauseState is pulled up by pausing in game.
 * @author Zachary Tu
 */
public class PauseState extends GameState {

	//This table contains the ui elements of the pause screen
	private Table table;
	
	//These are all of the display and buttons visible to the player.
	private Text pause, resumeOption, hubOption, settingOption, spectateOption, joinOption, exitOption;
	
	//This is the playstate that the pause state must be placed on top of.
	private PlayState ps;
	
	//This is the name of the player who paused
	private String pauser;
	
	//This determines whether the pause state should be removed or not next engine tick.
	private boolean toRemove = false;
	
	//is the game paused underneath this menu?
	private boolean paused;
	
	//Dimentions of the pause menu
	private final static int width = 500;
	private final static int height = 300;
	private final static int extraRowHeight = 100;
	
	/**
	 * Constructor will be called whenever a player pauses.
	 * @param gsm
	 */
	public PauseState(final GameStateManager gsm, PlayState ps, String pauser, boolean paused) {
		super(gsm);
		this.ps = ps;
		this.pauser = pauser;
		this.paused = paused;
		
		//When the server pauses, it sends a message to all clients to pause them as well.
		if (ps.isServer() && paused) {
			HadalGame.server.sendToAllTCP(new Packets.Paused(pauser, true));
		}
		
		SoundEffect.POSITIVE.play(gsm, 1.0f, false);
	}

	@Override
	public void show() {
		
		final PauseState me = this;
		
		stage = new Stage() {
			{
				//make the menu size adjust based on how many options are available
				int menuHeight = height;
				
				if (ps.isServer() && (gsm.getRecord().getFlags().get("HUB_REACHED").equals(1) || GameStateManager.currentMode == Mode.MULTI)) {
					menuHeight += extraRowHeight;
				}
				
				if (ps.isHub() && !ps.isServer() && GameStateManager.currentMode == Mode.MULTI) {
					menuHeight += extraRowHeight;
				}
				
				addActor(new MenuWindow(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT / 2 - menuHeight / 2, width, menuHeight));
				
				table = new Table();
				table.setLayoutEnabled(true);
				table.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT / 2 - menuHeight / 2);
				table.setSize(width, menuHeight);
				addActor(table);
				
				if (paused) {
					pause = new Text("PAUSED BY \n" + pauser, 0, 0, false);
				} else {
					pause = new Text("GAME NOT PAUSED", 0, 0, false);
				}
				pause.setScale(0.5f);
				
				resumeOption = new Text("RESUME", 0, 0, true);
				hubOption = new Text("RETURN TO HUB", 0, 0, true);
				settingOption = new Text("SETTINGS", 0, 0, true);
				spectateOption = new Text("SPECTATE", 0, 0, true);
				joinOption = new Text("JOIN", 0, 0, true);
				exitOption = new Text("EXIT TO TITLE", 0, 0, true);
				
				resumeOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) { unpause(); }
			    });
				
				hubOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	gsm.removeState(PauseState.class);
			        	
			        	if (ps.isServer()) {
			        		//If the server unpauses, send a message and notification to all players to unpause.
			        		HadalGame.server.sendToAllTCP(new Packets.Unpaused(ps.getPlayer().getName()));
			        		
			        		if (GameStateManager.currentMode == Mode.SINGLE) {
				        		ps.loadLevel(UnlockLevel.SSTUNICATE1, TransitionState.NEWLEVEL, "");
				        	}
				        	if (GameStateManager.currentMode == Mode.MULTI) {
				        		ps.loadLevel(UnlockLevel.HUB_MULTI, TransitionState.NEWLEVEL, "");
				        	}
	    				}
			        	SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
			        }
			    });
				
				settingOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	
			        	//Setting pops a setting state on top of the pause state.
			        	gsm.addSettingState(me, PauseState.class);
			        	SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
			        }
			    });
				
				spectateOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						unpause();
						if (ps.isServer()) {
							ps.becomeSpectator(ps.getPlayer());
						} else {
							HadalGame.client.sendTCP(new Packets.StartSpectate());
						}
			        }
			    });
				
				joinOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						unpause();
						if (ps.isServer()) {
							ps.exitSpectator(ps.getPlayer());
						} else {
							HadalGame.client.sendTCP(new Packets.EndSpectate());
						}
			        }
			    });

				exitOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	
			        	//Exiting returns to the title state and stops the server/client, disconnecting.
			        	gsm.removeState(PauseState.class);
			        	ps.returnToTitle(0.0f);
			        	
			        	SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
			        }
			    });
				
				table.add(pause).pad(5).expand().top().row();
				table.add(resumeOption).expand().row();
				
				//don't add return to hub option in singleplayer if hub hasn't bee nreached yet
				if (ps.isServer() && (gsm.getRecord().getFlags().get("HUB_REACHED").equals(1) || GameStateManager.currentMode == Mode.MULTI)) {
					table.add(hubOption).expand().row();
				}
				table.add(settingOption).expand().row();
				
				//atm, only cliens can manually join spectator mode
				if (ps.isHub() && !ps.isServer() && GameStateManager.currentMode == Mode.MULTI) {
					if (ps.isSpectatorMode()) {
						table.add(joinOption).expand().row();
					} else {
						table.add(spectateOption).expand().row();
					}
				}
				table.add(exitOption).expand().row();
			}
		};
		app.newMenu(stage);
		
		//We get the playstate's input processor so users can send messages + view score when paused
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(ps.stage);
		
		inputMultiplexer.addProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) {				
				
				if (paused) {
					if (keycode == PlayerAction.MESSAGE_WINDOW.getKey()) {
						ps.getController().keyDown(keycode);
					} else if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
						ps.getScoreWindow().setVisibility(true);
					}
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {	
				if (keycode == PlayerAction.PAUSE.getKey()) {
					unpause();
					
					//we return true here so that the client does not process another pause after the unpause
					return true;
				}
				return false; 
			}

			@Override
			public boolean keyTyped(char character) { return false; }

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

			@Override
			public boolean mouseMoved(int screenX, int screenY) { return false; }

			@Override
			public boolean scrolled(int amount) { return false; }
		});
		
		//if the game isn't actually paused, the player can still control the game underneath
		if (!paused) {
			inputMultiplexer.addProcessor(ps.controller);
		}
		
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
	
	@Override
	public void update(float delta) {
		
		//The playstate underneath should have their camera focus and ui act (letting dialog appear + disappear)
		if (ps != null) {
			if (paused) {
				ps.cameraUpdate();
			} else {
				ps.update(delta);
			}
			ps.stage.act();
		}
		
		//If the state has been unpaused, remove it
		if (toRemove) {
			SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
			
			//the following code makes sure that, if the host changes artifact slot number, these changes sync immediately.
			if (ps.isServer() && ps.isHub()) {
				ps.getPlayer().getPlayerData().syncArtifacts(false);
				for (Player player : HadalGame.server.getPlayers().values()) {
					player.getPlayerData().syncArtifacts(false);
				}
			}
			ps.getUiHub().refreshHub();
			gsm.removeState(PauseState.class);
		}
	}

	@Override
	public void render(float delta) {
		
		//Render the playstate and playstate ui underneath
		if (ps != null) {
			ps.render(delta);
			ps.stage.getViewport().apply();
			ps.stage.draw();
		}
	}

	@Override
	public void dispose() { stage.dispose(); }

	/**
	 * Run when the game is unpaused.
	 */
	public void unpause() {
    	
    	if (ps.isServer()) {
    		toRemove = true;

    		if (paused) {
    			//If the server unpauses, send a message and notification to all players to unpause.
        		HadalGame.server.sendToAllTCP(new Packets.Unpaused(ps.getPlayer().getName()));
    			HadalGame.server.addNotificationToAll(ps, ps.getPlayer().getName(), "UNPAUSED THE GAME!");
    		}
		} else {
			if (paused) {
				//If a client unpauses, tell the server so it can echo it to everyone else
				HadalGame.client.sendTCP(new Packets.Unpaused(ps.getPlayer().getName()));
			} else {
				toRemove = true;
			}
		}
	}
	
	//This is called when the pause state is designated to be removed.
	public void setToRemove(boolean toRemove) {	this.toRemove = toRemove; }

	public PlayState getPs() { return ps; }

	public String getPauser() {	return pauser; }	
	
	@Override
	public boolean processTransitions() { return !paused; }
}
