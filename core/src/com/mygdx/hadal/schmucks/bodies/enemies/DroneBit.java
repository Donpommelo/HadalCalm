package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;

public class DroneBit extends EnemySwimming {

	private final static int baseHp = 100;
	private final static String name = "DRONE BIT";
	
	private final static int scrapDrop = 0;

	private static final int width = 450;
	private static final int height = 450;
	
	private static final int hboxWidth = 450;
	private static final int hboxHeight = 450;
	
	private static final float attackCd = 10.0f;
	private static final float airSpeed = 0.2f;
	private static final float kbResist = 0.4f;

	private static final float scale = 0.2f;
	private static final float noiseRadius = 2.0f;

	private static final float minRange = 0.0f;
	private static final float maxRange = 1.0f;
	
	private static final Sprite sprite = Sprite.DRONE_BODY;
	
	private TextureRegion armBackSprite, armFrontSprite;
	private Animation<TextureRegion> eyeSprite;
	
	public DroneBit(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), name, sprite, EnemyType.DRONE_BIT, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		armBackSprite = Sprite.DRONE_ARM_BACK.getFrame();
		armFrontSprite = Sprite.DRONE_ARM_FRONT.getFrame();
		eyeSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, Sprite.DRONE_EYE.getFrames());
		eyeSprite.setPlayMode(PlayMode.NORMAL);
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
		getBodyData().addStatus(new StatChangeStatus(state, Stats.KNOCKBACK_RES, kbResist, getBodyData()));
		getBodyData().addStatus(new StatChangeStatus(state, Stats.AIR_SPD, airSpeed, getBodyData()));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), sprite, size));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), Sprite.DRONE_ARM_BACK, size));
		getBodyData().addStatus(new DeathRagdoll(state, getBodyData(), Sprite.DRONE_ARM_FRONT, size));
	}
	
	@Override
	public void acquireTarget() {}
	
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
		
		batch.draw((TextureRegion) eyeSprite.getKeyFrame(animationTime, false), 
				(flip ? size.x : 0) + getPixelPosition().x - size.x / 2, 
				getPixelPosition().y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + (float) Math.toDegrees(getAngle()));
		
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
