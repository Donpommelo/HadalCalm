package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.KillerNotes;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

public class KillerBeat extends RangedWeapon {

	private static final int CLIP_SIZE = 240;
	private static final int AMMO_SIZE = 0;
	private static final float SHOOT_CD = 0.18f;
	private static final float RELOAD_TIME = 1.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 24.0f;
	private static final float BONUS_PROJECTILE_SPEED = 60.0f;
	private static final float MAX_CHARGE = 1.0f;
	private static final float CHARGE_BONUS_THRESHOLD = 0.8f;
	private static final int BONUS_NUM_NOTES = 5;
	private static final int NOTE_SPREAD = 5;

	private static final Vector2 PROJECTILE_SIZE = KillerNotes.PROJECTILE_SIZE;
	private static final float LIFESPAN = KillerNotes.LIFESPAN;
	private static final float BASE_DAMAGE = KillerNotes.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_NEMATOCYTEARM;
	private static final Sprite EVENT_SPRITE = Sprite.P_NEMATOCYTEARM;

	private static final int[] NOTE_ARRAY = {0, 1, 2, 3, 4, 5, 6};
	private static final IntArray NOTES = new IntArray(NOTE_ARRAY);

	public KillerBeat(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
		setCharging(true);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		if (chargeCd < getChargeTime() * CHARGE_BONUS_THRESHOLD) {
			int randNote = MathUtils.random(6);
			SyncedAttack.KILLER_NOTES.initiateSyncedAttackMulti(state, user, startVelocity, new Vector2[]{startPosition},
					new Vector2[]{setNoteVelocity(randNote, startVelocity)}, randNote);
		} else {
			NOTES.shuffle();
			Vector2[] positions = new Vector2[BONUS_NUM_NOTES];
			Vector2[] velocities = new Vector2[BONUS_NUM_NOTES];
			float[] extraFields = new float[BONUS_NUM_NOTES];
			for (int i = 0; i < BONUS_NUM_NOTES; i++) {
				Vector2 finalVelocity = new Vector2(startVelocity);
				finalVelocity.setLength(BONUS_PROJECTILE_SPEED);
				positions[i] = startPosition;
				velocities[i] = setNoteVelocity(NOTES.get(i), finalVelocity);
				extraFields[i] = NOTES.get(i);
			}
			SyncedAttack.KILLER_NOTES.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities, extraFields);
		}
		graceCd = 0;
		chargeCd = 0.0f;
	}

	private Vector2 setNoteVelocity(int note, Vector2 startVelocity) {
		Vector2 noteVelo = new Vector2();
		switch (note) {
			case 0 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - (NOTE_SPREAD * 3));
			case 1 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - (NOTE_SPREAD * 2));
			case 2 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() - NOTE_SPREAD);
			case 3 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg());
			case 4 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + NOTE_SPREAD);
			case 5 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + (NOTE_SPREAD * 2));
			case 6 -> noteVelo.set(startVelocity).setAngleDeg(startVelocity.angleDeg() + (NOTE_SPREAD * 3));
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

		if (user.getUiHelper().getChargePercent() < CHARGE_BONUS_THRESHOLD) {
			return "";
		} else {
			return UIText.HIT_IT.text();
		}
	}

	@Override
	public float getBotRangeMax() { return BONUS_PROJECTILE_SPEED * LIFESPAN; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(MAX_CHARGE),
				String.valueOf(SHOOT_CD)};
	}
}
