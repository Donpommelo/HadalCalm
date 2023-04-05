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
	private static final float RANGE = 60.0f;

	private static final float BASE_DAMAGE = MorningStarProjectile.BASE_DAMAGE;
	private static final float CHAIN_LENGTH = MorningStarProjectile.CHAIN_LENGTH;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;

	//this is the hitbox that this weapon extends
	private Hitbox base, star;

	//is the hitbox active?
	private boolean active;

	public MorningStar(Player user) {
		super(user, SWING_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	private final Vector2 projOffset = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
		
		//when clicked, we create the flail weapon and move it in the direction of the mouse click
		if (!active) {
			active = true;
			projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(RANGE);
			Hitbox[] hboxes = SyncedAttack.MORNING_STAR.initiateSyncedAttackMulti(state, user, new Vector2(), new Vector2[2], new Vector2[2]);
			base = hboxes[0];
			star = hboxes[1];
		}
		if (star != null) {
			star.applyForceToCenter(projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(SWING_FORCE));
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData shooter) {}
	
	@Override
	public void unequip(PlayState state) {
		active = false;
		deactivate();
	}

	/**
	 * upon deactivation, we delete the base hbox and make the others have a temporary lifespan
	 * this is so that the user can fling the flail by switching to another weapon
	 */
	private void deactivate() {

		active = false;
		
		if (base != null) {
			base.die();
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
