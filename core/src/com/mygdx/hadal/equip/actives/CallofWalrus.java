package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.constants.UserDataType;
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
import com.mygdx.hadal.constants.Stats;

/**
 * @author Hedeon Holkner
 */
public class CallofWalrus extends ActiveItem {

	private static final float MAX_CHARGE = 12.0f;
	
	private static final float BUFF_DURATION = 4.0f;
	private static final float ATK_SPD_BUFF = 0.15f;
	private static final float DAMAGE_BUFF = 0.3f;

	private static final Vector2 PROJECTILE_SIZE = new Vector2(400, 400);
	private static final float DURATION = 0.4f;

	public CallofWalrus(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC18_BUFF.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);

		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getPixelPosition(), PROJECTILE_SIZE, DURATION, new Vector2(),
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
					if (UserDataType.BODY.equals(fixB.getType())) {
						BodyData ally = (BodyData) fixB;
						if (ally.getSchmuck().getHitboxFilter() == user.getPlayer().getHitboxFilter()) {
							if (!buffed.contains(fixB, false)) {
								buffed.add(fixB);
								ally.addStatus(new StatusComposite(state, BUFF_DURATION, false, user, ally,
										new StatChangeStatus(state, Stats.TOOL_SPD, ATK_SPD_BUFF, ally),
										new StatChangeStatus(state, Stats.DAMAGE_AMP, DAMAGE_BUFF, ally)));

								new ParticleEntity(state, ally.getSchmuck(), Particle.LIGHTNING_CHARGE, 1.0f, BUFF_DURATION,
										true, SyncType.CREATESYNC).setColor(HadalColor.RED);
							}
						}
					}
				}
			}
		});
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf(DURATION),
				String.valueOf((int) (ATK_SPD_BUFF * 100)),
				String.valueOf((int) (DAMAGE_BUFF * 100))};
	}
}
