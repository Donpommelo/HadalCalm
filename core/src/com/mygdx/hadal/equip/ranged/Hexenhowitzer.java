package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.MagicGlow;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Hexenhowitzer extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 1;
	private static final float shootCd = 0.35f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 22.0f;
	private static final float recoil = 6.0f;
	private static final float knockback = 20.0f;
	private static final float projectileSpeed = 35.0f;
	private static final Vector2 projectileSize = new Vector2(50, 25);
	private static final float lifespan = 1.5f;
	
	private static final float maxCharge = 80.0f;
	private static final float chargeLostPerShot = 2.5f;

	private static final Sprite weaponSprite = Sprite.MT_TORPEDO;
	private static final Sprite eventSprite = Sprite.P_TORPEDO;
	
	private static final float pitchSpread = 0.4f;
	
	private static final float superchargedShootCd = 0.07f;
	private static final float enemyChargeMultiplier = 0.25f;
	private static final int spread = 18;
	private boolean supercharged = false;
	private Status glowing;
	
	public Hexenhowitzer(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, false, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {
		
		//this is the same as the super method except we skip the clip size check
		shooter.statusProcTime(new ProcTime.Shoot(this));
		
		projOrigin.set(shooter.getSchmuck().getProjectileOrigin(weaponVelo, projectileSize.x));
		
		user.recoil(mouseLocation, recoil * (1 + shooter.getStat(Stats.RANGED_RECOIL)));
		
		//Shoot			
		fire(state, user, projOrigin, weaponVelo, faction);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float pitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * pitchSpread;
		SoundEffect.BOTTLE_ROCKET.playUniversal(state, startPosition, 0.4f, 1.0f + pitch, false);

		final Equippable me = this;
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		hbox.setGravity(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BRIGHT, 0.0f, 1.0f).setParticleColor(
			HadalColor.RANDOM));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		
		if (supercharged) {
			
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.MAGIC, DamageTypes.RANGED));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

			//when charged we deplete charge when shooting and remove visual effect when empty
			me.setChargeCd(me.getChargeCd() - chargeLostPerShot);
			if (me.getChargeCd() <= 0.0f) {
				supercharged = false;
				charging = false;

				if (glowing != null) {
					user.getBodyData().removeStatus(glowing);
				}
			}
			
			//when charged, we have a faster fire rate
			user.setShootCdCount(superchargedShootCd);
		} else {
			
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
				
				private final ArrayList<HadalData> damaged = new ArrayList<>();

				@Override
				public void onHit(HadalData fixB) {
					if (fixB != null) {
						if (fixB.getType().equals(UserDataTypes.BODY)) {
							if (!damaged.contains(fixB)) {
								damaged.add(fixB);
								
								//gain charge based on the amount of damage dealt by this weapon's projectiles
								float damage = fixB.receiveDamage(baseDamage * hbox.getDamageMultiplier(), hbox.getLinearVelocity().nor().scl(knockback), creator, true, DamageTypes.MAGIC, DamageTypes.RANGED);
								
								me.setCharging(true);
								
								if (fixB instanceof PlayerBodyData) {
									me.setChargeCd(me.getChargeCd() + damage);
								} else {
									me.setChargeCd(me.getChargeCd() + damage * enemyChargeMultiplier);
								}
								
								//if fully charged, get a visual effect
								if (me.getChargeCd() >= getChargeTime() && !supercharged) {
									supercharged = true;
									glowing = new MagicGlow(state, user.getBodyData());
									user.getBodyData().addStatus(glowing);
									
									SoundEffect.MAGIC25_SPELL.playUniversal(state, startPosition, 0.5f, false);
								}
							}
						}
					}
				}
			});
		}
	}
	
	//this is to avoid resetting the charge status when reequipping this weapon
	@Override
	public void equip(PlayState state) {
		if (supercharged) {
			glowing = new MagicGlow(state, user.getBodyData());
			user.getBodyData().addStatus(glowing);
		}
	}
		
	@Override
	public boolean reload(float delta) { 
		reloading = false;
		return false;
	}
	
	@Override
	public void unequip(PlayState state) {
		if (glowing != null) {
			user.getBodyData().removeStatus(glowing);
		}
	}
	
	//custom charging text to convey supercharge information
	@Override
	public String getChargeText() {
		if (supercharged) {
			return "SUPERCHARGE"; 
		} else {
			return "CHARGE"; 
		}
	}
}
