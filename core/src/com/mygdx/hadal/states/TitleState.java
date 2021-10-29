package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
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
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.utils.NameGenerator;

import static com.mygdx.hadal.utils.Constants.*;

/**
 * The TitleState is created upon initializing the game and will display an image.
 * This state also gives options to host, join a server as well as set player's name, change settings and exit.
 * @author Clottercups Clungfisher
 */
public class TitleState extends GameState {

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
	
	//Dimensions and position of the title menu
	private static final int titleX = 240;
	private static final int titleY = 720;
	private static final int titleXEnabled = 240;
	private static final int titleYEnabled = 360;
	private static final int titleWidth = 800;
	private static final int titleHeight = 370;
	
	private static final int menuX = 540;
	private static final int menuY = -240;
	private static final int menuXEnabled = 540;
	private static final int menuYEnabled = 40;
	private static final int width = 200;
	private static final int height = 240;

	private static final int nameX = 40;
	private static final int nameY = -240;
	private static final int nameXEnabled = 40;
	private static final int nameYEnabled = 40;
	private static final int nameWidth = 460;
	private static final int nameHeight = 120;

	private static final int notificationX = -460;
	private static final int notificationY = 180;
	private static final int notificationXEnabled = 40;
	private static final int notificationYEnabled = 180;
	private static final int notificationWidth = 460;
	private static final int notificationHeight = 60;
	
	private static final int versionNumX = 1060;
	private static final int versionNumY = 40;
	
	private static final int textWidth = 260;

	private static final float scale = 0.4f;
	private static final float scaleSide = 0.25f;
	private static final float optionHeight = 35.0f;
	private static final float mainOptionHeight = 40.0f;

	private static final int jelly1X = 640;
	private static final int jelly1Y = 300;
	
	private static final int diatom1X = 1080;
	private static final int diatom1Y = 80;
	
	private static final int diatom2X = 800;
	private static final int diatom2Y = 50;
	
	private static final int diatom3X = 200;
	private static final int diatom3Y = 30;
	
