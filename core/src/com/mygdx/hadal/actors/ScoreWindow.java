package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.SharedSetting;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.SettingState;

/**
 * The ScoreWindow is displayed when a player holds a button (default value tab) during a level
 * @author Zachary Tu
 *
 */
public class ScoreWindow {
	
	private PlayState state;
	
	private Table tableScore, tableSettings; 
	private MenuWindow windowScore, windowSettings;
	private Text lives, timer, mode, loadout, slots, pause, serverSize;
	
	//Dimentions and position of the results menu
	private final static int scoreWidth = 1000;
	private final static int scoreBaseHeight = 75;
	private final static int scoreTitleHeight = 60;
	private final static int scoreRowHeight = 50;
	private static final float scoreScale = 0.5f;
	private static final float scorePadX = 20.0f;
	private static final float scorePadY = 25.0f;

	private static final int maxNameLen = 30;

	private final static int settingsWidth = 280;
	private final static int settingsHeight = 500;
	private final static int settingsRowHeight = 20;
	private static final float settingsScale = 0.25f;
	private static final float settingsPadY = 15.0f;

	public ScoreWindow(PlayState state) {
		this.state = state;
		
		this.tableScore = new Table().center();
		this.windowScore = new MenuWindow(0, 0, 0, 0);

		this.tableSettings = new Table().center();
		this.windowSettings = new MenuWindow(0, 0, 0, 0);
		
		tableScore.setVisible(false);
		windowScore.setVisible(false);
		tableSettings.setVisible(false);
		windowSettings.setVisible(false);
		
		//Server must first reset each score at the start of a level (unless just a stage transition)
		if (state.isServer() && state.isReset()) {
			for (SavedPlayerFields score: HadalGame.server.getScores().values()) {
				score.newLevelReset(state);
			}
		}
		syncScoreTable();
		syncSettingTable();
	}
	
	/**
	 * This clears the table and updates it score with new player information. It then updates the ui as well.
	 * On the server it is run when players are added/removed or when any player info (kills, deaths, score etc) are updated.
	 * The server then sends a packet to the client telling them to sync their table as well
	 */
	public void syncScoreTable() {
		tableScore.clear();
		tableScore.remove();
		windowScore.remove();
		
		int tableHeight = scoreBaseHeight + scoreTitleHeight * 2;
		
		if (state.isServer()) {
			tableHeight += scoreRowHeight * HadalGame.server.getScores().size();
		} else {
			tableHeight += scoreRowHeight * HadalGame.client.getScores().size();
		}
		
		windowScore.setSize(scoreWidth, tableHeight);
		windowScore.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);
		
