package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.event.modes.SpawnerWave;
import com.mygdx.hadal.utils.Constants;

/**
 * A wave contains the info needed to spawn a single wave of arena enemies
 * Notable spawn numbers: 4,5 are grounded. 6 is in the center of the map.
 * @author Gringo Gnashmael
 */
public enum WaveType {

	WAVE1(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 5, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 15, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE, 30, waveLimit, 1, 2, 3));
		}
	},
	
	WAVE2(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 2, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 5, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 10, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 15, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 20, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 25, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 30, waveLimit, 1, 2, 3));
		}
	},
	
	WAVE3(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER, 5, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER, 15, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER, 30, waveLimit, 1, 2, 3));
		}
	},
	
	WAVE4(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 4, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 12, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2, 24, waveLimit, 1, 2, 3));
		}
	},
	
	WAVE5(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 1, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.TURRET_VOLLEY, 1, 4, 4, 5));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 5, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 10, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 10, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 15, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 15, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.TURRET_VOLLEY, 20, waveLimit, 4));
			this.enemies.add(new WaveEnemy(EnemyType.TURRET_VOLLEY, 20, waveLimit, 5));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 25, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 25, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 30, waveLimit, 1, 2, 3));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH, 30, waveLimit, 1, 2, 3));
		}
	},
	WAVE6(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 1, waveLimit, 1));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 1, waveLimit, 2));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 1, waveLimit, 3));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 1, waveLimit, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 2, waveLimit, 4));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 3, waveLimit, 5));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 4, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 5, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 10, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 15, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 20, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 25, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1, 30, waveLimit, 1, 2, 3, 4, 5, 6));
		}
	},
	WAVE7(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.DRONE, 1, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.DRONE, 5, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.DRONE, 15, waveLimit, 1, 2, 3, 4, 5, 6));
			this.enemies.add(new WaveEnemy(EnemyType.DRONE, 30, waveLimit, 1, 2, 3, 4, 5, 6));
		}
	}
	;
	
	//this is the list of enemies in the wave
	protected final Array<WaveEnemy> enemies = new Array<>();
	
	//tags if we want a wave to only spawn at certain spawn points
	protected final Array<WaveTag> tags = new Array<>();
	
	WaveType(WaveTag... tags) {
		this.tags.addAll(tags);
	}
	
	/**
	 * This spawns a single wave
	 */
	private static final float extraInterval = 0.4f;
	public void spawnWave(SpawnerWave spawner, int waveNum, int extraField) {

		//this makes each enemy spawn staggered
		float extraDelay = 0.0f;
		for (WaveEnemy enemy : enemies) {
			enemy.createEnemy(spawner, waveNum, extraField, extraDelay);
			extraDelay += extraInterval;
		}
	}
	
	//the last wave number.
	private static int lastWave;
	
	//the current wave spawned
	private static WaveType currentWave = WaveType.WAVE1;
	
	//the max wave (no one will probably reach this wave number)
	private static final int waveLimit = 100;
	
	/**
	 * This is activated when a wave spawner activates.
	 * This returns the type of wave to spawn
	 */
	public static WaveType getWave(Array<WaveTag> tags, int waveNum) {
		
		//b/c many wave spawners activate at once, this checks to make sure we only roll a wave type once.
		//the first spawner chooses a wave at random, and the others spawn from the same wave.
		if (lastWave != waveNum) {
			lastWave = waveNum;
			Array<WaveType> waves = new Array<>();
			
			//find a wave that complies with tag restrictions
			for (WaveType wave: WaveType.values()) {
				
				boolean get = false;
				
				for (WaveTag tag: tags) {
					if (wave.tags.contains(tag, false)) {
						get = true;
						break;
					}
				}
				if (get) {
					waves.add(wave);
				}
			}

			if (!waves.isEmpty()) {
				currentWave = waves.get(MathUtils.random(waves.size - 1));
			}
		}
		return currentWave;
	}
	
	//this is the delay before the enemies in a wave spawn
	private static final float waveDelay = 1.0f;
	/**
	 * A WaveEnemy represents a single enemy in a wave.
	 */
	public static class WaveEnemy {
		
		//this is the id of the last wave
		private int lastWave;
		
		//this is a list of wave spawn points that this enemy can be spawned at
		private final int[] pointId;
		
		//this is the id of the wave spawn point that this enemy will be spawned at
		private int thisId;
		
		//this min/max wave that this can be spawned at
		private final int minWave, maxWave;
		
		//this is the type of enemy represented by this wave enemy
		private final EnemyType type;
		
		public WaveEnemy(EnemyType type, int minWave, int maxWave, int... pointId) {
			this.minWave = minWave;
			this.maxWave = maxWave;
			this.pointId = pointId;
			this.type = type;
		}
		
		/**
		 * This creates the desired enemy.
		 * similar to waves, this is run for each spawner.
		 * The first time this is run each wave, choose a random spawner to use.
		 * Then, for each spawner, generate the enemy if it is the right one.
		 * extra delay is used for staggered spawns
		 */
		public void createEnemy(SpawnerWave spawner, int waveNum, float extraField, float extraDelay) {
			
			//obey the eave limits of this wave enemy
			if (waveNum < minWave || (waveNum > maxWave && maxWave != waveLimit)) {
				return;
			}
			
			//this if makes sure that this only run once per enemy. Sets the spawn point
			if (lastWave != waveNum) {
				lastWave = waveNum;
				
				if (pointId.length == 0) {
					thisId = 1;
				} else {
					thisId = pointId[MathUtils.random(pointId.length - 1)];
				}
			}

			//create the enemy
			if (thisId == spawner.getPointId()) {
				type.generateEnemyDelayed(spawner.getState(), spawner.getPixelPosition(), waveDelay + extraDelay,
						Constants.ENEMY_HITBOX, extraField, null, false, "");
			}
		}
	}
	
	public enum WaveTag {
		STANDARD,
	}
}
