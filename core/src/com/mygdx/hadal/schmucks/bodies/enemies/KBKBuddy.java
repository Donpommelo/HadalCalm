package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.DeathParticles;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieParticles;
import com.mygdx.hadal.utils.Constants;

public class KBKBuddy extends EnemySwimming {

	private final static int baseHp = 200;
	private final static String name = "SWIMMING KAMABOKO";
	
	private final static int scrapDrop = 0;

	private static final int width = 384;
	private static final int height = 384;
	
	private static final int hboxWidth = 210;
	private static final int hboxHeight = 90;
	
	private static final float attackCd = 0.25f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 3.0f;

	private static final float minRange = 1.0f;
	private static final float maxRange = 4.0f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	
	private TextureRegion faceSprite;
	
	public KBKBuddy(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.KBK_BUDDY, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		
		Filter filter = getMainFixture().getFilterData();
		filter.maskBits = (short) (Constants.BIT_SENSOR | Constants.BIT_PROJECTILE);
		getMainFixture().setFilterData(filter);
		
		getBodyData().addStatus(new Invulnerability(state, 0.1f, getBodyData(), getBodyData()));
		getBodyData().addStatus(new DeathParticles(state, getBodyData(), Particle.KAMABOKO_IMPACT, 1.0f));
	}
	
	private final static float baseDamage = 10.0f;
	private final static float knockback = 6.0f;
	private final static float projectileSpeed = 35.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 3.0f;
	private final static float range = 900.0f;
	@Override
	public void attackInitiate() {
		EnemyUtils.changeFloatingState(this, FloatingState.TRACKING_PLAYER, 0, 0.4f);
		
		if (state.isHub()) {
			return;
		}
		
		getActions().add(new EnemyAction(this, 0.0f) {
			
			private Vector2 startVelo = new Vector2();
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
					hbox.addStrategy(new DamageStandard(state, hbox, enemy.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
					hbox.addStrategy(new CreateParticles(state, hbox, enemy.getBodyData(), Particle.KAMABOKO_SHOWER, 0.0f, 3.0f));
				}
			}
		});
	}

	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = true;
		double realAngle = getAngle() % (Math.PI * 2);
		if ((realAngle > Math.PI / 2 && realAngle < 3 * Math.PI / 2) || (realAngle < -Math.PI / 2 && realAngle > -3 * Math.PI / 2)) {
			flip = false;
		}

		batch.draw(faceSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
	}
}
