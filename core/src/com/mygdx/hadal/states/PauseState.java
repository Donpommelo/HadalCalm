package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.actors.TableWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.managers.StateManager.Mode;
import com.mygdx.hadal.managers.StateManager.State;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.util.SocketManager;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.text.UIText;

/**
 * The PauseState is pulled up by pausing in game.
 * From here, the player can resume, change settings, exit game.
 * If a client, they can also use the spectator mode.
 * @author Yaptista Yihorn
 */
public class PauseState extends GameState {

	//Dimensions of the pause menu
	private static final float WIDTH = 500;
	private static final float HEIGHT = 275;
	private static final int EXTRA_ROW_HEIGHT = 55;
	private static final int OPTION_HEIGHT = 48;
	private static final int OPTION_PAD = 5;
	private static final float PAUSE_TEXT_SCALE = 0.3f;

	//This table contains the ui elements of the pause screen
	private Table table;
	
	//These are all of the display and buttons visible to the player.
	private Text pause, resumeOption, hubOption, extraOption, settingOption, spectateOption, joinOption, exitOption;
	
	//This is the playstate that the pause state must be placed on top of.
	private final PlayState ps;
	
	//This is the name of the player who paused
	private final String pauser;
	
	//This determines whether the pause state should be removed or not next engine tick.
	//We do this instead of removing right away in case we remove as a result of receiving a packet from another player unpausing (which can happen whenever).
	private boolean toRemove;
	
	//is the game paused underneath this menu?
	// In multiplayer, the pause menu will be brought up, but the game will not be paused (unless changed in settings)
	private final boolean paused;
	
	/**
	 * Constructor will be called whenever a player pauses.
	 */
	public PauseState(HadalGame app, PlayState ps, String pauser, boolean paused) {
		super(app);
		this.ps = ps;
		this.pauser = pauser;
		this.paused = paused;
		
		//When the server pauses, it sends a message to all clients to pause them as well.
		if (ps.isServer() && paused) {
			PacketManager.serverTCPAll(new Packets.Paused(pauser));
		}

		SoundManager.play(SoundEffect.POSITIVE);
	}

	@Override
	public void show() {
		
		final PauseState me = this;

		//b/c the pause state can get shown multiple times without getting removed, we must get rid of stage if already created
		if (stage != null) {
			stage.dispose();
		}

		stage = new Stage() {
			{
				//make the menu size adjust based on how many options are available
				float menuHeight = HEIGHT;
				
				//extra "return to hub" option is added if the hub has been reached or if the player is in multiplayer mode.
				if (HadalGame.usm.isHost() && (JSONManager.record.getFlags().get("HUB_REACHED") == 1 || StateManager.currentMode == Mode.MULTI)) {
					menuHeight += EXTRA_ROW_HEIGHT;
				}
				
				//extra "spectate" option is added if the player is in multiplayer mode.
				if (ps.getMode().isHub() && StateManager.currentMode == Mode.MULTI) {
					menuHeight += EXTRA_ROW_HEIGHT;
				}
				
				table = new TableWindow();
				table.setLayoutEnabled(true);
				table.setPosition(HadalGame.CONFIG_WIDTH / 2 - WIDTH / 2, HadalGame.CONFIG_HEIGHT / 2 - menuHeight / 2);
				table.setSize(WIDTH, menuHeight);
				addActor(table);
				
				//text indicates if the game is actually paused or not (if multiplayer pause is disabled in settings)
				if (paused) {
					pause = new Text(UIText.PAUSE_BY.text(pauser));
				} else {
					pause = new Text(UIText.PAUSE_NOT.text());
				}
				pause.setScale(PAUSE_TEXT_SCALE);
				
				resumeOption = new Text(UIText.RESUME.text()).setButton(true);
				hubOption = new Text(UIText.RETURN_HUB.text()).setButton(true);
				settingOption = new Text(UIText.SETTINGS.text()).setButton(true);
				extraOption = new Text(UIText.EXTRA.text()).setButton(true);
				spectateOption = new Text(UIText.SPECTATE.text()).setButton(true);
				joinOption = new Text(UIText.REJOIN.text()).setButton(true);
				exitOption = new Text(UIText.EXIT.text()).setButton(true);
				
				resumeOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) { unpause(); }
			    });
				
				hubOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	StateManager.removeState(PauseState.class);
			        	
