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
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.ResultsBackdrop;
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
	private Text readyOption, forceReadyOption;
	
	//This is the playstate that the results state is placed on top of
	private PlayState ps;
	
	//This is a list of all the saved player fields (scores) from the completed playstate
	private ArrayList<SavedPlayerFields> scores;
	
	//This i sa mapping of players in the completed playstate mapped to whether they're ready to return to the hub.
	private HashMap<SavedPlayerFields, Boolean> ready;
	
	private String text;
	
	//Dimentions and position of the results menu
	private final static int width = 1000;
	private final static int baseHeight = 75;
	private final static int titleHeight = 60;
	private final static int rowHeight = 50;
	private static final float scale = 0.5f;
	private static final int maxNameLen = 30;

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
				int tableHeight = baseHeight + titleHeight * 2 + rowHeight * scores.size();
				
				addActor(new ResultsBackdrop());
				addActor(new MenuWindow(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT - tableHeight, width, tableHeight));
				table = new Table();
				table.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT - tableHeight);
				table.setSize(width, tableHeight);
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
		
		Text title = new Text(text, 0, 0, false);
		title.setScale(scale);
		
		Text playerLabel = new Text("PLAYER", 0, 0, false);
		playerLabel.setScale(scale);
		
		Text killsLabel = new Text("KILLS", 0, 0, false);
		killsLabel.setScale(scale);
		
		Text deathsLabel = new Text("DEATHS", 0, 0, false);
		deathsLabel.setScale(scale);
		
		Text scoreLabel = new Text("SCORE", 0, 0, false);
		scoreLabel.setScale(scale);
		
		Text statusLabel = new Text("STATUS", 0, 0, false);
		statusLabel.setScale(scale);
		
		table.add(title).height(titleHeight).colspan(5).row();
		table.add(playerLabel).height(titleHeight).padRight(20);
		table.add(killsLabel).height(titleHeight).padRight(20);
		table.add(deathsLabel).height(titleHeight).padRight(20);
		table.add(scoreLabel).height(titleHeight).padRight(20);
		table.add(statusLabel).height(titleHeight).row();
		
		for (SavedPlayerFields score: scores) {
			
			String displayedName = score.getName();
			
			if (displayedName.length() > maxNameLen) {
				displayedName = displayedName.substring(0, maxNameLen).concat("...");
			}
			
			Text name = new Text(displayedName, 0, 0, false);
			name.setScale(scale);
			
			Text kills = new Text(score.getKills() + " ", 0, 0, false);
			kills.setScale(scale);
			Text death = new Text(score.getDeaths() + " ", 0, 0, false);
			death.setScale(scale);
			Text points = new Text(score.getScore() + " ", 0, 0, false);
			points.setScale(scale);
			Text status = new Text(ready.get(score) ? "READY" : "WAITING", 0, 0, false);
			status.setScale(scale);

			table.add(name).height(rowHeight).padBottom(25);
			table.add(kills).height(rowHeight).padBottom(25);
			table.add(death).height(rowHeight).padBottom(25);
			table.add(points).height(rowHeight).padBottom(25);
			table.add(status).height(rowHeight).padBottom(25).row();
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
		
		forceReadyOption = new Text("FORCE RETURN?", 0, 0, true);
		
		forceReadyOption.addListener(new ClickListener() {
	        
			@Override
			public void clicked(InputEvent e, float x, float y) {
	        	
	        	//When pressed, the force ready option forces a transition.
				returnToHub();
	        }
	    });
		forceReadyOption.setScale(0.5f);
		
		table.add(readyOption).expandX();
		if (ps.isServer()) {
			table.add(forceReadyOption).expandX();
		}
	}
	
	/**
	 * This is pressed whenever a player gets ready.
	 * @param playerId: If this is run by the server, this is the player's connID (or 0, if the host themselves).
	 * For the client, playerId is the index in scores of thte player that readies.
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
			returnToHub();
			
		}
	}

	public void returnToHub() {
		if (ps.isServer()) {
			gsm.getApp().setRunAfterTransition(new Runnable() {

				@Override
				public void run() {
					gsm.removeState(ResultsState.class);
					gsm.gotoHubState();
				}
				
			});
		}
		gsm.getApp().fadeOut();
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
