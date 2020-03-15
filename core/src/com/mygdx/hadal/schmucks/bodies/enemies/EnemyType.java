package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public enum EnemyType {

	CRAWLER1() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Crawler1(state, startPos, extraField, filter, spawner);
		}
	},
	
	CRAWLER2() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Crawler2(state, startPos, extraField, filter, spawner);
		}
	},
	
	CRAWLER3() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Crawler3(state, startPos, extraField, filter, spawner);
		}
	},
	
	LEAPER1() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Leaper1(state, startPos, extraField, filter, spawner);
		}
	},
	
	SWIMMER1() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Swimmer1(state, startPos, extraField, filter, spawner);
		}
	},
	
	SWIMMER2() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Swimmer2(state, startPos, extraField, filter, spawner);
		}
	},
	
	SCISSORFISH() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Scissorfish(state, startPos, extraField, filter, spawner);
		}
	},
	
	SPITTLEFISH() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Spittlefish(state, startPos, extraField, filter, spawner);
		}
	},
	
	TORPEDOFISH() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Torpedofish(state, startPos, extraField, filter, spawner);
		}
	},
	
	MISCFISH() {

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
	
	TURRET_FLAK() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new TurretFlak(state, startPos, extraField, filter, spawner);
		}
	},
	
	TURRET_VOLLEY() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new TurretVolley(state, startPos, extraField, filter, spawner);
		}
	},
	
	BOSS1() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Boss1(state, startPos, filter, spawner);
		}
	},
	
	BOSS2() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Boss2(state, startPos, filter, spawner);
		}
	},
	
	TURRET_BOSS() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new TurretBoss(state, startPos, extraField, filter, spawner);
		}
	},
	;
	
	
	EnemyType() {}
	
	public abstract Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner);
}
