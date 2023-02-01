package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Player;
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

	private static final float MAX_CHARGE = 8.0f;
	
	private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);
	private static final float DURATION = 0.5f;
	private static final float PROJECTILE_DAMAGE = 24.0f;
	private static final float PROJECTILE_KB = 15.0f;
	private static final float SLOW_DURATION = 5.0f;
	private static final float SLOW_SLOW = 0.75f;
	
	public MarineSnowglobe(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.MARINE_SNOW.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), new Vector2());
	}

	public static Hitbox createMarineSnow(PlayState state, Schmuck user, Vector2 startPosition) {
		SoundEffect.FREEZE_IMPACT.playSourced(state, startPosition, 0.9f, 0.5f);

		Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), PROJECTILE_SIZE, DURATION, new Vector2(), user.getHitboxFilter(),
				false, false, user, Sprite.NOTHING);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), PROJECTILE_DAMAGE, PROJECTILE_KB,
				DamageSource.MARINE_SNOWGLOBE, DamageTag.RANGED));
		hbox.addStrategy(new ContactUnitSlow(state, hbox, user.getBodyData(), SLOW_DURATION, SLOW_SLOW, Particle.ICE_CLOUD));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.ICE_CLOUD, 0.0f, 2.0f)
				.setParticleSize(25.0f).setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public float getBotRangeMin() { return 5.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) PROJECTILE_DAMAGE),
				String.valueOf((int) SLOW_DURATION),
				String.valueOf((int) (SLOW_SLOW * 100))};
	}
}
