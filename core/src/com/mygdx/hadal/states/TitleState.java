package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Backdrop;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TableWindow;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.text.NameGenerator;

import static com.mygdx.hadal.constants.Constants.*;

/**
 * The TitleState is created upon initializing the game and will display an image.
 * This state also gives options to host, join a server as well as set player's name, change settings and exit.
 * @author Clottercups Clungfisher
 */
public class TitleState extends GameState {

	//Dimensions and position of the title menu
	private static final int TITLE_X = 240;
	private static final int TITLE_Y = 720;
	private static final int TITLE_X_ENABLED = 240;
	private static final int TITLE_Y_ENABLED = 360;
	private static final int TITLE_WIDTH = 800;
	private static final int TITLE_HEIGHT = 370;

	private static final int MENU_X = 540;
	private static final int MENU_Y = -240;
	private static final int MENU_X_ENABLED = 540;
	private static final int MENU_Y_ENABLED = 40;
	private static final int MENU_WIDTH = 200;
	private static final int MENU_HEIGHT = 240;

	private static final int NAME_X = 40;
	private static final int NAME_Y = -240;
	private static final int NAME_X_ENABLED = 40;
	private static final int NAME_Y_ENABLED = 40;
	private static final int NAME_WIDTH = 460;
	private static final int NAME_HEIGHT = 120;

	private static final int NOTIFICATION_X = -460;
	private static final int NOTIFICATION_Y = 180;
	private static final int NOTIFICATION_X_ENABLED = 40;
	private static final int NOTIFICATION_Y_ENABLED = 180;
	private static final int NOTIFICATION_WIDTH = 460;
	private static final int NOTIFICATION_HEIGHT = 60;

	private static final int VERSION_NUM_X = 1060;
	private static final int VERSION_NUM_Y = 40;

	private static final int TEXT_WIDTH = 260;

	private static final float SCALE = 0.4f;
	private static final float SCALE_SIDE = 0.25f;
	private static final float OPTION_HEIGHT = 35.0f;
	private static final float MAIN_OPTION_HEIGHT = 40.0f;

	private static final int JELLY_1_X = 640;
	private static final int JELLY_1_Y = 300;

	private static final int DIATOM_1_X = 1080;
	private static final int DIATOM_1_Y = 80;

	private static final int DIATOM_2_X = 800;
	private static final int DIATOM_2_Y = 50;

	private static final int DIATOM_3_X = 200;
	private static final int DIATOM_3_Y = 30;

	//This table contains the option windows for the title.
	private Table tableName, tableMain, notificationTable;

	//this is the image backdrop of the title state
	private Backdrop backdrop;

	//These are all of the display and buttons visible to the player.
	private Text notifications;

	//Textfields for the player to change their name
	private TextField enterName;

	//ambient particle effects
	private final PooledEffect jelly, diatom1, diatom2, diatom3;
	
	//This boolean determines if input is disabled. input is disabled if the player joins/hosts.
	private boolean inputDisabled;

	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 */
	public TitleState(final GameStateManager gsm) {
		super(gsm);
		this.diatom1 = Particle.DIATOM.getParticle();
		this.diatom1.setPosition(DIATOM_1_X, DIATOM_1_Y);
		this.diatom2 = Particle.DIATOM.getParticle();
		this.diatom2.setPosition(DIATOM_2_X, DIATOM_2_Y);
		this.diatom3 = Particle.DIATOM.getParticle();
		this.diatom3.setPosition(DIATOM_3_X, DIATOM_3_Y);
		this.jelly = Particle.JELLY.getParticle();
		this.jelly.setPosition(JELLY_1_X, JELLY_1_Y);
	}
	
