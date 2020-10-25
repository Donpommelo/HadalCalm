package com.mygdx.hadal.actors;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ConsoleCommandUtil;

import java.util.ArrayList;

/**
 * The MessageWindow is a ui actor that pops up when the player presses the chat button (default binding shift).
 * This window lets the player type and send messages.
 * @author Clehoff Cenderbender
 */
public class MessageWindow {

	private static final int width = 500;
	private static final int height = 250;
	
	private static final int windowX = 440;
	private static final int windowYActive = 0;
	private static final int windowYInactive = -height;
	
	public static final float logScale = 0.25f;
	
	public static final float logPadding = 10.0f;
	
	private final PlayState state;
	private final Stage stage;
	
	public Table tableOuter, tableInner, tableLog; 
	
	private TextField enterMessage;
	private Text backButton;
	private ScrollPane textLog;
	
	//is this window currently visible? is this window locked and unable to be toggled?
	private boolean active, locked;
	
	private static final int maxMessageLength = 100;
	
	private static final ArrayList<String> textRecord = new ArrayList<>();
	
	public MessageWindow(PlayState state, Stage stage) {
		this.state = state;
		this.stage = stage;
		this.active = false;
		
		this.tableOuter = new Table().center();
		this.tableInner = new Table().center();
		this.tableLog = new Table().center();

		addTable();
	}
	
	/**
	 * This toggles the window on and off. It is run when the player presses the chat button
	 * This sets the keyboard focus to the window/back to the playstate
	 */
	public void toggleWindow() {
		
		//window is locked in the results state
		if (locked) { return; }
		
		//enables message to be run and window to be scrolled. Ran after actor enters screen.
		Runnable enableMsg = () -> {

			if (stage != null) {
				stage.setKeyboardFocus(enterMessage);
				stage.setScrollFocus(textLog);
				textLog.scrollTo(0, 0, 0, 0);
			}
			if (state.getController() != null) {
				if (state.isServer()) {
					((PlayerController) state.getController()).resetController();
				} else {
					((ClientController) state.getController()).resetController();
				}
			}
			active = true;
		};
		
		//disables typing and scrolling for actor. Ran after actor exits screen.
		Runnable disableMsg = () -> {

			if (!active) { return; }

			if (stage != null) {
				stage.setKeyboardFocus(null);
				if (stage.getScrollFocus() == textLog) {
					stage.setScrollFocus(null);
				}
			}
			if (state.getController() != null) {
				if (state.isServer()) {
					((PlayerController) state.getController()).syncController();
				} else {
					((ClientController) state.getController()).syncController();
				}
			}
			active = false;
		};
		
		if (active) {
			tableOuter.addAction(Actions.moveTo(windowX, windowYInactive, 0.25f, Interpolation.pow5Out));
			tableInner.addAction(Actions.sequence(Actions.run(disableMsg), Actions.moveTo(windowX, windowYInactive, 0.25f, Interpolation.pow5Out)));
		} else {
			tableOuter.addAction(Actions.moveTo(windowX, windowYActive, 0.5f, Interpolation.pow5Out));
			tableInner.addAction(Actions.sequence(Actions.run(enableMsg), Actions.moveTo(windowX, windowYActive, 0.25f, Interpolation.pow5Out)));
		}
		SoundEffect.UISWITCH2.play(state.getGsm(), 1.0f, false);
		enterMessage.setText("");
	}
	
	/**
	 * Sends a message.
	 * Server sends the message to all clients.
	 * Clients send a message to the server (who then relays it to all clients)
	 */
	public void sendMessage() {
		if (active) {
			if (!enterMessage.getText().equals("")) {
				if (state.isServer()) {
					if (ConsoleCommandUtil.parseChatCommand(state, state.getPlayer(), enterMessage.getText()) == -1) {
						if (state.getGsm().getSetting().isConsoleEnabled()) {
							if (ConsoleCommandUtil.parseConsoleCommand(state, enterMessage.getText()) == -1) {
								HadalGame.server.addNotificationToAll(state, state.getPlayer().getName(), enterMessage.getText(), DialogType.DIALOG, 0);
							}
						} else {
							HadalGame.server.addNotificationToAll(state, state.getPlayer().getName(), enterMessage.getText(), DialogType.DIALOG, 0);
						}
					}
				} else {
					if (ConsoleCommandUtil.parseChatCommandClient((ClientState) state, state.getPlayer(), enterMessage.getText()) == -1) {
						HadalGame.client.sendTCP(new Packets.ClientNotification(state.getPlayer().getName(), enterMessage.getText(), DialogType.DIALOG));
					}
				}
			}
		}
		enterMessage.setText("");
	}
	
