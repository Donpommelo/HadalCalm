package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.event.Currents;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.constants.Constants;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 * @author Hatonio Hadoof
 */
public class MeridianMaker extends ActiveItem {

	private static final float USECD = 0.0f;
	private static final float USEDELAY = 0.1f;
	private static final float MAX_CHARGE = 8.0f;
	
	private static final float BASE_DAMAGE = 45.0f;
	private static final float KNOCKBACK = 0.0f;
	private static final Vector2 PROJECTILE_SIZE = new Vector2(40, 40);
	private static final float LIFESPAN = 6.0f;
	private static final float PROJECTILE_SPEED = 30.0f;
	private static final int CURRENT_RADIUS = 100;
	private static final float CURRENT_FORCE = 1.0f;

	private static final Sprite PROJ_SPRITE = Sprite.NOTHING;

	public MeridianMaker(Schmuck user) {
		super(user, USECD, USEDELAY, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC11_WEIRD.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		
		final Vector2 currentVec = new Vector2(weaponVelo).nor().scl(CURRENT_FORCE);
		
		Hitbox hbox = new RangedHitbox(state, user.getPlayer().getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x), PROJECTILE_SIZE, LIFESPAN,
				new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED), user.getPlayer().getHitboxfilter(), false, false, user.getPlayer(), PROJ_SPRITE);
		
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY));
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user));
		hbox.addStrategy(new DamageStandard(state, hbox, user, BASE_DAMAGE, KNOCKBACK, DamageSource.MERIDIAN_MAKER, DamageTag.MAGIC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user));
		hbox.addStrategy(new CreateParticles(state, hbox, user, Particle.BRIGHT, 0.0f, 1.0f)
				.setParticleColor(HadalColor.CELESTE).setParticleSize(20));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user) {
			
			private final Vector2 lastPosition = new Vector2(hbox.getStartPos()).scl(PPM);
			
			@Override
			public void controller(float delta) {
				if (lastPosition.dst2(hbox.getPixelPosition()) > CURRENT_RADIUS * CURRENT_RADIUS) {
					new Currents(state, lastPosition.set(hbox.getPixelPosition()), new Vector2(CURRENT_RADIUS, CURRENT_RADIUS),
							currentVec, LIFESPAN);
				}
			}
		}); 
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) LIFESPAN)};
	}
}
