package com.mygdx.hadal.equip.melee;


import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Stats;

public class BatteringRam extends MeleeWeapon {

	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final Vector2 hitboxSize = new Vector2(90, 120);
	private static final float knockback = 40.0f;
	private static final float lifespan = 0.5f;

	private static final Sprite weaponSprite = Sprite.MT_SCRAPRIPPER;
	private static final Sprite eventSprite = Sprite.P_SCRAPRIPPER;

	private static final float maxCharge = 0.3f;
	
	private static final float minRecoil = 25.0f;
	private static final float maxRecoil = 175.0f;
	
	private static final float minDamage = 20.0f;
	private static final float maxDamage = 70.0f;

	private static final float minParticleTermination = 0.9f;
	private static final float maxParticleTermination = 0.6f;

	//keeps track of attack speed without input buffer doing an extra mouse click
	private static final float innateAttackCooldown = 0.5f;
	private float innateAttackCdCount;

	public BatteringRam(Schmuck user) {
		super(user, shootCd, shootDelay, weaponSprite, eventSprite, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mouseLocation) {
		super.mouseClicked(delta, state, shooter, faction, mouseLocation);

		if (innateAttackCdCount <= 0.0f) {
			charging = true;

			//while held, build charge until maximum
			if (chargeCd < getChargeTime()) {
				setChargeCd(chargeCd + delta);
			}
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		if (innateAttackCdCount <= 0.0f) {
			super.execute(state, bodyData);
		}
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		float charge = chargeCd / getChargeTime();
		SyncedAttack.BATTERING.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, charge);
		innateAttackCdCount = innateAttackCooldown * (1 - user.getBodyData().getStat(Stats.TOOL_SPD));
	}

	public static Hitbox createBattering(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.WOOSH.playSourced(state, startPosition, 1.0f);

		float chargeAmount = 0.0f;
		if (extraFields.length > 0) {
			chargeAmount = extraFields[0];
		}

		boolean right = startVelocity.x > 0;

		Particle particle = Particle.MOREAU_LEFT;
		if (right) {
			particle = Particle.MOREAU_RIGHT;
		}

		float particleLifespan = (1 - chargeAmount) * (minParticleTermination - maxParticleTermination) + maxParticleTermination;

		if (user instanceof Player) {
			ParticleEntity particles = new ParticleEntity(user.getState(), user, particle, 1.0f, 1.0f, true, SyncType.NOSYNC)
					.setScale(0.5f).setPrematureOff(particleLifespan)
					.setColor(WeaponUtils.getPlayerColor((Player) user));
			if (!state.isServer()) {
				((ClientState) state).addEntity(particles.getEntityID(), particles, false, ClientState.ObjectLayer.EFFECT);
			}
		}

		//velocity scales with charge percentage
		float velocity = chargeAmount * (maxRecoil - minRecoil) + minRecoil;
		float damage = chargeAmount * (maxDamage - minDamage) + minDamage;

		user.getBodyData().addStatus(new StatusComposite(state, lifespan, false, user.getBodyData(), user.getBodyData(),
				new StatChangeStatus(state, Stats.AIR_DRAG, 6.5f, user.getBodyData()),
				new StatChangeStatus(state, Stats.DAMAGE_RES, 0.5f, user.getBodyData())));

		Vector2 push = new Vector2(startVelocity).nor().scl(velocity);
		user.pushMomentumMitigation(push.x, push.y);

		Hitbox hbox = new Hitbox(state, startPosition, hitboxSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.NOTHING);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), damage, knockback, DamageTypes.MELEE)
				.setConstantKnockback(true, startVelocity));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true).setSynced(false));

		return hbox;
	}

	@Override
	public void update(PlayState state, float delta) {
		if (innateAttackCdCount > 0) {
			innateAttackCdCount -= delta;
		}
	}

	@Override
	public float getBotRangeMax() { return 17.0f; }
}
