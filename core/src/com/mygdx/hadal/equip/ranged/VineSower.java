package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;

import static com.mygdx.hadal.utils.Constants.PPM;

public class VineSower extends RangedWeapon {

	private static final int clipSize = 2;
	private static final int ammoSize = 28;
	private static final float shootCd = 0.0f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.4f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 28.0f;
	private static final float knockback = 10.0f;
	private static final float projectileSpeed = 42.0f;
	private static final Vector2 projectileSize = new Vector2(40, 31);
	private static final float lifespan = 5.0f;

	private static final Sprite[] vineSprites = {Sprite.VINE_A, Sprite.VINE_C, Sprite.VINE_D};
	private static final Sprite projSprite = Sprite.SEED;
	private static final Sprite weaponSprite = Sprite.MT_SHOTGUN;
	private static final Sprite eventSprite = Sprite.P_SHOTGUN;

	private static final float maxCharge = 0.4f;
	private static final int minVineNum = 4;
	private static final int maxVineNum = 7;
	private static final float vineLifespan = 1.25f;

	private static final Vector2 seedSize = new Vector2(45, 30);
	private static final float vineSpeed = 21.0f;
	private static final float vineDamage = 16.0f;
	private static final float vineKB = 20.0f;

	private static final int vineBendSpreadMin = 15;
	private static final int vineBendSpreadMax = 30;
	private static final int bendLength = 1;

	private static final Vector2 vineSize = new Vector2(40, 20);
	private static final Vector2 vineSpriteSize = new Vector2(60, 60);

