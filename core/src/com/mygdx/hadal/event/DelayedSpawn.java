package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.enemy.CreateBossEffects;
import com.mygdx.hadal.strategies.enemy.DeathActivateSpawner;
import com.mygdx.hadal.strategies.enemy.DeathPlayerScore;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A delayed spawn waits for a brief period of time, then dies and spawns a chosen enemy
 * This event is only ever created dynamically and so it does not have any connection behavior and will never be parsed from a tiled map.
 * @author Juthrop Jourdough
 */
public class DelayedSpawn extends Event {

	private static final Vector2 baseSize = new Vector2(32, 32);
	private static final float particleScale = 0.5f;
	private static final float particleScaleBoss = 1.5f;
	
	//the type of enemy that this will spawn
	private final EnemyType type;
	
	//field supplied to the newly spawned enemy
	private final short filter;
	private final float extraField;
	private final SpawnerSchmuck spawner;
	private final boolean isBoss;
	private final String bossName;

	public DelayedSpawn(PlayState state, Vector2 startPos, float lifespan, EnemyType type, short filter, float extraField, SpawnerSchmuck spawner, boolean isBoss, String bossName) {
		super(state, startPos, baseSize, lifespan);
		this.type = type;
		this.filter = filter;
		this.extraField = extraField;
		this.spawner = spawner;
		this.isBoss = isBoss;
		this.bossName = bossName;

		//bosses create bigger particles
		if (isBoss) {
			new ParticleEntity(state, startPos, Particle.RING, duration, true, SyncType.CREATESYNC).setScale(particleScaleBoss);
		} else {
			new ParticleEntity(state, startPos, Particle.RING, duration, true, SyncType.CREATESYNC).setScale(particleScale);
		}
	}

	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1.0f, 0, false, true,
				Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
	}
	
	@Override
	public boolean queueDeletion() {
		boolean deleted = super.queueDeletion();
		if (deleted) {
			
			//when deleted, spawn enemies and set boss data
			Enemy enemy = type.generateEnemy(state, startPos, filter, extraField);
			enemy.addStrategy(new DeathActivateSpawner(state, enemy, spawner));
			enemy.setBoss(isBoss);
			if (isBoss) {
				enemy.addStrategy(new CreateBossEffects(state, enemy));
				enemy.setName(bossName);
				state.setBoss(enemy);
			}
			if (SettingTeamMode.TeamMode.COOP.equals(state.getMode().getTeamMode())) {
				enemy.addStrategy(new DeathPlayerScore(state, enemy));
			}
		}
		return deleted;
	}
}
