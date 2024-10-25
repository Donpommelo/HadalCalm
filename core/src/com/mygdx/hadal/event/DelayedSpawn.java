package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.map.SettingTeamMode;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.enemies.Enemy;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.enemy.CreateBossEffects;
import com.mygdx.hadal.strategies.enemy.DeathActivateSpawner;
import com.mygdx.hadal.strategies.enemy.DeathPlayerScore;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A delayed spawn waits for a brief period of time, then dies and spawns a chosen enemy
 * This event is only ever created dynamically and so it does not have any connection behavior and will never be parsed from a tiled map.
 * @author Juthrop Jourdough
 */
public class DelayedSpawn extends Event {

	private static final Vector2 BASE_SIZE = new Vector2(32, 32);
	private static final float PARTICLE_SCALE = 0.5f;
	private static final float PARTICLE_SCALE_BOSS = 1.5f;
	
	//the type of enemy that this will spawn
	private final EnemyType type;
	
	//field supplied to the newly spawned enemy
	private final short filter;
	private final float extraField;
	private final SpawnerSchmuck spawner;
	private final boolean isBoss;
	private final String bossName;

	public DelayedSpawn(PlayState state, Vector2 startPos, float lifespan, EnemyType type, short filter, float extraField, SpawnerSchmuck spawner, boolean isBoss, String bossName) {
		super(state, startPos, BASE_SIZE, lifespan);
		this.type = type;
		this.filter = filter;
		this.extraField = extraField;
		this.spawner = spawner;
		this.isBoss = isBoss;
		this.bossName = bossName;

		//bosses create bigger particles
		ParticleCreate particleCreate = new ParticleCreate(Particle.RING, startPos)
				.setLifespan(duration)
				.setSyncType(SyncType.CREATESYNC)
				.setScale(PARTICLE_SCALE_BOSS);
		if (isBoss) {
			particleCreate.setScale(PARTICLE_SCALE_BOSS);
		} else {
			particleCreate.setScale(PARTICLE_SCALE);
		}
		EffectEntityManager.getParticle(state, particleCreate);
	}

	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
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
				state.getUIManager().setBoss(enemy);
			}
			if (SettingTeamMode.TeamMode.COOP.equals(state.getMode().getTeamMode())) {
				enemy.addStrategy(new DeathPlayerScore(state, enemy));
			}
		}
		return deleted;
	}
}
