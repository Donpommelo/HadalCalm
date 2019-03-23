package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.Packets;

/**
 * The Gameover state appears when you lose.
 * @author Zachary Tu
 *
 */
public class VictoryState extends GameState {

	private Stage stage;
	
	private PlayState ps;
	
	private final static int width = 800;
	private final static int height = 600;
	
	//Temporary links to other modules for testing.
	private Actor readyOption;
	private Table table;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public VictoryState(final GameStateManager gsm, PlayState ps) {
		super(gsm);
		this.ps = ps;
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				table = new Table();
				table.setLayoutEnabled(true);
				table.setPosition(
						HadalGame.CONFIG_WIDTH / 2 - width / 2, 
						HadalGame.CONFIG_HEIGHT / 2 - height / 2);
				table.setSize(width, height);
				addActor(table);
				
				
				readyOption = new Text(HadalGame.assetManager, "RETURN TO LOADOUT?", 0, 0, Color.WHITE);
				
				readyOption.addListener(new ClickListener() {
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().removeState(VictoryState.class);
			        	getGsm().removeState(PlayState.class);
			        	getGsm().addPlayState(UnlockLevel.HUB, new Loadout(gsm.getRecord()), null, TitleState.class);
			        	HadalGame.server.server.sendToAllTCP(new Packets.LoadLevel(UnlockLevel.HUB, false));
			        }
			    });
				readyOption.setScale(0.5f);
				
				table.add(ps.getScoreWindow().getTable()).row();
				
				if (ps.isServer()) {
					table.add(readyOption);
				}
			}
		};
		app.newMenu(stage);
	}

	/**
	 * 
	 */
	@Override
	public void update(float delta) {
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
