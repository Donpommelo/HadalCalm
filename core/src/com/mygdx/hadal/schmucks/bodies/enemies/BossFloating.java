package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.states.PlayState;

/**
 * Enemies are Schmucks that attack the player.
 * Floating enemies are the basic fish-enemies of the game
 * @author Zachary Tu
 *
 */
public class BossFloating extends Boss {
				
	//the angle that the boss is facing and the angle that it lerps towards.
    private float angle, desiredAngle;
	
    //the speed that the boss spins when spinning
	private int spinSpeed;
	
	//The boss's current state in terms of passive behavior (is it tracking the player, still, spinning etc)
	private BossState currentState;
	
	//this is the boss's sprite
	protected Animation<TextureRegion> floatingSprite;

	public BossFloating(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, enemyType type, short filter, int hp, int moveSpeed, int spinSpeed, float attackCd, 
			SpawnerSchmuck spawner, Sprite sprite) {
		super(state, startPos, size, hboxSize, type, filter, hp, moveSpeed, attackCd, spawner, sprite);
		
		this.angle = 0;
		this.desiredAngle = 0;
		this.spinSpeed = spinSpeed;
		
		this.currentState = BossState.TRACKING_PLAYER;
		
		this.floatingSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
	}


	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		//lerp towards desired angle
		float dist = (desiredAngle - angle) % 360;
		angle = angle + (2 * dist % 360 - dist) * 0.04f;		
		
		//when spinning, spin at a constant speed. When tracking, set desired angle to face player
		switch(currentState) {
		case SPINNING:
			angle += spinSpeed;
			break;
		case TRACKING_PLAYER:
			if (target != null) {				
				if (target.isAlive()) {
					desiredAngle = (float)(Math.atan2(
							target.getPosition().y - getPosition().y ,
							target.getPosition().x - getPosition().x) * 180 / Math.PI);
				}
			}
			break;
		default:
			break;
		}
		setOrientation((float) ((angle + 270) * Math.PI / 180));
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {
		super.render(batch);
		
		boolean flip = false;
		double realAngle = getOrientation() % (Math.PI * 2);
		if ((realAngle > Math.PI && realAngle < 2 * Math.PI) || (realAngle < 0 && realAngle > -Math.PI)) {
			flip = true;
		}
		
		batch.draw((TextureRegion) floatingSprite.getKeyFrame(animationTime, true), 
				getPixelPosition().x - hboxSize.y / 2, 
				(flip ? size.y : 0) + getPixelPosition().y - hboxSize.x / 2, 
				hboxSize.y / 2, 
				(flip ? -1 : 1) * hboxSize.x / 2,
				size.x, (flip ? -1 : 1) * size.y, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);

		if (shaderCount > 0) {
			batch.setShader(null);
		}
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, getPixelPosition(), size, sprite, getLinearVelocity(), 0.5f, false);
		}
		return super.queueDeletion();
	}
	
	public void setCurrentState(BossState currentState) { this.currentState = currentState; }
	
	public float getAngle() { return angle; }

	public void setAngle(float angle) { this.angle = angle; }
	
	public float getDesiredAngle() { return desiredAngle; }

	public void setDesiredAngle(float desiredAngle) { this.desiredAngle = desiredAngle; }

	public void setMoveSpeed(int moveSpeed) { this.moveSpeed = moveSpeed; }

	public void setSpinSpeed(int spinSpeed) { this.spinSpeed = spinSpeed; }
	
	@Override
	public float getAttackAngle() {	return angle;}
	
	public enum BossState {
		TRACKING_PLAYER,
		LOCKED,
		FREE,
		SPINNING
	}
}
