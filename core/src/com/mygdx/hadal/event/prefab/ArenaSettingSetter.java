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
 */
public class ArenaSettingSetter extends Prefabrication {

	private final static String weaponTimerId = "spawnWeapons";
	private final static String playerStartId = "playerstart";
	private final static float weaponSpawnTimer = 25.0f;
	private final static float waveSpawnTimer = 15.0f;
	private final static float arenaMatchZoom = 1.5f;
	
	public ArenaSettingSetter(PlayState state) {
		super(state);
	}
	
	@Override
	public void generateParts() {
		String timerId = TiledObjectUtil.getPrefabTriggerId();
		String multiId = TiledObjectUtil.getPrefabTriggerId();
		String uiTimerId = TiledObjectUtil.getPrefabTriggerId();
		String gameTimerId = TiledObjectUtil.getPrefabTriggerId();
		String waveTimerId = TiledObjectUtil.getPrefabTriggerId();
		String multiWaveId = TiledObjectUtil.getPrefabTriggerId();
		String gameCameraId = TiledObjectUtil.getPrefabTriggerId();

		RectangleMapObject playerstart = new RectangleMapObject();
		playerstart.setName("Multitrigger");
		playerstart.getProperties().put("triggeredId", playerStartId);
		playerstart.getProperties().put("triggeringId", "bounds1,bounds2," + gameCameraId);
		
		RectangleMapObject camera1 = new RectangleMapObject();
		camera1.setName("Camera");
		camera1.getProperties().put("zoom", arenaMatchZoom);
		camera1.getProperties().put("triggeredId", gameCameraId);
		
		RectangleMapObject timer = new RectangleMapObject();
		timer.setName("Timer");
		timer.getProperties().put("interval", 0.0f);
		timer.getProperties().put("triggeredId", timerId);
		timer.getProperties().put("triggeringId", multiId);
		
		RectangleMapObject multi = new RectangleMapObject();
		multi.setName("Multitrigger");
		multi.getProperties().put("triggeredId", multiId);
		multi.getProperties().put("triggeringId", timerId + "," + uiTimerId + "," + gameTimerId + "," + multiWaveId + "," + weaponTimerId);
		
		RectangleMapObject weaponTimer = new RectangleMapObject();
		weaponTimer.setName("Timer");
		weaponTimer.getProperties().put("interval", weaponSpawnTimer);
		weaponTimer.getProperties().put("triggeringId", weaponTimerId);
		
		int startTimer = state.getGsm().getSetting().getCoopTimer();
		
		RectangleMapObject game = new RectangleMapObject();
		game.setName("Game");
		game.getProperties().put("sync", "ALL");
		game.getProperties().put("triggeredId", gameTimerId);

		if (startTimer != 0) {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "Survive!,EMPTY,LEVEL,HISCORE,SCORE,TIMER");
			ui.getProperties().put("triggeredId", uiTimerId);
			
			game.getProperties().put("timer", Setting.indexToTimer(startTimer));
			game.getProperties().put("timerIncr", -1.0f);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
		} else {
			RectangleMapObject ui = new RectangleMapObject();
			ui.setName("UI");
			ui.getProperties().put("tags", "Survive!,EMPTY,LEVEL,HISCORE,SCORE,ENDLESS");
			ui.getProperties().put("triggeredId", uiTimerId);
			
			TiledObjectUtil.parseTiledEvent(state, ui);
		}
		
		TiledObjectUtil.parseTiledEvent(state, game);
		
		RectangleMapObject end = new RectangleMapObject();
		end.setName("End");
		end.getProperties().put("text", "Victory!");
		end.getProperties().put("triggeredId", "runOnGlobalTimerConclude");
		
		RectangleMapObject wave = new RectangleMapObject();
		wave.setName("Timer");
		wave.getProperties().put("interval", waveSpawnTimer);
		wave.getProperties().put("triggeredId", waveTimerId);
		wave.getProperties().put("triggeringId", multiWaveId);
		
		RectangleMapObject multiWave = new RectangleMapObject();
		multiWave.setName("Multitrigger");
		multiWave.getProperties().put("triggeredId", multiWaveId);
		multiWave.getProperties().put("triggeringId", "wave1,wave2,wave3,wave4,wave5,wave6");
		
		TiledObjectUtil.parseTiledEvent(state, playerstart);
		TiledObjectUtil.parseTiledEvent(state, camera1);
		TiledObjectUtil.parseTiledEvent(state, timer);
		TiledObjectUtil.parseTiledEvent(state, multi);
		TiledObjectUtil.parseTiledEvent(state, weaponTimer);
		TiledObjectUtil.parseTiledEvent(state, end);
		TiledObjectUtil.parseTiledEvent(state, wave);
		TiledObjectUtil.parseTiledEvent(state, multiWave);
	}
}