			        	if (ps.isServer()) {
			        		//If the server unpauses, send a message and notification to all players to unpause.
							PacketManager.serverTCPAll(new Packets.Unpaused());

							if (StateManager.currentMode == Mode.SINGLE) {
				        		ps.getTransitionManager().loadLevel(UnlockLevel.SSTUNICATE1, TransitionState.NEWLEVEL, "");
				        	}
				        	if (StateManager.currentMode == Mode.MULTI) {
				        		ps.getTransitionManager().loadLevel(UnlockLevel.HUB_MULTI, TransitionState.NEWLEVEL, "");
				        	}
	    				} else if (HadalGame.usm.isHost()) {
							if (StateManager.currentMode == Mode.SINGLE) {
								PacketManager.clientTCP(new Packets.ClientLevelRequest(UnlockLevel.SSTUNICATE1, GameMode.HUB, null));
							}
							if (StateManager.currentMode == Mode.MULTI) {
								PacketManager.clientTCP(new Packets.ClientLevelRequest(UnlockLevel.HUB_MULTI, GameMode.HUB, null));
							}
						}
						SoundManager.play(SoundEffect.NEGATIVE);
			        }
			    });
				
				settingOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	
			        	//Setting pops a setting state on top of the pause state.
						StateManager.addState(app, State.SETTING, me);
						SoundManager.play(SoundEffect.UISWITCH1);
			        }
			    });

				extraOption.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						//Setting pops a about state on top of the pause state.
						StateManager.addState(app, State.ABOUT, me);
						SoundManager.play(SoundEffect.UISWITCH1);
					}
				});
				
				spectateOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						unpause();
						if (ps.isServer()) {
							ps.getSpectatorManager().becomeSpectator(HadalGame.usm.getOwnUser(), true);
						} else {
							PacketManager.clientTCP(new Packets.StartSpectate());
						}
			        }
			    });
				
				joinOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
						unpause();
						if (ps.isServer()) {
							ps.getSpectatorManager().exitSpectator(HadalGame.usm.getOwnUser());
						} else {
							PacketManager.clientTCP(new Packets.EndSpectate(new Loadout(JSONManager.loadout)));
						}
			        }
			    });

				exitOption.addListener(new ClickListener() {
			        
					@Override
					public void clicked(InputEvent e, float x, float y) {
			        	
			        	//Exiting returns to the title state and stops the server/client, disconnecting.
						StateManager.removeState(PauseState.class);
			        	ps.getTransitionManager().returnToTitle(0.0f);
						SoundManager.play(SoundEffect.NEGATIVE);

						//exiting sends message to close lobby, if we are hosting one locally
						SocketManager.exit();
			        }
			    });
				
				table.add(pause).pad(5).expand().top().row();
				table.add(resumeOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				
				//don't add return to hub option in singleplayer if hub hasn't been reached yet
				if (HadalGame.usm.isHost() && (JSONManager.record.getFlags().get("HUB_REACHED") == 1 || StateManager.currentMode == Mode.MULTI)) {
					table.add(hubOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				}
				table.add(settingOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
				table.add(extraOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();

				if (ps.getMode().isHub() && StateManager.currentMode == Mode.MULTI) {
					if (ps.getSpectatorManager().isSpectatorMode()) {
						table.add(joinOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
					} else {
						table.add(spectateOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
					}
				}
				table.add(exitOption).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
			}
		};
		app.newMenu(stage);
		
		//We get the playstate input processor so users can send messages + view score when paused
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(ps.stage);
		
		inputMultiplexer.addProcessor(new InputAdapter() {

			@Override
			public boolean keyDown(int keycode) {				
				
				if (paused) {
					if (keycode == PlayerAction.MESSAGE_WINDOW.getKey()) {
						ps.getController().keyDown(keycode);
					} else if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
						ps.getUIManager().getScoreWindow().setVisibility(true);
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
				} else if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
					ps.getUIManager().getScoreWindow().setVisibility(false);
				}
				return false; 
			}
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
				ps.getCameraManager().cameraUpdate();
			} else {
				ps.update(delta);
			}
			ps.stage.act();
		}
		
		//If the state has been unpaused, remove it
		if (toRemove) {
			SoundManager.play(SoundEffect.NEGATIVE);
			
			//the following code makes sure that, if the host changes artifact slot number, these changes sync immediately.
			if (ps != null) {
				if (ps.isServer() && ps.getMode().isHub()) {
					for (User user : HadalGame.usm.getUsers().values()) {
						if (user.getPlayer() != null) {
							user.getPlayer().getArtifactHelper().syncArtifacts(false, true);
						}
					}
				}
				ps.getUIManager().getUiHub().refreshHub(null);
			}
			StateManager.removeState(PauseState.class);
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
				PacketManager.serverTCPAll(new Packets.Unpaused());
				HadalGame.server.addNotificationToAll(ps, HadalGame.usm.getOwnUser().getStringManager().getName(), UIText.UNPAUSED.text(), true, DialogType.SYSTEM);
    		}
		} else {
			if (paused) {
				//If a client unpauses, tell the server so it can echo it to everyone else
				PacketManager.clientTCP(new Packets.Unpaused());
			} else {
				toRemove = true;
			}
		}
	}
	
	//This is called when the pause state is designated to be removed.
	public void setToRemove(boolean toRemove) {	this.toRemove = toRemove; }

	public PlayState getPs() { return ps; }

	//if the game isn't really paused, we want to process transitions (if the player dies or level transitions level while menu is visible)
	@Override
	public boolean processTransitions() { return !paused; }
}
