package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class ChargeBeam extends RangedWeapon {

	private final static String name = "Charge Beam";
	private final static int clipSize = 5;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 2.25f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 30.0f;
	private final static float recoil = 7.5f;
	private final static float knockback = 22.5f;
	private final static float projectileSpeed = 16.0f;
	private final static int projectileWidth = 30;
	private final static int projectileHeight = 30;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 5;
	
	private static float chargeDura = 0.0f;
	private static int chargeStage = 0;
	private static final float maxCharge = 1.0f;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private final static Sprite eventSprite = Sprite.P_CHARGEBEAM;
	
	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, final Equipable tool, Vector2 startVelocity, float x, float y, short filter) {			
			
			if (chargeDura >= maxCharge) {
				chargeStage = 2;
			}
			else if (chargeDura >= 0.5f) {
				chargeStage = 1;
			} else {
				chargeStage = 0;
			}
			
			float sizeMultiplier = 2;
			float speedMultiplier = 1.5f;
			float damageMultiplier = 1.5f;
			float kbMultiplier = 1;

			switch(chargeStage) {
			case 2:
				sizeMultiplier = 4.0f;
				speedMultiplier = 3.0f;
				damageMultiplier = 3.5f;
				kbMultiplier = 3.0f;
				break;
			case 1:
				sizeMultiplier = 3.0f;
				speedMultiplier = 2.5f;
				damageMultiplier = 2.5f;
				kbMultiplier = 2.0f;
				break;
			}
			
			final float damageMultiplier2 = damageMultiplier;
			final float kbMultiplier2 = kbMultiplier;
			
			Hitbox hbox = new HitboxSprite(state, x, y, (int)(projectileWidth * sizeMultiplier), (int)(projectileHeight * sizeMultiplier), gravity, lifespan, projDura, 0, startVelocity.scl(speedMultiplier),
					filter, true, true, user, projSprite);
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						fixB.receiveDamage(baseDamage * damageMultiplier2, 
								this.hbox.getBody().getLinearVelocity().nor().scl(knockback * kbMultiplier2), 
								user.getBodyData(), tool, true, DamageTypes.RANGED);
					}
				}
			});
		}
	};
	
	public ChargeBeam(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weaponSprite, eventSprite);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {
		if (chargeDura < maxCharge && !reloading) {
			chargeDura+=delta;
		}
		super.mouseClicked(delta, state, shooter, faction, x, y);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {

	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		chargeDura = 0;
	}
}
