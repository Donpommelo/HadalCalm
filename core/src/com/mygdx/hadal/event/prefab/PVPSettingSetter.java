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
		String uiId = TiledObjectUtil.getPrefabTriggerId();
		String gameId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.0f);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", timerId + "," + uiId + "," + gameId);
		
		int startTimer = state.getGsm().getRecord().getTimer();
		
		if (startTimer != 0) {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "Fight!,EMPTY,TIMER");
			ui.getProperties().put("triggeredId", uiId);
			
			RectangleMapObject game = new RectangleMapObject();
			game.setName("Game");
			game.getProperties().put("sync", "ALL");
			game.getProperties().put("synced", true);
			game.getProperties().put("timer", Codex.indexToTimer(startTimer));
			game.getProperties().put("timerIncr", -1.0f);
			game.getProperties().put("triggeredId", gameId);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
			TiledObjectUtil.parseTiledEvent(state, game);
		}
		
		RectangleMapObject end = new RectangleMapObject();
		end.setName("End");
		end.getProperties().put("text", "Match Over");
		end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");
		
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, multi);
		TiledObjectUtil.parseTiledEvent(state, end);
	}
}
