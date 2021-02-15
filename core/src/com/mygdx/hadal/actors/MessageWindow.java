package com.mygdx.hadal.actors;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.input.ClientController;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ConsoleCommandUtil;
import com.mygdx.hadal.utils.TextFilterUtil;

import java.util.ArrayList;

/**
 * The MessageWindow is a ui actor that pops up when the player presses the chat button (default binding shift).
 * This window lets the player type and send messages.
 * @author Clehoff Cenderbender
 */
public class MessageWindow {

	private static final int width = 380;
	private static final int scrollWidth = 360;
	private static final int scrollBarPadding = 10;
	private static final int height = 200;

	private static final int windowX = 0;
	private static final int windowY = 130;

	public static final float logScale = 0.3f;

	public static final float logPadding = 7.5f;
	private static final int inputWidth = 200;
	public static final float inputHeight = 20.0f;
	public static final float inputPad = 5.0f;
	public static final float optionHeight = 35.0f;

	private final PlayState state;
	private final Stage stage;
	
	public Table table, tableLog;
	private TextField enterMessage;
	private Text backButton, sendButton;
	private ScrollPane textLog;
	
	//is this window currently active/invisible? is this window locked and unable to be toggled?
	private boolean active, invisible, locked;

	private static final int maxMessageLength = 80;
	private static final int maxNameLength = 30;
	private static final int padding = 10;

	//alpha of an inactive text window
	private static final float inactiveTransparency = 0.5f;

	//inactive message window disappears after this many seconds of no messages.
	private static final float inactiveFadeDelay = 8.0f;
	private float inactiveFadeCount;

	private static final ArrayList<String> textRecord = new ArrayList<>();
	private final TextureRegion grey;

