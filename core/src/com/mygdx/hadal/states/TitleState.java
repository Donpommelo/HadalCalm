package com.mygdx.hadal.states;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.TitleBackdrop;
import com.mygdx.hadal.client.KryoClient;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.utils.NameGenerator;

/**
 * The TitleState is created upon initializing the game and will display an image.
 * This state also gives options to host, join a server as well as set player's name, change settngs and exit.
 * @author Zachary Tu
 *
 */
public class TitleState extends GameState {

	//This table contains the options for the title.
	private Table table;
	
	//These are all of the display and buttons visible to the player.
	private Text nameDisplay, nameRand, hostOption, singleOption, joinOption, exitOption, settingsOption, searchOption, notifications;
	
	//Textfields for the player to enter an ip to connect to or change their name
	private TextField enterName, enterIP;
	
	//Dimentions and position of the title menu
	private final static int width = 700;
	private final static int height = 240;
	private final static int xOffset = 100;

	//This boolean determines if connection was attempted. Used to avoid multiple connections.
	private boolean connectAttempted = false;
	
	/**
	 * Constructor will be called once upon initialization of the StateManager.
	 * @param gsm
	 */
	public TitleState(final GameStateManager gsm) {
		super(gsm);
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				addActor(new TitleBackdrop());
				addActor(new MenuWindow(gsm, HadalGame.CONFIG_WIDTH - width - xOffset, 0, width, height));
				
				table = new Table();
				table.setPosition(HadalGame.CONFIG_WIDTH - width - xOffset, 0);
				table.setSize(width, height);
				addActor(table);
				
				nameDisplay = new Text("YOUR NAME: ", 0, 0, Color.BLACK);
				nameDisplay.setScale(0.5f);
				nameRand = new Text("RANDOM?", 0, 0, Color.BLACK);
				nameRand.setScale(0.5f);
				hostOption = new Text("HOST SERVER", 0, 0, Color.BLACK);
				hostOption.setScale(0.5f);
				singleOption = new Text("SINGLE PLAYER", 0, 0, Color.BLACK);
				singleOption.setScale(0.5f);
				joinOption = new Text("JOIN", 0, 0, Color.BLACK);
				joinOption.setScale(0.5f);
				searchOption = new Text("SEARCH?", 0, 0, Color.BLACK);
				searchOption.setScale(0.5f);
				settingsOption = new Text("SETTINGS", 0, 0, Color.BLACK);
				settingsOption.setScale(0.5f);
				exitOption = new Text("EXIT?", 0, 0, Color.BLACK);
				exitOption.setScale(0.5f);
				notifications = new Text("", 0, 0, Color.BLACK);
				notifications.setScale(0.5f);

				hostOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						//Save current name into records.
						gsm.getRecord().setName(enterName.getText());
						
						//Start up the server
						HadalGame.server.init(true);
						
						GameStateManager.currentMode = Mode.MULTI;
						
						//Enter the Hub State.
			        	getGsm().gotoHubState();
			        }
			    });
				
				singleOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						//Save current name into records.
						gsm.getRecord().setName(enterName.getText());
						
						//Start up the server
						HadalGame.server.init(false);
						GameStateManager.currentMode = Mode.SINGLE;
						
						//Enter the Hub State.
						getGsm().gotoHubState();
			        }
			    });
				
				joinOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						
						//If the player is already trying to connect, don't do anything
						if (connectAttempted) {
							return;
						}
						connectAttempted = true;
						
						//Save current name into records.
						gsm.getRecord().setName(enterName.getText());
						
						//Start up the Client
						HadalGame.client.init();
						
						//Attempt to connect to the chosen ip
						Gdx.app.postRunnable(new Runnable() {
					        
							@Override
					         public void run() {
					        	//Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
					            	try {
					                	HadalGame.client.client.connect(5000, enterIP.getText(),
					                			KryoClient.tcpPortSocket, KryoClient.udpPortSocket);
					                	setNotification("CONNECTED TO SERVER: " + enterIP.getText());
					                } catch (IOException ex) {
					                    ex.printStackTrace();
					                    setNotification("FAILED TO CONNECT TO SERVER!");
					                }
					            	
					            	//Let the player attempt to connect again after finishing
					            	connectAttempted = false;
					         }
						});
			        }
			    });
				
				searchOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						setNotification("SEARCHING FOR SERVER...");
						
						//If the player is already trying to connect, don't do anything
						if (connectAttempted) {
							return;
						}
						connectAttempted = true;
						
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
				            	connectAttempted = false;
							}
						});
			        }
			    });

				//Control Option leads player to control state to change controls (and eventually other settings)
				settingsOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addSettingState(null, TitleState.class);
			        }
			    });
				
				//Exit Option closes the game
				exitOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	Gdx.app.exit();
			        }
			    });
				
				//Name Rand button generates a random name for the player
				nameRand.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	enterName.setText(NameGenerator.generateFirstLast(true));
			        	setNotification("RANDOM NAME GENERATED!");
			        }
				});
				
				//If the player clicks outside of a text field, the field should be deselected
				addCaptureListener(new InputListener() {
					
					@Override
			         public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			            if (!(event.getTarget() instanceof TextField)) setKeyboardFocus(null);
			            return false;
			         }
		         });
				
				enterIP = new TextField("", gsm.getSkin());
				enterIP.setMessageText("ENTER IP");
				
				enterName = new TextField("", gsm.getSkin());
				enterName.setText(gsm.getRecord().getName());
				enterName.setMessageText("ENTER NAME");
				
				table.add(nameDisplay).pad(5).expandY();
				table.add(enterName).width(300);
				table.add(nameRand).row();
				table.add(singleOption).expandY().row();
				table.add(hostOption).expandY().row();
				table.add(joinOption).expandY();
				table.add(enterIP);
				table.add(searchOption).row();
				table.add(settingsOption).expandY().row();
				table.add(exitOption).expandY().row();
				table.add(notifications).expandY().align(Align.bottomRight);
			}
		};
		app.newMenu(stage);
	}

	@Override
	public void update(float delta) {}

	@Override
	public void render(float delta) {}

	@Override
	public void dispose() {	stage.dispose(); }
	
	/**
	 * This method changes the text notification displayed in the title state
	 * @param notification: new text
	 */
	public void setNotification(String notification) {
		notifications.setText(notification);
	}
}
