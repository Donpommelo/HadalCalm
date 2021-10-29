package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.map.SettingLives;
import com.mygdx.hadal.map.SettingTimer;
import com.mygdx.hadal.save.SharedSetting;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.SettingState;

import java.util.ArrayList;
import java.util.Map.Entry;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 * The ScoreWindow is displayed when a player holds a button (default value tab) during a level
 * @author Forgarita Frelphos
 */
public class ScoreWindow {
	
	private final PlayState state;
	
	private final Table tableOptions, tableScore, tableSettings;
	private final MenuWindow windowOptions, windowScore, windowSettings;

	//Dimensions and position of the results menu
	private static final int scoreWidth = 1000;
	private static final int scoreBaseHeight = 75;
	private static final int scoreTitleHeight = 60;
	private static final int scoreRowHeight = 50;
	private static final int scoreNameWidth = 500;
	private static final float scoreScale = 0.5f;
	private static final float scorePadX = 20.0f;
	private static final float scorePadY = 25.0f;

	private static final int settingsWidth = 280;
	private static final int settingsHeight = 500;
	private static final int settingsRowHeight = 20;
	private static final float settingsScale = 0.25f;
	private static final float settingsPadY = 15.0f;

	private static final float optionsWidth = 100.0f;
	private static final float optionsHeight = 25.0f;
	private static final float optionsExtraHeight = 25.0f;

	private final ArrayList<User> orderedUsers = new ArrayList<>();

	public ScoreWindow(PlayState state) {
		this.state = state;

		this.tableScore = new Table();
		this.windowScore = new MenuWindow(0, 0, 0, 0);

		this.tableSettings = new Table();
		this.windowSettings = new MenuWindow(0, 0, 0, 0);

		this.tableOptions = new Table();
		this.windowOptions = new MenuWindow(0, 0, 0, 0);

		tableScore.setVisible(false);
		windowScore.setVisible(false);
		tableSettings.setVisible(false);
		windowSettings.setVisible(false);

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

		//set table dimensions and location
		int tableHeight = scoreBaseHeight + scoreTitleHeight * 2;
		
		if (state.isServer()) {
			tableHeight += scoreRowHeight * HadalGame.server.getUsers().size();
		} else {
			tableHeight += scoreRowHeight * HadalGame.client.getUsers().size();
		}
		
		windowScore.setSize(scoreWidth, tableHeight);
		windowScore.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);
		
