package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.utils.Stats;

public class Spittlefish extends EnemySwimming {

	private static final int baseHp = 100;
	private static final String name = "SPITTLEFISH";
	
	private static final int scrapDrop = 2;
	
	private static final int width = 49;
	private static final int height = 19;
	
	private static final int hboxWidth = 49;
	private static final int hboxHeight = 19;
	
	private static final float attackCd = 0.5f;
	private static final float airSpeed = -0.4f;
	private static final float kbRes = 0.5f;
	
	private static final float minRange = 6.0f;
	private static final float maxRange = 12.0f;
	
	private static final float noiseRadius = 15.0f;

	private static final Sprite sprite = Sprite.FISH_SPITTLE;

	private static final Sprite projSprite = Sprite.SPIT;
	
	public Spittlefish(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height), new Vector2(hboxWidth, hboxHeight), name, sprite, EnemyType.SPITTLEFISH, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, kbRes, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
	}
	
	private static final float attackWindup = 0.4f;
	
	private static final float baseDamage = 7.5f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 15.0f;
	private static final Vector2 projectileSize = new Vector2(30, 20);
	private static final float lifespan = 3.0f;
	private static final float range = 900.0f;
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void attackInitiate() {
		
		EnemyUtils.changeSwimmingState(this, SwimmingState.STILL, 0.0f, 0.0f);
		EnemyUtils.changeFloatingFreeAngle(this, 0.0f, 0.0f);
		EnemyUtils.windupParticles(state, this, attackWindup, Particle.BUBBLE_TRAIL, 60.0f);
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(projectileSpeed, projectileSpeed).setAngle(getAttackAngle());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					SoundEffect.SPIT.playUniversal(state, enemy.getPixelPosition(), 0.8f, false);
					
					Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, projSprite);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactWallDie(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new AdjustAngle(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DieParticles(state, hbox, enemy.getBodyData(), Particle.BUBBLE_IMPACT));
					hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new ContactUnitSound(state, hbox, enemy.getBodyData(), SoundEffect.DAMAGE3, 0.5f, true));
				}
			}
		});
		
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.0f);
	}
}
