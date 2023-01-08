package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;

/**
 * @author Lassafras Lickette
 */
public class TaintedWater extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;
	
	private static final float DURATION = 4.0f;
	private static final float LIFESPAN = 1.0f;
	private static final float PROJECTILE_SPEED = 35.0f;
	private static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
	private static final Vector2 POISON_SIZE = new Vector2(101, 50);
	private static final float POISON_SIZE_SQUARED = 15000f;
	private static final float POISON_SPREAD = 75f;
	private static final float POISON_DAMAGE = 0.75f;
	private static final float POISON_DURATION = 2.5f;
	
	public TaintedWater(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {	
		SoundEffect.MAGIC27_EVIL.playUniversal(state, user.getPlayer().getPixelPosition(), 1.0f, false);

		Vector2 startPosition = user.getPlayer().getPixelPosition();

		Hitbox hboxBase = new RangedHitbox(state, startPosition, PROJECTILE_SIZE, LIFESPAN,
			new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED), user.getPlayer().getHitboxfilter(), false,
				false, user.getPlayer(), Sprite.NOTHING);

		hboxBase.addStrategy(new ControllerDefault(state, hboxBase, user));
		hboxBase.addStrategy(new ContactWallDie(state, hboxBase, user));
		hboxBase.addStrategy(new HitboxStrategy(state, hboxBase, user) {

			private final Vector2 lastPosition = new Vector2(startPosition);
			private float numPoison;
			@Override
			public void controller(float delta) {
				if (lastPosition.dst2(hbox.getPixelPosition()) > POISON_SIZE_SQUARED) {
					numPoison++;
					lastPosition.set(hbox.getPixelPosition());
					new Poison(state, hbox.getPixelPosition(), new Vector2(POISON_SIZE).add(0, numPoison * POISON_SPREAD),
							POISON_DAMAGE, POISON_DURATION, user.getSchmuck(),true, user.getSchmuck().getHitboxfilter(), DamageSource.TAINTED_WATER) {

						@Override
						public void create() {
							super.create();
							setAngle(weaponVelo.angleRad());
						}
					};
				}
			}
		});
	}
	
	@Override
	public float getUseDuration() { return DURATION; }

	@Override
	public float getBotRangeMin() { return 11.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) DURATION),
				String.valueOf((int) (POISON_DAMAGE * 60))};
	}
}
