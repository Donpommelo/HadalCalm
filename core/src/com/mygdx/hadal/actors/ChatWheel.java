package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.ConsoleCommandUtil;
import com.payne.games.piemenu.AnimatedPieMenu;
import com.payne.games.piemenu.PieMenu;

/**
 * This actor represents the chat wheel that can be used to send emotes on the fly.
 * This wheel is displayed in the center of the screen and can be controlled with the mouse.
 * @author Glacieweitz Glottwrassler
 */
public class ChatWheel {

	private final PlayState state;

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

	//this is a list of all the emote options
	private static final String[] options = {"RAGE", "NO", "YES", "LOVE", "SLEEP", "READY", "/roll", "SWEAT"};
	
	//is the chat wheel currently active or not?
	private boolean active;
	
	public ChatWheel(PlayState state) {
		this.state = state;
		this.wheelBase = Sprite.UI_MO_BASE.getFrame();
		this.wheelIndicator = Sprite.NOTIFICATIONS_CLEAR_CIRCLE.getFrame();
	}
	
	//we track the location of the mouse so that the wheel can track which direction the player has moved it in.
	private final Vector2 lastMousePosition = new Vector2();
	private final Vector2 pointerPosition = new Vector2();
	private final Vector2 totalDisplace = new Vector2();
	private final Vector2 lastDisplace = new Vector2();

	//players cannot use emotes when they are on cooldown
	private static final float emoteCd = 1.5f;
	private float emoteCount = emoteCd;

	/**
	 * This creates the chat wheel elements
	 */
	public void addTable(Stage stage) {
		PieMenu.PieMenuStyle style = new PieMenu.PieMenuStyle();
		style.sliceColor = new Color(1,1,1,0.5f);
		style.hoverColor = new Color(.7f,.3f,.5f,1);
		style.circumferenceWidth = borderThickness;
		style.separatorWidth = borderThickness;
				
		wheel = new AnimatedPieMenu(wheelBase, style, wheelWidth / 2) {
			@Override
			public void act(float delta) {

				//an active chat wheel tracks player mouse movement
				if (active) {
					lastDisplace.set(Gdx.input.getX(), -Gdx.input.getY()).sub(lastMousePosition).scl(indicatorAmplification);
					totalDisplace.add(lastDisplace).limit(wheelWidth / 2);

					//the chat wheel pointer moves based on the total mouse displacement while active. (limited by wheel radius)
					pointerPosition.set(wheel.getX() + wheel.getWidth() / 2, wheel.getY() + wheel.getHeight() / 2).add(totalDisplace);
					lastMousePosition.set(Gdx.input.getX(), -Gdx.input.getY());

					wheel.hoverSliceAtStage(pointerPosition.x, pointerPosition.y);
				}

				//decrement emote cooldown
				if (emoteCount > 0.0f) {
					emoteCount -= delta;
				}

				super.act(delta);
			}

			@Override
			public void draw(Batch batch, float alpha) {
				if (state.getGsm().getSetting().isHideHUD()) { return; }
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

		//add all options to the wheel
		for (int i = 0; i < options.length; i ++) {
			Backdrop option = new Backdrop(indexToEmote(i), 50, 50, getFrameIndex(i)).setMirror();
			option.setScale(textScaleUnselected);
			wheel.addActor(option);
		}
		
		wheel.setVisible(false);
		stage.addActor(wheel);
	}

	/**
	 * This is called when the chat wheel button is pressed or released.
	 * @param visible: do we make the wheel visible or invisible (button being pressed or released)?
	 */
	public void setVisibility(boolean visible) {

		if (visible) {
			if (!active) {
				//play the wheel fan animation and make no options highlighted
				wheel.animateOpening(0.4f);
				wheel.setHoveredIndex(PieMenu.NO_SELECTION);
				
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

					//if emote is off cooldown, execute the emote
					if (emoteCount <= 0.0f) {
						emoteCount = emoteCd;
						//server processes the emote. clients send packet to server
						if (state.isServer()) {
							emote(state.getPlayer(), option);
						} else {
							HadalGame.client.sendTCP(new Packets.SyncEmote(option));
						}
					}
				}
				wheel.animateClosing(0.4f);
			}
		}
		active = visible;
	}

	/**
	 * This method processes a single emote use.
	 * @param player: the player doing the emote
	 * @param emoteIndex: the index of the list of emotes
	 */
	public void emote(Player player, int emoteIndex) {
		//special logic for the emote that does a chat command (/roll)
		if (emoteIndex == 6) {
			ConsoleCommandUtil.parseChatCommand(state, player, options[emoteIndex]);
		} else {
			HadalGame.server.addChatToAll(state, options[emoteIndex], DialogType.SYSTEM, player.getConnId());
		}
		if (player.getPlayerData() != null) {
			SyncedAttack.EMOTE.initiateSyncedAttackSingle(state, player, new Vector2(), new Vector2(), emoteIndex);
		}
	}

	/**
	 *
	 * @param index: index in list of emote
	 * @return the sprite of the emote
	 */
	public static Sprite indexToEmote(int index) {
		return switch (index) {
			case 0 -> Sprite.EMOTE_RAGE;
			case 1 -> Sprite.EMOTE_NO;
			case 3 -> Sprite.EMOTE_LOVE;
			case 4 -> Sprite.EMOTE_SLEEP;
			case 5 -> Sprite.EMOTE_READY;
			case 6 -> Sprite.EMOTE_DICE;
			case 7 -> Sprite.EMOTE_SWEAT;
			default -> Sprite.EMOTE_YES;
		};
	}

	/**
	 * @param index: index in list of emote
	 * @return the frame of the emote's sprite that should represent it in the wheel.
	 */
	private int getFrameIndex(int index) {
		return switch (index) {
			case 1, 2, 4 -> 20;
			case 5 -> 1;
			case 0, 3 -> 10;
			case 7 -> 15;
			default -> 0;
		};
	}
}
