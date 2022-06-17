package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.save.SharedSetting;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.utils.Constants.MAX_NAME_LENGTH;

/**
 * The ScoreWindow is displayed when a player holds a button (default value tab) during a level
 * @author Forgarita Frelphos
 */
public class ScoreWindow {
	
	private final PlayState state;
	
	private final Table tableOptions, tableScore, tableSettings;
	private final MenuWindow windowOptions, windowScore;

	//Dimensions and position of the results menu
	private static final int scoreWidth = 1200;
	private static final int scoreBaseHeight = 50;
	private static final int scoreTitleHeight = 60;
	private static final int scoreRowHeight = 45;
	private static final int scoreNameWidth = 500;
	private static final float scoreTitleScale = 0.4f;
	private static final float scoreScale = 0.3f;
	private static final float scorePadX = 25.0f;
	private static final float scorePadY = 10.0f;

	private static final float scoreTitleHeightAim = 550.0f;
	private static final int scoreMaxPlayersAdjust = 18;
	private static final int scoreMinPlayersAdjust = 10;

	private static final int settingsWidth = 280;
	private static final int settingsHeight = 100;
	private static final int settingsRowHeight = 20;
	private static final float settingsScale = 0.25f;
	private static final float settingsPadY = 12.0f;

	private static final float optionsWidth = 100.0f;
	private static final float optionsHeight = 25.0f;
	private static final float optionsExtraHeight = 25.0f;

	public static final float artifactTagSize = 30.0f;
	private static final float artifactTagOffsetX = -100.0f;
	private static final float artifactTagOffsetY = 60.0f;
	private static final float artifactTagTargetWidth = 200.0f;

	private final Array<User> orderedUsers = new Array<>();

	public ScoreWindow(PlayState state) {
		this.state = state;

		this.tableScore = new Table();
		this.windowScore = new MenuWindow(0, 0, 0, 0);

		this.tableSettings = new Table();

		this.tableOptions = new Table();
		this.windowOptions = new MenuWindow(0, 0, 0, 0);

		tableScore.setVisible(false);
		windowScore.setVisible(false);
		tableSettings.setVisible(false);

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

		float scoreHeight = scoreRowHeight;
		float scorePad = scorePadY;

		//we calculate the size of each name based on the number of players
		int size;
		if (state.isServer()) {
			size = HadalGame.server.getUsers().size;
		} else {
			size = HadalGame.client.getUsers().size;
		}

		//if there are too many players, we start to gradually reduce the size + padding of each
		boolean shrink = size > scoreMinPlayersAdjust;
		if (shrink) {
			float rowHeight = scoreTitleHeightAim / Math.min(size, scoreMaxPlayersAdjust);
			scoreHeight = rowHeight * scoreRowHeight / (scoreRowHeight + scorePadY);
			scorePad = rowHeight * scorePadY / (scoreRowHeight + scorePadY);
		}

		//set table dimensions and location
		int tableHeight = scoreBaseHeight + scoreTitleHeight * 2;
		
		if (state.isServer()) {
			tableHeight += (scoreHeight + scorePad) * HadalGame.server.getUsers().size;
		} else {
			tableHeight += (scoreHeight + scorePad) * HadalGame.client.getUsers().size;
		}

		windowScore.setSize(scoreWidth, tableHeight);
		windowScore.setPosition((HadalGame.CONFIG_WIDTH - scoreWidth) / 2, HadalGame.CONFIG_HEIGHT - tableHeight);
		
		tableScore.setSize(scoreWidth, tableHeight);
		tableScore.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);

		//add table headings
		Text title = new Text(state.getMode().getName() + ": " + state.getLevel().getName());
		title.setScale(scoreTitleScale);
		
		Text playerLabel = new Text(UIText.PLAYER.text());
		playerLabel.setScale(scoreTitleScale);
		
		Text kdaLabel = new Text(UIText.KDA.text());
		kdaLabel.setScale(scoreTitleScale);

		Text scoreLabel = new Text(UIText.SCORE.text());
		scoreLabel.setScale(scoreTitleScale);
		
		Text winsLabel = new Text(UIText.WINS.text());
		winsLabel.setScale(scoreTitleScale);

		Text artifactsLabel = new Text(UIText.ARTIFACTS.text());
		artifactsLabel.setScale(scoreTitleScale);

