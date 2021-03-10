package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

public class VineSower extends RangedWeapon {

	private static final int clipSize = 2;
	private static final int ammoSize = 28;
	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.4f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 28.0f;
	private static final float recoil = 10.0f;
	private static final float knockback = 10.0f;
	private static final float projectileSpeed = 29.0f;
	private static final Vector2 projectileSize = new Vector2(40, 31);
	private static final float lifespan = 5.0f;

	private static final Sprite projSprite = Sprite.SEED;
	private static final Sprite weaponSprite = Sprite.MT_SHOTGUN;
	private static final Sprite eventSprite = Sprite.P_SHOTGUN;

	private static final float maxCharge = 0.4f;
	private static final int minVineNum = 2;
	private static final int maxVineNum = 5;
	private static final float vineLifespan = 2.0f;

	private static final Vector2 seedSize = new Vector2(45, 30);
	private static final float vineSpeed = 28.0f;
	private static final float vineDamage = 12.0f;
	private static final float vineKB = 20.0f;

	private static final int vineBendSpreadMin = 15;
	private static final int vineBendSpreadMax = 30;
	private static final int bendLength = 1;
	private static final int bendSpread = 0;

	private static final Vector2 vineSize = new Vector2(40, 20);
	private static final Vector2 vineSpriteSize = new Vector2(60, 60);

	public VineSower(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, shooter, faction, mousePosition);

		if (reloading || getClipLeft() == 0) { return; }
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.WOOSH.playUniversal(state, startPosition, 1.0f, 0.75f, false);

		final int finalVineNum = (int) (chargeCd / getChargeTime() * (maxVineNum - minVineNum) + minVineNum);

		RangedHitbox hbox = new RangedHitbox(state, startPosition, seedSize, lifespan, new Vector2(startVelocity), filter, true, true, user, projSprite);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR | Constants.BIT_DROPTHROUGHWALL));
		hbox.setGravity(1.5f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void die() {
				Vector2 finalVelo = new Vector2(hbox.getLinearVelocity()).nor().scl(vineSpeed);

				WeaponUtils.createVine(state, user, hbox.getPixelPosition(), finalVelo, finalVineNum, vineLifespan,
					vineDamage, vineKB, vineBendSpreadMin, vineBendSpreadMax, bendLength, bendSpread,
					seedSize, vineSize, vineSpriteSize, 1);
			}

			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getEntity().getMainFixture().getFilterData().categoryBits == Constants.BIT_DROPTHROUGHWALL) {
						hbox.die();
					}
				}
			}
		});
	}
}
