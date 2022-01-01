package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.OrbitUser;

/**
 * @author Toberdash Twaldbaum
 */
public class OrbitalShield extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 18.0f;
	
	private static final Vector2 projSize = new Vector2(25, 25);
	private static final Vector2 spriteSize = new Vector2(40, 40);

	private static final float projDamage= 27.0f;
	private static final float projKnockback= 25.0f;
	private static final float projLifespan= 5.0f;

	private static final float projSpeed= 180.0f;
	private static final float projRange= 5.0f;

	public OrbitalShield(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.ORBITAL_STAR.initiateSyncedAttackMulti(state, user.getPlayer(), new Vector2[]{}, new Vector2[]{});
	}

	public static Hitbox[] createOrbitals(PlayState state, Schmuck user) {

		Hitbox[] hboxes = new Hitbox[4];
		hboxes[0] = createOrbital(state, user, 0);
		hboxes[1] = createOrbital(state, user, 90);
		hboxes[2] = createOrbital(state, user, 180);
		hboxes[3] = createOrbital(state, user, 270);

		SoundEntity sound = new SoundEntity(state, hboxes[0], SoundEffect.MAGIC25_SPELL, projLifespan, 1.0f, 1.0f,
				true, true, SyncType.NOSYNC);

		if (!state.isServer()) {
			((ClientState) state).addEntity(sound.getEntityID(), sound, false, ClientState.ObjectLayer.EFFECT);
		}

		return hboxes;
	}
	
	private static Hitbox createOrbital(PlayState state, Schmuck user, float startAngle) {
		Hitbox hbox = new RangedHitbox(state, user.getPixelPosition(), projSize, projLifespan, new Vector2(),
				user.getHitboxfilter(), true, true, user, Sprite.STAR_WHITE);
		hbox.setSpriteSize(spriteSize);
		hbox.makeUnreflectable();
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), projDamage, projKnockback, DamageTypes.MAGIC).setStaticKnockback(true).setRepeatable(true));
		hbox.addStrategy(new OrbitUser(state, hbox, user.getBodyData(), startAngle, projRange, projSpeed));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.STAR_TRAIL, 0.0f, 1.0f)
		.setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public float getBotRangeMin() { return 9.0f; }
}
