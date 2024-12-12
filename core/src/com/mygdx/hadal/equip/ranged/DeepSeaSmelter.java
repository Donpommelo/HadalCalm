package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.DeepSmelt;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.text.UIText;

public class DeepSeaSmelter extends RangedWeapon {

	private static final int CLIP_SIZE = 500;
	private static final int AMMO_SIZE = 0;
	private static final float SHOOT_CD = 0.12f;
	private static final float RELOAD_TIME = 1.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 45.0f;
	private static final float PROJ_SPACING = 12.0f;
	private static final float MAX_CHARGE = 4.5f;
	private static final float CHARGE_PER_SHOT = 2.75f;
	private static final float BURN_DAMAGE = 4.0f;

	private static final Vector2 PROJECTILE_SIZE = DeepSmelt.PROJECTILE_SIZE;
	private static final float LIFESPAN = DeepSmelt.LIFESPAN;
	private static final float BASE_DAMAGE = DeepSmelt.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite EVENT_SPRITE = Sprite.P_NEMATOCYTEARM;
	
	private final Vector2 projOrigin = new Vector2();
	private final Vector2 projOffset = new Vector2();
	private boolean overheated;
	
	public DeepSeaSmelter(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);
		
		if (reloading || getClipLeft() == 0 || overheated) { return; }
		
		if (chargeCd < getChargeTime()) {
			setCharging(true);

			//we take EQUIP_CHARGE_RATE into account to avoid modifiers from making the weapon overheat faster
			setChargeCd(chargeCd + (delta + SHOOT_CD) * CHARGE_PER_SHOT * (1 - user.getBodyData().getStat(Stats.EQUIP_CHARGE_RATE)));
			
			if (chargeCd >= getChargeTime()) {
				user.getBodyData().addStatus(new Ablaze(state, MAX_CHARGE, user.getBodyData(), user.getBodyData(), BURN_DAMAGE,
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

		projOffset.set(startVelocity).rotate90(1).nor().scl(PROJ_SPACING);
		projOrigin.set(startPosition).add(projOffset);
		positions[0] = new Vector2(projOrigin);
		velocities[0] = startVelocity;
		projOffset.set(startVelocity).rotate90(-1).nor().scl(PROJ_SPACING);
		projOrigin.set(startPosition).add(projOffset);
		positions[1] = new Vector2(projOrigin);
		velocities[1] = startVelocity;

		SyncedAttack.DEEP_SMELT.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
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
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(SHOOT_CD),
				String.valueOf(MAX_CHARGE)};
	}
}
