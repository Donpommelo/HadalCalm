package com.mygdx.hadal.event.prefab;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.mygdx.hadal.save.Setting;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.utils.TiledObjectUtil;


/**
 * @author
 */
public class SpecialSettingSetter extends Prefabrication {

	private static final String weaponTimerId = "spawnWeapons";
	private static final String playerStartId = "playerstart";
	private static final float weaponSpawnTimer = 15.0f;
	private static final float pvpMatchZoom = 1.5f;
	private static final float defaultTime = 180.0f;

	public SpecialSettingSetter(PlayState state) {
		super(state);
	}
	
	@Override
	public void generateParts() {
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String uiTimerId = TiledObjectUtil.getPrefabTriggerId();
		String gameTimerId = TiledObjectUtil.getPrefabTriggerId();
		String gameLivesId = TiledObjectUtil.getPrefabTriggerId();
		String gameCameraId = TiledObjectUtil.getPrefabTriggerId();

		RectangleMapObject playerstart = new RectangleMapObject();
		playerstart.setName("Multitrigger");
		playerstart.getProperties().put("triggeredId", playerStartId);
		playerstart.getProperties().put("triggeringId", "bounds1,bounds2," + gameCameraId);
		
		RectangleMapObject camera1 = new RectangleMapObject();
		camera1.setName("Camera");
		camera1.getProperties().put("zoom", pvpMatchZoom);
		camera1.getProperties().put("triggeredId", gameCameraId);
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.0f);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", timerId + "," + uiTimerId + "," + gameTimerId + "," + gameLivesId + "," + weaponTimerId);

		RectangleMapObject weaponTimer = new RectangleMapObject();
		weaponTimer.setName("Timer");
		weaponTimer.getProperties().put("interval", weaponSpawnTimer);
		weaponTimer.getProperties().put("triggeringId", weaponTimerId);
		
		int startTimer = state.getGsm().getSetting().getPVPTimer();

		RectangleMapObject game = new RectangleMapObject();
		game.setName("Game");
		game.getProperties().put("sync", "ALL");
		game.getProperties().put("triggeredId", gameTimerId);

		RectangleMapObject uiTimer = new RectangleMapObject();
		uiTimer.setName("UI");

		uiTimer.getProperties().put("tags", "LEVEL,TIMER,TEAMSCORE");
		uiTimer.getProperties().put("triggeredId", uiTimerId);
		game.getProperties().put("timerIncr", -1.0f);

		if (startTimer != 0) {
			game.getProperties().put("timer", Setting.indexToTimer(startTimer));
		} else {
			game.getProperties().put("timer", defaultTime);
		}
		TiledObjectUtil.parseTiledEvent(state, uiTimer);

		state.setUnlimitedLife(true);
		
		TiledObjectUtil.parseTiledEvent(state, game);
		
		RectangleMapObject end = new RectangleMapObject();
		end.setName("End");
		end.getProperties().put("text", ResultsState.magicWord);
		end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");
		
		TiledObjectUtil.parseTiledEvent(state, playerstart);
		TiledObjectUtil.parseTiledEvent(state, camera1);
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, multi);
		TiledObjectUtil.parseTiledEvent(state, weaponTimer);
		TiledObjectUtil.parseTiledEvent(state, end);
	}
}
