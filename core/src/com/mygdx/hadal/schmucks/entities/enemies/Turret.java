package com.mygdx.hadal.schmucks.entities.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.MoveState;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.managers.RagdollManager;
import com.mygdx.hadal.managers.SpriteManager;
import com.mygdx.hadal.requests.RagdollCreate;
import com.mygdx.hadal.server.packets.PacketsSync;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.PacketUtil;

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
	
	public Turret(PlayState state, Vector2 startPos, EnemyType type, float startAngle, short filter, float baseHp, float attackCd, int scrapDrop, float scale) {
		super(state, startPos, new Vector2(baseWidth, baseHeight).scl(scale), new Vector2(hboxWidth, hboxHeight).scl(scale), type, filter, baseHp, attackCd, scrapDrop);
		this.attackAngle = 0;
		this.startAngle = startAngle;
		this.desiredAngle = startAngle;
		this.scale = scale;
		
		this.turretBase = SpriteManager.getAnimation(base);
		
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
		this.turretBarrel = SpriteManager.getAnimation(turretBarrelSprite);
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
	
	@Override
	public void render(SpriteBatch batch, Vector2 entityLocation) {
		boolean flip = Math.abs(attackAngle) > 90;

		float rotationYReal = rotationY;
		if (flip) {
			rotationYReal = size.y / scale - rotationY;
		}
		if (MoveState.DEFAULT.equals(moveState)) {
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
		
		super.render(batch, entityLocation);
	}

    //Turrets send their attack angle as a body angle because I don't feel like making a specific packet for them.
	//Just in case you were confused about this weird packet.
	@Override
	public void onServerSync() {
		state.getSyncPackets().add(new PacketsSync.SyncSchmuckAngled(entityID, getPosition(), new Vector2(), state.getTimer(),
				moveState,
				PacketUtil.percentToByte(getBodyData().getCurrentHp() / getBodyData().getStat(Stats.MAX_HP)),
				MathUtils.degRad * attackAngle));
	}
	
	@Override
	public void onClientSync(Object o) {
		super.onClientSync(o);
		if (o instanceof PacketsSync.SyncSchmuckAngled p) {
			serverAngle.set(0, 0);
			attackAngle = MathUtils.radDeg * PacketUtil.byteToRadianAngle(p.angle);
		}
	}

	@Override
	public boolean queueDeletion() {
		if (alive) {
			final Vector2 entityLocation = new Vector2(getPixelPosition());
			RagdollCreate ragdollCreate = new RagdollCreate()
					.setSprite(Sprite.TURRET_BASE)
					.setPosition(entityLocation)
					.setSize(size)
					.setVelocity(getLinearVelocity())
					.setLifespan(1.5f)
					.setGravity(1.0f)
					.setStartVelocity(true)
					.setFade();
			RagdollManager.getRagdoll(state, ragdollCreate);
			ragdollCreate.setSprite(turretBarrelSprite);
			RagdollManager.getRagdoll(state, ragdollCreate);
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
