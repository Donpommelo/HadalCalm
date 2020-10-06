package com.mygdx.hadal.equip.melee;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.ParticleColor;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ContactWallParticles;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStatic;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.utils.Stats;

public class BatteringRam extends MeleeWeapon {

	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final Vector2 hitboxSize = new Vector2(90, 120);
	private static final float knockback = 40.0f;
	private static final float lifespan = 0.5f;
	
	private static final Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private static final Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	private static final float maxCharge = 0.5f;
	
	private static final float minRecoil = 25.0f;
	private static final float maxRecoil = 175.0f;
	
	private static final float minDamage = 15.0f;
	private static final float maxDamage = 75.0f;
	
	public BatteringRam(Schmuck user) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);
		
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
		
		if (bodyData.getSchmuck().getShootCdCount() <= 0.0f) {
			super.execute(state, bodyData);
			charging = false;
			chargeCd = 0;
		}
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		SoundEffect.WOOSH.playUniversal(state, startPosition, 1.0f, false);
		new ParticleEntity(user.getState(), user, Particle.BRIGHT, 1.0f, lifespan, true, particleSyncType.TICKSYNC).setColor(ParticleColor.VIOLET);
		
		//velocity scales with charge percentage
		float velocity = chargeCd / getChargeTime() * (maxRecoil - minRecoil) + minRecoil;
		float damage = chargeCd / getChargeTime() * (maxDamage - minDamage) + minDamage;
				
		user.getBodyData().addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 7.5f, user.getBodyData(), user.getBodyData()));
		user.getBodyData().addStatus(new StatChangeStatus(state, 0.5f, Stats.DAMAGE_RES, 0.5f, user.getBodyData(), user.getBodyData()));
		Vector2 push = new Vector2(weaponVelo).nor().scl(velocity);
		user.pushMomentumMitigation(push.x, push.y);
		
		Hitbox hbox = new Hitbox(state, mouseLocation, hitboxSize, lifespan, new Vector2(), user.getHitboxfilter(), true, true, user, Sprite.IMPACT);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS));
		hbox.addStrategy(new DamageStatic(state, hbox, user.getBodyData(), damage, knockback, DamageTypes.MELEE));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2(), false));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true));
		
		user.setShootCdCount(0.5f);
	}
}
