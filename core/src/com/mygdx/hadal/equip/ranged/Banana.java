package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.strategies.hitbox.FlashNearDeath;

public class Banana extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 27;
	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.6f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 5.0f;
	private static final float knockback = 0.0f;
	private static final float projectileSpeed = 5.0f;
	private static final Vector2 projectileSize = new Vector2(43, 30);
	private static final float lifespan = 4.0f;
	
	private static final Sprite projSprite = Sprite.BANANA;
	private static final Sprite weaponSprite = Sprite.MT_ICEBERG;
	private static final Sprite eventSprite = Sprite.P_ICEBERG;
	
	private static final float maxCharge = 0.3f;
	private static final float projectileMaxSpeed = 60.0f;

	private static final int explosionRadius = 200;
	private static final float explosionDamage = 40.0f;
	private static final float explosionKnockback = 45.0f;
	
	public Banana(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, shooter, faction, mousePosition);

		if (reloading || getClipLeft() == 0) { return; }
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
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
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.SPRING.playUniversal(state, startPosition, 0.5f, false);

		//velocity scales with charge percentage
		float velocity = chargeCd / getChargeTime() * (projectileMaxSpeed - projectileSpeed) + projectileSpeed;
		
		//bouncy hbox is separate so it can pass through drop-through platforms
		Hitbox hboxBouncy = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity), filter, false, false, user, Sprite.NOTHING);
		hboxBouncy.setRestitution(0.8f);
		hboxBouncy.setGravity(3.5f);
		hboxBouncy.setSyncDefault(false);
		
		hboxBouncy.addStrategy(new ControllerDefault(state, hboxBouncy, user.getBodyData()));
		hboxBouncy.addStrategy(new DropThroughPassability(state, hboxBouncy, user.getBodyData()));	
				
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, projSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), hboxBouncy, new Vector2(), new Vector2(), false));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.SPRING, 0.1f));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.6f));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				
				//Set banana to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(10);
			}
		});
	}
}
