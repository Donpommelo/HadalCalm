package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class Swimmer1 extends EnemySwimming {

	private final static int baseHp = 100;

	private final static int scrapDrop = 1;

	private static final int width = 512;
	private static final int height = 512;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;
	
	private static final float attackCd = 2.0f;
	private static final float airSpeed = -0.5f;
	
	private static final float scale = 0.25f;

	private static final Sprite sprite = Sprite.KAMABOKO_SWIM;
	
	private TextureRegion faceSprite;
	
	public Swimmer1(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.CRAWLER1, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 2.0f;
	
	private static final int charge1Damage = 10;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 20;
	@Override
	public void attackInitiate() {
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, attackCd);
	};
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = false;
		
		if (getMoveDirection().x < 0) {
			flip = false;
		} else if (getMoveDirection().x > 0) {
			flip = true;
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
