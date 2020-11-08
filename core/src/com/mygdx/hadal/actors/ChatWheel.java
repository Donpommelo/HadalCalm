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
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;
import com.payne.games.piemenu.AnimatedPieMenu;
import com.payne.games.piemenu.PieMenu;

/**
 * This actor represents the chat wheel that can be used to send emotes on the fly.
 * This wheel is displayed in the center of the screen and can be controlled with the mouse.
 * @author Glacieweitz Glottwrassler
 *
 */
public class ChatWheel {

	private final PlayState state;
	private final Stage stage;
	
	private final TextureRegion wheelBase, wheelIndicator;
	private AnimatedPieMenu wheel;
	
	private static final int wheelX = 540;
	private static final int wheelY = 260;
	private static final float wheelWidth = 250.0f;
	private static final float wheelHeight = 250.0f;
	
	private static final float indicatorWidth = 40.0f;
	private static final float indicatorHeight = 40.0f;
	
	private static final float indicatorAmplification = 1.25f;
	private static final float textScaleUnselected = 0.25f;
	private static final float wheelThreshold = 0.25f;
	private static final float borderThickness = 5.0f;

	private static final String[] options = {"<RAGE>", "<NO>", "<YES>", "<LOVE>", "<SLEEP>", "READY", "<roll>", "<SWEAT>"};
	
	//is the chat wheel currently active or not?
	private boolean active;
	
	public ChatWheel(PlayState state, Stage stage) {
		this.state = state;
		this.stage = stage;
		
		this.wheelBase = Sprite.UI_MO_BASE.getFrame();
		this.wheelIndicator = Sprite.NOTIFICATIONS_CLEAR_CIRCLE.getFrame();
		
		addTable();
	}
	
	//we track the location of the mouse so that the wheel can track which direction the player has moved it in.
	private final Vector2 lastMousePosition = new Vector2();
	private final Vector2 pointerPosition = new Vector2();
	private final Vector2 totalDisplace = new Vector2();
	private final Vector2 lastDisplace = new Vector2();
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
				return mouseMoved(event);
			 }

			 @Override
			 public void touchDragged (InputEvent event, float x, float y, int pointer) {
				 mouseMoved(event);
			 }

			 private boolean mouseMoved(InputEvent event) {
				 if (event.getListenerActor() != wheel) { return false; }

				 lastDisplace.set(Gdx.input.getX(), -Gdx.input.getY()).sub(lastMousePosition).scl(indicatorAmplification);
				 totalDisplace.add(lastDisplace).limit(wheelWidth / 2);

				 pointerPosition.set(wheel.getX() + wheel.getWidth() / 2, wheel.getY() + wheel.getHeight() / 2).add(totalDisplace);
				 lastMousePosition.set(Gdx.input.getX(), -Gdx.input.getY());

				 wheel.hoverSliceAtStage(pointerPosition.x, pointerPosition.y);
				 return true;
			 }
		});
		
		//add all options to the wheel
		for (final String s : options) {
			Text option = new Text(s, 0, 0, false);
			option.setScale(textScaleUnselected);
			wheel.addActor(option);
		}
		
		wheel.setVisible(false);
		stage.addActor(wheel);
	}
	
	
	public void setVisibility(boolean visible) {
		
		if (visible) {
			if (!active) {
				//play the wheel fan animation and make no options highlighted
				wheel.animateOpening(0.4f);
				wheel.setHoveredIndex(-1);
				
				//keep track of the players mouse location so we know how they move relative to this vector
				lastMousePosition.set(Gdx.input.getX(), -Gdx.input.getY());
				
				//the pointer default position is the center of the wheel.
				pointerPosition.set(wheel.getX() + wheel.getWidth() / 2, wheel.getY() + wheel.getHeight() / 2);
				
				//reset all vectors that keep track of displacement so old movement does not carry over.
				lastDisplace.set(0, 0);
				totalDisplace.set(0, 0);
			}
		} else {
			//do the chat if wheel is active
			if (active) {
				int option = wheel.getHoveredIndex();
				
				if (option != -1 && option < options.length) {
					if (state.isServer()) {
						emote(state.getPlayer(), option);
					} else {
						HadalGame.client.sendTCP(new Packets.SyncEmote(option));
					}
				}
				wheel.animateClosing(0.4f);
			}
		}
		active = visible;
	}

	private static final float emoteCd = 2.0f;
	public void emote(Player player, int emoteIndex) {

		if (player.getEmoteCdCount() < 0) {
			player.setEmoteCdCount(emoteCd);

			HadalGame.server.addNotificationToAll(state, player.getName(), options[emoteIndex], DialogType.SYSTEM, 0);
			WeaponUtils.emote(state, player, indexToEmote(emoteIndex));
		}
	}

	private Sprite indexToEmote(int index) {
		Sprite emote = Sprite.EMOTE_YES;
		switch (index) {
			case 0:
				emote = Sprite.EMOTE_RAGE;
				break;
			case 1:
				emote = Sprite.EMOTE_NO;
				break;
			case 3:
				emote = Sprite.EMOTE_LOVE;
				break;
			case 4:
				emote = Sprite.EMOTE_SLEEP;
				break;
			case 7:
				emote = Sprite.EMOTE_SWEAT;
				break;
		}
		return emote;
	}
}
