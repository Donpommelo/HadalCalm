package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.SpawnerWave;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.utils.Constants;

public enum WaveType {

	WAVE1(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 1));
		}
	},
	
	WAVE2()
	
	;
	
	
	protected ArrayList<WaveEnemy> enemies = new ArrayList<WaveEnemy>();
	protected ArrayList<WaveTag> tags = new ArrayList<WaveTag>();
	
	private WaveType(WaveTag... tags) {
		for (WaveTag tag: tags) {
			this.tags.add(tag);
		}
	}
	
	public void spawnWave(SpawnerWave spawner, int waveNum, int extraField) {
		
		for (WaveEnemy enemy : enemies) {
			enemy.createEnemy(spawner, waveNum, extraField);
		}
	}
	
	public static WaveType getWave(ArrayList<WaveTag> tags) {
		Array<WaveType> waves = new Array<WaveType>();
		
		for (WaveType wave: WaveType.values()) {
			
			boolean get = false;
			
			for (WaveTag tag: tags) {
				if (wave.tags.contains(tag)) {
					get = true;
				}
			}
			if (get) {
				waves.add(wave);
			}
		}
		
		if (waves.isEmpty()) {
			return WaveType.WAVE1;
		} else {
			return waves.get(GameStateManager.generator.nextInt(waves.size));
		}
		
	}
	
	public class WaveEnemy {
		
		private int lastWave;
		private int[] pointId;
		private int thisId;
		private EnemyType type;
		
		public WaveEnemy(EnemyType type, int... pointId) {
			this.pointId = pointId;
			this.type = type;
		}
		
		public void createEnemy(SpawnerWave spawner, int waveNum, float extraField) {
			
			if (lastWave != waveNum) {
				lastWave = waveNum;
				
				if (pointId.length == 0) {
					thisId = 0;
				} else {
					thisId = GameStateManager.generator.nextInt(pointId.length);
				}
			}
			
			if (thisId == spawner.getPointId()) {
				type.generateEnemy(spawner.getState(), spawner.getPixelPosition(), Constants.ENEMY_HITBOX, extraField, null);
			}
		}
	}
	
	public enum WaveTag {
		STANDARD,
	}
}