	@Override
	public void show() {
		final TitleState me = this;

		//b/c the title state can get shown multiple times without getting removed, we must get rid of stage if already created
		if (stage != null) {
			stage.dispose();
		}

		stage = new Stage() {
			{
				addActor(new Backdrop(AssetList.TITLE_BACKGROUND.toString()) {
					
					@Override
				    public void draw(Batch batch, float alpha) {
						super.draw(batch, alpha);
						//draw particles here to avoid drawing them underneath the background. (b/c stage renders above state.render())
						diatom1.draw(batch, 0);
						diatom2.draw(batch, 0);
						diatom3.draw(batch, 0);
						jelly.draw(batch, 0);
						batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					}
				});
				backdrop = new Backdrop(AssetList.TITLE_CARD.toString(), TITLE_WIDTH, TITLE_HEIGHT);
				backdrop.setX(TITLE_X);
				backdrop.setY(TITLE_Y);
				
				addActor(backdrop);

				tableMain = new TableWindow();
				tableMain.setPosition(MENU_X, MENU_Y);
				tableMain.setSize(MENU_WIDTH, MENU_HEIGHT);
				addActor(tableMain);

				tableName = new TableWindow();
				tableName.setPosition(NAME_X, NAME_Y);
				tableName.setSize(NAME_WIDTH, NAME_HEIGHT);
				addActor(tableName);

				notificationTable = new TableWindow();
				notificationTable.setPosition(NOTIFICATION_X, NOTIFICATION_Y);
				notificationTable.setSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
				addActor(notificationTable);

				Text nameDisplay = new Text(UIText.YOUR_NAME.text());
				nameDisplay.setScale(SCALE_SIDE);

				Text nameRand = new Text(UIText.GEN_NAME.text()).setButton(true);
				nameRand.setScale(SCALE_SIDE);

				Text multiOption = new Text(UIText.MULTIPLAYER.text()).setButton(true);
				multiOption.setScale(SCALE);

				Text singleOption = new Text(UIText.SINGLEPLAYER.text()).setButton(true);
				singleOption.setScale(SCALE);

				Text settingsOption = new Text(UIText.SETTINGS.text()).setButton(true);
				settingsOption.setScale(SCALE);

				Text aboutOption = new Text(UIText.EXTRA.text()).setButton(true);
				aboutOption.setScale(SCALE);

				Text exitOption = new Text(UIText.EXIT.text()).setButton(true);
				exitOption.setScale(SCALE);

				notifications = new Text("");
				notifications.setScale(SCALE);

				Text versionNum = new Text(UIText.VERSION.text(HadalGame.VERSION), VERSION_NUM_X, VERSION_NUM_Y).setButton(true);
				versionNum.setScale(SCALE);
				versionNum.setHeight(OPTION_HEIGHT);

				versionNum.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						Gdx.net.openURI(HadalGame.VERSION_URL);
					}
				});

				multiOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {

						if (inputDisabled) { return; }

						if (enterName.getText().isEmpty()) {
							setNotification(UIText.PLEASE_ENTER_NAME.text());
							return;
						}

						inputDisabled = true;

						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);

						//Save current name into records.
						gsm.getLoadout().setName(enterName.getText());

