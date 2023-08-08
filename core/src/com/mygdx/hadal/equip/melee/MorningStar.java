package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.MorningStarProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class MorningStar extends MeleeWeapon {

	private static final float SWING_CD = 0.45f;
	private static final float SWING_FORCE = 7500.0f;

	private static final float BASE_DAMAGE = MorningStarProjectile.BASE_DAMAGE;
	private static final float CHAIN_LENGTH = MorningStarProjectile.CHAIN_LENGTH;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;

	public MorningStar(Player user) {
		super(user, SWING_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);

		if (state.isServer()) {
			createMorningStar(state, user, weaponVelo);
		} else {
			SyncedAttack.MORNING_STAR.initiateSyncedAttackNoHbox(state, user, weaponVelo, false);
		}
	}

	public static void createMorningStar(PlayState state, Player user, Vector2 weaponVelo) {
		Hitbox base = user.getSpecialWeaponHelper().getMorningStarBase();
		Hitbox star = user.getSpecialWeaponHelper().getMorningStar();

		//when clicked, we create the flail weapon and move it in the direction of the mouse click
		if (base == null || !base.isAlive()) {
			Hitbox[] hboxes = SyncedAttack.MORNING_STAR.initiateSyncedAttackMulti(state, user, new Vector2(), new Vector2[0], new Vector2[0]);
			base = hboxes[0];
			star = hboxes[1];

			user.getSpecialWeaponHelper().setMorningStarBase(base);
			user.getSpecialWeaponHelper().setMorningStar(star);

		}
		if (star != null) {
			star.applyForceToCenter(weaponVelo.nor().scl(SWING_FORCE));
		}
	}

	@Override
	public void execute(PlayState state, PlayerBodyData shooter) {}
	
	@Override
	public void unequip(PlayState state) {
		deactivate();
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		if (!this.equals(user.getPlayerData().getCurrentTool())) {
			deactivate();
		}
	}

	/**
	 * upon deactivation, we delete the base hbox and make the others have a temporary lifespan
	 * this is so that the user can fling the flail by switching to another weapon
	 */
	private void deactivate() {
		if (user.getSpecialWeaponHelper().getMorningStarBase() != null) {
			user.getSpecialWeaponHelper().getMorningStarBase().die();
		}
	}

	@Override
	public float getBotRangeMax() { return 5 * CHAIN_LENGTH + 2; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE)};
	}
}
