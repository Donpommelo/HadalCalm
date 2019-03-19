package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;

/**
 * The PauseState is pulled up by pausing in game.
 * @author Zachary Tu
 *
 */
public class PauseState extends GameState {

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Actor pause, resumeOption, exitOption;
	private Table table;
	
	private final static int width = 275;
	private final static int height = 200;
	
	private PlayState ps;
	private String pauser;
	
	private boolean toRemove = false;
		
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public PauseState(final GameStateManager gsm, PlayState ps, String pauser) {
		super(gsm);
		this.ps = ps;
		this.pauser = pauser;
		
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
	    					HadalGame.server.server.sendToAllTCP(new Packets.Unpaused(ps.getPlayer().getName()));
	    					HadalGame.server.addNotificationToAll(ps, ps.getPlayer().getName(), "UNPAUSED THE GAME!");
	    					
	    				} else {
	    					HadalGame.client.client.sendTCP(new Packets.Unpaused(ps.getPlayer().getName()));
	    					HadalGame.client.client.sendTCP(new Packets.Notification(ps.getPlayer().getName(), "UNPAUSED THE GAME!"));
	    				}
			        }
			    });
				
				exitOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(PauseState.class);
			        	if (ps.isServer()) {
	    					HadalGame.server.server.stop();
	    				} else {
	    					HadalGame.client.client.stop();
	    				}
			        	getGsm().removeState(PlayState.class);
			        	getGsm().removeState(ClientState.class);
			        }
			    });
				
				table.add(pause).pad(5).expand().top().row();
				table.add(resumeOption).expand().row();
				table.add(exitOption).expand().row();
			}
		};
		app.newMenu(stage);
	}
	
	/**
	 * 
	 */
	@Override
	public void update(float delta) {
		ps.cameraUpdate();
		
		if (toRemove) {
        	getGsm().removeState(PauseState.class);
		}
	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		if (ps != null) {
			ps.render();
		}
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

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
