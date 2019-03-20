package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

public class MessageWindow {

	private static final int width = 200;
	private static final int height = 160;
	
	private PlayState state;
	
	private Table tableOuter, tableInner; 
	
	private TextArea enterMessage;
	private Text sendMessage, backButton;
	
	private boolean active;
	
	public MessageWindow(PlayState state) {
		this.state = state;
		this.active = false;
		
		this.tableOuter = new Table().center();
		this.tableInner = new Table().center();
		
		addTable();
	}
	
	public void toggleWindow() {
		if (active) {
			tableOuter.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH / 2 - width / 2, -height, .5f, Interpolation.pow5Out));
			tableInner.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH / 2 - width / 2, -height, .5f, Interpolation.pow5Out));
			state.getStage().setKeyboardFocus(null);
		} else {
			tableOuter.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH / 2 - width / 2, 0, .5f, Interpolation.pow5Out));
			tableInner.addAction(Actions.moveTo(HadalGame.CONFIG_WIDTH / 2 - width / 2, 0, .5f, Interpolation.pow5Out));
			state.getStage().setKeyboardFocus(enterMessage);
		}
		active = !active;
	}
	
	public void sendMessage() {
		if (active) {
			if (!enterMessage.getText().equals("")) {
				if (state.isServer()) {
					HadalGame.server.addNotificationToAll(state, state.getPlayer().getName(),  enterMessage.getText());
				} else {
					HadalGame.client.client.sendTCP(new Packets.Notification(state.getPlayer().getName(),  enterMessage.getText()));
				}
			}
			toggleWindow();
		}
		enterMessage.setText("");
	}
	
	public void addTable() {
		tableOuter.clear();
		tableInner.clear();
		
		state.getStage().addActor(tableOuter);
		state.getStage().addActor(tableInner);
		tableOuter.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, -height);
		tableOuter.setWidth(width);
		tableOuter.setHeight(height);
		tableInner.setPosition(HadalGame.CONFIG_WIDTH / 2 - width / 2, -height);
		tableInner.setWidth(width);
		tableInner.setHeight(height);
		tableOuter.add(new MenuWindow(HadalGame.assetManager, state.getGsm(),
						0, 
						0, width, height));
		
		enterMessage = new TextArea("", state.getGsm().getSkin());
		enterMessage.setMessageText("ENTER MESSAGE");
		
		sendMessage = new Text(HadalGame.assetManager, "SEND", 0, 0, Color.WHITE);
		sendMessage.setScale(0.5f);
		
		backButton = new Text(HadalGame.assetManager, "BACK", 0, 0, Color.WHITE);
		backButton.setScale(0.5f);
		
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

		
		tableInner.add(enterMessage).colspan(2).expand().fill().top().row();
		tableInner.add(sendMessage).pad(15);
		tableInner.add(backButton).pad(15);
		
		active = false;
	}
}
