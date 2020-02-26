package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;

public enum EnemyType {

	CRAWLER1() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Crawler1(state, startPos, filter, spawner);
		}
	},
	
	SCISSORFISH() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Scissorfish(state, startPos, filter, spawner);
		}
	},
	
	SPITTLEFISH() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Spittlefish(state, startPos, filter, spawner);
		}
	},
	
	TORPEDOFISH() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Torpedofish(state, startPos, filter, spawner);
		}
	},
	
	TURRET_FLAK() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Turret(state, startPos, EnemyType.TURRET_FLAK, extraField, filter, spawner);
		}
	},
	
	TURRET_VOLLEY() {

		@Override
		public Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner) {
			return new Turret(state, startPos, EnemyType.TURRET_VOLLEY, extraField, filter, spawner);
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
	
	;
	
	
	EnemyType() {}
	
	public abstract Enemy generateEnemy(PlayState state, Vector2 startPos, short filter, float extraField, SpawnerSchmuck spawner);
}
