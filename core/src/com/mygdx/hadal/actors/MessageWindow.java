package com.mygdx.hadal.actors;

import java.util.ArrayList;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ConsoleCommandUtil;

/**
 * The MessageWindow is a ui actor that pops up when the player presses the chat button (default binding shift).
 * This window lets the player type and send messages.
 * @author Zachary Tu
 *
 */
public class MessageWindow {

	private static final int width = 500;
	private static final int height = 300;
	
	private static final int windowX = 440;
	private static final int windowYActive = 0;
	private static final int windowYInactive = -height;
	
	public static final float logScale = 0.25f;
	
	public static final float logPadding = 10.0f;
	
	private PlayState state;
	
	private Table tableOuter, tableInner, tableLog; 
	
	private TextField enterMessage;
	private Text sendMessage, backButton;
	private ScrollPane textLog;
	
	//is this window currently visible?
	private boolean active;
	
	private final static int maxMessageLength = 100;
	
	private static ArrayList<String> textRecord = new ArrayList<String>();
	
	public MessageWindow(PlayState state) {
		this.state = state;
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
		
		//enables message to be run and window to be scrolled. Ran after actor enters screen.
		Runnable enableMsg = new Runnable() {

			@Override
			public void run() {
				if (state.getStage() != null) {
					state.getStage().setKeyboardFocus(enterMessage);
					state.getStage().setScrollFocus(textLog);
					textLog.scrollTo(0, 0, 0, 0);
				}
			}
		};
		
		//disables typing and scrolling for actoe. Ran after actor exits screen.
		Runnable disableMsg = new Runnable() {

			@Override
			public void run() {
				
				if (state.getStage() != null) {
					state.getStage().setKeyboardFocus(null);
					if (!state.getStage().getScrollFocus().equals(null)) {
						if (state.getStage().getScrollFocus().equals(textLog)) {
							state.getStage().setScrollFocus(null);
						}
					}
				}
			}
		};
		
		if (active) {
			tableOuter.addAction(Actions.moveTo(windowX, windowYInactive, .25f, Interpolation.pow5Out));
			tableInner.addAction(Actions.sequence(Actions.moveTo(windowX, windowYInactive, .25f, Interpolation.pow5Out), Actions.run(disableMsg)));
			
			SoundEffect.UISWITCH2.play(state.getGsm());
		} else {
			tableOuter.addAction(Actions.moveTo(windowX, windowYActive, .5f, Interpolation.pow5Out));
			tableInner.addAction(Actions.sequence(Actions.moveTo(windowX, windowYActive, .25f, Interpolation.pow5Out), Actions.run(enableMsg)));
			
			SoundEffect.UISWITCH2.play(state.getGsm());
		}
		
		active = !active;
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
					HadalGame.server.addNotificationToAll(state, state.getPlayer().getName(), enterMessage.getText());
					
					if (state.getGsm().getSetting().isConsoleEnabled()) {
						ConsoleCommandUtil.parseCommand(state, enterMessage.getText());
					}
					
				} else {
					HadalGame.client.client.sendTCP(new Packets.Notification(state.getPlayer().getName(), enterMessage.getText()));
				}
			}
		}
		enterMessage.setText("");
	}
	
	/**
	 * This adds the table to the stage.
	 * It is called when the actor is instantiated
	 */
	public void addTable() {
		tableOuter.clear();
		tableInner.clear();
		
		state.getStage().addActor(tableOuter);
		state.getStage().addActor(tableInner);
		tableOuter.setPosition(windowX, windowYInactive);
		tableOuter.setWidth(width);
		tableOuter.setHeight(height);
		tableInner.setPosition(windowX, windowYInactive);
		tableInner.setWidth(width);
		tableInner.setHeight(height);
		tableOuter.add(new MenuWindow(0, 0, width, height));
		
		tableLog.setWidth(width);
		tableLog.setHeight(height);
		
		textLog = new ScrollPane(tableLog, GameStateManager.getSkin());
		textLog.setFadeScrollBars(true);
		
		enterMessage = new TextField("", GameStateManager.getSkin());
		enterMessage.setMaxLength(maxMessageLength);
		
		sendMessage = new Text("SEND", 0, 0, true);
		sendMessage.setScale(0.5f);
		
		backButton = new Text("EXIT", 0, 0, true);
		backButton.setScale(0.5f);
		
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
	 * @param text
	 */
	public void addText(String text) {
		textRecord.add(text);
		addTextLine(text);
	}
	
	/**
	 * After adding a text to the dialog record, we create a text actor for it and add that to the dialog box actor.
	 */
	public void addTextLine(String text) {
		Text newEntry = new Text(text, 0, 0, false, true, width - 50);
		newEntry.setScale(logScale);

		tableLog.add(newEntry).pad(logPadding, logPadding, logPadding, logPadding).left().row();
		textLog.scrollTo(0, 0, 0, 0);
	}
}
