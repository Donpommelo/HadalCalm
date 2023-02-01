package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Riftsplitter extends MeleeWeapon {

	private static final float SHOOT_CD = 0.4f;
	private static final float BASE_DAMAGE = 30.0f;
	private static final Vector2 PROJECTILE_SIZE = new Vector2(30, 120);
	private static final float PROJECTILE_SPEED = 33.0f;
	private static final float KNOCKBACK = 15.0f;
	private static final float LIFESPAN = 0.5f;
	
	private static final Vector2 SHOCKWAVE_SIZE = new Vector2(56, 64);
	private static final float SHOCKWAVE_INTERVAL = 0.1f;
	private static final float SHOCKWAVE_DAMAGE = 17.0f;
	private static final float SHOCKWAVE_SPEED = 15.0f;
	private static final float SHOCKWAVE_LIFESPAN = 0.4f;

	private static final Sprite PROJ_SPRITE = Sprite.SPLITTER_A;
	private static final Sprite WEAPON_SPRITE = Sprite.MT_SCRAPRIPPER;
	private static final Sprite EVENT_SPRITE = Sprite.P_SCRAPRIPPER;

	public Riftsplitter(Player user) {
		super(user, SHOOT_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		SoundEffect.WOOSH.playUniversal(state, shooter.getSchmuck().getPixelPosition(), 1.0f, false);
	}
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.RIFT_SPLIT.initiateSyncedAttackSingle(state, user, user.getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				startVelo.set(startVelocity).nor().scl(PROJECTILE_SPEED));
	}

	public static Hitbox createRiftSplit(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.METAL_IMPACT_1.playUniversal(state, startPosition, 0.4f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, startVelocity, user.getHitboxFilter(),
				false, true, user, PROJ_SPRITE);
		hbox.setRestitution(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.RIFTSPLITTER,
				DamageTag.MELEE, DamageTag.CUTTING));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
				.setParticleColor(HadalColor.TURQUOISE).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
				.setParticleColor(HadalColor.TURQUOISE).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SPLITTER_MAIN, 0.0f, 1.0f)
				.setRotate(true).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private float controllerCount = SHOCKWAVE_INTERVAL;
			@Override
			public void controller(float delta) {
				controllerCount += delta;

				//projectile repeatedly creates perpendicular projectiles as it moves in a straight line
				while (controllerCount >= SHOCKWAVE_INTERVAL) {
					controllerCount -= SHOCKWAVE_INTERVAL;
					createShockwave(0);
					createShockwave(-1);
				}
			}

			private void createShockwave(int rotate) {
				Hitbox shockwave = new RangedHitbox(state, hbox.getPixelPosition(), SHOCKWAVE_SIZE, SHOCKWAVE_LIFESPAN,
						new Vector2(hbox.getLinearVelocity()).rotate90(rotate).nor().scl(SHOCKWAVE_SPEED), user.getHitboxFilter(),
						true, true, user, Sprite.SPLITTER_B);
				shockwave.setSyncDefault(false);

				shockwave.addStrategy(new ControllerDefault(state, shockwave, user.getBodyData()));
				shockwave.addStrategy(new AdjustAngle(state, shockwave, user.getBodyData()));
				shockwave.addStrategy(new ContactWallDie(state, shockwave, user.getBodyData()));
				shockwave.addStrategy(new DamageStandard(state, shockwave, user.getBodyData(), SHOCKWAVE_DAMAGE, KNOCKBACK,
						DamageSource.RIFTSPLITTER, DamageTag.MELEE, DamageTag.CUTTING));
				shockwave.addStrategy(new ContactWallParticles(state, shockwave, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
						.setParticleColor(HadalColor.TURQUOISE).setSyncType(SyncType.NOSYNC));
				shockwave.addStrategy(new ContactUnitParticles(state, shockwave, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true)
						.setParticleColor(HadalColor.TURQUOISE).setSyncType(SyncType.NOSYNC));
				shockwave.addStrategy(new CreateParticles(state, shockwave, user.getBodyData(), Particle.SPLITTER_TRAIL, 0.0f, 1.0f)
						.setRotate(true).setSyncType(SyncType.NOSYNC));

				if (!state.isServer()) {
					((ClientState) state).addEntity(shockwave.getEntityID(), shockwave, false, ClientState.ObjectLayer.HBOX);
				}
			}
		});

		return hbox;
	}

	@Override
	public float getBotRangeMax() { return PROJECTILE_SPEED * LIFESPAN; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) SHOCKWAVE_DAMAGE),
				String.valueOf(SHOOT_CD)};
	}
}
