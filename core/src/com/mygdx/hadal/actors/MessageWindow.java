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
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.input.PlayerAction;
import com.mygdx.hadal.input.PlayerController;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.TextFilterUtil;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.ConsoleCommandUtil;
import com.mygdx.hadal.utils.TextUtil;

import static com.mygdx.hadal.constants.Constants.MAX_MESSAGE_LENGTH;
import static com.mygdx.hadal.constants.Constants.MAX_NAME_LENGTH_LONG;

/**
 * The MessageWindow is a ui actor that pops up when the player presses the chat button (default binding shift).
 * This window lets the player type and send messages.
 * @author Clehoff Cenderbender
 */
public class MessageWindow {

	private static final int WIDTH = 380;
	private static final int HEIGHT = 200;
	private static final int SCROLL_WIDTH = 360;
	private static final int SCROLL_PAD = 10;

	private static final int WINDOW_X = 0;
	private static final int WINDOW_Y = 130;

	public static final float LOG_SCALE = 0.3f;
	public static final float LOG_PAD = 7.5f;
	private static final int INPUT_WIDTH = 200;
	public static final float INPUT_HEIGHT = 20.0f;
	public static final float INPUT_PAD = 5.0f;
	public static final float OPTION_HEIGHT = 35.0f;
	private static final int BORDER_PAD = 10;

	//alpha of an inactive text window
	private static final float INACTIVE_TRANSPARENCY = 0.5f;

	//inactive message window disappears after this many seconds of no messages.
	private static final float INACTIVE_FADE_DELAY = 8.0f;

	//this tracks all text messages. Used for saving text logs to docs
	private static final Array<String> textRecord = new Array<>();

	private final PlayState state;
	private final Stage stage;
	
	public final Table table, tableLog;
	private TextField enterMessage;
	private Text backButton, sendButton;
	private ScrollPane textLog;
	
	//is this window currently active/invisible? is this window locked and unable to be toggled?
	private boolean active, invisible, locked;

	private float inactiveFadeCount;

	//grey image used as background for the message window
	private final TextureRegion grey;

