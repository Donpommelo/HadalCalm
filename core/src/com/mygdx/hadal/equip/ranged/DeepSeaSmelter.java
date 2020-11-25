package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

import java.util.concurrent.ThreadLocalRandom;

public class DeepSeaSmelter extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 1;
	private static final float shootCd = 0.12f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 6.0f;
	private static final float knockback = 22.0f;
	private static final float projectileSpeed = 40.0f;
	private static final Vector2 projectileSize = new Vector2(50, 15);
	private static final float lifespan = 1.0f;
	
	private static final Sprite projSprite = Sprite.SLAG;
	private static final Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite eventSprite = Sprite.P_NEMATOCYTEARM;
	
	private static final float pitchSpread = 0.4f;
	
	private static final float projSpacing = 20.0f;

	private static final float maxCharge = 4.5f;
	private static final float chargePerShot = 2.75f;
	private static final float burnDamage = 4.0f;

	private final Vector2 projOrigin = new Vector2();
	private final Vector2 projOffset = new Vector2();
	private boolean overheated;
	
	public DeepSeaSmelter(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		if (reloading || getClipLeft() == 0 || overheated) { return; }
		
		if (chargeCd < getChargeTime()) {
			setCharging(true);
			setChargeCd(chargeCd + (delta + shootCd) * chargePerShot);
			
			if (chargeCd >= getChargeTime()) {
				user.getBodyData().addStatus(new Ablaze(state, maxCharge, user.getBodyData(), user.getBodyData(), burnDamage));
				overheated = true;
			}
		}
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
		
		//weapon is disabled when overheated
		if (overheated) { return; }
		
		float pitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * pitchSpread;
		
		SoundEffect.METAL_IMPACT_1.playUniversal(state, startPosition, 0.5f, 1.0f + pitch, false);

		createProjectile(state, startPosition, startVelocity, filter, 1);
		createProjectile(state, startPosition, startVelocity, filter, -1);
	}
	
	private void createProjectile(PlayState state, Vector2 startPosition, Vector2 startVelocity, short filter, int rotate) {
		projOffset.set(startVelocity).rotate90(rotate).nor().scl(projSpacing);
		projOrigin.set(startPosition).add(projOffset);
		
		Hitbox hbox = new RangedHitbox(state, projOrigin, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.METAL_IMPACT_2, 0.3f));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.2f, true));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
			HadalColor.YELLOW));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
			HadalColor.YELLOW));
	}
	
	//heat level of the weapon decreases over time
	@Override
	public void update(float delta) {
		if (chargeCd > 0) {
			chargeCd -= delta;
		}
		
		//overheat decreases over time and the weapon can be reused when it depletes
		if (chargeCd <= 0) {
			setCharging(false);
			overheated = false;
		}
	}
	
	//this is to avoid resetting the charge status when reequipping this weapon
	@Override
	public void equip(PlayState state) {}
	
	@Override
	public boolean reload(float delta) { 
		reloading = false;
		return false;
	}
	
	//custom charging text to convey overheat information
	@Override
	public String getChargeText() {
		if (overheated) {
			return "OVERHEATED"; 
		} else {
			return "HEAT"; 
		}
	}
}