	/**
	 * This adds the table to the stage.
	 * It is called when the actor is instantiated
	 */
	private void addTable() {
		tableOuter.clear();
		tableInner.clear();
		
		stage.addActor(tableOuter);
		stage.addActor(tableInner);
		tableOuter.setPosition(windowX, windowYInactive);
		tableOuter.setWidth(width);
		tableOuter.setHeight(height);
		tableInner.setPosition(windowX, windowYInactive);
		tableInner.setWidth(width);
		tableInner.setHeight(height);
		tableOuter.add(new MenuWindow(0, 0, width, height));
		
		tableLog.setWidth(width);
		tableLog.setHeight(height);
		tableLog.padBottom(logPadding);
		
		textLog = new ScrollPane(tableLog, GameStateManager.getSkin());
		textLog.setFadeScrollBars(true);
		
		enterMessage = new TextField("", GameStateManager.getSkin()) {
			
			private boolean typing;
			private static final float typingInterval = 0.5f;
			private float typeCount;
			@Override
			protected InputListener createInputListener () {
				
				return new TextFieldClickListener() {
					
					@Override
		            public boolean keyDown(InputEvent event, int keycode) {
						if (keycode != Keys.ENTER && keycode != PlayerAction.EXIT_MENU.getKey()) {
							typing = true;
						}

						//window scrolls to bottom when typing
						textLog.scrollTo(0, 0, 0, 0);

						return super.keyDown(event, keycode);
					}
					
					@Override
		            public boolean keyUp(InputEvent event, int keycode) {
		                if (keycode == Keys.ENTER) {
		                	sendMessage();
		                } else if (keycode == PlayerAction.EXIT_MENU.getKey()) {
		                	toggleWindow();
		                }
		                return super.keyUp(event, keycode);
		            }
		        };
			}
			
			@Override
            public void act(float delta) {
            	super.act(delta);
            	typeCount += delta;
            	if (typeCount >= typingInterval) {
            		typeCount = 0;
            		if (typing) {
            			typing = false;
            			state.getPlayer().startTyping();
            			if (state.isServer()) {
            				HadalGame.server.sendToAllUDP(new Packets.SyncTyping(state.getPlayer().getEntityID().toString()));
            			} else {
            				HadalGame.client.sendUDP(new Packets.SyncTyping(state.getPlayer().getEntityID().toString()));
            			}
            		}
            	}
            }
		};

		enterMessage.setMaxLength(maxMessageLength);
		
		Text sendMessage = new Text("SEND", 0, 0, true);
		sendMessage.setScale(0.3f);
		
		backButton = new Text("EXIT", 0, 0, true);
		backButton.setScale(0.3f);
		
		//sending a message should return focus to the playstate
		sendMessage.addListener(new ClickListener() {
			
			@Override
	        public void clicked(InputEvent e, float x, float y) {
				sendMessage();
				state.getStage().setKeyboardFocus(null);
			}
		});
		
		backButton.addListener(new ClickListener() {
			
			@Override
	        public void clicked(InputEvent e, float x, float y) {
				toggleWindow();
			}
		});

		tableInner.add(textLog).colspan(2).expand().pad(15).top().left().row();
		tableInner.add(enterMessage).colspan(2).expand(1, 0).bottom().row();
		tableInner.add(backButton).pad(15).bottom().left();
		tableInner.add(sendMessage).pad(15).bottom().right();
		
		//windows starts off retracted
		active = false;
		
		//load previously sent messages so chat log doesn't clear on level transition
		for (String s: textRecord) {
			addTextLine(s);
		}
		
		state.getStage().addCaptureListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!(tableOuter.isAscendantOf(event.getTarget()) || tableInner.isAscendantOf(event.getTarget()))) {
		        	if (active) {
		        		toggleWindow();
		        	}
		        }
		        return false;
			}
		});
	}
	
	/**
	 * This adds a text to the text log. Called whenever a dialog is added to the dialog box.
	 * @param text: the new string we add to the message window
	 */
	public void addText(String text) {
		textRecord.add(text);
		addTextLine(text);
	}
	
	/**
	 * After adding a text to the dialog record, we create a text actor for it and add that to the dialog box actor.
	 */
	private void addTextLine(String text) {
		Text newEntry = new Text(text, 0, 0, false, true, width - 50);
		newEntry.setScale(logScale);

		tableLog.add(newEntry).pad(logPadding, logPadding, logPadding, logPadding).left().row();
		textLog.scrollTo(0, 0, 0, 0);
	}
	
	public static ArrayList<String> getTextRecord() { return textRecord; }	
	
	public boolean isActive() { return active; }
	
	/**
	 * this is used to create the result's version of the message window
	 * this makes the window locked into being active
	 */
	public void setLocked(boolean locked) { 
		this.locked = locked;
		
		if (locked) {
			backButton.remove();
		}
	}
}
