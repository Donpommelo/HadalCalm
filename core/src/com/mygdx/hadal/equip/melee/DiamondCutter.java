package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Stats;

import static com.mygdx.hadal.utils.Constants.PPM;

public class DiamondCutter extends MeleeWeapon {

	private static final float SWING_CD = 0.0f;
	private static final float WINDUP = 0.0f;
	private static final Vector2 PROJECTILE_SIZE = new Vector2(120, 120);
	private static final float BASE_DAMAGE = 8.5f;
	private static final float KNOCKBACK = 0.0f;
	private static final float RANGE = 90.0f;
	private static final float SPIN_SPEED = 8.0f;
	private static final float SPIN_INTERVAL = 0.017f;

	//keeps track of attack speed without input buffer doing an extra mouse click
	private static final float INNATE_ATTACK_COOLDOWN = 0.5f;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;
	private static final Sprite PROJ_SPRITE = Sprite.BUZZSAW;

	//this is the hitbox that this weapon extends
	private Hitbox hbox;
	
	//is the player holding their mouse?
	private boolean held = false;
	
	private SoundEntity sawSound;

	private float innateAttackCdCount;

	public DiamondCutter(Schmuck user) {
		super(user, SWING_CD, WINDUP, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	private final Vector2 projOffset = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {

		if (innateAttackCdCount <= 0.0f) {
			if (sawSound == null) {
				sawSound = new SoundEntity(state, user, SoundEffect.DRILL, 0.0f, 0.8f, 1.0f, true,
						true, SyncType.TICKSYNC);
			} else {
				sawSound.turnOn();
			}

			if (!held) {
				held = true;

				if (hbox != null) {
					if (hbox.isAlive()) {
						return;
					}
				}

				projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(RANGE);
				hbox = SyncedAttack.DIAMOND_CUTTER.initiateSyncedAttackSingle(state, user, new Vector2(projOffset), new Vector2());
			}
		}
	}

	public static Hitbox createDiamondCutter(PlayState state, Schmuck user) {
		Hitbox hbox = new Hitbox(state, new Vector2(), PROJECTILE_SIZE, 0, new Vector2(), user.getHitboxfilter(),
				true, true, user, PROJ_SPRITE);
		hbox.makeUnreflectable();
		hbox.setSyncedDeleteNoDelay(true);

		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL, 0.0f, 1.0f)
				.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private float controllerCount;
			@Override
			public void create() { hbox.setAngularVelocity(SPIN_SPEED); }

			private final Vector2 entityLocation = new Vector2();
			private final Vector2 pulseVelocity = new Vector2();
			private final Vector2 projOffset = new Vector2();
			@Override
			public void controller(float delta) {

				if (!user.isAlive()) {
					hbox.die();
				}

				projOffset.set(0, RANGE).setAngleDeg(((Player) user).getAttackAngle());
				entityLocation.set(user.getPosition());
				hbox.setTransform(entityLocation.x - projOffset.x / PPM,entityLocation.y - projOffset.y / PPM, hbox.getAngle());

				controllerCount += delta;
				while (controllerCount >= SPIN_INTERVAL) {
					controllerCount -= SPIN_INTERVAL;

					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), PROJECTILE_SIZE, SPIN_INTERVAL, pulseVelocity,
							user.getHitboxfilter(), true, true, user, Sprite.NOTHING);
					pulse.setSyncDefault(false);
					pulse.setEffectsVisual(false);
					pulse.makeUnreflectable();

					pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
					pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
							DamageSource.DIAMOND_CUTTER, DamageTag.MELEE).setStaticKnockback(true));

					if (!state.isServer()) {
						((ClientState) state).addEntity(pulse.getEntityID(), pulse, false, ClientState.ObjectLayer.HBOX);
					}
				}
			}

			@Override
			public void die() {
				hbox.queueDeletion();
			}
		});

		return hbox;
	}

	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		held = false;
		if (hbox != null) {
			hbox.die();
		}
		if (sawSound != null) {
			sawSound.turnOff();
		}
		if (innateAttackCdCount <= 0.0f) {
			innateAttackCdCount = INNATE_ATTACK_COOLDOWN * (1 - user.getBodyData().getStat(Stats.TOOL_SPD));
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		held = false;
		if (hbox != null) {
			hbox.die();
		}
		if (sawSound != null) {
			sawSound.terminate();
			sawSound = null;
		}
	}

	@Override
	public void update(PlayState state, float delta) {
		if (innateAttackCdCount > 0) {
			innateAttackCdCount -= delta;
		}
	}

	@Override
	public float getBotRangeMax() { return 4.67f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(BASE_DAMAGE),
				String.valueOf(SPIN_INTERVAL)};
	}
}
