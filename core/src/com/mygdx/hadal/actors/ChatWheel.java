package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.payne.games.piemenu.AnimatedPieMenu;
import com.payne.games.piemenu.PieMenu;

@SuppressWarnings("deprecation")
public class ChatWheel {

	private PlayState state;
	private Stage stage;
	
	private TextureRegion wheelBase, wheelIndicator;
	private AnimatedPieMenu wheel;
	
	private final static int wheelX = 540;
	private final static int wheelY = 260;
	private final static int wheelWidth = 250;
	private final static int wheelHeight = 250;
	
	private final static int indicatorWidth = 40;
	private final static int indicatorHeight = 40;
	
	private final static float indicatorAmplification = 1.25f;
	private final static float textScaleUnselected = 0.25f;
	private final static float wheelThreshold = 0.25f;
	private final static float borderThickness = 5.0f;

	private final static String[] options = {"temp1", "temp2", "temp3", "temp4", "temp5", "temp6", "READY", "<roll>"};
	
	public ChatWheel(PlayState state, Stage stage) {
		this.state = state;
		this.stage = stage;
		
		this.wheelBase = Sprite.UI_MO_BASE.getFrame();
		this.wheelIndicator = Sprite.UI_MO_READY.getFrame();
		
		addTable();
	}
	
	private Vector2 lastMousePosition = new Vector2();
	private Vector2 pointerPosition = new Vector2();
	private Vector2 totalDisplace = new Vector2();
	private Vector2 lastDisplace = new Vector2();
	public void addTable() {
		PieMenu.PieMenuStyle style = new PieMenu.PieMenuStyle();
		style.sliceColor = new Color(1,1,1,0.5f);
		style.hoverColor = new Color(.7f,.3f,.5f,1);
		style.circumferenceWidth = borderThickness;
		style.separatorWidth = borderThickness;
				
		wheel = new AnimatedPieMenu(wheelBase, style, wheelWidth / 2) {
			
			@Override
			public void draw(Batch batch, float alpha) {
				super.draw(batch, alpha);
				batch.draw(wheelIndicator, pointerPosition.x - indicatorWidth / 2, pointerPosition.y - indicatorHeight / 2, indicatorWidth, indicatorHeight);
			}
		};
		wheel.setX(wheelX);
		wheel.setY(wheelY);
		wheel.setWidth(wheelWidth);
		wheel.setHeight(wheelHeight);
		wheel.setInfiniteSelectionRange(true);
		wheel.setMiddleCancel(true);
		wheel.setInnerRadiusPercent(wheelThreshold);

		wheel.setPieMenuListener(new ClickListener() {
			
			 @Override
	        public boolean mouseMoved(InputEvent event, float x, float y) {
				 
				 if(event.getListenerActor() != wheel) { return false; }
				 
				 lastDisplace.set(Gdx.input.getX(), -Gdx.input.getY()).sub(lastMousePosition).scl(indicatorAmplification);
				 totalDisplace.add(lastDisplace).limit(wheelWidth / 2);
				 
				 pointerPosition.set(wheel.getX() + wheel.getWidth() / 2, wheel.getY() + wheel.getHeight() / 2).add(totalDisplace);
				 lastMousePosition.set(Gdx.input.getX(), -Gdx.input.getY());

				 wheel.hoverSliceAtStage(pointerPosition.x, pointerPosition.y);
				 
				 return true;
			 }
			
		});
		
		for (int i = 0; i < options.length; i++) {
			Text option = new Text(options[i], 0, 0, false);
			option.setScale(textScaleUnselected);
			wheel.addActor(option);
		}
		
		wheel.setVisible(false);
		stage.addActor(wheel);
	}
	
	
	public void setVisibility(boolean visible) {
		
		if (visible) {
			wheel.animateOpening(0.4f);
			wheel.setHoveredIndex(-1);
			lastMousePosition.set(Gdx.input.getX(), -Gdx.input.getY());
			pointerPosition.set(wheel.getX() + wheel.getWidth() / 2, wheel.getY() + wheel.getHeight() / 2);
			lastDisplace.set(0, 0);
			totalDisplace.set(0, 0);
		} else {
			//do the chat
			int option = wheel.getHoveredIndex();
			
			if (option != -1 && option < options.length) {
				if (state.isServer()) {
					HadalGame.server.addNotificationToAll(state, state.getPlayer().getName(), options[option], DialogType.SYSTEM);
				} else {
					HadalGame.client.sendTCP(new Packets.Notification(state.getPlayer().getName(), options[option], DialogType.SYSTEM));
				}
			}
			wheel.animateClosing(0.4f);
		}
	}
}
