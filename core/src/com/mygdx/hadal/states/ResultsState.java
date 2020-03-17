package com.mygdx.hadal.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SortByScores;

/**
 * The Results screen appears at the end of levels and displays the player's results
 * In this screen, the player can return to the hub when all players are ready.
 * @author Zachary Tu
 *
 */
public class ResultsState extends GameState {

	//This table contains the options for the title.
	private Table table;
		
	//These are all of the display and buttons visible to the player.
	private Text readyOption;
	
	//This is the playstate that the results state is placed on top of
	private PlayState ps;
	
	//This is a list of all the saved player fields (scores) from the completed playstate
	private ArrayList<SavedPlayerFields> scores;
	
	//This i sa mapping of players in the completed playstate mapped to whether they're ready to return to the hub.
	private HashMap<SavedPlayerFields, Boolean> ready;
	
	private String text;
	
	//Dimentions and position of the results menu
	private final static int width = 1000;
	private final static int height = 600;
	private static final float scale = 0.5f;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public ResultsState(final GameStateManager gsm, String text, PlayState ps) {
		super(gsm);
		this.text = text;
		this.ps = ps;
		
		//First, we obtain the list of scores, depending on whether we are the server or client.
		scores = new ArrayList<SavedPlayerFields>();
		
		if (ps.isServer()) {
			scores = new ArrayList<SavedPlayerFields>(HadalGame.server.getScores().values());
			gsm.getRecord().updateScore(scores.get(0).getScore(), ps.level);
		} else {
			scores = new ArrayList<SavedPlayerFields>(HadalGame.client.getScores().values());
//			gsm.getRecord().updateScore(scores.get(HadalGame.client.connID).getScore(), ps.level);
		}
		
		//Then, we sort according to score and give the winner(s) a win.
		Collections.sort(scores, new SortByScores());
		if (ps.isServer()) {
			int winningScore = scores.get(0).getScore();
			for (Entry<Integer, SavedPlayerFields> player: HadalGame.server.getScores().entrySet()) {
				if (player.getValue().getScore() == winningScore) {
					player.getValue().win();
				}
			}
		}
		
		//Finally we initialize the ready map with everyone set to not ready.
		ready = new HashMap<SavedPlayerFields, Boolean>();
		for (SavedPlayerFields score: scores) {
			ready.put(score, false);
		}
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
				syncScoreTable();
			}
		};
		gsm.getApp().fadeIn();
		app.newMenu(stage);
	}

	/**
	 * This is called whenever we set the displayed table of scores.
	 * This is done once at the start and once again whenever a player readies themselves.
	 */
	public void syncScoreTable() {
		table.clear();
		
		table.add(new Text(text, 0, 0, false)).padBottom(50).colspan(5).row();
		table.add(new Text("PLAYER", 0, 0, false)).padBottom(50).padRight(20);
		table.add(new Text("KILLS", 0, 0, false)).padBottom(50).padRight(20);
		table.add(new Text("DEATH", 0, 0, false)).padBottom(50).padRight(20);
		table.add(new Text("SCORE", 0, 0, false)).padBottom(50).padRight(20);
		table.add(new Text("STATUS", 0, 0, false)).padBottom(50).row();
		
		for (SavedPlayerFields score: scores) {
			Text name = new Text(score.getName(), 0, 0, false);
			name.setScale(scale);
			
			Text kills = new Text(score.getKills() + " ", 0, 0, false);
			Text death = new Text(score.getDeaths() + " ", 0, 0, false);
			Text points = new Text(score.getScore() + " ", 0, 0, false);
			Text status = new Text(ready.get(score) ? "READY" : "WAITING", 0, 0, false);
				
			table.add(name).padBottom(25);
			table.add(kills).padBottom(25);
			table.add(death).padBottom(25);
			table.add(points).padBottom(25);
			table.add(status).padBottom(25).row();
		}
		
		readyOption = new Text("RETURN TO LOADOUT?", 0, 0, true);
		
		readyOption.addListener(new ClickListener() {
	        
			@Override
			public void clicked(InputEvent e, float x, float y) {
	        	
	        	//When pressed, the ready option indicates to the server that that player is ready.
	        	if (ps.isServer()) {
	        		readyPlayer(0);
	        	} else {
	        		HadalGame.client.client.sendTCP(new Packets.ClientReady());
	        	}
	        }
	    });
		readyOption.setScale(0.5f);
		
		table.add(readyOption).padLeft(25);
	}
	
	/**
	 * This is pressed whenever a player gets ready.
	 * @param playerId: If this is run by the server, this is the player's connID (or 0, if the host themselves).
	 * For the client, playerId is the indix in scores of thte player that readies.
	 */
	public void readyPlayer(int playerId) {
		if (ps.isServer()) {
			
			//The server finds the player that readies, sets their readiness and informs all clients by sending that player's index
			SavedPlayerFields field = HadalGame.server.getScores().get(playerId);
			if (field != null) {
				ready.put(field, true);
				HadalGame.server.sendToAllTCP(new Packets.ClientReady(scores.indexOf(field)));
			}
		} else {
			
			//Clients just find the player based on that index and sets them as ready.
			ready.put(scores.get(playerId), true);
		}
		
		//When all players are ready, reddy will be true and we return to the hub
		boolean reddy = true;
		for (boolean b: ready.values()) {
			if (!b) {
				reddy = false;
			}
		}
		
		//sync score table to display new readiness
		syncScoreTable();
		
		//When the server is ready, we return to hub and tell all clients to do the same.
		if (reddy) {
			if (ps.isServer()) {
				gsm.getApp().setRunAfterTransition(new Runnable() {

					@Override
					public void run() {
						gsm.removeState(ResultsState.class);
						gsm.removeState(PlayState.class);
						gsm.gotoHubState();
					}
					
				});
			}
			
			gsm.getApp().fadeOut();
		}
	}

	@Override
	public void update(float delta) {}

	@Override
	public void render(float delta) {}

	@Override
	public void dispose() {
		stage.dispose();
	}
}
