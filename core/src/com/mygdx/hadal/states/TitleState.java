package com.mygdx.hadal.states;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TitleBackdrop;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.managers.GameStateManager.State;
import com.mygdx.hadal.utils.NameGenerator;

/**
 * The TitleState is created upon initializing the game and will display an image.
 * This state also gives options to host, join a server as well as set player's name, change settngs and exit.
 * @author Zachary Tu
 *
 */
public class TitleState extends GameState {

	//This table contains the option windows for the title.
	private Table tableName, tableMain, tableIP;
	
	private TitleBackdrop backdrop;
	
	//These are all of the display and buttons visible to the player.
	private Text nameDisplay, nameRand, ipDisplay, hostOption, singleOption, joinOption, exitOption, settingsOption, aboutOption, searchOption, notifications;
	
	//Textfields for the player to enter an ip to connect to or change their name
	private TextField enterName, enterIP;
	
	//Dimentions and position of the title menu
	private final static int titleX = 140;
	private final static int titleY = 580;
	private final static int titleWidth = 1000;
	private final static int titleHeight = 128;
	
	private final static int menuX = 540;
	private final static int menuY = 40;
	private final static int width = 200;
	private final static int height = 240;
	
	private final static int nameX = 40;
	private final static int nameY = 180;
	private final static int nameWidth = 460;
	private final static int nameHeight = 100;
	
	private final static int ipX = 40;
	private final static int ipY = 40;
	private final static int ipWidth = 460;
	private final static int ipHeight = 100;
	
	private final static int notificationX = 40;
	private final static int notificationY = 300;
	
	private final static int textWidth = 260;

	private final static float scale = 0.4f;
	private final static float scaleSide = 0.25f;
	private final static float optionHeight = 35.0f;

	//This boolean determines if input is disabled. input is disabled if the player joins/hosts.
	private boolean inputDisabled = false;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 */
	public TitleState(final GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void show() {
		
		stage = new Stage() {
			{
				backdrop = new TitleBackdrop();
				backdrop.setX(titleX);
				backdrop.setY(titleY);
				backdrop.setWidth(titleWidth);
				backdrop.setHeight(titleHeight);
				
				addActor(backdrop);
				addActor(new MenuWindow(menuX, menuY, width, height));
				addActor(new MenuWindow(nameX, nameY, nameWidth, nameHeight));
				addActor(new MenuWindow(ipX, ipY, ipWidth, ipHeight));
				
				tableMain = new Table();
				tableMain.setPosition(menuX, menuY);
				tableMain.setSize(width, height);
				addActor(tableMain);
				
				tableName = new Table();
				tableName.setPosition(nameX, nameY);
				tableName.setSize(nameWidth, nameHeight);
				addActor(tableName);
				
				tableIP = new Table();
				tableIP.setPosition(ipX, ipY);
				tableIP.setSize(ipWidth, ipHeight);
				addActor(tableIP);
				
				nameDisplay = new Text("YOUR NAME: ", 0, 0, false);
				nameDisplay.setScale(scaleSide);
				nameDisplay.setHeight(optionHeight);
				
				nameRand = new Text("GENERATE RANDOM NAME", 0, 0, true);
				nameRand.setScale(scaleSide);
				nameRand.setHeight(optionHeight);
				
				ipDisplay = new Text("ENTER IP: ", 0, 0, true);
				ipDisplay.setScale(scaleSide);
				ipDisplay.setHeight(optionHeight);
				
				joinOption = new Text("JOIN SERVER", 0, 0, true);
				joinOption.setScale(scaleSide);
				joinOption.setHeight(optionHeight);
				
				searchOption = new Text("SEARCH FOR NEARBY SERVERS", 0, 0, true);
				searchOption.setScale(scaleSide);
				searchOption.setHeight(optionHeight);
				
				hostOption = new Text("HOST SERVER", 0, 0, true);
				hostOption.setScale(scale);
				hostOption.setHeight(optionHeight);
				
				singleOption = new Text("SINGLE PLAYER", 0, 0, true);
				singleOption.setScale(scale);
				singleOption.setHeight(optionHeight);
				
				settingsOption = new Text("OPTIONS", 0, 0, true);
				settingsOption.setScale(scale);
				settingsOption.setHeight(optionHeight);
				
				aboutOption = new Text("ABOUT", 0, 0, true);
				aboutOption.setScale(scale);
				aboutOption.setHeight(optionHeight);
				
				exitOption = new Text("EXIT", 0, 0, true);
				exitOption.setScale(scale);
				exitOption.setHeight(optionHeight);
				
				notifications = new Text("", notificationX, notificationY, false);
				notifications.setScale(scale);
				notifications.setHeight(optionHeight);
				
				hostOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						if (inputDisabled) {
							return;
						}
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, false);
						
						//Save current name into records.
						gsm.getLoadout().setName(enterName.getText());
						
						//Start up the server in multiplayer mode
						HadalGame.server.init(true);
						GameStateManager.currentMode = Mode.MULTI;
						
						//Enter the Hub State.
						gsm.getApp().setRunAfterTransition(new Runnable() {

							@Override
							public void run() {
								gsm.gotoHubState();
							}
							
						});
						gsm.getApp().fadeOut();
			        }
			    });
				
				singleOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						if (inputDisabled) {
							return;
						}
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, false);
						
						//Save current name into records.
						gsm.getLoadout().setName(enterName.getText());
						
						//Start up the server in singleplayer mode
						HadalGame.server.init(false);
						GameStateManager.currentMode = Mode.SINGLE;
						
						//Enter the Hub State.
						gsm.getApp().setRunAfterTransition(new Runnable() {

							@Override
							public void run() {
								gsm.gotoHubState();
							}
							
						});
						gsm.getApp().fadeOut();
			        }
			    });
				
				joinOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						if (inputDisabled) {
							return;
						}
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, false);
						
						//Save current name into records.
						gsm.getLoadout().setName(enterName.getText());
						
						//Start up the Client
						HadalGame.client.init();
						GameStateManager.currentMode = Mode.MULTI;
						
						setNotification("SEARCHING FOR SERVER!");
						//Attempt to connect to the chosen ip
						Gdx.app.postRunnable(new Runnable() {
					        
							@Override
					         public void run() {
				        		//Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
				            	try {
				                	HadalGame.client.getClient().connect(5000, enterIP.getText(), gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());
				                	
				    				//save last joined ip if successful
				                	gsm.getRecord().setlastIp(enterIP.getText());
				                	
				                	setNotification("CONNECTED TO SERVER: " + enterIP.getText());
				                } catch (IOException ex) {
				                    setNotification("FAILED TO CONNECT TO SERVER!");
				                    
				                    //Let the player attempt to connect again after finishing
					            	inputDisabled = false;
				                }
					         }
						});
			        }
			    });
				
				searchOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						setNotification("SEARCHING FOR SERVER...");
						
						if (inputDisabled) {
							return;
						}
						inputDisabled = true;
						
						SoundEffect.UISWITCH2.play(gsm, false);
						
						Gdx.app.postRunnable(new Runnable() {
					        
							@Override
					         public void run() {
								//Search network for nearby hosts
								enterIP.setText(HadalGame.client.searchServer());
								
								//Set notification according to result
								if (enterIP.getText().equals("NO IP FOUND")) {
									setNotification("SERVER NOT FOUND!");
								} else {
									setNotification("FOUND SERVER: " + enterIP.getText());
								}
								
								//Let the player attempt to connect again after finishing
								inputDisabled = false;
							}
						});
			        }
			    });

				//Control Option leads player to control state to change settings
				settingsOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {

						if (inputDisabled) {
							return;
						}
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, false);
						
						//Enter the Setting State.
						gsm.getApp().setRunAfterTransition(new Runnable() {

							@Override
							public void run() {
								getGsm().addSettingState(null, TitleState.class);
							}
							
						});
						gsm.getApp().fadeOut();
			        }
			    });
				
				//About Option leads player to about state to view info
				aboutOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {

						if (inputDisabled) {
							return;
						}
						inputDisabled = true;
						
						SoundEffect.UISWITCH1.play(gsm, false);
						
						//Enter the About State.
						gsm.getApp().setRunAfterTransition(new Runnable() {

							@Override
							public void run() {
								getGsm().addState(State.ABOUT, TitleState.class);
							}
							
						});
						gsm.getApp().fadeOut();
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
						
						SoundEffect.UISWITCH3.play(gsm, false);
						
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
				
				enterIP = new TextField("", GameStateManager.getSkin());
				
				//retrieve last joined ip if existant
				if (gsm.getRecord().getLastIp() == "") {
					enterIP.setMessageText("ENTER IP");
				} else {
					enterIP.setText(gsm.getRecord().getLastIp());
				}
				
				
				enterName = new TextField(gsm.getLoadout().getName(), GameStateManager.getSkin());
				enterName.setMessageText("ENTER NAME");
				
				tableName.add(nameDisplay).pad(5);
				tableName.add(enterName).width(textWidth).height(optionHeight).row();
				tableName.add(nameRand).colspan(2);
				
				tableMain.add(singleOption).row();
				tableMain.add(hostOption).row();
				tableMain.add(settingsOption).row();
				tableMain.add(aboutOption).row();
				tableMain.add(exitOption).row();
				
				tableIP.add(ipDisplay).pad(5);
				tableIP.add(enterIP).width(textWidth).height(optionHeight).row();
				tableIP.add(joinOption);
				tableIP.add(searchOption);
				
				addActor(notifications);
			}
		};
		app.newMenu(stage);
		gsm.getApp().fadeIn();
		
		inputDisabled = false;
	}

	@Override
	public void update(float delta) {}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(40/255f, 51/255f, 77/255f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	@Override
	public void dispose() {	stage.dispose(); }
	
	/**
	 * This method changes the text notification displayed in the title state
	 * @param notification: new text
	 */
	public void setNotification(String notification) {
		notifications.setText(notification);
	}

	public void setInputDisabled(boolean inputDisabled) { this.inputDisabled = inputDisabled; }
}
