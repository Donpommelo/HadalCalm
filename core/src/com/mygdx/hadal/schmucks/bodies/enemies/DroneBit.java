package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

	private static final int baseHp = 125;
	private static final int scrapDrop = 0;

	private static final int width = 450;
	private static final int height = 450;
	
	private static final int hboxWidth = 450;
	private static final int hboxHeight = 450;
	
	private static final float attackCd = 10.0f;
	private static final float airSpeed = 0.2f;
	private static final float kbResist = 0.4f;

	private static final float scale = 0.15f;
	private static final float noiseRadius = 2.0f;
	private static final float trackSpeed = 0.6f;

	private static final float minRange = 0.0f;
	private static final float maxRange = 1.0f;
	
	private static final Sprite sprite = Sprite.DRONE_BODY;
	
	private final TextureRegion armBackSprite, armFrontSprite;
	private final Animation<TextureRegion> eyeSprite;
	
	public DroneBit(PlayState state, Vector2 startPos, float startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.DRONE_BIT, startAngle, filter, baseHp, attackCd, scrapDrop, spawner);
		armBackSprite = Sprite.DRONE_ARM_BACK.getFrame();
		armFrontSprite = Sprite.DRONE_ARM_FRONT.getFrame();
		eyeSprite = new Animation<>(PlayState.spriteAnimationSpeed, Sprite.DRONE_EYE.getFrames());
		eyeSprite.setPlayMode(PlayMode.NORMAL);
		EnemyUtils.setSwimmingChaseState(this, 1.0f, minRange, maxRange, 0.0f);
		setNoiseRadius(noiseRadius);
		setTrackSpeed(trackSpeed);
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
	
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		boolean flip = true;
		float realAngle = getAngle() % (MathUtils.PI * 2);
		if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
			flip = false;
		}
		
		entityLocation.set(getPixelPosition());
		batch.draw(armBackSprite, 
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		
		batch.draw(eyeSprite.getKeyFrame(animationTime, false),
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		
		super.render(batch);
		
		batch.draw(armFrontSprite, 
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
	}
}
