package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
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
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.*;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.save.UnlockManager;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.text.UIText;

import static com.mygdx.hadal.utils.Constants.*;

/**
 * The Results screen appears at the end of levels and displays the player's results
 * In this screen, the player can return to the hub when all players are ready.
 * @author Juppstein Jodswallop
 */
public class ResultsState extends GameState {

	//This table contains the options for the title.
	private Table table, tableInfo, tableInfoOuter, tableArtifact, tableExtra;
	private ScrollPane infoScroll, charactersScroll;
	private CheckBox returnToHub;
	private SelectBox<String> nextMapNames;

	//this displays extra post-game stats about a selected player
	private Text infoPlayerName;

	//This is the playstate that the results state is placed on top of. Used to access the state's message window
	private final PlayState ps;

	//This is a list of all the saved player fields (scores) from the completed playstate
	private final Array<SavedPlayerFields> scores = new Array<>();
    private final Array<PlayerResultsIcon> icons = new Array<>();
	private final Array<PooledEffect> effects = new Array<>();

    //This is a mapping of players in the completed playstate mapped to whether they're ready to return to the hub.
	private final ObjectMap<SavedPlayerFields, Boolean> ready = new ObjectMap<>();

	//list of map options that the host can select as next map if not returning to hub
	private final Array<UnlockLevel> nextMaps = new Array<>();

	//this text is displayed at the top of the state and usually indicates victory or loss
	private final String text;

	//if the results text is equal to the magic word, calculate the results text based on score
	public static final String magicWord = "fug";

	//did this player win the last game?
	private boolean won;

	//Dimensions and position of the results menu
	private static final int tableX = 20;
	private static final int tableY = 240;
	private static final int tableWidth = 1240;
	private static final int tableHeight = 460;
	private static final int characterScrollHeight = 430;

	private static final int infoXEnabled = 880;
	private static final int infoYEnabled = 20;
	private static final int infoX = 1280;
	private static final int infoY = 20;
	private static final int infoWidth = 380;
	private static final int infoHeight = 200;

	private static final int infoRowHeight = 20;
	private static final float infoTextScale = 0.25f;
	private static final float infoPadY = 15.0f;
	private static final float infoPadYSmall = 5.0f;

	public static final int infoNameHeight = 30;
	public static final int infoScrollHeight = 100;
	public static final int infoNamePad = 15;

	private static final int tableExtraX = 420;
	private static final int tableExtraY = 20;
	private static final int tableExtraWidth = 440;
	private static final int tableExtraHeight = 200;
	private static final int mapOptionsWidth = 200;
	private static final int mapOptionsHeight = 30;
	private static final int optionsPad = 10;

	private static final int titleHeight = 40;
	private static final float resultsScale = 0.6f;
	private static final float scale = 0.4f;

	private static final int optionHeight = 50;

	public static final float artifactTagSize = 40.0f;
	private static final float artifactTagOffsetX = -100.0f;
	private static final float artifactTagOffsetY = 60.0f;
	private static final float artifactTagTargetWidth = 200.0f;

	private static final float particleOffsetX = 75.0f;
	private static final float particleOffsetY = 150.0f;

	private static final float scrollAcceleration = 8.0f;

	private static final int messageX = 20;
	private static final int messageY = 20;

