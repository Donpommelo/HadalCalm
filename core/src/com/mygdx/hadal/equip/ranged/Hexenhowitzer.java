package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Hex;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.MagicGlow;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.text.UIText;

public class Hexenhowitzer extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 1;
	private static final float SHOOT_CD = 0.35f;
	private static final float RELOAD_TIME = 1.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 40.0f;
	private static final float MAX_CHARGE = 72.0f;
	private static final float CHARGE_LOST_PER_SHOT = 2.8f;
	private static final float SUPERCHARGED_SHOOT_CD = 0.07f;
	private static final float ENEMY_CHARGE_MULTIPLIER = 0.2f;

	private static final Vector2 PROJECTILE_SIZE = Hex.PROJECTILE_SIZE;
	private static final float LIFESPAN = Hex.LIFESPAN;
	private static final float BASE_DAMAGE = Hex.BASE_DAMAGE;
	private static final float RECOIL = 4.5f;
	private static final float KNOCKBACK = 20.0f;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_TORPEDO;
	private static final Sprite EVENT_SPRITE = Sprite.P_TORPEDO;

	private boolean supercharged = false;
	private Status glowing;
	
	public Hexenhowitzer(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}
	
	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		
		//this is the same as the super method except we skip the clip size check
		playerData.statusProcTime(new ProcTime.Shoot(this));
		
		projOrigin.set(playerData.getSchmuck().getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x));
		
		user.pushFromLocation(mouseLocation, RECOIL * (1 + playerData.getStat(Stats.RANGED_RECOIL)));
		
		//Shoot			
		fire(state, user, projOrigin, weaponVelo, faction);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = SyncedAttack.HEX.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, supercharged ? 1.0f : 0.0f);
		final Equippable me = this;
		if (supercharged) {
			//when charged we deplete charge when shooting and remove visual effect when empty
			me.setChargeCd(me.getChargeCd() - CHARGE_LOST_PER_SHOT);
			if (me.getChargeCd() <= 0.0f) {
				charging = false;
			}

			//when charged, we have a faster fire rate
			user.getShootHelper().setShootCdCount(SUPERCHARGED_SHOOT_CD);
 		} else {
			hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

				private final Array<HadalData> damaged = new Array<>();

				@Override
				public void onHit(HadalData fixB, Body body) {
					if (fixB != null) {
						if (UserDataType.BODY.equals(fixB.getType())) {
							if (!damaged.contains(fixB, false)) {
								damaged.add(fixB);

								//gain charge based on the amount of damage dealt by this weapon's projectiles
								float damage = fixB.receiveDamage(BASE_DAMAGE * hbox.getDamageMultiplier(),
										hbox.getLinearVelocity().nor().scl(KNOCKBACK), creator, true, hbox, DamageSource.HEXENHOWITZER,
										DamageTag.MAGIC, DamageTag.RANGED);

								me.setCharging(true);

								if (fixB instanceof PlayerBodyData) {
									me.setChargeCd(me.getChargeCd() + damage);
								} else {
									me.setChargeCd(me.getChargeCd() + damage * ENEMY_CHARGE_MULTIPLIER);
								}
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean charging = this.equals(user.getEquipHelper().getCurrentTool()) && !reloading && getClipLeft() > 0;

		if (charging) {
			//if fully charged, get a visual effect
			if (user.getUiHelper().getChargePercent() >= 1.0f && !supercharged) {
				supercharged = true;
				glowing = new MagicGlow(state, user.getBodyData());
				user.getBodyData().addStatus(glowing);

				SoundManager.play(state, new SoundLoad(SoundEffect.MAGIC25_SPELL)
						.setVolume(0.5f)
						.setPosition(playerPosition));
			}
			if (user.getUiHelper().getChargePercent() <= 0.0f && supercharged) {
				supercharged = false;

				if (glowing != null) {
					user.getBodyData().removeStatus(glowing);
				}
			}
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
			return UIText.SUPERCHARGE.text();
		} else {
			return UIText.CHARGE.text();
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(SHOOT_CD),
				String.valueOf(SUPERCHARGED_SHOOT_CD),
				String.valueOf((int) (MAX_CHARGE / CHARGE_LOST_PER_SHOT))};
	}
}
