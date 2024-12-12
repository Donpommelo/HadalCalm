package com.mygdx.hadal.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.SettingArcade;
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
		if (keycode == PlayerAction.INTERACT.getKey()) {
			keyDown(PlayerAction.INTERACT);
		} else if (keycode == PlayerAction.DIALOGUE.getKey()) {
			keyDown(PlayerAction.DIALOGUE);
		} else if (keycode == PlayerAction.SCORE_WINDOW.getKey()) {
			keyDown(PlayerAction.SCORE_WINDOW);
		} else if (keycode == PlayerAction.CHAT_WHEEL.getKey()) {
			keyDown(PlayerAction.CHAT_WHEEL);
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
			if (state.getSpectatorManager().isSpectatorMode()) {
				state.getUIManager().getUiSpectator().toggleSpectatorUI();
			}
		} else if (keycode == Input.Buttons.RIGHT) {
			if (state.getUIManager().getKillFeed() != null) {
				if (state.getSpectatorManager().isSpectatorMode() || state.getUIManager().getKillFeed().isRespawnSpectator()) {
					state.getUIManager().getUiSpectator().findValidSpectatorTarget();
				}
			}
		} else if (keycode == PlayerAction.READY_UP.getKey()) {
			keyUp(PlayerAction.READY_UP);
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
				if (StateManager.currentMode == StateManager.Mode.SINGLE) {

					//in single player, pausing pauses the game normally
					StateManager.addPauseState(state, JSONManager.loadout.getName(), PlayState.class, true);
				} else {

					//in multiplayer, pausing depends on setting
					StateManager.addPauseState(state, JSONManager.loadout.getName(), PlayState.class, JSONManager.setting.isMultiplayerPause());
				}
			} else {

				//clients bring up their own menu if pause is disabled and messages server otherwise
				if (JSONManager.hostSetting.isMultiplayerPause()) {
					PacketManager.clientTCP(new Packets.Paused(JSONManager.loadout.getName()));
				} else {
					StateManager.addPauseState(state, "", ClientState.class, false);
				}
			}
		} else if (action == PlayerAction.MESSAGE_WINDOW) {
			state.getUIManager().getMessageWindow().toggleWindow();
		} else if (action == PlayerAction.SCORE_WINDOW) {
			state.getUIManager().getScoreWindow().setVisibility(false);
		} else if (action == PlayerAction.CHAT_WHEEL) {
			state.getUIManager().getChatWheel().setVisibility(false);
		} else if (action == PlayerAction.READY_UP) {
			if (state.getMode().equals(GameMode.ARCADE)) {
				if (state.isServer()) {
					SettingArcade.readyUp(state, 0);
				} else {
					PacketManager.clientTCP(new Packets.ClientReady());
				}
			}
		}
	}

	public void keyDown(PlayerAction action) {
		//when spectating, host interact activates the map's designated "spectator event", if existant
		if (HadalGame.usm.isHost() && state.getSpectatorManager().isSpectatorMode()) {
			if (action == PlayerAction.INTERACT) {
				if (state.getSpectatorActivation() != null) {
					state.getSpectatorActivation().getEventData().onInteract(null);
				}
			}
		}

		if (action == PlayerAction.DIALOGUE) {
			if (state.getUIManager().getDialogBox() != null) {
				state.getUIManager().getDialogBox().nextDialogue();
			}
		} else if (action == PlayerAction.SCORE_WINDOW) {
			state.getUIManager().getScoreWindow().setVisibility(true);
		} else if (action == PlayerAction.CHAT_WHEEL) {
			state.getUIManager().getChatWheel().setVisibility(true);
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
