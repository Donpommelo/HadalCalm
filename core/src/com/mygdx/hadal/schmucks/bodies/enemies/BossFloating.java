package com.mygdx.hadal.schmucks.bodies.enemies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.hadal.HadalGame;
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
				
    private float angle, desiredAngle;
	
	private int spinSpeed;
	
	private BossState currentState;
	
	private Animation<TextureRegion> floatingSprite;
	/**
	 * Enemy constructor is run when an enemy spawner makes a new enemy.
	 * @param state: current gameState
	 * @param world: box2d world
	 * @param camera: game camera
	 * @param rays: game rayhandler
	 * @param width: width of enemy
	 * @param height: height of enemy
	 * @param x: enemy starting x position.
	 * @param y: enemy starting x position.
	 */
	public BossFloating(PlayState state, int x, int y, int width, int height, int hbWidth, int hbHeight, float scale, enemyType type, short filter, int hp, int moveSpeed, int spinSpeed, float attackCd, 
			SpawnerSchmuck spawner, Sprite sprite) {
		super(state, x, y, width, height, hbWidth, hbHeight, scale, type, filter, hp, moveSpeed, attackCd, spawner, sprite);
		
		this.angle = 0;
		this.desiredAngle = 0;
		this.spinSpeed = spinSpeed;
		
		this.currentState = BossState.TRACKING_PLAYER;
		
		this.floatingSprite = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, sprite.getFrames());
		
	}

	/**
	 * Enemy ai goes here. Default enemy behaviour just walks right/left towards player and fires weapon.
	 */
	@Override
	public void controller(float delta) {		
		super.controller(delta);
		
		angle = angle + (desiredAngle - angle) * 0.04f;
		setOrientation((float) ((angle + 270) * Math.PI / 180));
		
		switch(currentState) {
			
		case SPINNING:
			desiredAngle += spinSpeed;
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
	}
	
	/**
	 * draws enemy
	 */
	@Override
	public void render(SpriteBatch batch) {

		boolean flip = false;
		if (getOrientation() > Math.PI && getOrientation() < 2 * Math.PI) {
			flip = true;
		}
		
		if (flashingCount > 0) {
			batch.setShader(HadalGame.shader);
		}
		
		batch.draw((TextureRegion) floatingSprite.getKeyFrame(animationTime, true), 
				getPosition().x * PPM - hbHeight * scale / 2, 
				(flip ? height * scale : 0) + getPosition().y * PPM - hbWidth * scale / 2, 
				hbHeight * scale / 2, 
				(flip ? -1 : 1) * hbWidth * scale / 2,
				width * scale, (flip ? -1 : 1) * height * scale, 1, 1, 
				(float) Math.toDegrees(getOrientation()) - 90);

		if (flashingCount > 0) {
			batch.setShader(null);
		}
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, hbHeight * scale, hbWidth * scale, 
					(int)(getPosition().x * PPM), 
					(int)(getPosition().y * PPM), sprite, getLinearVelocity(), 0.5f, false);
		}
		return super.queueDeletion();
	}
	
	public void setCurrentState(BossState currentState) {
		this.currentState = currentState;
	}
	
	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}
	
	public float getDesiredAngle() {
		return desiredAngle;
	}

	public void setDesiredAngle(float desiredAngle) {
		this.desiredAngle = desiredAngle;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public void setSpinSpeed(int spinSpeed) {
		this.spinSpeed = spinSpeed;
	}
	
	@Override
	public float getAttackAngle() {
		return angle;
	}
	
	public enum BossState {
		TRACKING_PLAYER,
		LOCKED,
		FREE,
		SPINNING
	}
}