		tableScore.setSize(scoreWidth, tableHeight);
		tableScore.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);
		
		Text title = new Text(state.getLevel().toString(), 0, 0, false);
		title.setScale(scoreScale);
		
		Text playerLabel = new Text("PLAYER", 0, 0, false);
		playerLabel.setScale(scoreScale);
		
		Text killsLabel = new Text("KILLS", 0, 0, false);
		killsLabel.setScale(scoreScale);
		
		Text deathsLabel = new Text("DEATHS", 0, 0, false);
		deathsLabel.setScale(scoreScale);
		
		Text scoreLabel = new Text("SCORE", 0, 0, false);
		scoreLabel.setScale(scoreScale);
		
		Text winsLabel = new Text("WINS", 0, 0, false);
		winsLabel.setScale(scoreScale);
		
		tableScore.add(title).height(scoreTitleHeight).colspan(5).row();
		tableScore.add(playerLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(killsLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(deathsLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(scoreLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(winsLabel).height(scoreTitleHeight).row();
		
		if (state.isServer()) {
			
			for (SavedPlayerFields field: HadalGame.server.getScores().values()) {			
				
				String displayedName = field.getName();
				
				if (displayedName.length() > maxNameLen) {
					displayedName = displayedName.substring(0, maxNameLen).concat("...");
				}
				
				Text name = new Text(displayedName, 0, 0, false);
				name.setScale(scoreScale);
				
				Text kills = new Text(field.getKills() + " ", 0, 0, false);
				kills.setScale(scoreScale);
				Text death = new Text(field.getDeaths() + " ", 0, 0, false);
				death.setScale(scoreScale);
				Text points = new Text(field.getScore() + " ", 0, 0, false);
				points.setScale(scoreScale);
				Text wins = new Text(field.getWins() + " ", 0, 0, false);
				wins.setScale(scoreScale);

				tableScore.add(name).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(kills).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(death).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(points).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(wins).height(scoreRowHeight).padBottom(scorePadY).row();
				
				HadalGame.server.sendToAllUDP(new Packets.SyncScore(HadalGame.server.getScores()));
				
				state.getUiExtra().syncData();
			}
		} else {
			for (SavedPlayerFields field: HadalGame.client.getScores().values()) {				
				
				String displayedName = field.getName();
				
				if (displayedName.length() > maxNameLen) {
					displayedName = displayedName.substring(0, maxNameLen).concat("...");
				}
				
				Text name = new Text(displayedName, 0, 0, false);
				name.setScale(scoreScale);
				
				Text kills = new Text(field.getKills() + " ", 0, 0, false);
				kills.setScale(scoreScale);
				Text death = new Text(field.getDeaths() + " ", 0, 0, false);
				death.setScale(scoreScale);
				Text points = new Text(field.getScore() + " ", 0, 0, false);
				points.setScale(scoreScale);
				Text wins = new Text(field.getWins() + " ", 0, 0, false);
				wins.setScale(scoreScale);

				tableScore.add(name).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(kills).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(death).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(points).height(scoreRowHeight).padBottom(scorePadY);
				tableScore.add(wins).height(scoreRowHeight).padBottom(scorePadY).row();
				
				state.getUiExtra().syncData();
			}
		}
		
		state.getStage().addActor(windowScore);
		state.getStage().addActor(tableScore);
	}
	
	/**
	 * This syncs the settings portion of the score window, visible on the right hand side.
	 * This works very similarly to the score table. It updates when the settings change and echo that change to all clients
	 */
	public void syncSettingTable() {
		tableSettings.clear();
		tableSettings.remove();
		windowSettings.remove();
		
		windowSettings.setSize(settingsWidth, settingsHeight);
		windowSettings.setPosition(HadalGame.CONFIG_WIDTH - settingsWidth, HadalGame.CONFIG_HEIGHT - settingsHeight);
		
		tableSettings.setSize(settingsWidth, settingsHeight);
		tableSettings.setPosition(HadalGame.CONFIG_WIDTH - settingsWidth, HadalGame.CONFIG_HEIGHT - settingsHeight);
		
		Text title = new Text("SERVER SETTINGS", 0, 0, false);
		title.setScale(settingsScale);
		
		Text timerField = new Text("TIMER:", 0, 0, false);
		timerField.setScale(settingsScale);
		
		Text livesField = new Text("LIVES: ", 0, 0, false);
		livesField.setScale(settingsScale);
		
		Text modeField = new Text("MODE: ", 0, 0, false);
		modeField.setScale(settingsScale);
		
		Text loadoutField = new Text("LOADOUT: ", 0, 0, false);
		loadoutField.setScale(settingsScale);
		
		Text slotsField = new Text("ARTIFACT SLOTS: ", 0, 0, false);
		slotsField.setScale(settingsScale);
		
		Text pauseField = new Text("PAUSE ENABLED: ", 0, 0, false);
		pauseField.setScale(settingsScale);
		
		Text serverSizeField = new Text("SERVER CAPACITY: ", 0, 0, false);
		serverSizeField.setScale(settingsScale);

		
		SharedSetting used = state.getGsm().getSharedSetting();
		if (state.isServer()) {
			HadalGame.server.sendToAllUDP(new Packets.SyncSharedSettings(state.getGsm().getSharedSetting()));
		} else {
			used = state.getGsm().getHostSetting();
		}
		
		timer = new Text(SettingState.timerChoices[used.getTimer()], 0, 0, false);
		timer.setScale(settingsScale);
		
		lives = new Text(SettingState.livesChoices[used.getLives()], 0, 0, false);
		lives.setScale(settingsScale);
		
		mode = new Text(SettingState.modeChoices[used.getPvpMode()], 0, 0, false);
		mode.setScale(settingsScale);
		
		loadout = new Text(SettingState.loadoutChoices[used.getLoadoutType()], 0, 0, false);
		loadout.setScale(settingsScale);
		
		slots = new Text(SettingState.artifactChoices[used.getArtifactSlots()], 0, 0, false);
		slots.setScale(settingsScale);
		
		pause = new Text("" + used.isMultiplayerPause(), 0, 0, false);
		pause.setScale(settingsScale);
		
		serverSize = new Text(SettingState.capacityChoices[used.getMaxPlayers()], 0, 0, false);
		serverSize.setScale(settingsScale);
		
		tableSettings.add(title).height(settingsRowHeight).colspan(2).row();
		
		tableSettings.add(timerField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(timer).height(settingsRowHeight).padBottom(settingsPadY).row();
		
		tableSettings.add(livesField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(lives).height(settingsRowHeight).padBottom(settingsPadY).row();

		tableSettings.add(modeField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(mode).height(settingsRowHeight).padBottom(settingsPadY).row();

		tableSettings.add(loadoutField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(loadout).height(settingsRowHeight).padBottom(settingsPadY).row();

		tableSettings.add(slotsField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(slots).height(settingsRowHeight).padBottom(settingsPadY).row();

		tableSettings.add(pauseField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(pause).height(settingsRowHeight).padBottom(settingsPadY).row();

		tableSettings.add(serverSizeField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(serverSize).height(settingsRowHeight).padBottom(settingsPadY).row();

		state.getStage().addActor(windowSettings);
		state.getStage().addActor(tableSettings);
	}
	
	/**
	 * This sets the visibility of this ui element. 
	 * Usually, this is toggled on and off with the tab button
	 */
	public void setVisibility(boolean visible) { 
		tableScore.setVisible(visible);
		windowScore.setVisible(visible);
		tableSettings.setVisible(visible);
		windowSettings.setVisible(visible);
	}
}
