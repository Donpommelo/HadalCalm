package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Screech;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.SoundCreate;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.WorldUtil;

import static com.mygdx.hadal.constants.Constants.PPM;

public class Screecher extends RangedWeapon {

	private static final int CLIP_SIZE = 60;
	private static final int AMMO_SIZE = 240;
	private static final float SHOOT_CD = 0.15f;
	private static final float RELOAD_TIME = 1.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 12.0f;
	private static final int SPREAD = 1;

	private static final Vector2 PROJECTILE_SIZE = Screech.PROJECTILE_SIZE;
	private static final int RANGE = Screech.RANGE;
	private static final float LIFESPAN = Screech.LIFESPAN;
	private static final float BASE_DAMAGE = Screech.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;
	
	private SoundEntity screechSound;

	private float shortestFraction;
	
	public Screecher(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN);
	}

	private final Vector2 endPt = new Vector2();
	private final Vector2 newPosition = new Vector2();
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		
		//This is the max distance this weapon can shoot (hard coded to scale to weapon range modifiers)
		float distance = RANGE * (1 + user.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN));
		
		entityLocation.set(user.getPosition());
		endPt.set(entityLocation).add(new Vector2(startVelocity).nor().scl(distance));
		shortestFraction = 1.0f;
		
		//Raycast length of distance until we hit a wall
		if (WorldUtil.preRaycastCheck(entityLocation, endPt)) {
			state.getWorld().rayCast((fixture, point, normal, fraction) -> {

				if (fixture.getFilterData().categoryBits == BodyConstants.BIT_WALL) {
					if (fraction < shortestFraction) {
						shortestFraction = fraction;
						return fraction;
					}
				} else {
					if (fixture.getUserData() instanceof HadalData) {
						if (fixture.getUserData() instanceof BodyData && fraction < shortestFraction) {
							if (((BodyData)fixture.getUserData()).getSchmuck().getHitboxFilter() != filter) {
								shortestFraction = fraction;
								return fraction;
							}
						}
					}
				}
				return -1.0f;
			}, entityLocation, endPt);
		}

		//create explosions around the point we raycast towards
		newPosition.set(user.getPixelPosition()).add(new Vector2(startVelocity).nor().scl(distance * shortestFraction * PPM));
		newPosition.add(MathUtils.random(-SPREAD, SPREAD + 1), MathUtils.random(-SPREAD, SPREAD + 1));
		SyncedAttack.SCREECH.initiateSyncedAttackSingle(state, user, newPosition, startVelocity,distance * shortestFraction);
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		boolean shooting = user.getShootHelper().isShooting() && this.equals(user.getEquipHelper().getCurrentTool())
				&& !reloading && getClipLeft() > 0;

		if (shooting) {
			if (screechSound == null) {
				screechSound = EffectEntityManager.getSound(state, new SoundCreate(SoundEffect.BEAM3, user)
						.setVolume(0.8f));
			} else {
				screechSound.turnOn();
			}
		} else if (screechSound != null) {
			screechSound.turnOff();
		}
	}

	@Override
	public void unequip(PlayState state) {
		if (screechSound != null) {
			screechSound.terminate();
			screechSound = null;
		}
	}

	@Override
	public float getBotRangeMax() { return RANGE; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