		tableScore.add(title).height(scoreTitleHeight).colspan(6).row();
		tableScore.add(playerLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(kdaLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(scoreLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(winsLabel).height(scoreTitleHeight).padRight(scorePadX);
		tableScore.add(artifactsLabel).height(scoreTitleHeight).padBottom(scorePad).row();

		//add table entry for each player and sort according to score and spectator status
		orderedUsers.clear();
		if (state.isServer()) {
			for (ObjectMap.Entry<Integer, User> entry : HadalGame.server.getUsers().entries()) {
				orderedUsers.add(entry.value);
			}
		} else {
			for (ObjectMap.Entry<Integer, User> entry: HadalGame.client.getUsers().entries()) {
				orderedUsers.add(entry.value);
			}
		}

		orderedUsers.sort((a, b) -> {
			int cmp = (a.isSpectator() ? 1 : 0) - (b.isSpectator() ? 1 : 0);
			if (cmp == 0) { cmp = b.getScores().getScore() - a.getScores().getScore(); }
			return cmp;
		});

		for (User user : orderedUsers) {
			addEntry(user.getScores().getConnID(), user, scoreHeight, scorePad);
		}

		state.getStage().addActor(windowScore);
		state.getStage().addActor(tableScore);

		windowOptions.toFront();
		tableOptions.toFront();
		tableSettings.toFront();
	}
	
	/**
	 * This syncs the settings portion of the score window, visible on the right hand side.
	 * This works very similarly to the score table. It updates when the settings change and echo that change to all clients
	 */
	public void syncSettingTable() {
		tableSettings.clear();
		tableSettings.remove();

		//set table dimensions and location
		tableSettings.setSize(settingsWidth, settingsHeight);
		tableSettings.setPosition(HadalGame.CONFIG_WIDTH - settingsWidth, HadalGame.CONFIG_HEIGHT - settingsHeight);

		//add table headings
		Text slotsField = new Text(UIText.ARTIFACT_SLOTS.text());
		slotsField.setScale(settingsScale);

		Text serverSizeField = new Text(UIText.SERVER_CAPACITY.text());
		serverSizeField.setScale(settingsScale);

		//obtain settings. (host settings for clients)
		SharedSetting used = state.getGsm().getSharedSetting();
		if (state.isServer()) {
			HadalGame.server.sendToAllTCP(new Packets.SyncSharedSettings(state.getGsm().getSharedSetting()));
		} else {
			used = state.getGsm().getHostSetting();
		}

		Text slots = new Text(SettingState.artifactChoices[used.getArtifactSlots()]);
		slots.setScale(settingsScale);
		
		Text serverSize = new Text(SettingState.capacityChoices[used.getMaxPlayers()]);
		serverSize.setScale(settingsScale);
		
		tableSettings.add(slotsField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(slots).height(settingsRowHeight).padBottom(settingsPadY).row();

		tableSettings.add(serverSizeField).height(settingsRowHeight).padBottom(settingsPadY);
		tableSettings.add(serverSize).height(settingsRowHeight).padBottom(settingsPadY).row();

		state.getStage().addActor(tableSettings);

		windowOptions.toFront();
		tableOptions.toFront();
		tableSettings.toFront();
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
			Text mute = new Text("").setButton(true);
			if (user.isMuted()) {
				mute.setText(UIText.UNMUTE.text());
				mute.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						user.setMuted(false);
						tableOptions.remove();
						windowOptions.remove();
					}
				});
			} else {
				mute.setText(UIText.MUTE.text());
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
					Text ban = new Text(UIText.BAN.text()).setButton(true);
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

	/**
	 * 	helper method for adding a single entry to the score window
	 */
	private void addEntry(int connID, User user, float scoreHeight, float scorePad) {
		SavedPlayerFields field = user.getScores();

		String nameText = user.getNameAbridgedColored(MAX_NAME_LENGTH);

		Text name = new Text(field.getPingText() + nameText);
		name.setScale(scoreScale);
		name.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				openOptionsWindow(connID, user, e.getStageX(), e.getStageY());
			}
		});

		Text kda = new Text(field.getKills() + " / " + field.getDeaths() + " / " + field.getAssists());
		kda.setScale(scoreScale);
		Text points = new Text(field.getScore() + " ");
		points.setScale(scoreScale);
		Text wins = new Text(field.getWins() + " ");
		wins.setScale(scoreScale);

		//this displays the player's artifacts. Mouse over to see details
		Table tableArtifact = new Table();
		if (user.getPlayer() != null) {
			if (user.getPlayer().getPlayerData() != null) {
				for (UnlockArtifact c : user.getPlayer().getPlayerData().getLoadout().artifacts) {
					if (!c.equals(UnlockArtifact.NOTHING) && !c.isInvisible()) {
						ArtifactIcon newTag = new ArtifactIcon(c, c.getName() + "\n" + c.getDesc(),
								artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
						tableArtifact.add(newTag).width(artifactTagSize).height(artifactTagSize);
					}
				}
			}
		}

		tableScore.add(name).width(scoreNameWidth).height(scoreHeight).padBottom(scorePad).align(Align.center);
		tableScore.add(kda).height(scoreHeight).padRight(scorePadX).padBottom(scorePad);
		tableScore.add(points).height(scoreHeight).padRight(scorePadX).padBottom(scorePad);
		tableScore.add(wins).height(scoreHeight).padRight(scorePadX).padBottom(scorePad);
		tableScore.add(tableArtifact).height(scoreHeight).padRight(scorePadX).padBottom(scorePad).align(Align.center).row();

		//Adding new entries necessitates refreshing ui to account for new player's score
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
		tableOptions.remove();
		windowOptions.remove();
	}

	public Array<User> getOrderedUsers() { return orderedUsers; }
}
