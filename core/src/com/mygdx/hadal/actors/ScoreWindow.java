package com.mygdx.hadal.actors;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.UITagType;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.save.SharedSetting;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.StringManager;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH;

/**
 * The ScoreWindow is displayed when a player holds a button (default value tab) during a level
 * @author Forgarita Frelphos
 */
public class ScoreWindow {
	
	//Dimensions and position of the results menu
	private static final int SCORE_WIDTH = 1200;
	private static final int SCORE_BASE_HEIGHT = 50;
	private static final int SCORE_TITLE_HEIGHT = 60;
	private static final int SCORE_ROW_HEIGHT = 45;
	private static final int SCORE_NAME_WIDTH = 500;
	private static final float SCORE_TITLE_SCALE = 0.4f;
	private static final float SCORE_SCALE = 0.3f;
	private static final float SCORE_PAD_X = 25.0f;
	private static final float SCORE_PAD_Y = 10.0f;

	private static final float SCORE_TITLE_HEIGHT_AIM = 550.0f;
	private static final int SCORE_MAX_PLAYERS_ADJUST = 18;
	private static final int SCORE_MIN_PLAYERS_ADJUST = 10;

	private static final int SETTINGS_WIDTH = 280;
	private static final int SETTINGS_HEIGHT = 100;
	private static final int SETTINGS_ROW_HEIGHT = 20;
	private static final float SETTINGS_SCALE = 0.25f;
	private static final float SETTINGS_PAD_Y = 12.0f;

	private static final float OPTIONS_WIDTH = 100.0f;
	private static final float OPTIONS_HEIGHT = 25.0f;
	private static final float OPTIONS_EXTRA_HEIGHT = 25.0f;

	public static final float ARTIFACT_TAG_SIZE = 30.0f;
	private static final float ARTIFACT_TAG_OFFSET_X = -100.0f;
	private static final float ARTIFACT_TAG_OFFSET_Y = 60.0f;
	private static final float ARTIFACT_TAG_TARGET_WIDTH = 200.0f;

	private final PlayState state;

	private final Table tableOptions, tableScore, tableSettings;
	private final MenuWindow windowOptions, windowScore;

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

		float scoreHeight = SCORE_ROW_HEIGHT;
		float scorePad = SCORE_PAD_Y;

		//we calculate the size of each name based on the number of players
		int size = HadalGame.usm.getUsers().size;

		//if there are too many players, we start to gradually reduce the size + padding of each
		boolean shrink = size > SCORE_MIN_PLAYERS_ADJUST;
		if (shrink) {
			float rowHeight = SCORE_TITLE_HEIGHT_AIM / Math.min(size, SCORE_MAX_PLAYERS_ADJUST);
			scoreHeight = rowHeight * SCORE_ROW_HEIGHT / (SCORE_ROW_HEIGHT + SCORE_PAD_Y);
			scorePad = rowHeight * SCORE_PAD_Y / (SCORE_ROW_HEIGHT + SCORE_PAD_Y);
		}

		//set table dimensions and location
		int tableHeight = SCORE_BASE_HEIGHT + SCORE_TITLE_HEIGHT * 2;
		tableHeight += (int) ((scoreHeight + scorePad) * HadalGame.usm.getUsers().size);

		windowScore.setSize(SCORE_WIDTH, tableHeight);
		windowScore.setPosition((HadalGame.CONFIG_WIDTH - SCORE_WIDTH) / 2, HadalGame.CONFIG_HEIGHT - tableHeight);
		
