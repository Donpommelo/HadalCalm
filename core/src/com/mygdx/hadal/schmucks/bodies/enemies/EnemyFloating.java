package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;

/**
 * Floating enemies are the basic fish-enemies of the game.
 * These enemies can rotate to face the player.
 * @author Mebriana Meezy
 */
public class EnemyFloating extends Enemy {
				
    //the speed that the boss spins when spinning
	private int spinSpeed;
	
	//this is the speed that a tracking enemy will rotate to face its target
	private float trackSpeed = 0.04f;
	
	//The boss's current state in terms of passive behavior (is it tracking the player, still, spinning etc)
	private FloatingState currentState;
	
	//this is the boss's sprite
	private Animation<TextureRegion> floatingSprite;

	public EnemyFloating(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, short filter, int hp, float attackCd, int scrapDrop, SpawnerSchmuck spawner) {
		super(state, startPos, size, hboxSize, sprite, type, filter, hp, attackCd, scrapDrop, spawner);
		
		this.attackAngle = 0;
		this.desiredAngle = 0;
		
		this.currentState = FloatingState.TRACKING_PLAYER;
		
		if (!sprite.equals(Sprite.NOTHING)) {
			this.floatingSprite = new Animation<>(PlayState.spriteAnimationSpeedFast, sprite.getFrames());
			this.floatingSprite.setPlayMode(PlayMode.LOOP_PINGPONG);
		}
	}

	private float floatCount;
	private static final float pushInterval = 1 / 60f;
	private final Vector2 entityWorldLocation = new Vector2();
	private final Vector2 targetWorldLocation = new Vector2();
	@Override
	public void controller(float delta) {		
		super.controller(delta);

		floatCount += delta;
		while (floatCount >= pushInterval) {
			floatCount -= pushInterval;

			//lerp towards desired angle
			float dist = (desiredAngle - attackAngle) % 360;
			attackAngle = attackAngle + (2 * dist % 360 - dist) * trackSpeed;

			//when spinning, spin at a constant speed. When tracking, set desired angle to face player
			switch(currentState) {
				case ROTATING:
					desiredAngle += spinSpeed;
					break;
				case SPINNING:
					attackAngle += spinSpeed;
					break;
				case TRACKING_PLAYER:
					//rotate towards attack target
					if (attackTarget != null) {
						if (attackTarget.isAlive()) {
							entityWorldLocation.set(getPosition());
							targetWorldLocation.set(attackTarget.getPosition());
							desiredAngle = MathUtils.atan2(
								targetWorldLocation.y - entityWorldLocation.y ,
								targetWorldLocation.x - entityWorldLocation.x) * 180 / MathUtils.PI;
						}
					} else {
						//if there is no attack target, attempt to rotate towards movement target
						if (getMoveTarget() != null) {
							if (getMoveTarget().isAlive()) {
								entityWorldLocation.set(getPosition());
								targetWorldLocation.set(getMoveTarget().getPosition());

								desiredAngle = MathUtils.atan2(
									targetWorldLocation.y - entityWorldLocation.y ,
									targetWorldLocation.x - entityWorldLocation.x) * 180 / MathUtils.PI;
							}
						}
					}
					break;
				default:
					break;
			}
			setAngle(attackAngle * MathUtils.degRad);
		}
	}
	
	/**
	 * draws enemy
	 */
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		
		boolean flip = true;
		float realAngle = getAngle() % (MathUtils.PI * 2);
		if ((realAngle > MathUtils.PI / 2 && realAngle < 3 * MathUtils.PI / 2) || (realAngle < -MathUtils.PI / 2 && realAngle > -3 * MathUtils.PI / 2)) {
			flip = false;
		}
		
		entityLocation.set(getPixelPosition());
		batch.draw(floatingSprite.getKeyFrame(animationTime, true),
				(flip ? size.x : 0) + entityLocation.x - size.x / 2, 
				entityLocation.y - size.y / 2, 
				(flip ? -1 : 1) * size.x / 2, 
				size.y / 2,
				(flip ? -1 : 1) * size.x, size.y, 1, 1, 
				(flip ? 0 : 180) + MathUtils.radDeg * getAngle());
		super.render(batch);
	}

	public void onServerSync() {
		if (body != null && isSyncDefault()) {
			state.getSyncPackets().add(new PacketsSync.SyncEntityAngled(entityID.toString(), getPosition(), getLinearVelocity(),
					entityAge, state.getTimer(), getAngle()));
		}
	}

	private final Vector2 originPt = new Vector2();
	private final Vector2 addVelo = new Vector2();
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		originPt.set(getPixelPosition()).add(addVelo.set(startVelo).nor().scl(getHboxSize().x / 2));
		return originPt;
	}
	
	public void setCurrentState(FloatingState currentState) { this.currentState = currentState; }
	
	public void setSpinSpeed(int spinSpeed) { this.spinSpeed = spinSpeed; }
	
	public void setTrackSpeed(float trackSpeed) { this.trackSpeed = trackSpeed; }
	
	public enum FloatingState {
		TRACKING_PLAYER,
		FREE,
		ROTATING,
		SPINNING
	}
}