	public VineSower(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount,true,
				weaponSprite, eventSprite, projectileSize.x, lifespan, maxCharge);
	}
	
	@Override
	public void mouseClicked(float delta, PlayState state, BodyData shooter, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, shooter, faction, mousePosition);

		if (reloading || getClipLeft() == 0) { return; }
		
		charging = true;
		
		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
		}
	}
	
	@Override
	public void execute(PlayState state, BodyData shooter) {}
	
	@Override
	public void release(PlayState state, BodyData bodyData) {
		super.execute(state, bodyData);
		charging = false;
		chargeCd = 0;
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.WOOSH.playUniversal(state, startPosition, 1.0f, 0.75f, false);

		final int finalVineNum = (int) (chargeCd / getChargeTime() * (maxVineNum - minVineNum) + minVineNum);

		RangedHitbox hbox = new RangedHitbox(state, startPosition, seedSize, lifespan, new Vector2(startVelocity), filter, true, true, user, projSprite);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR | Constants.BIT_DROPTHROUGHWALL));
		hbox.setGravity(1.0f);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.VINE_SOWER,
				DamageTag.RANGED));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			@Override
			public void die() {
				Vector2 finalVelo = new Vector2(hbox.getLinearVelocity()).nor().scl(vineSpeed);
				float[] extraFields = new float[7 + finalVineNum * 3];
				extraFields[0] = finalVineNum;
				extraFields[1] = 1;
				for (int i = 2; i < 7 + finalVineNum * 3; i++) {
					extraFields[i] = MathUtils.random(vineBendSpreadMin, vineBendSpreadMax);
				}

				SyncedAttack.VINE.initiateSyncedAttackSingle(state, user, hbox.getPixelPosition(), finalVelo, extraFields);
			}

			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getEntity().getMainFixture().getFilterData().categoryBits == Constants.BIT_DROPTHROUGHWALL) {
						hbox.die();
					}
				}
			}
		});
	}

	public static Hitbox createVine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields, boolean synced) {
		SoundEffect.ATTACK1.playSourced(state, user.getPixelPosition(), 0.4f, 0.5f);

		final int vineNum = extraFields.length > 1 ? (int) extraFields[0] : 0;
		final int splitNum = extraFields.length > 1 ? (int) extraFields[1] : 0;

		//create an invisible hitbox that makes the vines as it moves
		RangedHitbox hbox = new RangedHitbox(state, startPosition, seedSize, vineLifespan, startVelocity, user.getHitboxfilter(),
				false, false, user, Sprite.NOTHING);
		hbox.setPassability(Constants.BIT_WALL);
		hbox.makeUnreflectable();
		hbox.setRestitution(1.0f);
		hbox.setSyncDefault(false);
		hbox.setNoSyncedDelete(true);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {

			private final Vector2 lastPosition = new Vector2();
			private final Vector2 lastPositionTemp = new Vector2();
			private final Vector2 entityLocation = new Vector2();
			private int vineCount, vineCountTotal, nextBend;
			private boolean bendRight;
			private float displacement;
			private final Vector2 angle = new Vector2();
			@Override
			public void controller(float delta) {
				entityLocation.set(hbox.getPixelPosition());

				displacement += lastPosition.dst(entityLocation);
				lastPositionTemp.set(lastPosition);
				lastPosition.set(entityLocation);

				//after moving distance equal to a vine, the hbox spawns a vine with random sprite
				if (displacement > vineSize.x) {
					if (lastPositionTemp.isZero()) {
						lastPosition.set(entityLocation);
					} else {
						lastPosition.add(new Vector2(lastPosition).sub(lastPositionTemp).nor().scl((displacement - vineSize.x) / PPM));
					}
					displacement = 0.0f;

					int randomIndex = MathUtils.random(vineSprites.length - 1);
					Sprite projSprite = vineSprites[randomIndex];

					RangedHitbox vine = new RangedHitbox(state, lastPosition, vineSize, vineLifespan, new Vector2(),
							user.getHitboxfilter(), true, true, creator.getSchmuck(),
							vineCountTotal == vineNum && splitNum == 0 ? Sprite.VINE_B : projSprite) {

						private final Vector2 newPosition = new Vector2();
						@Override
						public void create() {
							super.create();

							//vines match hbox velocity but are drawn at an offset so they link together better
							float newAngle = MathUtils.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x);
							newPosition.set(getPosition()).add(new Vector2(hbox.getLinearVelocity()).nor().scl(vineSize.x / 2 / PPM));
							setTransform(newPosition.x, newPosition.y, newAngle);
						}
					};
					vine.setSyncDefault(false);
					vine.setSpriteSize(vineSpriteSize);
					vine.setEffectsMovement(false);

					vine.addStrategy(new ControllerDefault(state, vine, user.getBodyData()));
					vine.addStrategy(new ContactUnitSound(state, vine, user.getBodyData(), SoundEffect.STAB, 0.6f, true).setSynced(false));
					vine.addStrategy(new DamageStandard(state, vine, user.getBodyData(), vineDamage, vineKB,
							DamageSource.VINE_SOWER, DamageTag.RANGED).setStaticKnockback(true));
					vine.addStrategy(new CreateParticles(state, vine, user.getBodyData(), Particle.DANGER_RED, 0.0f, 1.0f)
							.setParticleSize(90.0f).setSyncType(SyncType.NOSYNC));
					vine.addStrategy(new DieParticles(state, vine, user.getBodyData(), Particle.PLANT_FRAG).setSyncType(SyncType.NOSYNC));
					vine.addStrategy(new Static(state, vine, user.getBodyData()));

					if (!state.isServer()) {
						((ClientState) state).addEntity(vine.getEntityID(), vine, false, ClientState.ObjectLayer.HBOX);
					}

					vineCount++;
					vineCountTotal++;
					if (vineCount >= nextBend) {
						if (extraFields.length > vineCountTotal + 1) {
							//hbox's velocity changes randomly to make vine wobble
							hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * extraFields[vineCountTotal + 1]));
							bendRight = !bendRight;
							vineCount = 0;
							nextBend = bendLength + (int) (extraFields[vineCountTotal + 1]) % 2 == 0 ? 0 : 1;
						}
					}

					if (vineCountTotal > vineNum) {
						hbox.die();
					}
				}
			}

			@Override
			public void die() {

				if (splitNum > 0) {
					//when vine dies, it creates 2 vines that branch in separate directions
					float newDegrees = hbox.getLinearVelocity().angleDeg() + extraFields[4 + vineNum];
					float[] extraFields1 = new float[2 + vineNum];
					float[] extraFields2 = new float[2 + vineNum];
					extraFields1[0] = vineNum;
					extraFields2[0] = vineNum;
					extraFields1[1] = 0;
					extraFields2[1] = 0;
					for (int i = 0; i < vineNum; i++) {
						extraFields1[2 + i] = extraFields[vineNum + 5 + i];
						extraFields2[2 + i] = extraFields[vineNum * 2 + 5 + i];
					}
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					createVine(state, user, hbox.getPixelPosition(), new Vector2(angle), extraFields1, false);

					newDegrees = hbox.getLinearVelocity().angleDeg() - extraFields[4 + vineNum];
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					createVine(state, user, hbox.getPixelPosition(), new Vector2(angle), extraFields2, false);
				}
			}
		});

		if (!state.isServer() && !synced) {
			((ClientState) state).addEntity(hbox.getEntityID(), hbox, false, ClientState.ObjectLayer.HBOX);
		}

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) vineDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(maxCharge)};
	}
}
