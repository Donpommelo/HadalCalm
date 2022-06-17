package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.MoveState;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Impermeable;
import com.mygdx.hadal.statuses.ResetVelocity;
import com.mygdx.hadal.strategies.hitbox.*;

/**
 * @author Mildsinger Motherford
 */
public class GhostStep extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 3.0f;

	private static final float baseDamage = 20.0f;
	private static final Vector2 hitboxSize = new Vector2(90, 120);
	private static final float recoil = 75.0f;
	private static final float lifespan = 0.20f;

	public GhostStep(Schmuck user) {
		super(user, usecd, usedelay, maxCharge);
	}

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.GHOST_STEP.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), weaponVelo);
	}

	public static Hitbox createDematerialization(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.WOOSH.playUniversal(state, user.getPixelPosition(), 1.0f, false);

		int direction;

		if (user.getMoveState().equals(MoveState.MOVE_LEFT)) {
			direction = -1;
		} else if (user.getMoveState().equals(MoveState.MOVE_RIGHT)) {
			direction = 1;
		} else if (startVelocity.x > 0) {
			direction = 1;
		} else {
			direction = -1;
		}

		user.getBodyData().addStatus(new Impermeable(state, lifespan, user.getBodyData(), user.getBodyData())
				.setClientIndependent(true));
		user.getBodyData().addStatus(new ResetVelocity(state, lifespan, user.getBodyData(), user.getBodyData(), user.getLinearVelocity())
				.setClientIndependent(true));

		user.setLinearVelocity(recoil * direction, 0);

		Hitbox hbox = new Hitbox(state, startPosition, hitboxSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.NOTHING);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, 0.0f, DamageSource.GHOST_STEP,
				DamageTag.MAGIC));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.SHADOW_PATH, 0.0f, 1.0f)
			.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) maxCharge),
				String.valueOf((int) baseDamage)};
	}
}