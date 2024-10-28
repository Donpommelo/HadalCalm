package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.*;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.LoadoutManager;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.StatsManager;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.constants.Constants.*;
import static com.mygdx.hadal.managers.SkinManager.SKIN;

/**
 * The Results screen appears at the end of levels and displays the player's results
 * In this screen, the player can return to the hub when all players are ready.
 * @author Juppstein Jodswallop
 */
public class ResultsState extends GameState {

	//Dimensions and position of the results menu
	private static final int TABLE_X = 20;
	private static final int TABLE_Y = 240;
	private static final int TABLE_WIDTH = 1240;
	private static final int TABLE_HEIGHT = 460;
	private static final int CHARACTER_SCROLL_HEIGHT = 430;

	private static final int INFO_X_ENABLED = 880;
	private static final int INFO_Y_ENABLED = 20;
	private static final int INFO_X = 1280;
	private static final int INFO_Y = 20;
	private static final int INFO_WIDTH = 380;
	private static final int INFO_HEIGHT = 200;

	private static final int INFO_ROW_HEIGHT = 20;
	private static final float INFO_TEXT_SCALE = 0.25f;
	private static final float INFO_PAD_Y = 15.0f;
	private static final float INFO_PAD_Y_SMALL = 5.0f;

	public static final int INFO_NAME_HEIGHT = 30;
	public static final int INFO_SCROLL_HEIGHT = 100;
	public static final int INFO_NAME_PAD = 15;

	private static final int TABLE_EXTRA_X = 420;
	private static final int TABLE_EXTRA_Y = 20;
	private static final int TABLE_EXTRA_WIDTH = 440;
	private static final int TABLE_EXTRA_HEIGHT = 200;
	private static final int MAP_OPTIONS_WIDTH = 200;
	private static final int MAP_OPTIONS_HEIGHT = 30;
	private static final int OPTIONS_PAD = 10;

	private static final int TITLE_HEIGHT = 40;
	private static final float RESULTS_SCALE = 0.6f;
	private static final float SCALE = 0.4f;

	private static final int OPTION_HEIGHT = 50;

	public static final float ARTIFACT_TAG_SIZE = 40.0f;
	private static final float ARTIFACT_TAG_OFFSET_X = -100.0f;
	private static final float ARTIFACT_TAG_OFFSET_Y = 60.0f;
	private static final float ARTIFACT_TAG_TARGET_WIDTH = 200.0f;

	private static final float PARTICLE_OFFSET_X = 75.0f;
	private static final float PARTICLE_OFFSET_Y = 150.0f;

	private static final float SCROLL_ACCELERATION = 8.0f;

	private static final int MESSAGE_X = 20;
	private static final int MESSAGE_Y = 20;

	//if the results text is equal to the magic word, calculate the results text based on score
	public static final String MAGIC_WORD = "fug";

	//This table contains the options for the title.
	private Table table, tableInfo, tableInfoOuter, tableArtifact, tableExtra;
	private ScrollPane infoScroll, charactersScroll;
	private CheckBox returnToHub;
	private SelectBox<String> nextMapNames;

	//this displays extra post-game stats about a selected player
	private Text infoPlayerName;

	//This is the playstate that the results state is placed on top of. Used to access the state's message window
	protected final PlayState ps;

	//This is a list of all the saved player fields (scores) from the completed playstate
	protected final Array<User> users = new Array<>();
    private final Array<PlayerResultsIcon> icons = new Array<>();
	private final Array<PooledEffect> effects = new Array<>();

	//list of map options that the host can select as next map if not returning to hub
	private final Array<UnlockLevel> nextMaps = new Array<>();

	//this text is displayed at the top of the state and usually indicates victory or loss
	private final String text;

	//did this player win the last game?
	private boolean won;

	//these are used to process the fade transition from a playstate
	private final FrameBuffer fbo;
	private TextureRegion snapshot;
	private final Shader shader;

