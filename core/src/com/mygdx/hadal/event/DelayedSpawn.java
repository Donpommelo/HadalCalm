package com.mygdx.hadal.event;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.enemies.Enemy;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 */
public class DelayedSpawn extends Event {

	private final static Vector2 baseSize = new Vector2(32, 32);
	private EnemyType type;
	
	private short filter;
	private float extraField;
	private SpawnerSchmuck spawner;
	private boolean isBoss;
	private String bossName;
	
	public DelayedSpawn(PlayState state, Vector2 startPos, float lifespan, EnemyType type, short filter, float extraField, SpawnerSchmuck spawner, boolean isBoss, String bossName) {
		super(state, startPos, baseSize, lifespan);
		this.type = type;
		this.filter = filter;
		this.extraField = extraField;
		this.spawner = spawner;
		this.isBoss = isBoss;
		this.bossName = bossName;
		
		setStandardParticle(Particle.RING);
		setSynced(true);
	}

	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1.0f, 0, false, true, Constants.BIT_SENSOR, (short) 0, (short) 0, true, eventData);
		
		if (standardParticle != null) {
			standardParticle.onForBurst(duration);
		}
	}
	
	@Override
	public boolean queueDeletion() {
		boolean deleted = super.queueDeletion();
		if (deleted) {
			Enemy enemy = type.generateEnemy(state, startPos, filter, extraField, spawner);
			enemy.setBoss(isBoss);
			enemy.setName(bossName);
			if (isBoss) {
				state.setBoss(enemy);
			}
		}
		return deleted;
	}
}
