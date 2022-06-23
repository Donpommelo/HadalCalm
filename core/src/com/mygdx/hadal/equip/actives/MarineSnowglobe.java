package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSlow;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.Static;

/**
 * @author Plungfisher Plubdul
 */
public class MarineSnowglobe extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 8.0f;
	
	private static final Vector2 projectileSize = new Vector2(400, 400);
	private static final float duration = 0.5f;

	private static final float projectileDamage = 24.0f;
	private static final float projectileKB = 15.0f;
	
	private static final float slowDuration = 5.0f;
	private static final float slowSlow = 0.75f;
	
	public MarineSnowglobe(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.MARINE_SNOW.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), new Vector2());
	}

	public static Hitbox createMarineSnow(PlayState state, Schmuck user, Vector2 startPosition) {
		SoundEffect.FREEZE_IMPACT.playSourced(state, startPosition, 0.9f, 0.5f);

		Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), projectileSize, duration, new Vector2(), user.getHitboxfilter(),
				false, false, user, Sprite.NOTHING);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), projectileDamage, projectileKB,
				DamageSource.MARINE_SNOWGLOBE, DamageTag.RANGED));
		hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), slowDuration, slowSlow, Particle.ICE_CLOUD));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD, 0.0f, 2.0f)
				.setParticleSize(25.0f).setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public float getUseDuration() { return duration; }

	@Override
	public float getBotRangeMin() { return 5.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf((int) projectileDamage),
				String.valueOf((int) slowDuration),
				String.valueOf((int) (slowSlow * 100))};
	}
}
