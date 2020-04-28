package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.save.Setting;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.TiledObjectUtil;

/**
 * The Arena Setting Setter reads the player's settings to set up the rules of the survival arena. 
 * This includes rules about timer and sets according ui elements.
 * One of these should be placed in every arena map.
 * @author Zachary Tu
 *
 */
public class ArenaSettingSetter extends Prefabrication {

	public ArenaSettingSetter(PlayState state) {
		super(state);
	}
	
	@Override
	public void generateParts() {
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String uiTimerId = TiledObjectUtil.getPrefabTriggerId();
		String gameTimerId = TiledObjectUtil.getPrefabTriggerId();
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.0f);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", timerId + "," + uiTimerId + "," + gameTimerId);
		
		int startTimer = state.getGsm().getSetting().getTimer();
		
		RectangleMapObject game = new RectangleMapObject();
		game.setName("Game");
		game.getProperties().put("sync", "ALL");
		game.getProperties().put("triggeredId", gameTimerId);

		if (startTimer != 0) {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "Survive!,EMPTY,HISCORE,SCORE,TIMER");
			ui.getProperties().put("triggeredId", uiTimerId);
			
			game.getProperties().put("timer", Setting.indexToTimer(startTimer));
			game.getProperties().put("timerIncr", -1.0f);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
		} else {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "Survive!,EMPTY,HISCORE,SCORE,ENDLESS");
			ui.getProperties().put("triggeredId", uiTimerId);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
		}
		
		TiledObjectUtil.parseTiledEvent(state, game);
		
		RectangleMapObject end = new RectangleMapObject();
		end.setName("End");
		end.getProperties().put("text", "Victory!");
		end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");
		
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, multi);
		TiledObjectUtil.parseTiledEvent(state, end);
	}
}