	//these are used to process the fade transition from a playstate
	private final FrameBuffer fbo;
	private final TextureRegion snapshot;
	private final Shader shader;
	/**
	 * Constructor will be called whenever the game transitions into a results state
	 * @param text: this is the string that is displayed at the top of the result state
	 */
	public ResultsState(final GameStateManager gsm, String text, PlayState ps, FrameBuffer fbo) {
		super(gsm);
		this.text = text;
		this.ps = ps;
		this.fbo = fbo;
		this.shader = Shader.PERLIN_FADE;
		shader.loadShader();
		this.snapshot = new TextureRegion(fbo.getColorBufferTexture(), 0, fbo.getHeight(), fbo.getWidth(), -fbo.getHeight());

		//First, we obtain the list of scores, depending on whether we are the server or client.
		if (ps.isServer()) {
			for (User user : HadalGame.server.getUsers().values()) {
				if (!user.isSpectator()) {
					scores.add(user.getScores());
					if (user.getScores().getConnID() == 0) {
						won = user.getScores().isWonLast();
					}
				}
			}
			if (!scores.isEmpty()) {
				gsm.getRecord().updateScore(scores.get(0).getScore(), ps.level);
			}
		} else {
			for (User user : HadalGame.client.getUsers().values()) {
				if (!user.isSpectator()) {
					scores.add(user.getScores());
					if (user.getScores().getConnID() == HadalGame.client.connID) {
						won = user.getScores().isWonLast();
					}
				}
			}
		}

		//Then, we sort according to score and give the winner(s) a win. Being on the winning team overrides score
		scores.sort((a, b) -> {
			int cmp = (b.isWonLast() ? 1 : 0) - (a.isWonLast() ? 1 : 0);
			if (cmp == 0) { cmp = b.getTeamScore() - a.getTeamScore(); }
			if (cmp == 0) { cmp = b.getScore() - a.getScore(); }
			if (cmp == 0) { cmp = b.getKills() - a.getKills(); }
			if (cmp == 0) { cmp = a.getDeaths() - b.getDeaths(); }
			return cmp;
		});

		//Finally we initialize the ready map with everyone set to not ready. Bots don't need to ready up
		for (SavedPlayerFields score : scores) {
			if (score.getConnID() >= 0) {
				ready.put(score, false);
			}
		}
	}

	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new Backdrop(AssetList.RESULTS_CARD.toString()));

				table = new WindowTable();
				table.setPosition(tableX, tableY);
				table.setSize(tableWidth, tableHeight);
				addActor(table);
				syncScoreTable();

				tableInfoOuter = new WindowTable();

				infoPlayerName = new Text("");
				infoPlayerName.setScale(infoTextScale);

				tableInfo = new Table();
				tableArtifact = new Table();

				infoScroll = new ScrollPane(tableInfo, GameStateManager.getSkin());
				infoScroll.setFadeScrollBars(false);

				infoScroll.addListener(new InputListener() {

					@Override
					public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						stage.setScrollFocus(infoScroll);
					}
				});

				tableInfoOuter.add(infoPlayerName).pad(infoNamePad).height(infoNameHeight).row();
				tableInfoOuter.add(tableArtifact).height(infoNameHeight).row();
				tableInfoOuter.add(infoScroll).width(infoWidth).height(infoScrollHeight);
				tableInfoOuter.setPosition(infoX, infoY);
				tableInfoOuter.setSize(infoWidth, infoHeight);

				addActor(tableInfoOuter);

				tableExtra = new WindowTable();

				//These are all of the display and buttons visible to the player.
				final Text readyOption = new Text(UIText.READY.text()).setButton(true);

				readyOption.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {

						//When pressed, the ready option indicates to the server that that player is ready.
						if (ps.isServer()) {
							readyPlayer(0);
						} else {
							HadalGame.client.sendTCP(new Packets.ClientReady());
						}
					}
				});
				readyOption.setScale(scale);

				if (ps.isServer()) {
					final Text forceReadyOption = new Text(UIText.FORCE_READY.text()).setButton(true);

					forceReadyOption.addListener(new ClickListener() {

						@Override
						public void clicked(InputEvent e, float x, float y) {

							//When pressed, the force ready option forces a transition.
							allReady();
						}
					});
					forceReadyOption.setScale(scale);

					returnToHub = new CheckBox(UIText.RETURN_HUB.text(), GameStateManager.getSkin());
					returnToHub.setChecked(ps.getGsm().getSetting().isReturnToHubOnReady());

					Array<String> compliantMaps = new Array<>();
					Array<UnlockManager.UnlockTag> unlockTags = new Array<>();
					nextMaps.clear();
					for (UnlockLevel c : UnlockLevel.getUnlocks(ps, false, unlockTags)) {
						for (int i = 0; i < c.getModes().length; i++) {
							if (c.getModes()[i] == ps.mode.getCheckCompliance()) {
								compliantMaps.add(c.getName());
								nextMaps.add(c);
								break;
							}
						}
					}

					nextMapNames = new SelectBox<>(GameStateManager.getSkin());
					nextMapNames.setItems(compliantMaps);
					nextMapNames.setWidth(infoWidth);
					nextMapNames.setDisabled(returnToHub.isChecked());

					int currentMapIndex = nextMaps.indexOf(ps.level, false);
					if (currentMapIndex != -1) {
						nextMapNames.setSelectedIndex(currentMapIndex);
					}

					returnToHub.addListener(new ChangeListener() {

						@Override
						public void changed(ChangeEvent event, Actor actor) {
							nextMapNames.setDisabled(returnToHub.isChecked());
							ps.getGsm().getSetting().setReturnToHubOnReady(returnToHub.isChecked());
						}
					});

					tableExtra.add(readyOption).height(optionHeight).colspan(2).row();
					tableExtra.add(forceReadyOption).height(optionHeight).colspan(2).row();
					tableExtra.add(returnToHub).height(optionHeight);
					tableExtra.add(nextMapNames).width(mapOptionsWidth).height(mapOptionsHeight).pad(optionsPad);
				} else {
					tableExtra.add(readyOption).height(optionHeight);
				}

				tableExtra.setPosition(tableExtraX, tableExtraY);
				tableExtra.setSize(tableExtraWidth, tableExtraHeight);
				addActor(tableExtra);
			}
		};

		//we pull up and lock the playstate message window so players can chat in the aftergame.
		if (!ps.getMessageWindow().isActive()) {
			ps.getMessageWindow().toggleWindow();
		}

		//we start off playing no music. Results music only starts after playstate transition fade occurs
		HadalGame.musicPlayer.playSong(MusicTrackType.NOTHING, 1.0f);

		ps.getMessageWindow().setLocked(true);
		ps.getMessageWindow().table.setPosition(messageX, messageY);
		stage.addActor(ps.getMessageWindow().table);
		gsm.getApp().fadeIn();
		app.newMenu(stage);

		//this makes the info window start off visible with the player's own post-game stats
		if (ps.isServer()) {
			syncInfoTable(0);
		} else {
			syncInfoTable(HadalGame.client.connID);
		}

		//this draws the playstate snapshot over the results and makes it gradually dissolve after a delay
		stage.addActor(new Backdrop(AssetList.RESULTS_CARD.toString()) {

			private boolean songPlaying;
			private float progress;
			private float timer;
			private static final float fadeDelay = 2.0f;
			private static final float fadeDuration = 2.5f;
			@Override
			public void act(float delta) {
				super.act(delta);
				timer += delta;
				if (timer >= fadeDelay) {
					progress = Math.min(Math.max(0, (timer - fadeDelay) / fadeDuration), 1.0f);

					//after the delay, we bgein playing results music depending on the player's own victory status
					if (!songPlaying) {
						songPlaying = true;
						if (won) {
							HadalGame.musicPlayer.playSong(MusicTrackType.VICTORY, 1.0f);
						} else {
							HadalGame.musicPlayer.playSong(MusicTrackType.GAME_OVER, 1.0f);
						}
					}
				}
			}

			@Override
			public void draw(Batch batch, float alpha) {
				batch.setShader(shader.getShaderProgram());
				shader.shaderDefaultUpdate(progress);
				batch.draw(snapshot, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);
				batch.setShader(null);
			}
		});

		//this listener makes scrolling move through character scroll pane list
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) { return false;	}

			@Override
			public boolean keyUp(int keycode) { return false; }

			@Override
			public boolean keyTyped(char character) { return false; }

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

			@Override
			public boolean mouseMoved(int screenX, int screenY) { return false; }

			@Override
			public boolean scrolled(float amountX, float amountY) {
				if (charactersScroll != null) {
					charactersScroll.setScrollX(charactersScroll.getScrollX() + amountY * scrollAcceleration);
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
		charactersScroll = new ScrollPane(tableCharacters, GameStateManager.getSkin());
		charactersScroll.setFadeScrollBars(false);

		charactersScroll.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				stage.setScrollFocus(null);
			}
		});

		Text title = new Text(text);
		title.setScale(resultsScale);

		//for each player, get their field and create a results icon for them
		for (SavedPlayerFields score : scores) {
			int connId = score.getConnID();

			SavedPlayerFields field = null;
			SavedPlayerFieldsExtra fieldExtra = null;
			User user;
			if (ps.isServer()) {
				user = HadalGame.server.getUsers().get(connId);
			} else {
				user = HadalGame.client.getUsers().get(connId);
			}
			if (user != null) {
				field = user.getScores();
				fieldExtra = user.getScoresExtra();
			}

			if (field != null && fieldExtra != null) {

				//winners should have party particles over their head
				PooledEffect effect = null;
				if (field.isWonLast()) {
					effect = Particle.PARTY.getParticle();
					effects.add(effect);
				}

				final PooledEffect finalEffect = effect;
				PlayerResultsIcon icon = new PlayerResultsIcon(batch, field, fieldExtra) {

					@Override
					public void draw(Batch batch, float alpha) {
						super.draw(batch, alpha);
						if (finalEffect != null) {
							finalEffect.setPosition(getX() + particleOffsetX, getY() + particleOffsetY);
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
						syncInfoTable(score.getConnID());
					}
				});

				tableCharacters.add(icon);
				icons.add(icon);

				//bots should automatically ready up
				if (connId < 0) {
					icon.setReady(true);
				}
			}
		}

		table.add(title).height(titleHeight).row();
		table.add(charactersScroll).expandX().height(characterScrollHeight).row();
	}

	/**
	 * This fills the window with stats for the designated player
	 */
	private int currentConnId = -1;
	public void syncInfoTable(int connId) {

		//only refresh table if we are pulling up stats for a different player than who is already being displayed
		if (currentConnId == connId) { return; }
		currentConnId = connId;

		//info table slides in and shows the new text
		tableInfoOuter.addAction(Actions.sequence(Actions.moveTo(infoX, infoY, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(() -> {
			tableInfo.clear();
			tableArtifact.clear();

			SavedPlayerFields field = null;
			SavedPlayerFieldsExtra fieldExtra = null;
			User user;
			if (ps.isServer()) {
				user =  HadalGame.server.getUsers().get(connId);
			} else {
				user =  HadalGame.client.getUsers().get(connId);
			}
			if (user != null && !user.isSpectator()) {
				field = user.getScores();
				fieldExtra = user.getScoresExtra();
			}

			if (field != null && fieldExtra != null) {

				infoPlayerName.setText(field.getNameAbridged(MAX_NAME_LENGTH_LONG));

				Text damageDealtField = new Text(UIText.DAMAGE_DEALT.text());
				damageDealtField.setScale(infoTextScale);

				Text damageAllyField = new Text(UIText.FRIENDLY_FIRE.text());
				damageAllyField.setScale(infoTextScale);

				Text damageSelfField = new Text(UIText.SELF_DAMAGE.text());
				damageSelfField.setScale(infoTextScale);

				Text damageReceivedField = new Text(UIText.DAMAGE_RECEIVED.text());
				damageReceivedField.setScale(infoTextScale);

				Text damageDealt = new Text("" + (int) fieldExtra.getDamageDealt());
				damageDealt.setScale(infoTextScale);

				Text damageAlly = new Text("" + (int) fieldExtra.getDamageDealtAllies());
				damageAlly.setScale(infoTextScale);

				Text damageSelf = new Text("" + (int) fieldExtra.getDamageDealtSelf());
				damageSelf.setScale(infoTextScale);

				Text damageReceived = new Text("" + (int) fieldExtra.getDamageReceived());
				damageReceived.setScale(infoTextScale);

				tableInfo.add(damageDealtField).height(infoRowHeight).padBottom(infoPadY);
				tableInfo.add(damageDealt).height(infoRowHeight).padBottom(infoPadY).row();

				tableInfo.add(damageAllyField).height(infoRowHeight).padBottom(infoPadY);
				tableInfo.add(damageAlly).height(infoRowHeight).padBottom(infoPadY).row();

				tableInfo.add(damageSelfField).height(infoRowHeight).padBottom(infoPadY);
				tableInfo.add(damageSelf).height(infoRowHeight).padBottom(infoPadY).row();

				tableInfo.add(damageReceivedField).height(infoRowHeight).padBottom(infoPadY);
				tableInfo.add(damageReceived).height(infoRowHeight).padBottom(infoPadY).row();

				//display player's weapons, artifacts and active items (if synced properly)
				if (fieldExtra.getLoadout() != null) {

					for (UnlockArtifact c : fieldExtra.getLoadout().artifacts) {
						if (!c.equals(UnlockArtifact.NOTHING) && !c.isInvisible()) {
							ArtifactIcon newTag = new ArtifactIcon(c, c.getInfo().getName() + "\n" + c.getInfo().getDescription(),
									artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
							tableArtifact.add(newTag).width(artifactTagSize).height(artifactTagSize);
						}
					}

					for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
						if (!fieldExtra.getLoadout().multitools[i].equals(UnlockEquip.NOTHING)) {
							Text weaponField = new Text(UIText.RESULT_WEAPON.text((i + 1) + ": "));
							weaponField.setScale(infoTextScale);
							Text weapon = new Text(fieldExtra.getLoadout().multitools[i].getName());
							weapon.setScale(infoTextScale);
							tableInfo.add(weaponField).height(infoRowHeight).left().padBottom(infoPadYSmall);
							tableInfo.add(weapon).height(infoRowHeight).left().padBottom(infoPadYSmall).row();
						}
					}
					Text activeField = new Text(UIText.RESULT_ACTIVE.text());
					activeField.setScale(infoTextScale);
					Text active = new Text(fieldExtra.getLoadout().activeItem.getName());
					active.setScale(infoTextScale);
					tableInfo.add(activeField).height(infoRowHeight).left().padBottom(infoPadYSmall);
					tableInfo.add(active).height(infoRowHeight).left().padBottom(infoPadYSmall).row();
				}
			} else {
				infoPlayerName.setText("");
			}
		}), Actions.moveTo(infoXEnabled, infoYEnabled, TRANSITION_DURATION, INTP_FASTSLOW)));
	}

	/**
	 * This is pressed whenever a player gets ready.
	 * @param playerId: If this is run by the server, this is the player's connID (or 0, if the host themselves).
	 * For the client, playerId is the index in scores of the player that readies.
	 */
	public void readyPlayer(int playerId) {
		if (ps.isServer()) {

			//The server finds the player that readies, sets their readiness and informs all clients by sending that player's index
			User user = HadalGame.server.getUsers().get(playerId);
			if (user != null && !user.isSpectator()) {
				SavedPlayerFields field = user.getScores();
				ready.put(field, true);
				int iconId = scores.indexOf(field, false);
				icons.get(iconId).setReady(true);

				HadalGame.server.sendToAllTCP(new Packets.ClientReady(iconId));
			}
		} else {

			//Clients just find the player based on that index and sets them as ready.
			ready.put(scores.get(playerId), true);
			icons.get(playerId).setReady(true);
		}

		//When all players are ready, reddy will be true and we return to the hub
		boolean reddy = true;
		for (boolean b : ready.values()) {
			if (!b) {
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
			gsm.getApp().setRunAfterTransition(() -> {
				gsm.removeState(ResultsState.class, false);
				if (returnToHub.isChecked()) {
					gsm.gotoHubState(LobbyState.class);
					gsm.gotoHubState(TitleState.class);
				} else {
					UnlockLevel nextLevel = nextMaps.get(nextMapNames.getSelectedIndex());
					gsm.addPlayState(nextLevel, ps.mode, new Loadout(gsm.getLoadout()), null, LobbyState.class, true, "");
					gsm.addPlayState(nextLevel, ps.mode, new Loadout(gsm.getLoadout()), null, TitleState.class, true, "");
				}
			});
		}
		gsm.getApp().fadeOut();
	}

	private static final float particleCooldown = 1.5f;
	private float particleCounter;
	@Override
	public void update(float delta) {

		//we update the message window to take input
		ps.getMessageWindow().table.act(delta);

		//this lets us continue to process packets. (mostly used for disconnects)
		ps.processCommonStateProperties(delta, true);

		particleCounter += delta;

		if (particleCounter >= particleCooldown) {
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
}
