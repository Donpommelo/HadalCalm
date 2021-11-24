package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.text.HText;

public class KillerBeat extends RangedWeapon {

	private static final int clipSize = 240;
	private static final int ammoSize = 0;
	private static final float shootCd = 0.18f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 25.0f;
	private static final float recoil = 7.0f;
	private static final float knockback = 18.0f;
	private static final float projectileSpeed = 24.0f;
	private static final Vector2 projectileSize = new Vector2(40, 40);
	private static final float lifespan = 0.8f;

	private static final Sprite projSprite = Sprite.DIATOM_D;
	private static final Sprite weaponSprite = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite eventSprite = Sprite.P_NEMATOCYTEARM;

	private static final float maxCharge = 1.0f;
	private static final float chargeBonusThreshold = 0.8f;
	private static final float bonusProjectileSpeed = 60.0f;
	private static final int bonusNumNotes = 5;
	private static final int noteSpread = 5;

	private static final int[] noteArray = {0, 1, 2, 3, 4, 5, 6};
	private static final IntArray notes = new IntArray(noteArray);

	public KillerBeat(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount,
				true, weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
		setCharging(true);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		if (chargeCd < getChargeTime() * chargeBonusThreshold) {
			int randNote = MathUtils.random(6);
			createProjectile(state, user, startPosition, startVelocity, randNote, filter);
		} else {
			notes.shuffle();
			for (int i = 0; i < bonusNumNotes; i++) {
				Vector2 finalVelocity = new Vector2(startVelocity);
				finalVelocity.setLength(bonusProjectileSpeed);
				createProjectile(state, user, startPosition, finalVelocity, notes.get(i), filter);
			}
		}

		graceCd = 0;
		chargeCd = 0.0f;
	}

	private void createProjectile(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, int note, short filter) {

		Vector2 noteVelo = new Vector2();
		switch (note) {
			case 0 -> {
				SoundEffect.PIANO_C.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - (noteSpread * 3));
			}
			case 1 -> {
				SoundEffect.PIANO_D.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - (noteSpread * 2));
			}
			case 2 -> {
				SoundEffect.PIANO_F.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - noteSpread);
			}
			case 3 -> {
				SoundEffect.PIANO_G.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg());
			}
			case 4 -> {
				SoundEffect.PIANO_A.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + noteSpread);
			}
			case 5 -> {
				SoundEffect.PIANO_B.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + (noteSpread * 2));
			}
			case 6 -> {
				SoundEffect.PIANO_C2.playUniversal(state, startPosition, 0.5f, 1.0f, false);
				noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + (noteSpread * 3));
			}
		}

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, noteVelo, filter, true, true, user, projSprite);
		hbox.setDurability(2);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.ENERGY, DamageTypes.RANGED, DamageTypes.SOUND));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.RING_TRAIL, 0.0f, 1.0f));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.NOTE_IMPACT));
		hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.NOTE_IMPACT));
	}

	//meter charges over time
	private static final float graceTime = 0.2f;
	private float graceCd;
	@Override
	public void update(PlayState state, float delta) {
		if (chargeCd < getChargeTime()) {
			chargeCd += delta;
		}
		if (chargeCd >= getChargeTime()) {

			if (graceCd < graceTime) {
				graceCd += delta;
			}

			if (graceCd >= graceTime) {
				graceCd = 0.0f;
				chargeCd = 0;
			}
		}
	}
	
	//this is to avoid resetting the charge status when reequipping this weapon
	@Override
	public void equip(PlayState state) {}
	
	@Override
	public boolean reload(float delta) { 
		reloading = false;
		return false;
	}
	
	//custom charging text to convey overheat information
	@Override
	public String getChargeText() {

		if (((Player) user).getChargePercent() < chargeBonusThreshold) {
			return "";
		} else {
			return HText.HIT_IT.text();
		}
	}

	@Override
	public float getBotRangeMax() { return bonusProjectileSpeed * lifespan; }
}
