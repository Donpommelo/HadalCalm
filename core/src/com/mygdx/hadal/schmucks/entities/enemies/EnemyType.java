package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
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
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKCrawler(state, startPos, extraField, filter);
		}
	},
	
	CRAWLER1("CRAWLING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKCrawler(state, startPos, extraField, filter);
		}
	},
	
	CRAWLER1BIG("GIGA CRAWLING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKCrawler2(state, startPos, extraField, filter);
		}
	},
	
	CRAWLER2("CHARGER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Crawler2(state, startPos, extraField, filter);
		}
	},
	
	CRAWLER3("SPITTING CRAWLER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Crawler3(state, startPos, extraField, filter);
		}
	},
	
	LEAPER1("LEAPER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Leaper1(state, startPos, extraField, filter);
		}
	},
	
	SWIMMER1("SWIMMING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Swimmer1(state, startPos, extraField, filter);
		}
	},
	
	SWIMMER2("FIREBREATHING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Swimmer2(state, startPos, extraField, filter);
		}
	},
	
	SCISSORFISH("SCISSORFISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Scissorfish(state, startPos, extraField, filter);
		}
	},
	
	SPITTLEFISH("SPITTLEFISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Spittlefish(state, startPos, extraField, filter);
		}
	},
	
	TORPEDOFISH("TORPEDOFISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Torpedofish(state, startPos, extraField, filter);
		}
	},
	
	SPLITTER_SMALL("KAMABOKLING") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKSmall(state, startPos, extraField, filter);
		}
	},
	
	SPLITTER_MEDIUM("KAMABOKO MASS") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKMedium(state, startPos, extraField, filter);
		}
	},
	
	SPLITTER_LARGE("GREATER KAMABOKO MASS") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKLarge(state, startPos, extraField, filter);
		}
	},
	
	SPAWNER("KAMABOKO SPAWNER") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKSpawner(state, startPos, extraField, filter);
		}
	},
	
	BOUNCER("BOUNCING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKBouncer(state, startPos, extraField, filter);
		}
	},
	
	KBK_BUDDY("KAMABOKO BUDDY") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new KBKBuddy(state, startPos, extraField, filter);
		}
	},
	
	DRONE_BIT("DRONE BIT") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new DroneBit(state, startPos, extraField, filter);
		}
	},
	
	MISCFISH("MISCELLANEOUS FISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			double randNum = MathUtils.random();
			if (randNum < 0.3f) {
				return new Scissorfish(state, startPos, extraField, filter);
			} else if (randNum < 0.7f) {
				return new Spittlefish(state, startPos, extraField, filter);
			} else {
				return new Torpedofish(state, startPos, extraField, filter);
			}
		}
	},
	
	TURRET_FLAK("FLAK TURRET") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new TurretFlak(state, startPos, extraField, filter);
		}
	},
	
	TURRET_VOLLEY("VOLLEY TURRET") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new TurretVolley(state, startPos, extraField, filter);
		}
	},
	
	DRONE("DRONE") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Drone(state, startPos, extraField, filter, extraField);
		}
	},
	
	BOSS1("SLIGHTLY LARGER FISH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Boss1(state, startPos, filter);
		}
	},
	
	BOSS2("KING KAMABOKO") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Boss2(state, startPos, filter);
		}
	},
	
	TURRET_BOSS("TURRET BOSS") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new TurretBoss(state, startPos, extraField, filter);
		}
	},
	
	BOSS4("FALSE SUN") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Boss4(state, startPos, filter);
		}
	},

	BOSS5("NEPTUNE KING TYRRAZZA") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Boss5(state, startPos, filter);
		}
	},

	BOSS6("GILT-SCALED SERAPH") {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField) {
			return new Boss6(state, startPos, filter);
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
	 * @return the spawned enemies
	 */
	public abstract Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField);
	
	/**
	 * This is like generateEnemy, except it creates a delayed spawn event that creates the enemy after some time
	 */
	public void generateEnemyDelayed(PlayState state, Vector2 startPos, float lifespan, short filter, float extraField,
									 SpawnerSchmuck spawner, boolean isBoss, String bossName) {
		new DelayedSpawn(state, startPos, lifespan, this, filter, extraField, spawner, isBoss, bossName);
	}

	public String getName() { return name; }
}
