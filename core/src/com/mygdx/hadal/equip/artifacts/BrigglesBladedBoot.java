package com.mygdx.hadal.equip.artifacts;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.FixedToEntity;
import com.mygdx.hadal.utils.Constants;

public class BrigglesBladedBoot extends Artifact {

	private static final int statusNum = 1;
	private static final int slotCost = 1;
	
	private static final float baseDamage = 45.0f;
	private static final float knockback = 15.0f;

	private static final float recoil = 40.0f;

	private static final Vector2 size = new Vector2(28, 5);
	private static final Vector2 position = new Vector2(0, -1.5f);
	
	public BrigglesBladedBoot() {
		super(slotCost, statusNum);
	}

	@Override
	public Status[] loadEnchantments(PlayState state, BodyData b) {
		enchantment[0] = new Status(state, b) {
			
			private Hitbox hbox;
			private boolean created;
			
			@Override
			public void onRemove() {
				if (hbox != null) {
					if (hbox.isAlive()) {
						hbox.die();
					}
					created = false;
				}
			}
			
			
			@Override
			public void timePassing(float delta) {
				if (!created) {
					created = true;
					
					hbox = new Hitbox(state, inflicted.getSchmuck().getPixelPosition(), size, 0, new Vector2(), inflicted.getSchmuck().getHitboxfilter(), true, false, inflicted.getSchmuck(), Sprite.NOTHING);
					hbox.makeUnreflectable();
					hbox.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));
					
					hbox.addStrategy(new FixedToEntity(state, hbox, inflicted, new Vector2(0, 0), position, false));
					hbox.addStrategy(new DamageStandard(state, hbox, inflicted, baseDamage, knockback, DamageTypes.WHACKING, DamageTypes.MELEE)
						.setStaticKnockback(true).setRepeatable(true));
					hbox.addStrategy(new HitboxStrategy(state, hbox, inflicted) {
						
						@Override
						public void onHit(HadalData fixB) {
							SoundEffect.KICK1.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 0.3f, false);
							inflicted.getSchmuck().pushMomentumMitigation(0, recoil);
						}
						
						@Override
						public void die() {
							hbox.queueDeletion();
						}
					});
				}
			}
		};
		return enchantment;
	}
}
