package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathParticles;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class KBKCrawler2 extends EnemyCrawling {

	private final static int baseHp = 200;
	private final static String name = "GIGA CRAWLING KAMABOKO";

	private final static int scrapDrop = 8;

	private static final int width = 1024;
	private static final int height = 1024;
	
	private static final int hboxWidth = 560;
	private static final int hboxHeight = 240;

	private static final int smileOffset = 400;

	private static final float scale = 0.25f;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.3f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_CRAWL;
	
	private TextureRegion faceSprite;
	
	public KBKCrawler2(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.CRAWLER1BIG, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
		setCurrentState(CrawlingState.AVOID_PITS);
	}
	
	private static final int charge1Damage = 15;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 30;
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
		getBodyData().addStatus(new DeathParticles(state, getBodyData(), Particle.KAMABOKO_IMPACT, 1.0f));
		
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f, true);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = false;
		
		if (getMoveDirection() < 0) {
			flip = true;
		} else if (getMoveDirection() > 0) {
			flip = false;
		}

		batch.draw(faceSprite, 
				(flip ? 0 : size.x) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - getHboxSize().y / 2 - smileOffset * scale, 
				size.x / 2,
				(flip ? 1 : -1) * size.y / 2, 
				(flip ? 1 : -1) * size.x, size.y, 1, 1, 0);
		
	}
}