		tableScore.setSize(scoreWidth, tableHeight);
		tableScore.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);

		//add table headings
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

		//add table entry for each player and sort according to score
		orderedUsers.clear();
		if (state.isServer()) {
			for (Entry<Integer, User> entry : HadalGame.server.getUsers().entrySet()) {
				orderedUsers.add(entry.getValue());
			}
		} else {
			for (Entry<Integer, User> entry: HadalGame.client.getUsers().entrySet()) {
				orderedUsers.add(entry.getValue());
			}
		}

		orderedUsers.sort((a, b) -> {
			int cmp = (a.isSpectator() ? 1 : 0) - (b.isSpectator() ? 1 : 0);
			if (cmp == 0) { cmp = b.getScores().getScore() - a.getScores().getScore(); }
			return cmp;
		});

		for (User user: orderedUsers) {
			addEntry(user.getScores().getConnID(), user);
		}

		state.getStage().addActor(windowScore);
		state.getStage().addActor(tableScore);

		windowOptions.toFront();
		tableOptions.toFront();
	}
	
	/**
	 * This syncs the settings portion of the score window, visible on the right hand side.
	 * This works very similarly to the score table. It updates when the settings change and echo that change to all clients
	 */
	public void syncSettingTable() {
		tableSettings.clear();
		tableSettings.remove();
		windowSettings.remove();

		//set table dimensions and location
		windowSettings.setSize(settingsWidth, settingsHeight);
		windowSettings.setPosition(HadalGame.CONFIG_WIDTH - settingsWidth, HadalGame.CONFIG_HEIGHT - settingsHeight);
		
		tableSettings.setSize(settingsWidth, settingsHeight);
		tableSettings.setPosition(HadalGame.CONFIG_WIDTH - settingsWidth, HadalGame.CONFIG_HEIGHT - settingsHeight);

		//add table headings
		Text title = new Text("SERVER SETTINGS", 0, 0, false);
		title.setScale(settingsScale);
		
		Text pvpTimerField = new Text("PVP TIMER:", 0, 0, false);
		pvpTimerField.setScale(settingsScale);
		
		Text livesField = new Text("LIVES: ", 0, 0, false);
		livesField.setScale(settingsScale);

		Text slotsField = new Text("ARTIFACT SLOTS: ", 0, 0, false);
		slotsField.setScale(settingsScale);
		
		Text pauseField = new Text("PAUSE ENABLED: ", 0, 0, false);
		pauseField.setScale(settingsScale);
		
		Text serverSizeField = new Text("SERVER CAPACITY: ", 0, 0, false);
		serverSizeField.setScale(settingsScale);

		//obtain settings. (host settings for clients)
		SharedSetting used = state.getGsm().getSharedSetting();
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncSharedSettings(state.getGsm().getSharedSetting()));
		} else {
			used = state.getGsm().getHostSetting();
		}

		//set and add setting info
		Text pvpTimer = new Text(SettingTimer.timerChoices[used.getPVPTimer()], 0, 0, false);
		pvpTimer.setScale(settingsScale);
		
		Text lives = new Text(SettingLives.livesChoices[used.getLives()], 0, 0, false);
		lives.setScale(settingsScale);
		
		Text slots = new Text(SettingState.artifactChoices[used.getArtifactSlots()], 0, 0, false);
		slots.setScale(settingsScale);
		
		Text pause = new Text("" + used.isMultiplayerPause(), 0, 0, false);
		pause.setScale(settingsScale);
		
		Text serverSize = new Text(SettingState.capacityChoices[used.getMaxPlayers()], 0, 0, false);
		serverSize.setScale(settingsScale);
		
		tableSettings.add(title).height(settingsRowHeight).colspan(2).row();
		
		tableSettings.add(pvpTimerField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(pvpTimer).height(settingsRowHeight).padBottom(settingsPadY).row();
		
		tableSettings.add(livesField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(lives).height(settingsRowHeight).padBottom(settingsPadY).row();

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
	 * When the player clicks on a name, options to mute/ban appear
	 */
	private void openOptionsWindow(int connID, User user, float x, float y) {

		tableOptions.clear();
		tableOptions.remove();
		windowOptions.remove();

		//ui height scales to number of options available
		float height = optionsHeight;
		if (state.isServer()) {
			if (connID != 0) {
				height += optionsExtraHeight;
			}
		}

		windowOptions.setSize(optionsWidth, height);
		windowOptions.setPosition(x, y);

		tableOptions.setSize(optionsWidth, height);
		tableOptions.setPosition(x, y);

		//user can mute/unmute players
		if (user != null) {
			Text mute = new Text("", 0, 0, true);
			if (user.isMuted()) {
				mute.setText("UNMUTE");
				mute.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						user.setMuted(false);
						tableOptions.remove();
						windowOptions.remove();
					}
				});
			} else {
				mute.setText("MUTE");
				mute.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						user.setMuted(true);
						tableOptions.remove();
						windowOptions.remove();
					}
				});
			}
			mute.setScale(settingsScale);
			tableOptions.add(mute).height(optionsHeight).pad(5).row();

			//only host can ban.
			if (state.isServer()) {
				//host cannot ban self
				if (connID != 0) {
					Text ban = new Text("BAN", 0, 0, true);
					ban.setScale(settingsScale);
					ban.addListener(new ClickListener() {

						@Override
						public void clicked(InputEvent e, float x, float y) {
							HadalGame.server.kickPlayer(state, user, connID);
							tableOptions.remove();
							windowOptions.remove();
						}
					});
					tableOptions.add(ban).height(optionsHeight);
				}
			}
		}

		state.getStage().addActor(windowOptions);
		state.getStage().addActor(tableOptions);
	}

	//helper method for adding a single entry to the score window
	private void addEntry(int connID, User user) {
		SavedPlayerFields field = user.getScores();

		String nameText = user.getNameAbridgedColored(MAX_NAME_LENGTH);

		Text name = new Text(field.getPingText() + nameText, 0, 0, false);
		name.setScale(scoreScale);
		name.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				openOptionsWindow(connID, user, e.getStageX(), e.getStageY());
			}
		});

		Text kills = new Text(field.getKills() + " ", 0, 0, false);
		kills.setScale(scoreScale);
		Text death = new Text(field.getDeaths() + " ", 0, 0, false);
		death.setScale(scoreScale);
		Text points = new Text(field.getScore() + " ", 0, 0, false);
		points.setScale(scoreScale);
		Text wins = new Text(field.getWins() + " ", 0, 0, false);
		wins.setScale(scoreScale);

		tableScore.add(name).width(scoreNameWidth).height(scoreRowHeight).padBottom(scorePadY).align(Align.center);
		tableScore.add(kills).height(scoreRowHeight).padBottom(scorePadY);
		tableScore.add(death).height(scoreRowHeight).padBottom(scorePadY);
		tableScore.add(points).height(scoreRowHeight).padBottom(scorePadY);
		tableScore.add(wins).height(scoreRowHeight).padBottom(scorePadY).row();

		state.getUiExtra().syncUIText(UITag.uiType.ALL);
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

		tableOptions.remove();
		windowOptions.remove();
	}

	public ArrayList<User> getOrderedUsers() { return orderedUsers; }
}
