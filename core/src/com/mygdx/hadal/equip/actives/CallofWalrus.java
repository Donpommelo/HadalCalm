package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.statuses.StatusComposite;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.Static;
import com.mygdx.hadal.utils.Stats;

/**
 * @author Hedeon Holkner
 */
public class CallofWalrus extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 12.0f;
	
	private static final float buffDuration = 4.0f;
	
	private static final float atkSpdBuff = 0.15f;
	private static final float damageBuff = 0.3f;

	private static final Vector2 projectileSize = new Vector2(400, 400);
	private static final float duration = 0.4f;

	public CallofWalrus(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC18_BUFF.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);

		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), projectileSize, duration, new Vector2(),
				(short) 0, false, false, user.getPlayer(), Sprite.NOTHING);
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new Static(state, hbox, user));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.RING, 0.0f, 1.0f));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {

			private final Array<HadalData> buffed = new Array<>();
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getType().equals(UserDataType.BODY)) {
						BodyData ally = (BodyData) fixB;
						if (ally.getSchmuck().getHitboxfilter() == user.getPlayer().getHitboxfilter()) {
							if (!buffed.contains(fixB, false)) {
								buffed.add(fixB);
								ally.addStatus(new StatusComposite(state, buffDuration, false, user, ally,
										new StatChangeStatus(state, Stats.TOOL_SPD, atkSpdBuff, ally),
										new StatChangeStatus(state, Stats.DAMAGE_AMP, damageBuff, ally)));

								new ParticleEntity(state, ally.getSchmuck(), Particle.LIGHTNING_CHARGE, 1.0f, buffDuration,
										true, SyncType.CREATESYNC).setColor(HadalColor.RED);
							}
						}
					}
				}
			}
		});
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
