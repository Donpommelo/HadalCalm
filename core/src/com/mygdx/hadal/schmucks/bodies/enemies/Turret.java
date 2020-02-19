package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.SchmuckMoveStates;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.PlayState;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Zachary Tu
 *
 */
public class Turret extends Enemy {

	private static final int baseHp = 200;
	private static final float aiAttackCd = 0.5f;
	    
	private float angle;
	private float desiredAngle;
	private float startAngle;
	
	private Sprite turretBarrelSprite;
	protected Animation<? extends TextureRegion> turretBase, turretBarrel;
	
	private static final int baseWidth = 528;
	private static final int baseHeight = 252;
	
	private static final int hboxWidth = 261;
	private static final int hboxHeight = 165;
	
	private static final int rotationX = 131;
	private static final int rotationY = 114;
	
	private static final float scale = 0.5f;
	
	private static final Sprite base = Sprite.TURRET_BASE;
	private static final Sprite flak = Sprite.TURRET_FLAK;
	private static final Sprite volley = Sprite.TURRET_VOLLEY;
	
	public Turret(PlayState state, Vector2 startPos, enemyType type, int startAngle, short filter, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(baseWidth, baseHeight).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), Sprite.NOTHING, type, filter, baseHp, aiAttackCd, spawner);		
		this.angle = 0;
		this.startAngle = startAngle;
		this.desiredAngle = startAngle;
		
		this.turretBase = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, base.getFrames());
		
		switch(type) {
		case TURRET_FLAK:
			this.turretBarrelSprite = flak;
			break;
		case TURRET_VOLLEY:
			this.turretBarrelSprite = volley;
			break;
		default:
			break;
		}
		this.turretBarrel = new Animation<TextureRegion>(PlayState.spriteAnimationSpeed, turretBarrelSprite.getFrames());
		moveState = SchmuckMoveStates.TURRET_NOTSHOOTING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		super.create();
		this.body.setType(BodyDef.BodyType.StaticBody);
	}
	
	/**
	 * Enemy ai goes here. Default enemy behavior: don't move. shoot player on sight.
	 */
	@Override
	public void controller(float delta) {
		super.controller(delta);

		if (target == null) {
			moveState = SchmuckMoveStates.TURRET_NOTSHOOTING;
		} else {
			moveState = SchmuckMoveStates.TURRET_SHOOTING;
		}
		
		angle = angle + (desiredAngle - angle) * 0.05f;
		
		switch(moveState) {
			case TURRET_NOTSHOOTING:
				desiredAngle = startAngle;
				break;
			case TURRET_SHOOTING:
				if (target.isAlive()) {
					desiredAngle =  (float)(Math.atan2(
							target.getPosition().y - getPosition().y ,
							target.getPosition().x - getPosition().x) * 180 / Math.PI);

					if (desiredAngle < 0) {
						if (desiredAngle < -90) {
							desiredAngle = 180;
						} else {
							desiredAngle = 0;
						}
					}
				}
				break;
		default:
			break;
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {
		boolean flip = false;
		
		if (Math.abs(angle) > 90) {
			flip = true;
		}
		
		float rotationYReal = rotationY;
		
		if (flip) {
			rotationYReal = size.y / scale - rotationY;
		}
		
		if(moveState == SchmuckMoveStates.TURRET_NOTSHOOTING) {
			batch.draw((TextureRegion) turretBarrel.getKeyFrame(0, true), 
					getPixelPosition().x - hboxSize.x / 2, 
					(flip ? size.y - 12 : 0) + getPixelPosition().y - hboxSize.y / 2, 
					rotationX * scale, (flip ? -size.y : 0) + rotationYReal * scale,
					size.x, (flip ? -1 : 1) * size.y, 1, 1, angle);
		} else {
			batch.draw((TextureRegion) turretBarrel.getKeyFrame(animationTime, true), 
					getPixelPosition().x - hboxSize.x / 2, 
					(flip ? size.y - 12: 0) + getPixelPosition().y - hboxSize.y / 2, 
					rotationX * scale, (flip ? -size.y : 0) + rotationYReal * scale,
					size.x, (flip ? -1 : 1) * size.y, 1, 1, angle);
		}
		
		batch.draw((TextureRegion) turretBase.getKeyFrame(animationTime, true), 
				getPixelPosition().x - hboxSize.x / 2, 
				getPixelPosition().y - hboxSize.y / 2, 
				0, 0, size.x, size.y, 1, 1, 0.0f);	
	}
	
	private final static float baseDamage = 20.0f;
	private final static float projSpeed = 25.0f;
	private final static float knockback = 15.0f;
	private final static int projSize = 40;
	private final static float projLifespan = 4.0f;
	private final static float projInterval = 0.5f;
	@Override
	public void attackInitiate() {
		if (moveState.equals(SchmuckMoveStates.TURRET_SHOOTING)) {
			EnemyUtils.shootBullet(state, this, baseDamage, projSpeed, knockback, projSize, projLifespan, projInterval);
		}
	}
	
	//Turrets send their attack angle as a body angle because I don't feel like making a specific packet for them.
	//Just in case you were confused about this weird packet.
	@Override
	public void onServerSync() {
		HadalGame.server.sendToAllUDP(new Packets.SyncEntity(entityID.toString(), getPosition(), angle));
		HadalGame.server.sendToAllUDP(new Packets.SyncSchmuck(entityID.toString(), moveState));
	}
	
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncEntity) {
			Packets.SyncEntity p = (Packets.SyncEntity) o;
			setTransform(p.pos, 0);
			angle = p.angle;
		} else {
			super.onClientSync(o);
		}
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			new Ragdoll(state, getPixelPosition(), size, Sprite.TURRET_BASE, getLinearVelocity(), 0.75f, 1.0f, false);
			new Ragdoll(state, getPixelPosition(), size, turretBarrelSprite, getLinearVelocity(), 0.75f, 1.0f, false);
		}
		return super.queueDeletion();
	}
	
	@Override
	public float getAttackAngle() {	return angle;}
}
