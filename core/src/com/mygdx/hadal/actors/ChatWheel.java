package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

public class ChatWheel {

	private PlayState state;
	private BitmapFont font;
	private Stage stage;
	
	private TextureRegion wheelBase, wheelOverlay, wheelIndicator;
	private AHadalActor fug;
	
	private final static int wheelX = 540;
	private final static int wheelY = 260;
	private final static int wheelWidth = 200;
	private final static int wheelHeight = 200;
	
	private final static int indicatorWidth = 40;
	private final static int indicatorHeight = 40;
	
	private final static int textTargetWidth = 300;

	private final static int text1X = 640;
	private final static int text1Y = 480;
	
	private final static int text2X = 725;
	private final static int text2Y = 420;
	
	private final static int text3X = 725;
	private final static int text3Y = 300;
	
	private final static int text4X = 640;
	private final static int text4Y = 240;
	
	private final static int text5X = 555;
	private final static int text5Y = 300;
	
	private final static int text6X = 555;
	private final static int text6Y = 420;
	
	private final static float indicatorAmplification = 1.25f;
	private final static float textScaleUnselected = 0.4f;
	private final static float textScaleSelected = 0.6f;
	private final static float selectThreshold = 6400.0f;

	private final static String[] options = {"ready", "fug", "shid", "<roll>", "temp", "temp"};
	
	public ChatWheel(PlayState state, Stage stage) {
		this.state = state;
		this.font = HadalGame.SYSTEM_FONT_UI;
		this.stage = stage;
		
		this.wheelBase = Sprite.UI_MO_BASE.getFrame();
		this.wheelOverlay = Sprite.UI_MO_OVERLAY.getFrame();
		this.wheelIndicator = Sprite.UI_MO_READY.getFrame();
		
		addTable();
	}
	
	private Vector2 basePosition = new Vector2();
	private Vector2 pointerPosition = new Vector2();
	private Vector2 dist = new Vector2();
	public void addTable() {
		
		fug = new AHadalActor(wheelX, wheelY, wheelWidth, wheelHeight) {
			
			@Override
			public void draw(Batch batch, float alpha) {
				batch.draw(wheelBase, getX(), getY(), getWidth(), getHeight());
				batch.draw(wheelOverlay, getX(), getY(), getWidth(), getHeight());
				
				dist.set(Gdx.input.getX(), -Gdx.input.getY()).sub(basePosition).scl(indicatorAmplification).limit(wheelWidth / 2);
				pointerPosition.set(getX() + getWidth() / 2 - indicatorWidth / 2, getY() + getHeight() / 2 - indicatorHeight / 2).add(dist);
				batch.draw(wheelIndicator, pointerPosition.x, pointerPosition.y, indicatorWidth, indicatorHeight);

				int option = distToOption();

				font.setColor(Color.RED);
				
				if (option == 0) {
					font.getData().setScale(textScaleSelected);
				} else {
					font.getData().setScale(textScaleUnselected);
				}
				font.draw(batch, options[0], text1X - textTargetWidth / 2, text1Y, textTargetWidth, Align.center, true);
				
				if (option == 1) {
					font.getData().setScale(textScaleSelected);
				} else {
					font.getData().setScale(textScaleUnselected);
				}
				font.draw(batch, options[1], text2X, text2Y, textTargetWidth, Align.left, true);
				
				if (option == 2) {
					font.getData().setScale(textScaleSelected);
				} else {
					font.getData().setScale(textScaleUnselected);
				}
				font.draw(batch, options[2], text3X, text3Y, textTargetWidth, Align.left, true);
				
				if (option == 3) {
					font.getData().setScale(textScaleSelected);
				} else {
					font.getData().setScale(textScaleUnselected);
				}
				font.draw(batch, options[3], text4X - textTargetWidth / 2, text4Y, textTargetWidth, Align.center, true);
				
				if (option == 4) {
					font.getData().setScale(textScaleSelected);
				} else {
					font.getData().setScale(textScaleUnselected);
				}
				font.draw(batch, options[4], text5X - textTargetWidth, text5Y, textTargetWidth, Align.right, true);
				
				if (option == 5) {
					font.getData().setScale(textScaleSelected);
				} else {
					font.getData().setScale(textScaleUnselected);
				}
				font.draw(batch, options[5], text6X - textTargetWidth, text6Y, textTargetWidth, Align.right, true);
				
				font.setColor(HadalGame.DEFAULT_TEXT_COLOR);
			}
		};
		
		fug.setVisible(false);
		stage.addActor(fug);
	}
	
	private int distToOption() {
		
		boolean selected = false;
		float angle = dist.angle();
		if (dist.len2() > selectThreshold) {
			selected = true;
		}
		
		if (!selected) {
			return -1;
		}
		if (selected && (angle > 60 && angle <= 120)) {
			return 0;
		}
		if (selected && (angle <= 60)) {
			return 1;
		}
		if (selected && (angle > 300)) {
			return 2;
		}
		if (selected && (angle > 240 && angle <= 300)) {
			return 3;
		}
		if (selected && (angle > 180 && angle <= 240)) {
			return 4;
		}
		if (selected && (angle > 120 && angle <= 180)) {
			return 5;
		}
		
		return -1;
	}
	
	public void setVisibility(boolean visible) {
		fug.setVisible(visible);
		
		if (visible) {
			
			basePosition.set(Gdx.input.getX(), -Gdx.input.getY());
			
		} else {
			//do the chat
			int option = distToOption();
			
			if (option != -1) {
				if (state.isServer()) {
					HadalGame.server.addNotificationToAll(state, state.getPlayer().getName(), options[option], DialogType.SYSTEM);
				} else {
					HadalGame.client.sendTCP(new Packets.Notification(state.getPlayer().getName(), options[option], DialogType.SYSTEM));
				}
			}
		}
	}
}
