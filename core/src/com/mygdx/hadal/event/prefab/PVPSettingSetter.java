package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.event.hub.Codex;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Limiter is a prefab that is placed between triggerer and triggered to limit the number of times the trigger can activate
 * @author Zachary Tu
 *
 */
public class PVPSettingSetter extends Prefabrication {

	public PVPSettingSetter(PlayState state) {
		super(state);
	}
	
	@Override
	public void generateParts() {
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String uiTimerId = TiledObjectUtil.getPrefabTriggerId();
		String uiLivesId = TiledObjectUtil.getPrefabTriggerId();
		String gameTimerId = TiledObjectUtil.getPrefabTriggerId();
		String gameLivesId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.0f);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", timerId + "," + uiTimerId + "," + uiLivesId + "," + gameTimerId + "," + gameLivesId);
		
		int startTimer = state.getGsm().getRecord().getTimer();
		int startLives = state.getGsm().getRecord().getLives();
		
		RectangleMapObject game = new RectangleMapObject();
		game.setName("Game");
		game.getProperties().put("sync", "ALL");
		game.getProperties().put("synced", true);
		game.getProperties().put("triggeredId", gameTimerId);

		if (startTimer != 0) {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "Fight!,EMPTY,TIMER");
			ui.getProperties().put("triggeredId", uiTimerId);
			
			game.getProperties().put("timer", Codex.indexToTimer(startTimer));
			game.getProperties().put("timerIncr", -1.0f);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
		}
		
		if (startLives != 0) {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "LIVES");
			ui.getProperties().put("triggeredId", uiLivesId);
			
			game.getProperties().put("lives", startLives - 1);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
		} else {
			state.setUnlimitedLife(true);
		}
		
		TiledObjectUtil.parseTiledEvent(state, game);
		
		RectangleMapObject end = new RectangleMapObject();
		end.setName("End");
		end.getProperties().put("text", "Match Over");
		end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");
		
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, multi);
		TiledObjectUtil.parseTiledEvent(state, end);
	}
}
