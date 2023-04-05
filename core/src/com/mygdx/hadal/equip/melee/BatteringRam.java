package com.mygdx.hadal.equip.melee;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Batter;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class BatteringRam extends MeleeWeapon {

	private static final float SHOOT_CD = 0.0f;
	private static final float MAX_CHARGE = 0.25f;
	
	private static final float MIN_DAMAGE = Batter.MIN_DAMAGE;
	private static final float MAX_DAMAGE = Batter.MAX_DAMAGE;
	private static final float DAMAGE_REDUCTION = Batter.DAMAGE_REDUCTION;

	//keeps track of attack speed without input buffer doing an extra mouse click
	private static final float INNATE_ATTACK_COOLDOWN = 0.5f;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SCRAPRIPPER;
	private static final Sprite EVENT_SPRITE = Sprite.P_SCRAPRIPPER;

	private float innateAttackCdCount;

	public BatteringRam(Player user) {
		super(user, SHOOT_CD, WEAPON_SPRITE, EVENT_SPRITE, MAX_CHARGE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);

		if (innateAttackCdCount <= 0.0f) {
			charging = true;

			//while held, build charge until maximum
			if (chargeCd < getChargeTime()) {
				setChargeCd(chargeCd + delta);
			}
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData shooter) {}
	
	@Override
	public void release(PlayState state, PlayerBodyData bodyData) {
		if (innateAttackCdCount <= 0.0f) {
			super.execute(state, bodyData);
		}
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float charge = chargeCd / getChargeTime();
		SyncedAttack.BATTERING.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, charge);
		innateAttackCdCount = INNATE_ATTACK_COOLDOWN * (1 - user.getBodyData().getStat(Stats.TOOL_SPD));
	}

	@Override
	public void update(PlayState state, float delta) {
		if (innateAttackCdCount > 0) {
			innateAttackCdCount -= delta;
		}
	}

	@Override
	public float getBotRangeMax() { return 17.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MIN_DAMAGE),
				String.valueOf((int) MAX_DAMAGE),
				String.valueOf((int) (DAMAGE_REDUCTION * 100)),
				String.valueOf(MAX_CHARGE)};
	}
}
