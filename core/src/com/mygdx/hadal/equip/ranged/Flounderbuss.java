package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;

public class Flounderbuss extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 15;
	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.8f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 30.0f;
	private static final float knockback = 12.0f;
	private static final float projectileSpeed = 30.0f;
	private static final Vector2 projectileSize = new Vector2(36, 30);
	private static final float lifespan = 2.0f;
	
	private static final Sprite[] projSprites = {Sprite.FLOUNDER_A, Sprite.FLOUNDER_B};
	private static final Sprite weaponSprite = Sprite.MT_SHOTGUN;
	private static final Sprite eventSprite = Sprite.P_SHOTGUN;
	
	private static final float maxCharge = 0.5f;
	private static final float veloSpread = 0.6f;

	private static final int baseNumProj = 3;
	private static final int maxNumProj = 13;
	private static final float pitchSpread = 0.4f;
	private static final int spread = 20;
	
	public Flounderbuss(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
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
	
	private final Vector2 newVelocity = new Vector2();
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		//amount of projectiles scales to charge percent
		int numProj = (int) (maxNumProj * chargeCd / getChargeTime() + baseNumProj);
		Vector2[] positions = new Vector2[numProj];
		Vector2[] velocities = new Vector2[numProj];
		velocities[0] = startVelocity;
		for (int i = 0; i < numProj; i++) {
			newVelocity.set(startVelocity).scl((MathUtils.random() * veloSpread + 1 - veloSpread / 2));
			positions[i] = startPosition;
			velocities[i] = new Vector2(newVelocity);
		}
		SyncedAttack.FLOUNDER.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
	}

	public static Hitbox[] createFlounder(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition, Vector2[] startVelocity) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];
		if (startPosition.length != 0) {
			SoundEffect.SHOTGUN.playSourced(state, startPosition[0], 0.75f, 0.75f);
			user.recoil(weaponVelocity, recoil);

			for (int i = 0; i < startPosition.length; i++) {

				int randomIndex = MathUtils.random(projSprites.length - 1);
				Sprite projSprite = projSprites[randomIndex];

				Hitbox hbox = new RangedHitbox(state, startPosition[i], projectileSize, lifespan, new Vector2(startVelocity[i]),
						user.getHitboxfilter(), true, true, user, projSprite);
				hbox.setGravity(1.5f);
				hbox.setDurability(2);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.FISH, DamageTypes.RANGED));
				hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.DAMAGE1, 0.25f, true)
						.setPitchSpread(pitchSpread).setSynced(false));
				hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.WET_SPLAT, 0.25f)
						.setPitchSpread(pitchSpread).setSynced(false));
				hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}
}
