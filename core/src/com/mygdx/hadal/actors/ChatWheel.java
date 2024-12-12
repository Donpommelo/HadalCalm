package com.mygdx.hadal.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.schmucks.entities.Player;
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

	private static final int WHEEL_X = 540;
	private static final int WHEEL_Y = 260;
	private static final float WHEEL_WIDTH = 250.0f;
	private static final float WHEEL_HEIGHT = 250.0f;
	private static final float WHEEL_SPEED_OPEN = 0.3f;
	private static final float WHEEL_SPEED_CLOSE = 0.4f;

	private static final float INDICATOR_WIDTH = 40.0f;
	private static final float INDICATOR_HEIGHT = 40.0f;

	private static final float INDICATOR_AMPLIFICATION = 1.25f;
	private static final float TEXT_SCALE_UNSELECTED = 0.25f;
	private static final float WHEEL_THRESHOLD = 0.25f;
	private static final float BORDER_THICKNESS = 5.0f;

	//this is a list of all the emote options
	private static final String[] OPTIONS = {"RAGE", "NO", "YES", "LOVE", "SLEEP", "READY", "/roll", "SWEAT"};

	private final PlayState state;

	private final TextureRegion wheelBase, wheelIndicator;
	private AnimatedPieMenu wheel;

	//is the chat wheel currently active or not?
	private boolean active;
	
	public ChatWheel(PlayState state) {
		this.state = state;
		this.wheelBase = SpriteManager.getFrame(Sprite.UI_MO_BASE);
		this.wheelIndicator = SpriteManager.getFrame(Sprite.NOTIFICATIONS_CLEAR_CIRCLE);
	}
	
	//we track the location of the mouse so that the wheel can track which direction the player has moved it in.
	private final Vector2 lastMousePosition = new Vector2();
	private final Vector2 pointerPosition = new Vector2();
	private final Vector2 totalDisplace = new Vector2();
	private final Vector2 lastDisplace = new Vector2();

	//players cannot use emotes when they are on cooldown
	private static final float EMOTE_CD = 1.5f;
	private float emoteCount = EMOTE_CD;
	/**
	 * This creates the chat wheel elements
	 */
	public void addTable(Stage stage) {
		PieMenu.PieMenuStyle style = new PieMenu.PieMenuStyle();
		style.sliceColor = new Color(1,1,1,0.5f);
		style.hoverColor = new Color(0.7f,0.3f,0.5f,1);
		style.circumferenceWidth = BORDER_THICKNESS;
		style.separatorWidth = BORDER_THICKNESS;
				
		wheel = new AnimatedPieMenu(wheelBase, style, WHEEL_WIDTH / 2) {
			@Override
			public void act(float delta) {

				//an active chat wheel tracks player mouse movement
				if (active) {
					lastDisplace.set(Gdx.input.getX(), -Gdx.input.getY()).sub(lastMousePosition).scl(INDICATOR_AMPLIFICATION);
					totalDisplace.add(lastDisplace).limit(WHEEL_WIDTH / 2);

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
				if (JSONManager.setting.isHideHUD()) { return; }
				super.draw(batch, alpha);
				batch.draw(wheelIndicator, pointerPosition.x - INDICATOR_WIDTH / 2, pointerPosition.y - INDICATOR_HEIGHT / 2,
						INDICATOR_WIDTH, INDICATOR_HEIGHT);
			}
		};
		wheel.setX(WHEEL_X);
		wheel.setY(WHEEL_Y);
		wheel.setWidth(WHEEL_WIDTH);
		wheel.setHeight(WHEEL_HEIGHT);
		wheel.setInfiniteSelectionRange(true);
		wheel.setMiddleCancel(true);
		wheel.setInnerRadiusPercent(WHEEL_THRESHOLD);

		//add all options to the wheel
		for (int i = 0; i < OPTIONS.length; i ++) {
			Backdrop option = new Backdrop(indexToEmote(i), 50, 50, getFrameIndex(i)).setMirror();
			option.setScale(TEXT_SCALE_UNSELECTED);
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
				wheel.animateOpening(WHEEL_SPEED_OPEN);
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

				if (option != -1 && option < OPTIONS.length) {

					//if emote is off cooldown, execute the emote
					if (emoteCount <= 0.0f) {
						emoteCount = EMOTE_CD;
						//server processes the emote. clients send packet to server
						if (state.isServer()) {
							emote(HadalGame.usm.getOwnPlayer(), option, HadalGame.usm.getConnID());
						} else {
							PacketManager.clientTCP(new Packets.SyncEmote(option));
						}
					}
				}
				wheel.animateClosing(WHEEL_SPEED_CLOSE);
			}
		}
		active = visible;
	}

	/**
	 * This method processes a single emote use.
	 * @param player: the player doing the emote
	 * @param emoteIndex: the index of the list of emotes
	 */
	public void emote(Player player, int emoteIndex, int connID) {
		//special logic for the emote that does a chat command (/roll)
		if (emoteIndex == 6) {
			ConsoleCommandUtil.parseChatCommand(state, player, OPTIONS[emoteIndex], connID);
		} else {
			HadalGame.server.addChatToAll(state, OPTIONS[emoteIndex], DialogType.SYSTEM, connID);
		}
		if (player != null) {
			if (player.getPlayerData() != null) {
				SyncedAttack.EMOTE.initiateSyncedAttackSingle(state, player, new Vector2(), new Vector2(), emoteIndex);
			}
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
