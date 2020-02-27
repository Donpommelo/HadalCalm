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
 * Floating enemies are the basic fish-enemies of the game.
 * These enemies can rotate to face the player.
 * @author Zachary Tu
 *
 */
public class EnemyFloating extends Enemy {
				
	//the angle that the boss is facing and the angle that it lerps towards.
	private float angle;

    private float desiredAngle;
	
    //the speed that the boss spins when spinning
	private int spinSpeed;
	
	//The boss's current state in terms of passive behavior (is it tracking the player, still, spinning etc)
	private FloatingState currentState;
	
	//this is the boss's sprite
	private Animation<TextureRegion> floatingSprite;

	public EnemyFloating(PlayState state, Vector2 startPos, Vector2 size, Vector2 hboxSize, Sprite sprite, EnemyType type, short filter, int hp, float attackCd, SpawnerSchmuck spawner) {
		super(state, startPos, size, hboxSize, sprite, type, filter, hp, attackCd, spawner);
		
		this.angle = 0;
		this.desiredAngle = 0;
		
		this.currentState = FloatingState.TRACKING_PLAYER;
		
		if (!sprite.equals(Sprite.NOTHING)) {
			this.floatingSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
		}
	}


	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		//lerp towards desired angle
		float dist = (desiredAngle - angle) % 360;
		angle = angle + (2 * dist % 360 - dist) * 0.04f;		
		
		//when spinning, spin at a constant speed. When tracking, set desired angle to face player
		switch(currentState) {
		case ROTATING:
			desiredAngle += spinSpeed;
			break;
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
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, getPixelPosition(), size, sprite, getLinearVelocity(), 0.5f, 1.0f, false);
		}
		return super.queueDeletion();
	}
	
	public void setCurrentState(FloatingState currentState) { this.currentState = currentState; }
	
	public float getAngle() { return angle; }

	public void setAngle(float angle) { this.angle = angle; }
	
	public float getDesiredAngle() { return desiredAngle; }

	public void setDesiredAngle(float desiredAngle) { this.desiredAngle = desiredAngle; }

	public void setSpinSpeed(int spinSpeed) { this.spinSpeed = spinSpeed; }
	
	@Override
	public float getAttackAngle() {	return angle; }
	
	public enum FloatingState {
		TRACKING_PLAYER,
		FREE,
		ROTATING,
		SPINNING
	}
}
