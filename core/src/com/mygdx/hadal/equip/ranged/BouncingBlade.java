package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;

public class BouncingBlade extends RangedWeapon {

	private final static int clipSize = 5;
	private final static int ammoSize = 30;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 28.0f;
	private final static float recoil = 6.0f;
	private final static float knockback = 25.0f;
	private final static float projectileSpeed = 40.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 4.0f;
	
	private final static Sprite projSprite = Sprite.BUZZSAW;
	private final static Sprite weaponSprite = Sprite.MT_BLADEGUN;
	private final static Sprite eventSprite = Sprite.P_BLADEGUN;
	
	public BouncingBlade(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.METAL_IMPACT_1.playUniversal(state, startPosition, 0.75f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		hbox.setDurability(7);
		hbox.setRestitution(1.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));	
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
		hbox.addStrategy(new ContactWallLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.CUTTING, DamageTypes.RANGED));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.METAL_IMPACT_2, 0.4f));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE6, 0.5f, true));
	}
}
