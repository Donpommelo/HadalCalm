package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.SpawnerWave;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.utils.Constants;

public enum WaveType {

	WAVE1(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 1, 10, 1 , 2));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 4, 10, 1, 2));
		}
	},
	
	WAVE2(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 2, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 3, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 4, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 5, 10, 1, 2));

		}
	},
	
	WAVE3(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER, 1, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER, 4, 10, 1, 2));
		}
	},
	
	WAVE4(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 1, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 1, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 4, 10, 1, 2));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 4, 10, 1, 2));
		}
	}
	
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
	
	private static int lastWave;
	private static WaveType currentWave = WaveType.WAVE1;
	public static WaveType getWave(ArrayList<WaveTag> tags, int waveNum) {
		
		if (lastWave != waveNum) {
			lastWave = waveNum;
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

			if (!waves.isEmpty()) {
				currentWave = waves.get(GameStateManager.generator.nextInt(waves.size));
			}
		}
		
		return currentWave;
	}
	
	public class WaveEnemy {
		
		private int lastWave;
		private int[] pointId;
		private int thisId;
		private int minWave, maxWave;
		private EnemyType type;
		
		public WaveEnemy(EnemyType type, int minWave, int maxWave, int... pointId) {
			this.minWave = minWave;
			this.maxWave = maxWave;
			this.pointId = pointId;
			this.type = type;
		}
		
		public void createEnemy(SpawnerWave spawner, int waveNum, float extraField) {
			
			if (waveNum < minWave || waveNum > maxWave) {
				return;
			}
			
			if (lastWave != waveNum) {
				lastWave = waveNum;
				
				if (pointId.length == 0) {
					thisId = 1;
				} else {
					thisId = GameStateManager.generator.nextInt(pointId.length) + 1;
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
