package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.event.modes.SpawnerWave;

/**
 * A wave contains the info needed to spawn a single wave of arena enemies
 * Notable spawn numbers: 4,5 are grounded. 6 is in the center of the map.
 * @author Gringo Gnashmael
 */
public enum WaveType {

	WAVE1(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE).setExclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE).setExclusiveIndex(1).setMinWave(5));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE).setExclusiveIndex(1).setMinWave(15));
			this.enemies.add(new WaveEnemy(EnemyType.SPLITTER_LARGE).setExclusiveIndex(1).setMinWave(25));
		}
	},
	
	WAVE2(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setExclusiveIndex(1).setInclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setExclusiveIndex(1).setInclusiveIndex(2));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(1).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(2).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(1).setMinWave(2).setDelay(2 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(2).setMinWave(5).setDelay(2 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(1).setMinWave(10).setDelay(3 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(2).setMinWave(15).setDelay(3 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(1).setMinWave(20).setDelay(4 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(2).setMinWave(25).setDelay(4 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setInclusiveIndex(1).setMinWave(30).setDelay(5 * extraInterval));
		}
	},
	
	WAVE3(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER).setExclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER).setExclusiveIndex(1).setMinWave(5));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER).setExclusiveIndex(1).setMinWave(15));
			this.enemies.add(new WaveEnemy(EnemyType.SPAWNER).setExclusiveIndex(1).setMinWave(30));
		}
	},
	
	WAVE4(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2).setExclusiveIndex(1).setInclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2).setExclusiveIndex(1).setInclusiveIndex(2));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2).setInclusiveIndex(1).setMinWave(4).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2).setInclusiveIndex(2).setMinWave(12).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2).setInclusiveIndex(1).setMinWave(20).setDelay(2 * extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.SWIMMER2).setInclusiveIndex(2).setMinWave(28).setDelay(2 * extraInterval));
		}
	},
	
	WAVE5(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.TURRET_VOLLEY).setTag(WaveTag.GROUNDED).setExclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.TURRET_VOLLEY).setTag(WaveTag.GROUNDED).setExclusiveIndex(1).setMinWave(10));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setDelay(shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setDelay(2 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(5).setDelay(3 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(10).setDelay(4 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(10).setDelay(5 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(15).setDelay(6 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(15).setDelay(7 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(25).setDelay(8 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(25).setDelay(9 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(35).setDelay(10 * shortInterval));
			this.enemies.add(new WaveEnemy(EnemyType.MISCFISH).setMinWave(35).setDelay(11 * shortInterval));
		}
	},
	WAVE6(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setExclusiveIndex(1).setInclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setExclusiveIndex(1).setInclusiveIndex(2));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setExclusiveIndex(1).setInclusiveIndex(3));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setExclusiveIndex(1).setInclusiveIndex(4));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setExclusiveIndex(1).setInclusiveIndex(5).setMinWave(2));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setExclusiveIndex(1).setInclusiveIndex(6).setMinWave(3));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setInclusiveIndex(1).setMinWave(4).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setInclusiveIndex(2).setMinWave(6).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setInclusiveIndex(3).setMinWave(12).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setInclusiveIndex(4).setMinWave(16).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setInclusiveIndex(5).setMinWave(20).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setInclusiveIndex(6).setMinWave(25).setDelay(extraInterval));
			this.enemies.add(new WaveEnemy(EnemyType.LEAPER1).setMinWave(30));
		}
	},
	WAVE7(WaveTag.STANDARD) {
		{
			this.enemies.add(new WaveEnemy(EnemyType.DRONE).setExclusiveIndex(1));
			this.enemies.add(new WaveEnemy(EnemyType.DRONE).setExclusiveIndex(1).setMinWave(5));
			this.enemies.add(new WaveEnemy(EnemyType.DRONE).setExclusiveIndex(1).setMinWave(15));
			this.enemies.add(new WaveEnemy(EnemyType.DRONE).setExclusiveIndex(1).setMinWave(30));
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

	public Array<WaveTag> getTags() { return tags; }

	public Array<WaveEnemy> getEnemies() { return enemies; }

	//the max wave (no one will probably reach this wave number)
	public static final int waveLimit = 100;
	
	//this is the delay before the enemies in a wave spawn
	private static final float waveDelay = 1.0f;
	private static final float extraInterval = 0.4f;
	private static final float shortInterval = 0.2f;

	/**
	 * A WaveEnemy represents a single enemy in a wave.
	 */
	public static class WaveEnemy {

		//this is the type of enemy represented by this wave enemy
		private final EnemyType type;
		private float delay;
		private float currentDelay;

		private WaveTag tag = WaveTag.STANDARD;
		
		//this min/max wave that this can be spawned at
		private int minWave = 1;
		private int maxWave = waveLimit;

		//
		private int exclusiveIndex = 0;
		private int inclusiveIndex = 0;

		private SpawnerWave spawner;
		
		public WaveEnemy(EnemyType type) {
			this.type = type;
		}

		public WaveEnemy setTag(WaveTag tag) {
			this.tag = tag;
			return this;
		}

		public WaveEnemy setMinWave(int minWave) {
			this.minWave = minWave;
			return this;
		}

		public WaveEnemy setMaxWave(int maxWave) {
			this.maxWave = maxWave;
			return this;
		}

		public WaveEnemy setExclusiveIndex(int exclusiveIndex) {
			this.exclusiveIndex = exclusiveIndex;
			return this;
		}

		public WaveEnemy setInclusiveIndex(int inclusiveIndex) {
			this.inclusiveIndex = inclusiveIndex;
			return this;
		}

		public WaveEnemy setDelay(float delay) {
			this.delay = delay;
			return this;
		}

		public WaveTag getTag() { return tag; }

		public int getMinWave() { return minWave; }

		public int getMaxWave() { return maxWave; }

		public int getExclusiveIndex() { return exclusiveIndex; }

		public int getInclusiveIndex() { return inclusiveIndex; }

		public void setCurrentDelay(float currentDelay) { this.currentDelay = currentDelay; }

		public float getCurrentDelay() { return currentDelay; }

		public void resetDelay() { this.currentDelay = delay; }

		public void setSpawner(SpawnerWave spawner) { this.spawner = spawner; }

		public SpawnerWave getSpawner() { return spawner; }

		/**
		 * This creates the desired enemy.
		 * similar to waves, this is run for each spawner.
		 * The first time this is run each wave, choose a random spawner to use.
		 * Then, for each spawner, generate the enemy if it is the right one.
		 * extra delay is used for staggered spawns
		 */
		public void createEnemy() {

			//create the enemy
			type.generateEnemyDelayed(spawner.getState(), spawner.getPixelPosition(), waveDelay,
					BodyConstants.ENEMY_HITBOX, spawner.getExtraField(), null, false, "");
		}
	}
	
	public enum WaveTag {
		STANDARD,
		GROUNDED
	}
}
