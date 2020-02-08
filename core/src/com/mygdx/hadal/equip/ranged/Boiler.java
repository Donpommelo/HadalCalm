package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitStatus;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Boiler extends RangedWeapon {

	private final static int clipSize = 90;
	private final static int ammoSize = 270;
	private final static float shootCd = 0.02f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 2.5f;
	private final static float recoil = 1.0f;
	private final static float knockback = 2.0f;
	private final static float projectileSpeed = 20.0f;
	private final static Vector2 projectileSize = new Vector2(100, 50);
	private final static float lifespan = 0.5f;
	
	private final static float fireDuration = 4.0f;
	private final static float fireDamage = 3.0f;
	
	private final static Sprite weaponSprite = Sprite.MT_BOILER;
	private final static Sprite eventSprite = Sprite.P_BOILER;
	
	public Boiler(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		RangedHitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, Sprite.NOTHING);
		hbox.setDurability(3);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitStatus(state, hbox, user.getBodyData(), new Ablaze(state, fireDuration, user.getBodyData(), user.getBodyData(), fireDamage)));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED, DamageTypes.FIRE));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.FIRE, 3.0f));
	}
}
