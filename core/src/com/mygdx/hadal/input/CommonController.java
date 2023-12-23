package com.mygdx.hadal.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * The Common Controller is used for inputs that can be carried out without a player entity present.
 * This includes chatting, pausing, checking score, exiting menu, advancing dialog
 *
 * @author Courdough Clegnatio
 */
public class CommonController extends InputAdapter {

	private final PlayState state;

	public CommonController(PlayState state) { this.state = state; }

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == PlayerAction.DIALOGUE.getKey()) {
			keyDown(PlayerAction.DIALOGUE);
		} else if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			keyDown(PlayerAction.SCORE_WINDOW);
		} else if (keycode == PlayerAction.CHAT_WHEEL.getKey()) {
			keyDown(PlayerAction.CHAT_WHEEL);
		} else if (keycode == PlayerAction.EXIT_MENU.getKey()) {
			keyDown(PlayerAction.EXIT_MENU);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == PlayerAction.PAUSE.getKey()) {
			keyUp(PlayerAction.PAUSE);
		} else if (keycode == PlayerAction.MESSAGE_WINDOW.getKey()) {
			keyUp(PlayerAction.MESSAGE_WINDOW);
		} else if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			keyUp(PlayerAction.SCORE_WINDOW);
		} else if (keycode == PlayerAction.CHAT_WHEEL.getKey()) {
			keyUp(PlayerAction.CHAT_WHEEL);
		} else if (keycode == PlayerAction.ACTIVE_ITEM.getKey()) {
			if (state.isSpectatorMode()) {
				state.getUiSpectator().toggleSpectatorUI();
			}
		} else if (keycode == Input.Buttons.RIGHT) {
			if (state.getKillFeed() != null) {
				if (state.isSpectatorMode() || state.getKillFeed().isRespawnSpectator()) {
					state.getUiSpectator().findValidSpectatorTarget();
				}
			}
		}
		return false;
	}

	/**
	 * onReset is true if this is being called by the controller being synced (from finishing a transition/opening window)
	 * when this happens, we don't want to trigger jumping/shooting, just reset our button-held statuses
	 */
	public void keyUp(PlayerAction action) {

		if (action == PlayerAction.PAUSE) {
			if (state.isServer()) {
				if (GameStateManager.currentMode == GameStateManager.Mode.SINGLE) {

					//in single player, pausing pauses the game normally
					state.getGsm().addPauseState(state, state.getGsm().getLoadout().getName(), PlayState.class, true);
				} else {

					//in multiplayer, pausing depends on setting
					state.getGsm().addPauseState(state, state.getGsm().getLoadout().getName(), PlayState.class, state.getGsm().getSetting().isMultiplayerPause());
				}
			} else {

				//clients bring up their own menu if pause is disabled and messages server otherwise
				if (state.getGsm().getHostSetting().isMultiplayerPause()) {
					HadalGame.client.sendTCP(new Packets.Paused(state.getGsm().getLoadout().getName()));
				} else {
					state.getGsm().addPauseState(state, "", ClientState.class, false);
				}
			}
		} else if (action == PlayerAction.MESSAGE_WINDOW) {
			state.getMessageWindow().toggleWindow();
		} else if (action == PlayerAction.SCORE_WINDOW) {
			state.getScoreWindow().setVisibility(false);
		} else if (action == PlayerAction.CHAT_WHEEL) {
			state.getChatWheel().setVisibility(false);
		}
	}

	public void keyDown(PlayerAction action) {

		if (action == PlayerAction.DIALOGUE) {
			if (state.getDialogBox() != null) {
				state.getDialogBox().nextDialogue();
			}
		} else if (action == PlayerAction.SCORE_WINDOW) {
			state.getScoreWindow().setVisibility(true);
		} else if (action == PlayerAction.CHAT_WHEEL) {
			state.getChatWheel().setVisibility(true);
		} else if (action == PlayerAction.EXIT_MENU) {
			if (state.getUiHub().isActive()) {
				state.getUiHub().leave();
			}
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		keyDown(button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		keyUp(button);
		return false;
	}
}
