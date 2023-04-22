package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.TrickShot;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class TrickGun extends RangedWeapon {

	private static final int CLIP_SIZE = 5;
	private static final int AMMO_SIZE = 30;
	private static final float SHOOT_CD = 0.4f;
	private static final float RELOAD_TIME = 0.75f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 35.0f;

	private static final Vector2 PROJECTILE_SIZE = TrickShot.PROJECTILE_SIZE;
	private static final float LIFESPAN = TrickShot.LIFESPAN;
	private static final float BASE_DAMAGE = TrickShot.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_LASERROCKET;
	private static final Sprite EVENT_SPRITE = Sprite.P_LASERROCKET;

	private boolean firstClicked;
	private final Vector2 pos1 = new Vector2();
	private final Vector2 pos2 = new Vector2();
	private final Vector2 vel1 = new Vector2();
	private final Vector2 vel2 = new Vector2();

	public TrickGun(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, playerData, faction, mouseLocation);
		
		//when clicked, keep track of mouse location
		if (!firstClicked) {
			pos1.set(mouseLocation);
			firstClicked = true;
		}
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {}
	
	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		
		//when released, fire weapon at location where mouse was pressed and keep track of location where mouse is released.
		if (firstClicked) {
			
			//we use the player's mouse position rather than the weapons, b/c the weapon's mouse location won't update during its cooldown.
			pos2.set(playerData.getPlayer().getMouseHelper().getPixelPosition());
			
			float powerDiv = pos1.dst(pos2) / PROJECTILE_SPEED;
			
			float xImpulse = -(pos1.x - pos2.x) / powerDiv;
			float yImpulse = -(pos1.y - pos2.y) / powerDiv;
			vel2.set(xImpulse, yImpulse);
			
			powerDiv = user.getPixelPosition().dst(pos1.x, pos1.y) / PROJECTILE_SPEED;
			
			xImpulse = -(user.getPixelPosition().x - pos1.x) / powerDiv;
			yImpulse = -(user.getPixelPosition().y - pos1.y) / powerDiv;
			vel1.set(xImpulse, yImpulse);
			
			this.setWeaponVelo(vel1);

			super.execute(state, playerData);
			
			firstClicked = false;
		}
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float firstClickedIndicator = firstClicked ? 1.0f : 0.0f;
		SyncedAttack.TRICK_SHOT.initiateSyncedAttackSingle(state, user, startPosition, startVelocity,
				firstClickedIndicator, pos1.x, pos1.y, pos2.x, pos2.y);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