		tableScore.setSize(SCORE_WIDTH, tableHeight);
		tableScore.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);

		//add table headings
		Text title = new Text(state.getMode().getName() + ": " + state.getLevel().getName());
		title.setScale(SCORE_TITLE_SCALE);
		
		Text playerLabel = new Text(UIText.PLAYER.text());
		playerLabel.setScale(SCORE_TITLE_SCALE);
		
		Text kdaLabel = new Text(UIText.KDA.text());
		kdaLabel.setScale(SCORE_TITLE_SCALE);

		Text scoreLabel = new Text(UIText.SCORE.text());
		scoreLabel.setScale(SCORE_TITLE_SCALE);
		
		Text winsLabel = new Text(UIText.WINS.text());
		winsLabel.setScale(SCORE_TITLE_SCALE);

		Text artifactsLabel = new Text(UIText.ARTIFACTS.text());
		artifactsLabel.setScale(SCORE_TITLE_SCALE);

		tableScore.add(title).height(SCORE_TITLE_HEIGHT).colspan(6).row();
		tableScore.add(playerLabel).height(SCORE_TITLE_HEIGHT).padRight(SCORE_PAD_X);
		tableScore.add(kdaLabel).height(SCORE_TITLE_HEIGHT).padRight(SCORE_PAD_X);
		tableScore.add(scoreLabel).height(SCORE_TITLE_HEIGHT).padRight(SCORE_PAD_X);
		tableScore.add(winsLabel).height(SCORE_TITLE_HEIGHT).padRight(SCORE_PAD_X);
		tableScore.add(artifactsLabel).height(SCORE_TITLE_HEIGHT).padBottom(scorePad).row();

		//add table entry for each player and sort according to score and spectator status
		orderedUsers.clear();
		for (ObjectMap.Entry<Integer, User> entry : HadalGame.usm.getUsers().entries()) {
			orderedUsers.add(entry.value);
		}

		orderedUsers.sort((a, b) -> {
			int cmp = (a.isSpectator() ? 1 : 0) - (b.isSpectator() ? 1 : 0);
			if (0 == cmp) { cmp = b.getScoreManager().getScore() - a.getScoreManager().getScore(); }

			//this makes the player always able to see their score at the top of scoreboards
			if (!a.isSpectator() && a.getConnID() == HadalGame.usm.getConnID()) {
				cmp = -1;
			}
			if (!b.isSpectator() && b.getConnID() == HadalGame.usm.getConnID()) {
				cmp = 1;
			}
			return cmp;
		});

		for (User user : orderedUsers) {
			addEntry(user.getConnID(), user, scoreHeight, scorePad);
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
		tableSettings.setSize(SETTINGS_WIDTH, SETTINGS_HEIGHT);
		tableSettings.setPosition(HadalGame.CONFIG_WIDTH - SETTINGS_WIDTH, HadalGame.CONFIG_HEIGHT - SETTINGS_HEIGHT);

		//add table headings
		Text slotsField = new Text(UIText.ARTIFACT_SLOTS.text());
		slotsField.setScale(SETTINGS_SCALE);

		Text serverSizeField = new Text(UIText.SERVER_CAPACITY.text());
		serverSizeField.setScale(SETTINGS_SCALE);

		//obtain settings. (host settings for clients)
		SharedSetting used = JSONManager.sharedSetting;
		if (state.isServer()) {
			PacketManager.serverTCPAll(new Packets.SyncSharedSettings(JSONManager.sharedSetting));
		} else {
			used = JSONManager.hostSetting;
		}

		Text slots = new Text(SettingState.ARTIFACT_CHOICES[used.getArtifactSlots()]);
		slots.setScale(SETTINGS_SCALE);
		
		Text serverSize = new Text(SettingState.CAPACITY_CHOICES[used.getMaxPlayers()]);
		serverSize.setScale(SETTINGS_SCALE);
		
		tableSettings.add(slotsField).height(SETTINGS_ROW_HEIGHT).padBottom(SETTINGS_PAD_Y);
		tableSettings.add(slots).height(SETTINGS_ROW_HEIGHT).padBottom(SETTINGS_PAD_Y).row();

		tableSettings.add(serverSizeField).height(SETTINGS_ROW_HEIGHT).padBottom(SETTINGS_PAD_Y);
		tableSettings.add(serverSize).height(SETTINGS_ROW_HEIGHT).padBottom(SETTINGS_PAD_Y).row();

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
		float height = OPTIONS_HEIGHT;
		if (state.isServer()) {
			if (0 != connID) {
				height += OPTIONS_EXTRA_HEIGHT;
			}
		}

		windowOptions.setSize(OPTIONS_WIDTH, height);
		windowOptions.setPosition(x, y);

		tableOptions.setSize(OPTIONS_WIDTH, height);
		tableOptions.setPosition(x, y);

		//user can mute/unmute players
		if (null != user) {
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
			mute.setScale(SETTINGS_SCALE);
			tableOptions.add(mute).height(OPTIONS_HEIGHT).pad(5).row();

			//only host can ban.
			if (state.isServer()) {
				//host cannot ban self
				if (0 != connID) {
					Text ban = new Text(UIText.BAN.text()).setButton(true);
					ban.setScale(SETTINGS_SCALE);
					ban.addListener(new ClickListener() {

						@Override
						public void clicked(InputEvent e, float x, float y) {
							HadalGame.server.kickPlayer(state, user, connID);
							tableOptions.remove();
							windowOptions.remove();
						}
					});
					tableOptions.add(ban).height(OPTIONS_HEIGHT);
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
		ScoreManager scoreManager = user.getScoreManager();
		StringManager stringManager = user.getStringManager();

		String nameText = stringManager.getNameAbridgedColored(MAX_NAME_LENGTH);

		Text name = new Text(stringManager.getPingText() + nameText);
		name.setScale(SCORE_SCALE);
		name.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				openOptionsWindow(connID, user, e.getStageX(), e.getStageY());
			}
		});

		Text kda = new Text(scoreManager.getKills() + " / " + scoreManager.getDeaths() + " / " + scoreManager.getAssists());
		kda.setScale(SCORE_SCALE);
		Text points = new Text(scoreManager.getScore() + " ");
		points.setScale(SCORE_SCALE);
		Text wins = new Text(scoreManager.getWins() + " ");
		wins.setScale(SCORE_SCALE);

		//this displays the player's artifacts. Mouse over to see details
		Table tableArtifact = new Table();
		for (UnlockArtifact c : user.getLoadoutManager().getActiveLoadout().artifacts) {
			if (!UnlockArtifact.NOTHING.equals(c) && !c.isInvisible()) {
				ArtifactIcon newTag = new ArtifactIcon(c, c.getName() + "\n" + c.getDesc(),
						ARTIFACT_TAG_OFFSET_X, ARTIFACT_TAG_OFFSET_Y, ARTIFACT_TAG_TARGET_WIDTH);
				tableArtifact.add(newTag).width(ARTIFACT_TAG_SIZE).height(ARTIFACT_TAG_SIZE);
			}
		}

		tableScore.add(name).width(SCORE_NAME_WIDTH).height(scoreHeight).padBottom(scorePad).align(Align.center);
		tableScore.add(kda).height(scoreHeight).padRight(SCORE_PAD_X).padBottom(scorePad);
		tableScore.add(points).height(scoreHeight).padRight(SCORE_PAD_X).padBottom(scorePad);
		tableScore.add(wins).height(scoreHeight).padRight(SCORE_PAD_X).padBottom(scorePad);
		tableScore.add(tableArtifact).height(scoreHeight).padRight(SCORE_PAD_X).padBottom(scorePad).align(Align.center).row();

		//Adding new entries necessitates refreshing ui to account for new player's score
		state.getUIManager().getUiExtra().syncUIText(UITagType.ALL);
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
