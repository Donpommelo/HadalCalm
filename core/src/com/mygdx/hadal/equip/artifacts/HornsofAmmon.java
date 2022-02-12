package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.Status;

import static com.mygdx.hadal.utils.Constants.PRIORITY_PROC;

public class HornsofAmmon extends Artifact {

	private static final int slotCost = 3;
	
	private static final float threshold = 5.0f;
	private static final float invulnDura = 1.0f;
	
	public HornsofAmmon() {
		super(slotCost);
	}

	@Override
	public void loadEnchantments(PlayState state, PlayerBodyData p) {
		enchantment = new Status(state, p) {

			@Override
			public float onReceiveDamage(float damage, BodyData perp, Hitbox damaging, DamageTypes... tags) {
				if (damage > threshold) {
					if (p.getStatus(Invulnerability.class) == null) {
						SoundEffect.MAGIC18_BUFF.playUniversal(state, p.getSchmuck().getPixelPosition(), 0.5f, false);

						p.receiveDamage(damage, new Vector2(), perp, false, damaging, tags);
						p.addStatus(new Invulnerability(state, invulnDura, p, p));
						return 0;
					}					
				}
				return damage;
			}
		}.setPriority(PRIORITY_PROC);
	}
}
