package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;

public class XBomber extends RangedWeapon {

	private static final int clipSize = 2;
	private static final int ammoSize = 26;
	private static final float shootCd = 0.2f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.2f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 24.0f;
	private static final float recoil = 12.0f;
	private static final float knockback = 30.0f;
	private static final float projectileSpeed = 48.0f;
	private static final Vector2 projectileSize = new Vector2(80, 40);
	private static final float lifespan = 0.5f;
	
	private static final Sprite projSprite = Sprite.LASER_TURQUOISE;
	private static final Sprite weaponSprite = Sprite.MT_IRONBALL;
	private static final Sprite eventSprite = Sprite.P_IRONBALL;
	
	private static final Vector2 crossSize = new Vector2(700, 40);
	private static final float crossLifespan = 0.25f;
	private static final float crossDamage = 24.0f;

	public XBomber(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.X_BOMB.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createXBomb(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.FIRE9.playSourced(state, startPosition, 0.25f);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);
		hbox.setGravity(2.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION_FUN, 0.6f).setSynced(false));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT)
				.setParticleColor(HadalColor.CYAN).setSyncType(SyncType.NOSYNC));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void die() {

				//create 2 perpendicular projectiles
				createCross(1);
				createCross(-1);
			}

			private void createCross(int rotate) {
				Hitbox cross = new RangedHitbox(state, hbox.getPixelPosition(), crossSize, crossLifespan, new Vector2(),
						user.getHitboxfilter(), true, true, user, projSprite) {

					@Override
					public void create() {
						super.create();
						setTransform(getPosition().x, getPosition().y, MathUtils.PI / 4 * rotate);
					}
				};
				cross.setSyncDefault(false);
				cross.makeUnreflectable();
				cross.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));

				cross.addStrategy(new ControllerDefault(state, cross, user.getBodyData()));
				cross.addStrategy(new DamageStandard(state, cross, user.getBodyData(), crossDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED)
						.setConstantKnockback(true, startVelocity));
				cross.addStrategy(new ContactUnitParticles(state, cross, user.getBodyData(), Particle.LASER_IMPACT).setParticleColor(
						HadalColor.CYAN).setDrawOnSelf(false).setSyncType(SyncType.NOSYNC));
				cross.addStrategy(new Static(state, cross, user.getBodyData()));

				if (!state.isServer()) {
					((ClientState) state).addEntity(cross.getEntityID(), cross, false, ClientState.ObjectSyncLayers.HBOX);
				}
			}
		});

		return hbox;
	}
}
