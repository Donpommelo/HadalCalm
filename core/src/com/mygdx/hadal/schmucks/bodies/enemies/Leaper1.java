package com.mygdx.hadal.schmucks.bodies.enemies;

import java.util.concurrent.ThreadLocalRandom;

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

public class Leaper1 extends EnemyCrawling {

	private final static int baseHp = 100;
	private final static String name = "LEAPER";

	private final static int scrapDrop = 1;

	private static final int width = 512;
	private static final int height = 512;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;

	private static final int smileOffset = 200;

	private static final float scale = 0.25f;
	
	private static final float attackCd = 2.0f;
	private static final float groundSpeed = -0.75f;
	
	private static final Sprite sprite = Sprite.KAMABOKO_CRAWL;
	
	private TextureRegion faceSprite;
	
	public Leaper1(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.LEAPER1, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		faceSprite = Sprite.KAMABOKO_FACE.getFrames().get(GameStateManager.generator.nextInt(5));
		setCurrentState(CrawlingState.CHASE);
	}
	
	@Override
	public void create() {
		super.create();
		body.setGravityScale(2.0f);
		getBodyData().addStatus(new StatChangeStatus(state, Stats.GROUND_SPD, groundSpeed, getBodyData()));
		getBodyData().addStatus(new DeathParticles(state, getBodyData(), Particle.KAMABOKO_IMPACT, 1.0f));
		
		EnemyUtils.meleeAttackContinuous(state, this, charge1Damage, attackInterval, defaultMeleeKB, 0.0f, true);
	}
	
	private static final int charge1Damage = 10;
	private static final float attackInterval = 1.0f;
	private static final int defaultMeleeKB = 20;
	private static final int spread = 30;
	@Override
	public void attackInitiate() {
		push(new Vector2(0, 50).rotate(ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
	};
	
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
