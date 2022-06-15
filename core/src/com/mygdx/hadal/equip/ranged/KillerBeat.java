package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.text.UIText;

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
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
		setCharging(true);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		if (chargeCd < getChargeTime() * chargeBonusThreshold) {
			int randNote = MathUtils.random(6);
			SyncedAttack.KILLER_NOTES.initiateSyncedAttackMulti(state, user, startVelocity, new Vector2[]{startPosition},
					new Vector2[]{setNoteVelocity(randNote, startVelocity)}, randNote);
		} else {
			notes.shuffle();
			Vector2[] positions = new Vector2[bonusNumNotes];
			Vector2[] velocities = new Vector2[bonusNumNotes];
			float[] extraFields = new float[bonusNumNotes];
			for (int i = 0; i < bonusNumNotes; i++) {
				Vector2 finalVelocity = new Vector2(startVelocity);
				finalVelocity.setLength(bonusProjectileSpeed);
				positions[i] = startPosition;
				velocities[i] = setNoteVelocity(notes.get(i), finalVelocity);
				extraFields[i] = notes.get(i);
			}
			SyncedAttack.KILLER_NOTES.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities, extraFields);
		}
		graceCd = 0;
		chargeCd = 0.0f;
	}

	public static Hitbox[] createKillerNotes(PlayState state, Schmuck user, Vector2 weaponVelocity, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];
		user.recoil(weaponVelocity, recoil);

		if (startPosition.length != 0) {
			for (int i = 0; i < startPosition.length; i++) {
				int note = extraFields.length <= i ? 0 : (int) extraFields[i];
				switch (note) {
					case 0 -> SoundEffect.PIANO_C.playSourced(state, startPosition[i], 0.5f, 1.0f);
					case 1 -> SoundEffect.PIANO_D.playSourced(state, startPosition[i], 0.5f, 1.0f);
					case 2 -> SoundEffect.PIANO_F.playSourced(state, startPosition[i], 0.5f, 1.0f);
					case 3 -> SoundEffect.PIANO_G.playSourced(state, startPosition[i], 0.5f, 1.0f);
					case 4 -> SoundEffect.PIANO_A.playSourced(state, startPosition[i], 0.5f, 1.0f);
					case 5 -> SoundEffect.PIANO_B.playSourced(state, startPosition[i], 0.5f, 1.0f);
					case 6 -> SoundEffect.PIANO_C2.playSourced(state, startPosition[i], 0.5f, 1.0f);
				}

				Hitbox hbox = new RangedHitbox(state, startPosition[i], projectileSize, lifespan, startVelocity[i], user.getHitboxfilter(),
						true, true, user, projSprite);
				hbox.setDurability(2);

				hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
				hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
				hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
						DamageSource.KILLER_BEAT, DamageTag.ENERGY, DamageTag.RANGED, DamageTag.SOUND));
				hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.RING_TRAIL, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.NOTE_IMPACT).setSyncType(SyncType.NOSYNC));
				hbox.addStrategy(new ContactUnitParticles(state, hbox, user.getBodyData(), Particle.NOTE_IMPACT).setSyncType(SyncType.NOSYNC));
				hboxes[i] = hbox;
			}
		}
		return hboxes;
	}

	private Vector2 setNoteVelocity(int note, Vector2 startVelocity) {
		Vector2 noteVelo = new Vector2();
		switch (note) {
			case 0 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - (noteSpread * 3));
			case 1 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - (noteSpread * 2));
			case 2 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - noteSpread);
			case 3 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg());
			case 4 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + noteSpread);
			case 5 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + (noteSpread * 2));
			case 6 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + (noteSpread * 3));
		}
		return noteVelo;
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
			return UIText.HIT_IT.text();
		}
	}

	@Override
	public float getBotRangeMax() { return bonusProjectileSpeed * lifespan; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(maxCharge),
				String.valueOf(shootCd)};
	}
}
