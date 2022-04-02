package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.event.HealingArea;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_PROC;

public class Number1BossMug extends Artifact {

	private static final int slotCost = 1;
	
	private static final float procCd = 10.0f;

	private static final Vector2 fieldSize = new Vector2(280, 280);
	private static final float fieldHeal = 0.2f;
	private static final float healDuration = 5.0f;

	public Number1BossMug() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			private float procCdCount = procCd;
			@Override
			public void timePassing(float delta) {
				if (procCdCount < procCd) {
					procCdCount += delta;
				}
			}

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {

				if (procCdCount >= procCd && damage > 0) {
					procCdCount = 0;

					new SoundEntity(state, new HealingArea(state, p.getSchmuck().getPixelPosition(), fieldSize, fieldHeal, healDuration, p.getSchmuck(), (short) 0),
							SoundEffect.MAGIC21_HEAL, healDuration, 0.25f, 1.0f, true, true, SyncType.CREATESYNC);
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}
}
