package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.DelayedSpawn;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * enemy types represent the different types of enemies and how to spawn them
 * @author Lecoddad Lorabola
 */
public enum EnemyType {

	DELAYEDSPAWN("") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKCrawler(state, startPos, extraField, filter, spawner);
		}
	},
	
	CRAWLER1("CRAWLING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKCrawler(state, startPos, extraField, filter, spawner);
		}
	},
	
	CRAWLER1BIG("GIGA CRAWLING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKCrawler2(state, startPos, extraField, filter, spawner);
		}
	},
	
	CRAWLER2("CHARGER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Crawler2(state, startPos, extraField, filter, spawner);
		}
	},
	
	CRAWLER3("SPITTING CRAWLER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Crawler3(state, startPos, extraField, filter, spawner);
		}
	},
	
	LEAPER1("LEAPER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Leaper1(state, startPos, extraField, filter, spawner);
		}
	},
	
	SWIMMER1("SWIMMING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Swimmer1(state, startPos, extraField, filter, spawner);
		}
	},
	
	SWIMMER2("FIREBREATHING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Swimmer2(state, startPos, extraField, filter, spawner);
		}
	},
	
	SCISSORFISH("SCISSORFISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Scissorfish(state, startPos, extraField, filter, spawner);
		}
	},
	
	SPITTLEFISH("SPITTLEFISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Spittlefish(state, startPos, extraField, filter, spawner);
		}
	},
	
	TORPEDOFISH("TORPEDOFISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Torpedofish(state, startPos, extraField, filter, spawner);
		}
	},
	
	SPLITTER_SMALL("KAMABOKLING") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKSmall(state, startPos, extraField, filter, spawner);
		}
	},
	
	SPLITTER_MEDIUM("KAMABOKO MASS") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKMedium(state, startPos, extraField, filter, spawner);
		}
	},
	
	SPLITTER_LARGE("GREATER KAMABOKO MASS") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKLarge(state, startPos, extraField, filter, spawner);
		}
	},
	
	SPAWNER("KAMABOKO SPAWNER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKSpawner(state, startPos, extraField, filter, spawner);
		}
	},
	
	BOUNCER("BOUNCING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKBouncer(state, startPos, extraField, filter, spawner);
		}
	},
	
	KBK_BUDDY("KAMABOKO BUDDY") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new KBKBuddy(state, startPos, extraField, filter, spawner);
		}
	},
	
	DRONE_BIT("DRONE BIT") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new DroneBit(state, startPos, extraField, filter, spawner);
		}
	},
	
	MISCFISH("MISCELLANEOUS FISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			double randNum = Math.random();
			if (randNum < 0.3f) {
				return new Scissorfish(state, startPos, extraField, filter, spawner);
			} else if (randNum < 0.7f) {
				return new Spittlefish(state, startPos, extraField, filter, spawner);
			} else {
				return new Torpedofish(state, startPos, extraField, filter, spawner);
			}
		}
	},
	
	TURRET_FLAK("FLAK TURRET") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new TurretFlak(state, startPos, extraField, filter, spawner);
		}
	},
	
	TURRET_VOLLEY("VOLLEY TURRET") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new TurretVolley(state, startPos, extraField, filter, spawner);
		}
	},
	
	DRONE("DRONE") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Drone(state, startPos, extraField, filter, extraField, spawner);
		}
	},
	
	BOSS1("SLIGHTLY LARGER FISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Boss1(state, startPos, filter, spawner);
		}
	},
	
	BOSS2("KING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Boss2(state, startPos, filter, spawner);
		}
	},
	
	TURRET_BOSS("TURRET BOSS") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new TurretBoss(state, startPos, extraField, filter, spawner);
		}
	},
	
	BOSS4("FALSE SUN") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Boss4(state, startPos, filter, spawner);
		}
	},

	BOSS5("NEPTUNE KING TERRAZZA") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Boss5(state, startPos, filter, spawner);
		}
	},
	;

	//this is the name that shows up in the kill feed when you die to this enemy
	private final String name;

	EnemyType(String name) {
		this.name = name;
	}

	/**
	 * This generates an enemy of the chosen type
	 * @param state: state to spawn the enemy in
	 * @param startPos: the starting position of the enemy
	 * @param filter: the faction of the enemy
	 * @param extraField: some enemies use an extra field to determine starting orientation
	 * @param spawner: the event that spawned this enemy (null if spawned through other means)
	 * @return the spawned enemies
	 */
	public abstract Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner);
	
	/**
	 * This is like generateEnemy, except it creates a delayed spawn event that creates the enemy after some time
	 */
	public void generateEnemyDelayed(PlayState state, Vector2 startPos, float lifespan, short filter, float extraField, SpawnerSchmuck spawner, boolean isBoss, String bossName) {
		new DelayedSpawn(state, startPos, lifespan, this, filter, extraField, spawner, isBoss, bossName);
	}

	public String getName() { return name; }
}
