package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactUnitSound;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.utils.WorldUtil;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * @author Louhaha Losemary
 */
public class AnchorSmash extends ActiveItem {

	private static final float MAX_CHARGE = 16.0f;

	private static final Vector2 PROJECTILE_SIZE = new Vector2(300, 259);
	private static final float LIFESPAN = 4.0f;
	private static final float PROJECTILE_SPEED = 60.0f;

	private static final float RANGE = 1800.0f;
	private static final float BASE_DAMAGE = 80.0f;
	private static final float KNOCKBACK = 50.0f;

	private static final Sprite PROJ_SPRITE = Sprite.ANCHOR;

	public AnchorSmash(Schmuck user) {
		super(user, MAX_CHARGE);
	}

	private float shortestFraction;
	private final Vector2 originPt = new Vector2();
	private final Vector2 endPt = new Vector2();

	@Override
	public void useItem(PlayState state, PlayerBodyData user) {

		originPt.set(mouseLocation).scl(1 / PPM);
		endPt.set(originPt).add(0, -RANGE);
		shortestFraction = 1.0f;

		if (WorldUtil.preRaycastCheck(originPt, endPt)) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {
				if (fixture.getFilterData().categoryBits == Constants.BIT_WALL && fraction < shortestFraction) {
					shortestFraction = fraction;
					return fraction;
				}
				return -1.0f;
			}, originPt, endPt);
		}

		endPt.set(originPt).add(0, -RANGE * shortestFraction).scl(PPM);
		originPt.set(endPt).add(0, RANGE);

		SyncedAttack.ANCHOR.initiateSyncedAttackSingle(state, user.getPlayer(), originPt, new Vector2(), endPt.x, endPt.y);
	}

	public static Hitbox createAnchor(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {

		Vector2 endPt = new Vector2();
		if (extraFields.length > 1) {
			endPt.set(extraFields[0], extraFields[1]);
		}

		Hitbox hbox = new Hitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN, new Vector2(0, -PROJECTILE_SPEED),
				user.getHitboxFilter(), true, false, user, PROJ_SPRITE);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		hbox.makeUnreflectable();

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), BASE_DAMAGE, KNOCKBACK,
				DamageSource.ANCHOR_SMASH, DamageTag.WHACKING, DamageTag.MAGIC));
		hbox.addStrategy(new ContactUnitSound(state, hbox, user.getBodyData(), SoundEffect.SLASH, 0.8f, true).setSynced(false));
		hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.FALLING, 0.5f, false)
				.setPitch(0.75f).setSyncType(SyncType.NOSYNC));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private boolean landed;
			private final Vector2 hboxLocation = new Vector2();
			@Override
			public void controller(float delta) {
				hboxLocation.set(hbox.getPixelPosition());
				if (hboxLocation.y - hbox.getSize().y / 2 <= endPt.y) {
					hbox.setLinearVelocity(0, 0);

					if (!landed) {
						landed = true;

						SoundEffect.METAL_IMPACT_2.playSourced(state, hboxLocation, 1.0f);
						ParticleEntity particle = new ParticleEntity(state, hboxLocation, Particle.BOULDER_BREAK,
								0.5f, true, SyncType.NOSYNC);

						if (!state.isServer()) {
							((ClientState) state).addEntity(particle.getEntityID(), particle, false, ClientState.ObjectLayer.EFFECT);
						}
					}
				}
			}
		});

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
			String.valueOf((int) MAX_CHARGE),
			String.valueOf((int) BASE_DAMAGE)};
	}
}
