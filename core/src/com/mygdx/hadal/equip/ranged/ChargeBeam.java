package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
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

public class ChargeBeam extends RangedWeapon {

	private final static String name = "Charge Beam";
	private final static int clipSize = 4;
	private final static int ammoSize = 16;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.3f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 18.0f;
	private final static float recoil = 7.5f;
	private final static float knockback = 25.0f;
	private final static float projectileSpeed = 35.0f;
	private final static int projectileWidth = 64;
	private final static int projectileHeight = 64;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 5;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_CHARGEBEAM;
	private final static Sprite eventSprite = Sprite.P_CHARGEBEAM;
	
	private static final float maxCharge = 0.5f;
	private int chargeStage = 0;
	
	public ChargeBeam(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth * 2, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, int x, int y) {
		charging = true;
		if (chargeCd < maxCharge && !reloading) {
			chargeCd += delta;
		}
		super.mouseClicked(delta, state, shooter, faction, x, y);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {

	}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		final Equipable tool = this;
		
		if (chargeCd >= maxCharge) {
			chargeStage = 2;
		}
		else if (chargeCd >= maxCharge / 2) {
			chargeStage = 1;
		} else {
			chargeStage = 0;
		}
		
		float sizeMultiplier = 1.0f;
		float speedMultiplier = 1.0f;
		float damageMultiplier = 2.5f;
		float kbMultiplier = 2;

		switch(chargeStage) {
		case 2:
			sizeMultiplier = 3.0f;
			speedMultiplier = 3.0f;
			damageMultiplier = 5.0f;
			kbMultiplier = 4.5f;
			break;
		case 1:
			sizeMultiplier = 2.0f;
			speedMultiplier = 2.0f;
			damageMultiplier = 3.5f;
			kbMultiplier = 3.0f;
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
							this.hbox.getLinearVelocity().nor().scl(knockback * kbMultiplier2), 
							user.getBodyData(), tool, true, DamageTypes.RANGED);
				}
			}
		});
		
		if (chargeStage == 2) {
			new ParticleEntity(state, hbox, Particle.LIGHTNING, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
		}
	}
}
