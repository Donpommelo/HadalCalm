package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Scrapripper extends MeleeWeapon {

	private static final float SHOOT_CD = 0.45f;
	private static final float SHOOT_DELAY = 0.0f;
	private static final float BASE_DAMAGE = 60.0f;
	private static final Vector2 HITBOX_SIZE = new Vector2(200, 120);
	private static final Vector2 HITBOX_SPRITE_SIZE = new Vector2(300, 180);
	private static final float KNOCKBACK = 25.0f;
	private static final float LIFESPAN = 0.25f;
	
	private static final Sprite WEAPON_SPRITE = Sprite.MT_SCRAPRIPPER;
	private static final Sprite EVENT_SPRITE = Sprite.P_SCRAPRIPPER;

	public Scrapripper(Schmuck user) {
		super(user, SHOOT_CD, SHOOT_DELAY, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.SCRAPRIP.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createScraprip(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.WOOSH.playSourced(state, startPosition, 1.0f);

		Hitbox hbox = new Hitbox(state, startPosition, HITBOX_SIZE, LIFESPAN, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.IMPACT);
		hbox.setSpriteSize(HITBOX_SPRITE_SIZE);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK, DamageSource.SCRAPRIPPER,
				DamageTag.MELEE).setConstantKnockback(true, startVelocity));
		hbox.addStrategy(new FixedToEntity(state, hbox, user.getBodyData(), startVelocity, startVelocity.nor().scl(HITBOX_SIZE.x / 2 / PPM)));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.8f, true).setSynced(false));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EXPLOSION, 0.0f, 0.2f)
				.setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public float getBotRangeMax() { return 6.25f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(SHOOT_CD)};
	}
}