	/**
	 * Constructor will be called whenever the game transitions into a results state
	 * @param text: this is the string that is displayed at the top of the result state
	 */
	public ResultsState(HadalGame app, String text, PlayState ps, FrameBuffer fbo) {
		super(app);
		this.text = text;
		this.ps = ps;
		this.fbo = fbo;
		this.shader = Shader.PERLIN_FADE;
		initializeVisuals();

		//First, we obtain the list of scores
		for (User user : HadalGame.usm.getUsers().values()) {
			if (!user.isSpectator()) {
				users.add(user);
				if (user.getConnID() == HadalGame.usm.getConnID()) {
					won = user.getScoreManager().isWonLast();
				}
			}
		}

		if (!users.isEmpty()) {
			JSONManager.record.updateScore(users.get(0).getScoreManager().getScore(), ps.level);
		}

		//Then, we sort according to score and give the winner(s) a win. Being on the winning team overrides score
		users.sort((a, b) -> {
			int cmp = (b.getScoreManager().isWonLast() ? 1 : 0) - (a.getScoreManager().isWonLast() ? 1 : 0);
			if (cmp == 0) { cmp = b.getScoreManager().getTeamScore() - a.getScoreManager().getTeamScore(); }
			if (cmp == 0) { cmp = b.getScoreManager().getScore() - a.getScoreManager().getScore(); }
			if (cmp == 0) { cmp = b.getScoreManager().getKills() - a.getScoreManager().getKills(); }
			if (cmp == 0) { cmp = a.getScoreManager().getDeaths() - b.getScoreManager().getDeaths(); }
			return cmp;
		});

		//Finally we initialize the ready map with everyone set to not ready. Bots don't need to ready up
		for (User user : users) {
			user.getScoreManager().setReady(user.getConnID() < 0);
		}
	}

	public void initializeVisuals() {
		shader.loadShader();
		this.snapshot = new TextureRegion(fbo.getColorBufferTexture(), 0, fbo.getHeight(), fbo.getWidth(), -fbo.getHeight());
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new Backdrop(AssetList.RESULTS_CARD.toString()));

				table = new TableWindow();
				table.setPosition(TABLE_X, TABLE_Y);
				table.setSize(TABLE_WIDTH, TABLE_HEIGHT);
				addActor(table);
				syncScoreTable();

				tableInfoOuter = new TableWindow();

				infoPlayerName = new Text("");
				infoPlayerName.setScale(INFO_TEXT_SCALE);

				tableInfo = new Table();
				tableArtifact = new Table();

				infoScroll = new ScrollPane(tableInfo, SKIN);
				infoScroll.setFadeScrollBars(false);

