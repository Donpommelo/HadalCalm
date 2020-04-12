package com.mygdx.hadal.equip.ranged;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Spread;

public class Flounderbuss extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 15;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.0f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 9.0f;
	private final static float recoil = 30.0f;
	private final static float knockback = 12.0f;
	private final static float projectileSpeed = 25.0f;
	private final static Vector2 projectileSize = new Vector2(48, 40);
	private final static float lifespan = 2.0f;
	
	private final static Sprite[] projSprites = {Sprite.FLOUNDER_A, Sprite.FLOUNDER_B};
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private static final float maxCharge = 0.5f;
	private static final float veloSpread = 0.6f;

	private final static int maxNumProj = 12;
	private final static int spread = 20;
	
	public Flounderbuss(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, shooter, faction, mousePosition);

		if (reloading || getClipLeft() == 0) {
			return;
		}
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			chargeCd += delta;
			if (chargeCd >= getChargeTime()) {
				chargeCd = getChargeTime();
			}
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		charging = false;
		chargeCd = 0;
	}
	
	private Vector2 newVelocity = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.SHOTGUN.playUniversal(state, startPosition, 1.0f, false);

		//amount of projectiles scales to charge percent
		for (int i = 0; i < maxNumProj * chargeCd / getChargeTime() + 1; i++) {
			
			int randomIndex = ThreadLocalRandom.current().nextInt(projSprites.length);
			Sprite projSprite = projSprites[randomIndex];
			newVelocity.set(startVelocity).scl((ThreadLocalRandom.current().nextFloat() * veloSpread + 1 - veloSpread / 2));
			
			Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(newVelocity), filter, true, true, user, projSprite);
			hbox.setGravity(1.5f);
			
			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.SHRAPNEL, DamageTypes.RANGED));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));
		}
	}
}