	public MessageWindow(PlayState state, Stage stage) {
		this.state = state;
		this.stage = stage;
		this.grey = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.GREY.toString()));

		this.table = new Table() {

			@Override
			public void act(float delta) {
				super.act(delta);

				//keep track of how long the window is inactive. Make invisible after enough time.
				if (!active) {
					if (inactiveFadeCount > inactiveFadeDelay) {
						invisible = true;
					}

					if (inactiveFadeCount <= inactiveFadeDelay) {
						inactiveFadeCount += delta;
					}
				}
			}

			@Override
			public void draw(Batch batch, float parentAlpha) {

				if (invisible) { return; }
				if (state.getGsm().getSetting().isHideHUD()) { return; }

				//inactive message windows are drawn with reduced alpha
				if (!active) {
					batch.setColor(1.0f,  1.0f, 1.0f, inactiveTransparency);
				}

				batch.draw(grey, getX() - padding / 2.0f, getY() - padding / 2.0f, getWidth() + padding, getHeight() + padding);

				if (active) {
					super.draw(batch, parentAlpha);
				} else {
					super.draw(batch, inactiveTransparency);
				}

				if (!active) {
					batch.setColor(1.0f,  1.0f, 1.0f, 1.0f);
				}
			}
		};

		table.center();
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

		//log scrolls to bottom when opened.
		textLog.scrollTo(0, 0, 0, 0);
		if (active) {

			//reset keyboard and scroll focus when closing window
			stage.setKeyboardFocus(null);
			if (stage.getScrollFocus() == textLog) {
				stage.setScrollFocus(null);
			}

			//sync controller to avoid sticky keys (set all keys to current held position)
			if (state.getController() != null) {
				if (state.isServer()) {
					((PlayerController) state.getController()).syncController();
				} else {
					((ClientController) state.getController()).syncController();
				}
			}
			fadeOut();
		} else {

			//when opening this window, focus keyboard and scroll on it
			stage.setKeyboardFocus(enterMessage);
			stage.setScrollFocus(textLog);

			//reset controller to avoid sticky keys (releases all held keys)
			if (state.getController() != null) {
				if (state.isServer()) {
					((PlayerController) state.getController()).resetController();
				} else {
					((ClientController) state.getController()).resetController();
				}
			}
			fadeIn();
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

					//if this is a console commend, execute it. (if it is used by host and console is enabled)
					if (ConsoleCommandUtil.parseChatCommand(state, state.getPlayer(), enterMessage.getText()) == -1) {
						if (state.getGsm().getSetting().isConsoleEnabled()) {
							if (ConsoleCommandUtil.parseConsoleCommand(state, enterMessage.getText()) == -1) {
								HadalGame.server.addChatToAll(state, enterMessage.getText(), DialogType.DIALOG, 0);
							}
						} else {
							HadalGame.server.addChatToAll(state, enterMessage.getText(), DialogType.DIALOG, 0);
						}
					}
				} else {

					//if this is a chat command, execute it.
					if (ConsoleCommandUtil.parseChatCommandClient((ClientState) state, state.getPlayer(), enterMessage.getText()) == -1) {
						HadalGame.client.sendTCP(new Packets.ClientChat(enterMessage.getText(), DialogType.DIALOG));
					}
				}
			} else {

				//hitting enter with no text typed closes the window
				toggleWindow();
			}
		}
		enterMessage.setText("");
	}
	
	/**
	 * This adds the table to the stage.
	 * It is called when the actor is instantiated
	 */
	private void addTable() {
		table.clear();
		stage.addActor(table);
		table.setPosition(windowX, windowY);
		table.setWidth(width);
		table.setHeight(height);

		tableLog.padBottom(logPadding);

		textLog = new ScrollPane(tableLog, GameStateManager.getSkin());
		textLog.setFadeScrollBars(true);

		enterMessage = new TextField("", GameStateManager.getSkin()) {

			//this is used to indicate if the player is typing
			private boolean typing;
			private float typeCount;

			//player is "typing" 0.5 seconds after they type last.
			private static final float typingInterval = 0.5f;
			@Override
			protected InputListener createInputListener () {
				
				return new TextFieldClickListener() {
					
					@Override
		            public boolean keyDown(InputEvent event, int keycode) {

						//unless we are exiting/entering, typing should indicate that the player is typing
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

            	//after typing any key, the player is "typing" for some time where an icon will be drawn above them
            	typeCount += delta;
            	if (typeCount >= typingInterval) {
            		typeCount = 0;

            		//if typing, we notify other players that we are typing to display the speech bubble
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

		backButton = new Text("EXIT", 0, 0, true);
		backButton.setScale(logScale);

		sendButton = new Text("SEND", 0, 0, true);
		sendButton.setScale(logScale);

		sendButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				sendMessage();
			}
		});
		backButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent e, float x, float y) {
				toggleWindow();
			}
		});

		table.add(textLog).colspan(3).width(scrollWidth).expandY().pad(inputPad).top().left().row();
		table.add(backButton).height(optionHeight).pad(inputPad).bottom().left();
		table.add(enterMessage).width(inputWidth).height(inputHeight).bottom().center();
		table.add(sendButton).height(optionHeight).pad(inputPad).bottom().right();

		//windows starts off retracted
		fadeOut();

		//add starting text to give instructions to players
		addTextLine(TextFilterUtil.filterHotkeys(GameStateManager.miscText.getString("start")));

		//load previously sent messages so chat log doesn't clear on level transition
		for (String s: textRecord) {
			addTextLine(s);
		}

		//this makes clicking outside the window exit it.
		state.getStage().addCaptureListener(new InputListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (!table.isAscendantOf(event.getTarget())) {
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
	public void addText(String text, DialogType type, int connID) {
		User user;
		if (state.isServer()) {
			user = HadalGame.server.getUsers().get(connID);
		} else {
			user = HadalGame.client.getUsers().get(connID);
		}

		//do not display messages from muted players
		if (user != null) {
			if (!user.isMuted() && user.getPlayer() != null) {
				String newText;

				//system messages are all red.
				if (type.equals(DialogType.SYSTEM)) {
					newText = "[RED]" + user.getPlayer().getName() + ": " + text + " []";
				} else if (user.getPlayer() == null) {

					//text is white if player is a spectator or otherwise has no player
					newText = "[WHITE]" + user.getScores().getNameShort() + ": " + text + " []";
				} else {

					//normal chat messages color names according to the player's team color
					newText = WeaponUtils.getPlayerColorName(user.getPlayer(), maxNameLength) + ": " + text + " []";
				}
				addTextLine(newText);
				textRecord.add(newText);
			}
		}
	}
	
	/**
	 * After adding a text to the dialog record, we create a text actor for it and add that to the dialog box actor.
	 */
	private void addTextLine(String text) {
		Text newEntry = new Text(text, 0, 0, false, true, scrollWidth - scrollBarPadding);
		newEntry.setScale(logScale);
		newEntry.setFont(HadalGame.SYSTEM_FONT_UI_SMALL);

		tableLog.add(newEntry).pad(logPadding, 0, logPadding, scrollBarPadding).width(scrollWidth - scrollBarPadding).left().row();
		textLog.scrollTo(0, 0, 0, 0);

		//new text makes the window visible and resets the inactive fade time
		invisible = false;
		inactiveFadeCount = 0.0f;
	}

	/**
	 * When fading out, we disable inputs, hide actors and set actor as active
	 */
	private void fadeOut() {
		textLog.setTouchable(Touchable.disabled);
		backButton.setVisible(false);
		enterMessage.setVisible(false);
		sendButton.setVisible(false);
		active = false;
		invisible = false;
		inactiveFadeCount = 0.0f;
	}

	/**
	 * When fading in, we enable inputs, show actors and set actor as inactive
	 */
	private void fadeIn() {
		textLog.setTouchable(Touchable.enabled);
		backButton.setVisible(true);
		enterMessage.setVisible(true);
		sendButton.setVisible(true);
		active = true;
		invisible = false;
		inactiveFadeCount = 0.0f;
	}

	public static ArrayList<String> getTextRecord() { return textRecord; }	
	
	public boolean isActive() { return active; }
	
	/**
	 * this is used to create the result's version of the message window
	 * this makes the window locked into being active
	 */
	public void setLocked(boolean locked) { this.locked = locked; }
}
