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
import com.mygdx.hadal.managers.GameStateManager.State;

/**
 * The TitleState is created upon initializing the game and will display an image and allow the player to play or exit.
 * TODO: Eventually, this might be where we initialize game data + assets + player change settings.
 * @author Zachary Tu
 *
 */
public class TitleState extends GameState {

	private Stage stage;
	
	//Temporary links to other modules for testing.
	private Table table;
	private Text hostOption, joinOption, exitOption, controlOption, searchOption, notifications;
	private TextField enterIP;
	
	private final static int width = 500;
	private final static int height = 150;
	private final static int xOffset = 100;

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
				addActor(new TitleBackdrop(HadalGame.assetManager));
				addActor(new MenuWindow(HadalGame.assetManager, gsm, HadalGame.CONFIG_WIDTH - width - xOffset, 0, width, height));
				
				table = new Table();
				table.setLayoutEnabled(true);
				table.setPosition(HadalGame.CONFIG_WIDTH - width - xOffset, 0);
				table.setSize(width, height);
				addActor(table);
				
				hostOption = new Text(HadalGame.assetManager, "HOST SERVER", 0, 0, Color.BLACK);
				hostOption.setScale(0.5f);
				joinOption = new Text(HadalGame.assetManager, "JOIN: ", 0, 0, Color.BLACK);
				joinOption.setScale(0.5f);
				searchOption = new Text(HadalGame.assetManager, "SEARCH?", 0, 0, Color.BLACK);
				searchOption.setScale(0.5f);
				controlOption = new Text(HadalGame.assetManager, "CONTROLS", 0, 0, Color.BLACK);
				controlOption.setScale(0.5f);
				exitOption = new Text(HadalGame.assetManager, "EXIT?", 0, 0, Color.BLACK);
				exitOption.setScale(0.5f);
				notifications = new Text(HadalGame.assetManager, "", 0, 0, Color.BLACK);
				notifications.setScale(0.5f);
				
				hostOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						HadalGame.server.init();
			        	getGsm().addState(State.HUB, TitleState.class);
			        }
			    });
				
				joinOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						HadalGame.client.init(false);
						new Thread("Connect") {
				            public void run () {
				                try {
				                	HadalGame.client.client.connect(5000, enterIP.getText(),
				                			KryoClient.tcpPortSocket, KryoClient.udpPortSocket);
				                    notifications.setText("CONNECTED TO SERVER: " + enterIP.getText());
				                } catch (IOException ex) {
				                    ex.printStackTrace();
				                    notifications.setText("FAILED TO CONNECT TO SERVER!");
				                }
				            }
				        }.start();
				        
				        try {
                            Thread.sleep(600);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
			        }
			    });
				
				searchOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
						notifications.setText("SEARCHING FOR SERVER...");
						
						new Thread("Search") {
							
							@Override
				            public void run () {
								enterIP.setText(HadalGame.client.searchServer());
								if (enterIP.getText().equals("NO IP FOUND")) {
									notifications.setText("SERVER NOT FOUND!");
								} else {
									notifications.setText("FOUND SERVER: " + enterIP.getText());
								}
				            }
						}.start();
						
						try {
                            Thread.sleep(600);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
			        }
			    });

				controlOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	getGsm().addState(State.CONTROL, TitleState.class);
			        }
			    });
				
				exitOption.addListener(new ClickListener() {
					
					@Override
			        public void clicked(InputEvent e, float x, float y) {
			        	Gdx.app.exit();
			        }
			    });
				
				addCaptureListener(new InputListener() {
					
					@Override
			         public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
			            if (!(event.getTarget() instanceof TextField)) setKeyboardFocus(null);
			            return false;
			         }
		         });
				
				enterIP = new TextField("", gsm.getSkin());
				enterIP.setMessageText("ENTER IP");
				
				table.add(hostOption).pad(5).expandY().row();
				table.add(joinOption).expandY();
				table.add(enterIP);
				table.add(searchOption).row();
				table.add(controlOption).expandY().row();
				table.add(exitOption).expandY().row();
				table.add(notifications).expandY().align(Align.bottomRight);
			}
		};
		app.newMenu(stage);
	}

	/**
	 * 
	 */
	@Override
	public void update(float delta) {

	}

	/**
	 * This state will draw the image.
	 */
	@Override
	public void render() {
		
	}

	/**
	 * Delete the image texture.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

}