	public MessageWindow(PlayState state, Stage stage) {
		this.state = state;
		this.stage = stage;
		this.grey = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.GREY.toString()));

		this.table = new Table() {

			@Override
			public void act(float delta) {
				super.act(delta);

				//keep track of how long the window is inactive. Make invisible after enough time inactive.
				if (!active) {
					if (INACTIVE_FADE_DELAY < inactiveFadeCount) {
						invisible = true;
					}
					if (INACTIVE_FADE_DELAY >= inactiveFadeCount) {
						inactiveFadeCount += delta;
					}
				}
			}

			@Override
			public void draw(Batch batch, float parentAlpha) {

				//don't draw window if it has been inactive too long or if hud is hidden
				if (invisible) { return; }
				if (state.getGsm().getSetting().isHideHUD()) { return; }

				//inactive message windows are drawn with reduced alpha
				if (!active) {
					batch.setColor(1.0f,  1.0f, 1.0f, INACTIVE_TRANSPARENCY);
				}

				batch.draw(grey, getX() - BORDER_PAD / 2.0f, getY() - BORDER_PAD / 2.0f, getWidth() + BORDER_PAD,
						getHeight() + BORDER_PAD);

				if (active) {
					super.draw(batch, parentAlpha);
				} else {
					super.draw(batch, INACTIVE_TRANSPARENCY);
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
			if (null != state.getController()) {
				((PlayerController) state.getController()).syncController();
			}
			fadeOut();
		} else {

			//when opening this window, focus keyboard and scroll on it
			//we turn off chat wheel so it doesn't get stuck
			state.getChatWheel().setVisibility(false);
			stage.setKeyboardFocus(enterMessage);
			stage.setScrollFocus(textLog);

			//reset controller to avoid sticky keys (releases all held keys)
			if (null != state.getController()) {
				((PlayerController) state.getController()).resetController();
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
			if (!"".equals(enterMessage.getText())) {
				if (state.isServer()) {

					//if this is a console commend, execute it. (if it is used by host and console is enabled)
					if (-1 == ConsoleCommandUtil.parseChatCommand(state, HadalGame.usm.getOwnPlayer(), enterMessage.getText())) {
						if (state.getGsm().getSetting().isConsoleEnabled()) {
							if (-1 == ConsoleCommandUtil.parseConsoleCommand(state, enterMessage.getText())) {
								HadalGame.server.addChatToAll(state, enterMessage.getText(), DialogType.DIALOG, 0);
							}
						} else {
							HadalGame.server.addChatToAll(state, enterMessage.getText(), DialogType.DIALOG, 0);
						}
					}
				} else {
					//if this is a chat command, execute it.
					if (-1 == ConsoleCommandUtil.parseChatCommand(state, HadalGame.usm.getOwnPlayer(), enterMessage.getText())) {
						HadalGame.client.sendTCP(new Packets.ClientChat(enterMessage.getText(), DialogType.DIALOG));
					}
				}
			}
			//hitting enter closes the window regardless of whether text is present
			toggleWindow();
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
		table.setPosition(WINDOW_X, WINDOW_Y);
		table.setWidth(WIDTH);
		table.setHeight(HEIGHT);

		tableLog.padBottom(LOG_PAD);

		textLog = new ScrollPane(tableLog, GameStateManager.getSkin());
		textLog.setFadeScrollBars(true);

		enterMessage = new TextField("", GameStateManager.getSkin()) {

			//this is used to indicate if the player is typing
			private boolean typing;
			private float typeCount;

			//player is "typing" 0.5 seconds after they type last.
			private static final float TYPING_INTERVAL = 0.5f;
			@Override
			protected InputListener createInputListener () {
				
				return new TextFieldClickListener() {
					
					@Override
		            public boolean keyDown(InputEvent event, int keycode) {

						//unless we are exiting/entering, typing should indicate that the player is typing
						if (Keys.ENTER != keycode && PlayerAction.EXIT_MENU.getKey() != keycode) {
							typing = true;
						}

						//window scrolls to bottom when typing
						textLog.scrollTo(0, 0, 0, 0);

						return super.keyDown(event, keycode);
					}
					
					@Override
		            public boolean keyUp(InputEvent event, int keycode) {
		                if (Keys.ENTER == keycode) {
		                	sendMessage();
		                } else if (PlayerAction.EXIT_MENU.getKey() == keycode) {
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
            	if (TYPING_INTERVAL <= typeCount) {
            		typeCount = 0;

            		//if typing, we notify other players that we are typing to display the speech bubble
            		if (typing) {
            			typing = false;
						if (null != HadalGame.usm.getOwnPlayer()) {
							HadalGame.usm.getOwnPlayer().getUiHelper().startTyping();
						}
            		}
            	}
            }
		};
		enterMessage.setMaxLength(MAX_MESSAGE_LENGTH);

		backButton = new Text(UIText.EXIT.text()).setButton(true);
		backButton.setScale(LOG_SCALE);

		sendButton = new Text(UIText.SEND.text()).setButton(true);
		sendButton.setScale(LOG_SCALE);

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

		table.add(textLog).colspan(3).width(SCROLL_WIDTH).expandY().pad(INPUT_PAD).top().left().row();
		table.add(backButton).height(OPTION_HEIGHT).pad(INPUT_PAD).bottom().left();
		table.add(enterMessage).width(INPUT_WIDTH).height(INPUT_HEIGHT).bottom().center();
		table.add(sendButton).height(OPTION_HEIGHT).pad(INPUT_PAD).bottom().right();

		//windows starts off retracted
		fadeOut();

		//add starting text to give instructions to players
		addTextLine(TextFilterUtil.filterHotkeys(UIText.INFO_START.text()));

		//load previously sent messages so chat log doesn't clear on level transition
		for (String s : textRecord) {
			addTextLine(s);
		}

		//level transitions should not make the text window reappear
		if (!state.getMode().isHub()) {
			invisible = true;
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
		User user = HadalGame.usm.getUsers().get(connID);

		//do not display messages from muted players
		if (null != user) {
			if (!user.isMuted()) {
				String newText;

				//system messages are all red.
				if (DialogType.SYSTEM.equals(type)) {
					newText = "[RED]" + user.getStringManager().getNameShort() + ": " + text + " []";
				} else if (null == user.getPlayer()) {

					//text is white if player is a spectator or otherwise has no player
					newText = "[WHITE]" + user.getStringManager().getNameShort() + ": " + text + " []";
				} else {

					//normal chat messages color names according to the player's team color
					newText = TextUtil.getPlayerColorName(user.getPlayer(), MAX_NAME_LENGTH_LONG) + ": " + text + " []";
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
		Text newEntry = new Text(text).setWrap(SCROLL_WIDTH - SCROLL_PAD);
		newEntry.setScale(LOG_SCALE);
		newEntry.setFont(HadalGame.FONT_UI_ALT);

		tableLog.add(newEntry).pad(LOG_PAD, 0, LOG_PAD, SCROLL_PAD).width(SCROLL_WIDTH - SCROLL_PAD).left().row();
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

	public static Array<String> getTextRecord() { return textRecord; }
	
	public boolean isActive() { return active; }
	
	/**
	 * this is used to create the result screen's version of the message window
	 * this makes the window locked into being active
	 */
	public void setLocked(boolean locked) { this.locked = locked; }
}