				infoScroll.addListener(new InputListener() {

					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						stage.setScrollFocus(infoScroll);
					}
				});

				tableInfoOuter.add(infoPlayerName).pad(INFO_NAME_PAD).height(INFO_NAME_HEIGHT).row();
				tableInfoOuter.add(tableArtifact).height(INFO_NAME_HEIGHT).row();
				tableInfoOuter.add(infoScroll).width(INFO_WIDTH).height(INFO_SCROLL_HEIGHT);
				tableInfoOuter.setPosition(INFO_X, INFO_Y);
				tableInfoOuter.setSize(INFO_WIDTH, INFO_HEIGHT);

				addActor(tableInfoOuter);

				tableExtra = new TableWindow();

				//These are all of the display and buttons visible to the player.
				final Text readyOption = new Text(UIText.READY.text()).setButton(true);

				readyOption.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						//When pressed, the ready option indicates to the server that that player is ready.
						if (ps.isServer()) {
							readyPlayer(0);
						} else {
							PacketManager.clientTCP(new Packets.ClientReady());
						}
					}
				});
				readyOption.setScale(SCALE);

				if (HadalGame.usm.isHost()) {
					final Text forceReadyOption = new Text(UIText.FORCE_READY.text()).setButton(true);

					forceReadyOption.addListener(new ClickListener() {

						@Override
						public void clicked(InputEvent e, float x, float y) {

							//When pressed, the force ready option forces a transition.
							if (ps.isServer()) {
								allReady();
							} else {
								PacketManager.clientTCP(new Packets.ClientNextMapResponse(isReturnToHub(), getNextMap()));
							}
						}
					});
					forceReadyOption.setScale(SCALE);

					tableExtra.add(readyOption).height(OPTION_HEIGHT).colspan(2).row();
					tableExtra.add(forceReadyOption).height(OPTION_HEIGHT).colspan(2).row();

					returnToHub = new CheckBox(UIText.RETURN_HUB.text(), SKIN);
					returnToHub.setChecked(JSONManager.setting.isReturnToHubOnReady());

					Array<String> compliantMaps = new Array<>();
					Array<UnlockManager.UnlockTag> unlockTags = new Array<>();
					nextMaps.clear();
					for (UnlockLevel c : UnlockLevel.getUnlocks(false, unlockTags)) {
						for (int i = 0; i < c.getModes().length; i++) {
							if (c.getModes()[i] == ps.mode.getCheckCompliance()) {
								compliantMaps.add(c.getName());
								nextMaps.add(c);
								break;
							}
						}
					}

					nextMapNames = new SelectBox<>(SKIN);
					nextMapNames.setItems(compliantMaps);
					nextMapNames.setWidth(INFO_WIDTH);
					nextMapNames.setDisabled(returnToHub.isChecked());

					int currentMapIndex = nextMaps.indexOf(ps.level, false);
					if (currentMapIndex != -1) {
						nextMapNames.setSelectedIndex(currentMapIndex);
					}

					returnToHub.addListener(new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							nextMapNames.setDisabled(returnToHub.isChecked());
							JSONManager.setting.setReturnToHubOnReady(returnToHub.isChecked());
						}
					});

					tableExtra.add(returnToHub).height(OPTION_HEIGHT);
					tableExtra.add(nextMapNames).width(MAP_OPTIONS_WIDTH).height(MAP_OPTIONS_HEIGHT).pad(OPTIONS_PAD);
				} else {
					tableExtra.add(readyOption).height(OPTION_HEIGHT);
				}

				tableExtra.setPosition(TABLE_EXTRA_X, TABLE_EXTRA_Y);
				tableExtra.setSize(TABLE_EXTRA_WIDTH, TABLE_EXTRA_HEIGHT);
				addActor(tableExtra);
			}
		};

		//we pull up and lock the playstate message window so players can chat in the aftergame.
		if (!ps.getUIManager().getMessageWindow().isActive()) {
			ps.getUIManager().getMessageWindow().toggleWindow();
		}

		//we start off playing no music. Results music only starts after playstate transition fade occurs
		MusicPlayer.playSong(MusicTrackType.NOTHING, 1.0f);

		ps.getUIManager().getMessageWindow().setLocked(true);
		ps.getUIManager().getMessageWindow().table.setPosition(MESSAGE_X, MESSAGE_Y);
		stage.addActor(ps.getUIManager().getMessageWindow().table);
		FadeManager.fadeIn();
		app.newMenu(stage);

		//this makes the info window start off visible with the player's own post-game stats
		syncInfoTable(HadalGame.usm.getConnID());

		//this draws the playstate snapshot over the results and makes it gradually dissolve after a delay
		stage.addActor(new Backdrop(AssetList.BLACK.toString()) {

			private boolean songPlaying;
			private float progress;
			private float timer;
			private static final float FADE_DELAY = 2.0f;
			private static final float FADE_DURATION = 2.5f;
			@Override
			public void act(float delta) {
				super.act(delta);
				timer += delta;
				if (timer >= FADE_DELAY) {
					progress = MathUtils.clamp((timer - FADE_DELAY) / FADE_DURATION, 0.0f, 1.0f);

					//after the delay, we bgein playing results music depending on the player's own victory status
					if (!songPlaying) {
						songPlaying = true;
						if (won) {
							MusicPlayer.playSong(MusicTrackType.VICTORY, 1.0f);
						} else {
							MusicPlayer.playSong(MusicTrackType.GAME_OVER, 1.0f);
						}
					}
				}
			}

			@Override
			public void draw(Batch batch, float alpha) {
				batch.setShader(shader.getShaderProgram());
				shader.shaderEntityUpdate(null, progress);
				batch.draw(snapshot, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
				batch.setShader(null);
			}
		});

		//this listener makes scrolling move through character scroll pane list
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(new InputAdapter() {

			@Override
			public boolean scrolled(float amountX, float amountY) {
				if (charactersScroll != null) {
					charactersScroll.setScrollX(charactersScroll.getScrollX() + amountY * SCROLL_ACCELERATION);
					return true;
				}
				return false;
			}
		});
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	/**
	 * This is called whenever we set the displayed table of scores.
	 * This is done once at the start and once again whenever a player readies themselves.
	 */
	public void syncScoreTable() {
		table.clear();

		final Table tableCharacters = new Table();
		charactersScroll = new ScrollPane(tableCharacters, SKIN);
		charactersScroll.setFadeScrollBars(false);

		charactersScroll.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				stage.setScrollFocus(null);
			}
		});

		Text title = new Text(text);
		title.setScale(RESULTS_SCALE);

		//for each player, get their field and create a results icon for them
		for (User user : users) {
			int connID = user.getConnID();
			ScoreManager score = user.getScoreManager();

			//winners should have party particles over their head
			PooledEffect effect = null;
			if (score.isWonLast()) {
				effect = Particle.PARTY.getParticle();
				effects.add(effect);
			}

			final PooledEffect finalEffect = effect;
			PlayerResultsIcon icon = new PlayerResultsIcon(batch, user) {

				@Override
				public void draw(Batch batch, float alpha) {
					super.draw(batch, alpha);
					if (finalEffect != null) {
						finalEffect.setPosition(getX() + PARTICLE_OFFSET_X, getY() + PARTICLE_OFFSET_Y);
						finalEffect.draw(batch, 0.0f);
						batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					}
				}
			};

			//clicking the player's icon displays their post-game stats and loadout
			icon.addListener(new ClickListener() {

				@Override
				public void clicked (InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					syncInfoTable(user.getConnID());
				}
			});

			tableCharacters.add(icon);
			icons.add(icon);

			//bots should automatically ready up
			if (connID < 0) {
				icon.setReady(true);
			}
		}

		table.add(title).height(TITLE_HEIGHT).row();
		table.add(charactersScroll).expandX().height(CHARACTER_SCROLL_HEIGHT).row();
	}

	/**
	 * This fills the window with stats for the designated player
	 */
	private int currentConnID = -1;
	public void syncInfoTable(int connID) {

		//only refresh table if we are pulling up stats for a different player than who is already being displayed
		if (currentConnID == connID) { return; }
		currentConnID = connID;

		//info table slides in and shows the new text
		tableInfoOuter.addAction(Actions.sequence(Actions.moveTo(INFO_X, INFO_Y, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(() -> {
			tableInfo.clear();
			tableArtifact.clear();

			User user = HadalGame.usm.getUsers().get(connID);

			if (user != null) {
				StatsManager statsManager = user.getStatsManager();
				LoadoutManager loadoutManager = user.getLoadoutManager();

				infoPlayerName.setText(user.getStringManager().getNameAbridged(MAX_NAME_LENGTH_LONG));

				Text damageDealtField = new Text(UIText.DAMAGE_DEALT.text());
				damageDealtField.setScale(INFO_TEXT_SCALE);

				Text damageAllyField = new Text(UIText.FRIENDLY_FIRE.text());
				damageAllyField.setScale(INFO_TEXT_SCALE);

				Text damageSelfField = new Text(UIText.SELF_DAMAGE.text());
				damageSelfField.setScale(INFO_TEXT_SCALE);

				Text damageReceivedField = new Text(UIText.DAMAGE_RECEIVED.text());
				damageReceivedField.setScale(INFO_TEXT_SCALE);

				Text damageDealt = new Text("" + (int) statsManager.getDamageDealt());
				damageDealt.setScale(INFO_TEXT_SCALE);

				Text damageAlly = new Text("" + (int) statsManager.getDamageDealtAllies());
				damageAlly.setScale(INFO_TEXT_SCALE);

				Text damageSelf = new Text("" + (int) statsManager.getDamageDealtSelf());
				damageSelf.setScale(INFO_TEXT_SCALE);

				Text damageReceived = new Text("" + (int) statsManager.getDamageReceived());
				damageReceived.setScale(INFO_TEXT_SCALE);

				tableInfo.add(damageDealtField).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y);
				tableInfo.add(damageDealt).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y).row();

				tableInfo.add(damageAllyField).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y);
				tableInfo.add(damageAlly).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y).row();

				tableInfo.add(damageSelfField).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y);
				tableInfo.add(damageSelf).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y).row();

				tableInfo.add(damageReceivedField).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y);
				tableInfo.add(damageReceived).height(INFO_ROW_HEIGHT).padBottom(INFO_PAD_Y).row();

				//display player's weapons, artifacts and active items (if synced properly)
				for (UnlockArtifact c : loadoutManager.getActiveLoadout().artifacts) {
					if (!UnlockArtifact.NOTHING.equals(c) && !c.isInvisible()) {
						ArtifactIcon newTag = new ArtifactIcon(c, c.getName() + "\n" + c.getDesc(),
								ARTIFACT_TAG_OFFSET_X, ARTIFACT_TAG_OFFSET_Y, ARTIFACT_TAG_TARGET_WIDTH);
						tableArtifact.add(newTag).width(ARTIFACT_TAG_SIZE).height(ARTIFACT_TAG_SIZE);
					}
				}

				for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
					if (!UnlockEquip.NOTHING.equals(loadoutManager.getActiveLoadout().multitools[i])) {
						Text weaponField = new Text(UIText.RESULT_WEAPON.text((i + 1) + ": "));
						weaponField.setScale(INFO_TEXT_SCALE);
						Text weapon = new Text(loadoutManager.getActiveLoadout().multitools[i].getName());
						weapon.setScale(INFO_TEXT_SCALE);
						tableInfo.add(weaponField).height(INFO_ROW_HEIGHT).left().padBottom(INFO_PAD_Y_SMALL);
						tableInfo.add(weapon).height(INFO_ROW_HEIGHT).left().padBottom(INFO_PAD_Y_SMALL).row();
					}
				}
				Text activeField = new Text(UIText.RESULT_ACTIVE.text());
				activeField.setScale(INFO_TEXT_SCALE);
				Text active = new Text(loadoutManager.getActiveLoadout().activeItem.getName());
				active.setScale(INFO_TEXT_SCALE);
				tableInfo.add(activeField).height(INFO_ROW_HEIGHT).left().padBottom(INFO_PAD_Y_SMALL);
				tableInfo.add(active).height(INFO_ROW_HEIGHT).left().padBottom(INFO_PAD_Y_SMALL).row();
			} else {
				infoPlayerName.setText("");
			}
		}), Actions.moveTo(INFO_X_ENABLED, INFO_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW)));
	}

	/**
	 * This is pressed whenever a player gets ready.
	 * @param playerID: If this is run by the server, this is the player's connID (or 0, if the host themselves).
	 * For the client, playerID is the index in scores of the player that readies.
	 */
	public void readyPlayer(int playerID) {
		if (ps.isServer()) {

			//The server finds the player that readies, sets their readiness and informs all clients by sending that player's index
			User user = HadalGame.usm.getUsers().get(playerID);
			if (user != null && !user.isSpectator()) {
				user.getScoreManager().setReady(true);

				int iconID = users.indexOf(user, false);
				icons.get(iconID).setReady(true);

				PacketManager.serverTCPAll(new Packets.ClientReady(iconID));
			}
		} else {

			//Clients just find the player based on that index and sets them as ready.
			users.get(playerID).getScoreManager().setReady(true);
			icons.get(playerID).setReady(true);
		}

		//When all players are ready, reddy will be true and we return to the hub
		boolean reddy = true;
		for (User user : HadalGame.usm.getUsers().values()) {
			if (!user.isSpectator() && !user.getScoreManager().isReady()) {
				reddy = false;
				break;
			}
		}

		//When the server is ready, we return to hub and tell all clients to do the same.
		if (reddy) {
			allReady();
		}
	}

	/**
	 * This is run when all players ready up.
	 * If "return to hub" is selected, return to hub. Otherwise go to selected map
	 */
	public void allReady() {
		if (ps.isServer()) {
			exitResultsState(isReturnToHub(), getNextMap());
		}
		FadeManager.fadeOut();
	}

	public void exitResultsState(boolean returnToHub, UnlockLevel nextLevel) {
		FadeManager.setRunAfterTransition(() -> {
			StateManager.removeState(ResultsState.class, false);
			if (returnToHub) {
				StateManager.gotoHubState(app, LobbyState.class);
				StateManager.gotoHubState(app, TitleState.class);
			} else {
				StateManager.addPlayState(app, nextLevel, ps.mode, LobbyState.class, true, "");
				StateManager.addPlayState(app, nextLevel, ps.mode, TitleState.class, true, "");
			}
		});
	}

	private static final float PARTICLE_COOLDOWN = 1.5f;
	private float particleCounter;
	@Override
	public void update(float delta) {

		//we update the message window to take input
		ps.getUIManager().getMessageWindow().table.act(delta);

		//this lets us continue to process packets. (mostly used for disconnects)
		ps.processCommonStateProperties(delta, true);

		particleCounter += delta;

		if (particleCounter >= PARTICLE_COOLDOWN) {
			particleCounter = 0.0f;
			for (PooledEffect effect : effects) {
				effect.start();
			}
		}
		for (PooledEffect effect : effects) {
			effect.update(delta);
		}
	}

	@Override
	public void render(float delta) {}

	@Override
	public void dispose() {
	    stage.dispose();
		fbo.dispose();

	    for (PlayerResultsIcon icon : icons) {
	        icon.dispose();
        }
		for (PooledEffect effect : effects) {
			Particle.PARTY.removeEffect(effect);
		}
	}

	public PlayState getPs() { return ps; }

	public boolean isReturnToHub() {
		if (returnToHub != null) {
			return returnToHub.isChecked();
		}
		return true;
	}

	public UnlockLevel getNextMap() {
		if (nextMaps != null && nextMapNames != null) {
			return nextMaps.get(nextMapNames.getSelectedIndex());
		}
		return UnlockLevel.HUB_MULTI;
	}
}
