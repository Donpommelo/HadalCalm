package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class Drone extends EnemySwimming {

	private final static int baseHp = 100;
	private final static String name = "DRONE";
	
	private final static int scrapDrop = 1;

	private static final int width = 446;
	private static final int height = 387;
	
	private static final int hboxWidth = 280;
	private static final int hboxHeight = 120;
	
	private static final float attackCd = 2.0f;
	private static final float airSpeed = -0.3f;
	
	private static final float scale = 0.25f;
	private static final float noiseRadius = 3.0f;

	private static final Sprite sprite = Sprite.DRONE_BODY;
	
	private TextureRegion armBackSprite, armFrontSprite;
	private Animation<TextureRegion> eyeSprite, dotSprite;
	
	private int eyeType;
	
	public Drone(PlayState state, Vector2 startPos, float startAngle, short filter, float extraField, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.DRONE, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		armBackSprite = Sprite.DRONE_ARM_BACK.getFrame();
		armFrontSprite = Sprite.DRONE_ARM_FRONT.getFrame();
		eyeSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeedFast, Sprite.DRONE_EYE.getFrames());
		dotSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeedFast, Sprite.DRONE_DOT.getFrames());
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		
		this.eyeType = (int) extraField;
		
		setNoiseRadius(noiseRadius);
	}
	
	@Override
	public void create() {
		super.create();
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
	}
	
	private static final float minRange = 0.0f;
	private static final float maxRange = 2.0f;

	@Override
	public void attackInitiate() {};
	
	@Override
	public void render(SpriteBatch batch) {
		
		boolean flip = true;
		double realAngle = getAngle() % (Math.PI * 2);
		if ((realAngle > Math.PI / 2 && realAngle < 3 * Math.PI / 2) || (realAngle < -Math.PI / 2 && realAngle > -3 * Math.PI / 2)) {
			flip = false;
		}
		
		batch.draw(armBackSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		
		if (eyeType == 0) {
			batch.draw((TextureRegion) eyeSprite.getKeyFrame(animationTime, true), 
					(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
					getPixelPosition().y - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, 
					size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		}
		if (eyeType == 1) {
			batch.draw((TextureRegion) dotSprite.getKeyFrame(animationTime, true), 
					(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
					getPixelPosition().y - size.y / 2, 
					(flip ? -1 : 1) * size.x / 2, 
					size.y / 2,
					(flip ? -1 : 1) * size.x, size.y, 1, 1, 
					(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		}
		
		
		super.render(batch);
		
		batch.draw(armFrontSprite, 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
	}
}