						//Enter the Lobby State.
						transitionOut(() -> getGsm().addState(State.LOBBY, me));
					}
				});
				
				singleOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						if (inputDisabled) { return; }

						if (enterName.getText().isEmpty()) {
							setNotification(UIText.PLEASE_ENTER_NAME.text());
							return;
						}

						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						
						//Save current name into records.
						gsm.getLoadout().setName(enterName.getText());
						
						//Start up the server in singleplayer mode
						HadalGame.server.init(false);
						GameStateManager.currentMode = Mode.SINGLE;
						
						//Enter the Hub State.
						HadalGame.fadeManager.setRunAfterTransition(() -> gsm.gotoHubState(TitleState.class));
						HadalGame.fadeManager.fadeOut();
			        }
			    });
				

				//Control Option leads player to control state to change settings
				settingsOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {

						if (inputDisabled) { return; }
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						
						//Enter the Setting State.
						transitionOut(() -> getGsm().addState(State.SETTING, me));
			        }
			    });
				
				//About Option leads player to about state to view info
				aboutOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {

						if (inputDisabled) { return; }
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						
						//Enter the About State.
						transitionOut(() -> getGsm().addState(State.ABOUT, me));
			        }
			    });
				
				//Exit Option closes the game
				exitOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						if (gsm.getSetting().isExportChatLog()) {
							gsm.exportChatLogs();
						}
						Gdx.app.exit();
			        }
			    });
				
				//Name Rand button generates a random name for the player
				nameRand.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						SoundEffect.UISWITCH3.play(gsm, 1.0f, false);
						enterName.setText(NameGenerator.generateFirstLast(gsm.getSetting().isRandomNameAlliteration()));
			        	setNotification(UIText.RAND_NAME_GEN.text());
				        }
				});
				
				//If the player clicks outside of a text field, the field should be deselected
				addCaptureListener(new InputListener() {
					
					@Override
			         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			            if (!(event.getTarget() instanceof TextField)) {
			            	setKeyboardFocus(null);
			            }
			            return false;
			         }
		         });

				enterName = new TextField(gsm.getLoadout().getName(), GameStateManager.getSkin());
				enterName.setMaxLength(MAX_NAME_LENGTH_TOTAL);
				enterName.setMessageText(UIText.ENTER_NAME.text());

				tableName.add(nameDisplay).height(OPTION_HEIGHT).pad(5);
				tableName.add(enterName).width(TEXT_WIDTH).height(OPTION_HEIGHT).row();
				tableName.add(nameRand).height(OPTION_HEIGHT).colspan(2);
				
				tableMain.add(singleOption).height(MAIN_OPTION_HEIGHT).row();
				tableMain.add(multiOption).height(MAIN_OPTION_HEIGHT).row();
				tableMain.add(settingsOption).height(MAIN_OPTION_HEIGHT).row();
				tableMain.add(aboutOption).height(MAIN_OPTION_HEIGHT).row();
				tableMain.add(exitOption).height(MAIN_OPTION_HEIGHT).row();

				notificationTable.add(notifications).pad(5).expandX().left();
				addActor(versionNum);
			}
		};
		app.newMenu(stage);

		if (HadalGame.fadeManager.getFadeLevel() >= 1.0f) {
			HadalGame.fadeManager.fadeIn();
		}

		HadalGame.musicPlayer.playSong(MusicTrackType.TITLE, 1.0f);

		inputDisabled = true;
		transitionIn(() -> inputDisabled = false);
	}

	private void transitionOut(Runnable runnable) {
		backdrop.addAction(Actions.moveTo(TITLE_X, TITLE_Y, TRANSITION_DURATION, INTP_FASTSLOW));

		tableMain.addAction(Actions.moveTo(MENU_X, MENU_Y, TRANSITION_DURATION, INTP_FASTSLOW));
		tableName.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(NAME_X, NAME_Y, TRANSITION_DURATION, INTP_FASTSLOW)));
		notificationTable.addAction(Actions.moveTo(NOTIFICATION_X, NOTIFICATION_Y, TRANSITION_DURATION, INTP_FASTSLOW));
		notifications.setText("");
	}

	private void transitionIn(Runnable runnable) {
		backdrop.addAction(Actions.moveTo(TITLE_X_ENABLED, TITLE_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));

		tableMain.addAction(Actions.moveTo(MENU_X_ENABLED, MENU_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
		tableName.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(NAME_X_ENABLED, NAME_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW)));
	}

	@Override
	public void update(float delta) {
		diatom1.update(delta);
		diatom2.update(delta);
		diatom3.update(delta);
		jelly.update(delta);
	}

	@Override
	public void render(float delta) {}
	
	@Override
	public void dispose() {	
		stage.dispose();
		Particle.JELLY.removeEffect(jelly);
		Particle.DIATOM.removeEffect(diatom1);
		Particle.DIATOM.removeEffect(diatom2);
		Particle.DIATOM.removeEffect(diatom3);
	}
	
	/**
	 * This method changes the text notification displayed in the title state
	 * @param notification: new text
	 */
	public void setNotification(String notification) {
		if (!"".equals(notification)) {
			notificationTable.addAction(Actions.sequence(
					Actions.moveTo(NOTIFICATION_X, NOTIFICATION_Y, TRANSITION_DURATION, INTP_FASTSLOW),
					Actions.run(() -> notifications.setText(notification)),
					Actions.moveTo(NOTIFICATION_X_ENABLED, NOTIFICATION_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW)));
		}
	}
}