	//This boolean determines if input is disabled. input is disabled if the player joins/hosts.
	private boolean inputDisabled;

	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 */
	public TitleState(final GameStateManager gsm) {
		super(gsm);
		this.diatom1 = Particle.DIATOM.getParticle();
		this.diatom1.setPosition(diatom1X, diatom1Y);
		this.diatom2 = Particle.DIATOM.getParticle();
		this.diatom2.setPosition(diatom2X, diatom2Y);
		this.diatom3 = Particle.DIATOM.getParticle();
		this.diatom3.setPosition(diatom3X, diatom3Y);
		this.jelly = Particle.JELLY.getParticle();
		this.jelly.setPosition(jelly1X, jelly1Y);
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
					}
				});
				backdrop = new Backdrop(AssetList.TITLE_CARD.toString(), titleWidth, titleHeight);
				backdrop.setX(titleX);
				backdrop.setY(titleY);
				
				addActor(backdrop);

				tableMain = new WindowTable();
				tableMain.setPosition(menuX, menuY);
				tableMain.setSize(width, height);
				addActor(tableMain);

				tableName = new WindowTable();
				tableName.setPosition(nameX, nameY);
				tableName.setSize(nameWidth, nameHeight);
				addActor(tableName);

				notificationTable = new WindowTable();
				notificationTable.setPosition(notificationX, notificationY);
				notificationTable.setSize(notificationWidth, notificationHeight);
				addActor(notificationTable);

				Text nameDisplay = new Text("YOUR NAME: ", 0, 0, false);
				nameDisplay.setScale(scaleSide);

				Text nameRand = new Text("GENERATE RANDOM NAME", 0, 0, true);
				nameRand.setScale(scaleSide);

				Text multiOption = new Text("MULTIPLAYER", 0, 0, true);
				multiOption.setScale(scale);

				Text singleOption = new Text("SINGLEPLAYER", 0, 0, true);
				singleOption.setScale(scale);

				Text settingsOption = new Text("OPTIONS", 0, 0, true);
				settingsOption.setScale(scale);

				Text aboutOption = new Text("EXTRA", 0, 0, true);
				aboutOption.setScale(scale);

				Text exitOption = new Text("EXIT", 0, 0, true);
				exitOption.setScale(scale);

				notifications = new Text("", 0, 0, false);
				notifications.setScale(scale);

				Text versionNum = new Text("VERSION: " + HadalGame.Version, versionNumX, versionNumY, true);
				versionNum.setScale(scale);
				versionNum.setHeight(optionHeight);

				versionNum.addListener(new ClickListener() {

					@Override
					public void clicked(InputEvent e, float x, float y) {
						SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
						Gdx.net.openURI(HadalGame.VersionURL);
					}
				});

				multiOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {

						if (inputDisabled) { return; }

						if (enterName.getText().isEmpty()) {
							setNotification("PLEASE ENTER NAME");
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
							setNotification("PLEASE ENTER NAME");
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
						gsm.getApp().setRunAfterTransition(() -> gsm.gotoHubState(TitleState.class));
						gsm.getApp().fadeOut();
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
			        	setNotification("RANDOM NAME GENERATED!");
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
				enterName.setMessageText("ENTER NAME");
				
				tableName.add(nameDisplay).height(optionHeight).pad(5);
				tableName.add(enterName).width(textWidth).height(optionHeight).row();
				tableName.add(nameRand).height(optionHeight).colspan(2);
				
				tableMain.add(singleOption).height(mainOptionHeight).row();
				tableMain.add(multiOption).height(mainOptionHeight).row();
				tableMain.add(settingsOption).height(mainOptionHeight).row();
				tableMain.add(aboutOption).height(mainOptionHeight).row();
				tableMain.add(exitOption).height(mainOptionHeight).row();

				notificationTable.add(notifications).pad(5).expandX().left();
				addActor(versionNum);
			}
		};
		app.newMenu(stage);

		if (gsm.getApp().getFadeLevel() >= 1.0f) {
			gsm.getApp().fadeIn();
		}

		HadalGame.musicPlayer.playSong(MusicPlayer.MusicState.MENU, 1.0f);

		inputDisabled = true;
		transitionIn(() -> inputDisabled = false);
	}

	private void transitionOut(Runnable runnable) {
		backdrop.addAction(Actions.moveTo(titleX, titleY, TRANSITION_DURATION, INTP_FASTSLOW));

		tableMain.addAction(Actions.moveTo(menuX, menuY, TRANSITION_DURATION, INTP_FASTSLOW));
		tableName.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(nameX, nameY, TRANSITION_DURATION, INTP_FASTSLOW)));
		notificationTable.addAction(Actions.moveTo(notificationX, notificationY, TRANSITION_DURATION, INTP_FASTSLOW));
		notifications.setText("");
	}

	private void transitionIn(Runnable runnable) {
		backdrop.addAction(Actions.moveTo(titleXEnabled, titleYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));

		tableMain.addAction(Actions.moveTo(menuXEnabled, menuYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
		tableName.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(nameXEnabled, nameYEnabled, TRANSITION_DURATION, INTP_FASTSLOW)));
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
		jelly.free();
		diatom1.free();
		diatom2.free();
		diatom3.free();
	}
	
	/**
	 * This method changes the text notification displayed in the title state
	 * @param notification: new text
	 */
	public void setNotification(String notification) {
		if (!notification.equals("")) {
			notificationTable.addAction(Actions.sequence(
					Actions.moveTo(notificationX, notificationY, TRANSITION_DURATION, INTP_FASTSLOW),
					Actions.run(() -> notifications.setText(notification)),
					Actions.moveTo(notificationXEnabled, notificationYEnabled, TRANSITION_DURATION, INTP_FASTSLOW)));
		}
	}
}
