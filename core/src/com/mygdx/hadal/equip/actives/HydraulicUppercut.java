package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.statuses.Invulnerability;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.constants.Stats;

/**
 * @author Pronghort Phunzales
 */
public class HydraulicUppercut extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;
	
	private static final Vector2 HITBOX_SIZE = new Vector2(150, 150);
	private static final float BASE_DAMAGE = 60.0f;
	private static final float LIFESPAN = 0.5f;
	private static final float KNOCKBACK = 75.0f;
	private static final float RECOIL = 180.0f;
	private static final float PARTICLE_LIFESPAN = 0.6f;

	public HydraulicUppercut(Schmuck user) { super(user, MAX_CHARGE); }
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.HYDRAUlIC_UPPERCUT.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(), weaponVelo);
	}

	public static Hitbox createHydraulicUppercut(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.WOOSH.playSourced(state, user.getPixelPosition(), 1.0f);

		boolean right = startVelocity.x > 0;

		Particle particle = Particle.MOREAU_LEFT;
		if (right) {
			particle = Particle.MOREAU_RIGHT;
		}

		if (user instanceof Player player) {
			ParticleEntity particles = new ParticleEntity(user.getState(), user, particle, 1.5f, 1.0f,
					true, SyncType.NOSYNC)
					.setScale(0.5f).setPrematureOff(PARTICLE_LIFESPAN)
					.setColor(WeaponUtils.getPlayerColor(player));
			if (!state.isServer()) {
				((ClientState) state).addEntity(particles.getEntityID(), particles, false, ClientState.ObjectLayer.EFFECT);
			}
		}

		user.getBodyData().addStatus(new Invulnerability(state, 0.9f, user.getBodyData(), user.getBodyData()));
		user.getBodyData().addStatus(new StatChangeStatus(state, 0.5f, Stats.AIR_DRAG, 6.0f, user.getBodyData(), user.getBodyData())
				.setClientIndependent(true));

		user.pushMomentumMitigation(0, RECOIL);

		Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.IMPACT);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallParticles(state, hbox, user.getBodyData() , Particle.SPARKS).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
				DamageSource.HYDRAULIC_UPPERCUT, DamageTag.MELEE).setStaticKnockback(true));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), new Vector2(), new Vector2()));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.KICK1, 1.0f, true).setSynced(false));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(LIFESPAN)};
	}
}
