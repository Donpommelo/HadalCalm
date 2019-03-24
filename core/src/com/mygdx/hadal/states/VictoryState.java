package com.mygdx.hadal.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

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
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SortByScores;

/**
 * The Gameover state appears when you lose.
 * @author Zachary Tu
 *
 */
public class VictoryState extends GameState {

	private Stage stage;
	
	private PlayState ps;
	
	private final static int width = 1000;
	private final static int height = 600;
	private static final float scale = 0.5f;
	
	//Temporary links to other modules for testing.
	private Actor readyOption;
	private Table scoreTable;
	
	private ArrayList<SavedPlayerFields> scores;
	private HashMap<SavedPlayerFields, Boolean> ready;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public VictoryState(final GameStateManager gsm, PlayState ps) {
		super(gsm);
		this.ps = ps;
		
		scores = new ArrayList<SavedPlayerFields>();
		
		if (ps.isServer()) {
			scores = new ArrayList<SavedPlayerFields>(HadalGame.server.getScores().values());
		} else {
			scores = HadalGame.client.scores;
		}
		
		Collections.sort(scores, new SortByScores());
		if (ps.isServer()) {
			int winningScore = scores.get(0).getScore();
			for (Entry<Integer, SavedPlayerFields> player: HadalGame.server.getScores().entrySet()) {
				if (player.getValue().getScore() == winningScore) {
					player.getValue().getWin();
				}
			}
		}
		
		ready = new HashMap<SavedPlayerFields, Boolean>();
		for (SavedPlayerFields score: scores) {
			ready.put(score, false);
		}
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				
				scoreTable = new Table();
				scoreTable.setLayoutEnabled(true);
				scoreTable.setPosition(
						HadalGame.CONFIG_WIDTH / 2 - width / 2, 
						HadalGame.CONFIG_HEIGHT / 2 - height / 2);
				scoreTable.setSize(width, height);
				addActor(scoreTable);
				syncScoreTable();
			}
		};
		app.newMenu(stage);
	}

	public void syncScoreTable() {
		scoreTable.clear();
		
		scoreTable.add(new Text(HadalGame.assetManager, "PLAYER", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		scoreTable.add(new Text(HadalGame.assetManager, "KILLS", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		scoreTable.add(new Text(HadalGame.assetManager, "DEATH", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		scoreTable.add(new Text(HadalGame.assetManager, "SCORE", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		scoreTable.add(new Text(HadalGame.assetManager, "STATUS", 0, 0, Color.WHITE)).padBottom(50).row();
		
		for (SavedPlayerFields score: scores) {
			Text name = new Text(HadalGame.assetManager, score.getName(), 0, 0, Color.WHITE);
			name.setScale(scale);
			
			Text kills = new Text(HadalGame.assetManager, score.getKills() + " ", 0, 0, Color.WHITE);
			Text death = new Text(HadalGame.assetManager, score.getDeaths() + " ", 0, 0, Color.WHITE);
			Text points = new Text(HadalGame.assetManager, score.getScore() + " ", 0, 0, Color.WHITE);
			Text status = new Text(HadalGame.assetManager, ready.get(score) ? "READY" : "WAITING", 0, 0, Color.WHITE);
				
			scoreTable.add(name).padBottom(25);
			scoreTable.add(kills).padBottom(25);
			scoreTable.add(death).padBottom(25);
			scoreTable.add(points).padBottom(25);
			scoreTable.add(status).padBottom(25).row();
		}
		
		readyOption = new Text(HadalGame.assetManager, "RETURN TO LOADOUT?", 0, 0, Color.WHITE);
		
		readyOption.addListener(new ClickListener() {
	        public void clicked(InputEvent e, float x, float y) {
	        	if (ps.isServer()) {
	        		readyPlayer(0);
	        	} else {
	        		HadalGame.client.client.sendTCP(new Packets.ClientReady());
	        	}
	        }
	    });
		readyOption.setScale(0.5f);
		
		scoreTable.add(readyOption).padLeft(25);
	}
	
	public void readyPlayer(int playerId) {
		if (ps.isServer()) {
			SavedPlayerFields field = HadalGame.server.getScores().get(playerId);
			if (field != null) {
				ready.put(field, true);
				HadalGame.server.server.sendToAllTCP(new Packets.ClientReady(scores.indexOf(field)));
			}
		} else {
			ready.put(scores.get(playerId), true);
		}
		
		boolean reddy = true;
		for (boolean b: ready.values()) {
			if (!b) {
				reddy = false;
			}
		}
		
		syncScoreTable();
		
		if (reddy && ps.isServer()) {
			
			getGsm().removeState(VictoryState.class);
	    	getGsm().removeState(PlayState.class);
	    	getGsm().addPlayState(UnlockLevel.HUB, new Loadout(gsm.getRecord()), null, TitleState.class);
	    	HadalGame.server.server.sendToAllTCP(new Packets.LoadLevel(UnlockLevel.HUB, false));
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void update(float delta) {}

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
