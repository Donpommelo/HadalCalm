package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.EnemyUtils;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.strategies.enemy.KamabokoBody;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.constants.Constants;

public class KBKBuddy extends EnemySwimming {

	private static final int baseHp = 200;
	private static final int scrapDrop = 0;

	private static final int width = 384;
	private static final int height = 384;
	
	private static final int hboxWidth = 210;
	private static final int hboxHeight = 90;
	
	private static final float attackCd = 0.3f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 3.0f;

	private static final float minRange = 1.0f;
	private static final float maxRange = 4.0f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	
	public KBKBuddy(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.KBK_BUDDY, startAngle, filter, baseHp, attackCd, scrapDrop);
		addStrategy(new KamabokoBody(state, this, true));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		getSwimStrategy().setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		
		Filter filter = getMainFixture().getFilterData();
		filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE);
		getMainFixture().setFilterData(filter);
		
		getBodyData().addStatus(new Invulnerability(state, 0.1f, getBodyData(), getBodyData()));
	}
	
	private static final float baseDamage = 16.0f;
	private static final float knockback = 6.0f;
	private static final float projectileSpeed = 35.0f;
	private static final Vector2 projectileSize = new Vector2(50, 50);
	private static final float lifespan = 3.0f;
	private static final float range = 900.0f;
	@Override
	public void attackInitiate() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		
		if (state.getMode().isHub()) { return; }
		getActions().add(new EnemyAction(this, 0.0f) {
			
			private final Vector2 startVelo = new Vector2();
			@Override
			public void execute() {
				
				if (attackTarget == null) {
					return;
				}
				
				startVelo.set(attackTarget.getPixelPosition()).sub(enemy.getPixelPosition());
				
				if (startVelo.len2() < range * range) {
					startVelo.nor().scl(projectileSpeed);
					
					Hitbox hbox = new RangedHitbox(state, enemy.getProjectileOrigin(startVelo, size.x), projectileSize, lifespan, startVelo, enemy.getHitboxfilter(), true, true, enemy, Sprite.NOTHING);
					
					hbox.addStrategy(new ControllerDefault(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, enemy.getBodyData()));
					hbox.addStrategy(new DieParticles(state, hbox, enemy.getBodyData(), Particle.KAMABOKO_IMPACT));
					hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback,
							DamageSource.PEER_PRESSURE, DamageTag.RANGED));
					hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 1.0f));
				}
			}
		});
	}

	@Override
	public void acquireTarget() {
		super.acquireTarget();
		setApproachTarget(true);
	}
}
