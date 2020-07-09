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

public class Banana extends RangedWeapon {

	private final static int clipSize = 3;
	private final static int ammoSize = 27;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.4f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 85.0f;
	private final static float recoil = 5.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 8.0f;
	private final static Vector2 projectileSize = new Vector2(43, 30);
	private final static float lifespan = 4.0f;
	
	private final static Sprite projSprite = Sprite.BANANA;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;
	
	private static final float maxCharge = 0.3f;
	private final static float projectileMaxSpeed = 60.0f;

	private final static int explosionRadius = 200;
	private final static float explosionDamage = 45.0f;
	private final static float explosionKnockback = 45.0f;
	
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
		
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, new Vector2(startVelocity).nor().scl(velocity), filter, false, true, user, projSprite);
		hbox.setRestitution(1.0f);
		hbox.setGravity(3.5f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.SHRAPNEL, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.SPRING, 0.1f));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.6f));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			@Override
			public void create() {
				
				//Set banana to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(10);
			}
		});	
	}
}
