package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DeathRagdoll;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.enemy.MovementFloat.FloatingState;
import com.mygdx.hadal.strategies.enemy.MovementSwim.SwimmingState;

public class DroneBit extends EnemySwimming {

	public static final int baseHp = 125;
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

	private Player owner;

	public DroneBit(PlayState state, Vector2 startPos, float startAngle, short filter) {
		super(state, startPos, new Vector2(width, height).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), sprite, EnemyType.DRONE_BIT, startAngle, filter, baseHp, attackCd, scrapDrop);
		armBackSprite = Sprite.DRONE_ARM_BACK.getFrame();
		armFrontSprite = Sprite.DRONE_ARM_FRONT.getFrame();
		eyeSprite = new Animation<>(PlayState.SPRITE_ANIMATION_SPEED, Sprite.DRONE_EYE.getFrames());
		eyeSprite.setPlayMode(PlayMode.NORMAL);
		getSwimStrategy().setMaxRange(maxRange);
		getSwimStrategy().setMinRange(minRange);
		getSwimStrategy().setMoveSpeed(2.0f);
		getSwimStrategy().setCurrentState(SwimmingState.OTHER);
		getSwimStrategy().setNoiseRadius(noiseRadius);
		getFloatStrategy().setCurrentState(FloatingState.FREE);
		getFloatStrategy().setTrackSpeed(trackSpeed);
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

	private static final float tetherRange = 4.0f;
	private static final Vector2 tether = new Vector2(0, 1);
	private float floatCount;
	private final Vector2 entityWorldLocation = new Vector2();
	private final Vector2 targetWorldLocation = new Vector2();
	@Override
	public void controller(float delta) {

		if (owner != null) {
			if (owner.isAlive()) {

				getSwimStrategy().setMoveSpeed(1.0f);

				//the bit moves towards a position offset by the player's mouse
				getSwimStrategy().getMoveDirection().set(getPosition()).sub(getMoveTarget().getPosition())
						.add(tether.setAngleDeg(owner.getMouseHelper().getAttackAngle()).nor().scl(tetherRange));

				float dist = getSwimStrategy().getMoveDirection().len2();

				if (dist > maxRange * maxRange) {
					getSwimStrategy().getMoveDirection().scl(-1.0f);
				} else if (dist < maxRange * maxRange && dist > minRange * minRange) {
					getSwimStrategy().setMoveSpeed(0.0f);
				}

				floatCount += delta;
				while (floatCount >= Constants.INTERVAL) {
					floatCount -= Constants.INTERVAL;
					entityWorldLocation.set(getPosition());
					targetWorldLocation.set(owner.getMouseHelper().getPosition());
					setDesiredAngle(MathUtils.atan2(
							targetWorldLocation.y - entityWorldLocation.y ,
							targetWorldLocation.x - entityWorldLocation.x) * 180 / MathUtils.PI);
				}
			}
		}

		super.controller(delta);
	}

	@Override
	public void acquireTarget() {}
	
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		boolean flip = true;
		float realAngle = getAngle() % (MathUtils.PI * 2);
		if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
			flip = false;
		}
		
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
		
		super.render(batch, entityLocation);
		
		batch.draw(armFrontSprite, 
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
	}

	public void setOwner(Player owner) { this.owner = owner; }
}
