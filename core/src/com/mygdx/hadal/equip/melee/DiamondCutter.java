package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.DiamondCutterProjectile;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class DiamondCutter extends MeleeWeapon {

	private static final float SWING_CD = 0.0f;
	private static final float BASE_DAMAGE = DiamondCutterProjectile.BASE_DAMAGE;
	private static final float RANGE = DiamondCutterProjectile.RANGE;
	private static final float SPIN_INTERVAL = DiamondCutterProjectile.SPIN_INTERVAL;

	//keeps track of attack speed without input buffer doing an extra mouse click
	private static final float INNATE_ATTACK_COOLDOWN = 0.5f;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;

	//is the player holding their mouse?
	private boolean held = false;
	
	private SoundEntity sawSound;

	private float innateAttackCdCount;

	public DiamondCutter(Player user) {
		super(user, SWING_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	private final Vector2 projOffset = new Vector2();
	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData shooter, short faction, Vector2 mouseLocation) {
		if (innateAttackCdCount <= 0.0f) {
			if (!held) {
				held = true;

				if (user.getSpecialWeaponHelper().getDiamondCutterHbox() != null) {
					if (user.getSpecialWeaponHelper().getDiamondCutterHbox().isAlive()) {
						return;
					}
				}

				projOffset.set(mouseLocation).sub(shooter.getSchmuck().getPixelPosition()).nor().scl(RANGE);
				SyncedAttack.DIAMOND_CUTTER.initiateSyncedAttackSingle(state, user, new Vector2(projOffset), new Vector2());
			}
		}
	}

	//this indicates whether the button was last held. We need to keep track of this since clients will see the hbox created
	//prior to the player's "shooting" state being updated.
	//Using this, we only delete after the hbox is created to avoid skipping the delete
	private boolean shootingLast;
	@Override
	public void processEffects(PlayState state, float delta) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getPlayerData().getCurrentTool());

		if (shooting) {
			if (sawSound == null) {
				sawSound = new SoundEntity(state, user, SoundEffect.DRILL, 0.0f, 0.8f, 1.0f, true,
						true, SyncType.NOSYNC);
				if (!state.isServer()) {
					((ClientState) state).addEntity(sawSound.getEntityID(), sawSound, false, PlayState.ObjectLayer.EFFECT);
				}
			} else {
				sawSound.turnOn();
			}
		} else {
			if (sawSound != null) {
				sawSound.turnOff();
			}

			if (shootingLast && user.getSpecialWeaponHelper().getDiamondCutterHbox() != null) {
				user.getSpecialWeaponHelper().getDiamondCutterHbox().die();
				user.getSpecialWeaponHelper().setDiamondCutterHbox(null);
			}
		}
		shootingLast = shooting;
	}

	@Override
	public void execute(PlayState state, PlayerBodyData shooter) {}
	
	@Override
	public void release(PlayState state, PlayerBodyData bodyData) {
		held = false;
		if (innateAttackCdCount <= 0.0f) {
			innateAttackCdCount = INNATE_ATTACK_COOLDOWN * (1 - user.getBodyData().getStat(Stats.TOOL_SPD));
		}
	}
	
	@Override
	public void unequip(PlayState state) {
		held = false;
		if (user.getSpecialWeaponHelper().getDiamondCutterHbox() != null) {
			user.getSpecialWeaponHelper().getDiamondCutterHbox().die();
			user.getSpecialWeaponHelper().setDiamondCutterHbox(null);
		}
		if (sawSound != null) {
			sawSound.terminate();
			sawSound = null;
		}
	}

	@Override
	public void update(PlayState state, float delta) {
		if (innateAttackCdCount > 0) {
			innateAttackCdCount -= delta;
		}
	}

	@Override
	public float getBotRangeMax() { return 4.67f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(BASE_DAMAGE),
				String.valueOf(SPIN_INTERVAL)};
	}
}
