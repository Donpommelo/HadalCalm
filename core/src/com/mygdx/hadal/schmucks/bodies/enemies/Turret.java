package com.mygdx.hadal.schmucks.bodies.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.EnemyUtils;
import com.mygdx.hadal.event.SpawnerSchmuck;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * A Turret is an immobile enemy that fires towards players in sight.
 * @author Hergfish Helinda
 */
public class Turret extends Enemy {

	private float desiredAngle;
	private final float startAngle;
	
	private TurretState currentState;
	
	private Sprite turretBarrelSprite;
	private final Animation<? extends TextureRegion> turretBase, turretBarrel;
	private final float scale;

	private static final int baseWidth = 528;
	private static final int baseHeight = 252;
	
	private static final int hboxWidth = 261;
	private static final int hboxHeight = 165;
	
	private static final int rotationX = 131;
	private static final int rotationY = 114;
	
	private static final Sprite base = Sprite.TURRET_BASE;
	private static final Sprite flak = Sprite.TURRET_FLAK;
	private static final Sprite volley = Sprite.TURRET_VOLLEY;
	
	public Turret(PlayState state, Vector2 startPos, EnemyType type, float startAngle, short filter, float baseHp, float attackCd, int scrapDrop, float scale, SpawnerSchmuck spawner) {
		super(state, startPos, new Vector2(baseWidth, baseHeight).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), Sprite.NOTHING, type, filter, baseHp, attackCd, scrapDrop, spawner);
		this.attackAngle = 0;
		this.startAngle = startAngle;
		this.desiredAngle = startAngle;
		this.scale = scale;
		
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
		moveState = MoveState.DEFAULT;
		currentState = TurretState.STARTING;
	}
	
	/**
	 * Create the enemy's body and initialize enemy's user data.
	 */
	@Override
	public void create() {
		super.create();
		this.body.setType(BodyDef.BodyType.KinematicBody);
	}

	private float controllerCount;
	private static final float pushInterval = 1 / 60f;
	private final Vector2 entityWorldLocation = new Vector2();
	private final Vector2 targetWorldLocation = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);

		controllerCount += delta;
		while (controllerCount >= pushInterval) {
			controllerCount -= pushInterval;

			float dist = (desiredAngle - attackAngle) % 360;
			attackAngle = attackAngle + (2 * dist % 360 - dist) * 0.04f;

			switch(currentState) {
				case STARTING:
					desiredAngle = startAngle;
					break;
				case TRACKING:
					if (attackTarget != null) {
						if (attackTarget.isAlive()) {
							entityWorldLocation.set(getPosition());
							targetWorldLocation.set(attackTarget.getPosition());
							desiredAngle =  MathUtils.atan2(
								targetWorldLocation.y - entityWorldLocation.y ,
								targetWorldLocation.x - entityWorldLocation.x) * MathUtils.radDeg;
							if (desiredAngle < 0) {
								if (desiredAngle < -90) {
									desiredAngle = 180;
								} else {
									desiredAngle = 0;
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}
	}
	
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void render(SpriteBatch batch) {
		boolean flip = Math.abs(attackAngle) > 90;

		float rotationYReal = rotationY;
		if (flip) {
			rotationYReal = size.y / scale - rotationY;
		}
		entityLocation.set(getPixelPosition());
		if (moveState == MoveState.DEFAULT) {
			batch.draw(turretBarrel.getKeyFrame(0, true),
					entityLocation.x - getHboxSize().x / 2, 
					(flip ? size.y - 24 * scale : 0) + entityLocation.y - getHboxSize().y / 2, 
					rotationX * scale, (flip ? -size.y : 0) + rotationYReal * scale,
					size.x, (flip ? -1 : 1) * size.y, 1, 1, attackAngle);
		} else {
			batch.draw(turretBarrel.getKeyFrame(animationTime, true),
					entityLocation.x - getHboxSize().x / 2, 
					(flip ? size.y - 24 * scale : 0) +entityLocation.y - getHboxSize().y / 2, 
					rotationX * scale, (flip ? -size.y : 0) + rotationYReal * scale,
					size.x, (flip ? -1 : 1) * size.y, 1, 1, attackAngle);
		}
		
		batch.draw(turretBase.getKeyFrame(animationTime, true),
				entityLocation.x - getHboxSize().x / 2, 
				entityLocation.y - getHboxSize().y / 2, 
				0, 0, size.x, size.y, 1, 1, 0.0f);
		
		super.render(batch);
	}
	
	private static final float baseDamage = 20.0f;
	private static final float projSpeed = 25.0f;
	private static final float knockback = 15.0f;
	private static final int projSize = 40;
	private static final float projLifespan = 4.0f;
	private static final float projInterval = 0.5f;
	@Override
	public void attackInitiate() {
		EnemyUtils.shootBullet(state, this, baseDamage, projSpeed, knockback, projSize, projLifespan, projInterval);
	}
	
	//Turrets send their attack angle as a body angle because I don't feel like making a specific packet for them.
	//Just in case you were confused about this weird packet.
	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncSchmuckAngled(entityID.toString(), getPosition(), new Vector2(), entityAge,
				state.getTimer(), moveState, getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP), attackAngle));
	}
	
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuckAngled p) {
			attackAngle = p.angle;
		}
	}
	
	@Override
	public boolean queueDeletion() {
		if (alive) {
			entityLocation.set(getPixelPosition());
			new Ragdoll(state, entityLocation, size, Sprite.TURRET_BASE, getLinearVelocity(), 0.75f, 1.0f, true, false, true);
			new Ragdoll(state, entityLocation, size, turretBarrelSprite, getLinearVelocity(), 0.75f, 1.0f, true, false, true);
		}
		return super.queueDeletion();
	}
	
	private final Vector2 originPt = new Vector2();
	private final Vector2 addVelo = new Vector2();
	private static final float spawnDist = 300.0f;
	/**
	 * This method makes projectiles fired by the player spawn offset to be at the tip of the gun
	 */
	@Override
	public Vector2 getProjectileOrigin(Vector2 startVelo, float projSize) {
		originPt.set(getPixelPosition()).add(addVelo.set(startVelo).nor().scl(scale * spawnDist));
		return originPt;
	}
	
	public boolean isVisible() { return true; }

	public void setCurrentState(TurretState currentState) { this.currentState = currentState; }
	
	public enum TurretState {
		STARTING,
		TRACKING,
		FREE
	}
}
