package com.mygdx.hadal.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.ExplosionDefault;
import com.mygdx.hadal.strategies.hitbox.Static;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, meteors and bees.
 * @author Lotticelli Lamhock
 */
public class WeaponUtils {

	private static final float SELF_DAMAGE_REDUCTION = 0.5f;
	private static final float EXPLOSION_SPRITE_SCALE = 1.5f;
	private static final Sprite BOOM_SPRITE = Sprite.BOOM;
	public static void createExplosion(PlayState state, Vector2 startPos, float size, Schmuck user, float explosionDamage,
									   float explosionKnockback, short filter, boolean synced, DamageSource source) {
		
		float newSize = size * (1 + user.getBodyData().getStat(Stats.EXPLOSION_SIZE));

		//this prevents players from damaging allies with explosives in the hub
		short actualFilter = filter;

		if (user.getHitboxFilter() == BodyConstants.PLAYER_HITBOX && !state.getMode().isFriendlyFire()) {
			actualFilter = BodyConstants.PLAYER_HITBOX;
		}

		Hitbox hbox = new Hitbox(state, startPos, new Vector2(newSize, newSize), 0.4f, new Vector2(),
			actualFilter, true, false, user, BOOM_SPRITE);
		hbox.setSyncDefault(synced);

		hbox.setSpriteSize(new Vector2(newSize, newSize).scl(EXPLOSION_SPRITE_SCALE));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), explosionDamage, explosionKnockback,
				SELF_DAMAGE_REDUCTION, source, DamageTag.EXPLOSIVE));

		if (!state.isServer()) {
			((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
		}
	}

	public static void createMeteors(PlayState state, Schmuck user, Vector2 startPosition, int meteorNumber,
									 float meteorInterval, float baseDamage, float spread) {
		float[] extraFields = new float[3 + meteorNumber];
		extraFields[0] = meteorInterval;
		extraFields[1] = baseDamage;
		extraFields[2] = meteorNumber;

		for (int i = 0; i < meteorNumber; i++) {
			extraFields[i + 3] = (MathUtils.random() -  0.5f) * spread;
		}

		SyncedAttack.METEOR_STRIKE.initiateSyncedAttackSingle(state, user, startPosition, new Vector2(), extraFields);
	}

	public static void createVine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelo,
								  int vineNum, int splitNum, SyncedAttack syncedAttack) {

		float[] extraFields = new float[3 + vineNum];
		extraFields[0] = vineNum;
		extraFields[1] = splitNum;
		for (int i = 0; i < vineNum; i++) {
			extraFields[i + 2] = MathUtils.random();
		}

		syncedAttack.initiateSyncedAttackSingle(state, user, startPosition, startVelo, extraFields);
	}

}
