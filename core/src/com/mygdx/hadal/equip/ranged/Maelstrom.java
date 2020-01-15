package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitChainLightning;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;

public class Maelstrom extends RangedWeapon {

	private final static int clipSize = 8;
	private final static int ammoSize = 56;
	private final static float shootCd = 0.01f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.2f;
	private final static int reloadAmount = 0;

	private final static float recoil = 0.0f;
	private final static float baseDamage = 12.0f;
	private final static float knockback = 8.0f;
	private final static float projectileSpeedStart = 50.0f;
	private final static Vector2 projectileSize = new Vector2(30, 30);
	private final static float lifespan = 1.0f;
	
	private final static float chainDamage = 5.0f;
	private final static int chainAmount = 5;
	
	private final static Sprite weaponSprite = Sprite.MT_CHAINLIGHTNING;
	private final static Sprite eventSprite = Sprite.P_CHAINLIGHTNING;
	
	public Maelstrom(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		hbox.setDurability(chainAmount);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitChainLightning(state, hbox, user.getBodyData(), chainAmount, chainDamage));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LIGHTNING, 3.0f));
	}
}
