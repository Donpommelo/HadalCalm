package com.mygdx.hadal.battle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
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
		System.out.println(user.getHitboxFilter());
		if (user.getHitboxFilter() == Constants.PLAYER_HITBOX && state.getMode().isHub()) {
			actualFilter = Constants.PLAYER_HITBOX;
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

	public static void createExplodingReticle(PlayState state, Vector2 startPos, Schmuck user, float reticleSize,
											  float reticleLifespan, float explosionDamage, float explosionKnockback,
											  int explosionRadius, DamageSource source) {
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(reticleSize, reticleSize), reticleLifespan,
			new Vector2(), user.getHitboxFilter(), true, false, user, Sprite.CROSSHAIR);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.EVENT_HOLO, 0.0f, 1.0f)
				.setParticleSize(40.0f).setParticleColor(HadalColor.HOT_PINK).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				user.getHitboxFilter(), true, source));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
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

	private static final Sprite[] VINE_SPRITES = {Sprite.VINE_A, Sprite.VINE_C, Sprite.VINE_D};
	public static void createVine(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelo,
								  int vineNum, float lifespan, float vineDamage, float vineKB, int spreadMin,
								  int spreadMax, int bendLength, int bendSpread,  Vector2 vineInvisSize,
								  Vector2 vineSize, Vector2 vineSpriteSize, int splitNum) {
		SoundEffect.ATTACK1.playUniversal(state, user.getPixelPosition(), 0.4f, 0.5f, false);

		//create an invisible hitbox that makes the vines as it moves
		RangedHitbox hbox = new RangedHitbox(state, startPosition, vineInvisSize, lifespan, startVelo, user.getHitboxFilter(),
			false, false, user, Sprite.NOTHING);
		hbox.setSyncDefault(false);
		hbox.makeUnreflectable();
		hbox.setRestitution(1.0f);

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
				if (vineSize.x < displacement) {
					if (lastPositionTemp.isZero()) {
						lastPosition.set(entityLocation);
					} else {
						lastPosition.add(new Vector2(lastPosition).sub(lastPositionTemp).nor().scl((displacement - vineSize.x) / PPM));
					}
					displacement = 0.0f;

					int randomIndex = MathUtils.random(VINE_SPRITES.length - 1);
					Sprite projSprite = VINE_SPRITES[randomIndex];

					RangedHitbox vine = new RangedHitbox(state, lastPosition, vineSize, lifespan, new Vector2(),
						user.getHitboxFilter(), true, true, creator.getSchmuck(),
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
					vine.setSpriteSize(vineSpriteSize);
					vine.setEffectsMovement(false);

					vine.addStrategy(new ControllerDefault(state, vine, user.getBodyData()));
					vine.addStrategy(new ContactUnitSound(state, vine, user.getBodyData(), SoundEffect.STAB, 0.6f, true));
					vine.addStrategy(new DamageStandard(state, vine, user.getBodyData(), vineDamage, vineKB,
							DamageSource.ENEMY_ATTACK , DamageTag.RANGED).setStaticKnockback(true));
					vine.addStrategy(new CreateParticles(state, vine, user.getBodyData(), Particle.DANGER_RED, 0.0f, 1.0f).setParticleSize(90.0f));
					vine.addStrategy(new DieParticles(state, vine, user.getBodyData(), Particle.PLANT_FRAG));
					vine.addStrategy(new Static(state, vine, user.getBodyData()));

					vineCount++;
					vineCountTotal++;
					if (vineCount >= nextBend) {

						//hbox's velocity changes randomly to make vine wobble
						hbox.setLinearVelocity(hbox.getLinearVelocity().rotateDeg((bendRight ? -1 : 1) * MathUtils.random(spreadMin, spreadMax)));
						bendRight = !bendRight;
						vineCount = 0;
						nextBend = bendLength + (MathUtils.random(-bendSpread, bendSpread + 1));
					}
					if (vineCountTotal > vineNum) {
						hbox.die();
					}
				}
			}

			@Override
			public void die() {

				if (0 < splitNum) {
					//when vine dies, it creates 2 vines that branch in separate directions
					float newDegrees = hbox.getLinearVelocity().angleDeg() + (MathUtils.random(spreadMin, spreadMax));
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					WeaponUtils.createVine(state, user, hbox.getPixelPosition(), angle, vineNum, lifespan,
						vineDamage, vineKB, spreadMin, spreadMax, 2, 1,
						vineInvisSize, vineSize, vineSpriteSize, splitNum - 1);

					newDegrees = hbox.getLinearVelocity().angleDeg() - (MathUtils.random(spreadMin, spreadMax));
					angle.set(hbox.getLinearVelocity()).setAngleDeg(newDegrees);
					WeaponUtils.createVine(state, user, hbox.getPixelPosition(), angle, vineNum, lifespan,
						vineDamage, vineKB, spreadMin, spreadMax, 2, 1,
						vineInvisSize, vineSize, vineSpriteSize, splitNum - 1);
				}
			}
		});
	}

	/**
	 * This method returns a player's "color" corresponding to their team color or their character with no team.
	 * This is used to color code player name as well as for streak particle coloring
	 */
	public static Vector3 getPlayerColor(Player player) {

		//return empty vector if player's data has not been created yet.
		if (null != player.getPlayerData()) {
			Loadout loadout = player.getPlayerData().getLoadout();
			if (AlignmentFilter.NONE.equals(loadout.team)) {
				return loadout.character.getPalette().getIcon().getRGB();
			} else if (loadout.team.getPalette().getIcon().getRGB().isZero()) {
				return loadout.character.getPalette().getIcon().getRGB();
			} else {
				return loadout.team.getPalette().getIcon().getRGB();
			}
		} else {
			return new Vector3();
		}
	}

	private static final Vector3 rgb = new Vector3();
	/**
	 * This returns a string corresponding to a player's colored name. (optionally abridged)
	 * Used for kill feed messages and chat window names.
	 */
	public static String getPlayerColorName(Schmuck schmuck, int maxNameLen) {

		if (null == schmuck) { return ""; }

		if (schmuck instanceof Player player) {
			String displayedName = player.getName();

			if (displayedName.length() > maxNameLen) {
				displayedName = displayedName.substring(0, maxNameLen).concat("...");
			}

			//get the player's color and use color markup to add color tags.
			rgb.set(getPlayerColor(player));
			String hex = "#" + Integer.toHexString(Color.rgb888(rgb.x, rgb.y, rgb.z));
			return "[" + hex + "]" + displayedName + "[]";
		} else {
			return schmuck.getName();
		}
	}

	public static String getColorName(HadalColor color, String name) {
		String hex = "#" + Integer.toHexString(Color.rgb888(color.getColor()));
		return "[" + hex + "]" + name + "[]";
	}
}
