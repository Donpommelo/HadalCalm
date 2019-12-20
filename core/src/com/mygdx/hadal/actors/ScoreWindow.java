package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.PlayState;

public class ScoreWindow {

	private static final int width = 800;
	private static final int height = 600;
	
	private static final float scale = 0.5f;
	
	private PlayState state;
	
	private Table table; 
	
	public ScoreWindow(PlayState state) {
		this.state = state;
		
		this.table = new Table().center();
		
		table.setVisible(false);
		
		if (state.isServer()) {
			for (SavedPlayerFields score: HadalGame.server.getScores().values()) {
				score.newLevelReset(state);
				syncTable();
			}
		} else {
			syncTable();
		}
	}
	
	public void syncTable() {
		table.clear();
		
		state.getStage().addActor(table);
		table.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, HadalGame.CONFIG_HEIGHT / 2 - height / 2);
		table.setWidth(width);
		table.setHeight(height);
		table.align(Align.top);
		
		table.add(new Text(HadalGame.assetManager, "PLAYER", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		table.add(new Text(HadalGame.assetManager, "KILLS", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		table.add(new Text(HadalGame.assetManager, "DEATH", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		table.add(new Text(HadalGame.assetManager, "SCORE", 0, 0, Color.WHITE)).padBottom(50).padRight(20);
		table.add(new Text(HadalGame.assetManager, "WINS", 0, 0, Color.WHITE)).padBottom(50).row();
		
		if (state.isServer()) {
			
			for (SavedPlayerFields field: HadalGame.server.getScores().values()) {			
				Text name = new Text(HadalGame.assetManager, field.getName(), 0, 0, Color.WHITE);
				name.setScale(scale);
				
				Text kills = new Text(HadalGame.assetManager, field.getKills() + " ", 0, 0, Color.WHITE);
				Text death = new Text(HadalGame.assetManager, field.getDeaths() + " ", 0, 0, Color.WHITE);
				Text points = new Text(HadalGame.assetManager, field.getScore() + " ", 0, 0, Color.WHITE);
				Text wins = new Text(HadalGame.assetManager, field.getWins() + " ", 0, 0, Color.WHITE);
					
				table.add(name).padBottom(25);
				table.add(kills).padBottom(25);
				table.add(death).padBottom(25);
				table.add(points).padBottom(25);
				table.add(wins).padBottom(25).row();
				
				HadalGame.server.sendToAllTCP(new Packets.SyncScore(HadalGame.server.getScores()));
				
				state.getUiExtra().syncData();
			}
		} else {
			for (SavedPlayerFields field: HadalGame.client.getScores().values()) {				
				Text name = new Text(HadalGame.assetManager, field.getName(), 0, 0, Color.WHITE);
				name.setScale(scale);
				
				Text kills = new Text(HadalGame.assetManager, field.getKills() + " ", 0, 0, Color.WHITE);
				Text death = new Text(HadalGame.assetManager, field.getDeaths() + " ", 0, 0, Color.WHITE);
				Text points = new Text(HadalGame.assetManager, field.getScore() + " ", 0, 0, Color.WHITE);
				Text wins = new Text(HadalGame.assetManager, field.getWins() + " ", 0, 0, Color.WHITE);
					
				table.add(name).padBottom(25);
				table.add(kills).padBottom(25);
				table.add(death).padBottom(25);
				table.add(points).padBottom(25);
				table.add(wins).padBottom(25).row();
				
				state.getUiExtra().syncData();
			}
		}
	}
	
	public void setVisibility(boolean visible) {
		table.setVisible(visible);
	}

	public Table getTable() {
		return table;
	}
}
