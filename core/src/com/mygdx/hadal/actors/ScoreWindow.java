package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.PlayState;

/**
 * The ScoreWindow is displayed when a player holds a button (default value tab) during a level
 * @author Zachary Tu
 *
 */
public class ScoreWindow {
	
	private PlayState state;
	
	private Table table; 
	private MenuWindow window;
	
	//Dimentions and position of the results menu
	private final static int width = 1000;
	private final static int baseHeight = 75;
	private final static int titleHeight = 60;
	private final static int rowHeight = 50;
	private static final float scale = 0.5f;
	private static final int maxNameLen = 30;

	public ScoreWindow(PlayState state) {
		this.state = state;
		
		this.table = new Table().center();
		this.window = new MenuWindow(0, 0, 0, 0);

		table.setVisible(false);
		window.setVisible(false);
		
		//Server must first reset each score at the start of a level (unless just a stage transition)
		if (state.isServer() && state.isReset()) {
			for (SavedPlayerFields score: HadalGame.server.getScores().values()) {
				score.newLevelReset(state);
			}
		}
		syncTable();
	}
	
	/**
	 * This clears the table and updates it score with new player information. It then updates the ui as well.
	 * On the server it is run when players are added/removed or when any player info (kills, deaths, score etc) are updated.
	 * The server then sends a packet to the client telling them to sync their table as well
	 */
	public void syncTable() {
		table.clear();
		table.remove();
		window.remove();
		
		int tableHeight = baseHeight + titleHeight * 2;
		
		if (state.isServer()) {
			tableHeight += rowHeight * HadalGame.server.getScores().size();
		} else {
			tableHeight += rowHeight * HadalGame.client.getScores().size();
		}
		
		window.setSize(width, tableHeight);
		window.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT - tableHeight);
		
		table.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT - tableHeight);
		table.setSize(width, tableHeight);
		
		Text title = new Text(state.getLevel().toString(), 0, 0, false);
		title.setScale(scale);
		
		Text playerLabel = new Text("PLAYER", 0, 0, false);
		playerLabel.setScale(scale);
		
		Text killsLabel = new Text("KILLS", 0, 0, false);
		killsLabel.setScale(scale);
		
		Text deathsLabel = new Text("DEATHS", 0, 0, false);
		deathsLabel.setScale(scale);
		
		Text scoreLabel = new Text("SCORE", 0, 0, false);
		scoreLabel.setScale(scale);
		
		Text winsLabel = new Text("WINS", 0, 0, false);
		winsLabel.setScale(scale);
		
		table.add(title).height(titleHeight).colspan(5).row();
		table.add(playerLabel).height(titleHeight).padRight(20);
		table.add(killsLabel).height(titleHeight).padRight(20);
		table.add(deathsLabel).height(titleHeight).padRight(20);
		table.add(scoreLabel).height(titleHeight).padRight(20);
		table.add(winsLabel).height(titleHeight).row();
		
		if (state.isServer()) {
			
			for (SavedPlayerFields field: HadalGame.server.getScores().values()) {			
				
				String displayedName = field.getName();
				
				if (displayedName.length() > maxNameLen) {
					displayedName = displayedName.substring(0, maxNameLen).concat("...");
				}
				
				Text name = new Text(displayedName, 0, 0, false);
				name.setScale(scale);
				
				Text kills = new Text(field.getKills() + " ", 0, 0, false);
				kills.setScale(scale);
				Text death = new Text(field.getDeaths() + " ", 0, 0, false);
				death.setScale(scale);
				Text points = new Text(field.getScore() + " ", 0, 0, false);
				points.setScale(scale);
				Text wins = new Text(field.getWins() + " ", 0, 0, false);
				wins.setScale(scale);

				table.add(name).height(rowHeight).padBottom(25);
				table.add(kills).height(rowHeight).padBottom(25);
				table.add(death).height(rowHeight).padBottom(25);
				table.add(points).height(rowHeight).padBottom(25);
				table.add(wins).height(rowHeight).padBottom(25).row();
				
				HadalGame.server.sendToAllTCP(new Packets.SyncScore(HadalGame.server.getScores()));
				
				state.getUiExtra().syncData();
			}
		} else {
			for (SavedPlayerFields field: HadalGame.client.getScores().values()) {				
				
				String displayedName = field.getName();
				
				if (displayedName.length() > maxNameLen) {
					displayedName = displayedName.substring(0, maxNameLen).concat("...");
				}
				
				Text name = new Text(displayedName, 0, 0, false);
				name.setScale(scale);
				
				Text kills = new Text(field.getKills() + " ", 0, 0, false);
				kills.setScale(scale);
				Text death = new Text(field.getDeaths() + " ", 0, 0, false);
				death.setScale(scale);
				Text points = new Text(field.getScore() + " ", 0, 0, false);
				points.setScale(scale);
				Text wins = new Text(field.getWins() + " ", 0, 0, false);
				wins.setScale(scale);

				table.add(name).height(rowHeight).padBottom(25);
				table.add(kills).height(rowHeight).padBottom(25);
				table.add(death).height(rowHeight).padBottom(25);
				table.add(points).height(rowHeight).padBottom(25);
				table.add(wins).height(rowHeight).padBottom(25).row();
				
				state.getUiExtra().syncData();
			}
		}
		
		state.getStage().addActor(window);
		state.getStage().addActor(table);
	}
	
	public void setVisibility(boolean visible) { 
		table.setVisible(visible);
		window.setVisible(visible);
	}
}
