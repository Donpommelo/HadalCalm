package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.text.UIText;

public class DeepSeaSmelter extends RangedWeapon {

	private static final int clipSize = 500;
	private static final int ammoSize = 0;
	private static final float shootCd = 0.12f;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 4.0f;
	private static final float knockback = 22.0f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(50, 15);
	private static final float lifespan = 1.0f;
	
	private static final Sprite projSprite = Sprite.SLAG;
	private static final Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite eventSprite = Sprite.P_NEMATOCYTEARM;
	
	private static final float pitchSpread = 0.4f;
	
	private static final float projSpacing = 12.0f;

	private static final float maxCharge = 4.5f;
	private static final float chargePerShot = 2.75f;
	private static final float burnDamage = 4.0f;

	private final Vector2 projOrigin = new Vector2();
	private final Vector2 projOffset = new Vector2();
	private boolean overheated;
	
	public DeepSeaSmelter(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);
		
		if (reloading || getClipLeft() == 0 || overheated) { return; }
		
		if (chargeCd < getChargeTime()) {
			setCharging(true);

			//we take EQUIP_CHARGE_RATE into account to avoid modifiers from making the weapon overheat faster
			setChargeCd(chargeCd + (delta + shootCd) * chargePerShot * (1 - user.getBodyData().getStat(Stats.EQUIP_CHARGE_RATE)));
			
			if (chargeCd >= getChargeTime()) {
				user.getBodyData().addStatus(new Ablaze(state, maxCharge, user.getBodyData(), user.getBodyData(), burnDamage,
						DamageSource.DEEP_SEA_SMELTER));
				overheated = true;
			}
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {

		//weapon is disabled when overheated
		if (overheated) { return; }

		super.execute(state, playerData);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		//weapon is disabled when overheated
		if (overheated) { return; }

		Vector2[] positions = new Vector2[2];
		Vector2[] velocities = new Vector2[2];

		projOffset.set(startVelocity).rotate90(1).nor().scl(projSpacing);
		projOrigin.set(startPosition).add(projOffset);
		positions[0] = new Vector2(projOrigin);
		velocities[0] = startVelocity;
		projOffset.set(startVelocity).rotate90(-1).nor().scl(projSpacing);
		projOrigin.set(startPosition).add(projOffset);
		positions[1] = new Vector2(projOrigin);
		velocities[1] = startVelocity;

		SyncedAttack.DEEP_SMELT.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
	}

	public static Hitbox[] createDeepSmelt(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];
		if (startPosition.length != 0) {
			float pitch = (MathUtils.random() - 0.5f) * pitchSpread;
			SoundEffect.METAL_IMPACT_1.playSourced(state, startPosition[0], 0.5f, 1.0f + pitch);
			user.recoil(startVelocity[0], recoil);

			for (int i = 0; i < startPosition.length; i++) {
				Hitbox hbox = new RangedHitbox(state, startPosition[i], projectileSize, lifespan, startVelocity[i],
						user.getHitboxFilter(), true, true, user, projSprite);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
						DamageSource.DEEP_SEA_SMELTER, DamageTag.RANGED));
				hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.METAL_IMPACT_2, 0.3f).setSynced(false));
				hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.2f, true).setSynced(false));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
						HadalColor.YELLOW).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.LASER_IMPACT).setOffset(true).setParticleColor(
						HadalColor.YELLOW).setSyncType(SyncType.NOSYNC));

				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}

	//heat level of the weapon decreases over time
	@Override
	public void update(PlayState state, float delta) {
		if (chargeCd > 0) {
			chargeCd -= (delta * (1 - user.getBodyData().getStat(Stats.EQUIP_CHARGE_RATE)));
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
	
	//custom charging text to convey overheat information
	@Override
	public String getChargeText() {
		if (overheated) {
			return UIText.OVERHEAT.text();
		} else {
			return UIText.HEAT.text();
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(shootCd),
				String.valueOf(maxCharge)};
	}
}
